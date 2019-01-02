package com.ernestoyaquello.keepieuppie.views.game.models;

import com.ernestoyaquello.keepieuppie.views.game.models.base.BaseModel;
import com.ernestoyaquello.keepieuppie.views.game.utils.Assets;

public class Cloud extends BaseModel {
    private final int velX;
    private final int velY;

    public Cloud(int posX, int posY, int velX, int velY) {
        super(Assets.getCloud());

        this.velX = velX;
        this.velY = velY;
        this.posX = posX;
        this.posY = posY;
    }

    @Override
    public void update(float delta, int gameWidth, int gameHeight) {
        posY += velY * delta;
        posX += velX * delta;

        float rightSideOffset = (posX - (getWidth() / 2f)) - gameWidth;
        if (rightSideOffset > 0) {
            // Move the cloud to the left of the canvas so it will eventually reappear later
            posX = -getWidth();
            posX += rightSideOffset;
        }
    }
}
