package com.gpufast.recorder.muxer;

import android.media.MediaMuxer;
import android.util.Log;

import com.gpufast.recorder.video.EncodedImage;
import com.gpufast.recorder.video.VideoEncoder;
import com.gpufast.utils.ELog;

/**
 * 视频合成接口
 */
public class Mp4Muxer implements VideoEncoder.VideoEncoderCallback {
    private static final String TAG = MediaMuxer.class.getSimpleName();
    public MediaMuxer muxer;

    public Mp4Muxer() {
    }

    @Override
    public void onEncodedFrame(EncodedImage frame) {
        ELog.e(TAG, "onEncodedFrame: "+frame.buffer );
    }
}
