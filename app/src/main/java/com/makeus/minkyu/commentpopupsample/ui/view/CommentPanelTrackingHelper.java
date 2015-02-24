package com.makeus.minkyu.commentpopupsample.ui.view;

import android.view.View;

/**
 * Created by minkyu on 2015. 2. 16..
 */
public class CommentPanelTrackingHelper {

  private static CommentPanelTrackingHelper instance = new CommentPanelTrackingHelper();

  private TrackableScrollView mainView;
  private CommentPanel trackingView;
  private View fakeTrackingView;
  private View blurView;

  public boolean isInitialized = false;
  private boolean isBlurEnabled = false;

  private CommentPanelTrackingHelper() {}

  public static CommentPanelTrackingHelper getInstance() {
    return instance;
  }

  public void initialize(TrackableScrollView mainView, CommentPanel trackingView, View fakeTrackingView) {
    this.mainView = mainView;
    this.trackingView = trackingView;
    this.fakeTrackingView = fakeTrackingView;
    this.trackingView.setVisibility(View.GONE);
    this.isInitialized = true;
  }

  public void setBlurView(View blurView) {
    this.isBlurEnabled = true;
    this.blurView = blurView;
  }

  public TrackableScrollView getMainView() {
    return this.mainView;
  }

  public CommentPanel getTrackingView() {
    return this.trackingView;
  }

  public View getFakeTrackingView() {
    return this.fakeTrackingView;
  }

  public View getBlurView() {
    return this.blurView;
  }

  public boolean isTrackingViewAnimating() {
    if (this.trackingView == null)
      return false;

    return this.trackingView.isAnimating();
  }

  public void setBlurViewAlpha(float alpha) {
    if (!isBlurEnabled)
      return;

    this.blurView.setAlpha(alpha);
  }

  public void recycle() {
    this.mainView = null;
    this.trackingView = null;
    this.fakeTrackingView = null;
    if (isBlurEnabled)
      this.blurView = null;
    this.isBlurEnabled = false;
    this.isInitialized = false;
  }
}