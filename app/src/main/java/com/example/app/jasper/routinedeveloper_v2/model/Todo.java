package com.example.app.jasper.routinedeveloper_v2.model;

import java.io.Serializable;

public class Todo implements Serializable {

    long id =-1;
    String name;
    boolean done;

    public Todo(long id, String name, boolean done) {
        this.id = id;
        this.name = name;
        this.done = done;
    }

    public Todo(String name){
        this.name = name;
    }

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
