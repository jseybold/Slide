package me.ccrama.redditslide.Fragments;

import android.app.Activity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SwitchCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.devspark.robototextview.widget.RobotoRadioButton;

import me.ccrama.redditslide.R;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.Visuals.FontPreferences;

public class SettingsFontFragment {

    private static String getFontName(int resource) {
        switch (resource) {
            case R.string.font_size_huge:
                return "Huge";
            case R.string.font_size_larger:
                return "Larger";
            case R.string.font_size_large:
                return "Large";
            case R.string.font_size_medium:
                return "Medium";
            case R.string.font_size_small:
                return "Small";
            case R.string.font_size_smaller:
                return "Smaller";
            case R.string.font_size_tiny:
                return "Tiny";
            default:
                return "Medium";
        }
    }

    private Activity context;

    public SettingsFontFragment(Activity context) {
        this(context, false);
    }

    public SettingsFontFragment(Activity context, boolean autoBind) {
        this.context = context;
        if (autoBind) Bind();
    }

    public void Bind() {
        final TextView colorComment = (TextView) context.findViewById(R.id.commentFont);
        colorComment.setText(new FontPreferences(context).getCommentFontStyle().getTitle());
        context.findViewById(R.id.commentfontsize).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, v);
                popup.getMenu().add(0, R.string.font_size_huge, 0, R.string.font_size_huge);
                popup.getMenu().add(0, R.string.font_size_larger, 0, R.string.font_size_larger);
                popup.getMenu().add(0, R.string.font_size_large, 0, R.string.font_size_large);
                popup.getMenu().add(0, R.string.font_size_medium, 0, R.string.font_size_medium);
                popup.getMenu().add(0, R.string.font_size_small, 0, R.string.font_size_small);
                popup.getMenu().add(0, R.string.font_size_smaller, 0, R.string.font_size_smaller);

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        new FontPreferences(context).setCommentFontStyle(
                                FontPreferences.FontStyleComment.valueOf(getFontName(item.getItemId())));
                        colorComment.setText(new FontPreferences(context).getCommentFontStyle().getTitle());
                        SettingsThemeFragment.changed = true;
                        return true;
                    }
                });

                popup.show();
            }
        });
        final TextView colorPost = (TextView) context.findViewById(R.id.postFont);
        colorPost.setText(new FontPreferences(context).getPostFontStyle().getTitle());
        context.findViewById(R.id.postfontsize).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, v);
                popup.getMenu().add(0, R.string.font_size_huge, 0, R.string.font_size_huge);
                popup.getMenu().add(0, R.string.font_size_larger, 0, R.string.font_size_larger);
                popup.getMenu().add(0, R.string.font_size_large, 0, R.string.font_size_large);
                popup.getMenu().add(0, R.string.font_size_medium, 0, R.string.font_size_medium);
                popup.getMenu().add(0, R.string.font_size_small, 0, R.string.font_size_small);
                popup.getMenu().add(0, R.string.font_size_smaller, 0, R.string.font_size_smaller);
                popup.getMenu().add(0, R.string.font_size_tiny, 0, R.string.font_size_tiny);

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        new FontPreferences(context).setPostFontStyle(
                                FontPreferences.FontStyle.valueOf(getFontName(item.getItemId())));
                        colorPost.setText(new FontPreferences(context).getPostFontStyle().getTitle());
                        SettingsThemeFragment.changed = true;
                        return true;
                    }
                });

                popup.show();
            }
        });

        switch (new FontPreferences(context).getFontTypeComment()) {
            case Regular:
                ((RobotoRadioButton) context.findViewById(R.id.creg)).setChecked(true);
                break;
            case Slab:
                ((RobotoRadioButton) context.findViewById(R.id.cslab)).setChecked(true);
                break;
            case Condensed:
                ((RobotoRadioButton) context.findViewById(R.id.ccond)).setChecked(true);
                break;
            case Light:
                ((RobotoRadioButton) context.findViewById(R.id.clight)).setChecked(true);
                break;
            case System:
                ((RobotoRadioButton) context.findViewById(R.id.cnone)).setChecked(true);
                break;

        }
        ((RobotoRadioButton) context.findViewById(R.id.ccond)).setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SettingsThemeFragment.changed = true;
                    new FontPreferences(context).setCommentFont(FontPreferences.FontTypeComment.Condensed);
                }
            }
        });
        ((RobotoRadioButton) context.findViewById(R.id.cslab)).setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SettingsThemeFragment.changed = true;
                    new FontPreferences(context).setCommentFont(FontPreferences.FontTypeComment.Slab);
                }
            }
        });
        ((RobotoRadioButton) context.findViewById(R.id.creg)).setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SettingsThemeFragment.changed = true;
                    new FontPreferences(context).setCommentFont(FontPreferences.FontTypeComment.Regular);
                }
            }
        });
        ((RobotoRadioButton) context.findViewById(R.id.clight)).setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SettingsThemeFragment.changed = true;
                    new FontPreferences(context).setCommentFont(FontPreferences.FontTypeComment.Light);
                }
            }
        });
        ((RobotoRadioButton) context.findViewById(R.id.cnone)).setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SettingsThemeFragment.changed = true;
                    new FontPreferences(context).setCommentFont(FontPreferences.FontTypeComment.System);
                }
            }
        });
        switch (new FontPreferences(context).getFontTypeTitle()) {
            case Regular:
                ((RobotoRadioButton) context.findViewById(R.id.sreg)).setChecked(true);
                break;
            case Light:
                ((RobotoRadioButton) context.findViewById(R.id.sregl)).setChecked(true);
                break;
            case Slab:
                ((RobotoRadioButton) context.findViewById(R.id.sslabl)).setChecked(true);
                break;
            case SlabReg:
                ((RobotoRadioButton) context.findViewById(R.id.sslab)).setChecked(true);
                break;
            case CondensedReg:
                ((RobotoRadioButton) context.findViewById(R.id.scond)).setChecked(true);
                break;
            case CondensedBold:
                ((RobotoRadioButton) context.findViewById(R.id.scondb)).setChecked(true);
                break;
            case Condensed:
                ((RobotoRadioButton) context.findViewById(R.id.scondl)).setChecked(true);
                break;
            case Bold:
                ((RobotoRadioButton) context.findViewById(R.id.sbold)).setChecked(true);
                break;
            case Medium:
                ((RobotoRadioButton) context.findViewById(R.id.smed)).setChecked(true);
                break;
            case System:
                ((RobotoRadioButton) context.findViewById(R.id.snone)).setChecked(true);
                break;
        }
        ((RobotoRadioButton) context.findViewById(R.id.scond)).setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SettingsThemeFragment.changed = true;
                    new FontPreferences(context).setTitleFont(FontPreferences.FontTypeTitle.CondensedReg);
                }
            }
        });
        ((RobotoRadioButton) context.findViewById(R.id.sslab)).setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SettingsThemeFragment.changed = true;
                    new FontPreferences(context).setTitleFont(FontPreferences.FontTypeTitle.SlabReg);
                }
            }
        });
        ((RobotoRadioButton) context.findViewById(R.id.scondl)).setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SettingsThemeFragment.changed = true;
                    new FontPreferences(context).setTitleFont(FontPreferences.FontTypeTitle.Condensed);
                }
            }
        });
        ((RobotoRadioButton) context.findViewById(R.id.sbold)).setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SettingsThemeFragment.changed = true;
                    new FontPreferences(context).setTitleFont(FontPreferences.FontTypeTitle.Bold);
                }
            }
        });
        ((RobotoRadioButton) context.findViewById(R.id.smed)).setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SettingsThemeFragment.changed = true;
                    new FontPreferences(context).setTitleFont(FontPreferences.FontTypeTitle.Medium);
                }
            }
        });
        ((RobotoRadioButton) context.findViewById(R.id.sslabl)).setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SettingsThemeFragment.changed = true;
                    new FontPreferences(context).setTitleFont(FontPreferences.FontTypeTitle.Slab);
                }
            }
        });
        ((RobotoRadioButton) context.findViewById(R.id.sreg)).setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SettingsThemeFragment.changed = true;
                    new FontPreferences(context).setTitleFont(FontPreferences.FontTypeTitle.Regular);
                }
            }
        });
        ((RobotoRadioButton) context.findViewById(R.id.sregl)).setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SettingsThemeFragment.changed = true;
                    new FontPreferences(context).setTitleFont(FontPreferences.FontTypeTitle.Light);
                }
            }
        });
        ((RobotoRadioButton) context.findViewById(R.id.snone)).setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SettingsThemeFragment.changed = true;
                    new FontPreferences(context).setTitleFont(FontPreferences.FontTypeTitle.System);
                }
            }
        });
        ((RobotoRadioButton) context.findViewById(R.id.scondb)).setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SettingsThemeFragment.changed = true;
                    new FontPreferences(context).setTitleFont(FontPreferences.FontTypeTitle.CondensedBold);
                }
            }
        });

        {
            SwitchCompat single = (SwitchCompat) context.findViewById(R.id.linktype);
            single.setChecked(SettingValues.typeInText);
            single.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SettingValues.typeInText = isChecked;
                    SettingValues.prefs.edit().putBoolean(SettingValues.PREF_TYPE_IN_TEXT, isChecked).apply();
                }
            });
        }
        {
            SwitchCompat single = (SwitchCompat) context.findViewById(R.id.enlarge_links);
            single.setChecked(SettingValues.largeLinks);
            single.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SettingValues.largeLinks = isChecked;
                    SettingValues.prefs.edit().putBoolean(SettingValues.PREF_LARGE_LINKS, isChecked).apply();
                }
            });
        }
    }

}
