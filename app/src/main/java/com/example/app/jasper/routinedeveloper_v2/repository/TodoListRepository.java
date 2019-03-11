package com.example.app.jasper.routinedeveloper_v2.repository;

import android.arch.lifecycle.MutableLiveData;

import com.example.app.jasper.routinedeveloper_v2.model.RoomCRUDOperations;
import com.example.app.jasper.routinedeveloper_v2.model.SQLCRUDOperations;
import com.example.app.jasper.routinedeveloper_v2.model.Todo;

import java.util.List;

public class TodoListRepository {

    private static List<Todo> todoList;
    private static TodoListRepository instance;

    public static TodoListRepository getInstance(){
        if (instance == null){
            instance = new TodoListRepository();
        }
        return instance;
    }

    public MutableLiveData<List<Todo>> getTodoList(SQLCRUDOperations crudOperations) {
        todoList = crudOperations.readAllItems();
        MutableLiveData<List<Todo>> data = new MutableLiveData<>();
        data.setValue(todoList);
        return data;
    }

    public MutableLiveData<List<Todo>> getTodoList(RoomCRUDOperations roomCRUDOperations) {
        todoList = roomCRUDOperations.myRDao().getTodolist();
        MutableLiveData<List<Todo>> data = new MutableLiveData<>();
        data.setValue(todoList);
        return data;
    }
}
