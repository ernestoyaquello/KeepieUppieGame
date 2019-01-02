package com.ernestoyaquello.keepieuppie.views.game.models;

import android.graphics.Bitmap;

import com.ernestoyaquello.keepieuppie.views.game.models.base.BaseButton;
import com.ernestoyaquello.keepieuppie.views.game.utils.Assets;

public class MenuButtonSecondary extends BaseButton {

    public MenuButtonSecondary(Bitmap bitmap) {
        super(bitmap);
    }

    @Override
    protected Bitmap getButtonSelectedBitmap() {
        return Assets.getMenuButtonSecondaryClicked();
    }

    @Override
    protected Bitmap getButtonUnselectedBitmap() {
        return Assets.getMenuButtonSecondary();
    }
}