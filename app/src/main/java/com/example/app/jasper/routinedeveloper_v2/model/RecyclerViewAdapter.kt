package com.example.app.jasper.routinedeveloper_v2.model

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

class RecyclerViewAdapter(
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
        viewHolder.todoId.text = "${todoItem.position+1}"
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
                return true
            }
        });
        viewHolder.checkBox.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton, b: Boolean) {
                val checked = viewHolder.checkBox.isChecked
                val item = getItem(position)
                viewModel.completeTodo(item, checked)
                viewModel.loadRecentData()
            }
        })
    }

    override fun getItemId(position: Int) = position.toLong()

    override fun getItemCount() = todoList.size

    fun getItem(position: Int): Todo {
        return todoList[position]
    }

    fun swapPositions(fromPosition: Int, toPosition: Int) {
        viewModel.swapPositions(fromPosition, toPosition)
    }

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