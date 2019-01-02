package com.ernestoyaquello.keepieuppie.views.game.models;

import android.graphics.Bitmap;

import com.ernestoyaquello.keepieuppie.views.game.models.base.BaseButton;
import com.ernestoyaquello.keepieuppie.views.game.utils.Assets;

public class PauseButton extends BaseButton {
    private boolean isPaused;

    public PauseButton(Bitmap bitmap) {
        super(bitmap);

        isPaused = false;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    @Override
    protected Bitmap getButtonSelectedBitmap() {
        return isPaused ? Assets.getResumeButtonClicked() : Assets.getPauseButtonClicked();
    }

    @Override
    protected Bitmap getButtonUnselectedBitmap() {
        return isPaused ? Assets.getResumeButton() : Assets.getPauseButton();
    }
}