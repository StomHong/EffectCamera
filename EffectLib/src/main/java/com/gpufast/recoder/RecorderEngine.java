package com.gpufast.recoder;

public interface RecorderEngine {

    void setParams(RecorderParams params);

    void startRecorder();

    void stopRecorder();

    void stop();

    //合成多段mp4
    void jointVideo();


    void release();
}
