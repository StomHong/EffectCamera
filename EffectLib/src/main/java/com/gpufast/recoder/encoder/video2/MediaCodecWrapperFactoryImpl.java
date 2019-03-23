package com.gpufast.recoder.encoder.video2;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.os.Bundle;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;

class MediaCodecWrapperFactoryImpl implements MediaCodecWrapperFactory {
  private static class MediaCodecWrapperImpl implements MediaCodecWrapper {
    private final MediaCodec mediaCodec;

    public MediaCodecWrapperImpl(MediaCodec mediaCodec) {
      this.mediaCodec = mediaCodec;
    }

    @Override
    public void configure(MediaFormat format, Surface surface, MediaCrypto crypto, int flags) {
      mediaCodec.configure(format, surface, crypto, flags);
    }

    @Override
    public void start() {
      mediaCodec.start();
    }

    @Override
    public void flush() {
      mediaCodec.flush();
    }

    @Override
    public void stop() {
      mediaCodec.stop();
    }

    @Override
    public void release() {
      mediaCodec.release();
    }

    @Override
    public int dequeueInputBuffer(long timeoutUs) {
      return mediaCodec.dequeueInputBuffer(timeoutUs);
    }

    @Override
    public void queueInputBuffer(
        int index, int offset, int size, long presentationTimeUs, int flags) {
      mediaCodec.queueInputBuffer(index, offset, size, presentationTimeUs, flags);
    }

    @Override
    public int dequeueOutputBuffer(MediaCodec.BufferInfo info, long timeoutUs) {
      return mediaCodec.dequeueOutputBuffer(info, timeoutUs);
    }

    @Override
    public void releaseOutputBuffer(int index, boolean render) {
      mediaCodec.releaseOutputBuffer(index, render);
    }

    @Override
    public MediaFormat getOutputFormat() {
      return mediaCodec.getOutputFormat();
    }

    @Override
    public ByteBuffer[] getInputBuffers() {
      return mediaCodec.getInputBuffers();
    }

    @Override
    public ByteBuffer[] getOutputBuffers() {
      return mediaCodec.getOutputBuffers();
    }

    @Override
    @TargetApi(18)
    public Surface createInputSurface() {
      return mediaCodec.createInputSurface();
    }

    @Override
    @TargetApi(19)
    public void setParameters(Bundle params) {
      mediaCodec.setParameters(params);
    }
  }

  @Override
  public MediaCodecWrapper createByCodecName(String name) throws IOException {
    return new MediaCodecWrapperImpl(MediaCodec.createByCodecName(name));
  }
}