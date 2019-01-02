package com.ernestoyaquello.keepieuppie.views.customdialogs;

import androidx.appcompat.widget.AppCompatImageButton;
import android.view.View;
import com.ernestoyaquello.keepieuppie.R;
import com.ernestoyaquello.keepieuppie.views.base.BaseActivity;
import com.ernestoyaquello.keepieuppie.views.base.BaseCustomDialog;
import com.ernestoyaquello.keepieuppie.views.base.OnCustomDialogEventListener;

public class ShareScoreCustomDialog extends BaseCustomDialog implements View.OnClickListener {

    public ShareScoreCustomDialog(BaseActivity parentActivity, View dialogLayout, OnCustomDialogEventListener eventListener) {
        super(parentActivity, dialogLayout, eventListener);
    }

    @Override
    public void onCreate(View dialogLayout) {
        AppCompatImageButton backButton = dialogLayout.findViewById(R.id.back_button);
        backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_button:
                hideDialog();
                break;
        }
    }
}
