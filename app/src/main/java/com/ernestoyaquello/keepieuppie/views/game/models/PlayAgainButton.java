package com.ernestoyaquello.keepieuppie.views.game.models;

import android.graphics.Bitmap;

import com.ernestoyaquello.keepieuppie.views.game.models.base.BaseButton;
import com.ernestoyaquello.keepieuppie.views.game.utils.Assets;

public class PlayAgainButton extends BaseButton {

    public PlayAgainButton(Bitmap bitmap) {
        super(bitmap);
    }

    @Override
    protected Bitmap getButtonSelectedBitmap() {
        return Assets.getPlayAgainButtonClicked();
    }

    @Override
    protected Bitmap getButtonUnselectedBitmap() {
        return Assets.getPlayAgainButton();
    }
}