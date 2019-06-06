package com.gpufast.recorder.audio.encoder;

public interface AudioEncoderFactory {

    AudioCodecInfo[] getSupportCodecInfo();

    AudioEncoder createEncoder(AudioCodecInfo inputCodecInfo);

}
