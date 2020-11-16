package com.example.app.jasper.routinedeveloper_v2.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.app.jasper.routinedeveloper_v2.model.MySharedPrefs;
import com.example.app.jasper.routinedeveloper_v2.model.SQLDatabaseHelper;
import com.example.app.jasper.routinedeveloper_v2.model.Todo;

import java.util.List;

import androidx.lifecycle.MutableLiveData;

public class TodoListRepository {

    private static TodoListRepository instance;
    private Context context;
    private SQLDatabaseHelper dataBase;

    public static TodoListRepository getInstance(Context context) {
        if (instance == null) {
            instance = new TodoListRepository(context);
        }
        return instance;
    }

    public List<Todo> getAllItems (){
            return dataBase.readAllItems();
        }

    public long createItem(Todo item) {
        return dataBase.createItem(item);
    }
    public void updateItem(long id, Todo item) {
        dataBase.updateItem(id, item);
    }
    public void deleteItem(long id) {
        dataBase.deleteItem(id);
    }
    public List<Todo> readAllItems() {
        return dataBase.readAllItems();
    }
    public Todo readItem(long id) {
        return dataBase.readItem(id);
    }

//    public void saveScorePlus(String score) {
//        SharedPreferenceHelper.INSTANCE.setScorePlus(score);
//    }

//    public void saveScoreMinus(String score) { SharedPreferenceHelper.INSTANCE.setScoreMinus(score); }

    public TodoListRepository(Context context) {
        this.context = context;
        dataBase = SQLDatabaseHelper.getInstance(context);
    }
}
