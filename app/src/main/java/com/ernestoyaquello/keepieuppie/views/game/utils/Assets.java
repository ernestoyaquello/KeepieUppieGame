package com.ernestoyaquello.keepieuppie.views.game.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.HandlerThread;
import androidx.core.content.res.ResourcesCompat;
import android.util.SparseArray;

import com.ernestoyaquello.keepieuppie.R;
import com.ernestoyaquello.keepieuppie.views.game.framework.animations.GameAnimation;
import com.ernestoyaquello.keepieuppie.views.game.framework.animations.GameAnimationFrame;

import java.util.ArrayList;
import java.util.List;

public class Assets {

    private static SoundPool soundPool;
    private static HandlerThread soundHandlerThread;
    private static Handler soundHandler;
    private static SparseArray<List<Integer>> soundStreamIds;
    private static volatile boolean startingOrStoppingSound;

    private static int kickSoundId;
    private static int whistleSoundId;
    private static int failSoundId;
    private static int crowdSoundId;
    private static int backgroundDecorationOriginalWidth;
    private static Bitmap backgroundDecoration;
    private static Bitmap cloud;
    private static Bitmap scoreBoardTop;
    private static Bitmap scoreBoardBottom;
    private static Bitmap ballStandard;
    private static Bitmap ballBronze;
    private static Bitmap ballSilver;
    private static Bitmap ballGold;
    private static Bitmap ballTitanium;
    private static Bitmap ballDiamond;
    private static Bitmap ballBottomHighlight;
    private static Bitmap ballTransparent;
    private static Bitmap ballArrowLeft;
    private static Bitmap ballArrowRight;
    private static Bitmap ballUnlockedText;
    private static Bitmap gotItButton;
    private static Bitmap playAgainButton;
    private static Bitmap shareButton;
    private static Bitmap menuButton;
    private static Bitmap menuButtonSecondary;
    private static Bitmap resumeButton;
    private static Bitmap pauseButton;
    private static Bitmap gotItButtonClicked;
    private static Bitmap playAgainButtonClicked;
    private static Bitmap shareButtonClicked;
    private static Bitmap menuButtonClicked;
    private static Bitmap menuButtonSecondaryClicked;
    private static Bitmap resumeButtonClicked;
    private static Bitmap pauseButtonClicked;
    private static Bitmap gamePausedText;
    private static Bitmap tutorialText;
    private static Bitmap tutorialHand;
    private static Bitmap tutorialHandTapping;
    private static GameAnimation ballOverlayAnimation;
    private static GameAnimation newBallUnlockedTextBlinkingAnimation;
    private static GameAnimation menuButtonBlinkingAnimation;
    private static Typeface visitorFont;
    private static Typeface digitalPlayFont;

    private static Assets instance;
    private static volatile boolean isLoadingAssets;
    private static boolean soundAssetsLoaded;
    private static boolean fontAssetsLoaded;
    private static boolean bitmapAssetsLoaded;

    public static Assets getInstance() {
        if (instance == null) {
            instance = new Assets();
        }

        return instance;
    }

    private Assets() {
        soundAssetsLoaded = false;
        fontAssetsLoaded = false;
        bitmapAssetsLoaded = false;
        isLoadingAssets = false;
    }

    public static void loadNonBitmapAssets(Context context) {
        loadAllAssets(context, 0);
    }

    public static void loadAllAssets(Context context, int gameWidth) {
        if (!isLoadingAssets) {
            isLoadingAssets = true;

            if (loadSounds(context) | loadFonts(context) | loadBitmapsAndAnimations(context, gameWidth)) {
                System.gc();
            }

            isLoadingAssets = false;
        }
    }

