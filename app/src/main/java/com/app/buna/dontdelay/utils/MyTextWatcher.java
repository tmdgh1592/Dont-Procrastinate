package com.app.buna.dontdelay.utils;

import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.buna.dontdelay.customView.BackPressEditText;
import com.app.buna.dontdelay.R;

public class MyTextWatcher implements TextWatcher {

    private int mTextCount = 0;

    static TextView inputCountText;
    private String text;
    private EditText editText;
    private BackPressEditText backPressEditText;

    private Resources res;
    private ImageView writeOkImageView;
    private LinearLayout writeOkLayout;

    public MyTextWatcher(TextView inputCountText, EditText editText) {

        this.inputCountText = inputCountText;
        this.editText = editText;
    }

    public MyTextWatcher(BackPressEditText backPressEditText) {
        this.backPressEditText = backPressEditText;
    }

    public void setResource(Resources res, ImageView imageView, LinearLayout linearLayout) {
        this.res = res;
        this.writeOkImageView = imageView;
        this.writeOkLayout = linearLayout;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int textCount, int end) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int textCount, int end) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        mTextCount = backPressEditText.getText().length();
        if (mTextCount == 0) {
            writeOkImageView.setImageResource(R.drawable.unable_write_ok_icon);
            writeOkLayout.setClickable(false);
        } else if (mTextCount > 0) {
            writeOkImageView.setImageResource(R.drawable.enable_write_ok_icon);
            writeOkLayout.setClickable(true);
        }

        /*text = String.valueOf(mTextCount) + " / 20";
        inputCountText.setText(text);*/
    }
}
