package com.gpufast.recorder.audio;

import android.media.MediaCodec;
import android.media.MediaFormat;

import com.gpufast.recorder.video.EncodedImage;

public interface AudioEncoder {

    class AudioSettings {

    }

    public void initEncoder();

    public void encodePcm(byte[] bufferBytes , final int len , final long presentationTimeUs);

    interface AudioEncoderCallback {
        void onEncodedAudio(EncodedImage frame, MediaCodec.BufferInfo info, MediaFormat format);
    }
}
