package com.gpufast.recorder.audio.encoder;

import android.media.MediaCodecInfo;

import com.gpufast.logger.ELog;
import com.gpufast.recorder.hardware.MediaCodecWrapperFactoryImpl;

public class HwAudioEncoderFactory implements AudioEncoderFactory {

    private static final String TAG = "HwAudioEncoderFactory";


    @Override
    public AudioCodecInfo getSupportCodecInfo() {
        AudioCodecInfo info = null;
        for(AudioCodecType type : new AudioCodecType[]{AudioCodecType.AAC}){
            MediaCodecInfo codecInfo = findCodecForType(type);
            if(codecInfo != null){
                String name = type.name();
                info = new AudioCodecInfo(name,type);
            }
        }
        return info;
    }

    private MediaCodecInfo findCodecForType(AudioCodecType type) {


        return null;
    }

    @Override
    public AudioEncoder createEncoder(AudioCodecInfo inputCodecInfo) {
        //编码器的类型
        AudioCodecType type = AudioCodecType.valueOf(inputCodecInfo.name);
        //根据类型查找编码器的信息
        MediaCodecInfo info = findCodecForType(type);
        if (info == null) {
            ELog.e(this, "can't find Encoder by type" + inputCodecInfo.name);
            return null;
        }
        String name = info.getName();
        String mime = type.mimeType();
        ELog.i(TAG,"crate audio encoder, name:"+name +" mime:"+mime);
        return new HwAudioEncoder(new MediaCodecWrapperFactoryImpl(),type,name);
    }
}
