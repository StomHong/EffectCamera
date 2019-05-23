package com.gpufast.recorder;

import android.opengl.EGLContext;

public interface IRecorder {

    /**
     * Set the video recording parameter
     *
     * @param params {@linkplain RecorderParams}
     */
    void setParams(RecorderParams params);

    void setShareContext(EGLContext shareContext);

    boolean isRecording();

    void startRecorder();

    void stitchVideo();

    void sendVideoFrame(int textureId, int srcWidth, int srcHeight, long timeStamp);

    int getFps();

    void stopRecorder();

    void setRecorderListener(RecorderListener listener);

    void stop();

    void release();

    public interface RecorderListener {

        void onRecoderStart();

        void onRecoderStop();
    }
}
