package com.example.app.jasper.routinedeveloper_v2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.databinding.DataBindingUtil;
import com.example.app.jasper.routinedeveloper_v2.databinding.ActivityDetailviewBinding;
import com.example.app.jasper.routinedeveloper_v2.model.SQLCRUDOperations;
import com.example.app.jasper.routinedeveloper_v2.model.Todo;
import com.example.app.jasper.routinedeveloper_v2.repository.TodoListRepository;
import com.example.app.jasper.routinedeveloper_v2.view.DetailviewActions;


public class DetailviewActivity extends AppCompatActivity implements DetailviewActions {

    public static final String ARG_ITEM_ID = "itemId";
    public static final String CALL_MODE = "callMode";
    public static final String CALL_MODE_CREATE = "create";
    public static final Long EMPTY_ID = -99L;

    private Todo item;
    private SQLCRUDOperations crudOperations;
    TodoListRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityDetailviewBinding bindingMediator = DataBindingUtil.setContentView(this, R.layout.activity_detailview);

        receivedItemCheck();


        bindingMediator.setTodo(item);         // verbindet den bindingMediator mit dem erstellten item, sodass die Daten auch auf der OverviewActivity landet
        bindingMediator.setActions(this);
    }


    public Todo readCreateItemOld(){
        this.crudOperations = new SQLCRUDOperations(this);

        long itemId = getIntent().getLongExtra(ARG_ITEM_ID, -1);

        if (itemId != -1) item = crudOperations.readItem(itemId);
        else {
            this.item = new Todo();
        }
        return item;
    }

    public Todo receivedItemCheck(){

        repository = TodoListRepository.getInstance(getApplicationContext());

//        roomDatabase = TodoRoomDatabase.getInstance(getApplicationContext());

        long itemId = getIntent().getLongExtra(ARG_ITEM_ID, -1);

        if (itemId != -1) {
//            item = repository.readItem(itemId);
        } else {
            this.item = new Todo();
            repository.createItem(item);
            //roomDatabase.roomCRUDOperations().createItem(item);
        }
        return item;
    }

    @Override
    public void saveTodo() {

//        if (item.getId() == -1) {
//            long id = crudOperations.createItem(this.item);
//            this.item.setId(id);
//
//        } else {
        repository.updateItem(item);
//            roomDatabase.roomCRUDOperations().updateItem(item);
//        }

        Intent returnIntent = new Intent();
        returnIntent.putExtra(ARG_ITEM_ID, item.getId());

        setResult(RESULT_OK, returnIntent);    // RESULT_OK comes from Activity.class
        finish();
    }


//    @Override
//    public void saveTodo() {
//
//        if (item.getId() == -1) {
//            long id = crudOperations.createItem(this.item);
//            this.item.setId(id);
//
//        } else {
//            crudOperations.updateItem(item.getId(), item);
//        }
//
//        Intent returnIntent = new Intent();
//        returnIntent.putExtra(ARG_ITEM_ID, item.getId());
//
//        setResult(RESULT_OK, returnIntent);    // RESULT_OK comes from Activity.class
//        finish();
//    }

    @Override
    public void deleteTodo() {
        Intent returnIntent = new Intent();
        String callMode = getIntent().getExtras().getString(CALL_MODE, "0");

        if (callMode.equals(CALL_MODE_CREATE)) {
            returnIntent.putExtra(ARG_ITEM_ID, EMPTY_ID);

        } else {
            repository.deleteItem(item);
//            roomDatabase.roomCRUDOperations().deleteItem(item);
            returnIntent.putExtra(ARG_ITEM_ID, (long[]) null);
        }

        setResult(RESULT_OK, returnIntent);    // RESULT_OK comes from the Activity.class
        finish();
    }

//    @Override
//    public void deleteTodo() {
//        Intent returnIntent = new Intent();
//        String callMode = getIntent().getExtras().getString(CALL_MODE, "0");
//
//        if (callMode.equals(CALL_MODE_CREATE)) {
//            returnIntent.putExtra(ARG_ITEM_ID, EMPTY_ID);
//
//        } else {
//            crudOperations.deleteItem(item.getId());
//            returnIntent.putExtra(ARG_ITEM_ID, (long[]) null);
//        }
//
//        setResult(RESULT_OK, returnIntent);    // RESULT_OK comes from the Activity.class
//        finish();
//    }



    public void saveItem(){
//        List<Todo> todoList = roomDatabase.roomCRUDOperations().getAllItems();
    }

}