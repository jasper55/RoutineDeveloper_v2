package com.example.app.jasper.routinedeveloperv2.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.app.jasper.routinedeveloperv2.viewmodel.MainActivityViewModel;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class MySharedPrefs {

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String ENDING_DATE = "endingDate";
    private static final String SCOREPLUS = "scorePlus";
    private static final String SCOREMINUS = "scoreMinus";
    private static final String STOREDDAY = "storedDay";
    private static final String FIRSTSTART = "firstStart";
    private String endingDate;
    private String currentDay;
    private String scorePlus;
    private String scoreMinus;
    private Context context;
    static MainActivityViewModel mainActivityViewModel;

    private static MySharedPrefs instance;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;


    public static MySharedPrefs getInstance(Context context) {

        if (instance == null) {
            instance = new MySharedPrefs(context);
        }
        return instance;
    }

    public MySharedPrefs(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        this.editor = prefs.edit();
        editor.apply();
    }

    public void setCurrentDate() {
        int currentDay = Calendar.getInstance().DAY_OF_YEAR;
        editor.putInt(STOREDDAY, currentDay);
        editor.apply();
    }

    public void loadSharedPrefs() {
        endingDate = prefs.getString(ENDING_DATE, "");
        scorePlus = prefs.getString(SCOREPLUS, "0");
        scoreMinus = prefs.getString(SCOREMINUS, "0");
    }

    public void saveSharedPrefs(String endingDate, String scorePlus, String scoreMinus) {
        this.endingDate = endingDate;
        this.scorePlus = scorePlus;
        this.scoreMinus = scoreMinus;

        editor.putString(ENDING_DATE, endingDate);
        editor.putString(SCOREPLUS, scorePlus);
        editor.putString(SCOREMINUS, scoreMinus);
        editor.apply();

        mainActivityViewModel.postEndingDate(endingDate);
        mainActivityViewModel.postScorePlus(scorePlus);
        mainActivityViewModel.postScoreMinus(scoreMinus);
    }
    public void applyPrefsToViewModel() {
        mainActivityViewModel.postEndingDate(endingDate);
        mainActivityViewModel.postScorePlus(scorePlus);
        mainActivityViewModel.postScoreMinus(scoreMinus);
    }


    public void firstTimeStartingApp(Context context) {
        boolean firstStart = prefs.getBoolean(FIRSTSTART, true);

        if (firstStart) {

            Toast.makeText(context.getApplicationContext(), "Welcome to Routine Developer!", Toast.LENGTH_SHORT).show();

            setCurrentDate();

            editor.putBoolean(FIRSTSTART, false);
            editor.apply();
        }
    }

    public void updateScore(String plus, String minus) {
        setScoreMinus(minus);
        setScorePlus(plus);
    }

    public void clearEndingDate() {
        editor.putString(ENDING_DATE, null);
        editor.apply();
    }

    public String getEndingDate() {
        return this.endingDate;
    }

    public String getScorePlus() {
        return scorePlus;
    }

    public String getScoreMinus() {
        return this.scoreMinus;
    }

    public void connectViewModel() {
        mainActivityViewModel.postEndingDate(endingDate);
        mainActivityViewModel.postScorePlus(scorePlus);
        mainActivityViewModel.postScoreMinus(scoreMinus);
    }

    public void setScorePlus(String plus) {
        editor.putString(SCOREPLUS,plus);
        mainActivityViewModel.postScorePlus(plus);
    }

    public void setScoreMinus(String minus) {
        editor.putString(SCOREMINUS,minus);
        mainActivityViewModel.postScoreMinus(minus);
    }

    public void clearScore() {
        editor.putString(SCOREPLUS,"0");
        editor.putString(SCOREMINUS,"0");
        mainActivityViewModel.postScorePlus("0");
        mainActivityViewModel.postScoreMinus("0");
    }

    public void saveEndingDate(String date) {
        editor.putString(ENDING_DATE,date);
        editor.apply();
        mainActivityViewModel.postEndingDate(date);
    }

    public String getStoredDate() {
        currentDay = prefs.getString(STOREDDAY, "");
        return currentDay;
    }
}
