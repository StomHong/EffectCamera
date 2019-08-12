package com.gpufast.recorder.audio;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.gpufast.logger.ELog;
import com.gpufast.recorder.audio.encoder.AudioEncoder;

import java.lang.ref.WeakReference;

public class AudioClient implements AudioCollector.OnAudioFrameCallback {

    /**
     * 音频编码线程
     */
    private EncoderThread mEncoderThread;

    private AudioCollector mAudioCollector;

    private AudioProcessor mAudioPreprocessor;

    public AudioClient(AudioEncoder encoder,
                       AudioEncoder.Settings settings,
                       AudioEncoder.AudioEncoderCallback callback) {
        mEncoderThread = new EncoderThread(encoder, settings, callback);
        mAudioCollector = new AudioCollector();
        mAudioCollector.init(this);
    }


    public void start() {
        //启动音频编码线程
        mEncoderThread.start();
        mEncoderThread.waitUntilReady();
        //启动音频采集器
        mAudioCollector.start();

    }


    public void stop() {
        mEncoderThread.getHandler().sendToStop();
    }

    @Override
    public void onReceiveAudioFrame(AudioFrame frame) {
        AudioFrame newFrame = null;
        if (mAudioPreprocessor != null) {
            newFrame = mAudioPreprocessor.onReceiveAudioFrame(frame);
        }
        if (newFrame != null) {
            frame = newFrame;
        }
        //编码音频
        mEncoderThread.getHandler().sendAudioFrame(frame);
    }

    public void release() {


    }

    private static class EncoderThread extends Thread {
        private static final String TAG = EncoderThread.class.getSimpleName();
        private final Object mStartLock = new Object();
        private boolean mReady = false;
        private EncoderHandler mEncoderHandler;


        private AudioEncoder mAudioEncoder;
        private AudioEncoder.Settings mSettings;

        AudioEncoder.AudioEncoderCallback mCallback;

        EncoderThread(AudioEncoder encoder, AudioEncoder.Settings settings,
                      AudioEncoder.AudioEncoderCallback callback) {
            mAudioEncoder = encoder;
            mSettings = settings;
            mCallback = callback;
        }

        boolean isReady() {
            return mReady;
        }

        EncoderHandler getHandler() {
            return mEncoderHandler;
        }

        @Override
        public void run() {
            Looper.prepare();
            mEncoderHandler = new EncoderHandler(this);
            initEncoder();
            synchronized (mStartLock) {
                mReady = true;
                mStartLock.notify();  //通知调用线程，渲染线程准备工作完成
            }
            Looper.loop();
        }

        void waitUntilReady() {
            synchronized (mStartLock) {
                while (!mReady) {
                    try {
                        mStartLock.wait();
                    } catch (InterruptedException ie) { /* not expected */ }
                }
            }
        }

        private void initEncoder() {
            if (mAudioEncoder != null) {
                mAudioEncoder.initEncoder(mSettings, mCallback);
            }
        }

        void sendAudioFrame(AudioFrame frame) {
            if (mAudioEncoder != null && mReady) {
                mAudioEncoder.encodePcm(frame);
            }
        }

        /**
         * 必须在当前线程调用
         */
        private void shutdown() {
            ELog.d(TAG, "shutdown");
            Looper.myLooper().quit();
        }
    }


    private static class EncoderHandler extends Handler {
        private static final String TAG = EncoderHandler.class.getSimpleName();
        private static final int ON_FRAME_AVAILABLE = 0x001;
        private static final int ON_STOP = 0x002;

        private WeakReference<AudioClient.EncoderThread> mWeakEncoderThread;

        EncoderHandler(EncoderThread thread) {
            mWeakEncoderThread = new WeakReference<>(thread);
        }


        void sendAudioFrame(AudioFrame frame) {
            sendMessage(obtainMessage(ON_FRAME_AVAILABLE, frame));
        }

        private void sendToStop() {
            sendMessage(obtainMessage(ON_STOP));
        }

        @Override
        public void handleMessage(Message msg) {

            EncoderThread encoderThread = mWeakEncoderThread.get();
            if (encoderThread == null) {
                ELog.e(TAG, "mWeakEncoderThread.get() == null");
                return;
            }
            switch (msg.what) {
                case ON_FRAME_AVAILABLE:
                    encoderThread.sendAudioFrame((AudioFrame) msg.obj);
                    break;
                case ON_STOP:
                    encoderThread.shutdown();
                    break;
            }
        }
    }
}
