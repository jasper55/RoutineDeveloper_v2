package com.example.app.jasper.routinedeveloper_v2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.databinding.DataBindingUtil;
import android.util.Log;

import com.example.app.jasper.routinedeveloper_v2.databinding.ActivityDetailviewBinding;
import com.example.app.jasper.routinedeveloper_v2.model.SQLCRUDOperations;
import com.example.app.jasper.routinedeveloper_v2.model.Todo;
import com.example.app.jasper.routinedeveloper_v2.view.DetailviewActions;

public class DetailviewActivity extends AppCompatActivity implements DetailviewActions {

    public static final String ARG_ITEM_ID = "itemId";
    public static final String CALL_MODE = "callMode";
    public static final String CALL_MODE_CREATE = "create";
    public static final Long EMPTY_ID = -99L;


    private Todo item;
    private SQLCRUDOperations crudOperations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityDetailviewBinding bindingMediator = DataBindingUtil.setContentView(this, R.layout.activity_detailview);

        this.crudOperations = new SQLCRUDOperations(this);

        long itemId = getIntent().getLongExtra(ARG_ITEM_ID, -1);

        if (itemId != -1) item = crudOperations.readItem(itemId);
        else {
            this.item = new Todo();
        }

        bindingMediator.setTodo(item);         // verbindet den bindingMediator mit dem erstellten item, sodass die Daten auch auf der OverviewActivity landet
        bindingMediator.setActions(this);
    }

    @Override
    public void saveTodo() {

        if (item.getId() == -1) {
            Log.i("RD_Detailview", "crudOperations.createItem started");
            Log.i("RD_Detailview", item.getName());

            long id = crudOperations.createItem(this.item);
            this.item.setId(id);
            Log.i("RD_Detailview", "created id: " + String.valueOf(item.getId()));

        } else {
            crudOperations.updateItem(item.getId(), item);
            Log.i("RD_Detailview", "updated id: " + String.valueOf(item.getId()));
        }

        Intent returnIntent = new Intent();
        returnIntent.putExtra(ARG_ITEM_ID, item.getId());

        setResult(RESULT_OK, returnIntent);    // RESULT_OK comes from Activity.class
        finish();
    }

    @Override
    public void deleteTodo() {

        Intent returnIntent = new Intent();

        String callMode = getIntent().getExtras().getString(CALL_MODE, "0");
        Log.i("CallMode ", callMode);
        if (callMode.equals(CALL_MODE_CREATE)) {
            returnIntent.putExtra(ARG_ITEM_ID, EMPTY_ID);
            Log.i("Extra: ", String.valueOf(returnIntent.getLongExtra(ARG_ITEM_ID, -100)));
        }
        else {
            Log.i("Extra: ", "else");

            crudOperations.deleteItem(item.getId());
            returnIntent.putExtra(ARG_ITEM_ID, (long[]) null);
        }

        setResult(RESULT_OK, returnIntent);    // RESULT_OK comes from the Activity.class
        finish();
    }
}