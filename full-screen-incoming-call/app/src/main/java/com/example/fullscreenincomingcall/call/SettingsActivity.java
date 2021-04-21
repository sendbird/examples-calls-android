package com.example.fullscreenincomingcall.call;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.fullscreenincomingcall.R;
import com.example.fullscreenincomingcall.utils.ActivityUtils;
import com.example.fullscreenincomingcall.utils.AuthenticationUtils;

public class SettingsActivity extends AppCompatActivity {

    Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mContext = this;

        Toolbar settingsToolBar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(settingsToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        findViewById(R.id.linear_layout_sign_out).setOnClickListener(view1 -> {
            AuthenticationUtils.deauthenticate(mContext, isSuccess -> {
                if (mContext != null) {
                    ActivityUtils.startSignInActivityAndFinish(this);
                }
            });
        });

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}