package com.example.app.jasper.routinedeveloperv2.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "Todos")
public class Todo implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "Todo_name")
    private String name;

    @ColumnInfo(name = "Todo_status")
    private boolean done;

//    public Todo(long id, String name, boolean done) {
//        this.id = id;
//        this.name = name;
//        this.done = done;
//    }

//    public Todo(String name){
//        this.name = name;
//    }
//
    public Todo() {
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public boolean isDone() {
        return this.done;
    }
    public void setDone(boolean done) {
        this.done = done;
    }

    public String toString(){
        return this.name;
    }
}
