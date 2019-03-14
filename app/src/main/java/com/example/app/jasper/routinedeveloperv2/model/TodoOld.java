package com.example.app.jasper.routinedeveloperv2.model;

import java.io.Serializable;

public class TodoOld implements Serializable {

    long id =-1;
    String name;
    boolean done;

    public TodoOld(long id, String name, boolean done) {
        this.id = id;
        this.name = name;
        this.done = done;
    }

    public TodoOld(String name){
        this.name = name;
    }

    public TodoOld() {
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
