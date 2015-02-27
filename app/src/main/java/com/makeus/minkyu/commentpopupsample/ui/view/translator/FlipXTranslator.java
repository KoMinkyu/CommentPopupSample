/**
 * Created by minkyu on 15. 2. 26..
 */
package com.makeus.minkyu.commentpopupsample.ui.view.translator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.ViewGroup;

public class FlipXTranslator implements OverscrollTranslator
{
  boolean hasSwitchedToHardwareLayer;
  private boolean isDisabled;

  public FlipXTranslator() {
    super();
    this.isDisabled = false;
    this.hasSwitchedToHardwareLayer = false;
  }

  @Override
  public void cancel(final float n, final ViewGroup viewGroup) {
    if (!this.isDisabled) {
      viewGroup.setTranslationY(0.0f);
      viewGroup.setRotationX(0.0f);
      if (this.hasSwitchedToHardwareLayer) {
        viewGroup.setLayerType(0, (Paint)null);
        this.hasSwitchedToHardwareLayer = false;
      }
    }
  }

  @Override
  public boolean doesCustomDrawing() {
    return false;
  }

  @Override
  public void draw(final Canvas canvas) {
  }

  @Override
  public void setDisabled(final boolean isDisabled) {
    this.isDisabled = isDisabled;
  }

  @Override
  public void translateBottom(final float n, final ViewGroup viewGroup) {
    if (this.isDisabled) {
      return;
    }
    if (viewGroup.getLayerType() != 2) {
      viewGroup.setLayerType(2, (Paint)null);
      this.hasSwitchedToHardwareLayer = true;
    }
    viewGroup.setTranslationY(n / 3.0f);
    viewGroup.setPivotX((float)(viewGroup.getWidth() / 2));
    viewGroup.setPivotY(0.1f * viewGroup.getHeight());
    viewGroup.setRotationX(n / 200.0f);
  }

  @Override
  public void translateTop(final float n, final ViewGroup viewGroup) {
    if (this.isDisabled) {
      return;
    }
    if (viewGroup.getLayerType() != 2) {
      viewGroup.setLayerType(2, (Paint)null);
      this.hasSwitchedToHardwareLayer = true;
    }
    viewGroup.setTranslationY(n / 2.0f);
    viewGroup.setPivotX((float)(viewGroup.getWidth() / 2));
    viewGroup.setPivotY(0.9f * viewGroup.getHeight());
    viewGroup.setRotationX(n / 200.0f);
  }
}
