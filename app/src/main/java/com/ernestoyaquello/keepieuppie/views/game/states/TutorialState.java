package com.ernestoyaquello.keepieuppie.views.game.states;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;

import com.ernestoyaquello.keepieuppie.views.activities.GameActivity;
import com.ernestoyaquello.keepieuppie.views.game.framework.utils.Painter;
import com.ernestoyaquello.keepieuppie.views.game.models.GotItButton;
import com.ernestoyaquello.keepieuppie.views.game.models.Hand;
import com.ernestoyaquello.keepieuppie.views.game.states.base.BaseState;
import com.ernestoyaquello.keepieuppie.views.game.utils.Assets;

import java.lang.reflect.Type;

public class TutorialState extends BaseState<TutorialState.TutorialStateRestorationParams> {
    private Bitmap tutorialText;
    private GotItButton gotItButton;
    private Hand hand;

    private Paint paint;
    private Rect dstRect;
    private int outsideFrameColor;
    private int scoreTextColor;

    private boolean ballPositioned;
    private boolean showGotItButton;
    private boolean transitionToNextStateStarted;
    private double handTappingTicks;
    private int kicks;

    private float tutorialTextPosX;
    private float tutorialTextPosY;
    private float slideUpAnimationOffsetY;
    private float transitionOffsetX;

    public TutorialState(GameActivity activity) {
        super(activity);
    }

    @Override
    public void initialize(int gameWidth, int gameHeight, boolean hasBeenRestored) {
        initializeFirstState();

        createStandardBall();
        ball.activate();
        ball.setPosX(transitionOffsetX + -ball.getWidth());
        ball.setPosY(-ball.getHeight());

        tutorialText = Assets.getTutorialText();
        gotItButton = new GotItButton(Assets.getGotItButton());
        hand = new Hand(Assets.getTutorialHand());

        paint = new Paint();
        dstRect = new Rect();
        outsideFrameColor = Color.rgb(0, 90,98);
        scoreTextColor = Color.rgb(16, 134, 159);

        ballPositioned = false;
        showGotItButton = false;
        transitionToNextStateStarted = false;
        handTappingTicks = 0;
        kicks = 0;

        slideUpAnimationOffsetY = -1;
        transitionOffsetX = 0;
    }

    @Override
    public void updateImpl(float delta, int gameWidth, int gameHeight) {
        if (!shouldShowTutorial(activity)) {
            goToStartState();
        } else {
            float margin = ball.getRadius() * 0.7f;

            float buttonPosX = (gameWidth / 2f) - (gotItButton.getWidth() / 2f);
            float buttonPosY = gameHeight - gotItButton.getHeight() - margin;

            tutorialTextPosX = transitionOffsetX + (gameWidth / 2f) - (tutorialText.getWidth() / 2f);
            tutorialTextPosY = margin;

            float tutorialTextPosBottom = tutorialTextPosY + tutorialText.getHeight();
            float ballMaxPosY = tutorialTextPosBottom + ((buttonPosY - tutorialTextPosBottom) / 2f) - margin;
            if (!ballPositioned) {
                ball.setPosX(transitionOffsetX + (gameWidth / 2f) - (ball.getWidth() / 2f));
                ball.setPosY(-ballMaxPosY);

                ballPositioned = true;
            } else if (transitionToNextStateStarted) {
                ball.setPosX(transitionOffsetX + (gameWidth / 2f) - (ball.getWidth() / 2f));
            }

            float handPosY = ballMaxPosY + (ball.getRadius() * 1.5f);
            hand.setPosX(transitionOffsetX + (gameWidth / 2f) - (hand.getWidth() * 0.4f));
            hand.setPosY(handPosY);

            if (!hand.isTapping() && (ball.getPosY() + ball.getHeight()) >= hand.getPosY()) {
                hand.setTapping(true);
                handTappingTicks = 0;
            } else if (handTappingTicks <= 0.045) {
                handTappingTicks += delta;
                if (handTappingTicks > 0.045) {
                    hand.setTapping(false);
                    handTappingTicks = 0;
                }
            }

            if (ball.getPosY() > ballMaxPosY) {
                ball.clickAttempt(
                        (int) ball.getPosXCenter(),
                        (int) (ball.getPosYCenter() + (ball.getRadius() * 0.15f)),
                        gameWidth,
                        gameHeight);
                kicks++;
                if (kicks >= 2) {
                    showGotItButton = true;
                }
            }

            if (showGotItButton) {
                if (slideUpAnimationOffsetY == -1) {
                    slideUpAnimationOffsetY = buttonPosY + gotItButton.getHeight() + margin;
                } else {
                    slideUpAnimationOffsetY -= gameWidth * delta * 3;
                    slideUpAnimationOffsetY = slideUpAnimationOffsetY < 0 ? 0 : slideUpAnimationOffsetY;
                }

                gotItButton.setPosX(transitionOffsetX + buttonPosX);
                gotItButton.setPosY(buttonPosY + slideUpAnimationOffsetY);
            }

            if (transitionToNextStateStarted) {
                transitionOffsetX -= gameWidth * delta * 9;
                transitionOffsetX = transitionOffsetX < -gameWidth ? -gameWidth : transitionOffsetX;

                if (transitionOffsetX == -gameWidth) {
                    goToStartState();
                }
            }

            if (!transitionToNextStateStarted) {
                ball.update(delta, gameWidth, gameHeight);
            }
        }
    }

