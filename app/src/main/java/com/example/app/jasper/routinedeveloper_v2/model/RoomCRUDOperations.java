package com.example.app.jasper.routinedeveloper_v2.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;


@Database(entities = {Todo.class}, version = 1)
public abstract class RoomCRUDOperations extends RoomDatabase {

//    private static RoomCRUDOperations instance;
//
//    public static RoomCRUDOperations getInstance(){
//        if (instance == null){
//            instance = new RoomCRUDOperations();
//        }
//        return instance;
//    }


//    @Override
//    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
//        return null;
//    }
//
//    @Override
//    protected InvalidationTracker createInvalidationTracker() {
//        return null;
//    }


    public abstract RoomAccessObject myRDao();

}



