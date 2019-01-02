package com.ernestoyaquello.keepieuppie.views.game.models;

import android.graphics.Bitmap;

import com.ernestoyaquello.keepieuppie.views.game.framework.animations.GameAnimation;
import com.ernestoyaquello.keepieuppie.views.game.framework.utils.Painter;
import com.ernestoyaquello.keepieuppie.views.game.models.base.BaseButton;
import com.ernestoyaquello.keepieuppie.views.game.utils.Assets;

public class MenuButton extends BaseButton {
    private final GameAnimation menuButtonAnimation;
    private boolean animate;

    public MenuButton(Bitmap bitmap) {
        super(bitmap);

        menuButtonAnimation = Assets.getMenuButtonBlinkingAnimation();
        animate = false;
    }

    @Override
    protected Bitmap getButtonSelectedBitmap() {
        return Assets.getMenuButtonClicked();
    }

    @Override
    protected Bitmap getButtonUnselectedBitmap() {
        return Assets.getMenuButton();
    }

    @Override
    public void render(Painter painter, int gameWidth, int gameHeight) {
        if (!animate) {
            super.render(painter, gameWidth, gameHeight);
        } else {
            menuButtonAnimation.render(painter, (int) posX, (int) posY);
        }
    }

    @Override
    public void update(float delta, int gameWidth, int gameHeight) {
        if (!animate) {
            super.update(delta, gameWidth, gameHeight);
        } else {
            menuButtonAnimation.update(delta);
        }
    }

    public boolean isAnimated() {
        return animate;
    }

    public void animate() {
        this.animate = true;
    }

    public void stopAnimation() {
        this.animate = false;
    }
}