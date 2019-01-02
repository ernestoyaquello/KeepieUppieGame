package com.ernestoyaquello.keepieuppie.views.game.framework.animations;

import android.graphics.Bitmap;

public class GameAnimationFrame {
    private final Bitmap image;
    private final double duration;

    public GameAnimationFrame(Bitmap image, double duration) {
        this.image = image;
        this.duration = duration;
    }

    public double getDuration() {
        return duration;
    }

    public Bitmap getImage() {
        return image;
    }
}