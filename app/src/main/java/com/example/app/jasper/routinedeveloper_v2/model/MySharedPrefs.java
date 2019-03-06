package com.example.app.jasper.routinedeveloper_v2.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import java.util.Calendar;
import static android.content.Context.MODE_PRIVATE;

public class MySharedPrefs {

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String DATE = "date";
    private static final String SCOREPLUS = "scorePlus";
    private static final String SCOREMINUS = "scoreMinus";
    private static final String STOREDDAY = "storedDay";
    private static final String FIRSTSTART = "firstStart";
    public String date;
    private String scorePlus;
    private String scoreMinus;
    PrefsCallbackListener prefsCallbackListener;

    private static MySharedPrefs instance;

    public static MySharedPrefs getInstance(){

        if(instance==null) {
            instance=new MySharedPrefs();
        }
        return instance;
    }

    public void saveCurrentDateToPrefs(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int currentday = Calendar.getInstance().DAY_OF_YEAR;
        editor.putInt(STOREDDAY, currentday);
        editor.apply();
    }

    public void loadSharedPrefs(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        date = sharedPreferences.getString(DATE, "");
        scorePlus = sharedPreferences.getString(SCOREPLUS, "0");
        scoreMinus = sharedPreferences.getString(SCOREMINUS, "0");
    }

    public void saveSharedPrefs(Context context, String endingDate, String scorePlus, String scoreMinus) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        this.date = endingDate;
        this.scorePlus = scorePlus;
        this.scoreMinus = scoreMinus;

        editor.putString(DATE, endingDate);
        editor.putString(SCOREPLUS, scorePlus);
        editor.putString(SCOREMINUS, scoreMinus);
        editor.apply();
    }

    public void applyPrefsToView(PrefsCallbackListener prefsCallbackListener) {
        prefsCallbackListener.callbackUpdateView(this.date, this.scorePlus, this.scoreMinus);
    }

    public void firstTimeStartingApp(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        boolean firstStart = sharedPreferences.getBoolean(FIRSTSTART, true);

        if (firstStart) {

            Toast.makeText(context.getApplicationContext(), "Welcome to Routine Developer!", Toast.LENGTH_SHORT).show();

            saveCurrentDateToPrefs(context);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(FIRSTSTART, false);
            editor.apply();
        }
    }

    public void updateScore(Context context, String s, String s1) {
        saveSharedPrefs(context, date, s, s1);
    }

    public void clearDate(Context context, String scorePlus, String scoreMinus) {
        saveSharedPrefs(context, null, scorePlus, scoreMinus);
    }

    public interface PrefsCallbackListener {
        void callbackUpdateView(String date, String scorePlus, String scoreMinus);
    }

    public String getDate() {
        return date;
    }
    public String getScorePlus() {
        return scorePlus;
    }
    public String getScoreMinus() {
        return scoreMinus;
    }
}
