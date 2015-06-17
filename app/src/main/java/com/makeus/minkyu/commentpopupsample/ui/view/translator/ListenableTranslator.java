package com.makeus.minkyu.commentpopupsample.ui.view.translator;

import android.graphics.Canvas;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.makeus.minkyu.commentpopupsample.ui.view.TrackingManager;

/**
 * Created by minkyu on 15. 2. 27..
 */
public class ListenableTranslator implements OverscrollTranslator{

  public interface OnCancelListener{
    void onCancel();
  }

  public interface OnTranslateListener{
    void onTranslateTopListener(float translationY);
    void onTranslateBottomListener(float translationY);
  }

  private OnCancelListener onCancelListener;
  private OnTranslateListener onTranslateListener;

  private boolean isDisabled = false;

  @Override
  public void cancel(float paramFloat, ViewGroup viewGroup) {
    if (isDisabled)
      return;
    if (onCancelListener != null)
      onCancelListener.onCancel();
  }

  @Override
  public boolean doesCustomDrawing() {
    return false;
  }

  @Override
  public void draw(Canvas paramCanvas) {}

  @Override
  public void setDisabled(boolean disabled) {
    this.isDisabled = disabled;
  }

  @Override
  public void translateBottom(float translationYFactor, ViewGroup viewGroup) {
    if (this.isDisabled) return;
    if (onTranslateListener != null)
      onTranslateListener.onTranslateBottomListener(translationYFactor);
  }

  @Override
  public void translateTop(float translationYFactor, ViewGroup paramViewGroup) {
    if (this.isDisabled) return;
    if (onTranslateListener != null)
      onTranslateListener.onTranslateTopListener(translationYFactor);
  }

  public void setOnCancelListener(OnCancelListener onCancelListener) {
    if (onCancelListener == null)
      throw new IllegalArgumentException("OnCancelListener cannot be initialized with null");

    this.onCancelListener = onCancelListener;
  }

  public void setOnTranslateListener(OnTranslateListener onTranslateListener) {
    if (onTranslateListener == null)
      throw new IllegalArgumentException("OnTranslateListener cannot be initialized with null");

    this.onTranslateListener = onTranslateListener;
  }
}
