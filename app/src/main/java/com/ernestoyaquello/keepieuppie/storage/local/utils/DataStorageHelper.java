package com.ernestoyaquello.keepieuppie.storage.local.utils;

import android.app.Activity;
import android.content.SharedPreferences;

import com.ernestoyaquello.keepieuppie.storage.local.models.BallType;
import com.ernestoyaquello.keepieuppie.storage.local.mappers.BallTypeMapper;

public class DataStorageHelper {
    private static final String PREFERENCES_FILE_KEY =
            "com.ernestoyaquello.keepieuppie.GAME_SETTINGS";

    private static final String USER_SCORE_KEY = "userScore";
    private static final String USER_SELECTED_BALL_KEY = "userSelectedBall";
    private static final String MUSIC_ON_OFF_KEY = "musicOnOff";
    private static final String SHOW_TUTORIAL_KEY = "showTutorialKey";

    private static final int defaultUserScore = 0;
    private static final int defaultUserSelectedBallNumber = 0;
    private static final boolean defaultMusicState = true;
    private static final boolean defaultShowTutorial = true;

    private static BallType ballType = null;
    private static Integer userScore = null;
    private static Boolean isMusicActivated = null;
    private static Boolean showTutorial = null;

    public static int getUserScore(Activity activity) {
        if (userScore == null) {
            SharedPreferences sharedPref = getSharedPreferences(activity);
            userScore = sharedPref.getInt(USER_SCORE_KEY, defaultUserScore);
        }

        return userScore;
    }

    public static void setUserScore(Activity activity, int newScore) {
        userScore = newScore;

        SharedPreferences sharedPref = getSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt(USER_SCORE_KEY, newScore);
        editor.apply();
    }

    public static BallType getUserSelectedBall(Activity activity) {
        if (ballType == null) {
            SharedPreferences sharedPref = getSharedPreferences(activity);
            int userSelectedBallNumber = sharedPref.getInt(USER_SELECTED_BALL_KEY,
                    defaultUserSelectedBallNumber);
            ballType = BallTypeMapper.fromIntegerToBallType(userSelectedBallNumber);
        }

        return ballType;
    }

    public static void setUserSelectedBall(Activity activity, BallType selectedBall) {
        ballType = selectedBall;

        SharedPreferences sharedPref = getSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt(USER_SELECTED_BALL_KEY, BallTypeMapper.fromBallTypeToInteger(selectedBall));
        editor.apply();
    }

    public static boolean isMusicActivated(Activity activity) {
        if (isMusicActivated == null) {
            SharedPreferences sharedPref = getSharedPreferences(activity);
            isMusicActivated = sharedPref.getBoolean(MUSIC_ON_OFF_KEY, defaultMusicState);
        }

        return isMusicActivated;
    }

    public static void setMusicState(Activity activity, boolean musicActivated) {
        isMusicActivated = musicActivated;

        SharedPreferences sharedPref = getSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putBoolean(MUSIC_ON_OFF_KEY, musicActivated);
        editor.apply();
    }

    public static boolean shouldShowTutorial(Activity activity) {
        if (showTutorial == null) {
            SharedPreferences sharedPref = getSharedPreferences(activity);
            showTutorial = sharedPref.getBoolean(SHOW_TUTORIAL_KEY, defaultShowTutorial);
        }

        return showTutorial;
    }

    public static void setShowTutorial(Activity activity, boolean show) {
        showTutorial = show;

        SharedPreferences sharedPref = getSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putBoolean(SHOW_TUTORIAL_KEY, show);
        editor.apply();
    }

    private static SharedPreferences getSharedPreferences(Activity activity) {
        return activity.getSharedPreferences(PREFERENCES_FILE_KEY, Activity.MODE_PRIVATE);
    }
}