    private static boolean loadSounds(Context context) {
        if (!soundAssetsLoaded) {
            soundStreamIds = new SparseArray<>();

            if (soundPool == null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build();

                    soundPool = new SoundPool.Builder()
                            .setMaxStreams(5)
                            .setAudioAttributes(audioAttributes)
                            .build();
                } else {
                    soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
                }
            }

            if (soundHandlerThread != null && soundHandlerThread.isAlive()) {
                soundHandlerThread.quit();
            }

            soundHandlerThread = new HandlerThread("Sound Handler Thread");
            soundHandlerThread.start();
            soundHandler = new Handler(soundHandlerThread.getLooper());

            kickSoundId = loadSoundFromResources(context, R.raw.kick);
            whistleSoundId = loadSoundFromResources(context, R.raw.whistle);
            failSoundId = loadSoundFromResources(context, R.raw.fail);
            crowdSoundId = loadSoundFromResources(context, R.raw.crowd);

            soundAssetsLoaded = true;

            return true;
        }

        return false;
    }

    private static boolean loadFonts(Context context) {
        if (!fontAssetsLoaded) {
            visitorFont = loadFontFromResources(context, R.font.visitor);
            digitalPlayFont = loadFontFromResources(context, R.font.digital_play);

            fontAssetsLoaded = true;

            return true;
        }

        return false;
    }

    private static boolean loadBitmapsAndAnimations(Context context, int gameWidth) {
        if (!bitmapAssetsLoaded && gameWidth > 0) {

            // We load the background first so the rest of bitmaps can be resized relative to its size
            backgroundDecoration = loadBitmapFromResources(context, R.drawable.background_decoration, gameWidth, true);

            cloud = loadBitmapFromResources(context, R.drawable.cloud, gameWidth, false);
            scoreBoardTop = loadBitmapFromResources(context, R.drawable.scoreboard_results, gameWidth, false);
            scoreBoardBottom = loadBitmapFromResources(context, R.drawable.scoreboard_bottom, gameWidth, false);

            gotItButton = loadBitmapFromResources(context, R.drawable.tutorial_button_got_it, gameWidth, false);
            playAgainButton = loadBitmapFromResources(context, R.drawable.button_play_again, gameWidth, false);
            shareButton = loadBitmapFromResources(context, R.drawable.button_share, gameWidth, false);
            menuButton = loadBitmapFromResources(context, R.drawable.button_menu, gameWidth, false);
            menuButtonSecondary = loadBitmapFromResources(context, R.drawable.button_menu2, gameWidth, false);
            resumeButton = loadBitmapFromResources(context, R.drawable.button_resume, gameWidth, false);
            pauseButton = loadBitmapFromResources(context, R.drawable.button_pause, gameWidth, false);

            gotItButtonClicked = loadBitmapFromResources(context, R.drawable.tutorial_button_got_it_selected, gameWidth, false);
            playAgainButtonClicked = loadBitmapFromResources(context, R.drawable.button_play_again_selected, gameWidth, false);
            shareButtonClicked = loadBitmapFromResources(context, R.drawable.button_share_selected, gameWidth, false);
            menuButtonClicked = loadBitmapFromResources(context, R.drawable.button_menu_selected, gameWidth, false);
            menuButtonSecondaryClicked = loadBitmapFromResources(context, R.drawable.button_menu2_selected, gameWidth, false);
            resumeButtonClicked = loadBitmapFromResources(context, R.drawable.button_resume_selected, gameWidth, false);
            pauseButtonClicked = loadBitmapFromResources(context, R.drawable.button_pause_selected, gameWidth, false);
            gamePausedText = loadBitmapFromResources(context, R.drawable.game_paused_text, gameWidth, false);
            tutorialText = loadBitmapFromResources(context, R.drawable.tutorial_text, gameWidth, false);
            tutorialHand = loadBitmapFromResources(context, R.drawable.tutorial_hand, gameWidth, false);
            tutorialHandTapping = loadBitmapFromResources(context, R.drawable.tutorial_hand_selected, gameWidth, false);

            ballStandard = loadBitmapFromResources(context, R.drawable.ball_standard, gameWidth, false);
            ballBronze = loadBitmapFromResources(context, R.drawable.ball_bronze, gameWidth, false);
            ballSilver = loadBitmapFromResources(context, R.drawable.ball_silver, gameWidth, false);
            ballGold = loadBitmapFromResources(context, R.drawable.ball_gold, gameWidth, false);
            ballTitanium = loadBitmapFromResources(context, R.drawable.ball_titanium, gameWidth, false);
            ballDiamond = loadBitmapFromResources(context, R.drawable.ball_diamond, gameWidth, false);

            ballBottomHighlight = loadBitmapFromResources(context, R.drawable.ball_selected_bottom, gameWidth, false);
            ballTransparent = loadBitmapFromResources(context, R.drawable.ball_transparent, gameWidth, false);

            ballArrowLeft = loadBitmapFromResources(context, R.drawable.ball_arrow_left, gameWidth, false);
            ballArrowRight = loadBitmapFromResources(context, R.drawable.ball_arrow_right, gameWidth, false);

            ballOverlayAnimation = new GameAnimation(new GameAnimationFrame(ballTransparent, 0.4f), new GameAnimationFrame(ballBottomHighlight, 0.4f));

            ballUnlockedText = loadBitmapFromResources(context, R.drawable.ball_unlocked_text, gameWidth, false);
            newBallUnlockedTextBlinkingAnimation = new GameAnimation(new GameAnimationFrame(ballUnlockedText, 1), new GameAnimationFrame(null, 0.4f));

            menuButtonBlinkingAnimation = new GameAnimation(new GameAnimationFrame(menuButtonClicked, 1), new GameAnimationFrame(menuButton, 0.4f));

            bitmapAssetsLoaded = true;

            return true;
        }

        return false;
    }

    public static void playKickSound() {
        playSound(kickSoundId, false, false);
    }

    public static void playWhistleSound() {
        playSound(whistleSoundId, false, false);
    }

    public static void playFailSound() {
        playSound(failSoundId, false, false);
    }

    public static void playCrowdSound() {
        playSound(crowdSoundId, true, true);
    }

    public static void stopCrowdSound() {
        stopSound(crowdSoundId);
    }

    public static Bitmap getBackgroundDecoration() {
        return backgroundDecoration;
    }

    public static Bitmap getCloud() {
        return cloud;
    }

    public static Bitmap getScoreBoardTop() {
        return scoreBoardTop;
    }

    public static Bitmap getScoreBoardBottom() {
        return scoreBoardBottom;
    }

    public static Bitmap getBallStandard() {
        return ballStandard;
    }

    public static Bitmap getBallBronze() {
        return ballBronze;
    }

    public static Bitmap getBallSilver() {
        return ballSilver;
    }

    public static Bitmap getBallGold() {
        return ballGold;
    }

    public static Bitmap getBallTitanium() {
        return ballTitanium;
    }

    public static Bitmap getBallDiamond() {
        return ballDiamond;
    }

    public static Bitmap getBallBottomHighlight() {
        return ballBottomHighlight;
    }

    public static Bitmap getBallTransparent() {
        return ballTransparent;
    }

    public static Bitmap getBallUnlockedText() {
        return ballUnlockedText;
    }

    public static Bitmap getGotItButton() {
        return gotItButton;
    }

    public static Bitmap getPlayAgainButton() {
        return playAgainButton;
    }

    public static Bitmap getShareButton() {
        return shareButton;
    }

    public static Bitmap getMenuButton() {
        return menuButton;
    }

    public static Bitmap getMenuButtonSecondary() {
        return menuButtonSecondary;
    }

    public static Bitmap getResumeButton() {
        return resumeButton;
    }

    public static Bitmap getPauseButton() {
        return pauseButton;
    }

    public static Bitmap getGotItButtonClicked() {
        return gotItButtonClicked;
    }

    public static Bitmap getPlayAgainButtonClicked() {
        return playAgainButtonClicked;
    }

    public static Bitmap getShareButtonClicked() {
        return shareButtonClicked;
    }

    public static Bitmap getMenuButtonClicked() {
        return menuButtonClicked;
    }

    public static Bitmap getMenuButtonSecondaryClicked() {
        return menuButtonSecondaryClicked;
    }

    public static Bitmap getResumeButtonClicked() {
        return resumeButtonClicked;
    }

    public static Bitmap getPauseButtonClicked() {
        return pauseButtonClicked;
    }

    public static Bitmap getGamePausedText() {
        return gamePausedText;
    }

    public static Bitmap getTutorialText() {
        return tutorialText;
    }

    public static Bitmap getTutorialHand() {
        return tutorialHand;
    }

    public static Bitmap getTutorialHandTapping() {
        return tutorialHandTapping;
    }

    public static Bitmap getBallArrowLeft() {
        return ballArrowLeft;
    }

    public static Bitmap getBallArrowRight() {
        return ballArrowRight;
    }

    public static GameAnimation getBallOverlayAnimation() {
        return ballOverlayAnimation;
    }

    public static GameAnimation getNewBallUnlockedTextBlinkingAnimation() {
        return newBallUnlockedTextBlinkingAnimation;
    }

    public static GameAnimation getMenuButtonBlinkingAnimation() {
        return menuButtonBlinkingAnimation;
    }

    public static Typeface getVisitorFont() {
        return visitorFont;
    }

    public static Typeface getDigitalPlayFont() {
        return digitalPlayFont;
    }

    private static Typeface loadFontFromResources(Context context, int resourceId) {
        return ResourcesCompat.getFont(context, resourceId);
    }

    private static Bitmap loadBitmapFromResources(Context context, int resourceId, int gameWidth, boolean isBackground) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap originalSizeBitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, opts);

        int dimensionsInGameWidth;
        int dimensionsInGameHeight;

        if (!isBackground) {
            Rect dimensionsInGame = getBitmapDimensionsInGame(originalSizeBitmap, gameWidth);
            dimensionsInGameWidth = dimensionsInGame.width();
            dimensionsInGameHeight = dimensionsInGame.height();
        } else {
            backgroundDecorationOriginalWidth = originalSizeBitmap.getWidth();

            float aspectRatio = (float)originalSizeBitmap.getHeight() / (float)originalSizeBitmap.getWidth();
            dimensionsInGameWidth = gameWidth;
            dimensionsInGameHeight = (int)((float)gameWidth * aspectRatio);
        }

        return Bitmap.createScaledBitmap(originalSizeBitmap, dimensionsInGameWidth, dimensionsInGameHeight, false);
    }

    private static Rect getBitmapDimensionsInGame(Bitmap originalSizeBitmap, int gameWidth) {
        Rect rect = new Rect();

        // We use the background decoration as a reference because its width matches the game's
        float relativeWidth = (float) originalSizeBitmap.getWidth() / (float) backgroundDecorationOriginalWidth;
        int widthInGame = (int) (relativeWidth * (float) gameWidth);
        float aspectRatio = (float) originalSizeBitmap.getHeight() / (float) originalSizeBitmap.getWidth();
        int heightInGame = (int) ((float) widthInGame * aspectRatio);

        rect.set(0, 0, widthInGame, heightInGame);

        return rect;
    }

    private static int loadSoundFromResources(Context context, int resourceId) {
        return soundPool.load(context.getResources().openRawResourceFd(resourceId), 1);
    }

    private static void playSound(final int soundId, final boolean loop, final boolean stoppable) {
        // HACK Using handler thread to play sounds in secondary thread in order to avoid freezing the UI
        soundHandler.post(new Runnable() {
            @Override
            public void run() {
                int streamId = soundPool.play(soundId, 1, 1, 1, (loop ? -1 : 0), 0);
                if (stoppable && streamId != 0) {
                    if (!startingOrStoppingSound) {
                        startingOrStoppingSound = true;

                        try {
                            List<Integer> streamIds = soundStreamIds.get(soundId, null);
                            streamIds = streamIds == null ? new ArrayList<Integer>() : streamIds;
                            streamIds.add(streamId);

                            soundStreamIds.put(soundId, streamIds);
                        } catch (Exception e) {
                            // Do nothing
                        }

                        startingOrStoppingSound = false;
                    }
                }
            }
        });
    }

    private static void stopSound(int soundId) {
        if (!startingOrStoppingSound) {
            startingOrStoppingSound = true;

            try {
                List<Integer> streamIds = soundStreamIds.get(soundId, null);
                if (streamIds != null) {
                    for (int streamId : streamIds) {
                        soundPool.stop(streamId);
                    }

                    soundStreamIds.delete(soundId);
                }
            } catch (Exception e) {
                stopSound(soundId);
            }

            startingOrStoppingSound = false;
        }
    }
}
