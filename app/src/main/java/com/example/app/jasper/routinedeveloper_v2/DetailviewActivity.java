package com.example.app.jasper.routinedeveloper_v2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.example.app.jasper.routinedeveloper_v2.databinding.ActivityDetailviewBinding;
import com.example.app.jasper.routinedeveloper_v2.model.SQLDatabaseHelper;
import com.example.app.jasper.routinedeveloper_v2.model.Todo;
import com.example.app.jasper.routinedeveloper_v2.view.DetailviewActions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;

import static com.example.app.jasper.routinedeveloper_v2.OverviewActivity.POSITION;

public class DetailviewActivity extends AppCompatActivity implements DetailviewActions {

    public static final String ARG_ITEM_ID = "itemId";
    public static final String CALL_MODE = "callMode";
    public static final String CALL_MODE_CREATE = "create";
    public static final Long EMPTY_ID = -99L;

    private Todo item;
    private MutableLiveData<Todo> mutTodo = new MutableLiveData<>();
    private SQLDatabaseHelper crudOperations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityDetailviewBinding bindingMediator = DataBindingUtil.setContentView(this, R.layout.activity_detailview);

        getWindow().setNavigationBarColor(ResourcesCompat.getColor(getResources(), R.color.colorBackground, null));

        this.crudOperations = new SQLDatabaseHelper(this);

        long itemId = getIntent().getLongExtra(ARG_ITEM_ID, -1);
        int position = getIntent().getIntExtra(POSITION,0);

        if (itemId != -1) {
            item = crudOperations.readItem(itemId);
            mutTodo.postValue(item);
        } else {
            this.item = new Todo();
            item.setPosition(position);
            Log.d("POSITION",item.toString());
            mutTodo.postValue(item);
        }

        bindingMediator.setLifecycleOwner(this);
        bindingMediator.setTodo(mutTodo);         // verbindet den bindingMediator mit dem erstellten item, sodass die Daten auch auf der OverviewActivity landet
        bindingMediator.setActions(this);
    }

    @Override
    public void saveTodo() {

        if (item.getId() == -1) {
            long id = crudOperations.createItem(this.item);
            this.item.setId(id);

        } else {
            crudOperations.updateItem(item.getId(), item);
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

        if (callMode.equals(CALL_MODE_CREATE)) {
            returnIntent.putExtra(ARG_ITEM_ID, EMPTY_ID);

        } else {
            crudOperations.deleteItem(item.getId());
            returnIntent.putExtra(ARG_ITEM_ID, (long[]) null);
        }

        setResult(RESULT_OK, returnIntent);    // RESULT_OK comes from the Activity.class
        finish();
    }
}