package com.makeus.minkyu.commentpopupsample.ui.view;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.ViewPropertyAnimatorUpdateListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * Created by minkyu on 2015. 2. 11..
 */
public class CommentPanel extends LinearLayout {

  public enum State {
    Maximized,
    Minimized
  }

  public interface OnStateChangedListener {
    void onStateChangedListener(State chagnedState);
  }

  public interface OnGetOffsetForState {
    int getOffsetForState(State state);
  }

  public interface OnAnimationUpdateListener {
    void onAnimationUpdate(float animatedTranslationY);
  }

  private Context context;

  private static final int ANIMATION_DURATION = 500;

  private OnStateChangedListener onStateChangedListener;
  private OnGetOffsetForState onGetOffsetForState;
  private OnAnimationUpdateListener onAnimationUpdateListener;

  private State currentState = State.Minimized;
  private int panelOffsetY;

  private OvershootInterpolator interpolator;

  private VelocityTracker velocityTracker;
  private GestureDetector gestureDetector;
  private State stateBeforeTracking;

  private boolean isAnimating = false;
  private boolean isTracking = false;
  private boolean isPreTracking = false;

  private int startY = -1;
  private float oldY;

  private int pagingTouchSlop;
  private int minFlingVelocity;
  private int maxFlingVelocity;

  public CommentPanel(Context context) {
    super(context);
    this.context = context;
    initialize();
  }

  public CommentPanel(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context = context;
    initialize();
  }

  public CommentPanel(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.context = context;
    initialize();
  }

  private void initialize() {
    ViewConfiguration viewConfiguration = ViewConfiguration.get(this.context);
    this.pagingTouchSlop = viewConfiguration.getScaledPagingTouchSlop();
    this.minFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
    this.maxFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();

    this.interpolator = new OvershootInterpolator(1.5f);
  }

  @Override public boolean onInterceptTouchEvent(MotionEvent e) {
    /**
     * Has to be implemented for comment panel that containing ListView.
     */
    return super.onInterceptTouchEvent(e);
  }

  @Override public boolean onTouchEvent(MotionEvent e) {
    final int action = MotionEventCompat.getActionMasked(e);

    if (isAnimating()) return true;

    if (gestureDetector == null) {
      DoubleTapListener doubleTapListener = getDoubleTapListener();
      gestureDetector = new GestureDetector(this.context, doubleTapListener);
    }

    gestureDetector.onTouchEvent(e);

    e.offsetLocation(0, this.getTranslationY());
    if (action == MotionEvent.ACTION_DOWN) {
      this.trackCapturedMovement(e);
      return true;
    }

    if (!isTracking && !trackCapturedMovement(e)) return true;

    if (action != MotionEvent.ACTION_MOVE)
      velocityTracker.addMovement(e);

    if (action == MotionEvent.ACTION_MOVE) {
      float eventPosY = e.getY();
      int traveledDistance = Math.round(eventPosY - startY);
      if (currentState == State.Minimized)
        traveledDistance = getOffsetForState(State.Minimized) + traveledDistance;

      setNewOffsetY(traveledDistance);
      oldY = eventPosY;
    } else if (action == MotionEvent.ACTION_UP) {
      velocityTracker.computeCurrentVelocity(1000, maxFlingVelocity);
      if (Math.abs(velocityTracker.getYVelocity()) > minFlingVelocity && Math.abs(velocityTracker.getYVelocity()) < maxFlingVelocity)
        setState(currentState == State.Maximized ? State.Minimized : State.Maximized, true);
      else if (currentState == State.Maximized && panelOffsetY > this.getHeight() / 2)
        setState(State.Minimized, true);
      else if (currentState == State.Minimized && panelOffsetY < this.getHeight() / 2)
        setState(State.Maximized, true);
      else
        setState(currentState, true);

      isPreTracking = isTracking = false;
      velocityTracker.clear();
      velocityTracker.recycle();
    }

    return true;
  }

