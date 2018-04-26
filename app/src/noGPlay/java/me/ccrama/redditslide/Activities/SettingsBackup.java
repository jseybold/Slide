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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import me.ccrama.redditslide.Fragments.SettingsBackupFragment;
import me.ccrama.redditslide.R;
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

        ((ViewGroup) findViewById(R.id.settings_sync)).addView(
                getLayoutInflater().inflate(R.layout.activity_settings_sync_child, null));

        fragment.Bind();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 42) {
            fragment.progress =
                    new MaterialDialog.Builder(SettingsBackup.this).title(R.string.backup_restoring)
                            .content(R.string.misc_please_wait)
                            .cancelable(false)
                            .progress(true, 1)
                            .build();
            fragment.progress.show();

            if (data != null) {
                Uri fileUri = data.getData();
                Log.v(LogUtil.getTag(), "WORKED! " + fileUri.toString());

                StringWriter fw = new StringWriter();
                try {
                    InputStream is = getContentResolver().openInputStream(fileUri);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    int c = reader.read();
                    while (c != -1) {
                        fw.write(c);
                        c = reader.read();
                    }
                    String read = fw.toString();

                    if (read.contains("Slide_backupEND>")) {

                        String[] files = read.split("END>");
                        fragment.progress.dismiss();
                        fragment.progress = new MaterialDialog.Builder(SettingsBackup.this).title(
                                R.string.backup_restoring)
                                .progress(false, files.length - 1)
                                .build();
                        fragment.progress.show();
                        for (int i = 1; i < files.length; i++) {
                            String innerFile = files[i];
                            String t = innerFile.substring(6, innerFile.indexOf(">"));
                            innerFile = innerFile.substring(innerFile.indexOf(">") + 1,
                                    innerFile.length());

                            File newF = new File(getApplicationInfo().dataDir
                                    + File.separator
                                    + "shared_prefs"
                                    + File.separator
                                    + t);
                            Log.v(LogUtil.getTag(), "WRITING TO " + newF.getAbsolutePath());
                            try {
                                FileWriter newfw = new FileWriter(newF);
                                BufferedWriter bw = new BufferedWriter(newfw);
                                bw.write(innerFile);
                                bw.close();
                                fragment.progress.setProgress(fragment.progress.getCurrentProgress() + 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                        new AlertDialogWrapper.Builder(SettingsBackup.this).setCancelable(false)
                                .setTitle(R.string.backup_restore_settings)
                                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        ProcessPhoenix.triggerRebirth(SettingsBackup.this);

                                    }
                                })
                                .setMessage(R.string.backup_restarting)
                                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        ProcessPhoenix.triggerRebirth(SettingsBackup.this);
                                    }
                                })
                                .setPositiveButton(R.string.btn_ok,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                ProcessPhoenix.triggerRebirth(SettingsBackup.this);
                                            }
                                        })
                                .setCancelable(false)
                                .show();

                    } else {
                        fragment.progress.hide();
                        new AlertDialogWrapper.Builder(SettingsBackup.this).setTitle(
                                R.string.err_not_valid_backup)
                                .setMessage(R.string.err_not_valid_backup_msg)
                                .setPositiveButton(R.string.btn_ok, null)
                                .setCancelable(false)
                                .show();
                    }
                } catch (Exception e) {
                    fragment.progress.hide();
                    e.printStackTrace();
                    new AlertDialogWrapper.Builder(SettingsBackup.this).setTitle(
                            R.string.err_file_not_found)
                            .setMessage(R.string.err_file_not_found_msg)
                            .setPositiveButton(R.string.btn_ok, null)
                            .show();
                }

            } else {
                fragment.progress.dismiss();
                new AlertDialogWrapper.Builder(SettingsBackup.this).setTitle(
                        R.string.err_file_not_found)
                        .setMessage(R.string.err_file_not_found_msg)
                        .setPositiveButton(R.string.btn_ok, null)
                        .setCancelable(false)
                        .show();
            }

        }
    }

}
