package com.ernestoyaquello.keepieuppie.views.game.models;

import android.graphics.Bitmap;

import com.ernestoyaquello.keepieuppie.views.game.framework.utils.Painter;
import com.ernestoyaquello.keepieuppie.views.game.models.base.BaseModel;
import com.ernestoyaquello.keepieuppie.views.game.utils.Assets;

public class Hand extends BaseModel {
    private boolean isTapping;

    public Hand(Bitmap bitmap) {
        super(bitmap);
    }

    @Override
    public void update(float delta, int gameWidth, int gameHeight) {
        // Do nothing here
    }

    @Override
    public Bitmap getBitmap() {
        return isTapping ? Assets.getTutorialHandTapping() : Assets.getTutorialHand();
    }

    @Override
    public void render(Painter painter, int gameWidth, int gameHeight) {
        painter.drawImage(getBitmap(), (int) posX, (int) posY);
    }

    public boolean isTapping() {
        return isTapping;
    }

    public void setTapping(boolean tapping) {
        isTapping = tapping;
    }
}
