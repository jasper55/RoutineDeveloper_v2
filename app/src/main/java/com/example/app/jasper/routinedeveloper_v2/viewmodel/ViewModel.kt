package com.example.app.jasper.routinedeveloper_v2.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.app.jasper.routinedeveloper_v2.NotificationReceiver
import com.example.app.jasper.routinedeveloper_v2.NotificationReceiver.Companion.CALL_NOTIFICATION_ALERT_TIME
import com.example.app.jasper.routinedeveloper_v2.model.Todo
import com.example.app.jasper.routinedeveloper_v2.repository.TodoListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class ViewModel(application: Application) : AndroidViewModel(application) {
    val list = ArrayList<Todo>()
    val todoList = MutableLiveData<List<Todo>>()
    val isListLocked = MutableLiveData<Boolean>(false)
    val doneCounter = MutableLiveData<Int>()
    val undoneCounter = MutableLiveData<Int>()
    val challengeEndingDate = MutableLiveData<String>()
    val errorPromptHeader = MutableLiveData<String>()
    val errorPromptText = MutableLiveData<String>()
    val notificationTime = MutableLiveData<Long>()
    val context = application.applicationContext
    val repository = TodoListRepository.getInstance(context)
    private var vmIsUpdating // falls Daten von einem remoteServer geholt werden -> Progressbar implementieren
            : MutableLiveData<Boolean>? = null

    fun setChallengeEndingDate(endingDate: String) {
        challengeEndingDate.postValue(endingDate)
        viewModelScope.launch(Dispatchers.IO) {
            repository.challengeEndingDate = endingDate
        }
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
        viewModelScope.launch(Dispatchers.IO) {
            doneCounter.value?.let {
                val itemCount = todoList.value!!.size
                var itemsCheckedCount = 0
                for (item in todoList.value!!) {
                    if (item.isChecked) {
                        itemsCheckedCount += 1
                        var doneCounts = item.doneCounts
                        doneCounts += 1
                        item.doneCounts = doneCounts
                        repository.updateItem(item.id, item)
                        loadRecentData()
                    } else {
                        var undoneCounts = item.undoneCounts
                        undoneCounts += 1
                        item.undoneCounts = undoneCounts
                        repository.updateItem(item.id, item)
                        loadRecentData()
                    }
                }
                if (itemsCheckedCount == itemCount) {
                    doneCounter.postValue(doneCounter.value!!.plus(1))
                    repository.incrementOverallDoneCounter()
                } else {
                    undoneCounter.postValue(undoneCounter.value!!.plus(1))
                    repository.incrementOverallUndoneCounter()
                }

                Log.d("COUNTER", "itemCount: $itemCount, checks: $itemsCheckedCount")
            }
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

    fun loadRecentData() {
        viewModelScope.launch(Dispatchers.IO) {
            challengeEndingDate.postValue(repository.challengeEndingDate)
            undoneCounter.postValue(repository.undoneCount)
            doneCounter.postValue(repository.doneCount)
            val items = repository.readAllItems()
            todoList.postValue(items)
            for (item in items) {
                list.add(item)
            }
        }
    }

    fun clearItemScores(item: Todo) = viewModelScope.launch(Dispatchers.IO) {
        item.doneCounts = 0
        item.undoneCounts = 0
        repository.updateItem(item.id, item)
        loadRecentData()
    }

    fun deleteItem(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteItem(id)
            loadRecentData()
        }
    }

    fun swapPositions(item1: Todo, position1: Int, item2: Todo, position2: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            item1.position = position2
            repository.updateItem(item1.id, item1)
            item2.position = position1
            repository.updateItem(item2.id, item2)
            loadRecentData()
        }
    }

    fun completeTodo(item: Todo, checked: Boolean) {
        item.isChecked = checked
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateItem(item.id, item)
        }
    }

    fun toggleLockList() {
        isListLocked.value = !isListLocked.value!!
    }

}
