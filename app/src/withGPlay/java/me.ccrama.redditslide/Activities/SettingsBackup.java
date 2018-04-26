package me.ccrama.redditslide.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.processphoenix.ProcessPhoenix;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import me.ccrama.redditslide.Fragments.SettingsBackupFragment;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.util.LogUtil;


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

//        ((ViewGroup) findViewById(R.id.settings_sync)).addView(
//                getLayoutInflater().inflate(R.layout.activity_settings_sync_child, null));

        fragment.Bind();
    }

}