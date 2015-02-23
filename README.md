# CommentPopupSample

## Usage

``` java
// Initialize TrackingHelper
@InjectView(R.id.main_body) TrackableScrollView trackableScrollView;
@InjectView(R.id.comment_panel) CommentPanel commentPanel;
@InjectView(R.id.fake_comment_panel) View fakeCommentPanel;
@InjectView(R.id.popup_blur) View viewBlur;

CommentPanelTrackingHelper.getInstance().initialize(trackableScrollView, commentPanel, fakeCommentPanel);
CommentPanelTrackingHelper.getInstance().setBlurView(blurView);
```
``` java
// Using OnOverScrollingListener
trackableScrollView.setOnOverScrollingListener(new TrackableScrollView.OnOverScrollingListener() {
    @Override
    public void onOverScrolling(int deltaY, int scrollY, int scrollRangeY, int overScrollRangeY, int overScrolledY, boolean isTouchEvent) {
        // implement whatever you want with over scrolling
    }
});
```

