package com.gpufast.recoder.video.encoder;


import java.io.IOException;

interface MediaCodecWrapperFactory {
  MediaCodecWrapper createByCodecName(String name) throws IOException;
}