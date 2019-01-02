package com.ernestoyaquello.keepieuppie.views.game.states;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

import com.ernestoyaquello.keepieuppie.BuildConfig;
import com.ernestoyaquello.keepieuppie.views.activities.GameActivity;
import com.ernestoyaquello.keepieuppie.views.game.framework.utils.Painter;
import com.ernestoyaquello.keepieuppie.views.game.models.IndicationArrow;
import com.ernestoyaquello.keepieuppie.views.game.models.MenuButtonSecondary;
import com.ernestoyaquello.keepieuppie.views.game.states.base.BaseState;
import com.ernestoyaquello.keepieuppie.views.game.utils.Assets;

import java.lang.reflect.Type;

public class StartState extends BaseState<StartState.StartStateRestorationParams> {
    private int outsideClicks;
    private MenuButtonSecondary menuButton;
    private IndicationArrow leftIndicationArrow, rightIndicationArrow;

    public StartState(GameActivity activity) {
        super(activity);
    }

    @Override
    public void initialize(int gameWidth, int gameHeight, boolean hasBeenRestored) {
        initializeFirstState();

        createBall();
        ball.setPosX((gameWidth / 2f) - ball.getRadius());
        ball.setPosY((gameHeight / 2f) - ball.getRadius());

        menuButton = new MenuButtonSecondary(Assets.getMenuButtonSecondary());
        menuButton.setPosX(ball.getRadius() / 2f);
        menuButton.setPosY(gameHeight - menuButton.getHeight() - ball.getRadius() / 2f);

        createIndicationArrows(gameWidth, gameHeight);

//        if (isSoundActivated(activity)) {
//            Assets.playWhistleSound();
//        }
    }

    @Override
    public void updateImpl(float delta, int gameWidth, int gameHeight) {
        ball.update(delta, gameWidth, gameHeight);
        leftIndicationArrow.update(delta, gameWidth, gameHeight);
        rightIndicationArrow.update(delta, gameWidth, gameHeight);
        menuButton.setPosX(ball.getRadius() / 2f);
        menuButton.setPosY(gameHeight - menuButton.getHeight() - ball.getRadius() / 2f);
    }

    @Override
    public void renderBeforeBall(Painter painter, int gameWidth, int gameHeight) {
        painter.drawText(String.valueOf(score), (gameWidth / 2), (gameHeight / 3),
                Assets.getVisitorFont(), (0.15f * gameWidth), Color.rgb(0, 110, 160),
                Paint.Align.CENTER);

        menuButton.render(painter, gameWidth, gameHeight);
    }

    @Override
    public void renderAfterBall(Painter painter, int gameWidth, int gameHeight) {
        leftIndicationArrow.render(painter, gameWidth, gameHeight);
        rightIndicationArrow.render(painter, gameWidth, gameHeight);
    }

    @Override
    public boolean onTouch(int xInGame, int yInGame, int gameWidth, int gameHeight, MotionEvent event) {
        if (ball != null && menuButton != null) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (ball.firstClickAttempt(xInGame, yInGame, gameWidth, gameHeight)) {
                    setScore(1);
                    if (isSoundActivated(activity)) {
                        Assets.playKickSound();
                    }
                    setCurrentState(new PlayState(activity));

                    return true;
                } else if (!menuButton.isClicked()) {
                    menuButton.clickAttempt(xInGame, yInGame, gameWidth, gameHeight);
                }

                if (!menuButton.isClicked()) {
                    // Easter egg
                    outsideClicks++;
                    int numberOfClicksToActivateBot = BuildConfig.DEBUG ? 5 : 100;
                    if (outsideClicks >= numberOfClicksToActivateBot) {
                        startBot();
                    }
                }
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (menuButton.isClicked()) {
                    menuButton.clickAttempt(xInGame, yInGame, gameWidth, gameHeight);
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (menuButton.isClicked()) {
                    menuButton.setClicked(false);
                    finishGameActivity();
                }
            }
        }

        return true;
    }

    private void startBot() {
        outsideClicks = 0;
        setCurrentState(new PlayState(activity, true));
    }

    @Override
    public void pause() {
        // Do nothing here
    }

    @Override
    protected StartState.StartStateRestorationParams saveStateToStateParametersInstance() {
        // No need to save any parameters in this state as the default ones are being saved on the
        // base state, so we just return an empty "restoration parameters" object
        return new StartState.StartStateRestorationParams();
    }

    @Override
    protected void restoreStateFromStateParametersInstance(StartState.StartStateRestorationParams stateParameters) {
        // No custom parameters to restore here (this state is restored entirely in the base state
        // with just the default state parameters)
    }

    @Override
    protected Type getStateParametersClassType() {
        return StartState.StartStateRestorationParams.class;
    }

    @Override
    protected void removeReferences() {
        menuButton = null;
        leftIndicationArrow = null;
        rightIndicationArrow = null;
    }

    private void createIndicationArrows(int gameWidth, int gameHeight) {
        int halfWidthArrowBitmap = (int) (Assets.getBallArrowLeft().getWidth() / 2f);

        int posXCenter = (int) (ball.getPosXCenter() - halfWidthArrowBitmap);
        int posXOffset = (int) ((ball.getRadius() * 2f) / 3f);
        int posY = (int) (ball.getPosY() + (int) (ball.getRadius() * 2.2f));
        int vel = (int) (gameWidth * 0.1f);

        leftIndicationArrow = new IndicationArrow(posXCenter - posXOffset, posY, vel, true);
        rightIndicationArrow = new IndicationArrow(posXCenter + posXOffset, posY, vel, false);
    }

    public class StartStateRestorationParams extends BaseState.BaseStateRestorationParams {
    }
}