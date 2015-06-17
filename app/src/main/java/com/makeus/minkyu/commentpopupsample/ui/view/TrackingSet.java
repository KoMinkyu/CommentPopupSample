package com.makeus.minkyu.commentpopupsample.ui.view;

import android.view.View;

/**
 * Created by minkyu on 15. 2. 27..
 */
public class TrackingSet {
  final OverscrollScrollView scrollView;
  final CommentPanel trackingView;
  final View fakeTrackingView;
  final View blurView;

  private boolean blurViewEnabled = false;

  public TrackingSet(final OverscrollScrollView scrollView, final CommentPanel trackingView,
                     final View fakeTrackingView) {
    this.scrollView = scrollView;
    this.trackingView = trackingView;
    this.fakeTrackingView = fakeTrackingView;
    this.blurView = null;
    blurViewEnabled = false;
  }

  public TrackingSet(final OverscrollScrollView scrollView, final CommentPanel trackingView,
                     final View fakeTrackingView, final View blurView) {
    this.scrollView = scrollView;
    this.trackingView = trackingView;
    this.trackingView.setVisibility(View.GONE);
    this.fakeTrackingView = fakeTrackingView;
    this.blurView = blurView;
    this.blurViewEnabled = true;
  }

  public boolean isBlurViewEnabled() {
    return blurViewEnabled;
  }
}
