package me.ccrama.redditslide.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import me.ccrama.redditslide.Fragments.SettingsBackupFragment;
import me.ccrama.redditslide.R;


/**
 * Created by ccrama on 3/5/2015.
 */
public class SettingsBackup extends BaseActivityAnim {

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        fragment.onActivityResult(requestCode, resultCode, data);
    }

}