package com.gpufast.recoder;

import android.opengl.EGLContext;

import com.gpufast.recoder.video.EncoderType;
import com.gpufast.recoder.video.VideoEncoder;
import com.gpufast.recoder.video.VideoEncoderFactory;
import com.gpufast.recoder.video.encoder.VideoCodecInfo;
import com.gpufast.utils.ELog;

public class EffectRecorder implements IRecorder {
    private static final String TAG = "EffectRecorder";

    private boolean startRecorder = false;
    private VideoEncoderFactory videoEncoderFactory;
    private VideoEncoder mVideoEncoder;
    private VideoCodecInfo videoCodecInfo;


    private RecorderParams recorderParams;

    EffectRecorder() {
    }


    @Override
    public void setParams(RecorderParams params) {
        recorderParams = params;
        if (recorderParams.isHwEncoder()) {
            videoEncoderFactory = EncoderFactory.getVideoEncoderFactory(EncoderType.HW_VIDEO_ENCODER);
        }
        if (videoEncoderFactory != null) {
            VideoCodecInfo[] supportedCodecs = videoEncoderFactory.getSupportedCodecs();
            if (supportedCodecs != null && supportedCodecs.length > 0) {
                videoCodecInfo = supportedCodecs[0];
                ELog.d(TAG, "find a codec :" + videoCodecInfo.name);
            } else {
                ELog.e(TAG, "don't find a available codec :");
            }
        }
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
        if (videoEncoderFactory != null) {
            mVideoEncoder = videoEncoderFactory.createEncoder(videoCodecInfo);
            if (mVideoEncoder == null) {
                ELog.e(TAG,"can't create video encoder");
                return;
            }
            //mVideoEncoder.initEncoder();
        }
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
