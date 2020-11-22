package com.example.app.jasper.routinedeveloper_v2.repository

import android.content.Context
import android.util.Log
import com.example.app.jasper.routinedeveloper_v2.model.SQLDatabaseHelper
import com.example.app.jasper.routinedeveloper_v2.model.Todo

class TodoListRepository(context: Context) {
    private val dataBase: SQLDatabaseHelper = SQLDatabaseHelper.getInstance(context)
    private val prefs: SharedPreferenceHelper = SharedPreferenceHelper
    val allItems: List<Todo>
        get() = dataBase.readAllItems()

    fun createItem(item: Todo?): Long {
        return dataBase.createItem(item)
    }

    suspend fun updateItem(id: Long, item: Todo?) {
        dataBase.updateItem(id, item)
    }

    suspend fun deleteItem(id: Long) {
        dataBase.deleteItem(id)
    }

    suspend fun readAllItems(): List<Todo> {
        return dataBase.readAllItems()
    }

    suspend fun readItem(id: Long): Todo {
        return dataBase.readItem(id)
    }

    fun clearTargetDate() {
        prefs.challengeEndingDate = ""
    }

    fun clearScore() {
        prefs.undoneCount = 0
        prefs.doneCount = 0
    }

    val storedDay: Int
        get() = prefs.storedDay

    fun setCurrentDay(currentDay: Int) {
        prefs.updateDate(currentDay)
    }

    fun incrementDoneCounter() {
        var doneCount = prefs.doneCount
        doneCount += 1
        prefs.doneCount = doneCount
        Log.d("COUNTER", "doneCount incremented")
        val done = prefs.doneCount
        Log.d("COUNTER", "new done value: $done")
    }

    fun incrementUndoneCounter() {
        var undoneCount = prefs.undoneCount
        undoneCount += 1
        prefs.undoneCount = undoneCount
        Log.d("COUNTER", "undoneCount incremented")
        val undone = prefs.undoneCount
        Log.d("COUNTER", "new undone value: $undone")
    }

    fun setFirstStart(b: Boolean) {
        prefs.firstStart = b
    }

    suspend fun saveList(todos: List<Todo>) {
        for (item in todos) dataBase.updateItem(item.id, item)
    }

    suspend fun addItem(item: Todo) {
        dataBase.updateItem(item.id, item)
    }

    val undoneCount: Int?
        get() = prefs.undoneCount

    val doneCount: Int?
        get() = prefs.doneCount

    var challengeEndingDate: String?
        get() = prefs.challengeEndingDate
        set(endingDate) {
            prefs.challengeEndingDate = endingDate!!
        }

    companion object {
        private var instance: TodoListRepository? = null
        fun getInstance(context: Context): TodoListRepository {
            if (instance == null) {
                instance = TodoListRepository(context)
            }
            return instance as TodoListRepository
        }
    }

}