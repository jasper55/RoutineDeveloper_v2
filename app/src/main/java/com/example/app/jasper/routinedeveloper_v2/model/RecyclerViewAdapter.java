package com.example.app.jasper.routinedeveloper_v2.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app.jasper.routinedeveloper_v2.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";


    CustomItemClickListener customItemClickListener;
    private Context context;
    private List<Todo> todoList;
    private SQLCRUDOperations crudOperations;


    // Its similar to what your code in the ListView does (checking if convertView is null)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int position) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_todoitem,viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        final Todo todoItem = todoList.get(position);
        viewHolder.todoId.setText(String.valueOf(todoItem.getId()));
        viewHolder.todoName.setText(todoItem.getName());
        viewHolder.checkBox.setChecked(todoItem.isDone());

        // or:

//        viewHolder.todoId.setText(String.valueOf(todoItem.getId()));
//        viewHolder.todoName.setText(todoItem.getName());
//        viewHolder.checkBox.setChecked(todoItem.isDone());

        // methode hier oder im ViewHolder???
        //TODO testen wo der Clicklistner hinmuss
        viewHolder.todoName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customItemClickListener.onItemClick(position);
            }
        });

        viewHolder.todoName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                customItemClickListener.onLongItemClick(position);
                Toast.makeText(context,"onLongClicked",Toast.LENGTH_SHORT );
                return true;
            }
        });

        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            Todo mockItem = null;

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean b) {
                boolean checked = viewHolder.checkBox.isChecked();
                Log.i("Checkbox listener", String.valueOf(checked));
                Log.i("Checkbox position", String.valueOf(position));

                mockItem = todoList.get(position);
                long id = mockItem.getId();
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

    public void updateList2(Todo item, int position) {crudOperations.updateItem(item.getId(),item);
    todoList.add(item);
    notifyItemChanged(position);
    }

    public void updateList(){
        todoList = crudOperations.readAllItems();
        notifyDataSetChanged();
    }

    public void addItemToList(Todo item, int position) {crudOperations.updateItem(item.getId(),item);
        todoList = crudOperations.readAllItems();
        todoList.add(item);
        notifyItemChanged(position);
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


    public RecyclerViewAdapter(Context context, List<Todo> todoList,  SQLCRUDOperations crudOperations, CustomItemClickListener listener) {
        this.customItemClickListener = listener;
        this.context = context;
        this.todoList = todoList;
        this.crudOperations = crudOperations;
    }

    public interface CustomItemClickListener{
        void onItemClick(int position);

        void onLongItemClick(int position);
    }
}
