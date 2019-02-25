package com.example.app.jasper.routinedeveloper_v2.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.app.jasper.routinedeveloper_v2.R;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    public TextView todoId, todoName;
    public CheckBox checkBox;
    private Context context;
    private List<Todo> todoList;
    private SQLCRUDOperations crudOperations;



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_todoitem,viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Todo todoItem = todoList.get(i);
        this.todoId.setText(String.valueOf(todoItem.getId()));
        this.todoName.setText(todoItem.getName());
        this.checkBox.setChecked(todoItem.isDone());

        // oder:

//        viewHolder.todoId.setText(String.valueOf(todoItem.getId()));
//        viewHolder.todoName.setText(todoItem.getName());
//        viewHolder.checkBox.setChecked(todoItem.isDone());

        // methode hier oder im ViewHolder???
        //TODO testen wo der Clicklistner hinmuss

        this.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            Todo mockItem = null;

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean b) {
                int position = (int) buttonView.getTag();
                boolean checked = checkBox.isChecked();
                Log.i("Checkbox listener", String.valueOf(checked));
                Log.i("Checkbox position", String.valueOf(position));

                Todo item = todoList.get(position);
                long id = item.getId();
                mockItem.setDone(checked);
                crudOperations.updateItem(id, mockItem);
            }
        });
    }


    // Methode auch wichtig, da wenn default 0 gelassen, nicts wird angezeigt
    @Override
    public int getItemCount() {
        return todoList.size();
    }



    public Todo getItem(int position){
        return todoList.get(position);
    }

    public void addAll(List<Todo> todos) {
        crudOperations.readAllItems();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView todoId, todoName;
        public CheckBox checkBox;
        public LinearLayout itemListContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemListContainer = itemView.findViewById(R.id.item_list_container);
            todoId = itemView.findViewById(R.id.list_item_id);
            todoName = itemView.findViewById(R.id.list_item_name);
            checkBox = itemView.findViewById(R.id.list_item_checkBox);

//            this.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                Todo mockItem = null;
//
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean b) {
//                    int position = (int) buttonView.getTag();
//                    boolean checked = checkBox.isChecked();
//                    Log.i("Checkbox listener", String.valueOf(checked));
//                    Log.i("Checkbox position", String.valueOf(position));
//
//                    Todo item = todoList.get(position);
//                    long id = item.getId();
//                    mockItem.setDone(checked);
//                    crudOperations.updateItem(id, mockItem);
//                }
//            });
        }
    }

    public RecyclerViewAdapter(Context context, List<Todo> todoList,  SQLCRUDOperations crudOperations) {
        this.context = context;
        this.todoList = todoList;
        this.crudOperations = crudOperations;
    }
}
