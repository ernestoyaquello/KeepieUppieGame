package com.ernestoyaquello.keepieuppie.views.game.models;

import android.graphics.Bitmap;

import com.ernestoyaquello.keepieuppie.views.game.framework.animations.GameAnimation;
import com.ernestoyaquello.keepieuppie.views.game.framework.utils.Painter;
import com.ernestoyaquello.keepieuppie.views.game.models.base.BaseModel;

public class Ball extends BaseModel {

    public interface BallTouchedTheFloorListener {
        void onBallTouchedTheFloor();
    }

    private final float radius;
    private final float ballMargin;
    private final float accelerationGravity;
    private float rotation;
    private float velRotation;
    private float velX;
    private float velY;
    private boolean active;

    private final GameAnimation ballOverlayAnimation;

    public Ball(Bitmap bitmap, GameAnimation ballOverlayAnimation, int gameWidth) {
        super(bitmap);

        this.ballOverlayAnimation = ballOverlayAnimation;
        this.active = false;
        this.radius = (getWidth() + getHeight()) / 4;
        this.posX -= radius;
        this.posY -= radius;
        this.ballMargin = 0.07f * radius;
        this.rotation = 0;
        this.velRotation = 0;
        this.accelerationGravity = 28 * gameWidth;
    }

    public float getVelX() {
        return velX;
    }

    public float getVelY() {
        return velY;
    }

    public float getRadius() {
        return radius;
    }

    public float getRotation() {
        return rotation;
    }

    public float getVelRotation() {
        return velRotation;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public boolean isActive() {
        return active;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public void setVelRotation(float velRotation) {
        this.velRotation = velRotation;
    }

    public void setVelX(float velX) {
        this.velX = velX;
    }

    public void setVelY(float velY) {
        this.velY = velY;
    }

    @Override
    public synchronized boolean clickAttempt(int clickX, int clickY, int gameWidth, int gameHeight) {
        float posXCenter = getPosXCenter();
        float posYCenter = getPosYCenter();

        double distanceToCenter = Math.sqrt(Math.pow(clickX - posXCenter, 2)
                + Math.pow(clickY - posYCenter, 2));
        if (distanceToCenter <= radius) {
            velX = -(((clickX - posXCenter) / radius) * (2.5f * gameWidth));
            velRotation = velX * (700f / gameWidth);
            velY = -((1.5f * gameWidth * (((clickY >= posYCenter)
                    ? ((clickY) - posYCenter) : 0) / radius)) + (4.25f * gameWidth));

            return true;
        }

        return false;
    }

    public synchronized boolean firstClickAttempt(int clickX, int clickY, int gameWidth, int gameHeight) {
        // User has to click on the bottom of the ball to start playing
        if (clickY > (posY + radius + (ballMargin * 3))) {
            return clickAttempt(clickX, clickY, gameWidth, gameHeight);
        }

        return false;
    }

    @Override
    public synchronized void render(Painter painter, int gameWidth, int gameHeight) {
        painter.drawImage(bitmap, (int) posX, (int) posY, rotation);

        if (!active) {
            ballOverlayAnimation.render(painter, (int) posX, (int) posY, (int) getWidth(), (int) getHeight());
        }
    }

    @Override
    public void update(float delta, int gameWidth, int gameHeight) {
        update(delta, gameWidth, gameHeight, null);
    }

    public synchronized void update(float delta, int gameWidth, int gameHeight,
                                    BallTouchedTheFloorListener floorTouchedListener) {
        if (active) {
            boolean accelerate = true;
            float prevPosY = posY;
            float prevPosX = posX;

            posY += velY * delta;
            posX += velX * delta;
            rotation += velRotation * delta;

            if ((posY + getHeight()) >= gameHeight) {
                if (floorTouchedListener != null && posY != prevPosY && velY != 0) {
                    floorTouchedListener.onBallTouchedTheFloor();
                }

                posY = gameHeight - getHeight();
                velY = velY * -0.75f;
                velY = Math.abs(velY) < (0.2f * gameHeight) ? 0 : velY;
                accelerate = velY != 0;

                if (velX != 0) {
                    float signumPreviousVelX = Math.signum(velX);
                    velX -= posX - prevPosX;
                    if (signumPreviousVelX != Math.signum(velX) || Math.abs(velX) < (gameWidth * 0.12f)) {
                        velX = 0;
                    }
                }

                if (Math.abs(velY) > gameHeight * 0.5f) {
                    velRotation = (((velX * (485f / gameWidth)) * 4f) + velRotation) / 5f;
                } else {
                    velRotation = velX * (485f / gameWidth);
                }
            }

            if ((posX + getWidth() - ballMargin) >= gameWidth) {
                posX = gameWidth - getWidth() + ballMargin;
                velX = -Math.abs(velX / 1.25f);
                velRotation -= 0.3f * (velY * (1f / ((float)gameHeight / (float)gameWidth)));
            } else if ((posX + ballMargin) <= 0) {
                posX = 0 - ballMargin;
                velX = Math.abs(velX / 1.25f);
                velRotation += 0.3f * (velY * (1f / ((float)gameHeight / (float)gameWidth)));
            }

            velY += accelerate ? (accelerationGravity * delta) : 0;
        } else {
            ballOverlayAnimation.update(delta);
        }
    }
}
