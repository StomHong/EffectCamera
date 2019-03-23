package com.gpufast.recoder.encoder.video2;

/** 用于调整硬件编解码器的码率. */
interface BitrateAdjuster {
  /**
   * 设置目标码率（bps) 和帧率(fps)
   */
  void setTargets(int targetBitrateBps, int targetFps);

  /**
   * Reports that a frame of the given size has been encoded.  Returns true if the bitrate should
   * be adjusted.
   */
  void reportEncodedFrame(int size);

  /** 获取当前的码率. */
  int getAdjustedBitrateBps();

  /** 获取配置编码器的帧率. */
  int getCodecConfigFrameRate();
}