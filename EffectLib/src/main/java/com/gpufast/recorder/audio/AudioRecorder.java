package com.gpufast.recorder.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.gpufast.logger.ELog;

public class AudioRecorder {

    private AudioRecord mAudioRecord;
    private static final int SAMPLE_RATE = 44100;//采样率
    private static final int BIT_RATE = 48000;//码率 MediaCodecInfo.CodecProfileLevel.AACObjectLC >= 80Kbps

    public void init() {
        int minBufferSize = AudioRecord.getMinBufferSize(
                SAMPLE_RATE, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        if (minBufferSize == AudioRecord.ERROR || minBufferSize == AudioRecord.ERROR_BAD_VALUE) {
            ELog.e("HardwareAudioEncoder ", "AudioRecord.getMinBufferSize failed: " + minBufferSize);
        }

        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize);
    }

    public void start() {
        if (mAudioRecord != null) {
            mAudioRecord.startRecording();
        }
    }


    public void stop() {
        if (mAudioRecord != null) {
            mAudioRecord.stop();
        }
    }

    public void release() {
        if (mAudioRecord != null) {
            mAudioRecord.release();
        }
    }

    class AudioCollectThread extends Thread {
        private volatile boolean keepAlive = true;
        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            while (keepAlive) {
                int num = mAudioRecord.read(buffer, 0, buffer.length);
                if (num > 0) {
                    encode(buffer);
                    Log.i("AudioEncoderThread === ", num + " 读取录音数据成功");
                } else {
                    Log.e("AudioEncoderThread === ", " 读取录音数据出错");
                    break;
                }
            }
        }
        // Stops the inner thread loop and also calls AudioRecord.stop().
        // Does not block the calling thread.
        public void stopThread() {
            keepAlive = false;
        }
    }

}
