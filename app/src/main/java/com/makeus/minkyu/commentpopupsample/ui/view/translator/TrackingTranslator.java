package com.makeus.minkyu.commentpopupsample.ui.view.translator;

import android.graphics.Canvas;
import android.view.ViewGroup;

import com.makeus.minkyu.commentpopupsample.ui.view.TrackingManager;

/**
 * Created by minkyu on 15. 2. 27..
 */
public class TrackingTranslator implements OverscrollTranslator{

  private boolean isDisabled = false;
  private TrackingManager trackingManager;

  @Override
  public void cancel(float paramFloat, ViewGroup viewGroup) {
    if (isDisabled)
      return;
    viewGroup.setTranslationY(0.0f);
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
    if (this.isDisabled || trackingManager == null) return;
    trackingManager.getTrackingView().setTranslationY(translationYFactor);
  }

  @Override
  public void translateTop(float translationYFactor, ViewGroup paramViewGroup) {
    if (this.isDisabled || trackingManager == null) return;
  }

  public void setTrackingManager(TrackingManager trackingManager) {
    this.trackingManager = trackingManager;
  }
}
