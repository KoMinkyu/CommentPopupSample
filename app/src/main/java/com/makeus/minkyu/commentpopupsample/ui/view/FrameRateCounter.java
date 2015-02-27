/**
 * Created by minkyu on 15. 2. 26..
 */
package com.makeus.minkyu.commentpopupsample.ui.view;

import android.os.SystemClock;
import android.util.Log;

public class FrameRateCounter {
  private static long mLastTime;

  public static float timeStep() {
    long l1 = SystemClock.uptimeMillis();
    long l2 = l1 - mLastTime;
    boolean bool = (float)mLastTime < 0.0F;
    float f = 0.0F;
    if (bool)
      f = (float)l2 / 1000.0F;
    mLastTime = l1;
    Log.v("TAG", "uptimeMillis:" + l1);
    Log.v("TAG", "delay:" + l2);
    Log.v("TAG", "f?:" + f);
//    return Math.min(0.021F, f);
    return 0.021F;
  }
}

