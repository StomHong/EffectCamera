package com.gpufast.recorder.audio.encoder;

import android.media.MediaFormat;

import com.gpufast.recorder.audio.EncodedAudio;

public interface AudioEncoder {

    class Settings {
        final int bitrate; // Kilobits per second.
        final int sampleRate;

        public Settings(int sampleRate, int bitrate) {
            this.sampleRate = sampleRate;
            this.bitrate = bitrate;
        }
    }

    AudioCodecStatus initEncoder(AudioEncoder.Settings settings, AudioEncoderCallback callback);

    void encodePcm(byte[] bufferBytes, int len, long presentationTimeUs);

    void release();

    interface AudioEncoderCallback {

        void onUpdateAudioFormat(MediaFormat mediaFormat);

        void onEncodedAudio(EncodedAudio frame);
    }
}
