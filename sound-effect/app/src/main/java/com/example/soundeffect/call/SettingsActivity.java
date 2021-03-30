package com.example.soundeffect.call;

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

import com.example.soundeffect.R;
import com.example.soundeffect.utils.ActivityUtils;
import com.example.soundeffect.utils.AuthenticationUtils;
import com.example.soundeffect.utils.PrefUtils;
import com.sendbird.calls.SendBirdCall;

public class SettingsActivity extends AppCompatActivity {

    Context mContext;
    ToggleButton dialing;
    ToggleButton reconnected;
    ToggleButton reconnecting;
    ToggleButton ringing;

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
        });

    }

    public void setUpToggleButtons(){
        dialing = findViewById(R.id.dialing_sound_toggle_button);
        reconnected = findViewById(R.id.reconnected_sound_toggle_button);
        reconnecting = findViewById(R.id.reconnecting_sound_toggle_button);
        ringing = findViewById(R.id.ringing_sound_toggle_button);

        dialing.setChecked(PrefUtils.getDialing(mContext));
        reconnected.setChecked(PrefUtils.getReconnected(mContext));
        reconnecting.setChecked(PrefUtils.getReconnecting(mContext));
        ringing.setChecked(PrefUtils.getRinging(mContext));

        dialing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PrefUtils.setDialing(mContext, isChecked);
                if(isChecked){
                    SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.DIALING, R.raw.dialing);
                }else{
                    SendBirdCall.Options.removeDirectCallSound(SendBirdCall.SoundType.DIALING);
                }
            }
        });
        reconnected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PrefUtils.setReconnected(mContext, isChecked);
                if(isChecked){
                    SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.RECONNECTED, R.raw.reconnected);
                }else{
                    SendBirdCall.Options.removeDirectCallSound(SendBirdCall.SoundType.RECONNECTED);
                }
            }
        });
        reconnecting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PrefUtils.setReconnecting(mContext, isChecked);
                if(isChecked){
                    SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.RECONNECTING, R.raw.reconnecting);
                }else{
                    SendBirdCall.Options.removeDirectCallSound(SendBirdCall.SoundType.RECONNECTING);
                }
            }
        });
        ringing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PrefUtils.setRinging(mContext, isChecked);
                if(isChecked){
                    SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.RINGING, R.raw.ringing);
                }else{
                    SendBirdCall.Options.removeDirectCallSound(SendBirdCall.SoundType.RINGING);
                }
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
