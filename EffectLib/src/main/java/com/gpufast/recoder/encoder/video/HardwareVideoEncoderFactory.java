//
//package com.gpufast.recoder.encoder.video;
//
//import android.media.MediaCodecInfo;
//import android.media.MediaCodecList;
//import android.os.Build;
//
//import com.gpufast.gles.EglBase14;
//import com.gpufast.recoder.encoder.VideoEncoderFactory;
//import com.gpufast.recoder.encoder.video2.MediaCodecUtils;
//import com.gpufast.recoder.encoder.video2.VideoCodecInfo;
//import com.gpufast.recoder.encoder.video2.VideoCodecType;
//import com.gpufast.recoder.encoder.video2.VideoEncoder;
//import com.gpufast.utils.ELog;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import static com.gpufast.recoder.encoder.video2.MediaCodecUtils.EXYNOS_PREFIX;
//import static com.gpufast.recoder.encoder.video2.MediaCodecUtils.QCOM_PREFIX;
//
//
//@SuppressWarnings("deprecation")
//public class HardwareVideoEncoderFactory implements VideoEncoderFactory {
//    private static final String TAG = "HardwareVideoEncoderFactory";
//    //不支持h264编码的设备厂商
//    private static final List<String> H264_HW_EXCEPTION_MODELS =
//            Arrays.asList("SAMSUNG-SGH-I337", "Nexus 7", "Nexus 4");
//
//    private final EglBase14.Context sharedContext;
//
//    public HardwareVideoEncoderFactory(EglBase14.Context sharedContext) {
//
//    }
//
//    @Override
//    public VideoEncoder createEncoder(VideoCodecInfo inputCodecInfo) {
//        // 4.4之前不支持硬编码
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
//            return null;
//        }
//        //编码器的类型
//        VideoCodecType type = VideoCodecType.valueOf(inputCodecInfo.name);
//        //根据类型查找编码器的信息
//        MediaCodecInfo info = findCodecForType(type);
//        if (info == null) {
//            ELog.e(TAG, "can't find Encoder by type" + inputCodecInfo.name);
//            return null;
//        }
//
//        String codecName = info.getName();
//        String mime = type.mimeType();
//
//        Integer surfaceColorFormat = MediaCodecUtils.selectColorFormat(
//                MediaCodecUtils.TEXTURE_COLOR_FORMATS, info.getCapabilitiesForType(mime));
//
//
//        return new HardwareVideoEncoder(new MediaCodecWrapperFactoryImpl(), codecName, type,
//                surfaceColorFormat, inputCodecInfo.params, getKeyFrameIntervalSec(type),
//                createBitrateAdjuster(type, codecName),
//                sharedContext);
//    }
//
//    @Override
//    public VideoCodecInfo[] getSupportedCodecs() {
//        // 4.4之前不支持硬编码
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
//            return new VideoCodecInfo[0];
//        }
//        List<VideoCodecInfo> supportedCodecInfos = new ArrayList<VideoCodecInfo>();
//        for (VideoCodecType type : new VideoCodecType[]{VideoCodecType.H264}) {
//            MediaCodecInfo codec = findCodecForType(type);
//            if (codec != null) {
//                String name = type.name();
//                // 判断编码器是否支持highProfile
//                if (isH264HighProfileSupported(codec)) {
//                    supportedCodecInfos.add(new VideoCodecInfo(
//                            name, MediaCodecUtils.getCodecProperties(type, /* highProfile= */ true)));
//                }
//                supportedCodecInfos.add(new VideoCodecInfo(
//                        name, MediaCodecUtils.getCodecProperties(type, /* highProfile= */ false)));
//            }
//        }
//
//        return supportedCodecInfos.toArray(new VideoCodecInfo[supportedCodecInfos.size()]);
//    }
//
//    private MediaCodecInfo findCodecForType(VideoCodecType type) {
//        for (int i = 0; i < MediaCodecList.getCodecCount(); ++i) {
//            MediaCodecInfo info = null;
//            try {
//                info = MediaCodecList.getCodecInfoAt(i);
//            } catch (IllegalArgumentException e) {
//                ELog.e(TAG, "Cannot retrieve encoder codec info", e);
//            }
//
//            if (info == null || !info.isEncoder()) {
//                continue;
//            }
//
//            if (isSupportedCodec(info, type)) {
//                return info;
//            }
//        }
//        return null; // No support for this type.
//    }
//
//
//
//    private boolean isHardwareSupportedInCurrentSdk(MediaCodecInfo info, VideoCodecType type) {
//        switch (type) {
//            case H264:
//                return isHardwareSupportedInCurrentSdkH264(info);
//        }
//        return false;
//    }
//
//
//    /**
//     * 如果是高通的处理器，
//     * 则4.4之后支持硬编码，三星的处理器5.1之后支持
//     */
//    private boolean isHardwareSupportedInCurrentSdkH264(MediaCodecInfo info) {
//        if (H264_HW_EXCEPTION_MODELS.contains(Build.MODEL)) {
//            return false;
//        }
//        String name = info.getName();
//        return (name.startsWith(QCOM_PREFIX) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
//                || (name.startsWith(EXYNOS_PREFIX)
//                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
//    }
//
//    /**
//     * 获取编码器的关键帧间隔
//     *
//     * @param type
//     * @return
//     */
//    private int getKeyFrameIntervalSec(VideoCodecType type) {
//        switch (type) {
//            case H264:
//                return 20;
//        }
//        throw new IllegalArgumentException("Unsupported VideoCodecType " + type);
//    }
//
//
//    private BitrateAdjuster createBitrateAdjuster(VideoCodecType type, String codecName) {
//        if (codecName.startsWith(EXYNOS_PREFIX)) {
//            if (type == VideoCodecType.H264) {
//                return new FrameRateBitrateAdjuster();
//            }
//        }
//        return new BaseBitrateAdjuster();
//    }
//
//    /**
//     * 判断编码器是否支持highProfile
//     * sdk 版本必须是大于6.0，cpu需要适配
//     *
//     * @param info
//     * @return
//     */
//    private boolean isH264HighProfileSupported(MediaCodecInfo info) {
//        return Build.VERSION.SDK_INT > Build.VERSION_CODES.M
//                && info.getName().startsWith(EXYNOS_PREFIX);
//    }
//}
