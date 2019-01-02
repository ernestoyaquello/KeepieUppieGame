package com.ernestoyaquello.keepieuppie.views.base;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.ernestoyaquello.keepieuppie.R;

/**
 * This is not to create an actual dialog, but something that looks and behaves like one.
 */
public abstract class BaseCustomDialog {
    protected BaseActivity parentActivity;
    protected View dialogLayout;
    protected OnCustomDialogEventListener eventListener;

    protected BaseCustomDialog(BaseActivity parentActivity, View dialogLayout,
                            OnCustomDialogEventListener eventListener) {
        this.parentActivity = parentActivity;
        this.dialogLayout = dialogLayout;
        this.eventListener = eventListener;

        onCreate(dialogLayout);
    }

    public void showDialog(Animation showAnimation) {
        if (!parentActivity.isDialogVisible && !parentActivity.isDialogVisibilityChanging) {
            parentActivity.isDialogVisibilityChanging = true;

            final BaseCustomDialog instance = this;
            showAnimation.setZAdjustment(Animation.ZORDER_TOP);
            showAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    if (eventListener != null) {
                        eventListener.onShowDialogStarted();
                    }

                    dialogLayout.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (eventListener != null) {
                        eventListener.onShowDialogFinished();
                    }

                    parentActivity.lastOpenedDialog = instance;
                    parentActivity.isDialogVisible = true;
                    parentActivity.isDialogVisibilityChanging = false;
                    dialogLayout.clearAnimation();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            dialogLayout.bringToFront();
            dialogLayout.requestLayout();
            dialogLayout.startAnimation(showAnimation);
        }
    }

    public void showDialog() {
        Animation slideUp = AnimationUtils.loadAnimation(parentActivity.getApplicationContext(),
                R.anim.slide_in_up);

        showDialog(slideUp);
    }

    public void hideDialog(Animation hideAnimation) {
        if (parentActivity.isDialogVisible && !parentActivity.isDialogVisibilityChanging) {
            parentActivity.isDialogVisibilityChanging = true;

            hideAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    if (eventListener != null) {
                        eventListener.onHideDialogStarted();
                    }
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (eventListener != null) {
                        eventListener.onHideDialogFinished();
                    }

                    parentActivity.isDialogVisible = false;
                    parentActivity.isDialogVisibilityChanging = false;
                    dialogLayout.clearAnimation();
                    dialogLayout.setVisibility(View.GONE);
                    parentActivity.lastOpenedDialog = null;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            dialogLayout.startAnimation(hideAnimation);
        }
    }

    public void hideDialog() {
        Animation slideDown = AnimationUtils.loadAnimation(parentActivity.getApplicationContext(),
                R.anim.slide_out_down);

        hideDialog(slideDown);
    }

    public void onDestroy() {
        parentActivity = null; // Get rid of reference to activity
        dialogLayout = null;
        eventListener = null;
    }

    protected abstract void onCreate(View dialogLayout);
}
