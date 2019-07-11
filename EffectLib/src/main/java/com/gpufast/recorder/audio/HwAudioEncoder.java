package com.gpufast.recorder.audio;

import android.annotation.TargetApi;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 语音硬编码
 */
public class HwAudioEncoder implements AudioEncoder {

    private static  AudioCodecType audioMime = AudioCodecType.AAC;
    private MediaCodec mAudioCodec;

    private static final int SAMPLE_RATE = 44100;//采样率
    private static final int BIT_RATE = 96000;//码率 MediaCodecInfo.CodecProfileLevel.AACObjectLC >= 80Kbps

    private AudioRecord mAudioRecord;
    private MediaCodec.BufferInfo mBufferInfo;
    private static EncoderThread mEncoderThread;

    /**
     * 声道数
     */
    private static final int CHANNEL = 2;

    @Override
    public void prepare() throws IOException {
        MediaFormat format = new MediaFormat();
        format.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
        format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, CHANNEL);
        format.setInteger(MediaFormat.KEY_SAMPLE_RATE, SAMPLE_RATE);
        format.setString(MediaFormat.KEY_MIME, audioMime.mimeType());
        format.setInteger(MediaFormat.KEY_CHANNEL_MASK, AudioFormat.CHANNEL_IN_STEREO);
        format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);

        mAudioCodec = MediaCodec.createEncoderByType(audioMime.mimeType());
        mAudioCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mAudioCodec.start();

        mBufferInfo = new MediaCodec.BufferInfo();

        int minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize);

    }

    @Override
    public void startRecording(){
        if (mAudioRecord != null) {
            mAudioRecord.startRecording();
        }
        Looper.prepare();
        mEncoderThread = new EncoderThread();
        mEncoderThread.start();
        Looper.loop();
    }

    @Override
    public void pauseRecording() {
        if (mAudioRecord != null) {
            mAudioRecord.stop();
        }
        Looper.myLooper().quit();
    }

    @Override
    public void setAudioCodecType(AudioCodecType type) {
        audioMime = type;
    }

    @Override
    public void release() {
        if (mAudioRecord != null){
            mAudioRecord.release();
        }
    }

    @TargetApi(21)
    private void encode(byte[] data) {
        int inputBufferIndex = mAudioCodec.dequeueInputBuffer(-1);
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = mAudioCodec.getInputBuffer(inputBufferIndex);
            inputBuffer.clear();
            inputBuffer.put(data);
            inputBuffer.limit(data.length);
            mAudioCodec.queueInputBuffer(inputBufferIndex, 0, data.length,
                    (System.nanoTime() ) / 1000, 0);
        }

        int outputBufferIndex = mAudioCodec.dequeueOutputBuffer(mBufferInfo, 0);
        while (outputBufferIndex >= 0) {
            ByteBuffer outputData = mAudioCodec.getOutputBuffer(outputBufferIndex);


            mAudioCodec.releaseOutputBuffer(outputBufferIndex, false);
            outputBufferIndex = mAudioCodec.dequeueOutputBuffer(mBufferInfo, 0);

        }
    }

    class EncoderThread extends Thread {
        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            while (true) {
                int num = mAudioRecord.read(buffer, 0, buffer.length);
                if (num > 0) {
                    encode(buffer);
                } else {
                    Log.e("EncoderThread === ", num + " 读取录音数据出错");
                    break;
                }
            }

        }
    }

}