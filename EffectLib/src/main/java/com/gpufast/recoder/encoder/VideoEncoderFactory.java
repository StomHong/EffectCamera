package com.gpufast.recoder.encoder;

import com.gpufast.recoder.encoder.video.VideoCodecInfo;
import com.gpufast.recoder.encoder.video.VideoEncoder;

public interface VideoEncoderFactory {
    /**
     * 穿件编码器
     * @return
     */
    VideoEncoder createEncoder(VideoCodecInfo inputCodecInfo);

    VideoCodecInfo[] getSupportedCodecs();
}
