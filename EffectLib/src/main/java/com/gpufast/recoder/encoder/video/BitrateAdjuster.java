package com.gpufast.recoder.encoder.video;

/** Object that adjusts the bitrate of a hardware codec. */
interface BitrateAdjuster {
  /**
   * Sets the target bitrate in bits per second and frameRate in frames per second.
   */
  void setTargets(int targetBitrateBps, int targetFps);

  /**
   * Reports that a frame of the given size has been encoded.  Returns true if the bitrate should
   * be adjusted.
   */
  void reportEncodedFrame(int size);

  /** Gets the current bitrate. */
  int getAdjustedBitrateBps();

  /** Gets the frameRate for initial codec configuration. */
  int getCodecConfigFrameRate();
}