package com.ernestoyaquello.keepieuppie.views.activities;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatImageButton;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ernestoyaquello.keepieuppie.R;
import com.ernestoyaquello.keepieuppie.views.base.BaseActivity;
import com.ernestoyaquello.keepieuppie.views.base.OnCustomDialogEventListener;
import com.ernestoyaquello.keepieuppie.views.customdialogs.ExitConfirmationCustomDialog;
import com.ernestoyaquello.keepieuppie.storage.local.utils.DataStorageHelper;

public class MainMenuActivity extends BaseActivity implements View.OnClickListener,
        OnCustomDialogEventListener {
    private static final int PLAY_GAME_REQUEST = 0;
    private static final int OPTIONS_REQUEST = 1;

    private LinearLayout logoContainer;
    private AppCompatImageButton playButton;
    private AppCompatImageButton optionsButton;
    private AppCompatImageButton exitButton;
    private LinearLayout playButtonContainer;
    private LinearLayout optionsButtonContainer;
    private LinearLayout exitButtonContainer;
    private LinearLayout scoreLabelImageViewContainer;
    private TextView scoreTextView;
    private ExitConfirmationCustomDialog exitConfirmationCustomDialog;

    private boolean showUnlockedBallNotification;

    public MainMenuActivity() {
        super(R.layout.activity_main_menu);
    }

    @Override
    protected void onActivityCreated(Bundle savedInstanceState) {
        showUnlockedBallNotification = false;

        // Find views
        logoContainer = findViewById(R.id.logo_container);
        playButton = findViewById(R.id.play_button);
        optionsButton = findViewById(R.id.options_button);
        exitButton = findViewById(R.id.exit_button);
        playButtonContainer = findViewById(R.id.play_button_container);
        optionsButtonContainer = findViewById(R.id.options_button_container);
        exitButtonContainer = findViewById(R.id.exit_button_container);
        scoreLabelImageViewContainer = findViewById(R.id.score_label_container);
        scoreTextView = findViewById(R.id.score);
        LinearLayout exitConfirmationDialogLayout = findViewById(R.id.exit_confirmation_dialog);

        // Set listeners
        playButton.setOnClickListener(this);
        optionsButton.setOnClickListener(this);
        exitButton.setOnClickListener(this);

        // Create dialogs
        exitConfirmationCustomDialog = new ExitConfirmationCustomDialog(this,
                exitConfirmationDialogLayout, this);

        startAnimations();
    }

    private void startAnimations() {
        // Start animations
        Animation logoAnimation = AnimationUtils.loadAnimation(this, R.anim.logo_first_time);
        logoAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(),
                        android.R.anim.fade_in);
                fadeIn.setFillAfter(true);
                fadeIn.setStartOffset(1000);
                playButtonContainer.startAnimation(fadeIn);
                optionsButtonContainer.startAnimation(fadeIn);
                scoreLabelImageViewContainer.startAnimation(fadeIn);
                scoreTextView.startAnimation(fadeIn);
                exitButtonContainer.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        logoContainer.startAnimation(logoAnimation);
    }

    public void finishApplication() {
        releaseMediaPlayer();
        doFinish();
    }

    @Override
    public void finish() {
        if (!isDialogVisible) {
            exitConfirmationCustomDialog.showDialog();
        } else {
            hideLastOpenedDialog();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play_button:
                onPlayButtonClicked();
                break;
            case R.id.options_button:
                onOptionsButtonClicked();
                break;
            case R.id.exit_button:
                onExitButtonClicked();
                break;
        }
    }

    @Override
    public void onShowDialogStarted() {
        playButton.setEnabled(false);
        optionsButton.setEnabled(false);
        exitButton.setEnabled(false);
    }

    @Override
    public void onShowDialogFinished() {

    }

    @Override
    public void onHideDialogStarted() {

    }

    @Override
    public void onHideDialogFinished() {
        playButton.setEnabled(true);
        optionsButton.setEnabled(true);
        exitButton.setEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        int userScore = DataStorageHelper.getUserScore(this);
        scoreTextView.setText(String.valueOf(userScore));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (exitConfirmationCustomDialog != null) {
            exitConfirmationCustomDialog.onDestroy();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PLAY_GAME_REQUEST:
                onGameActivityResult(data);
                break;
            case OPTIONS_REQUEST:
                onOptionsActivityResult();
                break;
        }
    }

    private void onExitButtonClicked() {
        exitConfirmationCustomDialog.showDialog();
    }

    private void onOptionsButtonClicked() {
        Intent optionsIntent = new Intent(this, OptionsActivity.class);

        if (showUnlockedBallNotification) {
            // Remove animation from options button to get everything back to normal
            optionsButton.setImageResource(R.drawable.button_options_selector);
            optionsIntent.putExtra("newBall", true);

            showUnlockedBallNotification = false;
        }

        startActivityForResult(optionsIntent, OPTIONS_REQUEST);
    }

    private void onPlayButtonClicked() {
        pauseMusic();
        startActivityForResult(new Intent(this, GameActivity.class), PLAY_GAME_REQUEST);
    }

    private void onGameActivityResult(Intent data) {
        if (data != null && data.getExtras() != null) {
            Bundle parameters = data.getExtras();
            showUnlockedBallNotification = parameters.getBoolean("newBall", false);
            if (showUnlockedBallNotification) {
                // Use blinking animation on options button to suggest the user to click it
                optionsButton.setImageResource(R.drawable.button_options_selector_anim);
                AnimationDrawable blinkAnimation = (AnimationDrawable) optionsButton.getDrawable();
                blinkAnimation.start();
            }
        }
    }

    private void onOptionsActivityResult() {
        // No data to return here
    }
}
