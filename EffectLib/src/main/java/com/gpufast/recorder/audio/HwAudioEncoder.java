package com.gpufast.recorder.audio;

import android.annotation.TargetApi;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaRecorder;
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
    private static final int BIT_RATE = 48000;//码率 MediaCodecInfo.CodecProfileLevel.AACObjectLC >= 80Kbps

    private AudioRecord mAudioRecord;
    private MediaCodec.BufferInfo mBufferInfo;
    private static AudioEncoderThread mAudioEncoderThread;
    private AudioEncoderCallback callback;
    private long startTime;

    public HwAudioEncoder(AudioEncoderCallback callback) {
        this.callback = callback;
    }

    /**
     * 声道数
     */
    private static final int CHANNEL = 1;

    @Override
    public void prepare() throws IOException {
        MediaFormat format = new MediaFormat();
        format.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
        format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, CHANNEL);
        format.setInteger(MediaFormat.KEY_SAMPLE_RATE, SAMPLE_RATE);
        format.setString(MediaFormat.KEY_MIME, audioMime.mimeType());
        format.setInteger(MediaFormat.KEY_CHANNEL_MASK, AudioFormat.CHANNEL_IN_MONO);
        format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 0);

        mAudioCodec = MediaCodec.createEncoderByType(audioMime.mimeType());
        mAudioCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mAudioCodec.start();

        mBufferInfo = new MediaCodec.BufferInfo();

        int minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        if (minBufferSize == AudioRecord.ERROR || minBufferSize == AudioRecord.ERROR_BAD_VALUE) {
            Log.e("HwAudioEncoder ","AudioRecord.getMinBufferSize failed: " + minBufferSize);
        }
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize);

        startTime = System.nanoTime();
    }

    @Override
    public void startRecording(){
        if (mAudioRecord != null) {
            mAudioRecord.startRecording();
        }
        mAudioEncoderThread = new AudioEncoderThread();
        mAudioEncoderThread.start();
    }

    @Override
    public void stopRecording() {
        if (mAudioRecord != null) {
            mAudioRecord.stop();
        }
        mAudioEncoderThread.stopThread();
        mAudioEncoderThread = null;
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
                    (System.nanoTime() - startTime) / 1000, 0);
        }

        int outputBufferIndex = mAudioCodec.dequeueOutputBuffer(mBufferInfo, 0);
        while (outputBufferIndex >= 0) {
            ByteBuffer outputData = mAudioCodec.getOutputBuffer(outputBufferIndex);
            EncodedAudio encodedAudio = new EncodedAudio.Builder()
                    .setBuffer(outputData)
                    .setBufferInfo(mBufferInfo)
                    .setMediaFormat(mAudioCodec.getOutputFormat())
                    .createEncodedAudio();
            callback.onEncodedAudio(encodedAudio);
            mAudioCodec.releaseOutputBuffer(outputBufferIndex, false);
            outputBufferIndex = mAudioCodec.dequeueOutputBuffer(mBufferInfo, 0);

        }
    }

    class AudioEncoderThread extends Thread {

        private volatile boolean keepAlive = true;

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            while (keepAlive) {
                int num = mAudioRecord.read(buffer, 0, buffer.length);
                if (num > 0) {
                    encode(buffer);
                    Log.e("AudioEncoderThread === ", num + " 读取录音数据成功");
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