package com.example.app.jasper.routinedeveloper_v2.repository;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.os.AsyncTask;

import com.example.app.jasper.routinedeveloper_v2.model.TodoRoomDatabase;
import com.example.app.jasper.routinedeveloper_v2.model.SQLCRUDOperations;
import com.example.app.jasper.routinedeveloper_v2.model.Todo;

import java.util.List;

public class TodoListRepository {


    private TodoRoomDatabase roomDatabase;
    private static TodoListRepository instance;

    public TodoListRepository(Context context) {
        roomDatabase = TodoRoomDatabase.getInstance(context);
    }

    public static TodoListRepository getInstance(Context context){
        if (instance == null){
            instance = new TodoListRepository(context);
        }
        return instance;
    }
//    private static TodoRoomDatabase init(final Context context) {
//        instance = Room.databaseBuilder(context,
//                TodoRoomDatabase.class, DATABASE_NAME)
//                .allowMainThreadQueries().build();
//        return instance;
//    }


//    private static TodoListRepository instance = null;
//
//    public static TodoListRepository getInstance() {
//        if (instance == null) {
//            instance = new TodoListRepository();
//        }
//        return instance;
//    }



//    public MutableLiveData<List<Todo>> getAllItems(Context context, TodoRoomDatabase TodoRoomDatabase) {
//        todoList = TodoRoomDatabase.getInstance(context).roomCRUDOperations().getTodolist();
//        MutableLiveData<List<Todo>> data = new MutableLiveData<>();
//        data.setValue(todoList);
//        return data;
//    }

    public MutableLiveData<List<Todo>> getAllItems() {

        final MutableLiveData<List<Todo>> data = new MutableLiveData<>();

        new AsyncTask<Void, Void, List<Todo>>(){

            @Override
            protected List<Todo> doInBackground(Void... voids) {
                List<Todo> todoList = roomDatabase.roomCRUDOperations().getAllItems();
                return todoList;
            }

            @Override
            protected void onPostExecute(List<Todo> todoList) {
                data.setValue(todoList);
            }
        }.execute();
        return data;
    }

    public Todo readItem(long id){
        Todo todo = roomDatabase.roomCRUDOperations().readItem(id);
        return todo;
    }

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



//    private static volatile TodoListRepository instance = null;

//    public static synchronized TodoListRepository getInstance(Context context) {
//
//        if (instance == null){
//            init(context);
//        }
//        return instance;
//    }
//
//    public static boolean isInited() {
//        return instance != null;
//    }




    public void syncDatabases() {

    }
}
