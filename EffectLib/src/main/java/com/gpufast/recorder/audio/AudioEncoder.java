package com.gpufast.recorder.audio;

import java.io.IOException;

public interface AudioEncoder {

    void setAudioCodecType(AudioCodecType type);

    void prepare() throws IOException;

    void startRecording();

    void pauseRecording();

    void release();

    interface AudioEncoderCallback {
        void onEncodedAudio(EncodedAudio frame);
    }
}
