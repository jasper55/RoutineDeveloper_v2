package com.example.app.jasper.routinedeveloper_v2.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.app.jasper.routinedeveloper_v2.repository.TodoListRepository;
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
    public String endingDate;
    private String scorePlus;
    private String scoreMinus;
    private static Context context;
    private TodoListRepository repo;

    private static MySharedPrefs instance;
    private SharedPreferences.Editor prefEdidtor;

    public static MySharedPrefs getInstance(Context context){

        if(instance==null) {
            instance=new MySharedPrefs(context);
        }
        return instance;
    }

    public MySharedPrefs(Context context) {
        this.context = context;
//        repo = TodoListRepository.getInstance(context);
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

        endingDate = sharedPreferences.getString(DATE, "");
        scorePlus = sharedPreferences.getString(SCOREPLUS, "0");
        scoreMinus = sharedPreferences.getString(SCOREMINUS, "0");
    }

    public void saveSharedPrefs() {
        TodoListRepository repo = TodoListRepository.getInstance(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if( getEndingDate() != null) {String endingDate = repo.getEndingDate().getValue();
        this.endingDate = endingDate;}


        if( repo.getScorePlus() != null) {
            String scorePlus = repo.getScorePlus().getValue();
            String scoreMinus = repo.getScoreMinus().getValue();
            this.scorePlus = scorePlus;
            this.scoreMinus = scoreMinus;
        }

        editor.putString(DATE, endingDate);
        editor.putString(SCOREPLUS, scorePlus);
        editor.putString(SCOREMINUS, scoreMinus);
        editor.apply();
    }

    public void applyPrefsToView(MainActivityViewModel mainActivityViewModel) {
        mainActivityViewModel.setEndingDate(endingDate);
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

    public String getEndingDate() {
        return this.endingDate;
    }
    public String getScorePlus() {
        return scorePlus;
    }
    public String getScoreMinus() {
        return this.scoreMinus;
    }
}
