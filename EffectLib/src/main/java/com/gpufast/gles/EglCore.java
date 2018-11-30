package com.gpufast.gles;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.util.Log;

import com.gpufast.utils.LogUtils;

public class EglCore {
    private static final String TAG = "EglCore";

    /**
     * 构造标志：surface必须是可以记录的。
     * 防止EGL使用了一个不能被有效的转换为编码器可用的格式。
     */
    public static final int FLAG_RECORDABLE = 0x01;

    /**
     * 构造标志： 尝试使用openGLES3 如果openGLes 3不可用，则使用openGLEs 2
     */
    public static final int FLAG_TRY_GLES3 = 0x02;

    /**
     * android 平台特有的扩展
     */
    private static final int EGL_RECORDABLE_ANDROID = 0x3142;

    private EGLDisplay mEGLDisplay = EGL14.EGL_NO_DISPLAY;
    private EGLContext mEGLContext = EGL14.EGL_NO_CONTEXT;
    private EGLConfig mEGLConfig = null;
    private int mGlVersion = -1;


    public EglCore() {
        this(null, 0);
    }

    /**
     * 准备EGL display 和context
     *
     * @param sharedContext 共享的上下文，如果不需要使用共享上下文，可以传入null
     * @param flags         配置参数 {@link #FLAG_RECORDABLE}
     */
    public EglCore(EGLContext sharedContext, int flags) {

        if (mEGLDisplay != EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("EGL already set up");
        }
        if (sharedContext == null) {
            sharedContext = EGL14.EGL_NO_CONTEXT;
        }
        mEGLDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        if (mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("unable to get EGL14 display");
        }
        int[] version = new int[2];

        if (!EGL14.eglInitialize(mEGLDisplay, version, 0, version, 1)) {
            mEGLDisplay = null;
            throw new RuntimeException("unable to initialize EGL14");
        }
        // Try to get a GLES3 context, if requested.
        if ((flags & FLAG_TRY_GLES3) != 0) {
            LogUtils.dTag(TAG, "Tring GLES 3");
            EGLConfig config = getConfig(flags, 3);
            if (config != null) {
                int[] attrib3_list = {
                        EGL14.EGL_CONTEXT_CLIENT_VERSION, 3,
                        EGL14.EGL_NONE
                };
                EGLContext context = EGL14.eglCreateContext(mEGLDisplay, config, sharedContext,
                        attrib3_list, 0);

                if (EGL14.eglGetError() == EGL14.EGL_SUCCESS) {
                    //Log.d(TAG, "Got GLES 3 config");
                    mEGLConfig = config;
                    mEGLContext = context;
                    mGlVersion = 3;
                }
            }
        }
        if (mEGLContext == EGL14.EGL_NO_CONTEXT) {
            LogUtils.dTag(TAG, "Tring GLES 2");
            EGLConfig config = getConfig(flags, 2);
            if (config == null) {
                throw new RuntimeException("Unable to find a suitable EGLConfig");
            }
            int[] attrib2_list = {
                    EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                    EGL14.EGL_NONE
            };
            EGLContext context = EGL14.eglCreateContext(mEGLDisplay, config, sharedContext,
                    attrib2_list, 0);
            checkEglError("eglCreateContext");
            mEGLConfig = config;
            mEGLContext = context;
            mGlVersion = 2;
        }

        // 确认查询
        int[] values = new int[1];
        EGL14.eglQueryContext(mEGLDisplay, mEGLContext, EGL14.EGL_CONTEXT_CLIENT_VERSION,
                values, 0);
        Log.d(TAG, "EGLContext created, client version " + values[0]);

    }


    private EGLConfig getConfig(int flags, int version) {
        int renderableType = EGL14.EGL_OPENGL_ES2_BIT;
        if (version >= 3) {
            renderableType |= EGLExt.EGL_OPENGL_ES3_BIT_KHR;
        }
        int[] attribList = {
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_ALPHA_SIZE, 8,
                //EGL14.EGL_DEPTH_SIZE, 16, //视频渲染一般不需要深度
                //EGL14.EGL_STENCIL_SIZE, 8,//视频不需要蒙版
                EGL14.EGL_RENDERABLE_TYPE, renderableType,
                EGL14.EGL_NONE, 0,      // placeholder for recordable [@-3]
                EGL14.EGL_NONE
        };
        if ((flags & FLAG_RECORDABLE) != 0) {
            attribList[attribList.length - 3] = EGL_RECORDABLE_ANDROID;
            attribList[attribList.length - 2] = 1;
        }
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfigs = new int[1];
        if (!EGL14.eglChooseConfig(mEGLDisplay, attribList, 0, configs, 0, configs.length,
                numConfigs, 0)) {
            LogUtils.w(TAG, "unable to find RGB8888 / " + version + " EGLConfig");
            return null;
        }
        return configs[0];
    }

    /**
     * Checks for EGL errors.  Throws an exception if an error has been raised.
     */
    private void checkEglError(String msg) {
        int error;
        if ((error = EGL14.eglGetError()) != EGL14.EGL_SUCCESS) {
            throw new RuntimeException(msg + ": EGL error: 0x" + Integer.toHexString(error));
        }
    }

}