    @Override
    public void renderBeforeBall(Painter painter, int gameWidth, int gameHeight) {
//        float scorePosX = (gameWidth / 2f);
//        float scorePosY = hand.getPosY() - ball.getRadius();
//        painter.drawText("DEMO", scorePosX, scorePosY, Assets.getVisitorFont(),
//                (0.18f * gameWidth), scoreTextColor, Paint.Align.CENTER);
    }

    @Override
    public void renderAfterBall(Painter painter, int gameWidth, int gameHeight) {
        hand.render(painter, gameWidth, gameHeight);
        drawOverlay(painter.getCanvas(), gameWidth, gameHeight);

        if (showGotItButton) {
            gotItButton.render(painter, gameWidth, gameHeight);
        }

        painter.drawImage(tutorialText, (int) tutorialTextPosX, (int) tutorialTextPosY);
    }

    @Override
    public boolean onTouch(int xInGame, int yInGame, int gameWidth, int gameHeight, MotionEvent event) {
        if (gotItButton != null) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (!gotItButton.isClicked()) {
                    gotItButton.clickAttempt(xInGame, yInGame, gameWidth, gameHeight);
                }
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (gotItButton.isClicked()) {
                    gotItButton.clickAttempt(xInGame, yInGame, gameWidth, gameHeight);
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (gotItButton.isClicked()) {
                    gotItButton.setClicked(false);
                    transitionToNextStateStarted = true;
                }
            }
        }

        return true;
    }

    private void goToStartState() {
        setCurrentState(new StartState(activity));
        setShowTutorial(activity, false);
    }

    @Override
    public void pause() {
        // Do nothing here
    }

    @Override
    protected TutorialStateRestorationParams saveStateToStateParametersInstance() {
        // No need to save any parameters in this state as the default ones are being saved on the
        // base state, so we just return an empty "restoration parameters" object
        return new TutorialState.TutorialStateRestorationParams();
    }

    @Override
    protected void restoreStateFromStateParametersInstance(TutorialStateRestorationParams stateParameters) {
        // No custom parameters to restore here (this state is restored entirely in the base state
        // with just the default state parameters)
    }

    @Override
    protected Type getStateParametersClassType() {
        return TutorialState.TutorialStateRestorationParams.class;
    }

    @Override
    protected void removeReferences() {
        tutorialText = null;
        gotItButton = null;
        hand = null;
        paint = null;
        dstRect = null;
    }

    private void drawOverlay(Canvas canvas, int gameWidth, int gameHeight) {
        paint.setColor(outsideFrameColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha((int) (90f * ((gameWidth + transitionOffsetX) / gameWidth)));
        dstRect.set(0, 0, gameWidth, gameHeight);

        canvas.drawRect(dstRect, paint);
    }

    public class TutorialStateRestorationParams extends BaseState.BaseStateRestorationParams {
    }
}
