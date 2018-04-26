package me.ccrama.redditslide.Fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import me.ccrama.redditslide.R;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.util.FileUtil;

public class SettingsBackupFragment {

    private Activity context;

    public MaterialDialog progress;
    String         title;
    File           file;

    public SettingsBackupFragment(Activity context) {
        this(context, false);
    }

    public SettingsBackupFragment(Activity context, boolean autoBind) {
        this.context = context;
        if (autoBind) Bind();
    }

    public void Bind() {
        if (SettingValues.tabletUI) {

            context.findViewById(R.id.backfile).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialogWrapper.Builder(context).setTitle(
                            R.string.settings_backup_include_personal_title)
                            .setMessage(R.string.settings_backup_include_personal_text)
                            .setPositiveButton(R.string.btn_yes,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            backupToDir(false);
                                        }
                                    })
                            .setNegativeButton(R.string.btn_no,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            backupToDir(true);
                                        }
                                    })
                            .setNeutralButton(R.string.btn_cancel, null)
                            .setCancelable(false)
                            .show();
                }
            });


            context.findViewById(R.id.restorefile).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("file/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    String[] mimeTypes = {"text/plain"};
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                    context.startActivityForResult(intent, 42);

                }
            });
        } else {
            new AlertDialogWrapper.Builder(context).setTitle("Settings Backup is a Pro feature")
                    .setMessage(R.string.pro_upgrade_msg)
                    .setPositiveButton(R.string.btn_yes_exclaim,

                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    try {
                                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
                                                "market://details?id=me.ccrama.slideforreddittabletuiunlock")));
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
                                                "http://play.google.com/store/apps/details?id=me.ccrama.slideforreddittabletuiunlock")));
                                    }
                                }
                            })
                    .setNegativeButton(R.string.btn_no_danks,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    context.finish();
                                }
                            })
                    .setCancelable(false)
                    .show();
        }
    }

    public static void close(Closeable stream) {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (IOException ignored) {
        }
    }

    public void backupToDir(final boolean personal) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                progress = new MaterialDialog.Builder(context).cancelable(false)
                        .title(R.string.backup_backing_up)
                        .progress(false, 40)
                        .cancelable(false)
                        .build();
                progress.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                File prefsdir = new File(context.getApplicationInfo().dataDir, "shared_prefs");

                if (prefsdir.exists() && prefsdir.isDirectory()) {
                    String[] list = prefsdir.list();

                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                            .mkdirs();

                    File backedup = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS)
                            + File.separator
                            + "Slide"
                            + new SimpleDateFormat("-yyyy-MM-dd-HH-mm-ss").format(
                            Calendar.getInstance().getTime())
                            + (!personal ? "-personal" : "")
                            + ".txt");
                    file = backedup;
                    FileWriter fw = null;
                    try {
                        backedup.createNewFile();
                        fw = new FileWriter(backedup);
                        fw.write("Slide_backupEND>");
                        for (String s : list) {

                            if (!s.contains("cache") && !s.contains("ion-cookies") && !s.contains(
                                    "albums") && !s.contains("com.google") && (((personal
                                    && !s.contains("SUBSNEW")
                                    && !s.contains("appRestart")
                                    && !s.contains("STACKTRACE")
                                    && !s.contains("AUTH")
                                    && !s.contains("TAGS")
                                    && !s.contains("SEEN")
                                    && !s.contains("HIDDEN")
                                    && !s.contains("HIDDEN_POSTS"))) || !personal)) {
                                FileReader fr = null;
                                try {
                                    fr = new FileReader(new File(prefsdir + File.separator + s));
                                    int c = fr.read();
                                    fw.write("<START" + new File(s).getName() + ">");
                                    while (c != -1) {
                                        fw.write(c);
                                        c = fr.read();
                                    }
                                    fw.write("END>");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    close(fr);
                                }
                            }

                        }
                        return null;
                    } catch (Exception e) {
                        e.printStackTrace();
                        //todo error
                    } finally {
                        close(fw);
                    }

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                progress.dismiss();
                new AlertDialogWrapper.Builder(context).setTitle(
                        R.string.backup_complete)
                        .setMessage(R.string.backup_saved_downloads)
                        .setPositiveButton(R.string.btn_view,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = FileUtil.getFileIntent(file,
                                                new Intent(Intent.ACTION_VIEW),
                                                context);
                                        if (intent.resolveActivityInfo(context.getPackageManager(), 0)
                                                != null) {
                                            context.startActivity(Intent.createChooser(intent,
                                                    context.getString(R.string.settings_backup_view)));
                                        } else {
                                            Snackbar s =
                                                    Snackbar.make(context.findViewById(R.id.restorefile),
                                                            context.getString(
                                                                    R.string.settings_backup_err_no_explorer,
                                                                    file.getAbsolutePath() + file),
                                                            Snackbar.LENGTH_INDEFINITE);
                                            View view = s.getView();
                                            TextView tv = (TextView) view.findViewById(
                                                    android.support.design.R.id.snackbar_text);
                                            tv.setTextColor(Color.WHITE);
                                            s.show();
                                        }
                                    }
                                })
                        .setNegativeButton(R.string.btn_close, null)
                        .setCancelable(false)
                        .show();
            }
        }.execute();

    }
}
