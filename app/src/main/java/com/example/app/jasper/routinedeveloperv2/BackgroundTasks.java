package com.example.app.jasper.routinedeveloperv2;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import com.example.app.jasper.routinedeveloperv2.model.MySharedPrefs;
import com.example.app.jasper.routinedeveloperv2.model.SQLCRUDOperations;
import com.example.app.jasper.routinedeveloperv2.model.Todo;
import com.example.app.jasper.routinedeveloperv2.viewmodel.MainActivityViewModel;

import java.util.Calendar;
import java.util.List;
import static android.content.Context.MODE_PRIVATE;

public class BackgroundTasks {

    private static Context context;
    static MainActivityViewModel mainActivityViewModel;
    private Todo item;

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String SCOREPLUS = "scorePlus";
    private static final String SCOREMINUS = "scoreMinus";
    private static final String STOREDDAY = "storedDay";

    private static BackgroundTasks instance;

    public BackgroundTasks(Context context, MainActivityViewModel mainActivityViewModel) {
        this.context = context;
        this.mainActivityViewModel = mainActivityViewModel;
    }

    public static BackgroundTasks getInstance(Context context, MainActivityViewModel mainActivityViewModel){

        if(instance==null) {
            instance=new BackgroundTasks(context, mainActivityViewModel);
        }
        return instance;
    }

    public void loadPrefs() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        String scorePlus = sharedPreferences.getString(SCOREPLUS, "0");
        String scoreMinus = sharedPreferences.getString(SCOREMINUS, "0");

        mainActivityViewModel.setScorePlus(scorePlus);
        mainActivityViewModel.setScoreMinus(scoreMinus);
    }

    public void changeDate(){
        int currentday = Calendar.getInstance().DAY_OF_YEAR +1;
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        int lastday = sharedPreferences.getInt(STOREDDAY, 0);

        Log.i("RD_", String.valueOf(lastday));
        if (currentday != lastday) {
            summUpCheckBoxes();
        }
    }


    public boolean checkHasDateChanged() {
        int currentday = Calendar.getInstance().DAY_OF_YEAR;
        Log.i("RD_", String.valueOf(currentday));

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        int lastday = sharedPreferences.getInt(STOREDDAY, 0);

        Log.i("RD_", String.valueOf(lastday));
        if (currentday != lastday) {
            MySharedPrefs.getInstance(context).updateDate();
            return true;
        }
        else{
            return false;
        }
    }

    protected void summUpCheckBoxes() {

        MutableLiveData<List<Todo>> todoList = mainActivityViewModel.getTodoList();

        String scorePlus = mainActivityViewModel.getScorePlus().toString();
        String scoreMinus = mainActivityViewModel.getScoreMinus().toString();
        int doneCounter = Integer.parseInt(scorePlus);
        int undoneCounter = Integer.parseInt(scoreMinus);


        int todoListSize = todoList.getValue().size();
        int done = 0;
        List<Todo> mockList = todoList.getValue();

        for (int i = 0; i < todoListSize; i++) {
            item = mockList.get(i);
            Log.i("sumUpdone", String.valueOf(item.isDone()));
            if (item.isDone()) {
                done++;
            }
        }

        if (done == todoListSize) {
            doneCounter = ++doneCounter;
            mainActivityViewModel.setScorePlus(String.valueOf(doneCounter));
            Toast.makeText(context, "All Todos done yesterday", Toast.LENGTH_SHORT).show();
        } else {
            undoneCounter = ++undoneCounter;
            mainActivityViewModel.setScoreMinus(String.valueOf(undoneCounter));
        }

        MySharedPrefs.getInstance(context).updateScore();
        resetCheckBoxes(mockList);
    }

//    public void init(Context context, String scorePlus, String scoreMinus) {
//        this.context = context;
//        this.scorePlus = scorePlus;
//        this.scoreMinus = scoreMinus;
//    }

    private void resetCheckBoxes(List<Todo> todoList) {
        int size = todoList.size();
        Todo item;

        for (int i = 0; i < size; i++) {
            item = todoList.get(i);
            long id = item.getId();
            item.setDone(false);
            SQLCRUDOperations.getInstance(context).updateItem(id, item);
        }
        mainActivityViewModel.setTodoList(SQLCRUDOperations.getInstance(context).readAllItems());
    }

    public void clearScore() {
        mainActivityViewModel.setScorePlus("0");
        String plus = mainActivityViewModel.getScorePlus().toString();
        mainActivityViewModel.setScoreMinus("0");
        String minus = mainActivityViewModel.getScoreMinus().toString();
        MySharedPrefs.getInstance(context).updateScore();
    }

    public void clearTargetDate() {
        MySharedPrefs.getInstance(context).clearDate(context);
    }
}
