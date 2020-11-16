package com.example.app.jasper.routinedeveloper_v2.viewmodel;

import com.example.app.jasper.routinedeveloper_v2.model.Todo;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {

    private MutableLiveData<List<Todo>> todoList = new MutableLiveData<>();
    private MutableLiveData<String> doneCounter = new MutableLiveData<>();
    private MutableLiveData<String> undoneCounter = new MutableLiveData<>();
    private MutableLiveData<String> endingDate = new MutableLiveData<>();
    private MutableLiveData<Boolean> vmIsUpdating; // falls Daten von einem remoteServer geholt werden -> Progressbar implementieren


    public MutableLiveData<List<Todo>> getTodoList() { return todoList; }
    public MutableLiveData<String> getDoneCounter() {
        return doneCounter;
    }
    public MutableLiveData<String> getUndoneCounter() {
        return undoneCounter;
    }
    public MutableLiveData<String> getChallengeEndingDate() {
        return endingDate;
    }

    public void setDoneCounter(String doneCounter) {
        this.doneCounter.postValue(doneCounter);
    }
    public void setUndoneCounter(String undoneCounter) {
        this.undoneCounter.postValue(undoneCounter);
    }
    public void setChallengeEndingDate(String endingDate) {
        this.endingDate.postValue(endingDate);
    }

    public void setTodoList(List<Todo> list){
        this.todoList.postValue(list);
    }
    public void setVmIsUpdating(MutableLiveData<Boolean> vmIsUpdating) {
        this.vmIsUpdating = vmIsUpdating;
    }




}
