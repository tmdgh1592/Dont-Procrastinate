package com.app.buna.dontdelay.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.app.buna.dontdelay.R;

public class MemoActivity extends AppCompatActivity {

    private EditText memoEditText;
    private String memoText;
    private String newMemoText;

    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;

    final Intent sendIntent = new Intent();
    final Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);
        Intent recieveIntent = getIntent();

        memoEditText = findViewById(R.id.memo_edit_text);

        memoText = recieveIntent.getStringExtra("memo");
        memoEditText.setText(memoText);
    }

    @Override
    public void onBackPressed() {

        newMemoText = memoEditText.getText().toString();

        try {   // newMemoText : null 판단 try catch
            if (memoText.equals(newMemoText) && (memoText.isEmpty() || memoText.equals(newMemoText))) {     // 메모 내용 변경을 하지 않은 경우
                bundle.putString("memo", memoText);
                sendIntent.putExtras(bundle);
                setResult(RESULT_OK, sendIntent);
                finish();
            } else if (!memoText.equals(newMemoText)) {                                                     // 메모 내용을 변경한 경우
                createAlertDialog();
                alertDialog.show();
            } else {
                Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
        }catch (NullPointerException e){
            Log.d("NULL", "onBackPressed: " + e.getMessage());
            Toast.makeText(this, getString(R.string.nullError), Toast.LENGTH_SHORT).show();
            finish();
        }
        //super.onBackPressed();
    }

    private void createAlertDialog() {
        builder = new AlertDialog.Builder(this)
                .setMessage("작성을 마치시겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        newMemoText = memoEditText.getText().toString();
                        bundle.putString("memo", newMemoText);
                        sendIntent.putExtras(bundle);
                        setResult(RESULT_OK, sendIntent);
                        finish();
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();
                    }
                });
        alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
            }
        });
    }
}
