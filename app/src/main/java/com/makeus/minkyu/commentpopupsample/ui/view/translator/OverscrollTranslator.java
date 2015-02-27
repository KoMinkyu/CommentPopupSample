/**
 * Created by minkyu on 15. 2. 26..
 */
package com.makeus.minkyu.commentpopupsample.ui.view.translator;

import android.graphics.Canvas;
import android.view.ViewGroup;

public abstract interface OverscrollTranslator
{
  public abstract void cancel(float paramFloat, ViewGroup paramViewGroup);

  public abstract boolean doesCustomDrawing();

  public abstract void draw(Canvas paramCanvas);

  public abstract void setDisabled(boolean paramBoolean);

  public abstract void translateBottom(float paramFloat, ViewGroup paramViewGroup);

  public abstract void translateTop(float paramFloat, ViewGroup paramViewGroup);
}
