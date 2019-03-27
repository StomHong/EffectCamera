package com.gpufast.recoder.video;

import android.opengl.EGLContext;

import com.gpufast.recoder.video.encoder.VideoCodecInfo;


public interface VideoEncoderFactory {

    void setShareContext(EGLContext shareContext);

    VideoEncoder createEncoder(VideoCodecInfo inputCodecInfo);

    VideoCodecInfo[] getSupportedCodecs();
}
