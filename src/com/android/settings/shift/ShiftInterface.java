package com.android.settings.beanstalk;

import android.widget.Toast;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import android.content.ContentResolver;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.text.TextUtils;
import android.preference.PreferenceScreen;
import android.preference.PreferenceCategory;
import android.preference.Preference.OnPreferenceChangeListener;
import com.android.settings.cyanogenmod.AppMultiSelectListPreference;

import java.util.HashSet;
import java.util.Set;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.Arrays;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class ScreenAndAnimations extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_ENABLE_APP_CIRCLE_BAR = "enable_app_circle_bar";
    private static final String PREF_INCLUDE_APP_CIRCLE_BAR_KEY = "app_circle_bar_included_apps";

    private AppMultiSelectListPreference mIncludedAppCircleBar;
    private ListPreference mToastAnimation;
    private ListPreference mCrtMode;
    private CheckBoxPreference mEnableAppCircleBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.shift_ui_settings);

	PreferenceScreen prefSet = getPreferenceScreen();

	// App circle bar
	mEnableAppCircleBar = (CheckBoxPreference) prefScreen.findPreference(PREF_ENABLE_APP_CIRCLE_BAR);
	mEnableAppCircleBar.setChecked((Settings.System.getInt(getContentResolver(),
	Settings.System.ENABLE_APP_CIRCLE_BAR, 0) == 1));

	mIncludedAppCircleBar = (AppMultiSelectListPreference) prefScreen.findPreference		(PREF_INCLUDE_APP_CIRCLE_BAR_KEY);
	Set<String> includedApps = getIncludedApps();
	if (includedApps != null) mIncludedAppCircleBar.setValues(includedApps);
	mIncludedAppCircleBar.setOnPreferenceChangeListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
	ContentResolver resolver = getActivity().getContentResolver();
	boolean value;
	if (preference == mEnableAppCircleBar) {
	    boolean checked = ((CheckBoxPreference)preference).isChecked();
	    Settings.System.putInt(resolver,
		Settings.System.ENABLE_APP_CIRCLE_BAR, checked ? 1:0);
	} else {
	    return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	return true;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
	final String key = preference.getKey();

	if (preference == mIncludedAppCircleBar) {
	    storeIncludedApps((Set<String>) newValue);
	}
        return false;
    }

    private Set<String> getIncludedApps() {
	String included = Settings.System.getString(getActivity().getContentResolver(),
			Settings.System.WHITELIST_APP_CIRCLE_BAR);
	if (TextUtils.isEmpty(included)) {
		return null;
	}
	return new HashSet<String>(Arrays.asList(included.split("\\|")));
    }

    private void storeIncludedApps(Set<String> values) {
	StringBuilder builder = new StringBuilder();
	String delimiter = "";
	for (String value : values) {
		builder.append(delimiter);
		builder.append(value);
		delimiter = "|";
	}
	Settings.System.putString(getActivity().getContentResolver(),
		Settings.System.WHITELIST_APP_CIRCLE_BAR, builder.toString());
    }
}
