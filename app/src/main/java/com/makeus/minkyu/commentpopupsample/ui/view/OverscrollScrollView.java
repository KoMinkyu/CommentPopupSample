/**
 * Created by minkyu on 15. 2. 26..
 */
package com.makeus.minkyu.commentpopupsample.ui.view;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.ScrollView;

import com.makeus.minkyu.commentpopupsample.ui.view.translator.OffsetTranslator;
import com.makeus.minkyu.commentpopupsample.ui.view.translator.OverscrollTranslator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OverscrollScrollView extends ScrollView implements Overscrollable {

  public static final float RUBBERBAND_INCREASE_STRENGT_FACTOR = 1.6f;
  public static final int STANDARD_BOUNCE_BOOUNDS = 250;
  public static final int STANDARD_OVERSCROLL_DRAG_BOUNDS = 70;
  private final Bouncer bouncer;
  private float density;
  private boolean isBeeingDragged;
  private float lastScrollY;
  private float maxBounceBounds;
  private float nextScrollSpeed;
  private ValueAnimator offsetAnimator;
  private float offsetY;
  private OnBottomPullListener onBottomPullListener;
  private List<OnPullBeginListener> onPullBeginListeners;
  private OnPullReleaseListener onPullReleaseListener;
  private OnTopPullListener onTopPullListener;
  private OverscrollTranslator overscrollTranslator;
  private float screenHeight;
  private ViewTreeObserver.OnScrollChangedListener scrollListener;
  private MODE scrollMode;
  private float speed;
  private float touchDownY;

  public OverscrollScrollView(final Context context) {
    super(context);
    this.scrollMode = MODE.NO_OFFSET;
    this.bouncer = new Bouncer();
    this.lastScrollY = 0.0f;
    this.speed = 0.0f;
    this.onPullBeginListeners = new ArrayList<OnPullBeginListener>();
    this.init();
  }

  public OverscrollScrollView(final Context context, final AttributeSet set) {
    super(context, set);
    this.scrollMode = MODE.NO_OFFSET;
    this.bouncer = new Bouncer();
    this.lastScrollY = 0.0f;
    this.speed = 0.0f;
    this.onPullBeginListeners = new ArrayList<OnPullBeginListener>();
    this.init();
  }

  public OverscrollScrollView(final Context context, final AttributeSet set, final int n) {
    super(context, set, n);
    this.scrollMode = MODE.NO_OFFSET;
    this.bouncer = new Bouncer();
    this.lastScrollY = 0.0f;
    this.speed = 0.0f;
    this.onPullBeginListeners = new ArrayList<OnPullBeginListener>();
    this.init();
  }

  private void calculateScrollSpeed() {
    if (this.getChildCount() != 0) {
      this.speed = this.nextScrollSpeed;
      final float abs = Math.abs((this.lastScrollY - this.getScrollY()) / this.density / FrameRateCounter.timeStep());
      if (this.lastScrollY != this.getScrollY()) {
        this.nextScrollSpeed = abs;
        this.nextScrollSpeed /= 200.0f;
      }
      this.lastScrollY = this.getScrollY();
    }
  }

  private void cancelOverscroll() {
    this.offsetY = 0.0f;
    this.overscrollTranslator.cancel(0.0f, this);
    this.cancelPull();
  }

  private void cancelPull() {
    this.bouncer.cancelAnim();
    if (this.onBottomPullListener != null) {
      this.onBottomPullListener.onPull(0.0f);
    }
    if (this.onTopPullListener != null) {
      this.onTopPullListener.onPull(0.0f);
    }
  }

  private boolean cancelScrollWhenDragging(final MotionEvent motionEvent) {
    if (Math.abs(this.offsetY) > 4.0f * this.density) {
      motionEvent.setAction(3);
      return true;
    }
    return false;
  }

  private float getFlingAmount() {
    return Math.min(35.0f, this.speed) / 90.0f * this.maxBounceBounds;
  }

  private float getOverscrollAmount() {
    this.offsetAnimator.setCurrentPlayTime((long)(10000.0f * Math.abs(this.offsetY / this.screenHeight)));
    float floatValue = (float)this.offsetAnimator.getAnimatedValue();
    if (this.offsetY < 0.0f) {
      floatValue = -floatValue;
    }
    return floatValue;
  }

  private void init() {
    this.initDimensions();
    this.setOverScrollMode(OVER_SCROLL_NEVER);
    this.setVerticalScrollBarEnabled(false);
    this.overscrollTranslator = new OffsetTranslator();
    this.bouncer.setOverscrollTranslator(this.overscrollTranslator);
  }

  private void initDimensions() {
    this.screenHeight = this.getResources().getDisplayMetrics().heightPixels;
    this.density = this.getResources().getDisplayMetrics().density;
    this.maxBounceBounds = 250.0f * this.density;
    this.setMaxOverscrollBounds(70.0f * this.density);
  }

  private void invokeBottomOverscroll() {
    this.overscrollTranslator.translateBottom(this.getOverscrollAmount(), this);
    if (this.onBottomPullListener != null) {
      this.onBottomPullListener.onPull(this.offsetY);
    }
  }

  private void invokeOnpullBeginListener() {
    final Iterator<OnPullBeginListener> iterator = this.onPullBeginListeners.iterator();
    while (iterator.hasNext()) {
      iterator.next().onPullBegin(this);
    }
  }

  private void invokePullRelease() {
    Log.v("TAG", "pull released");
    final float overscrollAmount = this.getOverscrollAmount();
    if (this.isScrolledToTop()) {
      this.bouncer.pullReleaseSnapback(overscrollAmount, this, true);
    } else {
      Log.v("TAG", "bottom pull released");
      Log.v("TAG", "overscrollAmount:"+overscrollAmount);
      this.bouncer.pullReleaseSnapback(-overscrollAmount, this, false);
    }
    if (this.onPullReleaseListener != null) {
      this.onPullReleaseListener.onPullRelease(overscrollAmount);
    }
  }

  private void invokeTopOverscroll() {
    final float overscrollAmount = this.getOverscrollAmount();
    this.overscrollTranslator.translateTop(overscrollAmount, (ViewGroup) this);
    if (this.onTopPullListener != null) {
      this.onTopPullListener.onPull(overscrollAmount);
    }
  }

  private void setScrollMode(final MODE scrollMode) {
    if (scrollMode != this.scrollMode && this.scrollMode == MODE.NO_OFFSET && !this.isBeeingDragged) {
      this.invokePullRelease();
    }
    this.scrollMode = scrollMode;
  }

  public void addOnPullBeginListener(final OnPullBeginListener onPullBeginListener) {
    this.onPullBeginListeners.add(onPullBeginListener);
  }

  @Override
  public boolean dispatchTouchEvent(final MotionEvent motionEvent) {
    final int action = MotionEventCompat.getActionMasked(motionEvent);

    if (action == MotionEvent.ACTION_DOWN) {
      this.isBeeingDragged = true;
      this.touchDownY = motionEvent.getRawY();
      this.cancelOverscroll();
    }
    else if (action == MotionEvent.ACTION_UP) {
      this.isBeeingDragged = false;
      this.setScrollMode(MODE.NO_OFFSET);
    }
    else if (action == MotionEvent.ACTION_MOVE) {
      this.offsetY = motionEvent.getRawY() - this.touchDownY;
    }
    return super.dispatchTouchEvent(motionEvent);
  }

  public OverscrollTranslator getOverscrollTranslator() {
    return this.overscrollTranslator;
  }

  public ViewTreeObserver.OnScrollChangedListener getScrollListener() {
    return this.scrollListener;
  }

  public boolean isBeeingDragged() {
    return this.isBeeingDragged;
  }

  public boolean isScrolledToBottom() {
    final int n = this.getChildAt(0).getHeight() - this.getHeight();
    final int abs = Math.abs(this.getScrollY());
    boolean b = false;
    if (n <= abs) {
      b = true;
    }
    return b;
  }

  public boolean isScrolledToTop() {
    return this.getChildCount() == 0 || this.getScrollY() == 0;
  }

  @Override
  protected void onDraw(final Canvas canvas) {
    super.onDraw(canvas);
    if (this.scrollMode != MODE.NO_OFFSET) {
      this.overscrollTranslator.draw(canvas);
    }
  }

  @Override
  protected void onOverScrolled(final int n, final int n2, final boolean b, final boolean b2) {
    super.onOverScrolled(n, n2, b, b2);
    if (!this.isBeeingDragged && this.isScrolledToTop()) {
      this.bouncer.startBounce(this.getFlingAmount(), this, true);
    }
    else if (!this.isBeeingDragged && this.isScrolledToBottom()) {
      this.bouncer.startBounce(this.getFlingAmount(), this, false);
    }
  }

  @Override
  protected void onScrollChanged(final int n, final int n2, final int n3, final int n4) {
    super.onScrollChanged(n, n2, n3, n4);
    if (this.scrollListener != null) {
      this.scrollListener.onScrollChanged();
    }
    this.calculateScrollSpeed();
  }

  @Override
  public boolean onTouchEvent(final MotionEvent motionEvent) {
    if (this.isScrolledToBottom() && this.offsetY < 0.0f) {
      if (this.scrollMode == MODE.NO_OFFSET) {
        this.touchDownY = motionEvent.getRawY();
      }
      else {
        this.invokeBottomOverscroll();
      }
      this.setScrollMode(MODE.TOP_OFFSET);
      if (!this.cancelScrollWhenDragging(motionEvent)) {
        return super.onTouchEvent(motionEvent);
      }
    }
    else {
      if (!this.isScrolledToTop() || this.offsetY <= 0.0f) {
        if (this.scrollMode != MODE.NO_OFFSET) {
          motionEvent.setAction(0);
          this.cancelOverscroll();
        }
        this.setScrollMode(MODE.NO_OFFSET);
        return super.onTouchEvent(motionEvent);
      }
      if (this.scrollMode == MODE.NO_OFFSET) {
        this.touchDownY = motionEvent.getRawY();
        this.invokeOnpullBeginListener();
      }
      else {
        this.invokeTopOverscroll();
      }
      this.setScrollMode(MODE.BOTTOM_OFFSET);
      if (!this.cancelScrollWhenDragging(motionEvent)) {
        return super.onTouchEvent(motionEvent);
      }
    }
    return true;
  }

  public void setMaxBounceBounds(final float maxBounceBounds) {
    this.maxBounceBounds = maxBounceBounds;
  }

  public void setMaxOverscrollBounds(final float n) {
    (this.offsetAnimator = ValueAnimator.ofFloat(new float[] { 0.0f, n }).setDuration(10000L)).setInterpolator((TimeInterpolator) new DecelerateInterpolator(1.6f));
  }

  public void setOnBottomPullListener(final OnBottomPullListener onBottomPullListener) {
    this.onBottomPullListener = onBottomPullListener;
    this.bouncer.setOnBottomPullListener(onBottomPullListener);
  }

  public void setOnPullReleaseListener(final OnPullReleaseListener onPullReleaseListener) {
    this.onPullReleaseListener = onPullReleaseListener;
  }

  public void setOnTopPullListener(final OnTopPullListener onTopPullListener) {
    this.onTopPullListener = onTopPullListener;
    this.bouncer.setOnTopPullListener(onTopPullListener);
  }

  public void setOverscrollTranslator(final OverscrollTranslator overscrollTranslator) {
    this.overscrollTranslator = overscrollTranslator;
    this.bouncer.setOverscrollTranslator(overscrollTranslator);
    this.setWillNotDraw(!overscrollTranslator.doesCustomDrawing());
  }

  public void setScrollListener(final ViewTreeObserver.OnScrollChangedListener scrollListener) {
    this.scrollListener = scrollListener;
  }

  static enum MODE
  {
    BOTTOM_OFFSET,
    NO_OFFSET,
    TOP_OFFSET;
  }

  public interface OnPullBeginListener
  {
    void onPullBegin(View p0);
  }
}
