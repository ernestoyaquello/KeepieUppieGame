package com.ernestoyaquello.keepieuppie.views.game.states.base;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;

import com.ernestoyaquello.keepieuppie.storage.local.models.BallType;
import com.ernestoyaquello.keepieuppie.storage.local.utils.DataStorageHelper;
import com.ernestoyaquello.keepieuppie.views.activities.GameActivity;
import com.ernestoyaquello.keepieuppie.views.game.framework.animations.GameAnimation;
import com.ernestoyaquello.keepieuppie.views.game.framework.utils.Painter;
import com.ernestoyaquello.keepieuppie.views.game.models.Ball;
import com.ernestoyaquello.keepieuppie.views.game.models.Cloud;
import com.ernestoyaquello.keepieuppie.views.game.utils.Assets;
import com.google.gson.Gson;

import java.lang.reflect.Type;

public abstract class BaseState<T extends BaseState.BaseStateRestorationParams> implements State {
    protected static int bgColor;
    protected static int score;
    protected static int previousRecord;
    protected static int record;
    protected static boolean paused;
    protected static Ball ball;
    private static Cloud cloud, cloud2;

    private int gameWidth, gameHeight;
    private boolean hasBeenInitialized;
    private boolean hasBeenRestored;

    protected GameActivity activity;

    protected BaseState(GameActivity activity) {
        this.activity = activity;

        this.hasBeenInitialized = false;
    }

    @Override
    public void init(int gameWidth, int gameHeight) {
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;

        createBallIfNecessary();
        createCloudsIfNecessary();

        initialize(gameWidth, gameHeight, hasBeenRestored);
        hasBeenInitialized = true;
    }

    @Override
    public void update(float delta, int gameWidth, int gameHeight) {
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;

        int extraSpeed = score <= 250 ? score : 250;
        delta /= (float)(1500 - extraSpeed);

        if (hasBeenInitialized) {
            cloud.update(delta, gameWidth, gameHeight);
            cloud2.update(delta, gameWidth, gameHeight);

            if (!paused) {
                updateImpl(delta, gameWidth, gameHeight);
            }
        }
    }

    @Override
    public void render(Painter painter, int gameWidth, int gameHeight) {
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;

        if (hasBeenInitialized) {
            painter.fillRect(bgColor);

            cloud.render(painter, gameWidth, gameHeight);
            cloud2.render(painter, gameWidth, gameHeight);

            renderBackground(painter, gameWidth, gameHeight);

            renderBeforeBall(painter, gameWidth, gameHeight);
            ball.render(painter, gameWidth, gameHeight);
            renderAfterBall(painter, gameWidth, gameHeight);
        }
    }

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void resume() {
        paused = false;
    }

    @Override
    public void cleanResources() {
        removeReferences();
    }

    @Override
    public String saveStateToSerializedState() {
        T parameters = getStateParameters();

        return new Gson().toJson(parameters);
    }

    @Override
    public void restoreStateFromSerializedState(String serializedStateParameters) {
        if (serializedStateParameters != null && !serializedStateParameters.equals("")) {
            Gson gson = new Gson();
            T parameters = gson.fromJson(serializedStateParameters, getStateParametersClassType());
            setStateParameters(parameters);
        }
    }

    public void destroy() {
        cleanResources();

        ball = null;
        cloud = null;
        cloud2 = null;
        activity = null;
    }

    protected void initializeFirstState() {
        bgColor = Color.rgb(114, 214, 255);
        score = 0;
        record = getUserScore(activity);
        previousRecord = record;
    }

    protected void finishGameActivity() {
        activity.finishActivity();
    }

    protected void finishGameActivity(Bundle parameters) {
        if (parameters != null && !parameters.isEmpty()) {
            Intent intent = new Intent();
            intent.putExtras(parameters);

            activity.finishActivity(intent);
        } else {
            finishGameActivity();
        }
    }

    protected static int getUserScore(Activity activity) {
        return DataStorageHelper.getUserScore(activity);
    }

    protected static void setUserScore(Activity activity, int newScore) {
        DataStorageHelper.setUserScore(activity, newScore);
    }

    protected static boolean shouldShowTutorial(Activity activity) {
        return DataStorageHelper.shouldShowTutorial(activity);
    }

    protected static void setShowTutorial(Activity activity, boolean showTutorial) {
        DataStorageHelper.setShowTutorial(activity, showTutorial);
    }

    protected void setCurrentState(State newState) {
        activity.setCurrentState(newState);
    }

    protected void setScore(int newScore) {
        score = newScore;
        record = score > record ? score : record;
    }

    protected void incrementScore() {
        setScore(score + 1);
    }

    protected void createBall() {
        ball = createBallModelInstance(activity, gameWidth, gameHeight);
    }

    protected void createStandardBall() {
        Bitmap ballBitmap =  Assets.getBallStandard();
        GameAnimation ballOverlayAnimation = Assets.getBallOverlayAnimation();

        ball = new Ball(ballBitmap, ballOverlayAnimation, gameWidth);
    }

