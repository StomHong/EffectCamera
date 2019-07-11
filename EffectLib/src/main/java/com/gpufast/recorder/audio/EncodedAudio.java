package com.gpufast.recorder.audio;

import android.media.MediaCodec;
import android.media.MediaFormat;

import java.nio.ByteBuffer;

public class EncodedAudio {

    public final ByteBuffer mBuffer;
    public final MediaFormat mMediaFormat;
    public final MediaCodec.BufferInfo mBufferInfo;

    private EncodedAudio(ByteBuffer mBuffer, MediaFormat mMediaFormat, MediaCodec.BufferInfo mBufferInfo) {
        this.mBuffer = mBuffer;
        this.mMediaFormat = mMediaFormat;
        this.mBufferInfo = mBufferInfo;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder{

        private ByteBuffer buffer;
        private MediaFormat mediaFormat;
        private MediaCodec.BufferInfo bufferInfo;

        private Builder() {
        }

        public void setBuffer(ByteBuffer buffer) {
            this.buffer = buffer;
        }

        public void setMediaFormat(MediaFormat mediaFormat) {
            this.mediaFormat = mediaFormat;
        }

        public void setBufferInfo(MediaCodec.BufferInfo bufferInfo) {
            this.bufferInfo = bufferInfo;
        }

        public EncodedAudio createEncodedAudio() {
            return new EncodedAudio(buffer,mediaFormat,bufferInfo);
        }
    }
}
