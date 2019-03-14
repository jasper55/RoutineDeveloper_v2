package com.example.app.jasper.routinedeveloperv2.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.example.app.jasper.routinedeveloperv2.model.MySharedPrefs;
import com.example.app.jasper.routinedeveloperv2.model.Todo;
import com.example.app.jasper.routinedeveloperv2.repository.TodoListRepository;

import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private MutableLiveData<List<Todo>> todoList = new MutableLiveData<>();
    private MutableLiveData<String> scorePlus = new MutableLiveData<>();
    private MutableLiveData<String> scoreMinus = new MutableLiveData<>();
    private MutableLiveData<String> endingDate = new MutableLiveData<>();
    private MutableLiveData<Boolean> vmIsUpdating; // falls Daten von einem remoteServer geholt werden -> Progressbar implementieren

    private TodoListRepository todoListRepo;

    public MutableLiveData<List<Todo>> getTodoList() {
        return todoList;
    }

    public void receiveDataFromRepo(TodoListRepository todoListRepo) {
        if (todoListRepo == null){
            Log.i("Repo", "Repo is null");
            todoList.postValue(null);
            return;
        }
        todoList.postValue(todoListRepo.getAllItems().getValue());
    }

    public void initUiElements(MySharedPrefs prefs){
        scorePlus.postValue(prefs.getScorePlus());
        scoreMinus.postValue(prefs.getScoreMinus());
        endingDate.postValue(prefs.getEndingDate());
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

    public void postScorePlus(String scorePlus) {
        this.scorePlus.postValue(scorePlus);
    }

    public void postScoreMinus(String scoreMinus) {
        this.scoreMinus.postValue(scoreMinus);
    }

    public void postEndingDate(String endingDate) {
        this.endingDate.postValue(endingDate);
    }

    public void setTodoList(List<Todo> list){
        this.todoList.setValue(list);
    }
    public void setVmIsUpdating(MutableLiveData<Boolean> vmIsUpdating) {
        this.vmIsUpdating = vmIsUpdating;
    }

}
