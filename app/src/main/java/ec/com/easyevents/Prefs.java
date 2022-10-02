package ec.com.easyevents;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ec.com.easyevents.adapter.DjAdapter;

public class Prefs {

    public static void setDefaultsPreference(String key, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getDefaultsPreference(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "");
    }
}
