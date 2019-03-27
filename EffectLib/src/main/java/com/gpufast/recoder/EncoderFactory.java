package com.gpufast.recoder;

import com.gpufast.recoder.video.EncoderType;
import com.gpufast.recoder.video.VideoEncoderFactory;
import com.gpufast.recoder.video.encoder.HardwareVideoEncoderFactory;

/**
 * @author Sivin 2019/3/27
 * Description:
 */
public class EncoderFactory {

    public static VideoEncoderFactory getVideoEncoderFactory(EncoderType type){
        switch (type){
            case HW_VIDEO_ENCODER:
                return new HardwareVideoEncoderFactory();
        }
        return null;
    }

}
