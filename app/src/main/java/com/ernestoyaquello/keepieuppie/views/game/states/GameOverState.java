package com.ernestoyaquello.keepieuppie.views.game.states;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;

import com.ernestoyaquello.keepieuppie.views.activities.GameActivity;
import com.ernestoyaquello.keepieuppie.views.game.framework.animations.GameAnimation;
import com.ernestoyaquello.keepieuppie.views.game.framework.utils.Painter;
import com.ernestoyaquello.keepieuppie.views.game.models.MenuButton;
import com.ernestoyaquello.keepieuppie.views.game.models.PlayAgainButton;
import com.ernestoyaquello.keepieuppie.views.game.models.ShareButton;
import com.ernestoyaquello.keepieuppie.views.game.states.base.BaseState;
import com.ernestoyaquello.keepieuppie.views.game.utils.Assets;

import java.lang.reflect.Type;

public class GameOverState extends BaseState<GameOverState.GameOverStateRestorationParams> {
    private PlayAgainButton playAgainButton;
    private MenuButton menuButton;
    private ShareButton shareButton;

    private Bitmap scoreBoardTopBitmap;
    private Bitmap scoreBoardBottomBitmap;
    private GameAnimation unlockedBallTextAnimation;

    private float topMargin;
    private float slideDownAnimationOffsetY;
    private float slideRightAnimationOffsetX;

    private boolean buttonsAndScoreAnimationsFinished;
    private boolean showingShareScoreDialog;

    private boolean newUnlockedBall;

    public GameOverState(GameActivity activity) {
        super(activity);
    }

    @Override
    public void initialize(int gameWidth, int gameHeight, boolean hasBeenRestored) {
        scoreBoardTopBitmap = Assets.getScoreBoardTop();
        scoreBoardBottomBitmap = Assets.getScoreBoardBottom();
        playAgainButton = new PlayAgainButton(Assets.getPlayAgainButton());
        menuButton = new MenuButton(Assets.getMenuButton());
        shareButton = new ShareButton(Assets.getShareButton());

        topMargin = ball.getHeight();

        slideDownAnimationOffsetY = -(topMargin
                + scoreBoardTopBitmap.getHeight()
                + scoreBoardBottomBitmap.getHeight()
                + playAgainButton.getHeight());
        slideRightAnimationOffsetX = -((gameWidth / 2f) + (playAgainButton.getWidth() / 2f));

        buttonsAndScoreAnimationsFinished = false;

        newUnlockedBall = false;

        int[] unlockBallThresholdScores = {25, 50, 100, 200, 500};
        for (int i = 0; i < unlockBallThresholdScores.length && !newUnlockedBall; i++) {
            int thresholdScore = unlockBallThresholdScores[i];
            newUnlockedBall = previousRecord < thresholdScore && score >= thresholdScore;
        }

        if (newUnlockedBall) {
            unlockedBallTextAnimation = Assets.getNewBallUnlockedTextBlinkingAnimation();
        }
    }

    @Override
    public void updateImpl(float delta, int gameWidth, int gameHeight) {
        ball.update(delta, gameWidth, gameHeight);

        if (newUnlockedBall && buttonsAndScoreAnimationsFinished) {
            // Use blinking animation on menu button to suggest the user to click it
            menuButton.animate();
            menuButton.update(delta, gameWidth, gameHeight);

            unlockedBallTextAnimation.update(delta);
        }

        if (!buttonsAndScoreAnimationsFinished) {
            // Slide down animation
            if (slideDownAnimationOffsetY < 0) {
                slideDownAnimationOffsetY += gameWidth * delta * 3;
            }

            if (slideDownAnimationOffsetY >= 0) {
                slideDownAnimationOffsetY = 0;
            }

            // Slide right animation
            if (slideRightAnimationOffsetX < 0) {
                slideRightAnimationOffsetX += gameWidth * delta * 3;
            }

            if (slideRightAnimationOffsetX >= 0) {
                slideRightAnimationOffsetX = 0;
            }

            buttonsAndScoreAnimationsFinished = slideDownAnimationOffsetY == 0
                    && slideRightAnimationOffsetX == 0;
        }
    }

    @Override
    public void renderBeforeBall(Painter painter, int gameWidth, int gameHeight) {
    }

