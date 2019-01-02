package com.ernestoyaquello.keepieuppie.views.customdialogs;

import androidx.appcompat.widget.AppCompatImageButton;
import android.view.View;

import com.ernestoyaquello.keepieuppie.views.activities.MainMenuActivity;
import com.ernestoyaquello.keepieuppie.R;
import com.ernestoyaquello.keepieuppie.views.base.BaseActivity;
import com.ernestoyaquello.keepieuppie.views.base.BaseCustomDialog;
import com.ernestoyaquello.keepieuppie.views.base.OnCustomDialogEventListener;

public class ExitConfirmationCustomDialog extends BaseCustomDialog implements View.OnClickListener {

    public ExitConfirmationCustomDialog(BaseActivity parentActivity, View dialogLayout,
                                        OnCustomDialogEventListener eventListener) {
        super(parentActivity, dialogLayout, eventListener);
    }

    @Override
    public void onCreate(View dialogLayout) {
        AppCompatImageButton exitConfirmationDialogExitButton = dialogLayout.findViewById(R.id.exit_confirmation_exit_button);
        AppCompatImageButton exitConfirmationDialogStayButton = dialogLayout.findViewById(R.id.exit_confirmation_stay_button);

        exitConfirmationDialogExitButton.setOnClickListener(this);
        exitConfirmationDialogStayButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.exit_confirmation_stay_button:
                parentActivity.hideLastOpenedDialog();
                break;
            case R.id.exit_confirmation_exit_button:
                if (parentActivity instanceof MainMenuActivity) {
                    ((MainMenuActivity) parentActivity).finishApplication();
                }
                break;
        }
    }
}
