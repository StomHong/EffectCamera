package com.gpufast.recorder.audio.encoder;

public enum  AudioCodecType {

    OPUS("audio/opus"),
    AAC("audio/mp4a-latm");

    private final String mimeType;

    AudioCodecType(String mimeType) {
        this.mimeType = mimeType;
    }

    String mimeType() {
        return mimeType;
    }

}
