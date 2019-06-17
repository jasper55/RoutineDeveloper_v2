package com.example.app.jasper.routinedeveloper_v2;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import com.example.app.jasper.routinedeveloper_v2.model.MySharedPrefs;
import com.example.app.jasper.routinedeveloper_v2.model.RecyclerViewAdapter;
import com.example.app.jasper.routinedeveloper_v2.model.SQLCRUDOperations;
import com.example.app.jasper.routinedeveloper_v2.model.Todo;
import com.example.app.jasper.routinedeveloper_v2.repository.TodoListRepository;
import com.example.app.jasper.routinedeveloper_v2.viewmodel.MainActivityViewModel;

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

    public void changeDate(RecyclerViewAdapter adapter){
        int currentday = Calendar.getInstance().DAY_OF_YEAR +1;
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        int lastday = sharedPreferences.getInt(STOREDDAY, 0);

        Log.i("RD_", String.valueOf(lastday));
        if (currentday != lastday) {
            summUpCheckBoxes(adapter);
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

    protected void summUpCheckBoxes(RecyclerViewAdapter adapter) {

        MutableLiveData<List<Todo>> todoList = TodoListRepository.getInstance(context).getAllItems();
        MySharedPrefs prefs = MySharedPrefs.getInstance(context);

        String scorePlus = prefs.getScorePlus();
        String scoreMinus = prefs.getScoreMinus();
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
            prefs.setScoreplus(String.valueOf(doneCounter));
            Toast.makeText(context, "All Todos done yesterday", Toast.LENGTH_SHORT).show();
        } else {
            undoneCounter = ++undoneCounter;
            prefs.setScorepMinus(String.valueOf(undoneCounter));
        }
        resetCheckBoxes(mockList);
        adapter.restList(mockList);
        adapter.notifyDataSetChanged();
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
    }

    public void clearScore() {
        mainActivityViewModel.setScorePlus("0");
        mainActivityViewModel.setScoreMinus("0");
//        MySharedPrefs.getInstance(context).updateScore();
    }

    public void clearTargetDate() {
        MySharedPrefs.getInstance(context).clearDate();
    }
}
