package com.texeljoy.ht_effect.model;

/**
 * 缓存的key值
 */
public enum HtUICacheKey {

  BEAUTY_SKIN_SELECT_POSITION(0,""), //选中了哪个美肤
  BEAUTY_FACE_TRIM_SELECT_POSITION(0,""), //选中了哪个美型
  BEAUTY_MAKE_UP_SELECT_POSITION(0,""), //选中了哪个美妆
  BEAUTY_MAKE_UP_STYLE_SELECT_POSITION(0,""), //选中了哪个妆容推荐
  BEAUTY_MAKE_UP_STYLE_SELECT_NAME(0,""), //选中了哪个妆容推荐
  BEAUTY_BODY_SELECT_POSITION(0,""), //选中了哪个美体
  BEAUTY_STYLE_SELECT_POSITION(0,""), //选中了哪个风格
  FILTER_SELECT_POSITION(3,""),//选中了哪个滤镜
  FILTER_SELECT_NAME(0,"ziran3"),//选中了哪个滤镜
  EFFECT_FILTER_SELECT_POSITION,//选中了哪个趣味滤镜
  EFFECT_FILTER_SELECT_NAME,//选中了哪个趣味滤镜
  FUNNY_FILTER_SELECT_POSITION(0, ""),//选中了哪个趣味滤镜
  FUNNY_FILTER_SELECT_NAME,//选中了哪个趣味滤镜
  HAIR_SELECT_POSITION,//选中了哪个美发
  HAIR_SELECT_NAME,//选中了哪个美发
  MAKEUP_SELECT_POSITION,//选中了哪个妆容推荐
  LIPSTICK_SELECT_POSITION(0,""),//选中了哪个口红
  EYEBROW_SELECT_POSITION(0,""),//选中了哪个眉毛
  BLUSH_SELECT_POSITION(0,""),//选中了哪个腮红
  EYESHADOW_SELECT_POSITION,//选中了哪个眼影
  EYELINE_SELECT_POSITION,//选中了哪个眼线
  EYELASH_SELECT_POSITION,//选中了哪个睫毛
  PUPILS_SELECT_POSITION,//选中了哪个美瞳
  LIPSTICK_SELECT_COLOR_POSITION,//选中了哪个口红颜色位置
  EYEBROW_SELECT_COLOR_POSITION,//选中了哪个眉毛颜色位置
  BLUSH_SELECT_COLOR_POSITION,//选中了哪个腮红颜色位置

  LIPSTICK_SELECT_TYPE(0,"-1"),//选中了哪个口红
  EYEBROW_SELECT_TYPE(0,"-1"),//选中了哪个眉毛
  BLUSH_SELECT_TYPE(0,"-1"),//选中了哪个腮红
  EYESHADOW_SELECT_NAME,//选中了哪个眼影
  EYELINE_SELECT_NAME,//选中了哪个眼线
  EYELASH_SELECT_NAME,//选中了哪个睫毛
  PUPILS_SELECT_NAME,//选中了哪个美瞳
  GREENSCREEN_EDIT_POSITION,
  GREENSCREEN_EDIT,

  LIPSTICK_COLOR_NAME(0,"rouhefen"), //选中了哪个口红的颜色名称
  EYEBROW_COLOR_NAME(0,"roufenzong"), //选中了哪个眉毛的颜色名称
  BLUSH_COLOR_NAME(0,"richang"), //选中了哪个腮红的颜色名称
  ;

  int defaultInt;
  String defaultStr;

  public int getDefaultInt() {
    return defaultInt;
  }

  public String getDefaultStr() {
    return defaultStr;
  }

  HtUICacheKey(int defaultInt, String defaultStr) {
    this.defaultInt = defaultInt;
    this.defaultStr = defaultStr;
  }

  HtUICacheKey() {
    defaultStr = "";
    defaultInt = 0;
  }

}
