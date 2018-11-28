package com.gpufast.camera;

/**
 * @author Sivin 2018/10/26
 * Description:图像画面的宽高比
 */
public class AspectRatio {

    int m;
    int n;

    public AspectRatio(int m, int n) {
        this.m = m;
        this.n = n;
    }

    public float ofFloat(){
        return m * 1.0f / n;
    }

}
