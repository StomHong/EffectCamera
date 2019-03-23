package com.gpufast.recoder.encoder.video;

/**
 * BitrateAdjuster that tracks bitrate and framerate but does not adjust them.
 */
class BaseBitrateAdjuster implements BitrateAdjuster {
    protected int targetBitrateBps;
    protected int targetFps;

    @Override
    public void setTargets(int targetBitrateBps, int targetFps) {
        this.targetBitrateBps = targetBitrateBps;
        this.targetFps = targetFps;
    }

    @Override
    public void reportEncodedFrame(int size) {
        // No op.
    }

    @Override
    public int getAdjustedBitrateBps() {
        return targetBitrateBps;
    }

    @Override
    public int getCodecConfigFrameRate() {
        return targetFps;
    }
}