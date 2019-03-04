package com.example.app.jasper.routinedeveloper_v2.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.ContextMenu;
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
    public static String date;
    private static String prefs_scorePlus;
    private static String prefs_scoreMinus;

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
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_YEAR);
        editor.putInt(STOREDDAY, currentDay);
        editor.apply();
    }

    public void loadSharedPrefs(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        date = sharedPreferences.getString(DATE, "");
        prefs_scorePlus = sharedPreferences.getString(SCOREPLUS, "0");
        prefs_scoreMinus = sharedPreferences.getString(SCOREMINUS, "0");
    }

    public void saveSharedPrefs(Context context,String endingDate, String scorePlus, String scoreMinus) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(DATE, endingDate);
        editor.putString(SCOREPLUS, scorePlus);
        editor.putString(SCOREMINUS, scoreMinus);
        editor.apply();
    }

//    public void applyPrefsToView(Context context,TextView challengeEndingDate, TextView textViewPlus, TextView textViewMinus) {
//        challengeEndingDate.setText(date);
//        textViewPlus.setText(prefs_scorePlus);
//        textViewMinus.setText(prefs_scoreMinus);
//    }


    public void firstTimeStartingApp(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        boolean firstStart = sharedPreferences.getBoolean(FIRSTSTART, true);

        if (firstStart) {

            Toast.makeText(context, "Welcome to Routine Developer!", Toast.LENGTH_SHORT).show();
            saveCurrentDateToPrefs(context);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(FIRSTSTART, false);
            editor.apply();
        }
    }



}
