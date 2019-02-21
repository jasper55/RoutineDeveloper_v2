package com.example.app.jasper.routinedeveloper_v2.model;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.app.jasper.routinedeveloper_v2.R;

public class ListItemViewHolder {

    public Todo listItem;

    private TextView todoId;
    private TextView todoName;
    public CheckBox checkBox;

    public ListItemViewHolder(final ArrayAdapter<Todo> listViewAdapter, final SQLCRUDOperations crudOperations, View itemView) {

        this.todoId = itemView.findViewById(R.id.listitemId);
        this.todoName = itemView.findViewById(R.id.listitemName);
        this.checkBox = itemView.findViewById(R.id.listitemCheckBox);

        this.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            Todo mockItem = null;

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean b) {
                int position = (int) buttonView.getTag();
                boolean checked = checkBox.isChecked();
                Log.i("Checkbox listener", String.valueOf(checked));
                Log.i("Checkbox position", String.valueOf(position));

                mockItem = listViewAdapter.getItem(position);
                mockItem.setDone(checked);
                crudOperations.updateItem(mockItem.getId(), mockItem);
            }
        });
    }

    public void unbind() {
        this.listItem = null;
    }

    public Todo bind(Todo todoItem) {
        this.todoId.setText(String.valueOf(todoItem.getId()));
        this.todoName.setText(todoItem.getName());
        this.checkBox.setChecked(todoItem.isDone());
        return todoItem;
    }

}
