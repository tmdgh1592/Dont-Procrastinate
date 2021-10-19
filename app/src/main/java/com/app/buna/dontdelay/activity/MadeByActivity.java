package com.app.buna.dontdelay.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.app.buna.dontdelay.R;

public class MadeByActivity extends AppCompatActivity {

    private LinearLayout requestLayout;
    private ImageView goBackImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_made_by);
        requestLayout = findViewById(R.id.request_layout);
        requestLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("mailto:"+getString(R.string.developer_email));
                startActivity(new Intent(Intent.ACTION_SENDTO, uri));
            }
        });
        goBackImageView = findViewById(R.id.go_back_image_view);
        goBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
