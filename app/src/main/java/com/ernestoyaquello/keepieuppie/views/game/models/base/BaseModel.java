package com.ernestoyaquello.keepieuppie.views.game.models.base;

import android.graphics.Bitmap;

import com.ernestoyaquello.keepieuppie.views.game.framework.utils.Painter;

public abstract class BaseModel {
    protected float posX;
    protected float posY;
    protected Bitmap bitmap;

    protected BaseModel(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public float getPosX() {
        return posX;
    }

    public float getPosXCenter() {
        return posX + (bitmap.getWidth() / 2f);
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public float getPosYCenter() {
        return posY + (bitmap.getHeight() / 2f);
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public float getWidth() {
        return bitmap.getWidth();
    }

    public float getHeight() {
        return bitmap.getHeight();
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public boolean clickAttempt(int clickX, int clickY, int gameWidth, int gameHeight) {
        return clickX >= posX && clickX <= (posX + bitmap.getWidth()) && clickY >= posY && clickY <= (posY + bitmap.getHeight());
    }

    public void render(Painter painter, int gameWidth, int gameHeight) {
        painter.drawImage(bitmap, (int) posX, (int) posY);
    }

    public abstract void update(float delta, int gameWidth, int gameHeight);
}
