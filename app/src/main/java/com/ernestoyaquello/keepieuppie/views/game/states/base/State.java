package com.ernestoyaquello.keepieuppie.views.game.states.base;

import android.view.MotionEvent;

import com.ernestoyaquello.keepieuppie.views.game.framework.utils.Painter;

public interface State {
    void init(int gameWidth, int gameHeight);
    void update(float delta, int gameWidth, int gameHeight);
    void render(Painter painter, int gameWidth, int gameHeight);
    void pause();
    void resume();
    void cleanResources();
    boolean onTouch(int xInGame, int yInGame, int gameWidth, int gameHeight, MotionEvent event);
    String saveStateToSerializedState();
    void restoreStateFromSerializedState(String serializedStateParameters);
}