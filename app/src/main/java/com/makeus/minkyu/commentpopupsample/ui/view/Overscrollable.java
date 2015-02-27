/**
 * Created by minkyu on 15. 2. 26..
 */
package com.makeus.minkyu.commentpopupsample.ui.view;

public abstract interface Overscrollable
{
  public abstract void setMaxBounceBounds(float paramFloat);

  public abstract void setMaxOverscrollBounds(float paramFloat);

  public abstract void setOnBottomPullListener(OnBottomPullListener paramOnBottomPullListener);

  public abstract void setOnPullReleaseListener(OnPullReleaseListener paramOnPullReleaseListener);

  public abstract void setOnTopPullListener(OnTopPullListener paramOnTopPullListener);

  public static abstract interface OnBottomPullListener
  {
    public abstract void onPull(float paramFloat);
  }

  public static abstract interface OnPullReleaseListener
  {
    public abstract void onPullRelease(float paramFloat);
  }

  public static abstract interface OnTopPullListener
  {
    public abstract void onPull(float paramFloat);
  }
}