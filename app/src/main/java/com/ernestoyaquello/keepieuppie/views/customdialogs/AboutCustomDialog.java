package com.ernestoyaquello.keepieuppie.views.customdialogs;

import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.widget.AppCompatImageButton;
import android.view.View;

import com.ernestoyaquello.keepieuppie.R;
import com.ernestoyaquello.keepieuppie.views.base.BaseActivity;
import com.ernestoyaquello.keepieuppie.views.base.BaseCustomDialog;
import com.ernestoyaquello.keepieuppie.views.base.OnCustomDialogEventListener;

public class AboutCustomDialog extends BaseCustomDialog implements View.OnClickListener {

    public AboutCustomDialog(BaseActivity parentActivity, View dialogLayout,
                             OnCustomDialogEventListener eventListener) {
        super(parentActivity, dialogLayout, eventListener);
    }

    @Override
    public void onCreate(View dialogLayout) {
        AppCompatImageButton aboutBackButton = dialogLayout.findViewById(R.id.about_back_button);
        AppCompatImageButton aboutRateButton = dialogLayout.findViewById(R.id.about_rate_button);

        aboutBackButton.setOnClickListener(this);
        aboutRateButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.about_back_button:
                hideDialog();
                break;
            case R.id.about_rate_button:
                openAppInGooglePlay();
                break;
        }
    }

    private void openAppInGooglePlay() {
        final String appPackageName = parentActivity.getApplicationContext().getPackageName();
        try {
            parentActivity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException ex) {
            parentActivity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }
}