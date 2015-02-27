/**
 * Created by minkyu on 15. 2. 26..
 */
package com.makeus.minkyu.commentpopupsample.ui.view.translator;

import android.graphics.Canvas;
import android.view.ViewGroup;

public class OffsetTranslator implements OverscrollTranslator {
  private boolean isDisabled = false;

  public void cancel(float paramFloat, ViewGroup paramViewGroup)
  {
    if (this.isDisabled)
      return;
    paramViewGroup.setTranslationY(0.0F);
  }

  public boolean doesCustomDrawing()
  {
    return false;
  }

  public void draw(Canvas paramCanvas)
  {
  }

  public void setDisabled(boolean paramBoolean)
  {
    this.isDisabled = paramBoolean;
  }

  public void translateBottom(float paramFloat, ViewGroup paramViewGroup)
  {
    if (this.isDisabled)
      return;
    paramViewGroup.setTranslationY(paramFloat);
  }

  public void translateTop(float paramFloat, ViewGroup paramViewGroup)
  {
    if (this.isDisabled)
      return;
    paramViewGroup.setTranslationY(paramFloat);
  }
}