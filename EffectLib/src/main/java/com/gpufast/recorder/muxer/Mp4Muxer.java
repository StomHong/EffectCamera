package com.gpufast.recorder.muxer;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;

import com.gpufast.recorder.audio.AudioEncoder;
import com.gpufast.recorder.file.FileWriter;
import com.gpufast.recorder.video.EncodedImage;
import com.gpufast.recorder.video.VideoEncoder;
import com.gpufast.recorder.video.encoder.VideoCodecType;

import java.io.IOException;

/**
 * 视频合成接口
 */
public class Mp4Muxer implements VideoEncoder.VideoEncoderCallback, AudioEncoder.AudioEncoderCallback {

    public FileWriter mH264Writer;
    public MediaMuxer mMediaMuxer;
    int audioTrackIndex = -1;
    int videoTrackIndex = -1;

    public Mp4Muxer(String outputPath) {
        mH264Writer = new FileWriter(outputPath);
        try {
            mMediaMuxer = new MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
//        mH264Writer.startWrite();
        if (mMediaMuxer != null) {
            mMediaMuxer.start();
        }
    }

    public void stop() {
//        mH264Writer.stopWrite();
        if (mMediaMuxer != null) {
            mMediaMuxer.stop();
        }
    }

    public void release() {
        if (mMediaMuxer != null) {
            mMediaMuxer.release();
        }
    }

    @Override
    public void onEncodedFrame(EncodedImage frame, MediaCodec.BufferInfo info,MediaFormat format) {
        if (mH264Writer != null) {
//            mH264Writer.writeToFile(frame.buffer);
        }
        if (videoTrackIndex == -1) {
            videoTrackIndex = mMediaMuxer.addTrack(format);
            mMediaMuxer.start();
        }
        if (mMediaMuxer != null) {
            mMediaMuxer.writeSampleData(videoTrackIndex, frame.buffer, info);
        }
    }

    @Override
    public void onEncodedAudio(EncodedImage frame, MediaCodec.BufferInfo info, MediaFormat format) {
        if (mMediaMuxer != null) {
            mMediaMuxer.writeSampleData(audioTrackIndex, frame.buffer, info);
        }
    }
}
