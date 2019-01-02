package com.ernestoyaquello.keepieuppie.views.game.models;

import android.graphics.Bitmap;

import com.ernestoyaquello.keepieuppie.views.game.models.base.BaseButton;
import com.ernestoyaquello.keepieuppie.views.game.utils.Assets;

public class ShareButton extends BaseButton {

    public ShareButton(Bitmap bitmap) {
        super(bitmap);
    }

    @Override
    protected Bitmap getButtonSelectedBitmap() {
        return Assets.getShareButtonClicked();
    }

    @Override
    protected Bitmap getButtonUnselectedBitmap() {
        return Assets.getShareButton();
    }
}