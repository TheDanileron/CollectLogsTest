package com.test.collectlogstest;


import androidx.preference.PreferenceManager;

public class PreferenceHelper {
    public boolean isCollectingLogs() {
        return PreferenceManager.getDefaultSharedPreferences(App.getContext())
                .getBoolean(App.getContext().getString(R.string.preference_logs_key), false);
    }

    public void setCollectingLogs(boolean isCollecting) {
         PreferenceManager.getDefaultSharedPreferences(App.getContext())
               .edit().putBoolean(App.getContext().getString(R.string.preference_logs_key), isCollecting).apply();
    }
}
