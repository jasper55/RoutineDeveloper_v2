package com.example.app.jasper.routinedeveloper_v2.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.app.jasper.routinedeveloper_v2.model.MySharedPrefs;
import com.example.app.jasper.routinedeveloper_v2.model.SQLCRUDOperations;
import com.example.app.jasper.routinedeveloper_v2.model.Todo;
import com.example.app.jasper.routinedeveloper_v2.repository.TodoListRepository;

import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private MutableLiveData<List<Todo>> todoList;
    private MutableLiveData<String> scorePlus;
    private MutableLiveData<String> scoreMinus;
    private MutableLiveData<String> endingDate;
    private MutableLiveData<Boolean> vmIsUpdating; // falls Daten von einem remoteServer geholt werden -> Progressbar implementieren

    private TodoListRepository todoListRepo;

    public MutableLiveData<List<Todo>> getTodoList() {
        return todoList;
    }

    public void initTodoListRepo(SQLCRUDOperations crudOperations) {
        if (todoList != null){
            return;
        }
        todoListRepo = TodoListRepository.getInstance();
        todoList = todoListRepo.getTodoList(crudOperations);
        scorePlus = new MutableLiveData<>();
        scoreMinus = new MutableLiveData<>();
        endingDate = new MutableLiveData<>();
    }

    public void initUiElements(MySharedPrefs prefs){
        scorePlus.setValue(prefs.getScorePlus());

        scoreMinus.setValue(prefs.getScoreMinus());

        endingDate.setValue(prefs.getDate());
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
        this.scorePlus.setValue(scorePlus);
    }

    public void setScoreMinus(String scoreMinus) {
        this.scoreMinus.setValue(scoreMinus);
    }

    public void setEndingDate(String endingDate) {
        this.endingDate.setValue(endingDate);
    }

    public void setTodoList(List<Todo> list){
        this.todoList.setValue(list);
    }
    public void setVmIsUpdating(MutableLiveData<Boolean> vmIsUpdating) {
        this.vmIsUpdating = vmIsUpdating;
    }
}
