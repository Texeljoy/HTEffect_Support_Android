package com.texeljoy.ht_effect.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import com.texeljoy.ht_effect.R;

/**
 * 滤镜枚举类
 */
public enum HtFunnyFilterEnum {
  // 无
  NONE(R.string.none, "", R.drawable.icon_funny_none_white,R.drawable.icon_funny_none_black),

  ET(R.string.funny_et, "et", R.drawable.icon_funny_alien_white,R.drawable.icon_funny_alien_black),
  BIG_NOSE(R.string.funny_big_nose, "bignose", R.drawable.icon_funny_bignose_white,R.drawable.icon_funny_bignose_black),
  BIG_MOUTH(R.string.funny_big_mouth, "bigmouth", R.drawable.icon_funny_bigmouth_white,R.drawable.icon_funny_bigmouth_black),
  SQUARE_FACE(R.string.funny_fangxinglian, "squareface", R.drawable.icon_funny_squareface_white,R.drawable.icon_funny_squareface_black),
  BIG_HEAD(R.string.funny_big_head, "bighead", R.drawable.icon_funny_bighead_white,R.drawable.icon_funny_bighead_black),
  PEAR_FACE(R.string.funny_dudu, "pearface", R.drawable.icon_funny_pearface_white,R.drawable.icon_funny_pearface_black),
  SMALL_EYE(R.string.funny_doudou, "smalleye", R.drawable.icon_funny_smalleye_white,R.drawable.icon_funny_smalleye_black),
  THIN_FACE(R.string.funny_snake, "thinface", R.drawable.icon_funny_thinface_white,R.drawable.icon_funny_thinface_black)
  ;
  //名字
  private final int stringRes;

  //对应的资源名
  private final String keyName;

  //图标地址
  private final int iconRes_white;

  private final int iconRes_black;



  HtFunnyFilterEnum(@StringRes int stringRes, String keyName, @DrawableRes int iconResWhite, @DrawableRes int iconResBlack) {
    this.stringRes = stringRes;
    this.keyName = keyName;
    this.iconRes_white = iconResWhite;
    this.iconRes_black = iconResBlack;
  }

  public String getName(Context context) {
    return context.getString(stringRes);
  }

  public String getKeyName() {
    return keyName;
  }

  public Drawable getIconResWhite(Context context) {
    return ContextCompat.getDrawable(context, iconRes_white);
  }
  public Drawable getIconResBlack(Context context) {
    return ContextCompat.getDrawable(context, iconRes_black);
  }

}
