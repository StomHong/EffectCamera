package com.gpufast.recorder.audio.encoder;

import android.media.MediaCodec;

class HwAudioEncoder implements AudioEncoder {

    private static final String audioMime = "audio/mp4a-latm";

    private MediaCodec mCodec;

    @Override
    public void initEncoder() {

    }

    @Override
    public void encodePcm(byte[] bufferBytes, int len, long presentationTimeUs) {

    }

}