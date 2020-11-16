package com.example.app.jasper.routinedeveloper_v2;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import com.example.app.jasper.routinedeveloper_v2.model.MySharedPrefs;
import com.example.app.jasper.routinedeveloper_v2.model.RecyclerViewAdapter;
import com.example.app.jasper.routinedeveloper_v2.model.SQLDatabaseHelper;
import com.example.app.jasper.routinedeveloper_v2.model.Todo;
import com.example.app.jasper.routinedeveloper_v2.repository.SharedPreferenceHelper;
import com.example.app.jasper.routinedeveloper_v2.repository.TodoListRepository;
import com.example.app.jasper.routinedeveloper_v2.viewmodel.MainActivityViewModel;

import java.util.Calendar;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.example.app.jasper.routinedeveloper_v2.repository.SharedPreferenceHelper.SHARED_PREFS;
import static com.example.app.jasper.routinedeveloper_v2.repository.SharedPreferenceHelper.STORED_DAY;

public class BackgroundTasks {

    private static Context context;
    static MainActivityViewModel mainActivityViewModel;
    private Todo item;


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

    public void changeDate(RecyclerViewAdapter adapter){
        int currentday = Calendar.getInstance().DAY_OF_YEAR +1;
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        int lastday = sharedPreferences.getInt(STORED_DAY, 0);

        Log.i("RD_", String.valueOf(lastday));
        if (currentday != lastday) {
            summUpCheckBoxes(adapter);
        }
    }


    public boolean checkHasDateChanged() {
        boolean hasChanged;
        int currentday = Calendar.getInstance().DAY_OF_YEAR;
        Log.i("RD_", String.valueOf(currentday));

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        int lastday = sharedPreferences.getInt(STORED_DAY, 0);

        Log.i("RD_", String.valueOf(lastday));
        if (currentday != lastday) {
            MySharedPrefs.getInstance(context).updateDate();
            hasChanged = true;
        }
        else{
            hasChanged = false;
        }
        return hasChanged;
    }

    protected void summUpCheckBoxes(RecyclerViewAdapter adapter) {
        List<Todo> todoList = TodoListRepository.getInstance(context).getAllItems();

        int doneCounter = SharedPreferenceHelper.INSTANCE.getDoneCount();
        int undoneCounter = SharedPreferenceHelper.INSTANCE.getUndoneCount();


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
            doneCounter += 1;
            SharedPreferenceHelper.INSTANCE.setDoneCount(doneCounter);
            Toast.makeText(context, "All Todos done yesterday", Toast.LENGTH_SHORT).show();
        } else {
            undoneCounter += 1;
            SharedPreferenceHelper.INSTANCE.setUndoneCount(undoneCounter);
        }
        unCheckAllItems(todoList);
        adapter.loadListFromDB();
    }

    private void unCheckAllItems(List<Todo> todoList) {
        int size = todoList.size();
        Todo item;

        for (int i = 0; i < size; i++) {
            item = todoList.get(i);
            long id = item.getId();
            item.setDone(false);
            SQLDatabaseHelper.getInstance(context).updateItem(id, item);
        }
    }

    public void clearScore() {
        mainActivityViewModel.setDoneCounter("0");
        mainActivityViewModel.setUndoneCounter("0");
        SharedPreferenceHelper.INSTANCE.setUndoneCount(0);
        SharedPreferenceHelper.INSTANCE.setDoneCount(0);
    }

    public void clearTargetDate() {
        SharedPreferenceHelper.INSTANCE.setChallengeEndingDate("");
        mainActivityViewModel.setChallengeEndingDate("");
    }
}
