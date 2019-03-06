package com.example.app.jasper.routinedeveloper_v2;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import com.example.app.jasper.routinedeveloper_v2.model.MySharedPrefs;
import com.example.app.jasper.routinedeveloper_v2.model.SQLCRUDOperations;
import com.example.app.jasper.routinedeveloper_v2.model.Todo;
import java.util.Calendar;
import java.util.List;
import static android.content.Context.MODE_PRIVATE;

public class BackgroundTasks {

    private Context context;
    private Todo item;

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String DATE = "endingDate";
    private static final String SCOREPLUS = "scorePlus";
    private static final String SCOREMINUS = "scoreMinus";
    private static final String STOREDDAY = "storedDay";
    private static final String FIRSTSTART = "firstStart";
    private String scorePlus, scoreMinus;
    CallbackListener callbackListener;

    private static BackgroundTasks instance;

    public static BackgroundTasks getInstance(){

        if(instance==null) {
            instance=new BackgroundTasks();
        }
        return instance;
    }

    public void loadPrefs() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        scorePlus = sharedPreferences.getString(SCOREPLUS, "0");
        scoreMinus = sharedPreferences.getString(SCOREMINUS, "0");
    }

    public boolean checkHasDateChanged() {
        int currentday = Calendar.getInstance().DAY_OF_YEAR;
        Log.i("RD_", String.valueOf(currentday));
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        int lastday = sharedPreferences.getInt(STOREDDAY, 0);

        Log.i("RD_", String.valueOf(lastday));
        if (currentday != lastday) {
            MySharedPrefs.getInstance().saveCurrentDateToPrefs(context);
            return true;
        }
        else{
            return false;
        }
    }

    protected void summUpCheckBoxes(List<Todo> todoList) {

        loadPrefs();
        int doneCounter = Integer.parseInt(scorePlus);
        int undoneCounter = Integer.parseInt(scoreMinus);

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
            scorePlus = String.valueOf(doneCounter);
            callbackListener.updatePlusView(String.valueOf(doneCounter));
            Toast.makeText(context, "All Todos done yesterday", Toast.LENGTH_SHORT).show();
        } else {
            undoneCounter = ++undoneCounter;
            scoreMinus = String.valueOf(undoneCounter);
            callbackListener.updateMinusView(String.valueOf(undoneCounter));
        }

        MySharedPrefs.getInstance().updateScore(context, scorePlus, scoreMinus);
        resetCheckBoxes(todoList);
    }

    public void init(Context context, String scorePlus, String scoreMinus, CallbackListener backgroundtaskListener) {
        this.context = context;
        this.scorePlus = scorePlus;
        this.scoreMinus = scoreMinus;
        this.callbackListener = backgroundtaskListener;
    }

    interface CallbackListener {
        void updateMinusView(String data);
        void updatePlusView(String data);
    }

    private void resetCheckBoxes(List<Todo> todoList) {
        int size = todoList.size();
        Todo item;

        for (int i = 0; i < size; i++) {
            item = todoList.get(i);
            long id = item.getId();
            item.setDone(false);
            SQLCRUDOperations.getInstance(context).updateItem(id, item);
        }
    }

    public void clearScore() {
        MySharedPrefs.getInstance().updateScore(context, "0", "0");
    }

    public void clearTargetDate() {
        MySharedPrefs.getInstance().clearDate(context, scorePlus, scoreMinus);
    }
}
