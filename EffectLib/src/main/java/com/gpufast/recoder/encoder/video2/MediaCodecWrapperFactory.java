package com.gpufast.recoder.encoder.video2;


import java.io.IOException;

interface MediaCodecWrapperFactory {
  MediaCodecWrapper createByCodecName(String name) throws IOException;
}