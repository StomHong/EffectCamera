package com.gpufast.recorder.video.encoder;

import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.opengl.EGLContext;
import android.os.Build;

import com.gpufast.recorder.video.VideoEncoder;
import com.gpufast.recorder.video.VideoEncoderFactory;
import com.gpufast.recorder.video.btadjuster.FrameRateBitrateAdjuster;
import com.gpufast.utils.ELog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.gpufast.recorder.video.encoder.MediaCodecUtils.EXYNOS_PREFIX;
import static com.gpufast.recorder.video.encoder.MediaCodecUtils.HISI_PREFIX;
import static com.gpufast.recorder.video.encoder.MediaCodecUtils.QCOM_PREFIX;


/**
 * 该类的主要目的是，对想要选择的硬编码器进行提前探测，检测
 * 确保能准确的获得想要的编码器
 */
public class HardwareVideoEncoderFactory implements VideoEncoderFactory {

    private EGLContext sharedContext;
    private static final List<String> H264_HW_EXCEPTION_MODELS =
            Arrays.asList("SAMSUNG-SGH-I337", "Nexus 7", "Nexus 4");


    @Override
    public void setShareContext(EGLContext shareContext) {
        sharedContext = shareContext;
    }

    @Override
    public VideoCodecInfo[] getSupportedCodecs() {
        List<VideoCodecInfo> supportedCodecInfoList = new ArrayList<>();
        for (VideoCodecType type : new VideoCodecType[]{VideoCodecType.H264}) {
            MediaCodecInfo codecInfo = findCodecForType(type);
            if (codecInfo != null) {
                String name = type.name();
                if (isH264HighProfileSupported(codecInfo)) {
                    supportedCodecInfoList.add(new VideoCodecInfo(name, VideoCodecInfo.Profile.HHEIGHT));
                }
                supportedCodecInfoList.add(new VideoCodecInfo(name, VideoCodecInfo.Profile.BASE_LINE));
            }
        }

        int size = supportedCodecInfoList.size();

        return supportedCodecInfoList.toArray(new VideoCodecInfo[size]);
    }

    @Override
    public VideoEncoder createEncoder(VideoCodecInfo inputCodecInfo) {
        //编码器的类型
        VideoCodecType type = VideoCodecType.valueOf(inputCodecInfo.name);
        //根据类型查找编码器的信息
        MediaCodecInfo info = findCodecForType(type);
        if (info == null) {
            ELog.e(this, "can't find Encoder by type" + inputCodecInfo.name);
            return null;
        }

        String codecName = info.getName();
        String mime = type.mimeType();
        ELog.d(this, "codecName :" + codecName + " mime:" + mime);
        Integer surfaceColorFormat = MediaCodecUtils.selectColorFormat(
                MediaCodecUtils.TEXTURE_COLOR_FORMATS, info.getCapabilitiesForType(mime));

        return new HardwareVideoEncoder(new MediaCodecWrapperFactoryImpl(), codecName, type,
                surfaceColorFormat, inputCodecInfo.params, getKeyFrameIntervalSec(type),
                new FrameRateBitrateAdjuster(),
                sharedContext);
    }

    /**
     * 获取编码器的关键帧间隔
     *
     * @param type type
     * @return KeyFrameIntervalSec 每个多少每秒一个关键帧
     */
    private int getKeyFrameIntervalSec(VideoCodecType type) {
        switch (type) {
            case H264:
                return 20;
            case H265:
                //TODO：实现265
                return 20;
        }
        throw new IllegalArgumentException("Unsupported VideoCodecType " + type);
    }

    private MediaCodecInfo findCodecForType(VideoCodecType type) {
        for (int i = 0; i < MediaCodecList.getCodecCount(); ++i) {
            MediaCodecInfo info = null;
            try {
                info = MediaCodecList.getCodecInfoAt(i);
            } catch (IllegalArgumentException e) {
                ELog.e(this, "Cannot retrieve encoder codec info", e);
            }
            if (info == null || !info.isEncoder()) {
                continue;
            }
            if (isSupportedCodec(info, type)) {
                return info;
            }
        }
        return null;
    }

    // 判断编码器是否支持硬编码
    private boolean isSupportedCodec(MediaCodecInfo info, VideoCodecType type) {
        if (!MediaCodecUtils.codecSupportsType(info, type)) {
            return false;
        }
        if (MediaCodecUtils.selectColorFormat(
                MediaCodecUtils.ENCODER_COLOR_FORMATS, info.getCapabilitiesForType(type.mimeType()))
                == null) {
            return false;
        }
        return isHardwareSupportedInCurrentSdk(info, type);
    }

    private boolean isHardwareSupportedInCurrentSdk(MediaCodecInfo info, VideoCodecType type) {
        switch (type) {
            case H264:
                return isHardwareSupportedInCurrentSdkH264(info);
            case H265:
                //TODO:实现H265编码检测
                return false;
        }
        return false;
    }


    /**
     * 如果是高通的处理器， 则4.4之后支持硬编码，三星的处理器5.1之后支持
     */
    @SuppressWarnings("all")
    private boolean isHardwareSupportedInCurrentSdkH264(MediaCodecInfo info) {
        if (H264_HW_EXCEPTION_MODELS.contains(Build.MODEL)) {
            return false;
        }
        String name = info.getName();
        if (name.startsWith(QCOM_PREFIX) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return true;
        }
        if ((name.startsWith(EXYNOS_PREFIX) || (name.startsWith(HISI_PREFIX))
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            return true;
        }
        return false;
    }


    private boolean isH264HighProfileSupported(MediaCodecInfo info) {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.M
                && info.getName().startsWith(EXYNOS_PREFIX);
    }
}
