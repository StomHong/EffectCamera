package com.gpufast.recorder.audio.encoder;

import android.media.MediaCodecInfo;

import com.gpufast.recorder.video.encoder.VideoCodecInfo;

import java.util.ArrayList;
import java.util.List;

public class HardwareAudioEncoderFactory implements AudioEncoderFactory {


    @Override
    public AudioCodecInfo[] getSupportCodecInfo() {
        List<VideoCodecInfo> supportedCodecInfoList = new ArrayList<>();
        for(AudioCodecType type : new AudioCodecType[]{AudioCodecType.AAC}){
            MediaCodecInfo codecInfo = findCodecForType(type);

        }


        return new AudioCodecInfo[0];
    }

    private MediaCodecInfo findCodecForType(AudioCodecType type) {


        return null;
    }

    @Override
    public AudioEncoder createEncoder(AudioCodecInfo inputCodecInfo) {


        return null;
    }
}
