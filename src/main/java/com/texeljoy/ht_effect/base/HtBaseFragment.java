package com.texeljoy.ht_effect.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.hwangjr.rxbus.RxBus;

/**
 *  简易封装的Fragment
 */
public abstract class HtBaseFragment extends Fragment {

  @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater,
                                               @Nullable ViewGroup container,
                                               @Nullable Bundle savedInstanceState) {
    RxBus.get().register(this);
    View view = inflater.inflate(getLayoutId(), container, false);
    initView(view, savedInstanceState);
    return view;
  }

  protected abstract int getLayoutId();

  protected abstract void initView(View view, Bundle savedInstanceState);


  @Override public void onDestroy() {
    super.onDestroy();
    RxBus.get().unregister(this);
  }
}
