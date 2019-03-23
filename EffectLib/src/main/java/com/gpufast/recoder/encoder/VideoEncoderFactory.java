package com.gpufast.recoder.encoder;

import com.gpufast.recoder.encoder.video2.VideoCodecInfo;
import com.gpufast.recoder.encoder.video2.VideoEncoder;

public interface VideoEncoderFactory {
    /**
     * 穿件编码器
     * @return
     */
    VideoEncoder createEncoder(VideoCodecInfo inputCodecInfo);

    VideoCodecInfo[] getSupportedCodecs();
}
