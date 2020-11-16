package com.example.app.jasper.routinedeveloper_v2

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.*
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app.jasper.routinedeveloper_v2.model.RecyclerViewAdapter
import com.example.app.jasper.routinedeveloper_v2.model.RecyclerViewAdapter.CustomItemClickListener
import com.example.app.jasper.routinedeveloper_v2.model.Todo
import com.example.app.jasper.routinedeveloper_v2.repository.SharedPreferenceHelper
import com.example.app.jasper.routinedeveloper_v2.repository.TodoListRepository
import com.example.app.jasper.routinedeveloper_v2.viewmodel.MainActivityViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class OverviewActivity : AppCompatActivity() {

    private var isFABOpen = false

    private lateinit  var timepicker: TimePicker
    private lateinit var alertTimeListener: OnTimeSetListener
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    private lateinit var challengeEndingDate: TextView
    private lateinit var challengeEndingDateSetListener: OnDateSetListener
    private lateinit var textViewPlus: TextView
    private lateinit var textViewMinus: TextView
    private lateinit var item: Todo

    private lateinit var fab_add: FloatingActionButton
    private lateinit var fab_timer: FloatingActionButton
    private lateinit var fab_notification: FloatingActionButton
    private lateinit var fab_menu: FloatingActionButton
    private lateinit var fab_container_add: LinearLayout
    private lateinit var fab_container_notification: LinearLayout
    private lateinit var fab_container_timer: LinearLayout
    private lateinit var fabOverlay: View
    private lateinit var repository: TodoListRepository
    private lateinit var backgroundTask: BackgroundTasks
    private lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_overview)
        SharedPreferenceHelper.initWith(applicationContext)

        if (SharedPreferenceHelper.firstStart) {
            Toast.makeText(this, "Welcome to Routine Developer!", Toast.LENGTH_SHORT).show()
        } else {
            SharedPreferenceHelper.firstStart = false
        }
//        myPrefs.firstTimeStartingApp(application)
//        myPrefs.loadSharedPrefs()
        mainActivityViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        initRespository(this)
        initView()

        //myPrefs.connectViewModel(mainActivityViewModel);
        observeLiveData()
        loadDataFromPrefs()


//        myPrefs.applyPrefsToView(mainActivityViewModel);
        backgroundTask = BackgroundTasks.getInstance(this, mainActivityViewModel)
        //        backgroundTask.init(this,
