package com.example.app.jasper.routinedeveloper_v2.model;

public class LocalData {

    private String date;
    private int doneCounter = 0;
    private int undoneCounter = 0;
    private int cheatDays;
    //private String score = doneCounter + ":" + undoneCounter;


    public LocalData(String date, int doneCounter, int undoneCounter) {
        this.date = date;
        this.doneCounter = doneCounter;
        this.undoneCounter = undoneCounter;
    }

    public LocalData() {
        this.date = date;
        this.doneCounter = doneCounter;
        this.undoneCounter = undoneCounter;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public int getDoneCounter() {
        return doneCounter;
    }
    public void setDoneCounter(int doneCounter) {
        this.doneCounter = doneCounter;
    }

    public int getUndoneCounter() {
        return undoneCounter;
    }
    public void setUndoneCounter(int undoneCounter) {
        this.undoneCounter = undoneCounter;
    }


}
