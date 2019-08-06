package com.gpufast.recorder;

import android.opengl.EGLContext;

import com.gpufast.logger.ELog;
import com.gpufast.recorder.audio.encoder.AudioEncoder;
import com.gpufast.recorder.audio.encoder.HwAudioEncoder;
import com.gpufast.recorder.muxer.Mp4Muxer;
import com.gpufast.recorder.video.EncoderType;
import com.gpufast.recorder.video.VideoClient;
import com.gpufast.recorder.video.VideoEncoder;
import com.gpufast.recorder.video.VideoEncoderFactory;
import com.gpufast.recorder.video.encoder.VideoCodecInfo;

import java.io.IOException;

public class EffectRecorder extends BaseRecorder {

    private static final String TAG = EffectRecorder.class.getSimpleName();

    private volatile boolean recorderStarted = false;

    private EGLContext shareContext;

    private VideoEncoderFactory videoEncoderFactory;
    private VideoCodecInfo videoCodecInfo;
    private VideoEncoder.Settings videoSettings;
    private VideoClient mVideoClient;
    private Mp4Muxer mMp4Muxer;
    private AudioEncoder audioEncoder;

    RecorderListener mRecorderListener;

    //开始码率
    public final int startBitrate = 4000; // Kilobits per second.
    //帧率
    public final int maxFrameRate = 30;

    EffectRecorder() {
    }

    @Override
    public void setParams(RecorderParams params) {
        if (params == null) {
            return;
        }
        if (params.isEnableVideo()) {
            //get video encoder params
            videoSettings = new VideoEncoder.Settings(params.getVideoWidth(),
                    params.getVideoHeight(), startBitrate, maxFrameRate);

            if (params.isHwEncoder()) {
                videoEncoderFactory = EncoderFactory.getVideoEncoderFactory(EncoderType.HW_VIDEO_ENCODER);
            } else {
                videoEncoderFactory = EncoderFactory.getVideoEncoderFactory(EncoderType.SW_VIDEO_ENCODER);
            }

            if (videoEncoderFactory != null) {
                if (shareContext != null) {
                    videoEncoderFactory.setShareContext(shareContext);
                }
                VideoCodecInfo[] supportedCodecs = videoEncoderFactory.getSupportedCodecs();
                if (supportedCodecs != null && supportedCodecs.length > 0) {
                    videoCodecInfo = supportedCodecs[0];
                    ELog.d(TAG, "find a codec :" + videoCodecInfo.name);
                } else {
                    ELog.e(TAG, "can't find a available codec :");
                }
            }
        }
        mMp4Muxer = new Mp4Muxer(params.getSavePath());
        try {
            audioEncoder = new HwAudioEncoder(mMp4Muxer);
            audioEncoder.initEncoder();
        } catch (IOException e) {
            ELog.e(TAG, "Init HwAudioEncoder:" + e.getMessage());
        }
    }

    @Override
    public void setShareContext(EGLContext shareContext) {
        this.shareContext = shareContext;
        if (videoEncoderFactory != null) {
            videoEncoderFactory.setShareContext(shareContext);
        }
    }

    @Override
    public boolean isRecording() {
        return recorderStarted;
    }

    @Override
    public void startRecorder() {
        if (recorderStarted) {
            return;
        }
        recorderStarted = true;

        if (videoEncoderFactory != null) {
            ELog.i(TAG, "create video encoder");
            VideoEncoder videoEncoder = videoEncoderFactory.createEncoder(videoCodecInfo);
            if (videoEncoder == null) {
                ELog.e(TAG, "can't create video encoder.");
                return;
            }
            mVideoClient = new VideoClient(videoEncoder, videoSettings, mMp4Muxer);
            mVideoClient.start();
        }



        if (audioEncoder != null) {
            audioEncoder.startRecording();
        }

        if (mRecorderListener != null) {
            mRecorderListener.onRecorderStart();
        }
    }

    @Override
    public void jointVideo() {

    }

    @Override
    public void sendVideoFrame(int textureId, int srcWidth, int srcHeight) {
        if (mVideoClient != null && recorderStarted) {
            mVideoClient.sendVideoFrame(textureId, srcWidth, srcHeight);
        }
    }


    @Override
    public void stopRecorder() {
        if (mVideoClient != null) {
            mVideoClient.stop();
        }
        if (mRecorderListener != null) {
            mRecorderListener.onRecorderStop();
        }
        if (audioEncoder != null){
            audioEncoder.stopRecording();
        }
        if (mMp4Muxer != null){
            mMp4Muxer.stop();
        }
    }

    @Override
    public void setRecorderListener(RecorderListener listener) {
       this.mRecorderListener = listener;
    }


    @Override
    public void release() {
        if (audioEncoder != null) {
            audioEncoder.release();
        }
        if (mMp4Muxer != null) {
            mMp4Muxer.release();
        }
    }

}
