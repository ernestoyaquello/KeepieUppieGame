package com.ernestoyaquello.keepieuppie.views.game.framework.utils;

import android.view.MotionEvent;
import android.view.View;

import com.ernestoyaquello.keepieuppie.views.game.states.base.State;

public class InputHandler implements View.OnTouchListener {
    private State currentState;

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int xInGame = (int)event.getX();
        int yInGame = (int)event.getY();

        return currentState.onTouch(xInGame, yInGame, v.getWidth(), v.getHeight(), event);
    }
}