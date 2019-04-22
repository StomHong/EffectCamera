package com.easyui.uilib.utils;

import android.content.Context;
import android.support.annotation.NonNull;
/**
 * @author Sivin 2018/10/26
 * Description:the utils of screen Dimensions
 */
public class DensityUtils {

    /**
     * dipValue to pxValue
     * @param context context
     * @param dipValue dipValue
     * @return the px value of dipValue
     */
    public static int dp2px(@NonNull Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * pxValue to dipValue
     * @param context context
     * @param pxValue pxValue
     * @return the dip value of px
     */
    public static int px2dp(@NonNull Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * spValue to pxValue
     * @param context context
     * @param spValue context
     * @return the px value of sp
     */
    public static int sp2px(@NonNull Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * pxValue to spValue
     * @param context context
     * @param pxValue pxValue
     * @return the sp value of px
     */
    public static int px2sp(@NonNull Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * dipValue to spValue
     * @param context context
     * @param dipValue dipValue
     * @return the sp value of px
     */
    public static int dp2sp(Context context, float dipValue) {
        float pxValue = dp2px(context, dipValue);
        return px2sp(context, pxValue);
    }

    /**
     * spValue to dpValue
     * @param context context
     * @param spValue spValue
     * @return the dp value of sp
     */
    public static int sp2dp(@NonNull Context context, float spValue) {
        float pxValue = sp2px(context, spValue);
        return px2dp(context, pxValue);
    }


}