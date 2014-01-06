package com.trumpetx.egauge.widget.util;

import android.preference.Preference;

public interface PreferenceBinder {
    Preference findPreference(CharSequence prefName);

    void addPreferencesFromResource(int preferencesResId);
}