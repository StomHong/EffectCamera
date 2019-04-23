package com.gpufast.recorder.muxer;

import android.media.MediaMuxer;

import com.gpufast.recorder.video.EncodedImage;
import com.gpufast.recorder.video.VideoEncoder;
import com.gpufast.utils.ELog;

import java.io.IOException;

/**
 * 视频合成接口
 */
public class Mp4Muxer implements VideoEncoder.VideoEncoderCallback {
    private static final String TAG = MediaMuxer.class.getSimpleName();
    public MediaMuxer mMediaMuxer;

    public Mp4Muxer(String outputPath){
        try {
            mMediaMuxer = new MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEncodedFrame(EncodedImage frame) {
        ELog.e("sivin", "onEncodedFrame: "+frame.captureTimeNs );
    }
}
