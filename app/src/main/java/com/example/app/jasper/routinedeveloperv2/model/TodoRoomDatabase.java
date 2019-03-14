package com.example.app.jasper.routinedeveloperv2.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;


@Database(entities = {Todo.class}, version = 1)
public abstract class TodoRoomDatabase extends RoomDatabase {

    public abstract RoomCRUDOperations roomCRUDOperations();

    private static TodoRoomDatabase instance;
    private static final String DATABASE_NAME = "TODO_ROOM_DB";

    public static TodoRoomDatabase getInstance(Context context) {
//        synchronized (TodoRoomDatabase.class) {
            instance = Room.databaseBuilder(context,
                    TodoRoomDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
//        }
        return instance;
    }
}