  private boolean trackCapturedMovement(MotionEvent e) {
    final int action = MotionEventCompat.getActionMasked(e);
    if (e.getAction() == MotionEvent.ACTION_DOWN) {
      oldY = startY = (int) e.getY();

      velocityTracker = VelocityTracker.obtain();
      velocityTracker.addMovement(e);

      isPreTracking = true;
      stateBeforeTracking = currentState;

      return false;
    }

    if (action == MotionEvent.ACTION_UP) {
      isPreTracking = isTracking = false;
    }

    if (!isPreTracking) return false;

    velocityTracker.addMovement(e);

    if (action == MotionEvent.ACTION_MOVE) {
      double distance = Math.abs(e.getY() - startY);

      if (distance < pagingTouchSlop) return false;
    }

    oldY = startY = (int) e.getY();
    isTracking = true;

    return true;
  }

  private boolean isTrackingAvailable(MotionEvent e) {
    return stateBeforeTracking == State.Maximized ? e.getY() >= startY : e.getY() <= startY;
  }

  private int getOffsetForState(State state) {
    if (onGetOffsetForState != null) return onGetOffsetForState.getOffsetForState(state);

    View parentView = (View) getParent();
    switch(state) {
      default:
      case Minimized:
        return parentView.getBottom();
      case Maximized:
        return 0;
    }
  }

  private void setNewOffsetY(int newOffsetY) {
    if (onAnimationUpdateListener != null)
      onAnimationUpdateListener.onAnimationUpdate(newOffsetY);
    setTranslationY(newOffsetY);
  }

  public void setState(final State newState, boolean animate) {
    this.currentState = newState;
    this.isAnimating = true;

    this.animateTranslationY(getOffsetForState(newState), ANIMATION_DURATION, interpolator, new Runnable() {
      @Override
      public void run() {
        isAnimating = false;
        if (onStateChangedListener != null) {
          onStateChangedListener.onStateChangedListener(newState);
        }
      }
    });
  }

  private void animateTranslationY(int translation, int duration, Interpolator interpolator, Runnable callback) {
    ViewPropertyAnimatorCompat animator = ViewCompat.animate(this);
    animator.setDuration(duration).translationY(translation);
    animator.setUpdateListener(new ViewPropertyAnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(View view) {
        if (onAnimationUpdateListener == null) return;
        onAnimationUpdateListener.onAnimationUpdate(getTranslationY());
      }
    });

    if (callback != null)
      animator.withEndAction(callback);

    if (interpolator != null)
      animator.setInterpolator(interpolator);

    animator.start();
  }

  public boolean isMaximized() {
    return this.currentState == State.Maximized;
  }

  public boolean isMinimized() {
    return this.currentState == State.Minimized;
  }

  private DoubleTapListener getDoubleTapListener() {
    DoubleTapListener l = new DoubleTapListener(new Runnable() {
      @Override
      public void run() {
        /**
         *  Could be implemented for double tap event.
         */
      }
    });

    return l;
  }

  public boolean isAnimating() {
    return this.isAnimating;
  }

  public void setOnStateChangedListener(OnStateChangedListener l) {
    if (l == null) {
      throw new IllegalArgumentException("OnStateChangedListener cannot be initialized with null");
    }
    this.onStateChangedListener = l;
  }

  public void setOnGetOffsetForState(OnGetOffsetForState onGetOffsetForState) {
    if (onGetOffsetForState == null)
      throw new IllegalArgumentException("OnGetOffsetForState cannot be initialized with null");

    this.onGetOffsetForState = onGetOffsetForState;

  }

  public void setOnAnimationUpdateListener(OnAnimationUpdateListener onAnimationUpdateListener) {
    if (onAnimationUpdateListener == null)
      throw new IllegalArgumentException("OnAnimationUpdateListener cannot be initialized with null");

    this.onAnimationUpdateListener = onAnimationUpdateListener;
  }

  private class DoubleTapListener extends GestureDetector.SimpleOnGestureListener {
    Runnable callback;

    public DoubleTapListener(Runnable callback) {
      this.callback = callback;
    }

    @Override public boolean onDoubleTap(MotionEvent e) {
      callback.run();
      return true;
    }
  }
}
