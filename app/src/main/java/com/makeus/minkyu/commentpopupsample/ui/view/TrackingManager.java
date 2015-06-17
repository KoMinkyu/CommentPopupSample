package com.makeus.minkyu.commentpopupsample.ui.view;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.makeus.minkyu.commentpopupsample.ui.view.translator.ListenableTranslator;

/**
 * Created by minkyu on 2015. 2. 16..
 */
public class TrackingManager {

  static final float MAX_OVER_SCROLL_BOUNDS = 100.0f;
  static final float MAX_BOUNCE_BOUNDS = 380.0f;

  private static final String TAG = "TrackingManager";

  static final String LOG_INIT = "Initialize TrackingManager with tracking set";

  static final String ERROR_RE_INIT_TRACKING_SET = "Unknown error! Maybe trying to initialize TrackingManager again. " + "TrackingManager can't be re-initialized.";
  static final String ERROR_INIT_TRACKING_SET_NULL = "TrackingManager can not be initialized with null TrackingSet";
  static final String ERROR_BLUR_NOT_ENABLED = "Blur view is not enabled. " + "Set blur view in TrackingSet.";

  private Context context;
  private TrackingSet trackingSet;

  private ListenableTranslator listenableTranslator;

  public TrackingManager(Context context) {
    this.context = context;

    listenableTranslator = new ListenableTranslator();
    listenableTranslator.setOnTranslateListener(getTranslatorOnTranslateListener());
    listenableTranslator.setOnCancelListener(getTranslatorOnCancelListener());
  }

  private ListenableTranslator.OnTranslateListener getTranslatorOnTranslateListener() {
    return new ListenableTranslator.OnTranslateListener() {
      @Override public void onTranslateTopListener(float translationY) {}

      @Override
      public void onTranslateBottomListener(float translationY) {
        bounceTrackingView(translationY);
      }
    };
  }

  private ListenableTranslator.OnCancelListener getTranslatorOnCancelListener() {
    return new ListenableTranslator.OnCancelListener() {
      @Override
      public void onCancel() {
        final OverscrollScrollView scrollView = getScrollView();

        scrollView.setTranslationY(0.0f);
        scrollView.setScaleX(1.0f);
        scrollView.setScaleY(1.0f);
        setBlurViewAlpha(0.0f);
      }
    };
  }

  public void setTrackingSet(TrackingSet trackingSet) {
    if (trackingSet == null)
      throw new IllegalArgumentException(ERROR_INIT_TRACKING_SET_NULL);

    this.trackingSet = trackingSet;
    initializeScrollView();
    initializeCommentPanel();
    Log.d(TAG, LOG_INIT);
  }

  private void initializeCommentPanel() {
    final CommentPanel commentPanel = getTrackingView();
    commentPanel.setOnGetOffsetForState(getOnGetOffsetForState());
    commentPanel.setOnAnimationUpdateListener(getOnAnimationUpdateListener());
  }

  private void initializeScrollView() {
    final OverscrollScrollView scrollView = getScrollView();
    scrollView.setOverscrollTranslator(listenableTranslator);
    scrollView.setMaxOverscrollBounds(getRawMaxOverScrollBounds());
    scrollView.setMaxBounceBounds(getRawMaxBounceBounds());
    scrollView.setScrollListener(getOnScrollChangedListener());
    scrollView.setOnPullReleaseListener(getOnPullReleaseListener());
  }

  private CommentPanel.OnGetOffsetForState getOnGetOffsetForState() {
    return new CommentPanel.OnGetOffsetForState() {
      @Override
      public int getOffsetForState(CommentPanel.State state) {
        switch (state) {
          default:
          case Minimized:
            return getFakeTrackingView().getTop() - getScrollView().getScrollY();
          case Maximized:
            return 0;
        }
      }
    };
  }

  private CommentPanel.OnAnimationUpdateListener getOnAnimationUpdateListener() {
    return new CommentPanel.OnAnimationUpdateListener() {
      @Override
      public void onAnimationUpdate(float animatedTranslationY) {
        float fakeTrackingViewTranslationY = getFakeTrackingView().getTop() - getScrollView().getScrollY();
        float animatedRatio = animatedTranslationY / fakeTrackingViewTranslationY;
        if (animatedRatio < 0.0f || animatedRatio > 1.0f) return;

        setScrollViewScale(1.0f - animatedRatio);
        setBlurViewAlpha(1.0f - animatedRatio);
      }
    };
  }

  private ViewTreeObserver.OnScrollChangedListener getOnScrollChangedListener() {
    return new ViewTreeObserver.  OnScrollChangedListener() {
      @Override
      public void onScrollChanged() {
        translateTrackingView();
      }
    };
  }

  private Overscrollable.OnPullReleaseListener getOnPullReleaseListener() {
    return new Overscrollable.OnPullReleaseListener() {
      @Override
      public void onPullRelease(float scrollViewOffsetY) {
        if (getScrollView().isScrolledToBottom() && Math.abs(scrollViewOffsetY) >= (getRawMaxOverScrollBounds()) / 2) {
          getTrackingView().setState(CommentPanel.State.Maximized, true);
        }
      }
    };
  }

  private float getRawMaxOverScrollBounds() {
    return MAX_OVER_SCROLL_BOUNDS * getDensity();
  }

  private float getRawMaxBounceBounds() {
    return MAX_BOUNCE_BOUNDS * getDensity();
  }

  private float getDensity() {
    return context.getResources().getDisplayMetrics().density;
  }

  public void translateTrackingView() {
    if (getTrackingView().getVisibility() == View.GONE)
      getTrackingView().setVisibility(View.VISIBLE);

    float fakeViewTranslationY = getFakeTrackingView().getTop() - getScrollView().getScrollY();
    if(!getTrackingView().isAnimating())
      getTrackingView().setTranslationY(fakeViewTranslationY);
  }

  public void bounceTrackingView(float bounceFactor) {
    float fakeViewTranslationY = getFakeTrackingView().getTop() - getScrollView().getScrollY();
    getTrackingView().setTranslationY(fakeViewTranslationY + bounceFactor);

    float overScrollRatio = getOverScrollRatio(bounceFactor);
    setScrollViewScale(overScrollRatio);
    setBlurViewAlpha(overScrollRatio);
  }

  private float getOverScrollRatio(float bounceFactor) {
    final float density = context.getResources().getDisplayMetrics().density;
    if (bounceFactor < 0)
      bounceFactor = -bounceFactor;

    return bounceFactor / (MAX_OVER_SCROLL_BOUNDS * density);
  }

  private void setScrollViewScale(float overScrollRatio) {
    float scaleRatio = 1.0f - ((1.0f - 0.90f) * overScrollRatio);

    getScrollView().setScaleX(scaleRatio);
    getScrollView().setScaleY(scaleRatio);
  }

  public void setBlurViewAlpha(float overScrollRatio) {
    if (!trackingSet.isBlurViewEnabled())
      return;

    float alpha = 0.5f * overScrollRatio;
    getBlurView().setAlpha(alpha);
  }

  public OverscrollScrollView getScrollView() {
    return trackingSet.scrollView;
  }

  public CommentPanel getTrackingView() {
    return trackingSet.trackingView;
  }

  public View getFakeTrackingView() {
    return trackingSet.fakeTrackingView;
  }

  public View getBlurView() {
    if (trackingSet.isBlurViewEnabled())
      return trackingSet.blurView;
    else
      throw new IllegalStateException(ERROR_BLUR_NOT_ENABLED);
  }

  public boolean isTrackingViewAnimating() {
    return trackingSet.trackingView.isAnimating();
  }
}