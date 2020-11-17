package com.example.app.jasper.routinedeveloper_v2.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.app.jasper.routinedeveloper_v2.NotificationReceiver
import com.example.app.jasper.routinedeveloper_v2.NotificationReceiver.Companion.CALL_NOTIFICATION_ALERT_TIME
import com.example.app.jasper.routinedeveloper_v2.model.SQLDatabaseHelper
import com.example.app.jasper.routinedeveloper_v2.model.Todo
import com.example.app.jasper.routinedeveloper_v2.repository.TodoListRepository
import java.util.*

class ViewModel(application: Application) : AndroidViewModel(application) {
    val todoList = MutableLiveData<List<Todo>>()
    val doneCounter = MutableLiveData<Int>()
    val undoneCounter = MutableLiveData<Int>()
    val challengeEndingDate = MutableLiveData<String>()
    val notificationTime = MutableLiveData<Long>()
    val context = application.applicationContext
    val repository = TodoListRepository(context)
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
        repository.setChallengeEndingDate(endingDate)
    }

    fun setTodoList(list: List<Todo>) {
        todoList.postValue(list)
    }

    fun setVmIsUpdating(vmIsUpdating: MutableLiveData<Boolean>?) {
        this.vmIsUpdating = vmIsUpdating
    }

    fun checkHasDateChanged(): Boolean {
        val currentday = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        var dateHasChanged = false
        if(repository.storedDay != currentday) {
            dateHasChanged = true
            repository.setCurrentDay(currentday)
        } else {
            dateHasChanged = false
        }
        return dateHasChanged
    }

    fun sumUpCheckBoxes() {
        val list = todoList.value!!
        val itemCount = list.size
        var itemCheckCount = 0
        for (item in list) {
            if (item.isDone) {
                itemCheckCount += 1
            }
        }
        if (itemCheckCount == itemCount) {
            doneCounter.value = doneCounter.value!!.plus(1)
            repository.incrementDoneCounter()
        } else {
            undoneCounter.value = undoneCounter.value!!.plus(1)
            repository.incrementUndoneCounter()
        }
        Log.d("COUNTER","itemCOunt: $itemCount, checks: $itemCheckCount")
    }

    fun clearScore() {
        undoneCounter.value = 0
        doneCounter.value = 0
        repository.clearScore()
    }

    fun clearTargetDate() {
        challengeEndingDate.value = ""
        repository.clearTargetDate()
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
        repository.setFirstStart(b)
    }

    fun saveList(todos: List<Todo?>?) {
        repository.saveList(todos)
    }

    fun addItem(item: Todo) {
        repository.addItem(item)
    }

    fun updateItem(item: Todo, list: List<Todo>) {
        repository.updateItem(item.id,item)
        todoList.value = list
    }

}