    protected static boolean isSoundActivated(Activity activity) {
        return DataStorageHelper.isMusicActivated(activity);
    }

    private void createBallIfNecessary() {
        if (ball == null) {
            createBall();
        }
    }

    private void createCloudsIfNecessary() {
        if (cloud == null) {
            cloud = new Cloud(-(int) ball.getRadius(), (int) (ball.getRadius() * 0.8f), (int) (gameWidth * 0.025f), 0);
        }

        if (cloud2 == null) {
            cloud2 = new Cloud((int) (gameWidth * 0.65f), (int) ball.getWidth(), (int) (gameWidth * 0.025f), 0);
        }
    }

    private static Ball createBallModelInstance(GameActivity activity, int gameWidth, int gameHeight) {
        Bitmap ballBitmap = null;
        switch (getUserSelectedBall(activity)) {
            case Standard:
                ballBitmap = Assets.getBallStandard();
                break;
            case Bronze:
                ballBitmap = Assets.getBallBronze();
                break;
            case Silver:
                ballBitmap = Assets.getBallSilver();
                break;
            case Gold:
                ballBitmap = Assets.getBallGold();
                break;
            case Titanium:
                ballBitmap = Assets.getBallTitanium();
                break;
            case Diamond:
                ballBitmap = Assets.getBallDiamond();
                break;
        }

        GameAnimation ballOverlayAnimation = Assets.getBallOverlayAnimation();

        return new Ball(ballBitmap, ballOverlayAnimation, gameWidth);
    }

    private static BallType getUserSelectedBall(Activity activity) {
        return DataStorageHelper.getUserSelectedBall(activity);
    }

    private static void renderBackground(Painter painter, int gameWidth, int gameHeight) {
        Bitmap bgDecorBitmap = Assets.getBackgroundDecoration();

        painter.drawImage(bgDecorBitmap, 0, gameHeight - bgDecorBitmap.getHeight());
    }

    private T getStateParameters() {
        T parameters = null;
        try {
            parameters = saveStateToStateParametersInstance();
            if (parameters != null) {
                parameters.gameWidth = gameWidth;
                parameters.gameHeight = gameHeight;
                parameters.ballPosX = ball.getPosX();
                parameters.ballPosY = ball.getPosY();
                parameters.ballActive = ball.isActive();
                parameters.ballRotation = ball.getRotation();
                parameters.ballVelRotation = ball.getVelRotation();
                parameters.ballVelX = ball.getVelX();
                parameters.ballVelY = ball.getVelY();
                parameters.score = score;
                parameters.previousRecord = previousRecord;
                parameters.record = record;
                parameters.paused = paused;
            }
        } catch (Exception e) {
            // Do nothing
        }

        return parameters;
    }

    private void setStateParameters(T stateParameters) {
        if (stateParameters != null) {
            gameWidth = stateParameters.gameWidth;
            gameHeight = stateParameters.gameHeight;

            // Load assets before restoring the state parameters
            Assets.loadAllAssets(activity.getApplicationContext(), gameWidth);

            // Restore ball and its state
            createBallIfNecessary();

            if (stateParameters.ballActive) {
                ball.activate();
            } else {
                ball.deactivate();
            }

            ball.setPosX(stateParameters.ballPosX);
            ball.setPosY(stateParameters.ballPosY);
            ball.setRotation(stateParameters.ballRotation);
            ball.setVelRotation(stateParameters.ballVelRotation);
            ball.setVelX(stateParameters.ballVelX);
            ball.setVelY(stateParameters.ballVelY);

            // Restore clouds (their position doesn't actually get restored, but it does not matter)
            createCloudsIfNecessary();

            // Restore other parameters
            score = stateParameters.score;
            previousRecord = stateParameters.previousRecord;
            record = stateParameters.record;
            paused = stateParameters.paused;

            restoreStateFromStateParametersInstance(stateParameters);

            hasBeenRestored = true;
        }
    }

    protected abstract T saveStateToStateParametersInstance();

    protected abstract void restoreStateFromStateParametersInstance(T stateParameters);

    protected abstract Type getStateParametersClassType();

    protected abstract void removeReferences();

    public abstract void initialize(int gameWidth, int gameHeight, boolean hasBeenRestored);

    @Override
    public abstract boolean onTouch(int xInGame, int yInGame, int gameWidth, int gameHeight, MotionEvent event);

    public abstract void updateImpl(float delta, int gameWidth, int gameHeight);

    public abstract void renderBeforeBall(Painter painter, int gameWidth, int gameHeight);

    public abstract void renderAfterBall(Painter painter, int gameWidth, int gameHeight);

    public abstract class BaseStateRestorationParams {
        int gameWidth;
        int gameHeight;
        float ballPosX;
        float ballPosY;
        float ballRotation;
        float ballVelRotation;
        float ballVelX;
        float ballVelY;
        boolean ballActive;
        int score;
        int previousRecord;
        int record;
        boolean paused;
    }
}
