package com.gpufast.recoder.video;

import com.gpufast.recoder.video.encoder.VideoCodecInfo;
import com.gpufast.recoder.video.VideoEncoder;

public interface VideoEncoderFactory {

    VideoEncoder createEncoder(VideoCodecInfo inputCodecInfo);

    VideoCodecInfo[] getSupportedCodecs();
}
