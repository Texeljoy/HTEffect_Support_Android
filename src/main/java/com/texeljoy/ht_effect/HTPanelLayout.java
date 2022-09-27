package com.texeljoy.ht_effect;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.texeljoy.ht_effect.fragment.HtARPropsFragment;
import com.texeljoy.ht_effect.fragment.HtBeautyFragment;
import com.texeljoy.ht_effect.fragment.HtGestureFrameFragment;
import com.texeljoy.ht_effect.fragment.HtModeFragment;
import com.texeljoy.ht_effect.fragment.HtPortraitFragment;
import com.texeljoy.ht_effect.fragment.HtThreeDFragment;
import com.texeljoy.ht_effect.model.HTEventAction;
import com.texeljoy.ht_effect.model.HTViewState;
import com.texeljoy.ht_effect.model.HtState;
import com.texeljoy.ht_effect.model.HtStyle;
import com.texeljoy.ht_effect.utils.DpUtils;
import com.texeljoy.ht_effect.utils.HtConfigTools;
import com.texeljoy.ht_effect.utils.HtUICacheUtils;
import com.texeljoy.ht_effect.utils.SharedPreferencesUtil;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 美颜面板主体
 * 使用美颜面板请务必调用init()方法
 */
@SuppressWarnings("unused")
public class HTPanelLayout extends ConstraintLayout
    implements ValueAnimator.AnimatorUpdateListener {

  private AppCompatImageView ivHtTrigger;

  private final ValueAnimator showAnim =
      ValueAnimator.ofFloat(DpUtils.dip2px(320), 0);
  private final ValueAnimator hideAnim =
      ValueAnimator.ofFloat(0, DpUtils.dip2px(320));
  private final ValueAnimator takePhotoAnim =
      ValueAnimator.ofFloat(DpUtils.dip2px(320), 0);

  //show filter tip timer
  private Timer showFilterTipTimer;

  private FragmentManager fm;

  public void setBtnShutterClick(BtnShutterClick btnShutterClick) {
    this.btnShutterClick = btnShutterClick;
  }

  private BtnShutterClick btnShutterClick;

  private View controllerView;
  private AppCompatImageView btnShutter;
  private AppCompatImageView shutterIv;
  private AppCompatTextView tvFilterType;
  private TextView tiInteractionHint;

  public HTPanelLayout(@NonNull Context context) {
    super(context);
  }

  public HTPanelLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public HTPanelLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  /**
   * 使用时务必调用
   *
   * @param fm 初始化必须
   * @return 当前view
   */
  public HTPanelLayout init(FragmentManager fm) {
    this.fm = fm;
    SharedPreferencesUtil.init(getContext());
    RxBus.get().register(this);
    HtConfigTools.getInstance().initHtConfigTools(getContext());
    checkVersion();
    initView();
    initData();
    return this;
  }

  {
    //初始化动画参数
    showAnim.setDuration(250);
    hideAnim.setDuration(250);
    showAnim.setInterpolator(new AccelerateInterpolator());
    hideAnim.setInterpolator(new LinearInterpolator());
    showAnim.addUpdateListener(HTPanelLayout.this);
    hideAnim.addUpdateListener(HTPanelLayout.this);

    takePhotoAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override public void onAnimationUpdate(ValueAnimator valueAnimator) {
        btnShutter.postOnAnimation(new Runnable() {
          @Override public void run() {
            btnShutter.setTranslationY((Float) valueAnimator.getAnimatedValue());
          }
        });
      }
    });

    takePhotoAnim.addListener(new Animator.AnimatorListener() {
      @Override public void onAnimationStart(Animator animator) {

      }

      //根据状态显示不同的拍照按钮
      @Override public void onAnimationEnd(Animator animator) {
        if (HtState.isDark && ((Float)
            (takePhotoAnim.getAnimatedValue())) == 0F) {
          //黑色主题 面板隐藏
          btnShutter.setImageDrawable(ContextCompat
              .getDrawable(getContext(), R.mipmap.ic_shutter_dark));
        } else if (HtState.isDark && ((Float)
            (takePhotoAnim.getAnimatedValue())) != 0F) {
          //黑色主题 面板开启
          btnShutter.setImageDrawable(ContextCompat
              .getDrawable(getContext(), R.drawable.ic_shutter_dark));
        } else if (!HtState.isDark && HtState.currentViewState == HTViewState.HIDE) {
          btnShutter.setImageDrawable(ContextCompat
              //白色主题 面板隐藏
              .getDrawable(getContext(), R.mipmap.icon_shutter_light));
        } else {
          //白色主题 面板显示
          btnShutter.setImageDrawable(ContextCompat
              .getDrawable(getContext(), R.mipmap.icon_shutter_light));
        }
      }

      @Override public void onAnimationCancel(Animator animator) {

      }

      @Override public void onAnimationRepeat(Animator animator) {

      }
    });

    takePhotoAnim.setDuration(300);
    //和面板的动画插值器不同来实现差值动画
    takePhotoAnim.setInterpolator(new LinearInterpolator());
  }

  /**
   * 初始化View
   */
  private void initView() {
    LayoutInflater.from(getContext()).inflate(R.layout.layout_ht_panel, this);
    ivHtTrigger = findViewById(R.id.iv_ht_trigger);
    controllerView = findViewById(R.id.controller_view);
    btnShutter = findViewById(R.id.btn_shutter);
    shutterIv = findViewById(R.id.shutter_iv);
    tvFilterType = findViewById(R.id.tv_filter_tip);
    tiInteractionHint = findViewById(R.id.interaction_hint);
    //初始化View的时候记得把美颜的参数进行应用
    HtUICacheUtils.initCache(true);
  }

  ///版本检测
  private void checkVersion() {
    String curVersion = "1.0";

    // String oldVersion = KvUtils.get().getString("mt_version");

    // if (oldVersion.equals("") || Float.parseFloat(curVersion) > Float.parseFloat(oldVersion)) {
    //
    // }
  }

  //view被销毁时
  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    RxBus.get().unregister(this);
  }

  //切换面板
  @Subscribe(thread = EventThread.MAIN_THREAD,
             tags = { @Tag(HTEventAction.ACTION_SHOW_FILTER) })
  public void showFilterTip(Object o) {
    if (showFilterTipTimer != null) {
      showFilterTipTimer.cancel();
    }
    tvFilterType.setText(HtState.currentFilter.getTitle());
    tvFilterType.setVisibility(VISIBLE);
    showFilterTipTimer = new Timer();
    showFilterTipTimer.schedule(new TimerTask() {
      @Override public void run() {
        tvFilterType.post(new Runnable() {
          @Override public void run() {
            tvFilterType.setVisibility(GONE);
          }
        });
      }
    }, 800);

  }

  /**
   * 初始化数据源
   */
  private void initData() {
    changeTheme(null);
    replaceView(new HtModeFragment(),"");

    btnShutter.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        //点击拍照
        if (btnShutterClick != null) {
          btnShutterClick.clickShutter();
        }
      }
    });

    shutterIv.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        //点击拍照
        if (btnShutterClick != null) {
          btnShutterClick.clickShutter();
        }
      }
    });

    ivHtTrigger.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View view) {
        showPanel(HTViewState.MODE);
      }
    });

    //空白处隐藏面板
    setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        v.performClick();

        Log.e("click","--||--");
        backContainer();

        return false;
      }
    });
  }

  /**
   * 返回上级View
   */
  private void backContainer() {
    Log.e("backFrom", HtState.currentViewState.name());
    switch (HtState.currentViewState) {
      case HIDE:
        return;
      //一级面板
      case MODE:
        hideContainer();
        break;
      //二级面板
      case BEAUTY:
      case BEAUTY_FILTER:
      case AR:
      case ThreeD:
      case GESTURE:
      case PORTRAIT:
        showPanel(HTViewState.MODE);
        break;
    }
  }

  /**
   * 隐藏面板
   */
  private void hideContainer() {
    //如果已经隐藏,则不进行动作
    if (HtState.currentViewState == HTViewState.HIDE) return;
    HtState.currentViewState = HTViewState.HIDE;

    //先停止按钮动画
    // if (takePhotoAnim.isRunning()) {
    //   takePhotoAnim.cancel();
    // }
    // takePhotoAnim.setFloatValues(btnShutter.getTranslationY(), DpUtils.dip2px(0));
    // takePhotoAnim.start();

    //动画完成后再显示美颜按钮,避免重叠
    hideAnim.addListener(new Animator.AnimatorListener() {
      @Override public void onAnimationStart(Animator animation) {

      }

      @Override public void onAnimationEnd(Animator animation) {
        ivHtTrigger.setVisibility(View.VISIBLE);
        //btnShutter.setVisibility(View.VISIBLE);
        //动画监听器用完记得回收,避免内存泄漏
        hideAnim.removeListener(this);
      }

      @Override public void onAnimationCancel(Animator animation) {

      }

      @Override public void onAnimationRepeat(Animator animation) {

      }
    });

    //打断当前的动画,设置新的参数地点,达成打断效果
    if (showAnim.isRunning()) {
      showAnim.cancel();
      hideAnim.setFloatValues(controllerView.getTranslationY(),
          DpUtils.dip2px(320));
      hideAnim.start();
      return;
    }

    hideAnim.setFloatValues(controllerView.getTranslationY(),
        DpUtils.dip2px(320));

    hideAnim.start();
  }

  /**
   * 切换主题
   */
  @Subscribe(thread = EventThread.MAIN_THREAD,
             tags = { @Tag(HTEventAction.ACTION_CHANGE_THEME) })
  public void changeTheme(Object o) {
    if (HtState.isDark) {
      btnShutter.setImageDrawable(ContextCompat
          .getDrawable(getContext(), R.drawable.ic_shutter_dark));
      ivHtTrigger.setImageDrawable(ContextCompat
          .getDrawable(getContext(), R.mipmap.ic_trigger_white));
    } else {
      btnShutter.setImageDrawable(ContextCompat
          .getDrawable(getContext(), R.mipmap.icon_shutter_light));
      ivHtTrigger.setImageDrawable(ContextCompat
          .getDrawable(getContext(), R.mipmap.ic_trigger_black));
    }
  }

  //切换面板
  @Subscribe(thread = EventThread.MAIN_THREAD,
             tags = { @Tag(HTEventAction.ACTION_CHANGE_PANEL) })
  public void showPanel(HTViewState viewState) {
    //根据切换的面板做对应的处理
    Log.e("change_Panel:", viewState.name());
    switch (viewState) {
      case HIDE:
        hideContainer();
        break;

      case MODE:
        ivHtTrigger.setVisibility(View.GONE);
        //btnShutter.setVisibility(View.GONE);
        if (HtState.currentViewState == HTViewState.HIDE) {
          showModePanel();
        } else {
          Log.e("change_Panel:", "Mode_Fragment");
          switchModePanel(new HtModeFragment(),"");
          //shutterIv.setVisibility(View.GONE);
          RxBus.get().post(HTEventAction.ACTION_STYLE_SELECTED,"");
        }
        HtState.currentViewState = viewState;
        break;

      case BEAUTY:
        ivHtTrigger.setVisibility(View.GONE);
        //shutterIv.setVisibility(View.VISIBLE);
        //btnShutter.setVisibility(View.GONE);
        switchModePanel(new HtBeautyFragment(),"beauty");
        HtState.currentViewState = viewState;
        HtState.currentSecondViewState = HTViewState.BEAUTY_SKIN;
        Log.e("--Beauty--",viewState.name());
        break;

      case AR:
        ivHtTrigger.setVisibility(GONE);
        //shutterIv.setVisibility(View.VISIBLE);
        //btnShutter.setVisibility(View.GONE);
        switchModePanel(new HtARPropsFragment(),"ar");
        HtState.currentViewState = viewState;
        HtState.currentAR = HTViewState.AR_PROP;
        Log.e("--AR--",viewState.name());
        break;

      case ThreeD:
        ivHtTrigger.setVisibility(View.GONE);
        //shutterIv.setVisibility(View.VISIBLE);
        //btnShutter.setVisibility(View.GONE);
        switchModePanel(new HtThreeDFragment(),"threed");
        HtState.currentViewState = viewState;
        Log.e("--Make Up--",viewState.name());
        break;

      case GESTURE:
        ivHtTrigger.setVisibility(View.GONE);
        //shutterIv.setVisibility(View.VISIBLE);
        //btnShutter.setVisibility(View.GONE);
        switchModePanel(new HtGestureFrameFragment(),"gesture");
        HtState.currentViewState = viewState;
        Log.e("--Gesture--",viewState.name());
        break;

      case PORTRAIT:
        ivHtTrigger.setVisibility(View.GONE);
        //shutterIv.setVisibility(View.VISIBLE);
        //btnShutter.setVisibility(View.GONE);
        switchModePanel(new HtPortraitFragment(),"portrait");
        HtState.currentViewState = viewState;
        Log.e("--Portrait--",viewState.name());
        break;

      case BEAUTY_FILTER:
        ivHtTrigger.setVisibility(View.GONE);
        //shutterIv.setVisibility(View.VISIBLE);
        //btnShutter.setVisibility(View.GONE);
        switchModePanel(new HtBeautyFragment(),"beauty_filter");
        HtState.currentViewState = HTViewState.BEAUTY;
        HtState.currentSecondViewState = HTViewState.BEAUTY_FILTER;
        Log.e("--Filter--",viewState.name());
        break;
    }

  }

  /**
   * 展示提示语句
   * @param hint
   */
  @Subscribe(thread = EventThread.MAIN_THREAD,
             tags = { @Tag(HTEventAction.ACTION_SHOW_GESTURE) })
  public void showHint(String hint) {
    tiInteractionHint.setVisibility(TextUtils.isEmpty(hint) ? View.GONE : View.VISIBLE);
    tiInteractionHint.setText(hint);
  }

  /**
   * 风格推荐被选中后的提示语句
   * @param o
   */
  @Subscribe(thread = EventThread.MAIN_THREAD,
             tags = { @Tag(HTEventAction.ACTION_STYLE_SELECTED) })
  public void showStyleHint(Object o) {

    if (showFilterTipTimer != null) {
      showFilterTipTimer.cancel();
    }
    tiInteractionHint.setText(HtState.currentStyle != HtStyle.YUAN_TU ? "请先取消“风格推荐”效果" : "");
    tiInteractionHint.setVisibility(VISIBLE);
    showFilterTipTimer = new Timer();
    showFilterTipTimer.schedule(new TimerTask() {
      @Override public void run() {
        tiInteractionHint.post(new Runnable() {
          @Override public void run() {
            tiInteractionHint.setVisibility(GONE);
          }
        });
      }
    }, 1500);


  }

  /**
   * 设置面板动画
   * @param dpValue
   */
  private void setTakePhotoAnim(float dpValue){
    if (takePhotoAnim.isRunning()) {
      takePhotoAnim.cancel();
    }
    takePhotoAnim.setFloatValues(btnShutter.getTranslationY(), DpUtils.dip2px(dpValue));
    takePhotoAnim.start();
  }

  /**
   * 显示功能面板
   */
  private void showModePanel() {
    if (hideAnim.isRunning()) {
      hideAnim.cancel();
    }
    if (showAnim.isRunning()) {
      showAnim.cancel();
    }
    showAnim.setFloatValues(controllerView.getTranslationY(), 0);

    showAnim.addListener(new Animator.AnimatorListener() {
      @Override public void onAnimationStart(Animator animator) {
        replaceView(new HtModeFragment(),"");
        //btnShutter.setVisibility(View.GONE);
        //shutterIv.setVisibility(View.GONE);
        showAnim.removeListener(this);
      }

      @Override public void onAnimationEnd(Animator animator) {

      }

      @Override public void onAnimationCancel(Animator animator) {

      }

      @Override public void onAnimationRepeat(Animator animator) {

      }
    });
    showAnim.start();
  }

  /**
   * 切换子View面板
   *
   * @param fragment 被切换的片段
   */
  private void switchModePanel(@Nullable Fragment fragment, String which) {

    if (hideAnim.isRunning()) {
      hideAnim.cancel();
    }

    if (showAnim.isRunning()) {
      showAnim.cancel();
    }

    hideAnim.setFloatValues(controllerView.getTranslationY(),
        DpUtils.dip2px(330));

    hideAnim.addListener(new Animator.AnimatorListener() {
      @Override public void onAnimationStart(Animator animator) {

        //此处禁用点击空白处隐藏面板，否则在第三层面板中快速点击空白区域，第一层和第二层的面板会重合
        setOnTouchListener(null);

      }

      @Override public void onAnimationEnd(Animator animator) {
        showAnim.setFloatValues(controllerView.getTranslationY(), 0F);
        if (fragment != null) {
          replaceView(fragment,which);
        }
        showAnim.start();
        hideAnim.removeListener(this);

        //此前点击空白处隐藏面板被禁用，此处需放开
        setOnTouchListener(new OnTouchListener() {
          @Override
          public boolean onTouch(View v, MotionEvent event) {
            v.performClick();

            Log.e("click","--||--");
            backContainer();

            return false;
          }
        });
      }

      @Override public void onAnimationCancel(Animator animator) {

      }

      @Override public void onAnimationRepeat(Animator animator) {


      }
    });

    hideAnim.start();

  }

  @Override public void onAnimationUpdate(ValueAnimator valueAnimator) {
    controllerView.postOnAnimation(new Runnable() {
      @Override public void run() {

        float offsetY = (float) valueAnimator.getAnimatedValue();

        controllerView.setTranslationY(offsetY);
      }
    });
  }

  /**
   * 切换子view
   *
   * @param fragment 被切换的碎片
   */
  private void replaceView(Fragment fragment, String which) {

    FragmentTransaction ft = fm.beginTransaction();
    Bundle bundle = new Bundle();
    //设置数据
    bundle.putString("switch", which);
    //绑定 Fragment
    fragment.setArguments(bundle);

    ft.replace(R.id.container_fragment, fragment);

    ft.commitAllowingStateLoss();
  }

  public interface BtnShutterClick {
    //点击拍照按钮的回调
    void clickShutter();
  }

}







