package com.gpufast.recoder.video;

import com.gpufast.recoder.video.encoder.VideoCodecInfo;
import com.gpufast.recoder.video.VideoEncoder;

public interface VideoEncoderFactory {
    /**
     * 穿件编码器
     * @return
     */
    VideoEncoder createEncoder(VideoCodecInfo inputCodecInfo);

    VideoCodecInfo[] getSupportedCodecs();
}
