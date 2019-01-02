package com.ernestoyaquello.keepieuppie.views.activities;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatImageButton;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.ernestoyaquello.keepieuppie.R;
import com.ernestoyaquello.keepieuppie.views.base.BaseActivity;
import com.ernestoyaquello.keepieuppie.views.base.OnCustomDialogEventListener;
import com.ernestoyaquello.keepieuppie.views.customdialogs.AboutCustomDialog;
import com.ernestoyaquello.keepieuppie.views.customdialogs.ChangeBallCustomDialog;
import com.ernestoyaquello.keepieuppie.storage.local.utils.DataStorageHelper;

public class OptionsActivity extends BaseActivity implements View.OnClickListener,
        OnCustomDialogEventListener {
    private AppCompatImageButton soundButton;
    private AppCompatImageButton changeBallButton;
    private View optionsLayout;
    private AboutCustomDialog aboutCustomDialog;
    private ChangeBallCustomDialog changeBallCustomDialog;

    private boolean showUnlockedBallNotification;

    public OptionsActivity() {
        super(R.layout.activity_options);
    }

    @Override
    protected void onActivityCreated(Bundle savedInstanceState) {
        // Find views
        soundButton = findViewById(R.id.sound_button);
        changeBallButton = findViewById(R.id.change_ball_button);
        optionsLayout = findViewById(R.id.options);
        AppCompatImageButton aboutButton = findViewById(R.id.about_button);
        AppCompatImageButton backButton = findViewById(R.id.options_back_button);
        LinearLayout logoImageContainer = findViewById(R.id.logo_container);
        LinearLayout aboutDialogLayout = findViewById(R.id.about_dialog);
        LinearLayout changeBallDialogLayout = findViewById(R.id.change_ball_dialog);

        // Set listeners
        soundButton.setOnClickListener(this);
        changeBallButton.setOnClickListener(this);
        aboutButton.setOnClickListener(this);
        backButton.setOnClickListener(this);

        // Create dialogs
        aboutCustomDialog = new AboutCustomDialog(this, aboutDialogLayout, this);
        changeBallCustomDialog = new ChangeBallCustomDialog(this, changeBallDialogLayout, this);

        // Start logo animation
        Animation logoAnimation = AnimationUtils.loadAnimation(this, R.anim.logo_repeat);
        logoImageContainer.startAnimation(logoAnimation);

        // Set the music button image
        boolean isMusicActive = DataStorageHelper.isMusicActivated(this);
        if (!isMusicActive) {
            soundButton.setImageDrawable(ContextCompat.getDrawable(this,
                    R.drawable.button_sound_off_selector));
        } else {
            soundButton.setImageDrawable(ContextCompat.getDrawable(this,
                    R.drawable.button_sound_on_selector));
        }

        showUnlockedBallNotification = getIntent().getBooleanExtra("newBall", false);
        if (showUnlockedBallNotification) {
            // Use blinking animation on change ball button to suggest the user to click it
            changeBallButton.setImageResource(R.drawable.button_change_ball_selector_anim);
            AnimationDrawable blinkingAnimation = (AnimationDrawable) changeBallButton.getDrawable();
            blinkingAnimation.start();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sound_button:
                onSoundButtonClicked();
                break;
            case R.id.change_ball_button:
                onChangeBallButtonClicked();
                break;
            case R.id.about_button:
                onAboutButtonClicked();
                break;
            case R.id.options_back_button:
                onBackButtonClicked();
                break;
        }
    }

    @Override
    public void onShowDialogStarted() {
        Animation slideOut = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_out_up);

        optionsLayout.startAnimation(slideOut);
    }

    @Override
    public void onShowDialogFinished() {
        optionsLayout.clearAnimation();
        optionsLayout.setVisibility(View.GONE);
    }

    @Override
    public void onHideDialogStarted() {
        Animation slideIn = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_in_down);

        optionsLayout.setVisibility(View.VISIBLE);
        optionsLayout.startAnimation(slideIn);
    }

    @Override
    public void onHideDialogFinished() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (aboutCustomDialog != null) {
            aboutCustomDialog.onDestroy();
        }

        if (changeBallCustomDialog != null) {
            changeBallCustomDialog.onDestroy();
        }
    }

    private void changeMusicState() {
        boolean isMusicActive = DataStorageHelper.isMusicActivated(this);

        if (isMusicActive) {
            releaseMediaPlayer();
            soundButton.setImageDrawable(ContextCompat.getDrawable(this,
                    R.drawable.button_sound_off_selector));
        } else {
            resumeMusicIfNecessary(true);
            soundButton.setImageDrawable(ContextCompat.getDrawable(this,
                    R.drawable.button_sound_on_selector));
        }

        DataStorageHelper.setMusicState(this, !isMusicActive);
    }

    private void onSoundButtonClicked() {
        changeMusicState();
    }

    private void onChangeBallButtonClicked() {
        if (showUnlockedBallNotification) {
            // Remove animation from change ball button to get everything back to normal
            changeBallButton.setImageResource(R.drawable.button_change_ball_selector);

            getIntent().putExtra("newBall", false);
            showUnlockedBallNotification = false;
        }

        changeBallCustomDialog.showDialog();
    }

    private void onAboutButtonClicked() {
        aboutCustomDialog.showDialog();
    }

    private void onBackButtonClicked() {
        finish();
    }
}
