package com.gpufast.recorder;

import com.gpufast.recorder.video.EncoderType;
import com.gpufast.recorder.video.VideoEncoderFactory;
import com.gpufast.recorder.video.encoder.HardwareVideoEncoderFactory;

/**
 * @author Sivin 2019/3/27
 * Description:
 */
class EncoderFactory {

    static VideoEncoderFactory getVideoEncoderFactory(EncoderType type){
        switch (type){
            case HW_VIDEO_ENCODER:
                return new HardwareVideoEncoderFactory();
        }
        return null;
    }

}