    @Override
    public void renderAfterBall(Painter painter, int gameWidth, int gameHeight) {
        if (!showingShareScoreDialog) {
            // Render scoreboard layout (two images)
            int scoreBoardTopPosX = (int) ((gameWidth / 2f) - (scoreBoardTopBitmap.getWidth() / 2f));
            int scoreBoardTopPosY = (int) (topMargin + slideDownAnimationOffsetY);
            painter.drawImage(scoreBoardTopBitmap,
                    scoreBoardTopPosX,
                    scoreBoardTopPosY);

            int scoreBoardBottomPosX = scoreBoardTopPosX;
            int scoreBoardBottomPosY = scoreBoardTopPosY + scoreBoardTopBitmap.getHeight();
            painter.drawImage(scoreBoardBottomBitmap,
                    scoreBoardBottomPosX,
                    scoreBoardBottomPosY);

            // Render score/record text
            float scorePosX = (gameWidth / 2f) - (scoreBoardTopBitmap.getWidth() * 0.225f);
            float scorePosY = topMargin
                    + (scoreBoardTopBitmap.getHeight() / 2f)
                    + (0.05f * gameWidth)
                    + slideDownAnimationOffsetY;
            painter.drawText(String.valueOf(score).replace('0', 'O'),
                    scorePosX,
                    scorePosY,
                    Assets.getDigitalPlayFont(),
                    (0.1f * gameWidth),
                    Color.rgb(255, 255, 255),
                    Paint.Align.CENTER);

            float recordPosX = (gameWidth / 2f) + (scoreBoardTopBitmap.getWidth() * 0.25f);
            float recordPosY = scorePosY;
            painter.drawText(String.valueOf(record).replace('0', 'O'),
                    recordPosX,
                    recordPosY,
                    Assets.getDigitalPlayFont(),
                    (0.1f * gameWidth),
                    Color.rgb(255, 255, 255),
                    Paint.Align.CENTER);

            // Render play again button
            int playAgainButtonPosX = (int) (slideRightAnimationOffsetX
                    + (gameWidth / 2f)
                    - (playAgainButton.getWidth() / 2f));
            int playAgainButtonPosY = (int) (topMargin
                    + scoreBoardTopBitmap.getHeight()
                    + scoreBoardBottomBitmap.getHeight()
                    + (gameWidth * 0.048f));
            playAgainButton.setPosX(playAgainButtonPosX);
            playAgainButton.setPosY(playAgainButtonPosY);
            playAgainButton.render(painter, gameWidth, gameHeight);

            // Render share button
            int shareButtonPosX = (int) (playAgainButtonPosX - (2 * slideRightAnimationOffsetX));
            int shareButtonPosY = (int) (playAgainButtonPosY + playAgainButton.getHeight() + (gameWidth * 0.024f));
            shareButton.setPosX(shareButtonPosX);
            shareButton.setPosY(shareButtonPosY);
            shareButton.render(painter, gameWidth, gameHeight);

            // Render menu button
            int menuButtonPosX = (int) (shareButtonPosX + playAgainButton.getWidth() - menuButton.getWidth());
            int menuButtonPosY = shareButtonPosY;
            menuButton.setPosX(menuButtonPosX);
            menuButton.setPosY(menuButtonPosY);
            menuButton.render(painter, gameWidth, gameHeight);

            if (newUnlockedBall && buttonsAndScoreAnimationsFinished) {
                // Render new unlocked ball text
                int unlockedBallTextPosX = playAgainButtonPosX;
                int unlockedBallTextPosY = (int) (shareButtonPosY
                        + shareButton.getHeight()
                        + (gameWidth * 0.024f));
                unlockedBallTextAnimation.render(painter, unlockedBallTextPosX, unlockedBallTextPosY);
            }
        }
    }

    @Override
    public boolean onTouch(int xInGame, int yInGame, int gameWidth, int gameHeight, MotionEvent event) {
        if (buttonsAndScoreAnimationsFinished && !showingShareScoreDialog) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (!buttonPressed()) {
                    playAgainButton.clickAttempt(xInGame, yInGame, gameWidth, gameHeight);
                    menuButton.clickAttempt(xInGame, yInGame, gameWidth, gameHeight);
                    shareButton.clickAttempt(xInGame, yInGame, gameWidth, gameHeight);
                }
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (buttonPressed()) {
                    playAgainButton.clickAttempt(xInGame, yInGame, gameWidth, gameHeight);
                    menuButton.clickAttempt(xInGame, yInGame, gameWidth, gameHeight);
                    shareButton.clickAttempt(xInGame, yInGame, gameWidth, gameHeight);
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (buttonPressed()) {
                    if (playAgainButton.isClicked()) {
                        playAgainButton.setClicked(false);
                        setCurrentState(new StartState(activity));
                    } else if (menuButton.isClicked()) {
                        menuButton.setClicked(false);
                        if (!newUnlockedBall) {
                            finishGameActivity();
                        } else {
                            Bundle parameters = new Bundle();
                            parameters.putBoolean("newBall", true);
                            finishGameActivity(parameters);
                        }
                    } else if (shareButton.isClicked()) {
                        shareButton.setClicked(false);
                        shareScore(score, new GameActivity.ScoreSharingDialogListener() {
                            @Override
                            public void onScoreSharingDialogAppeared() {
                                showingShareScoreDialog = true;
                            }

                            @Override
                            public void onScoreSharingDialogDisappeared() {
                                showingShareScoreDialog = false;
                            }
                        });
                    }
                }
            }
//        } else if (!buttonsAndScoreAnimationsFinished && event.getAction() == MotionEvent.ACTION_DOWN) {
//            // Finish animations when the user clicks and they are still happening
//            slideDownAnimationOffsetY = 0;
//            slideRightAnimationOffsetX = 0;
//            buttonsAndScoreAnimationsFinished = true;
        }

        return true;
    }

    @Override
    public void pause() {
        // Do nothing here
    }

    @Override
    protected GameOverState.GameOverStateRestorationParams saveStateToStateParametersInstance() {
        // No need to save any parameters in this state as the default ones are being saved on the
        // base state, so we just return an empty "restoration parameters" object
        return new GameOverState.GameOverStateRestorationParams();
    }

    @Override
    protected void restoreStateFromStateParametersInstance(GameOverState.GameOverStateRestorationParams stateParameters) {
        // No custom parameters to restore here (this state is restored entirely in the base state
        // with just the default state parameters)
    }

    @Override
    protected Type getStateParametersClassType() {
        return GameOverState.GameOverStateRestorationParams.class;
    }

    @Override
    protected void removeReferences() {
        playAgainButton = null;
        menuButton = null;
        shareButton = null;
        scoreBoardTopBitmap = null;
        scoreBoardBottomBitmap = null;
        unlockedBallTextAnimation = null;
    }

    private void shareScore(int score, GameActivity.ScoreSharingDialogListener sharedListener) {
        activity.shareScore(score, sharedListener);
    }

    private boolean buttonPressed() {
        return (playAgainButton != null && playAgainButton.isClicked())
                || (menuButton != null && menuButton.isClicked())
                || (shareButton != null && shareButton.isClicked());
    }

    public class GameOverStateRestorationParams extends BaseState.BaseStateRestorationParams {
    }
}