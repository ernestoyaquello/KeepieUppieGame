package com.ernestoyaquello.keepieuppie.views.game.models;

import com.ernestoyaquello.keepieuppie.views.game.models.base.BaseModel;
import com.ernestoyaquello.keepieuppie.views.game.utils.Assets;

public class IndicationArrow extends BaseModel {
    private final int limitYTop;
    private final int limitYBottom;
    private final boolean isLeft;
    private int vel;

    public IndicationArrow(int posX, int posY, int vel, boolean isLeft) {
        super(isLeft ? Assets.getBallArrowLeft() : Assets.getBallArrowRight());

        this.posX = posX;
        this.posY = posY;
        this.vel = -vel;
        this.limitYTop = posY;
        this.limitYBottom = posY + (int) (getHeight() + (getHeight() / 2.5f));
        this.isLeft = isLeft;
    }

    @Override
    public void update(float delta, int gameWidth, int gameHeight) {
        float velChange = vel * delta;
        posY += velChange;
        posX += isLeft ? -velChange : velChange;

        if ((posY + getHeight()) > limitYBottom) {
            float excess = (posY + getHeight()) - limitYBottom;
            posY -= excess;
            posX += isLeft ? excess : -excess;
            vel *= -1;
        } else if (posY < limitYTop) {
            float excess = limitYTop - posY;
            posY += excess;
            posX += isLeft ? -excess : excess;
            vel *= -1;
        }
    }
}
