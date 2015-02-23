package com.makeus.minkyu.commentpopupsample.ui.view.callback;

import android.support.v4.widget.ViewDragHelper;
import android.view.View;

/**
 * Created by minkyu on 2015. 2. 16..
 */
public class ViewDragHelperCallback extends ViewDragHelper.Callback {
  private View trackingView;

  public ViewDragHelperCallback(View trackingView){
    this.trackingView = trackingView;
  }

  @Override
  public boolean tryCaptureView(View view, int pointerId) {
    return view.equals(trackingView);
  }
}
