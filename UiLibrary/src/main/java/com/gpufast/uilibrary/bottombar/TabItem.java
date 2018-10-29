package com.gpufast.uilibrary.bottombar;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class TabItem extends RelativeLayout {

    private Context mContext;
    private ImageView mIconView;
    private Drawable mNormalIcon;
    private Drawable mSelectedIcon;
    private LayerDrawable mCompundIcon;
    private int mIconSize;
    private int mTopMargin;
    private String mTitle;
    private Paint mTextPaint;
    private int mTextSize; //sp
    private Typeface mTypeFace;

    public TabItem(Context context) {
        super(context);
    }


    private void init(Context ctx) {
        mContext = ctx;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.weight = 3;
        setLayoutParams(lp);
        initPaint();
        initIconView();
    }

    private void initPaint() {
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(DensityUtils.sp2px(mContext, mTextSize));
        mTextPaint.setTypeface(mTypeFace);
    }

    private void initIconView() {
        mIconView = new ImageView(mContext);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mIconSize, mIconSize);
        lp.addRule(mTitle == null ? RelativeLayout.CENTER_IN_PARENT : RelativeLayout.CENTER_HORIZONTAL);
        if (mTitle != null) {
            lp.topMargin = mTopMargin;
        }
        mIconView.setScaleType(ImageView.ScaleType.FIT_XY);
        mIconView.setLayoutParams(lp);
        addView(mIconView);
        updateIcon();
    }

    private void updateIcon() {
        if (mSelectedIcon == null) {
            mIconView.setImageDrawable(mNormalIcon);
        } else {
            mCompundIcon = new LayerDrawable(new Drawable[]{mNormalIcon, mSelectedIcon});
            mNormalIcon.setAlpha(255);
            mSelectedIcon.setAlpha(0);
            mIconView.setImageDrawable(mCompundIcon);
        }
    }


}
