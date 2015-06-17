package com.makeus.minkyu.commentpopupsample;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import com.makeus.minkyu.commentpopupsample.ui.view.CommentPanel;
import com.makeus.minkyu.commentpopupsample.ui.view.OverscrollScrollView;
import com.makeus.minkyu.commentpopupsample.ui.view.TrackingManager;
import com.makeus.minkyu.commentpopupsample.ui.view.TrackingSet;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends ActionBarActivity {

  @InjectView(R.id.main_body) OverscrollScrollView mainBody;
  @InjectView(R.id.fake_comment_panel) View fakeView;
  @InjectView(R.id.comment_panel) CommentPanel commentPanel;
  @InjectView(R.id.popup_blur) View blurView;
  @InjectView(R.id.comment_content) ScrollView commentContent;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.test_overscroll);
    ButterKnife.inject(this);

    initTrackingManager();
  }

  private void initTrackingManager() {
    TrackingSet trackingSet = new TrackingSet(this.mainBody, this.commentPanel, this.fakeView, this.blurView);
    TrackingManager trackingManager = new TrackingManager(getApplicationContext());
    trackingManager.setTrackingSet(trackingSet);

    commentContent.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        Log.v("TAG", "scrollY:" + commentContent.getScrollY());
        Log.v("TAG", "CommentConetnt Height:" + commentContent.getHeight());
        return false;
      }
    });
  }
}