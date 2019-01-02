package com.ernestoyaquello.keepieuppie.views.game.models.base;

import android.graphics.Bitmap;

import com.ernestoyaquello.keepieuppie.views.game.framework.utils.Painter;

public abstract class BaseButton extends BaseModel {
    protected boolean clicked;

    protected BaseButton(Bitmap bitmap) {
        super(bitmap);

        clicked = false;
    }

    protected BaseButton(Bitmap bitmap, boolean clicked) {
        super(bitmap);

        this.clicked = clicked;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public void render(Painter painter, int gameWidth, int gameHeight) {
        painter.drawImage(getBitmap(), (int) posX, (int) posY);
    }

    @Override
    public Bitmap getBitmap() {
        return clicked ? getButtonSelectedBitmap() : getButtonUnselectedBitmap();
    }

    @Override
    public boolean clickAttempt(int clickX, int clickY, int gameWidth, int gameHeight) {
        clicked = super.clickAttempt(clickX, clickY, gameWidth, gameHeight);

        return clicked;
    }

    @Override
    public void update(float delta, int gameWidth, int gameHeight) {
        // Do nothing
    }

    protected abstract Bitmap getButtonSelectedBitmap();

    protected abstract Bitmap getButtonUnselectedBitmap();
}
