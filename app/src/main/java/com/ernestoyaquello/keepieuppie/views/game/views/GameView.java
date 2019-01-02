package com.ernestoyaquello.keepieuppie.views.game.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

import com.ernestoyaquello.keepieuppie.views.activities.GameActivity;
import com.ernestoyaquello.keepieuppie.views.game.framework.utils.InputHandler;
import com.ernestoyaquello.keepieuppie.views.game.framework.utils.Painter;
import com.ernestoyaquello.keepieuppie.views.game.states.TutorialState;
import com.ernestoyaquello.keepieuppie.views.game.states.base.BaseState;
import com.ernestoyaquello.keepieuppie.views.game.states.base.State;
import com.ernestoyaquello.keepieuppie.views.game.utils.Assets;

import java.lang.reflect.Constructor;

public class GameView extends View implements Runnable {
    private static final String SUPER_STATE_KEY = "superState";
    private static final String STATE_CLASS_NAME_KEY = "currentStateClassName";
    private static final String STATE_SERIALIZED_PARAMETERS_KEY = "currentStateSerializedParameters";

    private Painter graphics;
    private int gameWidth;
    private int gameHeight;

    private volatile boolean isFinishing;
    private volatile boolean isRunning;
    private volatile boolean isDrawing;
    private volatile boolean isRestoringState;
    private boolean bgSoundPlaying;
    private State currentState;
    private State firstState;
    private Thread updaterThread;
    private long timeAfterUpdate;

    private GameActivity gameActivity;
    private InputHandler inputHandler;

    public GameView(Context context) {
        super(context);

        setupGameView((GameActivity) context);
    }

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setupGameView((GameActivity) context);
    }

    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setupGameView((GameActivity) context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public GameView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setupGameView((GameActivity) context);
    }

    private void setupGameView(GameActivity gameActivity) {
        this.gameActivity = gameActivity;
        this.firstState = new TutorialState(gameActivity);

        this.gameWidth = 0;
        this.gameHeight = 0;

        this.isRestoringState = true;
        this.isRunning = false;
        this.bgSoundPlaying = false;

        setSaveEnabled(true);
        setSaveFromParentEnabled(true);

        gameActivity.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        gameActivity.getWindow().setFormat(PixelFormat.RGB_565);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);

        if ((width != gameWidth || height != gameHeight) && width > 0 && height > 0) {
            gameWidth = width;
            gameHeight = height;

            onGameViewMeasured();
        }
    }

    private void onGameViewMeasured() {
        if (gameActivity != null) {
            Assets.loadAllAssets(gameActivity.getApplicationContext(), gameWidth);

            if (graphics == null) {
                graphics = new Painter();
                setLayerType(LAYER_TYPE_HARDWARE, graphics.getPaint());
            }

            while (!isRestoringState) {
                // TODO Avoid this loop with proper synchronisation code
            }

            initInput();
            if (currentState == null) {
                setCurrentState(firstState);
                firstState = null;
            }

            startGame();
        }
    }

    private void initInput() {
        if (inputHandler == null) {
            inputHandler = new InputHandler();

            setOnTouchListener(inputHandler);
        }
    }

    public void setCurrentState(State newState) {
        if (newState != null) {
            final State previousState = currentState;

            newState.init(gameWidth, gameHeight);
            currentState = newState;
            inputHandler.setCurrentState(currentState);

            if (previousState != null) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        previousState.cleanResources();
                    }
                }, 1000);
            }
        }
    }

    private void startGame() {
        if (!isRunning) {
            isRunning = true;

            // Start updating
            timeAfterUpdate = System.nanoTime();
            updaterThread = new Thread(this, "Game Updater Thread");
            updaterThread.start();
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            long timeBeforeUpdate = System.nanoTime();
            long updateDuration = timeBeforeUpdate - timeAfterUpdate;

            update(updateDuration / 1000000L);
            postInvalidateOnAnimation();

            timeAfterUpdate = System.nanoTime();

            long sleepTime = Math.max(2, 17 - (timeAfterUpdate - timeBeforeUpdate));
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isRunning && !isDrawing) {
            isDrawing = true;

            render(canvas);

            isDrawing = false;
        }
    }

    private void update(float delta) {
        if (currentState != null) {
            currentState.update(delta, gameWidth, gameHeight);
        }
    }

    private void render(Canvas canvas) {
        if (currentState != null) {
            graphics.setCanvas(canvas);
            currentState.render(graphics, gameWidth, gameHeight);
        }
    }

    public void pause() {
        if (currentState != null) {
            currentState.pause();
        }
    }

    public void resume() {
        if (currentState != null) {
            currentState.resume();
        }
    }

    public synchronized void playBackgroundSound() {
        if (!bgSoundPlaying) {
            bgSoundPlaying = true;
            Assets.playCrowdSound();
        }
    }

    public synchronized void stopBackgroundSound() {
        if (bgSoundPlaying) {
            bgSoundPlaying = false;
            Assets.stopCrowdSound();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        finishGame();
    }

    public synchronized void finishGame() {
        if (isRunning && !isFinishing) {
            isFinishing = true;
            isRunning = false;

            gameWidth = 0;
            gameHeight = 0;

            finishGameRunnables();
            removeReferences();
            System.gc();
        }
    }

    private void finishGameRunnables() {
        while (updaterThread != null && updaterThread.isAlive()) {
            try {
                updaterThread.join();
            } catch (InterruptedException e) {
                // Do nothing
            }
        }
    }

    private void removeReferences() {
        if (currentState != null) {
            ((BaseState) currentState).destroy();
        }

        gameActivity = null;
        currentState = null;
        firstState = null;
        graphics = null;
        inputHandler = null;
        updaterThread = null;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SUPER_STATE_KEY, super.onSaveInstanceState());

        if (currentState != null) {
            String currentStateClassName = currentState.getClass().getName();
            bundle.putString(STATE_CLASS_NAME_KEY, currentStateClassName);
            bundle.putString(STATE_SERIALIZED_PARAMETERS_KEY, currentState.saveStateToSerializedState());
        }

        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        isRestoringState = false;

        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;

            String currentStateClassName = bundle.getString(STATE_CLASS_NAME_KEY, null);
            if (currentStateClassName != null && !currentStateClassName.equals("")) {
                try {
                    Class<?> stateClass = Class.forName(currentStateClassName);
                    Constructor constructor = stateClass.getConstructor(GameActivity.class);
                    State previousStateRestored = (State) constructor.newInstance(gameActivity);
                    String serializedStateParameters =
                            bundle.getString(STATE_SERIALIZED_PARAMETERS_KEY, null);
                    if (serializedStateParameters != null && !serializedStateParameters.equals("")) {
                        if (firstState != null) {
                            firstState.cleanResources();
                        }

                        previousStateRestored.restoreStateFromSerializedState(serializedStateParameters);
                        firstState = previousStateRestored;
                    }
                } catch (Exception e) {
                    // Do nothing
                }
            }

            state = bundle.getParcelable(SUPER_STATE_KEY);
        }

        super.onRestoreInstanceState(state);

        isRestoringState = true;
    }
}
