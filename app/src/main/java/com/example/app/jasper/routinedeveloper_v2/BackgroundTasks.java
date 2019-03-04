package com.example.app.jasper.routinedeveloper_v2;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app.jasper.routinedeveloper_v2.model.MySharedPrefs;
import com.example.app.jasper.routinedeveloper_v2.model.SQLCRUDOperations;
import com.example.app.jasper.routinedeveloper_v2.model.Todo;

import java.util.Calendar;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class BackgroundTasks {

    private MySharedPrefs myPrefs;
    private static List<Todo> todoList;
    private static Context context;
    private static SQLCRUDOperations crudOperations;
    private static TextView challengeEndingDate, textViewPlus, textViewMinus;
    private static Todo item;

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String DATE = "date";
    private static final String SCOREPLUS = "scorePlus";
    private static final String SCOREMINUS = "scoreMinus";
    private static final String STOREDDAY = "storedDay";
    private static final String FIRSTSTART = "firstStart";
    private String date, prefs_scorePlus, prefs_scoreMinus;

    public void loadPrefs(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        date = sharedPreferences.getString(DATE, "");
        prefs_scorePlus = sharedPreferences.getString(SCOREPLUS, "0");
        prefs_scoreMinus = sharedPreferences.getString(SCOREMINUS, "0");
    }

    public void checkIsDateChanged(String date, CharSequence text, CharSequence textViewMinusText) {

        int currentday = Calendar.getInstance().DAY_OF_YEAR;
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        int lastday = sharedPreferences.getInt(STOREDDAY, 0);

        if (currentday != lastday) {

            summUpCheckBoxes(todoList);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(STOREDDAY, currentday);
            editor.apply();
        }
    }

    protected void summUpCheckBoxes(List<Todo> todoList) {

        loadPrefs();
        int doneCounter = Integer.parseInt(prefs_scorePlus);
        int undoneCounter = Integer.parseInt(prefs_scoreMinus);

        int todoListSize = todoList.size();
        int done = 0;

        for (int i = 0; i < todoListSize; i++) {
            item = todoList.get(i);
            Log.i("sumUpdone", String.valueOf(item.isDone()));
            if (item.isDone()) {
                done++;
            }
        }

        if (done == todoListSize) {
            doneCounter = ++doneCounter;
            textViewPlus.setText(String.valueOf(doneCounter));
            Toast.makeText(context, "All Todos done yesterday", Toast.LENGTH_SHORT).show();
        } else {
            undoneCounter = ++undoneCounter;
            textViewMinus.setText(String.valueOf(undoneCounter));
        }

        myPrefs.saveSharedPrefs(challengeEndingDate, textViewPlus, textViewMinus);
        resetCheckBoxes();
    }

    private void resetCheckBoxes() {
        int size = todoList.size();
        Todo item;

        for (int i = 0; i < size; i++) {
            item = todoList.get(i);
            long id = item.getId();
            item.setDone(false);
            crudOperations.updateItem(id, item);
        }
    }

    public void clearScore() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(DATE, challengeEndingDate.getText().toString());
        editor.putString(SCOREPLUS, "0");
        editor.putString(SCOREMINUS, "0");
        prefs_scorePlus = "0";
        prefs_scoreMinus = "0";

        editor.apply();
        myPrefs.applyPrefsToView(challengeEndingDate, textViewPlus, textViewMinus);
    }

    public void clearTargetDate() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        date = null;
        editor.putString(DATE, date);
        editor.putString(SCOREPLUS, textViewPlus.getText().toString());
        editor.putString(SCOREMINUS, textViewMinus.getText().toString());

        editor.apply();
        myPrefs.applyPrefsToView(challengeEndingDate, textViewPlus, textViewMinus);
    }

    public BackgroundTasks(Context context, final List<Todo> todoList, MySharedPrefs myPrefs,
                           SQLCRUDOperations crudOperations,
                           TextView challengeEndingDate, TextView textViewPlus, TextView textViewMinus) {

        this.context = context;
        this.todoList = todoList;
        this.myPrefs = myPrefs;
        this.crudOperations = crudOperations;
        this.challengeEndingDate = challengeEndingDate;
        this.textViewMinus = textViewMinus;
        this.textViewPlus = textViewPlus;
    }
}
