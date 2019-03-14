package com.example.app.jasper.routinedeveloperv2;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.example.app.jasper.routinedeveloperv2.model.MySharedPrefs;
import com.example.app.jasper.routinedeveloperv2.model.Todo;
import com.example.app.jasper.routinedeveloperv2.repository.TodoListRepository;
import com.example.app.jasper.routinedeveloperv2.viewmodel.MainActivityViewModel;

import java.util.Calendar;
import java.util.List;

public class BackgroundTasks {

    private static Context context;
    private MySharedPrefs prefs;

    private Todo item;

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String SCOREPLUS = "scorePlus";
    private static final String SCOREMINUS = "scoreMinus";

    private static BackgroundTasks instance;

    public BackgroundTasks(Context context, MySharedPrefs prefs) {
        this.context = context;
        this.prefs = prefs;
    }

    public static BackgroundTasks getInstance(Context context, MySharedPrefs prefs){

        if(instance==null) {
            instance=new BackgroundTasks(context, prefs);
        }
        return instance;
    }

    public void changeDate(MainActivityViewModel mainActivityViewModel){
        int currentday = Calendar.getInstance().DAY_OF_YEAR +1;
        int lastday = Integer.getInteger(prefs.getEndingDate());

        Log.i("RD_", String.valueOf(lastday));
        if (currentday != lastday) {
            summUpCheckBoxes(mainActivityViewModel);
        }
    }

//
//    public boolean checkHasDateChanged() {
//        int currentday = Calendar.getInstance().DAY_OF_YEAR;
//        Log.i("RD_", String.valueOf(currentday));
//
//        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
//        int lastday = sharedPreferences.getInt(STOREDDAY, 0);
//
//        Log.i("RD_", String.valueOf(lastday));
//        if (currentday != lastday) {
//            MySharedPrefs.getInstance(context).setCurrentDate();
//            return true;
//        }
//        else{
//            return false;
//        }
//    }


    public boolean checkHasDateChanged() {
        int currentday = Calendar.getInstance().DAY_OF_YEAR;

        int lastday = Integer.parseInt(prefs.getStoredDate());

        if (currentday != lastday) {
            prefs.setCurrentDate();
            return true;
        }
        else{
            return false;
        }
    }

    protected void summUpCheckBoxes(MainActivityViewModel mainActivityViewModel) {

        try {
            MutableLiveData<List<Todo>> todoList = mainActivityViewModel.getTodoList();
            String scorePlus = mainActivityViewModel.getScorePlus().getValue();
            String scoreMinus = mainActivityViewModel.getScoreMinus().getValue();
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
                prefs.setScorePlus(String.valueOf(doneCounter));
                mainActivityViewModel.postScorePlus(String.valueOf(doneCounter));
                Toast.makeText(context, "All Todos done yesterday", Toast.LENGTH_SHORT).show();
            } else {
                undoneCounter = ++undoneCounter;
                prefs.setScorePlus(String.valueOf(undoneCounter));
                mainActivityViewModel.postScoreMinus(String.valueOf(undoneCounter));
            }
            resetCheckBoxes(mockList);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
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
            TodoListRepository.getInstance(context).updateItem(id, item);
            //Todo make sure that ViewModel is updeted by updating the RecyclerViewlist
//            SQLCRUDOperations.getInstance(context).updateItem(id, item);
        }
//        mainActivityViewModel.setTodoList(SQLCRUDOperations.getInstance(context).readAllItems());
    }

    public void clearScore() {
        MySharedPrefs myPrefs = MySharedPrefs.getInstance(context);
        myPrefs.clearScore();
    }

    public void clearTargetDate() {
        MySharedPrefs.getInstance(context).clearEndingDate();
    }
}
