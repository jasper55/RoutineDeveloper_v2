package com.example.app.jasper.routinedeveloper_v2.model;

import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    CustomItemClickListener customItemClickListener;
    private int currentPosition;
    private Context context;
    private List<Todo> todoList=new ArrayList<>();

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

                mockItem = todoList.get(position);
                long id = mockItem.getId();
                mockItem.setDone(checked);
                SQLDatabaseHelper.getInstance(context.getApplicationContext()).updateItem(id, mockItem);
            }
        });
    }

    // Methode auch wichtig, da wenn default 0 gelassen wird, wird nichts angezeigt
    @Override
    public int getItemCount() {
        if (todoList == null) return 0;
        return todoList.size();
    }

    public Todo getItem(int position) {
        return todoList.get(position);
    }

    public void addItem(Todo item) {
        SQLDatabaseHelper.getInstance(context.getApplicationContext()).updateItem(item.getId(), item);
        todoList.add(item);
//        this.notifyDataSetChanged();
    }

    public void loadListFromDB(Todo item, int position) {
        todoList.set(position, SQLDatabaseHelper.getInstance(context.getApplicationContext()).readItem(item.getId()));
//        this.notifyDataSetChanged();
    }

    public void removeItem(int position) {
        todoList.remove(position);
        this.notifyDataSetChanged();
    }

    public int getPosition() {
        return currentPosition;
    }

    public void loadListFromDB() {
        todoList.clear();
        todoList.addAll(SQLDatabaseHelper.getInstance(context.getApplicationContext()).readAllItems());
        this.notifyDataSetChanged();
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

    public RecyclerViewAdapter(Context context,   CustomItemClickListener listener) {
        this.customItemClickListener = listener;
        this.context = context;

    }

    public interface CustomItemClickListener {
        void onItemClick(int position);
        void onLongItemClick(int position);
    }
}
