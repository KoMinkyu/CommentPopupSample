package com.makeus.minkyu.commentpopupsample;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;

import com.makeus.minkyu.commentpopupsample.ui.view.TrackableScrollView;
import com.makeus.minkyu.commentpopupsample.ui.view.CommentPanel;
import com.makeus.minkyu.commentpopupsample.ui.view.CommentPanelTrackingHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends ActionBarActivity {

  boolean panelInitialized = false;

  @InjectView(R.id.main_body)
  TrackableScrollView mainBody;
  @InjectView(R.id.comment_panel)
  CommentPanel commentPanel;
  @InjectView(R.id.fake_comment_panel) View fakeCommentPanel;
  @InjectView(R.id.popup_blur) View viewBlur;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.inject(this);

    CommentPanelTrackingHelper commentPanelTrackingHelper = CommentPanelTrackingHelper.getInstance();
    commentPanelTrackingHelper.initialize(mainBody, commentPanel, fakeCommentPanel);
    commentPanelTrackingHelper.setBlurView(viewBlur);

    mainBody.setOnOverScrollingListener(new TrackableScrollView.OnOverScrollingListener() {
      @Override
      public void onOverScrolling(int deltaY, int scrollY, int scrollRangeY, int overScrollRangeY, int overScrolledY, boolean isTouchEvent) {
//        if (!isTouchEvent && !commentPanel.isMaximized())
//          commentPanel.setState(CommentPanel.State.Maximized, true);
      }
    });
  }

  @Override public void onWindowFocusChanged(boolean hasFocus) {

  }
}