//                mainActivityViewModel.getScorePlus().toString(),
//                mainActivityViewModel.getScoreMinus().toString());
        setClickListenersToViewElements()

    } // onCreate() - end

    private fun observeLiveData() {
        mainActivityViewModel.todoList.observe(this, Observer { todos: List<Todo?>? -> recyclerViewAdapter!!.loadListFromDB() })
        mainActivityViewModel.doneCounter.observe(this, Observer { it: String? -> textViewPlus!!.text = it })
        mainActivityViewModel.undoneCounter.observe(this, Observer { it: String? -> textViewMinus!!.text = it })
        mainActivityViewModel.challengeEndingDate.observe(this, Observer { it: String? -> challengeEndingDate!!.text = it })
    }

    private fun setClickListenersToViewElements() {
        //setItemListClickListener();
        setEndingDateListener()
    }

    private fun initRespository(context: Context) {
        repository = TodoListRepository.getInstance(context);
    }


    private fun setEndingDateListener() {
        challengeEndingDate.setOnClickListener {
            val calender = Calendar.getInstance()
            val year = calender[Calendar.YEAR]
            val month = calender[Calendar.MONTH]
            val day = calender[Calendar.DAY_OF_MONTH]
            val datePickerDialog = DatePickerDialog(this@OverviewActivity,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    challengeEndingDateSetListener,
                    year, month, day)
            // Hintergrund transparent machen
            datePickerDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            datePickerDialog.show()
        }
        challengeEndingDateSetListener = OnDateSetListener { datePicker, year, month, day ->
            var month = month
            month += 1
            val date = "$day.$month.$year"
            // challengeEndingDate.setText(date);
            mainActivityViewModel.challengeEndingDate.value = date
//                    .postValue(date)
            SharedPreferenceHelper.challengeEndingDate = date
//            myPrefs!!.saveSharedPrefs()
        }
    }

    private fun initView() {
        initRecyclerViewList()
        instantiateTimePicker()
        instantiateFABMenu()
        challengeEndingDate = findViewById(R.id.tvDate)
        initActionBar()
    }

    private fun loadDataFromPrefs() {
        mainActivityViewModel.challengeEndingDate.value = SharedPreferenceHelper.challengeEndingDate
        mainActivityViewModel.undoneCounter.value = SharedPreferenceHelper.doneCount.toString()
        mainActivityViewModel.doneCounter.value = SharedPreferenceHelper.undoneCount.toString()
        mainActivityViewModel.todoList.value = repository.getAllItems()
    }

    private fun initActionBar() {
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        supportActionBar!!.setCustomView(R.layout.custom_action_bar_layout)
        textViewPlus = findViewById(R.id.scorePlus)
        textViewMinus = findViewById(R.id.scoreMinus)
    }

    private fun initRecyclerViewList() {
        recyclerView = findViewById(R.id.recycler_view_data)
        recyclerViewAdapter = RecyclerViewAdapter(this, object : CustomItemClickListener {
            override fun onItemClick(position: Int) {
                showDetailViewForEdit(recyclerViewAdapter.getItem(position))
            }
            override fun onLongItemClick(position: Int) {}
        })
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = recyclerViewAdapter
    }

    private fun instantiateTimePicker() {
        timepicker = TimePicker(this)
        alertTimeListener = OnTimeSetListener { timePicker, hour, min ->
            timepicker.currentHour = hour
            timepicker.currentMinute = min
            createNotificationIntent(timepicker)
        }
    }

    private fun instantiateFABMenu() {
        fab_container_add = findViewById(R.id.fab_container_add)
        fab_container_notification = findViewById(R.id.fab_container_notification)
        fab_container_timer = findViewById(R.id.fab_container_timer)
        fabOverlay = findViewById(R.id.fabOverlay)
        fab_menu = findViewById(R.id.fab_menu)
        fab_menu.setOnClickListener {
            if (!isFABOpen) {
                showFABMenu()
            } else {
                closeFABMenu()
            }
        }
        fabOverlay.setOnClickListener { closeFABMenu() }
        fab_add = findViewById(R.id.fab_add)
        fab_add.setOnClickListener {
            showDetailViewForCreate()
            closeFABMenu()
        }
        fab_timer = findViewById(R.id.fab_timer)
        fab_timer.setOnClickListener {
            setChallengeEndingTime()
            closeFABMenu()
        }
        fab_notification = findViewById(R.id.fab_notification)
        fab_notification.setOnClickListener {
            setNotificationTime()
            createNotificationIntent(timepicker)
            closeFABMenu()
        }
    }

    @SuppressLint("RestrictedApi")
    private fun showFABMenu() {
        isFABOpen = true
        //applyBlurOnBackground();
        fab_container_add.visibility = View.VISIBLE
        fab_container_timer.visibility = View.VISIBLE
        fab_container_notification.visibility = View.VISIBLE
        fabOverlay.visibility = View.VISIBLE
        fab_menu.animate().rotationBy(270f).setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {}
            override fun onAnimationEnd(animator: Animator) {}
            override fun onAnimationCancel(animator: Animator) {}
            override fun onAnimationRepeat(animator: Animator) {}
        })
        fab_container_add.animate().translationY(-resources.getDimension(R.dimen.standard_175))
        fab_container_notification.animate().translationY(-resources.getDimension(R.dimen.standard_120))
        fab_container_timer.animate().translationY(-resources.getDimension(R.dimen.standard_65))
    }

    @SuppressLint("RestrictedApi")
    private fun closeFABMenu() {
//        removeBlurOnBackground();
        isFABOpen = false
        fabOverlay.visibility = View.GONE
        fab_container_add.animate().translationY(0f)
        fab_container_notification.animate().translationY(0f)
        fab_container_timer.animate().translationY(0f)
        fab_menu.animate().rotationBy(-270f).setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {}
            override fun onAnimationEnd(animator: Animator) {
                if (!isFABOpen) {
                    fab_container_add.visibility = View.GONE
                    fab_container_notification.visibility = View.GONE
                    fab_container_timer.visibility = View.GONE
                }
            }

            override fun onAnimationCancel(animator: Animator) {}
            override fun onAnimationRepeat(animator: Animator) {}
        })
        backgroundTask.changeDate(recyclerViewAdapter)
    }

    override fun onBackPressed() {
        if (isFABOpen) {
            closeFABMenu()
        } else {
            super.onBackPressed()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.i("RD_", "onPause")
    } // onPause - end

    override fun onRestart() {
        super.onRestart()
        Log.i("RD_", "onRestart")
        if (backgroundTask.checkHasDateChanged()) {
//            backgroundTask.summUpCheckBoxes();
        }
    }

    override fun onStop() {
        super.onStop()
        Log.i("RD_", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("RD_", "onDestroy")
    }

    override fun onResume() {
        super.onResume()
        Log.i("RD_", "onResume")
        if (backgroundTask.checkHasDateChanged()) {
            backgroundTask.summUpCheckBoxes(recyclerViewAdapter)
        }
    } //onResume - end

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (resultCode == Activity.RESULT_OK) {
            val position = recyclerViewAdapter.position
            try {
                val id = intent!!.getLongExtra(DetailviewActivity.ARG_ITEM_ID, -100)
                Log.i("ID_", id.toString())
                if (id == EMPTY_ID) {
                    Toast.makeText(this, "no changes", Toast.LENGTH_LONG).show()
                } else {
                    item = repository.readItem(id)
                    if (requestCode == CALL_CREATE_ITEM) {
                        Toast.makeText(this, "new item received", Toast.LENGTH_LONG).show()
                        recyclerViewAdapter.addItem(item)
                    }
                    if (requestCode == CALL_EDIT_ITEM) {
                        recyclerViewAdapter.loadListFromDB(item, position)
                        Toast.makeText(this, "item updated", Toast.LENGTH_LONG).show()
                    } else {
                    }
                }
            } catch (e: Exception) {
                recyclerViewAdapter.removeItem(position)
                Toast.makeText(this, "item removed $position", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        } // if resultCode
    } // onActivityResult

    private fun setChallengeEndingTime() {
        val calender = Calendar.getInstance()
        val year = calender[Calendar.YEAR]
        val month = calender[Calendar.MONTH]
        val day = calender[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(this@OverviewActivity,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                challengeEndingDateSetListener,
                year, month, day)
        // Hintergrund transparent machen
        datePickerDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        datePickerDialog.show()
        closeFABMenu()
    }

    private fun setNotificationTime() {
        val calender = Calendar.getInstance()
        val hour = calender[Calendar.HOUR_OF_DAY]
        val minute = calender[Calendar.MINUTE]
        val timePickerDialog = TimePickerDialog(this@OverviewActivity,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                alertTimeListener,
                hour, minute, true)

        // Hintergrund transparent machen
        timePickerDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        timePickerDialog.show()
        timepicker.currentHour = hour
        timepicker.currentMinute = minute
        timepicker.setIs24HourView(true)
    }

    private fun createNotificationIntent(timePicker: TimePicker?) {
        val time: Long
        val hour = timePicker!!.currentHour * 60 * 60 * 1000.toLong()
        val min = timePicker.currentMinute * 60 * 1000.toLong()
        time = hour + min
        val timeInMillis = Calendar.getInstance().timeInMillis
        val current_hour = (Calendar.getInstance()[Calendar.HOUR] + 12) * 60 * 60 * 1000.toLong()
        val current_min = (Calendar.getInstance()[Calendar.MINUTE] - 1) * 60 * 1000.toLong()
        val current_sec = Calendar.getInstance()[Calendar.SECOND] * 1000.toLong()
        val passed_millis = current_hour + current_min + current_sec
        val alertTimeInMillis = timeInMillis - passed_millis + time
        val notificationIntent = Intent(applicationContext, NotificationReceiver::class.java)
        val pIntent = PendingIntent.getBroadcast(applicationContext, CALL_NOTIFICATION_ALERT_TIME, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alertTimeInMillis, AlarmManager.INTERVAL_DAY, pIntent)
    }

    private fun showDetailViewForCreate() {
        val createIntent = Intent(this, DetailviewActivity::class.java)
        createIntent.putExtra(CALL_MODE, CALL_MODE_CREATE)
        startActivityForResult(createIntent, CALL_CREATE_ITEM)
    }

    private fun showDetailViewForEdit(item: Todo) {
        val editIntent = Intent(this, DetailviewActivity::class.java)
        editIntent.putExtra(DetailviewActivity.ARG_ITEM_ID, item.id)
        startActivityForResult(editIntent, CALL_EDIT_ITEM)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_overview, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.alertTime) {
            setNotificationTime()
            createNotificationIntent(timepicker)
            return true
        }
        if (id == R.id.clearScore) {
            backgroundTask.clearScore()
            //            myPrefs.applyPrefsToView(mainActivityViewModel);
            return true
        }
        if (id == R.id.clearTarget) {
            backgroundTask.clearTargetDate()
            //            myPrefs.applyPrefsToView(mainActivityViewModel);
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val CALL_NOTIFICATION_ALERT_TIME = 100
        private const val CALL_EDIT_ITEM = 0
        private const val CALL_CREATE_ITEM = 1
        private const val CALL_MODE = "callMode"
        const val CALL_MODE_CREATE = "create"
        const val EMPTY_ID = -99L
    }
}