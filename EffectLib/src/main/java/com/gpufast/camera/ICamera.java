package com.gpufast.camera;

/**
 * @author Sivin 2018/10/26
 * Description:
 */
public interface ICamera {

    /**
     * 打开前置摄像头
     * @return success return true otherwise return false
     */
    boolean openFrontCamera();

    /**
     * 打开后置摄像头
     * @return success return true otherwise return false
     */
    boolean openBackCamera();


    /**
     * 前后摄像头切换
     */
    void switchCamera();


    /**
     * 开始预览数据
     */
    void startPreview();

    /**
     * 停止预览数据
     */
    void stopPreview();

    /**
     * 停止摄像头
     */
    void stopCamera();
}
