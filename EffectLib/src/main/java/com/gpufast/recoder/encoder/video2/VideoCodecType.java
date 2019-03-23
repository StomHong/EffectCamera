package com.gpufast.recoder.encoder.video2;

/**
 * 视频编解码器的类型
 */
public enum VideoCodecType {

    H264("video/avc");

    private final String mimeType;

    VideoCodecType(String mimeType) {
        this.mimeType = mimeType;
    }

    String mimeType() {
        return mimeType;
    }
}