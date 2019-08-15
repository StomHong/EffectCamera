package com.gpufast.recorder;

import android.opengl.EGLContext;

import com.gpufast.logger.ELog;
import com.gpufast.recorder.audio.AudioClient;
import com.gpufast.recorder.audio.encoder.AudioCodecInfo;
import com.gpufast.recorder.audio.encoder.AudioEncoder;
import com.gpufast.recorder.audio.encoder.AudioEncoderFactory;
import com.gpufast.recorder.muxer.Mp4Muxer;
import com.gpufast.recorder.video.EncoderType;
import com.gpufast.recorder.video.VideoClient;
import com.gpufast.recorder.video.VideoEncoder;
import com.gpufast.recorder.video.VideoEncoderFactory;
import com.gpufast.recorder.video.encoder.VideoCodecInfo;

public class EffectRecorder extends BaseRecorder {

    private static final String TAG = EffectRecorder.class.getSimpleName();

    //视频码率(Kilobits per second）
    private final static int VIDEO_BITRATE = 4000;
    //视频帧率
    private final static int VIDEO_FRAME_RATE = 30;
    //音频采样率
    private final static int AUDIO_SAMPLE_RATE = 4000;
    //音频码率
    private final static int AUDIO_BITRATE = 30;

    private volatile boolean recorderStarted = false;

    private EGLContext shareContext;

    private VideoEncoderFactory videoEncoderFactory;

    private VideoCodecInfo videoCodecInfo;

    private VideoEncoder.Settings videoSettings;

    private VideoClient mVideoClient;


    private AudioEncoder.Settings audioSettings;

    private AudioEncoderFactory audioEncoderFactory;

    private AudioCodecInfo audioCodecInfo;

    private AudioClient mAudioClient;

    private Mp4Muxer mMp4Muxer;

    private RecorderListener mRecorderListener;


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
                    params.getVideoHeight(), VIDEO_BITRATE, VIDEO_FRAME_RATE);

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

        if (params.isEnableAudio()) {
            audioSettings = new AudioEncoder.Settings(AUDIO_SAMPLE_RATE, AUDIO_BITRATE);
            if (params.isHwEncoder()) {
                audioEncoderFactory = EncoderFactory.getAudioEncoder(EncoderType.HW_AUDIO_ENCODER);
            } else {
                audioEncoderFactory = EncoderFactory.getAudioEncoder(EncoderType.SW_AUDIO_ENCODER);
            }
            if (audioEncoderFactory != null) {
                audioCodecInfo = audioEncoderFactory.getSupportCodecInfo();
            }
        }

        mMp4Muxer = new Mp4Muxer(params.getSavePath());
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
            if (videoEncoder != null) {
                mVideoClient = new VideoClient(videoEncoder, videoSettings, mMp4Muxer);
                mVideoClient.start();
                return;
            } else {
                ELog.e(TAG, "can't create video encoder.");
            }
        }


        if (mAudioClient != null) {
            mAudioClient.start();
        }

        if (mRecorderListener != null) {
            mRecorderListener.onRecorderStart();
        }
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
        if (mAudioClient != null) {
            mAudioClient.stop();
        }
        if (mMp4Muxer != null) {
            mMp4Muxer.stop();
        }
    }


    @Override
    public void jointVideo() {

    }


    @Override
    public void setRecorderListener(RecorderListener listener) {
        this.mRecorderListener = listener;
    }


    @Override
    public void release() {
        if (mVideoClient != null) {
            mVideoClient.release();
            mVideoClient = null;
        }

        if (mAudioClient != null) {
            mAudioClient.release();
            mVideoClient = null;
        }
        if (mMp4Muxer != null) {
            mMp4Muxer.release();
            mMp4Muxer = null;
        }
    }


}
