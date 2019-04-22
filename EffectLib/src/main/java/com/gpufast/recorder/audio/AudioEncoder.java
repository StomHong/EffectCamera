package com.gpufast.recorder.audio;

public interface AudioEncoder {

    public void initEncoder();

    public void encodePcm(byte[] bufferBytes , final int len , final long presentationTimeUs);

}
