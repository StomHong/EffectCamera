package com.gpufast.recorder.muxer;

import android.media.MediaMuxer;
import android.util.Log;

import com.gpufast.recorder.audio.AudioEncoder;
import com.gpufast.recorder.audio.EncodedAudio;
import com.gpufast.recorder.video.EncodedImage;
import com.gpufast.recorder.video.VideoEncoder;

import java.io.IOException;

/**
 * 视频合成接口
 */
public class Mp4Muxer implements VideoEncoder.VideoEncoderCallback, AudioEncoder.AudioEncoderCallback {

    private static final String TAG = Mp4Muxer.class.getSimpleName();

    public MediaMuxer mMediaMuxer;
    private int audioTrackIndex = -1;
    private int videoTrackIndex = -1;
    boolean mediaMuxerStarted = false;

    public Mp4Muxer(String outputPath) {
        try {
            mMediaMuxer = new MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException e) {
            Log.e(TAG,"Init MediaMuxer:" + e.getMessage());
        }
    }

    @Override
    public void onEncodedFrame(EncodedImage frame) {
        if (videoTrackIndex == -1 && frame.mediaFormat != null && frame.buffer != null && audioTrackIndex != -1) {
            videoTrackIndex = mMediaMuxer.addTrack(frame.mediaFormat);
            if (videoTrackIndex < 0) {
                Log.e(TAG, "Add video track failed" );
            }
        }
        if (videoTrackIndex != -1 && audioTrackIndex != -1) {
            start();
            mMediaMuxer.writeSampleData(videoTrackIndex, frame.buffer, frame.bufferInfo);
            Log.i(TAG, "Write video data ，time="+frame.bufferInfo.presentationTimeUs );
        }

    }

    @Override
    public void onEncodedAudio(EncodedAudio frame) {
        if (audioTrackIndex == -1 && frame.mMediaFormat != null && frame.mBuffer != null) {
            audioTrackIndex = mMediaMuxer.addTrack(frame.mMediaFormat);
            if (audioTrackIndex < 0) {
                Log.e(TAG, "Add audio track failed" );
            }
        }
        if (mediaMuxerStarted) {
            mMediaMuxer.writeSampleData(audioTrackIndex, frame.mBuffer, frame.mBufferInfo);
            Log.i(TAG, "Write audio data，time="+frame.mBufferInfo.presentationTimeUs );
        }
    }

    private void start() {
        try {
            if (mMediaMuxer != null && !mediaMuxerStarted) {
                mMediaMuxer.start();
                mediaMuxerStarted = true;
            }
        } catch (Exception e) {
            Log.e(TAG,"Start MediaMuxer:" + e.getMessage());
            mediaMuxerStarted = false;
        }
    }

    public void stop() {
        try {
            if (mMediaMuxer != null)
                mMediaMuxer.stop();
        } catch (Exception e) {
            Log.e(TAG,"Stop MediaMuxer:" + e.getMessage());
        }
    }

    public void release() {
        try {
            if (mMediaMuxer != null)
                mMediaMuxer.release();
        } catch (Exception e) {
            Log.e(TAG,"Release MediaMuxer:" + e.getMessage());
        }
    }

}
