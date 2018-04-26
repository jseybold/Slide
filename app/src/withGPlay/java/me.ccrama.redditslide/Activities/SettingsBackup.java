package me.ccrama.redditslide.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import me.ccrama.redditslide.Fragments.SettingsBackupFragment;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.SettingValues;


/**
 * Created by ccrama on 3/5/2015.
 */
public class SettingsBackup extends BaseActivityAnim
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private SettingsBackupFragment fragment = new SettingsBackupFragment(this);

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyColorTheme();
        setContentView(R.layout.activity_settings_sync);
        setupAppBar(R.id.toolbar, R.string.settings_title_backup, true, true);

        ((ViewGroup) findViewById(R.id.settings_sync)).addView(
                getLayoutInflater().inflate(R.layout.activity_settings_sync_child, null));

        fragment.Bind();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (SettingValues.tabletUI) fragment.mGoogleApiClient.connect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.println(Log.DEBUG, "SETTINGSBACKUP", "HELLO WORLD! 1");

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.println(Log.DEBUG, "SETTINGSBACKUP", "HELLO WORLD! 2");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.println(Log.DEBUG, "SETTINGSBACKUP", "HELLO WORLD! 3");
    }
}