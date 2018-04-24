package me.ccrama.redditslide.Fragments;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.AlertDialogWrapper;

import java.util.ArrayList;
import java.util.List;

import me.ccrama.redditslide.Activities.BaseActivity;
import me.ccrama.redditslide.Activities.Slide;
import me.ccrama.redditslide.ColorPreferences;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.Visuals.Palette;
import me.ccrama.redditslide.util.LogUtil;
import me.ccrama.redditslide.util.OnSingleClickListener;
import uz.shift.colorpicker.LineColorPicker;
import uz.shift.colorpicker.OnColorChangedListener;


public class SettingsThemeFragment<ActivityType extends BaseActivity & SettingsFragment.RestartActivity> {

    private ActivityType context;

    public static boolean changed;
    int back;

    public SettingsThemeFragment(ActivityType context) {
        this(context, false);
    }

    public SettingsThemeFragment(ActivityType context, boolean autoBind) {
        this.context = context;
        if (autoBind) Bind();
    }

    public void Bind() {
        back = new ColorPreferences(context).getFontStyle().getThemeType();

        context.findViewById(R.id.accent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = context.getLayoutInflater();
                final View dialoglayout = inflater.inflate(R.layout.chooseaccent, null);
                AlertDialogWrapper.Builder builder =
                        new AlertDialogWrapper.Builder(context);
                final TextView title = (TextView) dialoglayout.findViewById(R.id.title);
                title.setBackgroundColor(Palette.getDefaultColor());

                final LineColorPicker colorPicker =
                        (LineColorPicker) dialoglayout.findViewById(R.id.picker3);

                int[] arrs = new int[ColorPreferences.getNumColorsFromThemeType(0)];
                int i = 0;
                for (ColorPreferences.Theme type : ColorPreferences.Theme.values()) {
                    if (type.getThemeType() == 0) {
                        arrs[i] = ContextCompat.getColor(context, type.getColor());
                        i++;
                    }
                }

                colorPicker.setColors(arrs);
                colorPicker.setSelectedColor(ContextCompat.getColor(context,
                        new ColorPreferences(context).getFontStyle().getColor()));

                dialoglayout.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SettingsThemeFragment.changed = true;
                        int color = colorPicker.getColor();
                        ColorPreferences.Theme t = null;
                        for (ColorPreferences.Theme type : ColorPreferences.Theme.values()) {
                            if (ContextCompat.getColor(context, type.getColor()) == color
                                    && back == type.getThemeType()) {
                                t = type;
                                LogUtil.v("Setting to " + t.getTitle());
                                break;
                            }
                        }
                        new ColorPreferences(context).setFontStyle(t);
                        context.restartActivity();
                    }
                });

                builder.setView(dialoglayout);
                builder.show();
            }
        });

        context.findViewById(R.id.theme).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = context.getLayoutInflater();
                final View dialoglayout = inflater.inflate(R.layout.choosethemesmall, null);
                AlertDialogWrapper.Builder builder =
                        new AlertDialogWrapper.Builder(context);
                final TextView title = (TextView) dialoglayout.findViewById(R.id.title);
                title.setBackgroundColor(Palette.getDefaultColor());

                if (SettingValues.isNight()) {
                    dialoglayout.findViewById(R.id.nightmsg).setVisibility(View.VISIBLE);
                }

                dialoglayout.findViewById(R.id.black)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SettingsThemeFragment.changed = true;
                                String[] names =
                                        new ColorPreferences(context).getFontStyle()
                                                .getTitle()
                                                .split("_");
                                String name = names[names.length - 1];
                                final String newName = name.replace("(", "");
                                for (ColorPreferences.Theme theme : ColorPreferences.Theme.values()) {
                                    if (theme.toString().contains(newName)
                                            && theme.getThemeType() == 2) {
                                        back = theme.getThemeType();
                                        new ColorPreferences(context).setFontStyle(theme);
                                        context.restartActivity();
                                        break;
                                    }
                                }
                            }
                        });
                dialoglayout.findViewById(R.id.light)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SettingsThemeFragment.changed = true;
                                String[] names =
                                        new ColorPreferences(context).getFontStyle()
                                                .getTitle()
                                                .split("_");
                                String name = names[names.length - 1];
                                final String newName = name.replace("(", "");
                                for (ColorPreferences.Theme theme : ColorPreferences.Theme.values()) {
                                    if (theme.toString().contains(newName)
                                            && theme.getThemeType() == 1) {
                                        new ColorPreferences(context).setFontStyle(theme);
                                        back = theme.getThemeType();
                                        context.restartActivity();
                                        break;
                                    }
                                }
                            }
                        });
                dialoglayout.findViewById(R.id.pixel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SettingsThemeFragment.changed = true;
                        String[] names = new ColorPreferences(context).getFontStyle()
                                .getTitle()
                                .split("_");
                        String name = names[names.length - 1];
                        final String newName = name.replace("(", "");
                        for (ColorPreferences.Theme theme : ColorPreferences.Theme.values()) {
                            if (theme.toString().contains(newName) && theme.getThemeType() == 7) {
                                new ColorPreferences(context).setFontStyle(theme);
                                back = theme.getThemeType();
                                context.restartActivity();
                                break;
                            }
                        }
                    }
                });
                dialoglayout.findViewById(R.id.dark).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SettingsThemeFragment.changed = true;
                        String[] names = new ColorPreferences(context).getFontStyle()
                                .getTitle()
                                .split("_");
                        String name = names[names.length - 1];
                        final String newName = name.replace("(", "");
                        for (ColorPreferences.Theme theme : ColorPreferences.Theme.values()) {
                            if (theme.toString().contains(newName) && theme.getThemeType() == 0) {
                                new ColorPreferences(context).setFontStyle(theme);
                                back = theme.getThemeType();
                                context.restartActivity();
                                break;
                            }
                        }
                    }
                });
                dialoglayout.findViewById(R.id.blacklighter)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SettingsThemeFragment.changed = true;
                                String[] names =
                                        new ColorPreferences(context).getFontStyle()
                                                .getTitle()
                                                .split("_");
                                String name = names[names.length - 1];
                                final String newName = name.replace("(", "");
                                for (ColorPreferences.Theme theme : ColorPreferences.Theme.values()) {
                                    if (theme.toString().contains(newName)
                                            && theme.getThemeType() == 4) {
                                        back = theme.getThemeType();
                                        new ColorPreferences(context).setFontStyle(theme);
                                        context.restartActivity();
                                        break;
                                    }
                                }
                            }
                        });
                dialoglayout.findViewById(R.id.deep)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SettingsThemeFragment.changed = true;
                                String[] names =
                                        new ColorPreferences(context).getFontStyle()
                                                .getTitle()
                                                .split("_");
                                String name = names[names.length - 1];
                                final String newName = name.replace("(", "");
                                for (ColorPreferences.Theme theme : ColorPreferences.Theme.values()) {
                                    if (theme.toString().contains(newName)
                                            && theme.getThemeType() == 8) {
                                        back = theme.getThemeType();
                                        new ColorPreferences(context).setFontStyle(theme);
                                        context.restartActivity();
                                        break;
                                    }
                                }
                            }
                        });
                dialoglayout.findViewById(R.id.sepia)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SettingsThemeFragment.changed = true;
                                String[] names =
                                        new ColorPreferences(context).getFontStyle()
                                                .getTitle()
                                                .split("_");
                                String name = names[names.length - 1];
                                final String newName = name.replace("(", "");
                                for (ColorPreferences.Theme theme : ColorPreferences.Theme.values()) {
                                    if (theme.toString().contains(newName)
                                            && theme.getThemeType() == 5) {
                                        back = theme.getThemeType();
                                        new ColorPreferences(context).setFontStyle(theme);
                                        context.restartActivity();
                                        break;
                                    }
                                }
                            }
                        });
                dialoglayout.findViewById(R.id.red).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SettingsThemeFragment.changed = true;
                        String[] names = new ColorPreferences(context).getFontStyle()
                                .getTitle()
                                .split("_");
                        String name = names[names.length - 1];
                        final String newName = name.replace("(", "");
                        for (ColorPreferences.Theme theme : ColorPreferences.Theme.values()) {
                            if (theme.toString().contains(newName) && theme.getThemeType() == 6) {
                                back = theme.getThemeType();
                                new ColorPreferences(context).setFontStyle(theme);
                                context.restartActivity();
                                break;
                            }
                        }
                    }
                });
                dialoglayout.findViewById(R.id.blue).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SettingsThemeFragment.changed = true;
                        String[] names = new ColorPreferences(context).getFontStyle()
                                .getTitle()
                                .split("_");
                        String name = names[names.length - 1];
                        final String newName = name.replace("(", "");
                        for (ColorPreferences.Theme theme : ColorPreferences.Theme.values()) {
                            if (theme.toString().contains(newName) && theme.getThemeType() == 3) {
                                new ColorPreferences(context).setFontStyle(theme);
                                back = theme.getThemeType();
                                context.restartActivity();
                                break;
                            }
                        }
                    }
                });

                builder.setView(dialoglayout);
                builder.show();
            }

        });

        context.findViewById(R.id.main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = context.getLayoutInflater();
                final LinearLayout dialoglayout = (LinearLayout) inflater.inflate(R.layout.choosemain, null);
                final AlertDialogWrapper.Builder builder =
                        new AlertDialogWrapper.Builder(context);
                final TextView title = (TextView) dialoglayout.findViewById(R.id.title);
                title.setBackgroundColor(Palette.getDefaultColor());

                final LineColorPicker colorPicker =
                        (LineColorPicker) dialoglayout.findViewById(R.id.picker);
                final LineColorPicker colorPicker2 =
                        (LineColorPicker) dialoglayout.findViewById(R.id.picker2);

                colorPicker.setColors(ColorPreferences.getBaseColors(context));
                int currentColor = Palette.getDefaultColor();
                for (int i : colorPicker.getColors()) {
                    for (int i2 : ColorPreferences.getColors(context.getBaseContext(), i)) {
                        if (i2 == currentColor) {
                            colorPicker.setSelectedColor(i);
                            colorPicker2.setColors(ColorPreferences.getColors(context.getBaseContext(), i));
                            colorPicker2.setSelectedColor(i2);
                            break;
                        }
                    }
                }

                colorPicker.setOnColorChangedListener(new OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int c) {
                        SettingsThemeFragment.changed = true;
                        colorPicker2.setColors(ColorPreferences.getColors(context.getBaseContext(), c));
                        colorPicker2.setSelectedColor(c);
                    }
                });

                colorPicker2.setOnColorChangedListener(new OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int i) {
                        SettingsThemeFragment.changed = true;
                        title.setBackgroundColor(colorPicker2.getColor());
                        if (context.findViewById(R.id.toolbar) != null) context.findViewById(R.id.toolbar).setBackgroundColor(colorPicker2.getColor());

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Window window = context.getWindow();
                            window.setStatusBarColor(
                                    Palette.getDarkerColor(colorPicker2.getColor()));
                        }
                        context.setRecentBar(context.getString(R.string.title_theme_settings),
                                colorPicker2.getColor());

                    }
                });

                dialoglayout.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SettingValues.colorIcon) {
                            context.getPackageManager().setComponentEnabledSetting(
                                    new ComponentName(context,
                                            ColorPreferences.getIconName(context,
                                                    Reddit.colors.getInt("DEFAULTCOLOR", 0))),
                                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                    PackageManager.DONT_KILL_APP);

                            context.getPackageManager().setComponentEnabledSetting(
                                    new ComponentName(context,
                                            ColorPreferences.getIconName(context,
                                                    colorPicker2.getColor())),
                                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                                    PackageManager.DONT_KILL_APP);
                        }
                        Reddit.colors.edit()
                                .putInt("DEFAULTCOLOR", colorPicker2.getColor())
                                .apply();
                        context.restartActivity();
                    }
                });

                builder.setView(dialoglayout);
                builder.show();
            }

        });

        //Color tinting mode
        final SwitchCompat s2 = (SwitchCompat) context.findViewById(R.id.tint_everywhere);

        ((TextView) context.findViewById(R.id.tint_current)).setText(
                SettingValues.colorBack ? (SettingValues.colorSubName ? context.getString(
                        R.string.subreddit_name_tint) : context.getString(R.string.card_background_tint))
                        : context.getString(R.string.misc_none));

        boolean enabled = !((TextView) context.findViewById(R.id.tint_current)).getText()
                .equals(context.getString(R.string.misc_none));

        context.findViewById(R.id.dotint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, v);
                popup.getMenuInflater()
                        .inflate(R.menu.color_tinting_mode_settings, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.none:
                                SettingValues.colorBack = false;
                                SettingValues.prefs.edit()
                                        .putBoolean(SettingValues.PREF_COLOR_BACK, false)
                                        .apply();
                                break;
                            case R.id.background:
                                SettingValues.colorBack = true;
                                SettingValues.colorSubName = false;
                                SettingValues.prefs.edit()
                                        .putBoolean(SettingValues.PREF_COLOR_BACK, true)
                                        .apply();
                                SettingValues.prefs.edit()
                                        .putBoolean(SettingValues.PREF_COLOR_SUB_NAME, false)
                                        .apply();
                                break;
                            case R.id.name:
                                SettingValues.colorBack = true;
                                SettingValues.colorSubName = true;
                                SettingValues.prefs.edit()
                                        .putBoolean(SettingValues.PREF_COLOR_BACK, true)
                                        .apply();
                                SettingValues.prefs.edit()
                                        .putBoolean(SettingValues.PREF_COLOR_SUB_NAME, true)
                                        .apply();
                                break;
                        }
                        ((TextView) context.findViewById(R.id.tint_current)).setText(
                                SettingValues.colorBack ? (SettingValues.colorSubName ? context.getString(
                                        R.string.subreddit_name_tint)
                                        : context.getString(R.string.card_background_tint))
                                        : context.getString(R.string.misc_none));
                        boolean enabled = !((TextView) context.findViewById(R.id.tint_current)).getText()
                                .equals(context.getString(R.string.misc_none));
                        s2.setEnabled(enabled);
                        s2.setChecked(SettingValues.colorEverywhere);
                        s2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView,
                                                         boolean isChecked) {
                                SettingValues.colorEverywhere = isChecked;
                                SettingValues.prefs.edit()
                                        .putBoolean(SettingValues.PREF_COLOR_EVERYWHERE, isChecked)
                                        .apply();
                            }
                        });
                        return true;
                    }
                });

                popup.show();
            }
        });

        s2.setEnabled(enabled);
        s2.setChecked(SettingValues.colorEverywhere);
        s2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingValues.colorEverywhere = isChecked;
                SettingValues.prefs.edit()
                        .putBoolean(SettingValues.PREF_COLOR_EVERYWHERE, isChecked)
                        .apply();
            }
        });

        final SwitchCompat colorNavbarSwitch =
                (SwitchCompat) context.findViewById(R.id.color_navigation_bar);

        colorNavbarSwitch.setChecked(SettingValues.colorNavBar);
        colorNavbarSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsThemeFragment.changed = true;
                SettingValues.colorNavBar = isChecked;
                SettingValues.prefs.edit()
                        .putBoolean(SettingValues.PREF_COLOR_NAV_BAR, isChecked)
                        .apply();
                context.themeSystemBars("");
                if (!isChecked) {
                    context.getWindow().setNavigationBarColor(Color.TRANSPARENT);
                }

            }
        });
        final SwitchCompat colorIcon = (SwitchCompat) context.findViewById(R.id.color_icon);

        colorIcon.setChecked(SettingValues.colorIcon);
        colorIcon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingValues.colorIcon = isChecked;
                SettingValues.prefs.edit()
                        .putBoolean(SettingValues.PREF_COLOR_ICON, isChecked)
                        .apply();
                if (isChecked) {
                    context.getPackageManager().setComponentEnabledSetting(
                            new ComponentName(context,
                                    Slide.class.getPackage().getName() + ".Slide"),
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP);
                    context.getPackageManager().setComponentEnabledSetting(
                            new ComponentName(context,
                                    ColorPreferences.getIconName(context,
                                            Reddit.colors.getInt("DEFAULTCOLOR", 0))),
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP);
                } else {
                    context.getPackageManager().setComponentEnabledSetting(
                            new ComponentName(context,
                                    ColorPreferences.getIconName(context,
                                            Reddit.colors.getInt("DEFAULTCOLOR", 0))),
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP);

                    context.getPackageManager().setComponentEnabledSetting(
                            new ComponentName(context,
                                    Slide.class.getPackage().getName() + ".Slide"),
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP);
                }
            }
        });
        LinearLayout nightMode = (LinearLayout) context.findViewById(R.id.night);
        assert nightMode != null;
        nightMode.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (SettingValues.tabletUI) {
                    LayoutInflater inflater = context.getLayoutInflater();
                    final View dialoglayout = inflater.inflate(R.layout.nightmode, null);
                    final AlertDialogWrapper.Builder builder =
                            new AlertDialogWrapper.Builder(context);
                    final Dialog dialog = builder.setView(dialoglayout).create();
                    dialog.show();
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            //todo save
                        }
                    });
                    SwitchCompat s = (SwitchCompat) dialog.findViewById(R.id.enabled);
                    s.setChecked(SettingValues.nightMode);
                    s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            SettingValues.nightMode = isChecked;
                            SettingValues.prefs.edit()
                                    .putBoolean(SettingValues.PREF_NIGHT_MODE, isChecked)
                                    .apply();
                            SettingsThemeFragment.changed = true;
                        }
                    });
                    switch (SettingValues.nightTheme) {
                        case 0:
                            ((RadioButton) dialoglayout.findViewById(R.id.dark)).setChecked(true);
                            break;
                        case 2:
                            ((RadioButton) dialoglayout.findViewById(R.id.amoled)).setChecked(true);
                            break;
                        case 3:
                            ((RadioButton) dialoglayout.findViewById(R.id.blue)).setChecked(true);
                            break;
                        case 6:
                            ((RadioButton) dialoglayout.findViewById(R.id.red)).setChecked(true);
                            break;
                        case 4:
                            ((RadioButton) dialoglayout.findViewById(
                                    R.id.amoled_contrast)).setChecked(true);
                            break;
                        default:
                            ((RadioButton) dialoglayout.findViewById(R.id.dark)).setChecked(true);
                            break;

                    }
                    ((RadioButton) dialoglayout.findViewById(R.id.dark)).setOnCheckedChangeListener(
                            new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView,
                                                             boolean isChecked) {
                                    if (isChecked) {
                                        SettingsThemeFragment.changed = true;
                                        SettingValues.nightTheme = 0;
                                        SettingValues.prefs.edit()
                                                .putInt(SettingValues.PREF_NIGHT_THEME, 0)
                                                .apply();
                                    }
                                }
                            });
                    ((RadioButton) dialoglayout.findViewById(
                            R.id.amoled)).setOnCheckedChangeListener(
                            new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView,
                                                             boolean isChecked) {
                                    if (isChecked) {
                                        SettingsThemeFragment.changed = true;
                                        SettingValues.nightTheme = 2;
                                        SettingValues.prefs.edit()
                                                .putInt(SettingValues.PREF_NIGHT_THEME, 2)
                                                .apply();
                                    }
                                }
                            });
                    ((RadioButton) dialoglayout.findViewById(R.id.red)).setOnCheckedChangeListener(
                            new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView,
                                                             boolean isChecked) {
                                    if (isChecked) {
                                        SettingsThemeFragment.changed = true;
                                        SettingValues.nightTheme = 6;
                                        SettingValues.prefs.edit()
                                                .putInt(SettingValues.PREF_NIGHT_THEME, 6)
                                                .apply();
                                    }
                                }
                            });
                    ((RadioButton) dialoglayout.findViewById(R.id.blue)).setOnCheckedChangeListener(
                            new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView,
                                                             boolean isChecked) {
                                    if (isChecked) {
                                        SettingsThemeFragment.changed = true;
                                        SettingValues.nightTheme = 3;
                                        SettingValues.prefs.edit()
                                                .putInt(SettingValues.PREF_NIGHT_THEME, 3)
                                                .apply();
                                    }
                                }
                            });
                    ((RadioButton) dialoglayout.findViewById(
                            R.id.amoled_contrast)).setOnCheckedChangeListener(
                            new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView,
                                                             boolean isChecked) {
                                    if (isChecked) {
                                        SettingsThemeFragment.changed = true;
                                        SettingValues.nightTheme = 4;
                                        SettingValues.prefs.edit()
                                                .putInt(SettingValues.PREF_NIGHT_THEME, 4)
                                                .apply();
                                    }
                                }
                            });
                    {
                        final List<String> timesStart = new ArrayList<String>() {{
                            add("6pm");
                            add("7pm");
                            add("8pm");
                            add("9pm");
                            add("10pm");
                            add("11pm");
                        }};
                        final Spinner startSpinner =
                                (Spinner) dialoglayout.findViewById(R.id.start_spinner);
                        final ArrayAdapter<String> startAdapter =
                                new ArrayAdapter<>(context,
                                        android.R.layout.simple_spinner_item, timesStart);
                        startAdapter.setDropDownViewResource(
                                android.R.layout.simple_spinner_dropdown_item);
                        startSpinner.setAdapter(startAdapter);

                        //set the currently selected pref
                        startSpinner.setSelection(startAdapter.getPosition(
                                Integer.toString(SettingValues.nightStart).concat("pm")));

                        startSpinner.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view,
                                                               int position, long id) {
                                        //get the time, but remove the "pm" from the string when parsing
                                        final int time = Integer.parseInt(
                                                ((String) startSpinner.getItemAtPosition(
                                                        position)).replaceAll("pm", ""));

                                        SettingValues.nightStart = time;
                                        SettingValues.prefs.edit()
                                                .putInt(SettingValues.PREF_NIGHT_START, time)
                                                .apply();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                    }
                    {
                        final List<String> timesEnd = new ArrayList<String>() {{
                            add("12am");
                            add("1am");
                            add("2am");
                            add("3am");
                            add("4am");
                            add("5am");
                            add("6am");
                            add("7am");
                            add("8am");
                            add("9am");
                            add("10am");
                        }};
                        final Spinner endSpinner =
                                (Spinner) dialoglayout.findViewById(R.id.end_spinner);
                        final ArrayAdapter<String> endAdapter =
                                new ArrayAdapter<>(context,
                                        android.R.layout.simple_spinner_item, timesEnd);
                        endAdapter.setDropDownViewResource(
                                android.R.layout.simple_spinner_dropdown_item);
                        endSpinner.setAdapter(endAdapter);

                        //set the currently selected pref
                        endSpinner.setSelection(endAdapter.getPosition(
                                Integer.toString(SettingValues.nightEnd).concat("am")));

                        endSpinner.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view,
                                                               int position, long id) {
                                        //get the time, but remove the "am" from the string when parsing
                                        final int time = Integer.parseInt(
                                                ((String) endSpinner.getItemAtPosition(
                                                        position)).replaceAll("am", ""));

                                        SettingValues.nightEnd = time;
                                        SettingValues.prefs.edit()
                                                .putInt(SettingValues.PREF_NIGHT_END, time)
                                                .apply();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                    }
                    {
                        Button okayButton = (Button) dialoglayout.findViewById(R.id.ok);
                        okayButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                } else {
                    new AlertDialogWrapper.Builder(context).setTitle(
                            R.string.general_nighttheme_ispro)
                            .setMessage(R.string.pro_upgrade_msg)
                            .setPositiveButton(R.string.btn_yes_exclaim,

                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int whichButton) {
                                            try {
                                                context.startActivity(new Intent(Intent.ACTION_VIEW,
                                                        Uri.parse(
                                                                "market://details?id=me.ccrama.slideforreddittabletuiunlock")));
                                            } catch (android.content.ActivityNotFoundException anfe) {
                                                context.startActivity(new Intent(Intent.ACTION_VIEW,
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
    }

}
