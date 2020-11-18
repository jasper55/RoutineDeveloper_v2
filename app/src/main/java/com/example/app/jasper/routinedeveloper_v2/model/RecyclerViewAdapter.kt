package com.example.app.jasper.routinedeveloper_v2.model

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app.jasper.routinedeveloper_v2.R
import com.example.app.jasper.routinedeveloper_v2.viewmodel.ViewModel

import java.util.*

class RecyclerViewAdapter(
        private val context: Context,
        var userActionClickListener: UserActionClickListener,
        var todoList: List<Todo>,
        private val viewModel: ViewModel
) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    private var currentPosition: Int = 0

    fun setList(todoList: List<Todo>) {
        this.todoList = todoList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.layout_todoitem, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val todoItem = todoList[position]
        viewHolder.todoId.text = todoItem.id.toString()
        viewHolder.todoName.text = todoItem.name
        viewHolder.checkBox.isChecked = todoItem.isDone
        viewHolder.itemListContainer.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                userActionClickListener.onItemClick(position)
                currentPosition = position
            }
        })
        viewHolder.itemListContainer.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(p0: View?): Boolean {
                currentPosition = position
//                        customItemClickListener.onLongItemClick(position)
                return true
            }
        });
        viewHolder.checkBox.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton, b: Boolean) {
                val checked = viewHolder.checkBox.isChecked
                val item = getItem(position)
                viewModel.completeTodo(item, checked)
            }
        })
    }

    override fun getItemId(position: Int) = position.toLong()

    override fun getItemCount() = todoList.size ?: 0

    fun getItem(position: Int): Todo {
        return todoList[position]
    }

    fun swapPositions(fromPosition: Int, toPosition: Int) {
        viewModel.swapPositions(fromPosition, toPosition)
    }

    fun deleteItem(position: Int) {
        val item = todoList[position]
        viewModel.deleteItem(item.id)
    }

//    fun addItem(item: Todo) {
//        todoList!!.add(item)
//        //        viewmodel.setTodoList(todoList);
//        notifyDataSetChanged()
//    }
//
//    fun loadListFromDB(item: Todo, position: Int) {
//        todoList!![position] = SQLDatabaseHelper.getInstance(context.applicationContext).readItem(item.id)
//        //        this.notifyDataSetChanged();
//    }
//
//    fun removeItem(position: Int) {
//        todoList!!.removeAt(position)
//        notifyDataSetChanged()
//    }
//
//    fun loadListFromDB() {
//        todoList!!.clear()
//        todoList.addAll(SQLDatabaseHelper.getInstance(context.applicationContext).readAllItems())
//        notifyDataSetChanged()
//    }
//
//    fun updateList(todos: List<Todo?>?) {
//        todoList!!.clear()
//        todoList.addAll(todos)
//        notifyDataSetChanged()
//    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var todoId: TextView
        var todoName: TextView
        var checkBox: CheckBox
        var itemListContainer: LinearLayout

        init {
            itemListContainer = itemView.findViewById(R.id.item_list_container)
            todoId = itemView.findViewById(R.id.list_item_id)
            todoName = itemView.findViewById(R.id.list_item_name)
            checkBox = itemView.findViewById(R.id.list_item_checkBox)
        }
    }

    interface UserActionClickListener {
        fun onItemClick(position: Int)
    }


}