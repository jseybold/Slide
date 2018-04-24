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
import android.support.v7.widget.AppCompatTextView;
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
import me.ccrama.redditslide.Fragments.SettingsFragment;
import me.ccrama.redditslide.Fragments.SettingsGeneralFragment;
import me.ccrama.redditslide.Fragments.SettingsThemeFragment;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.Visuals.Palette;
import me.ccrama.redditslide.util.NetworkUtil;
import me.ccrama.redditslide.util.OnSingleClickListener;


/**
 * Created by ccrama on 3/5/2015.
 */
public class Settings extends BaseActivity
        implements FolderChooserDialogCreate.FolderCallback, SettingsFragment.RestartActivity {

    private final static int RESTART_SETTINGS_RESULT = 2;
    private       int                                                scrollY;
    private       SharedPreferences.OnSharedPreferenceChangeListener prefsListener;
    private       String                                             prev_text;
    public static boolean                                            changed;  //whether or not a Setting was changed

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

        if (getIntent() != null & !Strings.isNullOrEmpty(getIntent().getStringExtra("prev_text"))) {
            prev_text = getIntent().getStringExtra("prev_text");
        } else if (savedInstanceState != null) {
            prev_text = savedInstanceState.getString("prev_text");
        }

        if (!Strings.isNullOrEmpty(prev_text)) {
            ((EditText) findViewById(R.id.settings_search)).setText(prev_text);
        }

        BuildLayout(prev_text);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("prev_text", prev_text);
        super.onSaveInstanceState(outState);
    }

    private void BuildLayout(String text) {
        // Clear the settings out, then re-add the default top-level settings
        ((LinearLayout) findViewById(R.id.settings_parent)).removeAllViews();

        ((ViewGroup) findViewById(R.id.settings_parent)).addView(
                getLayoutInflater().inflate(R.layout.activity_settings_child, null));
        Bind();

        /* The EditView contains text that we can use to search for matching settings */
        if (!Strings.isNullOrEmpty(text)){
            Log.println(Log.DEBUG, "TEST", "box has something in it!");

            /* SettingsGeneral - "General" */
            ((ViewGroup) findViewById(R.id.settings_parent)).addView(
                    getLayoutInflater().inflate(R.layout.activity_settings_general_child, null));
            // Todo: Might want to move this object instantiation somewhere else...
            SettingsGeneralFragment SGF = new SettingsGeneralFragment(Settings.this);
            SGF.Bind();

            /* SettingsTheme - "Main Theme" */
            ((ViewGroup) findViewById(R.id.settings_parent)).addView(
                    getLayoutInflater().inflate(R.layout.activity_settings_theme_child, null));
            // Todo: Might want to move this object instantiation somewhere else...
            SettingsThemeFragment STF = new SettingsThemeFragment(Settings.this);
            STF.Bind();

            /* Font */

            /* Comments */

            /* Link Handling */

            /* History */

            /* Data Saving */

            /* Backup & Restore */

            /* Go through each subview and scan it for matching text, non-matches */
            ViewGroup parent = (ViewGroup) findViewById(R.id.settings_parent);
            Log.println(Log.DEBUG, "Settings", "settings_parent has " + String.valueOf(parent.getChildCount()) + " children");

            for (int i=0; i<parent.getChildCount(); i++) {

                Log.println(Log.DEBUG, "Settings", String.valueOf(i) + ": " + parent.getChildAt(i).toString());

                /* Remove top-level TextView labels */
                if (parent.getChildAt(i) instanceof TextView) {
                    if (!((TextView) parent.getChildAt(i))
                            .getText()
                            .toString()
                            .toLowerCase()
                            .contains(text.toLowerCase())) {
                        Log.println(Log.DEBUG, "Settings", "Removing TextView: " + ((TextView) parent.getChildAt(i)).getText());
                        parent.removeView(parent.getChildAt(i));
                        i--;
                    }
                }

                        /* Go through each ViewGroup and its children recursively,
                           searching for a TextView with matching text
                         */
                else if (parent.getChildAt(i) instanceof ViewGroup) {
                    loopViews((ViewGroup) parent.getChildAt(i), text.toLowerCase(), "");
                }

//                        /* Get rid of any fluff that isn't an actual setting (ex. headers) */
//                        else {
//                            Log.println(Log.DEBUG, "Settings", "    removing View: " + parent.getChildAt(i).toString());
//                            parent.removeView(parent.getChildAt(i));
//                            i--;
//                        }
            }
        }

        /* Try to clean up the mess we've made */
        System.gc();
    }

    private void Bind() {

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

    private boolean loopViews(ViewGroup view, String text, String indent) {

        boolean foundText = false;

        for (int i = 0; i < view.getChildCount(); i++) {

            Log.println(Log.DEBUG, "loopViews",indent +  view.getClass().toString() + " - " + view.getChildAt(i).getClass().toString());
            View v = view.getChildAt(i);

            // Todo: Do we need to check both types here or would one suffice?
            if (view.getChildAt(i) instanceof TextView || view.getChildAt(i) instanceof AppCompatTextView){

                // Found a label
                if ( v.getTag() != null && v.getTag().toString().equals("label")) {
                    Log.println(Log.DEBUG, "loopViews", indent + "Removing label: " + ((TextView) v).getText());
                    view.removeView(v);
                    i--;
                    continue;
                }

                // Found matching text!
                if (((TextView) v).getText().toString().toLowerCase().contains(text)) {
                    Log.println(Log.DEBUG, "loopViews", indent + "Found text! - " + ((TextView) v).getText());
                    foundText = true;
                    continue;
                }

                Log.println(Log.DEBUG, "loopViews", indent + "No match - " + ((TextView) v).getText());

            } else if (v instanceof ViewGroup) {
                // Look for matching TextView in the ViewGroup, remove the ViewGroup if no match is found
                if (!this.loopViews((ViewGroup) v, text, indent + "  ")) {
                    Log.println(Log.DEBUG, "loopViews", indent + "Removing ViewGroup: " + v.getClass().toString());
                    view.removeView(v);
                    i--;
                } else {
                    foundText = true;
                }
            }
        }

        return foundText;
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
                Log.println(Log.DEBUG, "TEST", "You pressed " + s.toString() + ", prev_text is " + prev_text);
                String text = s.toString().trim();

                /* No idea why, but this event can fire many times when there is no change */
                if (text.equalsIgnoreCase(prev_text)) {
                    Log.println(Log.DEBUG, "TEST", "onTextChanged fired, but no change detected!");
                    return;
                } else {
                    BuildLayout(text);
                }
                prev_text = text;
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

        findViewById(R.id.maintheme).setOnClickListener(new OnSingleClickListener() {
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
    public void restartActivity() {
        Intent i = new Intent(this, Settings.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        i.putExtra("prev_text", prev_text);
        startActivity(i);
        overridePendingTransition(0, 0);

        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SettingValues.prefs.unregisterOnSharedPreferenceChangeListener(prefsListener);
    }

}