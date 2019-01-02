package com.ernestoyaquello.keepieuppie.views.base;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ernestoyaquello.keepieuppie.R;
import com.ernestoyaquello.keepieuppie.storage.local.utils.DataStorageHelper;
import com.ernestoyaquello.keepieuppie.views.game.utils.Assets;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class BaseActivity extends AppCompatActivity {
    public boolean isDialogVisible;
    public boolean isDialogVisibilityChanging;
    public BaseCustomDialog lastOpenedDialog;

    protected static MediaPlayer player;
    protected RelativeLayout fullScreenContent;
    protected FrameLayout gameContent;
    protected View contentLayout;
    protected boolean useBackgroundMusicInThisActivity;
    protected boolean isShowingCloudsAnimation;
    protected static boolean isStartingAnotherActivity;

    protected ImageView cloud;
    protected ImageView secondCloud;
    protected View clouds;
    private final int idContentLayout;

    private AdView adView;
    private FrameLayout adViewContainer;
    private ConsentForm consentForm;

    private static Assets assets;

    private static final Object musicLock = new Object();

    protected BaseActivity(int idContentLayout) {
        this.idContentLayout = idContentLayout;
        this.useBackgroundMusicInThisActivity = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isStartingAnotherActivity = false;

        setContentView(R.layout.activity_base);

        setUpAdvertAndLoadIfAble();
        loadAssets();

        gameContent = findViewById(R.id.game_content);
        fullScreenContent = findViewById(R.id.fullscreen_content);
        clouds = findViewById(R.id.bg_clouds);
        cloud = findViewById(R.id.cloud);
        secondCloud = findViewById(R.id.cloud2);

        hideStatusAndActionBar();

        isShowingCloudsAnimation = false;
        startCloudsAnimation();

        startMusicIfNecessary();

        setActivityContentView(savedInstanceState);
    }

    public void hideLastOpenedDialog() {
        lastOpenedDialog.hideDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();

        startCloudsAnimation();
        resumeMusicIfNecessary(false);
        hideStatusAndActionBar();
    }

    @Override
    protected void onPause() {
        super.onPause();

        pauseMusic();
    }

    @Override
    protected void onStop() {
        super.onStop();

        isShowingCloudsAnimation = false;

        if (cloud != null) {
            cloud.clearAnimation();
        }

        if (secondCloud != null) {
            secondCloud.clearAnimation();
        }

        if (clouds != null){
            clouds.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (lastOpenedDialog != null) {
            lastOpenedDialog.onDestroy();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        hideStatusAndActionBar();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        isStartingAnotherActivity = true;
        super.startActivityForResult(intent, requestCode);

        overridePendingTransition(R.anim.fade_in_from_black, R.anim.fade_out_to_black);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        isStartingAnotherActivity = false;
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void finish() {
        if (isDialogVisible && !isDialogVisibilityChanging) {
            hideLastOpenedDialog();
        } else if (!isDialogVisibilityChanging) {
            doFinish();
        }
    }

    public void finishActivity() {
        doFinish();
    }

    public void finishActivity(Intent intent) {
        setResult(RESULT_OK, intent);
        doFinish();
    }

    public void doFinish() {
        isStartingAnotherActivity = true;
        super.finish();
        overridePendingTransition(R.anim.fade_in_from_black, R.anim.fade_out_to_black);
    }

    private void loadAssets() {
        // Storing a static reference to an instance of the class so the assets class instance won't
        // get removed from memory, which will hopefully keep the assets in memory as well
        assets = Assets.getInstance();
        Assets.loadNonBitmapAssets(getApplicationContext());
    }

    protected void setUpAdvertAndLoadIfAble() {
        MobileAds.initialize(this, getString(R.string.adMobAppId));

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float screenWidthInDp = displayMetrics.widthPixels / displayMetrics.density;

        AdSize adSize = AdSize.BANNER;
        if (screenWidthInDp >= 468 && screenWidthInDp < 728) {
            adSize = AdSize.FULL_BANNER;
        } else if (screenWidthInDp >= 728) {
            adSize = AdSize.LEADERBOARD;
        }

        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.adMobBannerUnitId));
        adView.setAdSize(adSize);

        adViewContainer = findViewById(R.id.bottom);
        RelativeLayout.LayoutParams adViewContainerLayoutParams =
                (RelativeLayout.LayoutParams) adViewContainer.getLayoutParams();
        adViewContainerLayoutParams.height = adSize.getHeightInPixels(this);
        adViewContainer.setLayoutParams(adViewContainerLayoutParams);

        adViewContainer.addView(adView);

        final Context context = this;
        ConsentInformation consentInformation = ConsentInformation.getInstance(this);
        String[] publisherIds = {getString(R.string.adMobPublisherId)};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                boolean isRequestLocationInEuropeanUnionOrUnknown =
                        ConsentInformation.getInstance(context).isRequestLocationInEeaOrUnknown();

                if (!isRequestLocationInEuropeanUnionOrUnknown
                        || consentStatus == ConsentStatus.PERSONALIZED
                        || consentStatus == ConsentStatus.NON_PERSONALIZED ) {

                    doLoadAdvert(consentStatus);
                } else {
                    launchConsentFormAndLoadAdvertIfAble();
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                launchConsentFormAndLoadAdvertIfAble();
            }
        });
    }

    private void doLoadAdvert(ConsentStatus consentStatus) {
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();

        String[] adMobTestDevicesIds = getResources().getString(R.string.adMobTestDevicesIds).split(",");
        for (String adMobTestDeviceId : adMobTestDevicesIds) {
            adRequestBuilder.addTestDevice(adMobTestDeviceId);
        }

        if (consentStatus == ConsentStatus.NON_PERSONALIZED) {
            Bundle extras = new Bundle();
            extras.putString("npa", "1");
            adRequestBuilder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
        }

        AdRequest adRequest = adRequestBuilder.build();
        adView.loadAd(adRequest);
    }

    private void launchConsentFormAndLoadAdvertIfAble() {
        URL privacyUrl = null;
        try {
            privacyUrl = new URL(getString(R.string.app_privacy_policy_url));
        } catch (MalformedURLException e) {
            // Do nothing here, I don't care
        }

        consentForm = new ConsentForm.Builder(this, privacyUrl)
                .withListener(new ConsentFormListener() {
                    @Override
                    public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                        doLoadAdvert(consentStatus);
                    }

                    @Override
                    public void onConsentFormLoaded() {
                        showContentForm();
                    }
                })
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
                .build();
        consentForm.load();
    }

    private void showContentForm() {
        if (consentForm != null && !consentForm.isShowing()) {
            consentForm.show();
        }
    }

    protected void hideStatusAndActionBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        decorView.setSystemUiVisibility(uiOptions);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    protected void resumeMusicIfNecessary(boolean forceResume) {
        if (!useBackgroundMusicInThisActivity) {
            pauseMusic();
            releaseMediaPlayer();
        } else {
            boolean isMusicActive = DataStorageHelper.isMusicActivated(this);
            if (isMusicActive || forceResume) {
                if (player != null) {
                    synchronized (musicLock) {
                        if (!player.isPlaying()) {
                            player.start();
                        }
                    }
                } else {
                    InitialiseMusicTask initialisationTask = new InitialiseMusicTask(this, useBackgroundMusicInThisActivity);
                    initialisationTask.execute(null, null, null);
                }
            }
        }
    }

    protected void pauseMusic() {
        synchronized (musicLock) {
            if (player != null && player.isPlaying() && !isStartingAnotherActivity) {
                player.pause();
            }
        }
    }

    protected void releaseMediaPlayer() {
        synchronized (musicLock) {
            if (player != null) {
                if (player.isPlaying()) {
                    player.pause();
                }
                player.release();
                player = null;
            }
        }
    }

    protected void startCloudsAnimation() {
        if (clouds != null){
            clouds.setVisibility(View.VISIBLE);
        }

        if (!isShowingCloudsAnimation) {
            isShowingCloudsAnimation = true;

            Animation cloudAnimationRepeat = AnimationUtils.loadAnimation(this, R.anim.cloud_repeat);
            final Animation secondCloudAnimationRepeat = AnimationUtils.loadAnimation(this, R.anim.cloud_repeat);

            if (cloud != null && cloudAnimationRepeat != null) {
                cloudAnimationRepeat.setInterpolator(this, android.R.anim.linear_interpolator);
                cloud.startAnimation(cloudAnimationRepeat);
            }

            if (secondCloud != null && secondCloudAnimationRepeat != null) {
                secondCloudAnimationRepeat.setStartOffset(-secondCloudAnimationRepeat.getDuration() / 2);
                secondCloudAnimationRepeat.setInterpolator(this, android.R.anim.linear_interpolator);
                secondCloudAnimationRepeat.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        secondCloudAnimationRepeat.setStartOffset(0);
                    }
                });

                secondCloud.startAnimation(secondCloudAnimationRepeat);
            }
        }
    }

    private void startMusicIfNecessary() {
        resumeMusicIfNecessary(false);
    }

    private void setActivityContentView(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(this);
        contentLayout = inflater.inflate(idContentLayout, gameContent, true);

        onActivityCreated(savedInstanceState);
    }

    private static class InitialiseMusicTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<Context> contextReference;
        private final boolean useBackgroundMusicInThisActivity;

        InitialiseMusicTask(Context context, boolean useBackgroundMusicInThisActivity) {
            this.contextReference = new WeakReference<>(context);
            this.useBackgroundMusicInThisActivity = useBackgroundMusicInThisActivity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Create media player for music
            synchronized (musicLock) {
                if (player == null) {
                    player = MediaPlayer.create(contextReference.get(), R.raw.music);
                    if (player != null && useBackgroundMusicInThisActivity) {
                        player.setLooping(true); // Set looping
                        player.setVolume(1.0f, 1.0f);
                        player.start();
                    }
                }
            }

            contextReference.clear();

            return null;
        }
    }

    /**
     * Replaces onCreate() in all the activity classes of this application.
     *
     * @param savedInstanceState The saved instance state
     */
    protected abstract void onActivityCreated(Bundle savedInstanceState);
}