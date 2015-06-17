/**
 * Created by minkyu on 15. 2. 26..
 */
package com.makeus.minkyu.commentpopupsample.ui.view;

import android.animation.ValueAnimator;
import android.util.Log;
import android.view.ViewGroup;

import com.makeus.minkyu.commentpopupsample.ui.view.translator.OverscrollTranslator;

public class Bouncer
{
  private static final float FRICTION = 5000.0f;
  public static final int RUBBER_BAND_STRENGTH = 8;
  public static final int VELOCITY_EXTRA = 15;
  private ValueAnimator anim;
  private float density;
  private boolean isTopOffset;
  private float offset;
  private Overscrollable.OnBottomPullListener onBottomPullListener;
  private OnSnapBackFinishedListener onSnapBackFinishedListener;
  private Overscrollable.OnTopPullListener onTopPullListener;
  private OverscrollTranslator overscrollTranslator;
  private float scrollVelocity;

  public Bouncer() {
    super();
    this.scrollVelocity = 0.0f;
  }

  private void applyRubberband(final float n) {
    this.offset -= n * (8.0f * this.offset);
  }

  private void checkforSnapbackFinished() {
    if ((int)Math.floor(this.scrollVelocity) == 0 && (int)Math.floor(this.offset) == 0) {
      this.cancelAnim();
    }
  }

  private ValueAnimator.AnimatorUpdateListener getListener(final ViewGroup viewGroup, final float n) {
    return new ValueAnimator.AnimatorUpdateListener() {
      public void onAnimationUpdate(final ValueAnimator valueAnimator) {
        final float timeStep = FrameRateCounter.timeStep();
        Bouncer.this.offset += timeStep * Bouncer.this.scrollVelocity;
        Bouncer.this.updateScrollVelocity(timeStep);
        Bouncer.this.applyRubberband(timeStep);
        Bouncer.this.offsetCallback(viewGroup);
        Bouncer.this.checkforSnapbackFinished();
      }
    };
  }

  private void offsetCallback(final ViewGroup viewGroup) {
    if (this.isTopOffset) {
      this.overscrollTranslator.translateTop(this.offset, viewGroup);
      if (this.onTopPullListener != null)
        this.onTopPullListener.onPull(this.offset);
    } else {
      this.overscrollTranslator.translateBottom(-this.offset, viewGroup);
      if (this.onBottomPullListener != null)
        this.onBottomPullListener.onPull(this.offset);
    }
  }

  private void resetValues() {
    this.offset = 0.0f;
    FrameRateCounter.timeStep();
  }

  private void startBounceAnim(final ViewGroup viewGroup) {
    this.anim = ValueAnimator.ofFloat(new float[] { 0.0f, 10000.0f }).setDuration(7000L);
    this.anim.addUpdateListener(this.getListener(viewGroup, this.density));
    this.anim.start();
  }

  private void updateScrollVelocity(final float n) {
    if (this.scrollVelocity > 0.0f) {
      this.scrollVelocity -= n * (5000.0f * this.density);
      return;
    }
    this.scrollVelocity = 0.0f;
  }

  public void cancelAnim() {
    if (this.anim != null) {
      this.anim.removeAllUpdateListeners();
      this.anim.cancel();
      if (this.onSnapBackFinishedListener != null) {
        this.onSnapBackFinishedListener.onSnapBackFinished();
      }
    }
  }

  public boolean isBounceInProgess() {
    return this.anim != null && this.anim.isRunning();
  }

  public void pullReleaseSnapback(final float offset, final ViewGroup viewGroup, final boolean isTopOffset) {
    this.isTopOffset = isTopOffset;
    this.density = viewGroup.getContext().getResources().getDisplayMetrics().density;
    this.scrollVelocity = 0.0f;
    this.resetValues();
    this.offset = offset;
    this.startBounceAnim(viewGroup);
  }

  public void setOnBottomPullListener(final Overscrollable.OnBottomPullListener onBottomPullListener) {
    this.onBottomPullListener = onBottomPullListener;
  }

  public void setOnSnapBackFinishedListener(final OnSnapBackFinishedListener onSnapBackFinishedListener) {
    this.onSnapBackFinishedListener = onSnapBackFinishedListener;
  }

  public void setOnTopPullListener(final Overscrollable.OnTopPullListener onTopPullListener) {
    this.onTopPullListener = onTopPullListener;
  }

  public void setOverscrollTranslator(final OverscrollTranslator overscrollTranslator) {
    this.overscrollTranslator = overscrollTranslator;
  }

  public void startBounce(final float n, final ViewGroup viewGroup, final boolean isTopOffset) {
    this.isTopOffset = isTopOffset;
    this.density = viewGroup.getContext().getResources().getDisplayMetrics().density;
    this.scrollVelocity = 15.0f * n;
    this.resetValues();
    this.startBounceAnim(viewGroup);
  }

  public interface OnSnapBackFinishedListener
  {
    void onSnapBackFinished();
  }
}