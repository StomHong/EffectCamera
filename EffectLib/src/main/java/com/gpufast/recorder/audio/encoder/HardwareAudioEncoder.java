package com.gpufast.recorder.audio.encoder;

import android.media.AudioFormat;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;

import com.gpufast.logger.ELog;
import com.gpufast.recorder.audio.EncodedAudio;
import com.gpufast.recorder.hardware.MediaCodecWrapper;
import com.gpufast.recorder.hardware.MediaCodecWrapperFactory;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 音频硬编码器
 */
public class HardwareAudioEncoder implements AudioEncoder {

    private static final String TAG = "HardwareAudioEncoder";

    private MediaCodecWrapperFactory mediaCodecFactory;

    private MediaCodecWrapper codec;

    private final String codecName;

    private AudioCodecType codecType;

    private int bitRate;

    private int sampleRate;


    private MediaCodec.BufferInfo mBufferInfo;


    private AudioEncoderCallback callback;
    /**
     * 声道数
     */
    private static final int CHANNEL = 2;

    public HardwareAudioEncoder(MediaCodecWrapperFactory mediaCodecFactory,
                                AudioCodecType codecType, String codecName) {
        this.mediaCodecFactory = mediaCodecFactory;
        this.codecType = codecType;
        this.codecName = codecName;
    }


    @Override
    public AudioCodecStatus initEncoder(AudioEncoder.Settings settings, AudioEncoderCallback callback) {
        if (settings == null) {
            return AudioCodecStatus.ERR_PARAMETER;
        }

        try {
            codec = mediaCodecFactory.createByCodecName(codecName);
        } catch (IOException | IllegalArgumentException e) {
            ELog.e(TAG, "Cannot create media encoder " + codecName);
            return AudioCodecStatus.FALLBACK_SOFTWARE;
        }


        ELog.e(TAG, "initEncoder bitrate = " + settings.bitrate + " sample rate = " + settings.sampleRate +
                " mimeType = " + codecType.mimeType());

        try {
            MediaFormat format = new MediaFormat();
            format.setInteger(MediaFormat.KEY_BIT_RATE, settings.bitrate);
            format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, CHANNEL);
            format.setInteger(MediaFormat.KEY_SAMPLE_RATE, settings.sampleRate);
            format.setString(MediaFormat.KEY_MIME, codecType.mimeType());
            format.setInteger(MediaFormat.KEY_CHANNEL_MASK, AudioFormat.CHANNEL_IN_STEREO);
            format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
            format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 0);

            codec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            codec.start();

        } catch (IllegalStateException e) {
            ELog.e(TAG, "initEncoder failed:" + e.getLocalizedMessage());
            release();
            return AudioCodecStatus.FALLBACK_SOFTWARE;
        }
        mBufferInfo = new MediaCodec.BufferInfo();
        return AudioCodecStatus.OK;
    }

    @Override
    public void encodePcm(byte[] pcmData, int len, long presentationTimeUs) {
        int inputBufferIndex = codec.dequeueInputBuffer(-1);
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = codec.getInputBuffer(inputBufferIndex);
            if (inputBuffer == null) {
                ELog.e(TAG, "dequeue input buffer is null. inputBufferIndex = " + inputBufferIndex);
                return;
            }
            inputBuffer.clear();
            inputBuffer.put(pcmData);
            inputBuffer.limit(len);
            codec.queueInputBuffer(
                    inputBufferIndex, 0, len, presentationTimeUs, 0);
        }

        int outputBufferIndex = codec.dequeueOutputBuffer(mBufferInfo, 0);

        while (outputBufferIndex >= 0) {

            ByteBuffer outputData = codec.getOutputBuffer(outputBufferIndex);

            EncodedAudio encodedAudio = new EncodedAudio.Builder()
                    .setBuffer(outputData)
                    .setBufferInfo(mBufferInfo)
                    .createEncodedAudio();
            if (callback != null) {
                callback.onEncodedAudio(encodedAudio);
            }

            codec.releaseOutputBuffer(outputBufferIndex, presentationTimeUs);
            outputBufferIndex = codec.dequeueOutputBuffer(mBufferInfo, 0);
        }
    }


    @Override
    public void release() {
        if (codec != null) {
            codec.stop();
        }
        if (codec != null) {
            codec.release();
        }
    }
}