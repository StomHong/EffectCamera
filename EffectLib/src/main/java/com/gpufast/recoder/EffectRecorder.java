package com.gpufast.recoder;

import android.opengl.EGLContext;

import com.gpufast.recoder.encoder.video2.HardwareVideoEncoderFactory;
import com.gpufast.recoder.encoder.video2.VideoCodecInfo;
import com.gpufast.recoder.encoder.video2.VideoCodecType;
import com.gpufast.recoder.encoder.video2.VideoEncoder;

public class EffectRecorder implements IRecorder {
    private static final String TAG = "EffectRecorder";

    private boolean startRecorder = false;

    private VideoCodecInfo inputCodecInfo = null;
    private VideoEncoder mVideoEncoder = null;
    private boolean mVideoEncoderInit = false;

    EffectRecorder() {
    }


    @Override
    public void setParams(RecorderParams params) {

    }

    @Override
    public void setShareContext(EGLContext shareContext) {

    }

    @Override
    public boolean isRecording() {
        return false;
    }

    @Override
    public void startRecorder() {

    }


    @Override
    public void jointVideo() {

    }


    @Override
    public void sendVideoFrame(int textureId, int srcWidth, int srcHeight, long timeStamp) {

    }

    @Override
    public int getFps() {
        return 0;
    }


    @Override
    public void stopRecorder() {

    }


    @Override
    public void stop() {

    }

    @Override
    public void release() {

    }

}
