package com.gpufast.recoder.muxer;

import android.media.MediaMuxer;

import com.gpufast.recoder.video.EncodedImage;
import com.gpufast.recoder.video.VideoEncoder;

/**
 * 视频合成接口
 */
public class Mp4Muxer implements VideoEncoder.VideoEncoderCallback {

    public MediaMuxer muxer;

    public Mp4Muxer() {

    }

    @Override
    public void onEncodedFrame(EncodedImage frame) {

    }
}
