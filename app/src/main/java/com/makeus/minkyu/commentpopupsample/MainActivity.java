package com.makeus.minkyu.commentpopupsample;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;

import com.makeus.minkyu.commentpopupsample.ui.view.OverscrollScrollView;
import com.makeus.minkyu.commentpopupsample.ui.view.Overscrollable;
import com.makeus.minkyu.commentpopupsample.ui.view.TrackingManager;
import com.makeus.minkyu.commentpopupsample.ui.view.translator.TrackingTranslator;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends ActionBarActivity {

//  @InjectView(R.id.main_body) TrackableScrollView mainBody;
//  @InjectView(R.id.comment_panel) CommentPanel commentPanel;
//  @InjectView(R.id.fake_comment_panel) View fakeCommentPanel;
//  @InjectView(R.id.popup_blur) View viewBlur;
  @InjectView(R.id.main_body) OverscrollScrollView mainBody;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.test_overscroll);
    ButterKnife.inject(this);

    initScroll();
//    CommentPanelTrackingManager commentPanelTrackingManager = CommentPanelTrackingManager.getInstance();
//    commentPanelTrackingManager.initialize(mainBody, commentPanel, fakeCommentPanel);
//    commentPanelTrackingManager.setBlurView(viewBlur);

//    mainBody.setOnOverScrollingListener(new TrackableScrollView.OnOverScrollingListener() {
//      @Override
//      public void onOverScrolling(int deltaY, int scrollY, int scrollRangeY, int overScrollRangeY, int overScrolledY, boolean isTouchEvent) {
////        if (!isTouchEvent && !commentPanel.isMaximized())
////          commentPanel.setState(CommentPanel.State.Maximized, true);
//      }
//    });
  }

  private void initScroll() {
    TrackingTranslator trackingTranslator = new TrackingTranslator();
    trackingTranslator.setTrackingManager();
    this.mainBody.setOverscrollTranslator(new TrackingTranslator());
    this.mainBody.setMaxOverscrollBounds(70.0F * getResources().getDisplayMetrics().density);
    this.mainBody.setMaxBounceBounds(380.0F * getResources().getDisplayMetrics().density);
    this.mainBody.setOnBottomPullListener(new Overscrollable.OnBottomPullListener() {
      @Override
      public void onPull(final float n) {
        if (mainBody.isBeeingDragged())
          Log.v("TAG", "hi:" + n);
      }
    });
    this.mainBody.setOnPullReleaseListener(new Overscrollable.OnPullReleaseListener() {
      @Override
      public void onPullRelease(final float n) {

      }
    });
//    float f = getResources().getDimension(2131296302);
//    this.mainBody.setOnPullReleaseListener(getOnPullReleaseListener());
//    this.mainBody.addOnPullBeginListener(getOnScrollBeginListener());
//    this.mainBody.setOnTopPullListener(getTopOverscrollListener());
  }

  @Override public void onWindowFocusChanged(boolean hasFocus) {

  }
}