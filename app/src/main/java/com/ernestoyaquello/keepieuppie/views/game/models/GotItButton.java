package com.ernestoyaquello.keepieuppie.views.game.models;

import android.graphics.Bitmap;

import com.ernestoyaquello.keepieuppie.views.game.models.base.BaseButton;
import com.ernestoyaquello.keepieuppie.views.game.utils.Assets;

public class GotItButton extends BaseButton {

    public GotItButton(Bitmap bitmap) {
        super(bitmap);
    }

    @Override
    protected Bitmap getButtonSelectedBitmap() {
        return Assets.getGotItButtonClicked();
    }

    @Override
    protected Bitmap getButtonUnselectedBitmap() {
        return Assets.getGotItButton();
    }
}