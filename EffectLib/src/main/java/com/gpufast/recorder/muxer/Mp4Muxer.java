package com.gpufast.recorder.muxer;

import android.media.MediaMuxer;

import com.gpufast.recorder.audio.AudioEncoder;
import com.gpufast.recorder.audio.EncodedAudio;
import com.gpufast.recorder.video.EncodedImage;
import com.gpufast.recorder.video.VideoEncoder;

import java.io.IOException;

/**
 * 视频合成接口
 */
class Mp4Muxer implements VideoEncoder.VideoEncoderCallback, AudioEncoder.AudioEncoderCallback {

    public MediaMuxer mMediaMuxer;

    public Mp4Muxer(String outputPath) {
        try {
             mMediaMuxer = new MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEncodedFrame(EncodedImage frame) {
        mMediaMuxer.writeSampleData(-1,frame.buffer,frame.bufferInfo);
    }

    @Override
    public void onEncodedAudio(EncodedAudio frame) {
        mMediaMuxer.writeSampleData(-1,frame.mBuffer,frame.mBufferInfo);
    }


}
