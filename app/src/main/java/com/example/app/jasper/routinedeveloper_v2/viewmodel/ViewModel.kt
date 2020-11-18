package com.example.app.jasper.routinedeveloper_v2.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.app.jasper.routinedeveloper_v2.NotificationReceiver
import com.example.app.jasper.routinedeveloper_v2.NotificationReceiver.Companion.CALL_NOTIFICATION_ALERT_TIME
import com.example.app.jasper.routinedeveloper_v2.model.SQLDatabaseHelper
import com.example.app.jasper.routinedeveloper_v2.model.Todo
import com.example.app.jasper.routinedeveloper_v2.repository.SharedPreferenceHelper
import com.example.app.jasper.routinedeveloper_v2.repository.TodoListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class ViewModel(application: Application) : AndroidViewModel(application) {
    val list = ArrayList<Todo>()
    val todoList = MutableLiveData<List<Todo>>()
    val doneCounter = MutableLiveData<Int>()
    val undoneCounter = MutableLiveData<Int>()
    val challengeEndingDate = MutableLiveData<String>()
    val notificationTime = MutableLiveData<Long>()
    val context = application.applicationContext
    val repository = TodoListRepository.getInstance(context)
    private var vmIsUpdating // falls Daten von einem remoteServer geholt werden -> Progressbar implementieren
            : MutableLiveData<Boolean>? = null

    fun setDoneCounter(doneCounter: Int) {
        this.doneCounter.postValue(doneCounter)
    }

    fun setUndoneCounter(undoneCounter: Int) {
        this.undoneCounter.postValue(undoneCounter)
    }

    fun setChallengeEndingDate(endingDate: String) {
        challengeEndingDate.postValue(endingDate)
        viewModelScope.launch(Dispatchers.IO) {
            repository.setChallengeEndingDate(endingDate)
        }
    }

    fun setVmIsUpdating(vmIsUpdating: MutableLiveData<Boolean>?) {
        this.vmIsUpdating = vmIsUpdating
    }

    fun checkHasDateChanged(): Boolean {
        val currentday = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        var dateHasChanged = false
        if (repository.storedDay != currentday) {
            dateHasChanged = true
            repository.setCurrentDay(currentday)
        } else {
            dateHasChanged = false
        }
        return dateHasChanged
    }

    fun sumUpCheckBoxes() {
        doneCounter.value?.let {
            val itemCount = list.size
            var itemCheckCount = 0
            for (item in list) {
                if (item.isDone) {
                    itemCheckCount += 1
                }
            }
            if (itemCheckCount == itemCount) {
                viewModelScope.launch(Dispatchers.IO) {
                    doneCounter.postValue(doneCounter.value!!.plus(1))
                    repository.incrementDoneCounter()
                }
            } else {
                viewModelScope.launch(Dispatchers.IO) {
                    undoneCounter.postValue(undoneCounter.value!!.plus(1))
                    repository.incrementUndoneCounter()
                }
            }
            Log.d("COUNTER", "itemCOunt: $itemCount, checks: $itemCheckCount")
        }
    }

    fun clearScore() {
        undoneCounter.postValue(0)
        doneCounter.postValue(0)
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearScore()
        }
    }

    fun clearTargetDate() {
        viewModelScope.launch(Dispatchers.IO) {
            challengeEndingDate.postValue("")
            repository.clearTargetDate()
        }
    }

    fun createNotificationIntent(hour: Int, min: Int) {
        Log.d("ALERT_TIME", "hour: $hour, min: $min")
        val time: Long
        val hour = hour * 60 * 60 * 1000.toLong()
        val min = min * 60 * 1000.toLong()
        time = hour + min
        val timeInMillis = Calendar.getInstance().timeInMillis
        val current_hour = (Calendar.getInstance()[Calendar.HOUR] + 12) * 60 * 60 * 1000.toLong()
        val current_min = (Calendar.getInstance()[Calendar.MINUTE] - 1) * 60 * 1000.toLong()
        val current_sec = Calendar.getInstance()[Calendar.SECOND] * 1000.toLong()
        val passed_millis = current_hour + current_min + current_sec
        val alertTimeInMillis = timeInMillis - passed_millis + time
        val notificationIntent = Intent(getApplication(), NotificationReceiver::class.java)
        val pIntent = PendingIntent.getBroadcast(context, CALL_NOTIFICATION_ALERT_TIME, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alertTimeInMillis, AlarmManager.INTERVAL_DAY, pIntent)
    }

    fun setFirstStart(b: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setFirstStart(b)
        }
    }

    fun saveList(todos: List<Todo>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveList(todos)
        }
    }

    fun addItem(item: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            list.add(item)
            repository.addItem(item)
        }
    }

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            challengeEndingDate.postValue(repository.getChallengeEndingDate())
            undoneCounter.postValue(repository.undoneCount)
            doneCounter.postValue(repository.doneCount)
            val items = repository.allItems
            todoList.postValue(items)
            for (item in items) {
                list.add(item)
            }

            if (items.isNotEmpty()) {
                Log.d("CHECKED", "after todos: ${items.get(0).isDone}")


                Log.d("COUNTER", "done: ${repository.doneCount}")
                Log.d("COUNTER", "undone: ${repository.undoneCount}")
            }
        }
    }

    fun deleteItem(id: Long) {
        list.remove(repository.readItem(id))
//        val list = ArrayList<Todo>()
//        for (item in todoList.value!!)
//            if (item.id != id) {
//                list.add(item)
//            }
        todoList.postValue(list)
    }

    fun swapPositions(fromPosition: Int, toPosition: Int) {
        Collections.swap(list, fromPosition, toPosition)
        todoList.postValue(list)
    }

}
