package com.example.app.jasper.routinedeveloper_v2.repository;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.os.AsyncTask;

import com.example.app.jasper.routinedeveloper_v2.model.MySharedPrefs;
import com.example.app.jasper.routinedeveloper_v2.model.TodoRoomDatabase;
import com.example.app.jasper.routinedeveloper_v2.model.SQLCRUDOperations;
import com.example.app.jasper.routinedeveloper_v2.model.Todo;

import java.util.List;

public class TodoListRepository {

    private TodoRoomDatabase roomDatabase;
    private static TodoListRepository instance;
    private MutableLiveData<String> scorePlus;
    private MutableLiveData<String> scoreMinus;
    private MutableLiveData<String> endingDate;
    private MutableLiveData<List<Todo>> todoList;

    public TodoListRepository(Context context) {
        initLiveData();
        setValues(context);
    }

    public static TodoListRepository getInstance(Context context){
        if (instance == null){
            instance = new TodoListRepository(context);
        }
        return instance;
    }

    public void initLiveData(){
        todoList = new MutableLiveData<>();
        scorePlus = new MutableLiveData<>();
        scoreMinus = new MutableLiveData<>();
        endingDate = new MutableLiveData<>();
    }

    public void setValues(Context context){
        roomDatabase = TodoRoomDatabase.getInstance(context);
        List<Todo> list = roomDatabase.roomCRUDOperations().getAllItems();
        MySharedPrefs prefs = MySharedPrefs.getInstance(context);

        todoList.setValue(list);
        scorePlus.setValue(prefs.getScorePlus());
        scoreMinus.setValue(prefs.getScoreMinus());
        endingDate.setValue(prefs.getEndingDate());
    }

    public MutableLiveData<List<Todo>> getAllItems() {
        return todoList;
    }

//    public Todo readItem(long id){
//        Todo todo = roomDatabase.roomCRUDOperations().readItem(id);
//        return todo;
//    }

    public void updateItem(final Todo item){
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                roomDatabase.roomCRUDOperations().updateItem(item);
                return null;
            }
        }.execute();
    }

    public void createItem(final Todo item){

        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                roomDatabase.roomCRUDOperations().createItem(item);
                return null;
            }
        }.execute();
    }

    public void deleteItem(final Todo item){
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                roomDatabase.roomCRUDOperations().deleteItem(item);
                return null;
            }
        }.execute();
    }




    public MutableLiveData<List<Todo>> getTodoListOld(SQLCRUDOperations crudOperations) {
        List<Todo> todoList;
        todoList = crudOperations.readAllItems();
        MutableLiveData<List<Todo>> data = new MutableLiveData<>();
        data.setValue(todoList);
        return data;
    }



    public void syncDatabases() {

    }
}
