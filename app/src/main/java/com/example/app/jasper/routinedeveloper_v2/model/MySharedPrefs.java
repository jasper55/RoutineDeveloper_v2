package com.example.app.jasper.routinedeveloper_v2.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.app.jasper.routinedeveloper_v2.viewmodel.MainActivityViewModel;

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
    private static Context context;
    static MainActivityViewModel mainActivityViewModel;

    private static MySharedPrefs instance;
    private SharedPreferences.Editor prefEdidtor;

    public static MySharedPrefs getInstance(Context context){

        if(instance==null) {
            instance=new MySharedPrefs(context);
            instance.scoreMinus = "0";
            instance.scorePlus = "0";
        }
        return instance;
    }

    public MySharedPrefs(Context context) {
        this.context = context;
    }

    public void updateDate() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int currentday = Calendar.getInstance().DAY_OF_YEAR;
        editor.putInt(STOREDDAY, currentday);
        editor.apply();
    }

    public void loadSharedPrefs() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        date = sharedPreferences.getString(DATE, "");
        scorePlus = sharedPreferences.getString(SCOREPLUS, "0");
        scoreMinus = sharedPreferences.getString(SCOREMINUS, "0");
    }

    public void saveSharedPrefs() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String endingDate = mainActivityViewModel.getEndingDate().toString();
        String scorePlus = mainActivityViewModel.getScorePlus().toString();
        String scoreMinus = mainActivityViewModel.getScoreMinus().toString();

        date = endingDate;
        this.scorePlus = scorePlus;
        this.scoreMinus = scoreMinus;

        editor.putString(DATE, endingDate);
        editor.putString(SCOREPLUS, scorePlus);
        editor.putString(SCOREMINUS, scoreMinus);
        editor.apply();
    }

    public void applyPrefsToView(MainActivityViewModel mainActivityViewModel) {
        mainActivityViewModel.setEndingDate(date);
        mainActivityViewModel.setScorePlus(scorePlus);
        mainActivityViewModel.setScoreMinus(scoreMinus);
    }

    public void firstTimeStartingApp(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        boolean firstStart = sharedPreferences.getBoolean(FIRSTSTART, true);

        if (firstStart) {

            Toast.makeText(context.getApplicationContext(), "Welcome to Routine Developer!", Toast.LENGTH_SHORT).show();

            updateDate();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(FIRSTSTART, false);
            editor.apply();
        }
    }

    public void updateScore() {
        saveSharedPrefs();
    }

    public void clearDate(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String endingDate = null;

        editor.putString(DATE, endingDate);
        editor.apply();
    }

    public String getDate() {
        return this.date;
    }
    public String getScorePlus() {
        return scorePlus;
    }
    public String getScoreMinus() {
        return this.scoreMinus;
    }

    public void connectViewModel(MainActivityViewModel mainActivityViewModel) {
        mainActivityViewModel.setEndingDate(date);
        mainActivityViewModel.setScorePlus(scorePlus);
        mainActivityViewModel.setScoreMinus(scoreMinus);
    }
}
