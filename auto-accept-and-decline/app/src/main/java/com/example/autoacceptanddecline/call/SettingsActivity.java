package com.example.autoacceptanddecline.call;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.autoacceptanddecline.R;
import com.example.autoacceptanddecline.utils.ActivityUtils;
import com.example.autoacceptanddecline.utils.AuthenticationUtils;
import com.example.autoacceptanddecline.utils.PrefUtils;

public class SettingsActivity extends AppCompatActivity {

    Context mContext;
    ToggleButton autoAccept;
    ToggleButton autoDecline;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mContext = this;

        Toolbar settingsToolBar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(settingsToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setUpToggleButtons();

        findViewById(R.id.linear_layout_sign_out).setOnClickListener(view1 -> {
            AuthenticationUtils.deauthenticate(mContext, isSuccess -> {
                if (mContext != null) {
                    ActivityUtils.startSignInActivityAndFinish(this);
                }
            });
            autoAccept.setChecked(false);
            autoDecline.setChecked(false);
        });

    }

    public void setUpToggleButtons(){
        autoAccept = findViewById(R.id.accept_toggle_button);
        autoDecline = findViewById(R.id.decline_toggle_button);
        autoAccept.setChecked(PrefUtils.getAutoAccept(mContext));
        autoDecline.setChecked(PrefUtils.getAutoDecline(mContext));
        autoAccept.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PrefUtils.setAutoAccept(mContext, isChecked);
                Log.d("SettingsActivity","auto accept: "+ isChecked);
            }
        });
        autoDecline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PrefUtils.setAutoDecline(mContext, isChecked);
                Log.d("SettingsActivity","auto decline: "+ isChecked);
            }
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
