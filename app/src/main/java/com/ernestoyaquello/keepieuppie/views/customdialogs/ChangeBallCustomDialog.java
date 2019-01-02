package com.ernestoyaquello.keepieuppie.views.customdialogs;

import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatImageButton;
import android.view.View;
import android.widget.ImageView;

import com.ernestoyaquello.keepieuppie.R;
import com.ernestoyaquello.keepieuppie.views.base.BaseActivity;
import com.ernestoyaquello.keepieuppie.views.base.BaseCustomDialog;
import com.ernestoyaquello.keepieuppie.views.base.OnCustomDialogEventListener;
import com.ernestoyaquello.keepieuppie.storage.local.utils.DataStorageHelper;
import com.ernestoyaquello.keepieuppie.storage.local.models.BallType;

public class ChangeBallCustomDialog extends BaseCustomDialog implements View.OnClickListener {
    private ImageView ballSelectionTop;
    private AppCompatImageButton backButton;
    private AppCompatImageButton standardBallButton;
    private AppCompatImageButton bronzeBallButton;
    private AppCompatImageButton silverBallButton;
    private AppCompatImageButton goldBallButton;
    private AppCompatImageButton titaniumBallButton;
    private AppCompatImageButton diamondBallButton;

    public ChangeBallCustomDialog(BaseActivity parentActivity, View dialogLayout,
                                  OnCustomDialogEventListener eventListener) {
        super(parentActivity, dialogLayout, eventListener);
    }

    @Override
    public void onCreate(View dialogLayout) {
        ballSelectionTop = dialogLayout.findViewById(R.id.ball_selection_top);
        standardBallButton = dialogLayout.findViewById(R.id.ball_standard);
        bronzeBallButton = dialogLayout.findViewById(R.id.ball_bronze);
        silverBallButton = dialogLayout.findViewById(R.id.ball_silver);
        goldBallButton = dialogLayout.findViewById(R.id.ball_gold);
        titaniumBallButton = dialogLayout.findViewById(R.id.ball_titanium);
        diamondBallButton = dialogLayout.findViewById(R.id.ball_diamond);
        backButton = dialogLayout.findViewById(R.id.back_button);

        standardBallButton.setOnClickListener(this);
        bronzeBallButton.setOnClickListener(this);
        silverBallButton.setOnClickListener(this);
        goldBallButton.setOnClickListener(this);
        titaniumBallButton.setOnClickListener(this);
        diamondBallButton.setOnClickListener(this);
        backButton.setOnClickListener(this);

        setInitialBallTypesValues();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ball_standard:
                activateBallType(BallType.Standard, true);
                break;
            case R.id.ball_bronze:
                activateBallType(BallType.Bronze, true);
                break;
            case R.id.ball_silver:
                activateBallType(BallType.Silver, true);
                break;
            case R.id.ball_gold:
                activateBallType(BallType.Gold, true);
                break;
            case R.id.ball_titanium:
                activateBallType(BallType.Titanium, true);
                break;
            case R.id.ball_diamond:
                activateBallType(BallType.Diamond, true);
                break;
            case R.id.back_button:
                hideDialog();
                break;
        }
    }

    private void setInitialBallTypesValues() {
        int userScore = DataStorageHelper.getUserScore(parentActivity);
        BallType userSelectedBallType = DataStorageHelper.getUserSelectedBall(parentActivity);

        standardBallButton.setEnabled(true);
        bronzeBallButton.setEnabled(userScore >= 25);
        silverBallButton.setEnabled(userScore >= 50);
        goldBallButton.setEnabled(userScore >= 100);
        titaniumBallButton.setEnabled(userScore >= 200);
        diamondBallButton.setEnabled(userScore >= 500);

        activateBallType(userSelectedBallType, false);
    }

    private void deactivateAllBallTypes() {
        standardBallButton.setActivated(false);
        bronzeBallButton.setActivated(false);
        silverBallButton.setActivated(false);
        goldBallButton.setActivated(false);
        titaniumBallButton.setActivated(false);
        diamondBallButton.setActivated(false);
    }

    private void activateBallType(BallType ballType, boolean storeData) {
        AppCompatImageButton selectedButton = standardBallButton;
        int topDrawableId = R.drawable.ball_selection_top_standard;

        switch (ballType) {
            case Bronze:
                selectedButton = bronzeBallButton;
                topDrawableId = R.drawable.ball_selection_top_bronze;
                break;
            case Silver:
                selectedButton = silverBallButton;
                topDrawableId = R.drawable.ball_selection_top_silver;
                break;
            case Gold:
                selectedButton = goldBallButton;
                topDrawableId = R.drawable.ball_selection_top_gold;
                break;
            case Titanium:
                selectedButton = titaniumBallButton;
                topDrawableId = R.drawable.ball_selection_top_titanium;
                break;
            case Diamond:
                selectedButton = diamondBallButton;
                topDrawableId = R.drawable.ball_selection_top_diamond;
                break;
        }

        if (selectedButton.isEnabled()) {
            deactivateAllBallTypes();
            selectedButton.setActivated(true);
            ballSelectionTop.setImageDrawable(ContextCompat.getDrawable(parentActivity,
                    topDrawableId));

            if (storeData) {
                DataStorageHelper.setUserSelectedBall(parentActivity, ballType);
            }
        }
    }
}
