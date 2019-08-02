package com.gpufast.recorder.audio.encoder;

public interface AudioEncoder {

    void initEncoder();

    void encodePcm(byte[] bufferBytes, final int len, final long presentationTimeUs);

}
