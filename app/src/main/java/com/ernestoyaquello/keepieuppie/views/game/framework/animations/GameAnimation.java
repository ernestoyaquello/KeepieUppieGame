package com.ernestoyaquello.keepieuppie.views.game.framework.animations;

import android.graphics.Bitmap;

import com.ernestoyaquello.keepieuppie.views.game.framework.utils.Painter;

public class GameAnimation {
    private final GameAnimationFrame[] frames;
    private final double[] frameEndTimes;
    private int currentFrameIndex;
    private double totalDuration;
    private double currentTime;

    public GameAnimation(GameAnimationFrame... frames) {
        this.currentFrameIndex = 0;
        this.totalDuration = 0;
        this.currentTime = 0;
        this.frames = frames;
        this.frameEndTimes = new double[frames.length];
        for (int i = 0; i < frames.length; i++) {
            GameAnimationFrame f = frames[i];
            totalDuration += f.getDuration();
            frameEndTimes[i] = totalDuration;
        }
    }

    public synchronized void update(float delta) {
        currentTime += delta;
        if (currentTime > totalDuration) {
            wrapAnimation();
        }

        while (currentTime > frameEndTimes[currentFrameIndex]) {
            currentFrameIndex++;
        }
    }

    public void render(Painter painter, int x, int y, int width, int height) {
        Bitmap image = frames[currentFrameIndex].getImage();
        if (image != null) {
            painter.drawImage(image, x, y);
        }
    }

    public void render(Painter painter, int x, int y) {
        Bitmap image = frames[currentFrameIndex].getImage();
        if (image != null) {
            painter.drawImage(image, x, y);
        }
    }

    public GameAnimationFrame getFrame(int index) {
        return frames[index];
    }

    private void wrapAnimation() {
        currentFrameIndex = 0;
        currentTime %= totalDuration;
    }
}