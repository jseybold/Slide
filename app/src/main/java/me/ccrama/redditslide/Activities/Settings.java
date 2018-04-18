package me.ccrama.redditslide.Activities;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.google.common.base.Strings;


import java.io.File;

import me.ccrama.redditslide.Authentication;
import me.ccrama.redditslide.DragSort.ReorderSubreddits;
import me.ccrama.redditslide.FDroid;
import me.ccrama.redditslide.Fragments.FolderChooserDialogCreate;
import me.ccrama.redditslide.Fragments.SettingsGeneralFragment;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.Visuals.Palette;
import me.ccrama.redditslide.util.NetworkUtil;
import me.ccrama.redditslide.util.OnSingleClickListener;


/**
 * Created by ccrama on 3/5/2015.
 */
public class Settings extends BaseActivity implements FolderChooserDialogCreate.FolderCallback{
    private final static int RESTART_SETTINGS_RESULT = 2;
    private       int                                                scrollY;
    private       SharedPreferences.OnSharedPreferenceChangeListener prefsListener;
    public static boolean                                            changed;
            //whether or not a Setting was changed

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESTART_SETTINGS_RESULT) {
            Intent i = new Intent(Settings.this, Settings.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            i.putExtra("position", scrollY);
            startActivity(i);
            overridePendingTransition(0, 0);
            finish();
            overridePendingTransition(0, 0);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyColorTheme();
        setContentView(R.layout.activity_settings);
        setupAppBar(R.id.toolbar, R.string.title_settings, true, true);

        LayoutInflater inflater = getLayoutInflater();
        LinearLayout parent = (LinearLayout) findViewById(R.id.settings_parent);
        LinearLayout child = (LinearLayout) inflater.inflate(R.layout.activity_settings_child, null);
        parent.addView(child);

        onChildCreate();
    }

    private void onChildCreate() {

        SettingValues.expandedSettings = true;
        setSettingItems();

        final ScrollView mScrollView = ((ScrollView) findViewById(R.id.base));

        prefsListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Settings.changed = true;
            }
        };

        SettingValues.prefs.registerOnSharedPreferenceChangeListener(prefsListener);

        mScrollView.post(new Runnable() {

            @Override
            public void run() {
                ViewTreeObserver observer = mScrollView.getViewTreeObserver();
                if (getIntent().hasExtra("position")) {
                    mScrollView.scrollTo(0, getIntent().getIntExtra("position", 0));
                }
                observer.addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

                    @Override
                    public void onScrollChanged() {
                        scrollY = mScrollView.getScrollY();
                    }
                });
            }
        });
    }

    private String loopViews(ViewGroup view, String text) {
        for (int i = 0; i < view.getChildCount(); i++) {
            View v = view.getChildAt(i);
            if (v instanceof TextView) {
                if (((TextView) v).getText().toString().toLowerCase().contains(text)) {
                    return ((TextView) v).getText().toString();
                }
            } else if (v instanceof ViewGroup) {
                String ret = this.loopViews((ViewGroup) v, text);
                if (!Strings.isNullOrEmpty(ret)) {
                    return ret;
                }
            }
        }
        return null;
    }

    private void setSettingItems() {
        View pro = findViewById(R.id.pro);
        if (SettingValues.tabletUI) {
            pro.setVisibility(View.GONE);
        } else {
            pro.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    new AlertDialogWrapper.Builder(Settings.this).setTitle(
                            R.string.settings_support_slide)
                            .setMessage(R.string.pro_upgrade_msg)
                            .setPositiveButton(R.string.btn_yes_exclaim,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                            try {
                                                startActivity(new Intent(Intent.ACTION_VIEW,
                                                        Uri.parse(
                                                                "market://details?id=me.ccrama.slideforreddittabletuiunlock")));
                                            } catch (ActivityNotFoundException e) {
                                                startActivity(new Intent(Intent.ACTION_VIEW,
                                                        Uri.parse(
                                                                "http://play.google.com/store/apps/details?id=me.ccrama.slideforreddittabletuiunlock")));
                                            }
                                        }
                                    })
                            .setNegativeButton(R.string.btn_no_danks,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                            dialog.dismiss();
                                        }
                                    })
                            .show();
                }
            });
        }

        ((EditText) findViewById(R.id.settings_search)).addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.println(Log.DEBUG, "TEST", "You pressed " + s.toString());

                String text = s.toString();

                if (!Strings.isNullOrEmpty(text)){
                    Log.println(Log.DEBUG, "TEST", "box has something in it!");

                    View layout = findViewById(R.id.settings_child);
                    if (layout != null) {
                        ((ViewGroup) layout.getParent()).removeView(layout);
                    }

                    View layout2 = findViewById(R.id.settings_general_child);
                    if (layout2 != null) {
                        ((ViewGroup) layout2.getParent()).removeView(layout2);
                    }

                    if (findViewById(R.id.settings_general_child) == null) {
                        ViewGroup child = (ViewGroup) getLayoutInflater().inflate(R.layout.activity_settings_general_child, null);
                        ((ViewGroup) findViewById(R.id.settings_parent)).addView(child);
                    }

                    // TODO: Implement a check for top-level TextView's that may potentially contain
                    // TODO: text that matches our search

                    /* Go through each subview and scan it for matching text, non-matches */
                    ViewGroup general_settings_parent = (ViewGroup) findViewById(R.id.settings_general_child);
                    Log.println(Log.DEBUG, "Settings", "Found " + String.valueOf(general_settings_parent.getChildCount()) + " children");

                    for (int i=0; i<general_settings_parent.getChildCount(); i++) {

                        Log.println(Log.DEBUG, "Settings", String.valueOf(i) + ": " + general_settings_parent.getChildAt(i).toString());

                        if (general_settings_parent.getChildAt(i) instanceof ViewGroup) {

                            ViewGroup child = (ViewGroup) general_settings_parent.getChildAt(i);
                            Log.println(Log.DEBUG, "Settings", "  Processing general/" + child.toString());

                            String matched_string = loopViews(child, text.toLowerCase());
                            if (Strings.isNullOrEmpty(matched_string)) {
                                Log.println(Log.DEBUG, "Settings", "    removing ViewGroup: " + child.toString());
                                general_settings_parent.removeView(child);
                                i--;
                            }

                        } else {
                            /* Get rid of any fluff that isn't an actual setting (ex. headers) */
                            Log.println(Log.DEBUG, "Settings", "    removing View: " + general_settings_parent.getChildAt(i).toString());
                            general_settings_parent.removeView(general_settings_parent.getChildAt(i));
                            i--;
                        }
                    }

                    /* Bind actions to the new layout */
                    SettingsGeneralFragment SGF = new SettingsGeneralFragment(Settings.this);
                    SGF.Bind();

                } else if (findViewById(R.id.settings_child) == null) {
                    Log.println(Log.DEBUG, "TEST", "box is empty/null");

                    // TODO: Remove all children before settings_child is removed?
                    View layout = findViewById(R.id.settings_general_child);
                    if (layout != null) {
                        ((ViewGroup) layout.getParent()).removeView(layout);
                    }

                    ViewGroup child = (ViewGroup) getLayoutInflater().inflate(R.layout.activity_settings_child, null);
                    ((ViewGroup) findViewById(R.id.settings_parent)).addView(child);
                    onChildCreate();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        findViewById(R.id.general).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent i = new Intent(Settings.this, SettingsGeneral.class);
                startActivityForResult(i, RESTART_SETTINGS_RESULT);
            }
        });

        findViewById(R.id.history).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent i = new Intent(Settings.this, SettingsHistory.class);
                startActivity(i);
            }
        });

        findViewById(R.id.about).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent i = new Intent(Settings.this, SettingsAbout.class);
                startActivity(i);
            }
        });

        findViewById(R.id.offline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Settings.this, ManageOfflineContent.class);
                startActivity(i);
            }
        });

        findViewById(R.id.datasave).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent i = new Intent(Settings.this, SettingsData.class);
                startActivity(i);
            }
        });

        findViewById(R.id.subtheme).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent i = new Intent(Settings.this, SettingsSubreddit.class);
                startActivityForResult(i, RESTART_SETTINGS_RESULT);
            }
        });

        findViewById(R.id.filter).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                Intent i = new Intent(Settings.this, SettingsFilter.class);
                startActivity(i);
            }
        });

        findViewById(R.id.synccit).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                Intent i = new Intent(Settings.this, SettingsSynccit.class);
                startActivity(i);
            }
        });

        findViewById(R.id.reorder).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                Intent inte = new Intent(Settings.this, ReorderSubreddits.class);
                Settings.this.startActivity(inte);
            }
        });

        findViewById(R.id.theme).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                Intent i = new Intent(Settings.this, SettingsTheme.class);
                startActivityForResult(i, RESTART_SETTINGS_RESULT);
            }
        });

        findViewById(R.id.handling).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent i = new Intent(Settings.this, SettingsHandling.class);
                startActivity(i);
            }
        });

        findViewById(R.id.layout).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent i = new Intent(Settings.this, EditCardsLayout.class);
                startActivity(i);
            }
        });

        findViewById(R.id.backup).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent i = new Intent(Settings.this, SettingsBackup.class);
                startActivity(i);
            }
        });

        findViewById(R.id.font).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent i = new Intent(Settings.this, SettingsFont.class);
                startActivity(i);
            }
        });

        findViewById(R.id.tablet).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                  /*  Intent inte = new Intent(Overview.this, Overview.class);
                    inte.putExtra("type", UpdateSubreddits.COLLECTIONS);
                    Overview.this.startActivity(inte);*/
                if (SettingValues.tabletUI) {
                    LayoutInflater inflater = getLayoutInflater();
                    final View dialoglayout = inflater.inflate(R.layout.tabletui, null);
                    final AlertDialogWrapper.Builder builder =
                            new AlertDialogWrapper.Builder(Settings.this);
                    final Resources res = getResources();

                    dialoglayout.findViewById(R.id.title)
                            .setBackgroundColor(Palette.getDefaultColor());
                    //todo final Slider portrait = (Slider) dialoglayout.findViewById(R.id.portrait);
                    final SeekBar landscape = (SeekBar) dialoglayout.findViewById(R.id.landscape);

                    //todo  portrait.setBackgroundColor(Palette.getDefaultColor());
                    landscape.setProgress(Reddit.dpWidth - 1);

                    ((TextView) dialoglayout.findViewById(R.id.progressnumber)).setText(
                            res.getQuantityString(R.plurals.landscape_columns,
                                    landscape.getProgress() + 1, landscape.getProgress() + 1));

                    landscape.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress,
                                boolean fromUser) {
                            ((TextView) dialoglayout.findViewById(R.id.progressnumber)).setText(
                                    res.getQuantityString(R.plurals.landscape_columns,
                                            landscape.getProgress() + 1,
                                            landscape.getProgress() + 1));
                            Settings.changed = true;
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });
                    final Dialog dialog = builder.setView(dialoglayout).create();
                    dialog.show();
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            Reddit.dpWidth = landscape.getProgress() + 1;
                            Reddit.colors.edit()
                                    .putInt("tabletOVERRIDE", landscape.getProgress() + 1)
                                    .apply();
                        }
                    });
                    SwitchCompat s = (SwitchCompat) dialog.findViewById(R.id.dualcolumns);
                    s.setChecked(SettingValues.dualPortrait);
                    s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            SettingValues.dualPortrait = isChecked;
                            SettingValues.prefs.edit()
                                    .putBoolean(SettingValues.PREF_DUAL_PORTRAIT, isChecked)
                                    .apply();
                        }
                    });
                    SwitchCompat s2 = (SwitchCompat) dialog.findViewById(R.id.fullcomment);
                    s2.setChecked(SettingValues.fullCommentOverride);
                    s2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            SettingValues.fullCommentOverride = isChecked;
                            SettingValues.prefs.edit()
                                    .putBoolean(SettingValues.PREF_FULL_COMMENT_OVERRIDE, isChecked)
                                    .apply();
                        }
                    });
                    SwitchCompat s3 = (SwitchCompat) dialog.findViewById(R.id.singlecolumnmultiwindow);
                    s3.setChecked(SettingValues.singleColumnMultiWindow);
                    s3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            SettingValues.singleColumnMultiWindow = isChecked;
                            SettingValues.prefs.edit()
                                    .putBoolean(SettingValues.PREF_SINGLE_COLUMN_MULTI, isChecked)
                                    .apply();
                        }
                    });
                } else {
                    new AlertDialogWrapper.Builder(Settings.this).setTitle(
                            "Mutli-Column Settings are a Pro feature")
                            .setMessage(R.string.pro_upgrade_msg)
                            .setPositiveButton(R.string.btn_yes_exclaim,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                            try {
                                                startActivity(new Intent(Intent.ACTION_VIEW,
                                                        Uri.parse(
                                                                "market://details?id=me.ccrama.slideforreddittabletuiunlock")));
                                            } catch (android.content.ActivityNotFoundException anfe) {
                                                startActivity(new Intent(Intent.ACTION_VIEW,
                                                        Uri.parse(
                                                                "http://play.google.com/store/apps/details?id=me.ccrama.slideforreddittabletuiunlock")));
                                            }
                                        }
                                    })
                            .setNegativeButton(R.string.btn_no_danks,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                int whichButton) {

                                        }
                                    })
                            .show();
                }
            }
        });

        if(FDroid.isFDroid){
            ((TextView) findViewById(R.id.donatetext)).setText("Donate via PayPal");
        }
        findViewById(R.id.support).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if(FDroid.isFDroid){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=56FKCCYLX7L72"));
                    startActivity(browserIntent);
                } else {
                    Intent inte = new Intent(Settings.this, DonateView.class);
                    Settings.this.startActivity(inte);
                }
            }
        });

        findViewById(R.id.comments).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent inte = new Intent(Settings.this, SettingsComments.class);
                Settings.this.startActivity(inte);
            }
        });

        if (Authentication.isLoggedIn && NetworkUtil.isConnected(this)) {
            findViewById(R.id.reddit_settings).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    Intent i = new Intent(Settings.this, SettingsReddit.class);
                    startActivity(i);
                }
            });
        } else {
            findViewById(R.id.reddit_settings).setEnabled(false);
            findViewById(R.id.reddit_settings).setAlpha(0.25f);
        }
    }

    @Override
    public void onFolderSelection(@NonNull FolderChooserDialogCreate dialog, @NonNull File folder) {
        SettingsGeneralFragment f = new SettingsGeneralFragment(this);
        f.onFolderSelection(dialog, folder);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SettingValues.prefs.unregisterOnSharedPreferenceChangeListener(prefsListener);
    }
}