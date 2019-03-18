package com.example.app.jasper.routinedeveloper_v2.model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface RoomCRUDOperations {

    @Insert
    long createItem(Todo todo);

    @Query("select * from Todos")  // Todos ist aus TodoKlasse @Entity (tableName)
    List<Todo> getAllItems();

    @Update
    void updateItem(Todo item);

//    @Query("SELECT * FROM Todos WHERE id=:id")
//    Todo readItem(long id);

    @Delete
    void deleteItem(Todo item);


//    All of the parameters of these CRUD annotated methods must either be classes annotated with Entity or collections/array of it.
}
