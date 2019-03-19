package com.example.app.jasper.routinedeveloper_v2.repository;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import com.example.app.jasper.routinedeveloper_v2.model.MySharedPrefs;
import com.example.app.jasper.routinedeveloper_v2.model.SQLCRUDOperations;
import com.example.app.jasper.routinedeveloper_v2.model.Todo;

import java.util.List;

public class TodoListRepository {

    private MutableLiveData<List<Todo>> todoList = new MutableLiveData<>();
    private static TodoListRepository instance;
    private MutableLiveData<String> scorePlus = new MutableLiveData<>();
    private MutableLiveData<String> scoreMinus = new MutableLiveData<>();
    private MutableLiveData<String> endingDate = new MutableLiveData<>();

    public static TodoListRepository getInstance(Context context) {
        if (instance == null) {
            instance = new TodoListRepository(context);
        }
        return instance;
    }


    public void bindData(Context context) {
        MySharedPrefs prefs = MySharedPrefs.getInstance(context);
        todoList.postValue(SQLCRUDOperations.getInstance(context).readAllItems());

        scorePlus.postValue(prefs.getScorePlus());
        scoreMinus.postValue(prefs.getScoreMinus());
        endingDate.postValue(prefs.getEndingDate());
    }

        public MutableLiveData<List<Todo>> getAllItems (Context context){
            todoList.postValue(SQLCRUDOperations.getInstance(context).readAllItems());
            return todoList;
        }

        public MutableLiveData<String> getScorePlus(){
            return scorePlus;
        }

        public MutableLiveData<String> getScoreMinus(){
            return scoreMinus;
        }

        public MutableLiveData<String> getEndingDate(){
            return endingDate;
        }


    public TodoListRepository(Context context) {
        bindData(context);
    }
}
