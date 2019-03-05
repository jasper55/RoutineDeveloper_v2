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

import com.example.app.jasper.routinedeveloper_v2.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    CustomItemClickListener customItemClickListener;
    int currentPosition;
    private Context context;
    private List<Todo> todoList;

    // Its similar to what your code in the ListView does (checking if convertView is null)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int position) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_todoitem, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        final Todo todoItem = todoList.get(position);
        viewHolder.todoId.setText(String.valueOf(todoItem.getId()));
        viewHolder.todoName.setText(todoItem.getName());
        viewHolder.checkBox.setChecked(todoItem.isDone());

        viewHolder.itemListContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customItemClickListener.onItemClick(position);
                currentPosition = position;
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
                SQLCRUDOperations.getInstance(context.getApplicationContext()).updateItem(id, mockItem);
            }
        });
    }

    // Methode auch wichtig, da wenn default 0 gelassen wird, wird nichts angezeigt
    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public Todo getItem(int position) {
        return todoList.get(position);
    }

    public void addItem(Todo item) {
        SQLCRUDOperations.getInstance(context.getApplicationContext()).updateItem(item.getId(), item);
        todoList.add(item);
        this.notifyDataSetChanged();
    }

    public void updateList(Todo item, int position) {
        todoList.set(position, SQLCRUDOperations.getInstance(context.getApplicationContext()).readItem(item.getId()));
        this.notifyDataSetChanged();
    }

    public void removeItem(int position) {
        todoList.remove(position);
        this.notifyDataSetChanged();
    }

    public int getPosition() {
        return currentPosition;
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
        }
    }

    public RecyclerViewAdapter(Context context, List<Todo> todoList,  CustomItemClickListener listener) {
        this.customItemClickListener = listener;
        this.context = context;
        this.todoList = todoList;
    }

    public interface CustomItemClickListener {
        void onItemClick(int position);
        void onLongItemClick(int position);
    }
}
