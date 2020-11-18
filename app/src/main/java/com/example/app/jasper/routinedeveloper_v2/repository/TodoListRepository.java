package com.example.app.jasper.routinedeveloper_v2.repository;

import android.content.Context;
import android.util.Log;

import com.example.app.jasper.routinedeveloper_v2.model.SQLDatabaseHelper;
import com.example.app.jasper.routinedeveloper_v2.model.Todo;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TodoListRepository {

    private static TodoListRepository instance;
    private SQLDatabaseHelper dataBase;
    private SharedPreferenceHelper prefs;

    public static TodoListRepository getInstance(Context context) {
        if (instance == null) {
            instance = new TodoListRepository(context);
        }
        return instance;
    }

    public TodoListRepository(Context context) {
        dataBase = SQLDatabaseHelper.getInstance(context);
        prefs = SharedPreferenceHelper.INSTANCE;
    }

    public List<Todo> getAllItems() {
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



    public void clearTargetDate() {
        prefs.setChallengeEndingDate("");
    }

    public void clearScore() {
        prefs.setUndoneCount(0);
        prefs.setDoneCount(0);
    }

    public int getStoredDay() {
        return prefs.getStoredDay();
    }

    public void setCurrentDay(int currentDay) {
        prefs.updateDate(currentDay);
    }

    public void incrementDoneCounter() {
        int doneCount = prefs.getDoneCount();
        doneCount += 1;
        prefs.setDoneCount(doneCount);
        Log.d("COUNTER", "doneCount incremented");


        int done = prefs.getDoneCount();
        Log.d("COUNTER", "new done value: " + String.valueOf(done));

    }

    public void incrementUndoneCounter() {
        int undoneCount = prefs.getUndoneCount();
        undoneCount += 1;
        prefs.setUndoneCount(undoneCount);
        Log.d("COUNTER", "undoneCount incremented");


        int undone = prefs.getUndoneCount();
        Log.d("COUNTER", "new undone value: " + undone);
    }

    public void setChallengeEndingDate(String endingDate) {
        prefs.setChallengeEndingDate(endingDate);
    }

    public void setFirstStart(boolean b) {
        prefs.setFirstStart(b);
    }

    public void saveList(List<Todo> todos) {
        for (Todo item : todos)
            dataBase.updateItem(item.getId(), item);
    }

    public void addItem(Todo item) {
        dataBase.updateItem(item.getId(), item);
    }

    @Nullable
    public Integer getUndoneCount() {
        return prefs.getUndoneCount();
    }

    @Nullable
    public Integer getDoneCount() {
        return prefs.getDoneCount();
    }

    @Nullable
    public String getChallengeEndingDate() {
        return prefs.getChallengeEndingDate();
    }
}
