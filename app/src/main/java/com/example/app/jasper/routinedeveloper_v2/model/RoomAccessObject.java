package com.example.app.jasper.routinedeveloper_v2.model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface RoomAccessObject {

    @Insert
    public void addItem(Todo todo);

    @Query("select * from Todos")  // ist aus TodoKlasse
    public List<Todo> getTodolist();

    @Query("update Todos where Todo.id = id")
    public Todo updateItem(long id);

}
