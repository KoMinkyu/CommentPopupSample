package com.makeus.minkyu.commentpopupsample.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by minkyu on 2015. 2. 11..
 */

public class TrackableScrollView extends ScrollView {

  public interface OnOverScrollingListener {
    /**
     * Occur when ScrollView is over scrolling.
     *
     * @param deltaY scroll strength factor.
     * @param scrollY current scroll y factor.
     * @param scrollRangeY maximum scroll range without over scroll.
     * @param overScrolledY current over scrolled y factor.
     * @param overScrollRangeY maximum scroll range with over scroll.
     * @param isTouchEvent
     */
    void onOverScrolling(int deltaY, int scrollY, int scrollRangeY, int overScrolledY, int overScrollRangeY, boolean isTouchEvent);
  }

  private final static int OVERSCROLL_SCALE = 300;
  private final static float MINIMUM_SCALE = 0.9f;

  private Context context;

  private OnOverScrollingListener onOverScrollingListener;

  private int mMaxYOverscrollDistance;

  private boolean isEndOfScroll = false;
  private boolean isPivotInitialized = false;

  private CommentPanelTrackingHelper commentPanelTrackingHelper;

  public TrackableScrollView(final Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context = context;

    this.setOverScrollScale();
    commentPanelTrackingHelper = CommentPanelTrackingHelper.getInstance();
  }

  private void setOverScrollScale() {
    final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    final float density = displayMetrics.density;
    mMaxYOverscrollDistance = (int) (density * OVERSCROLL_SCALE);
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (isEndOfScroll()) return;

    if (!isPivotInitialized) initializeAnimationPivot();
  }

  private void initializeAnimationPivot() {
    setPivotX(getWidth() / 2);
    isPivotInitialized = true;
  }

  @Override protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    super.onScrollChanged(l, t, oldl, oldt);

    View view = getChildAt(getChildCount() - 1);
    int diff = (view.getBottom() - (getHeight() + getScrollY()));

    this.isEndOfScroll = (diff <= 0);
    trackTrackingView();
  }

  private void trackTrackingView() {
    if (!commentPanelTrackingHelper.isInitialized)
      return;

    if (!commentPanelTrackingHelper.isTrackingViewAnimating() &&
        !commentPanelTrackingHelper.getTrackingView().isMaximized()) {
      float overScrolledRatio = getOverScrolledRatio();
      translateTrackingView(overScrolledRatio);
      translateScrollTargetView(overScrolledRatio);
    }
  }

  private float getOverScrolledRatio() {
    final View scrollTargetView = getChildAt(getChildCount() - 1);

    int overScrolledFactor = -(scrollTargetView.getBottom() - (getHeight() + getScrollY()));
    float overScrolledRatio = (float)overScrolledFactor/(float)mMaxYOverscrollDistance;

    return overScrolledRatio;
  }

  private void translateTrackingView(float overScrolledRatio) {
    final View trackingView = commentPanelTrackingHelper.getTrackingView();
    final View fakeTrackingView = commentPanelTrackingHelper.getFakeTrackingView();
    final View scrollTargetView = getChildAt(getChildCount() - 1);

    if (trackingView == null || fakeTrackingView == null || scrollTargetView == null) return;

    if (trackingView.getVisibility() == GONE)
      trackingView.setVisibility(VISIBLE);

    float fakeViewTranslationY = fakeTrackingView.getTop() - getScrollY();
    float translationY = fakeViewTranslationY - (fakeViewTranslationY * overScrolledRatio * 0.7f);

    trackingView.setTranslationY(isEndOfScroll() ? translationY : fakeTrackingView.getTop() - getScrollY());
  }

  private void translateScrollTargetView(float overScrolledRatio) {
    final View scrollTargetView = getChildAt(getChildCount() - 1);

    if (isEndOfScroll())
      scrollTargetView.setTranslationY(mMaxYOverscrollDistance * overScrolledRatio);
  }

  @Override protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY,
                                           int scrollRangeX, int scrollRangeY, int maxOverScrollX,
                                           int maxOverScrollY, boolean isTouchEvent) {

    final boolean notOversrollable = isNotOverscrollable(scrollY);

    if (notOversrollable) {
      commentPanelTrackingHelper.setBlurViewAlpha(0.0f);
      setScale(1.0f);
      getChildAt(getChildCount() - 1).setTranslationY(0.0f);
    }

    if (isEndOfScroll()) {
      final int overScrolledRangeY = scrollY - scrollRangeY;
      scaleWithOverScroll(overScrolledRangeY);

      if (this.onOverScrollingListener != null)
        this.onOverScrollingListener.onOverScrolling(deltaY, scrollY, scrollRangeY, overScrolledRangeY,
                                                     scrollRangeY + mMaxYOverscrollDistance, isTouchEvent);
    }

    maxOverScrollY = notOversrollable ? maxOverScrollY : mMaxYOverscrollDistance;

    return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY,
      maxOverScrollX, maxOverScrollY, isTouchEvent);
  }

  private void scaleWithOverScroll(int overScrolledRangeY) {
    float overScrolledRatio = (float)overScrolledRangeY / (float)mMaxYOverscrollDistance;
    float scaleRatio = 1.0f - ((1.0f - MINIMUM_SCALE) * overScrolledRatio);

    setAlphaBlurViewWithOverScroll(overScrolledRatio);
    setScale(scaleRatio);
  }

  private void setAlphaBlurViewWithOverScroll(float overScrolledRatio) {
    float alphaRatio = 0.5f * overScrolledRatio;
    commentPanelTrackingHelper.setBlurViewAlpha(alphaRatio);
  }

  private void setScale(float scaleRatio) {
    setScaleX(scaleRatio);
    setScaleY(scaleRatio);
  }

  private boolean isNotOverscrollable(int scrollY) {
    return scrollY <= 0 || commentPanelTrackingHelper.isTrackingViewAnimating();
  }

  @Override public boolean onTouchEvent(MotionEvent e) {
    if (commentPanelTrackingHelper.isTrackingViewAnimating())
      return false;

    return super.onTouchEvent(e);
  }

  @Override public boolean onInterceptTouchEvent(MotionEvent e) {
    if (commentPanelTrackingHelper.isTrackingViewAnimating())
      return false;

    return super.onInterceptTouchEvent(e);
  }

  public void setOnOverScrollingListener(OnOverScrollingListener l) {
    this.onOverScrollingListener = l;
  }

  public boolean isEndOfScroll() {
    return this.isEndOfScroll;
  }

}
