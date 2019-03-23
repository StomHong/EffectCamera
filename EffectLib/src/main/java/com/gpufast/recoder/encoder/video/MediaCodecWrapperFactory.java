package com.gpufast.recoder.encoder.video;

import java.io.IOException;

interface MediaCodecWrapperFactory {
  MediaCodecWrapper createByCodecName(String name) throws IOException;
}