package com.gpufast.recorder.muxer;

import android.media.MediaMuxer;

import com.gpufast.recorder.file.FileWriter;
import com.gpufast.recorder.video.EncodedImage;
import com.gpufast.recorder.video.VideoEncoder;

/**
 * 视频合成接口
 */
public class Mp4Muxer implements VideoEncoder.VideoEncoderCallback {

    public FileWriter mH264Writer;
    public MediaMuxer mMediaMuxer;

    public Mp4Muxer(String outputPath) {
        mH264Writer = new FileWriter(outputPath);
    }

    public void start(){
        mH264Writer.startWrite();
    }

    public void stop(){
        mH264Writer.stopWrite();
    }


    @Override
    public void onEncodedFrame(EncodedImage frame) {
        if (mH264Writer != null) {
            mH264Writer.writeToFile(frame.buffer);
        }
    }
}
