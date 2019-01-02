package com.ernestoyaquello.keepieuppie.views.game.states;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

import com.ernestoyaquello.keepieuppie.views.activities.GameActivity;
import com.ernestoyaquello.keepieuppie.views.game.framework.utils.Painter;
import com.ernestoyaquello.keepieuppie.views.game.models.Ball;
import com.ernestoyaquello.keepieuppie.views.game.models.PauseButton;
import com.ernestoyaquello.keepieuppie.views.game.states.base.BaseState;
import com.ernestoyaquello.keepieuppie.views.game.utils.Assets;

import java.lang.reflect.Type;
import java.util.Random;

public class PlayState extends BaseState<PlayState.PlayStateRestorationParams>
        implements Ball.BallTouchedTheFloorListener {
    private PauseButton pauseButton;
    private Bitmap gamePausedTextBitmap;
    private boolean pauseButtonIsRendered;
    private int scoreTextColor;

    private static Random random;
    private boolean useBot;

    public PlayState(GameActivity activity, boolean useBot) {
        super(activity);

        this.useBot = useBot;
    }

    public PlayState(GameActivity activity) {
        super(activity);

        this.useBot = false;
    }

    @Override
    public void initialize(int gameWidth, int gameHeight, boolean hasBeenRestored) {
        if (!hasBeenRestored) {
            paused = false;
        }

        ball.activate();
        random = new Random();

        pauseButton = new PauseButton(Assets.getPauseButton());
        pauseButton.setPosX(pauseButton.getWidth() * 0.35f);
        pauseButton.setPosY(gameHeight - (pauseButton.getHeight() * 1.35f));
        pauseButton.setPaused(paused);

        gamePausedTextBitmap = Assets.getGamePausedText();

        scoreTextColor = Color.rgb(0, 110, 160);
    }

    @Override
    public void updateImpl(float delta, int gameWidth, int gameHeight) {
        ball.update(delta, gameWidth, gameHeight, this);
        pauseButton.setPosX(pauseButton.getWidth() * 0.35f);
        pauseButton.setPosY(gameHeight - (pauseButton.getHeight() * 1.35f));
        useBotIfRequired(gameWidth, gameHeight);
    }

    @Override
    public void renderBeforeBall(Painter painter, int gameWidth, int gameHeight) {
        // Draw score
        float scorePosX = (gameWidth / 2f);
        float scorePosY = (gameHeight / 3f);
        painter.drawText(String.valueOf(score), scorePosX, scorePosY, Assets.getVisitorFont(),
                (0.15f * gameWidth), scoreTextColor, Paint.Align.CENTER);
    }

    @Override
    public void renderAfterBall(Painter painter, int gameWidth, int gameHeight) {
        // Draw pause/resume button (except when the ball is to close to it)
        pauseButtonIsRendered = paused || ball.getPosX() > (pauseButton.getPosX() + (pauseButton.getWidth() * 2f))
                || (ball.getPosY() + ball.getHeight()) < (pauseButton.getPosY() - pauseButton.getHeight());
        if (pauseButtonIsRendered) {
            pauseButton.render(painter, gameWidth, gameHeight);
        }

        // Draw pause text in the center of the screen if the game is paused
        if (paused) {
            float pauseTextPosX = (gameWidth - gamePausedTextBitmap.getWidth()) / 2f;
            float pauseTextPosY = (gameHeight / 2f) - (gamePausedTextBitmap.getHeight() / 2f);
            
            painter.drawImage(gamePausedTextBitmap, (int) pauseTextPosX, (int) pauseTextPosY);
        }
    }

    @Override
    public boolean onTouch(int xInGame, int yInGame, int gameWidth, int gameHeight, MotionEvent event) {
        if (ball != null && pauseButton != null) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (!paused) {
                    if (ball.clickAttempt(xInGame, yInGame, gameWidth, gameHeight)) {
                        incrementScore();
                        if (isSoundActivated(activity)) {
                            Assets.playKickSound();
                        }
                    } else if (!pauseButton.isClicked() && pauseButtonIsRendered
                            && !pauseButton.clickAttempt(xInGame, yInGame, gameWidth, gameHeight)) {
                        useBot = false;
                    }
                } else if (!pauseButton.isClicked() && pauseButtonIsRendered) {
                    pauseButton.clickAttempt(xInGame, yInGame, gameWidth, gameHeight);
                }
            } else if (event.getAction() == MotionEvent.ACTION_MOVE && pauseButton.isClicked()) {
                pauseButton.clickAttempt(xInGame, yInGame, gameWidth, gameHeight);
            } else if (event.getAction() == MotionEvent.ACTION_UP && pauseButton.isClicked()) {
                paused = !paused;

                pauseButton.setClicked(false);
                pauseButton.setPaused(paused);
            }
        }

        return true;
    }

    @Override
    public void pause() {
        super.pause();

        if (pauseButton != null) {
            pauseButton.setPaused(paused);
        }
    }

    @Override
    protected PlayStateRestorationParams saveStateToStateParametersInstance() {
        PlayStateRestorationParams parameters = new PlayStateRestorationParams();
        parameters.useBot = useBot;

        return parameters;
    }

    @Override
    protected void restoreStateFromStateParametersInstance(PlayStateRestorationParams stateParameters) {
        if (stateParameters != null) {
            useBot = stateParameters.useBot;
        }
    }

    @Override
    protected Type getStateParametersClassType() {
        return PlayStateRestorationParams.class;
    }

    @Override
    protected void removeReferences() {
        pauseButton = null;
        random = null;
        gamePausedTextBitmap = null;
    }

    @Override
    public void onBallTouchedTheFloor() {
        if (score == record) {
            setUserScore(activity, record);
        }

        if (isSoundActivated(activity)) {
            Assets.playFailSound();
        }

        setCurrentState(new GameOverState(activity));
    }

    private void useBotIfRequired(int gameWidth, int gameHeight) {
        if (useBot && !paused) {
            if(ball.getPosYCenter() > (gameHeight / (random.nextFloat() + 1.5)) && ball.getVelY() > 0) {
                if (ball.clickAttempt((int)(ball.getPosXCenter() + ball.getRadius() * 0.7f
                                * random.nextFloat() * (random.nextInt(2) == 1 ? 1 : -1)),
                        (int)(ball.getPosYCenter() + ball.getRadius() * random.nextFloat()
                                * (random.nextInt(2) == 1 ? 1 : -1)), gameWidth, gameHeight)) {
                    incrementScore();
                    if (isSoundActivated(activity)) {
                        Assets.playKickSound();
                    }
                }
            }
        }
    }

    public class PlayStateRestorationParams extends BaseState.BaseStateRestorationParams {
        private boolean useBot;
    }
}
