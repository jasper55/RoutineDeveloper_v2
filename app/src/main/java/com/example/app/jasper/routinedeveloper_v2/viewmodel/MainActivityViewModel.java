package com.example.app.jasper.routinedeveloper_v2.viewmodel;

import android.app.Application;
import android.content.Context;

import com.example.app.jasper.routinedeveloper_v2.model.Todo;
import com.example.app.jasper.routinedeveloper_v2.repository.TodoListRepository;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {

    private MutableLiveData<List<Todo>> todoList = new MutableLiveData<>();
    private MutableLiveData<String> scorePlus = new MutableLiveData<>();
    private MutableLiveData<String> scoreMinus = new MutableLiveData<>();
    private MutableLiveData<String> endingDate = new MutableLiveData<>();
    private MutableLiveData<Boolean> vmIsUpdating; // falls Daten von einem remoteServer geholt werden -> Progressbar implementieren

    private TodoListRepository repository;


    public MutableLiveData<List<Todo>> getTodoList() {
        return todoList;
    }

    public void receiveDateFromRepo(Context context) {
//        if (todoList != null){
//            return;
//        }
        repository = TodoListRepository.getInstance(context);
        todoList = repository.getAllItems();
        scorePlus = repository.getScorePlus();
        scoreMinus = repository.getScoreMinus();
        endingDate = repository.getEndingDate();
    }

    public MutableLiveData<String> getScorePlus() {
        return scorePlus;
    }

    public MutableLiveData<String> getScoreMinus() {
        return scoreMinus;
    }

    public MutableLiveData<String> getEndingDate() {
        return endingDate;
    }

    public void setScorePlus(String scorePlus) {
        this.scorePlus.postValue(scorePlus);
    }

    public void setScoreMinus(String scoreMinus) {
        this.scoreMinus.postValue(scoreMinus);
    }

    public void setEndingDate(String endingDate) {
        this.endingDate.postValue(endingDate);
    }

    public void setTodoList(List<Todo> list){
        this.todoList.postValue(list);
    }
    public void setVmIsUpdating(MutableLiveData<Boolean> vmIsUpdating) {
        this.vmIsUpdating = vmIsUpdating;
    }




}
