package com.texeljoy.ht_effect.view;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;


/**
 * 贴纸等资源的布局控件
 */
public class HtSquareConstraintLayout extends ConstraintLayout {
    public HtSquareConstraintLayout(Context context) {
        super(context);
    }

    public HtSquareConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HtSquareConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
