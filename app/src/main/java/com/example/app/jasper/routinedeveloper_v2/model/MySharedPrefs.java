package com.example.app.jasper.routinedeveloper_v2.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.TextView;
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
    private String date, prefs_scorePlus, prefs_scoreMinus;
    private Context context;

    public void saveCurrentDateToPrefs() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_YEAR);
        editor.putInt(STOREDDAY, currentDay);
        editor.apply();
    }

    public void loadSharedPrefs() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        date = sharedPreferences.getString(DATE, "");
        prefs_scorePlus = sharedPreferences.getString(SCOREPLUS, "0");
        prefs_scoreMinus = sharedPreferences.getString(SCOREMINUS, "0");
    }

    public void saveSharedPrefs(TextView challengeEndingDate, TextView textViewPlus, TextView textViewMinus) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(DATE, challengeEndingDate.getText().toString());
        editor.putString(SCOREPLUS, textViewPlus.getText().toString());
        editor.putString(SCOREMINUS, textViewMinus.getText().toString());
        editor.apply();
    }

    public void applyPrefsToView(TextView challengeEndingDate, TextView textViewPlus, TextView textViewMinus) {
        challengeEndingDate.setText(date);
        textViewPlus.setText(prefs_scorePlus);
        textViewMinus.setText(prefs_scoreMinus);
    }


    public void firstTimeStartingApp() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        boolean firstStart = sharedPreferences.getBoolean(FIRSTSTART, true);

        if (firstStart) {

            Toast.makeText(context, "Welcome to Routine Developer!", Toast.LENGTH_SHORT).show();
            saveCurrentDateToPrefs();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(FIRSTSTART, false);
            editor.apply();
        }
    }


    public MySharedPrefs(Context context) {
        this.context = context;
    }
}
