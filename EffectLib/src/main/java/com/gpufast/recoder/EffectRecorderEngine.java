package com.gpufast.recoder;

import android.opengl.EGLContext;

import com.gpufast.recoder.encoder.video2.VideoEncoder;
import com.gpufast.recoder.encoder.video2.HardwareVideoEncoderFactory;
import com.gpufast.recoder.encoder.video2.VideoCodecInfo;
import com.gpufast.recoder.encoder.video2.VideoCodecType;
import com.gpufast.render.Render;

public class EffectRecorderEngine implements RecorderEngine, Render.FrameCallback {
    private static final String TAG = "EffectRecorderEngine";
    private static EffectRecorderEngine mRecorderEngine = null;

    private boolean startRecorder = false;


    private VideoCodecInfo inputCodecInfo = null;
    private VideoEncoder mVideoEncoder = null;
    private boolean mVideoEncoderInit = false;


    public static EffectRecorderEngine getInstance() {
        if (mRecorderEngine == null) {
            synchronized (EffectRecorderEngine.class) {
                if (mRecorderEngine == null) {
                    mRecorderEngine = new EffectRecorderEngine();
                }
            }
        }
        return mRecorderEngine;
    }



    private EffectRecorderEngine() {
        HardwareVideoEncoderFactory factory = new HardwareVideoEncoderFactory();
        mVideoEncoder = factory.createEncoder(new VideoCodecInfo(VideoCodecType.H264, VideoCodecInfo.Profile.BASE_LINE));
    }


    @Override
    public int onFrameCallback(EGLContext context, int textureId, int width, int height) {
        if (startRecorder) {
            if(mVideoEncoderInit){
//                VideoCodecStatus status = mVideoEncoder.initEncode();
//                if(status == VideoCodecStatus.OK){
//                    mVideoEncoderInit = true;
//                }else{
//                    ELog.e(TAG,"VideoEncoder init failed");
//                    return status.getNumber();
//                }
            }
            //mVideoEncoder.encode()
        }
        return 0;
    }

    @Override
    public void onEglContextDestroy() {


    }


    @Override
    public void setParams(RecorderParams params) {

    }

    @Override
    public void startRecorder() {

    }

    @Override
    public void stopRecorder() {

    }


    @Override
    public void stop() {

    }

    @Override
    public void jointVideo() {

    }

    @Override
    public void release() {

    }
}
