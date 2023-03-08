package com.yun.orderPad.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.yun.orderPad.R;

import java.lang.ref.WeakReference;


public abstract class BaseDialog extends DialogFragment {
  private static final float DEFAULT_DIM = 0.5f;

  private WeakReference<View> weakContentView;

  protected View contentView;

  protected SimpleHandler handler;

  private boolean isShowing = false;

  protected abstract int getLayoutRes();

  protected abstract void bindView(View contentView);

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(getHandler().getStyle(), getHandler().getDialogTheme());
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    getDialog().setCanceledOnTouchOutside(getHandler().isOutCancelable());
    getDialog().setOnKeyListener((dialog, keyCode, event) -> {
      if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
        getHandler().onKeyback();
        return !getHandler().isCancelable();
      }
      return false;
    });

    contentView = inflater.inflate(getLayoutRes(), container, false);
    weakContentView = new WeakReference<>(contentView);
    getDialog().setOnDismissListener(dialog -> getHandler().onDialogDismiss());
    bindView(contentView);
    return contentView;
  }


  @Override
  public void onStart() {
    super.onStart();
    Window window = getDialog().getWindow();
    if (window == null) {
      return;
    }
    WindowManager.LayoutParams params = window.getAttributes();
    params.dimAmount = getHandler().getDimAmount();
    params.width = getHandler().getWidth();
    params.height = getHandler().getHeight();
    params.gravity = getHandler().getGravity();

    window.setAttributes(params);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getHandler().onDialogCreated();
  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    if (isShowing) {
      isShowing = false;
      super.onDismiss(dialog);
      getHandler().onDialogDismiss();
    }
  }

  public View findViewById(int id) {
    if (weakContentView == null) {
      return null;
    }
    final View view = weakContentView.get();
    if (view == null) {
      return null;
    }
    return view.findViewById(id);
  }

  protected SimpleHandler getHandler() {
    if (handler == null) {
      handler = new SimpleHandler();
    }
    return handler;
  }

  public static class SimpleHandler {

    protected int getStyle() {
      return DialogFragment.STYLE_NO_TITLE;
    }

    protected int getDialogTheme() {
      return R.style.TvDialog;
    }

    protected float getDimAmount() {
      return DEFAULT_DIM;
    }

    protected int getWidth() {
      return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    protected int getHeight() {
      return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    protected int getGravity() {
      return Gravity.CENTER;
    }

    protected boolean isCancelable() {
      return true;
    }

    protected boolean isOutCancelable() {
      return true;
    }

    protected void onKeyback() {

    }

    protected void onDialogCreated() {

    }

    protected void onDialogDismiss() {

    }
  }

  //防止重复添加fragment崩溃
  @Override
  public void show(FragmentManager manager, String tag) {
    if (!isShowing) {
      isShowing = true;
      Fragment fragment = manager.findFragmentByTag(tag);
      if (fragment != null) {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.remove(fragment);
        transaction.commitAllowingStateLoss();
      }
      super.show(manager, tag);
    }
  }

  public boolean isShowing() {
    return (getDialog() != null && getDialog().isShowing()) || isShowing;
  }
}
