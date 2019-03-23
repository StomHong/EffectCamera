///*
// *  Copyright 2017 The WebRTC project authors. All Rights Reserved.
// *
// *  Use of this source code is governed by a BSD-style license
// *  that can be found in the LICENSE file in the root of the source
// *  tree. An additional intellectual property rights grant can be found
// *  in the file PATENTS.  All contributing project authors may
// *  be found in the AUTHORS file in the root of the source tree.
// */
//
//package com.gpufast.recoder.encoder.video;
//
//import android.annotation.TargetApi;
//import android.media.MediaCodec;
//import android.media.MediaFormat;
//import android.opengl.GLES20;
//import android.os.Bundle;
//import android.view.Surface;
//
//import com.gpufast.gles.EglBase;
//import com.gpufast.gles.EglBase14;
//import com.gpufast.recoder.encoder.video2.EncodedImage;
//import com.gpufast.recoder.encoder.video2.GlRectDrawer;
//import com.gpufast.recoder.encoder.video2.VideoCodecInfo;
//import com.gpufast.recoder.encoder.video2.VideoCodecType;
//import com.gpufast.recoder.encoder.video2.VideoEncoder;
//import com.gpufast.utils.ELog;
//import com.gpufast.utils.ThreadUtils;
//
//
//import java.io.IOException;
//import java.nio.ByteBuffer;
//import java.util.Map;
//import java.util.concurrent.BlockingDeque;
//import java.util.concurrent.LinkedBlockingDeque;
//
///**
// * Android hardware video encoder.
// *
// * @note This class is only supported on Android Kitkat and above.
// */
//@TargetApi(19)
//@SuppressWarnings("deprecation")
//        // Cannot support API level 19 without using deprecated methods.
//class HardwareVideoEncoder implements VideoEncoder {
//    private static final String TAG = "HardwareVideoEncoder";
//    // Bitrate modes - should be in sync with OMX_VIDEO_CONTROLRATETYPE defined
//    // in OMX_Video.h
//    private static final int VIDEO_ControlRateConstant = 2;
//    // Key associated with the bitrate control mode value (above). Not present as a MediaFormat
//    // constant until API level 21.
//    private static final String KEY_BITRATE_MODE = "bitrate-mode";
//
//    private static final int VIDEO_AVC_PROFILE_HIGH = 8;
//    private static final int VIDEO_AVC_LEVEL_3 = 0x100;
//
//    private static final int MAX_VIDEO_FRAMERATE = 30;
//
//    // See MAX_ENCODER_Q_SIZE in androidmediaencoder.cc.
//    private static final int MAX_ENCODER_Q_SIZE = 2;
//
//    private static final int MEDIA_CODEC_RELEASE_TIMEOUT_MS = 5000;
//    private static final int DEQUEUE_OUTPUT_BUFFER_TIMEOUT_US = 100000;
//
//    // --- Initialized on construction.
//    private final MediaCodecWrapperFactory mediaCodecWrapperFactory;
//    //编码器名称
//    private final String codecName;
//    //编码器类型H264
//    private final VideoCodecType codecType;
//    //surface的颜色格式
//    private final Integer surfaceColorFormat;
//
//    private final Map<String, String> params;
//    private final int keyFrameIntervalSec; // Base interval for generating key frames.
//    // Qualcomm video encoders.
//    private final BitrateAdjuster bitrateAdjuster;
//    private final EglBase14.Context sharedContext;
//
//    // Drawer used to draw input textures onto the codec's input surface.
//    private final GlRectDrawer textureDrawer = new GlRectDrawer();
//    private final VideoFrameDrawer videoFrameDrawer = new VideoFrameDrawer();
//
//
//    // 一个EncodedImage队列。与编解码器中的帧相对应的构造器
//    // These builders are pre-populated with all the information that can't be sent through MediaCodec.
//    private final BlockingDeque<EncodedImage.Builder> outputBuilders = new LinkedBlockingDeque<>();
//
//    private final ThreadUtils.ThreadChecker encodeThreadChecker = new ThreadUtils.ThreadChecker();
//    private final ThreadUtils.ThreadChecker outputThreadChecker = new ThreadUtils.ThreadChecker();
//
//    // --- Set on initialize and immutable until release.
//    private Callback callback;
//    private boolean automaticResizeOn;
//
//    // --- Valid and immutable while an encoding session is running.
//    private MediaCodecWrapper codec;
//    // Thread that delivers encoded frames to the user callback.
//    private Thread outputThread;
//
//    // EGL base wrapping the shared texture context.  Holds hooks to both the shared context and the
//    // input surface.  Making this base current allows textures from the context to be drawn onto the
//    // surface.
//    private EglBase textureEglBase;
//    // Input surface for he codec.  The encoder will draw input textures onto this surface.
//    private Surface textureInputSurface;
//
//    private int width;
//    private int height;
//
//    // --- Only accessed on the output thread.
//    // Contents of the last observed config frame output by the MediaCodec. Used by H.264.
//    private ByteBuffer configBuffer;
//    private int adjustedBitrate;
//
//    // Whether the encoder is running.  Volatile so that the output thread can watch this value and
//    // exit when the encoder stops.
//    private volatile boolean running;
//    // Any exception thrown during shutdown.  The output thread releases the MediaCodec and uses this
//    // value to send exceptions thrown during release back to the encoder thread.
//    private volatile Exception shutdownException;
//
//    /**
//     * Creates a new HardwareVideoEncoder with the given codecName, codecType, colorFormat, key frame
//     * intervals, and bitrateAdjuster.
//     *
//     * @param codecName           the hardware codec implementation to use
//     * @param codecType           the type of the given video codec (eg. VP8, VP9, or H264)
//     * @param surfaceColorFormat  color format for surface mode or null if not available
//     * @param keyFrameIntervalSec interval in seconds between key frames; used to initialize the codec
//     * @param bitrateAdjuster     algorithm used to correct codec implementations that do not produce the
//     *                            desired bitrates
//     * @throws IllegalArgumentException if colorFormat is unsupported
//     */
//    public HardwareVideoEncoder(MediaCodecWrapperFactory mediaCodecWrapperFactory, String codecName,
//                                VideoCodecType codecType, Integer surfaceColorFormat,
//                                Map<String, String> params, int keyFrameIntervalSec,
//                                BitrateAdjuster bitrateAdjuster, EglBase14.Context sharedContext) {
//        this.mediaCodecWrapperFactory = mediaCodecWrapperFactory;
//        this.codecName = codecName;
//        this.codecType = codecType;
//        this.surfaceColorFormat = surfaceColorFormat;
//        this.params = params;
//        this.keyFrameIntervalSec = keyFrameIntervalSec;
//        this.bitrateAdjuster = bitrateAdjuster;
//        this.sharedContext = sharedContext;
//        // Allow construction on a different thread.
//        encodeThreadChecker.detachThread();
//    }
//
//    @Override
//    public VideoCodecStatus initEncode(VideoSettings settings, Callback callback) {
//        encodeThreadChecker.checkIsOnValidThread();
//        this.callback = callback;
//        automaticResizeOn = settings.automaticResizeOn;
//        this.width = settings.width;
//        this.height = settings.height;
//
//        if (settings.startBitrate != 0 && settings.maxFrameRate != 0) {
//            bitrateAdjuster.setTargets(settings.startBitrate * 1000, settings.maxFrameRate);
//        }
//
//        adjustedBitrate = bitrateAdjuster.getAdjustedBitrateBps();
//
//        ELog.d(TAG,
//                "initEncode: " + width + " x " + height + ". @ " + settings.startBitrate
//                        + "kbps. Fps: " + settings.maxFrameRate);
//        return initEncodeInternal();
//    }
//
//    private VideoCodecStatus initEncodeInternal() {
//        encodeThreadChecker.checkIsOnValidThread();
//        try {
//            codec = mediaCodecWrapperFactory.createByCodecName(codecName);
//        } catch (IOException | IllegalArgumentException e) {
//            ELog.e(TAG, "Cannot create media encoder " + codecName);
//            return VideoCodecStatus.FALLBACK_SOFTWARE;
//        }
//        final int colorFormat = surfaceColorFormat;
//        try {
//            MediaFormat format = MediaFormat.createVideoFormat(codecType.mimeType(), width, height);
//            format.setInteger(MediaFormat.KEY_BIT_RATE, adjustedBitrate);
//            format.setInteger(KEY_BITRATE_MODE, VIDEO_ControlRateConstant);
//            format.setInteger(MediaFormat.KEY_COLOR_FORMAT, colorFormat);
//            format.setInteger(MediaFormat.KEY_FRAME_RATE, bitrateAdjuster.getCodecConfigFrameRate());
//            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, keyFrameIntervalSec);
//
//            if (codecType == VideoCodecType.H264) {
//                String profileLevelId = params.get(VideoCodecInfo.H264_FMTP_PROFILE_LEVEL_ID);
//                if (profileLevelId == null) {
//                    profileLevelId = VideoCodecInfo.H264_CONSTRAINED_BASELINE_3_1;
//                }
//                switch (profileLevelId) {
//                    case VideoCodecInfo.H264_CONSTRAINED_HIGH_3_1:
//                        format.setInteger("profile", VIDEO_AVC_PROFILE_HIGH);
//                        format.setInteger("level", VIDEO_AVC_LEVEL_3);
//                        break;
//                    case VideoCodecInfo.H264_CONSTRAINED_BASELINE_3_1:
//                        break;
//                    default:
//                        ELog.w(TAG, "Unknown profile level id: " + profileLevelId);
//                }
//            }
//            ELog.d(TAG, "Format: " + format);
//
//            codec.configure(
//                    format, null /* surface */, null /* crypto */, MediaCodec.CONFIGURE_FLAG_ENCODE);
//
//            textureEglBase = new EglBase14(sharedContext, EglBase.CONFIG_RECORDABLE);
//            textureInputSurface = codec.createInputSurface();
//            textureEglBase.createSurface(textureInputSurface);
//            textureEglBase.makeCurrent();
//
//            codec.start();
//        } catch (IllegalStateException e) {
//            ELog.e(TAG, "initEncodeInternal failed", e);
//            release();
//            return VideoCodecStatus.FALLBACK_SOFTWARE;
//        }
//        running = true;
//        outputThreadChecker.detachThread();
//        outputThread = createOutputThread();
//        outputThread.start();
//        return VideoCodecStatus.OK;
//    }
//
//
//
//    @Override
//    public VideoCodecStatus encode(VideoFrame videoFrame) {
//        encodeThreadChecker.checkIsOnValidThread();
//        if (codec == null) {
//            return VideoCodecStatus.UNINITIALIZED;
//        }
//
//        final VideoFrame.Buffer videoFrameBuffer = videoFrame.getBuffer();
//        final boolean isTextureBuffer = videoFrameBuffer instanceof VideoFrame.TextureBuffer;
//
//        //如果输入分辨率发生变化，则使用新分辨率重新启动编解码器
//        final int frameWidth = videoFrame.getBuffer().getWidth();
//        final int frameHeight = videoFrame.getBuffer().getHeight();
//
//        if (frameWidth != width || frameHeight != height) {
//            VideoCodecStatus status = resetCodec(frameWidth, frameHeight);
//            if (status != VideoCodecStatus.OK) {
//                return status;
//            }
//        }
//
//        if (outputBuilders.size() > MAX_ENCODER_Q_SIZE) {
//            //编码器中的有太多帧数据，需要丢掉该帧
//            ELog.e(TAG, "Dropped frame, encoder queue full");
//            return VideoCodecStatus.NO_OUTPUT; // See webrtc bug 2887.
//        }
//
//
//        EncodedImage.Builder builder = EncodedImage.builder()
//                .setCaptureTimeNs(videoFrame.getTimestampNs())
//                .setCompleteFrame(true)
//                .setEncodedWidth(videoFrame.getBuffer().getWidth())
//                .setEncodedHeight(videoFrame.getBuffer().getHeight())
//                .setRotation(videoFrame.getRotation());
//
//        outputBuilders.offer(builder);
//
//        final VideoCodecStatus returnValue = encodeTextureBuffer(videoFrame);
//        if (returnValue != VideoCodecStatus.OK) {
//            outputBuilders.pollLast();
//        }
//        return returnValue;
//    }
//
//    private VideoCodecStatus encodeTextureBuffer(VideoFrame videoFrame) {
//        encodeThreadChecker.checkIsOnValidThread();
//        try {
//            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
//            // 没有必要去释放这个frame，因为它没有buffer数据.
//            VideoFrame derotatedFrame = new VideoFrame(videoFrame.getBuffer(), 0 /* rotation */, videoFrame.getTimestampNs());
//            videoFrameDrawer.drawFrame(derotatedFrame, textureDrawer, null /* additionalRenderMatrix */);
//            textureEglBase.swapBuffers(videoFrame.getTimestampNs());
//        } catch (RuntimeException e) {
//            ELog.e(TAG, "encodeTexture failed", e);
//            return VideoCodecStatus.ERROR;
//        }
//        return VideoCodecStatus.OK;
//    }
//
//    @Override
//    public String getImplementationName() {
//        return "HWEncoder";
//    }
//
//    /**
//     * 重置编码器
//     * @param newWidth          newWidth
//     * @param newHeight         newHeight
//     * @return
//     */
//    private VideoCodecStatus resetCodec(int newWidth, int newHeight) {
//        encodeThreadChecker.checkIsOnValidThread();
//        VideoCodecStatus status = release();
//        if (status != VideoCodecStatus.OK) {
//            return status;
//        }
//        width = newWidth;
//        height = newHeight;
//        return initEncodeInternal();
//    }
//
//    private Thread createOutputThread() {
//        return new Thread() {
//            @Override
//            public void run() {
//                while (running) {
//                    deliverEncodedImage();
//                }
//                releaseCodecOnOutputThread();
//            }
//        };
//    }
//
//    /**
//     * 开启线程从Encoder里不断的读取编码后的h264数据
//     */
//    protected void deliverEncodedImage() {
//        //检查线程
//        outputThreadChecker.checkIsOnValidThread();
//        try {
//            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
//            int index = codec.dequeueOutputBuffer(info, DEQUEUE_OUTPUT_BUFFER_TIMEOUT_US);
//            if (index < 0) {
//                return;
//            }
//            ByteBuffer codecOutputBuffer = codec.getOutputBuffers()[index];
//            codecOutputBuffer.position(info.offset);
//            codecOutputBuffer.limit(info.offset + info.size);
//
//            if ((info.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
//                ELog.d(TAG, "Config frame generated. Offset: " + info.offset + ". Size: " + info.size);
//                configBuffer = ByteBuffer.allocateDirect(info.size);
//                configBuffer.put(codecOutputBuffer);
//            } else {
//                bitrateAdjuster.reportEncodedFrame(info.size);
//                if (adjustedBitrate != bitrateAdjuster.getAdjustedBitrateBps()) {
//                    updateBitrate();
//                }
//
//                final boolean isKeyFrame = (info.flags & MediaCodec.BUFFER_FLAG_SYNC_FRAME) != 0;
//                if (isKeyFrame) {
//                    ELog.d(TAG, "Sync frame generated");
//                }
//
//                final ByteBuffer frameBuffer;
//                if (isKeyFrame && codecType == VideoCodecType.H264) {
//                    ELog.d(TAG,
//                            "Prepending config frame of size " + configBuffer.capacity()
//                                    + " to output buffer with offset " + info.offset + ", size " + info.size);
//                    // For H.264 key frame prepend SPS and PPS NALs at the start.
//                    frameBuffer = ByteBuffer.allocateDirect(info.size + configBuffer.capacity());
//                    configBuffer.rewind();
//                    frameBuffer.put(configBuffer);
//                    frameBuffer.put(codecOutputBuffer);
//                    frameBuffer.rewind();
//                } else {
//                    frameBuffer = codecOutputBuffer.slice();
//                }
//
//                final EncodedImage.FrameType frameType = isKeyFrame
//                        ? EncodedImage.FrameType.VideoFrameKey
//                        : EncodedImage.FrameType.VideoFrameDelta;
//                EncodedImage.Builder builder = outputBuilders.poll();
//                builder.setBuffer(frameBuffer).setFrameType(frameType);
//                callback.onEncodedFrame(builder.createEncodedImage());
//            }
//            codec.releaseOutputBuffer(index, false);
//        } catch (IllegalStateException e) {
//            ELog.e(TAG, "deliverOutput failed", e);
//        }
//    }
//
//
//    @Override
//    public VideoCodecStatus release() {
//        encodeThreadChecker.checkIsOnValidThread();
//
//        final VideoCodecStatus returnValue;
//        if (outputThread == null) {
//            returnValue = VideoCodecStatus.OK;
//        } else {
//            // The outputThread actually stops and releases the codec once running is false.
//            running = false;
//            if (!ThreadUtils.joinUninterruptibly(outputThread, MEDIA_CODEC_RELEASE_TIMEOUT_MS)) {
//                ELog.e(TAG, "Media encoder release timeout");
//                returnValue = VideoCodecStatus.TIMEOUT;
//            } else if (shutdownException != null) {
//                // Log the exception and turn it into an error.
//                ELog.e(TAG, "Media encoder release exception", shutdownException);
//                returnValue = VideoCodecStatus.ERROR;
//            } else {
//                returnValue = VideoCodecStatus.OK;
//            }
//        }
//
//        textureDrawer.release();
//        videoFrameDrawer.release();
//
//        if (textureEglBase != null) {
//            textureEglBase.release();
//            textureEglBase = null;
//        }
//        if (textureInputSurface != null) {
//            textureInputSurface.release();
//            textureInputSurface = null;
//        }
//        outputBuilders.clear();
//
//        codec = null;
//        outputThread = null;
//
//        // Allow changing thread after release.
//        encodeThreadChecker.detachThread();
//        return returnValue;
//    }
//
//    private void releaseCodecOnOutputThread() {
//        outputThreadChecker.checkIsOnValidThread();
//        ELog.d(TAG, "Releasing MediaCodec on output thread");
//        try {
//            codec.stop();
//        } catch (Exception e) {
//            ELog.e(TAG, "Media encoder stop failed", e);
//        }
//        try {
//            codec.release();
//        } catch (Exception e) {
//            ELog.e(TAG, "Media encoder release failed", e);
//            // Propagate exceptions caught during release back to the main thread.
//            shutdownException = e;
//        }
//        configBuffer = null;
//        ELog.d(TAG, "Release on output thread done");
//    }
//
//    private VideoCodecStatus updateBitrate() {
//        outputThreadChecker.checkIsOnValidThread();
//        adjustedBitrate = bitrateAdjuster.getAdjustedBitrateBps();
//        try {
//            Bundle params = new Bundle();
//            params.putInt(MediaCodec.PARAMETER_KEY_VIDEO_BITRATE, adjustedBitrate);
//            codec.setParameters(params);
//            return VideoCodecStatus.OK;
//        } catch (IllegalStateException e) {
//            ELog.e(TAG, "updateBitrate failed", e);
//            return VideoCodecStatus.ERROR;
//        }
//    }
//
//
//}
