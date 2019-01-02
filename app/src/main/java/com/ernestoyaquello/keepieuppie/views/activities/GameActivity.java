package com.ernestoyaquello.keepieuppie.views.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ernestoyaquello.keepieuppie.R;
import com.ernestoyaquello.keepieuppie.storage.local.utils.DataStorageHelper;
import com.ernestoyaquello.keepieuppie.views.adapters.ShareScoreAdapter;
import com.ernestoyaquello.keepieuppie.views.base.BaseActivity;
import com.ernestoyaquello.keepieuppie.views.base.OnCustomDialogEventListener;
import com.ernestoyaquello.keepieuppie.views.customdialogs.ShareScoreCustomDialog;
import com.ernestoyaquello.keepieuppie.views.game.states.base.State;
import com.ernestoyaquello.keepieuppie.views.game.views.GameView;

import java.util.List;

public class GameActivity extends BaseActivity implements OnCustomDialogEventListener {

    public interface ScoreSharingDialogListener {
        void onScoreSharingDialogAppeared();
        void onScoreSharingDialogDisappeared();
    }

    private static final int SHARE_SCORE_REQUEST = 1;

    private GameView gameView;

    private LinearLayout shareScoreDialogLayout;
    private ScoreSharingDialogListener scoreSharingListener;
    private ShareScoreCustomDialog shareScoreCustomDialog;
    private ImageView shareScoreListLoadingImage;
    private RecyclerView shareScoreList;
    private ShareScoreAdapter shareScoreListAdapter;
    private int scoreToShare;

    public GameActivity() {
        super(R.layout.activity_game);

        this.useBackgroundMusicInThisActivity = false;
    }

    @Override
    protected void onActivityCreated(Bundle savedInstanceState) {
        gameView = findViewById(R.id.game_view);

        shareScoreListLoadingImage = findViewById(R.id.share_score_list_loading);
        shareScoreList = findViewById(R.id.share_score_list);
        shareScoreList.setLayoutManager(new LinearLayoutManager(this));
        shareScoreListAdapter = new ShareScoreAdapter(this);
        shareScoreList.setAdapter(shareScoreListAdapter);
        shareScoreDialogLayout = findViewById(R.id.share_score_dialog);
        shareScoreCustomDialog = new ShareScoreCustomDialog(this, shareScoreDialogLayout, this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Get rid of views we don't need them here
        fullScreenContent.removeView(clouds);
        fullScreenContent.removeView(findViewById(R.id.bg_decoration));
    }

    public void setCurrentState(State state) {
        if (gameView != null) {
            gameView.setCurrentState(state);
        }
    }

    public void shareScore(int score, ScoreSharingDialogListener sharedListener) {
        scoreToShare = score;
        scoreSharingListener = sharedListener;

        clearShareScoreList();
        shareScoreCustomDialog.showDialog();
        shareScoreDialogLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onShowDialogStarted() {
    }

    @Override
    public void onShowDialogFinished() {
        if (scoreSharingListener != null) {
            scoreSharingListener.onScoreSharingDialogAppeared();
        }

        loadDataIntoShareScoreList();
    }

    @Override
    public void onHideDialogStarted() {
        if (scoreSharingListener != null) {
            scoreSharingListener.onScoreSharingDialogDisappeared();
            scoreSharingListener = null;
        }
    }

    @Override
    public void onHideDialogFinished() {
        shareScoreDialogLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();

        gameView.pause();

        if (DataStorageHelper.isMusicActivated(this)) {
            gameView.stopBackgroundSound();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (DataStorageHelper.isMusicActivated(this)) {
            gameView.playBackgroundSound();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (shareScoreCustomDialog != null) {
            shareScoreCustomDialog.onDestroy();
        }
    }

    @Override
    public void doFinish() {
        super.doFinish();

        // This is delayed to stop the user from seeing an empty blue screen while going back to the menu
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                gameView.finishGame();
            }
        }, 150);
    }

    @Override
    protected void startCloudsAnimation() {
        // Do nothing here
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SHARE_SCORE_REQUEST) {
            shareScoreCustomDialog.hideDialog();
        }
    }

    private void clearShareScoreList() {
        // Show loading view with animation
        shareScoreListLoadingImage.clearAnimation();
        shareScoreListLoadingImage.setVisibility(View.VISIBLE);
        Animation loadingAnimation = AnimationUtils.loadAnimation(this, R.anim.fading);
        loadingAnimation.setRepeatMode(Animation.REVERSE);
        loadingAnimation.setRepeatCount(Animation.INFINITE);
        shareScoreListLoadingImage.startAnimation(loadingAnimation);

        // Remove all elements from the list and hide it
        shareScoreListAdapter.setDataSet(null);
        shareScoreList.setVisibility(View.GONE);
    }

    private void loadDataIntoShareScoreList() {
        final String appPackageName = getApplicationContext().getPackageName();
        final String gameUrl = "https://play.google.com/store/apps/details?id=" + appPackageName;
        String text = getString(R.string.share_text, scoreToShare, getString(R.string.hashtag), gameUrl);

        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);

        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            PackageManager pm = getApplicationContext().getPackageManager();
            List<ResolveInfo> activities = pm.queryIntentActivities(shareIntent, 0);

            shareScoreListLoadingImage.clearAnimation();
            shareScoreListLoadingImage.setVisibility(View.GONE);

            shareScoreList.setVisibility(View.VISIBLE);
            shareScoreListAdapter.setDataSet(activities);
            shareScoreListAdapter.setItemClickListener(new ShareScoreAdapter.ItemClickListener() {
                @Override
                public void onListItemClick(View view, int position) {
                    ResolveInfo item = shareScoreListAdapter.getItem(position);
                    ActivityInfo activity = item.activityInfo;
                    ComponentName component = new ComponentName(activity.packageName, activity.name);
                    shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    shareIntent.setComponent(component);

                    startActivityForResult(shareIntent, SHARE_SCORE_REQUEST);
                }
            });
        } else {
            shareScoreCustomDialog.hideDialog();
        }
    }
}
