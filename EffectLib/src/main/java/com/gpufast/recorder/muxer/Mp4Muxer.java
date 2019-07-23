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
public class Mp4Muxer implements VideoEncoder.VideoEncoderCallback, AudioEncoder.AudioEncoderCallback {

    public MediaMuxer mMediaMuxer;
    private int audioTrackIndex = -1;
    private int videoTrackIndex = -1;
    boolean hasStarted = false;

    public Mp4Muxer(String outputPath) {
        try {
            mMediaMuxer = new MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEncodedFrame(EncodedImage frame) {
        if (videoTrackIndex == -1 && frame.mediaFormat != null) {
            videoTrackIndex = mMediaMuxer.addTrack(frame.mediaFormat);
        }
        if (videoTrackIndex != -1 && audioTrackIndex != -1) {
            start();
            mMediaMuxer.writeSampleData(videoTrackIndex, frame.buffer, frame.bufferInfo);
        }

    }

    @Override
    public void onEncodedAudio(EncodedAudio frame) {
        if (audioTrackIndex == -1 && frame.mMediaFormat != null) {
            audioTrackIndex = mMediaMuxer.addTrack(frame.mMediaFormat);
        }
        if (hasStarted) {
            mMediaMuxer.writeSampleData(audioTrackIndex, frame.mBuffer, frame.mBufferInfo);
        }
    }

    private void start() {
        try {
            if (mMediaMuxer != null) {
                mMediaMuxer.start();
                hasStarted = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            hasStarted = false;
        }
    }

    public void stop() {
        try {
            if (mMediaMuxer != null)
                mMediaMuxer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void release() {
        if (mMediaMuxer != null)
            mMediaMuxer.release();
    }

}
