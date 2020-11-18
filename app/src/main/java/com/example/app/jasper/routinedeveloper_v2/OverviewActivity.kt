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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app.jasper.routinedeveloper_v2.model.RecyclerViewAdapter
import com.example.app.jasper.routinedeveloper_v2.model.RecyclerViewAdapter.UserActionClickListener
import com.example.app.jasper.routinedeveloper_v2.model.Todo
import com.example.app.jasper.routinedeveloper_v2.repository.SharedPreferenceHelper
import com.example.app.jasper.routinedeveloper_v2.repository.TodoListRepository
import com.example.app.jasper.routinedeveloper_v2.viewmodel.ViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class OverviewActivity : AppCompatActivity() {

    private var isFABOpen = false

    private lateinit var userActionClickListener: UserActionClickListener

    private lateinit var timepicker: TimePicker
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
    private lateinit var viewModel: ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_overview)

        SharedPreferenceHelper.initWith(applicationContext)
        viewModel = ViewModelProvider(this).get(ViewModel::class.java)

        initRepository(this)
        initView()

        checkIfFirstStart()
        observeLiveData()
        loadData()
    }

    private fun observeLiveData() {
        viewModel.todoList.observe(this, Observer { todos: List<Todo> ->
            if (todos.isNotEmpty()) {
                Log.d("CHECKED", "todos: ${todos.get(0).isDone}")
                val sortedList = todos.sortedBy { it.name }
                recyclerViewAdapter.setList(sortedList)
            }
        })
        viewModel.doneCounter.observe(this, Observer { textViewPlus.text = it.toString() })
        viewModel.undoneCounter.observe(this, Observer { textViewMinus.text = it.toString() })
        viewModel.challengeEndingDate.observe(this, Observer { challengeEndingDate.text = it })
    }

    private fun initRepository(context: Context) {
        repository = TodoListRepository.getInstance(context)
    }


    private fun setEndingDateListener() {
        challengeEndingDate = findViewById(R.id.tvDate)
        challengeEndingDate.setOnClickListener {
            val calender = Calendar.getInstance()
            val year = calender[Calendar.YEAR]
            val month = calender[Calendar.MONTH]
            val day = calender[Calendar.DAY_OF_MONTH]
            val datePickerDialog = DatePickerDialog(this@OverviewActivity,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    challengeEndingDateSetListener,
                    year, month, day)
            datePickerDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            datePickerDialog.show()
        }
        challengeEndingDateSetListener = OnDateSetListener { datePicker, year, month, day ->
            var month = month
            month += 1
            val date = "$day.$month.$year"
            viewModel.setChallengeEndingDate(date)
        }
    }

    private fun initView() {
        initRecyclerViewList()
        instantiateTimePicker()
        instantiateFABMenu()
        setEndingDateListener()
        initActionBar()
    }

    private fun loadData() {
        viewModel.loadData()
    }

    private fun initActionBar() {
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        supportActionBar!!.setCustomView(R.layout.custom_action_bar_layout)
        textViewPlus = findViewById(R.id.scorePlus)
        textViewMinus = findViewById(R.id.scoreMinus)
    }

    private fun initRecyclerViewList() {
        userActionClickListener = object : UserActionClickListener {
            override fun onItemClick(position: Int) {
                showDetailViewForEdit(recyclerViewAdapter.getItem(position))
            }
        }

        recyclerView = findViewById(R.id.recycler_view_data)
        recyclerViewAdapter = RecyclerViewAdapter(
                applicationContext,
                userActionClickListener,
                ArrayList(0),
                viewModel)


        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = recyclerViewAdapter

        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(divider)

        val touchCallback = object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.DOWN or
                        ItemTouchHelper.UP or
                        ItemTouchHelper.START or
                        ItemTouchHelper.END,
                0) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                recyclerViewAdapter.swapPositions(fromPosition, toPosition)
                Toast.makeText(applicationContext, "positions swapped", Toast.LENGTH_SHORT).show()
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                Toast.makeText(applicationContext, "item deleted", Toast.LENGTH_SHORT).show()
                val position = viewHolder.adapterPosition
                recyclerViewAdapter.deleteItem(position)
            }

        }
        val itemTouchHelper = ItemTouchHelper(touchCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

    }

    private fun instantiateTimePicker() {
        timepicker = TimePicker(this)
        alertTimeListener = OnTimeSetListener { timePicker, hour, min ->
            timepicker.currentHour = hour
            timepicker.currentMinute = min
            viewModel.createNotificationIntent(hour, min)
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
//        mainActivityViewModel.incrementDate()
//        backgroundTask.changeDate(recyclerViewAdapter)
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
        Log.i("DATE", "onRestart")
        Log.d("DATE", "${viewModel.checkHasDateChanged()}")
        if (viewModel.checkHasDateChanged()) {
            viewModel.sumUpCheckBoxes()
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
        loadData()
        Log.i("DATE", "onResume")
        Log.d("DATE", "${viewModel.checkHasDateChanged()}")
//        if (viewModel.checkHasDateChanged()) {
        viewModel.sumUpCheckBoxes()
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (resultCode == Activity.RESULT_OK) {
            try {
                val id = intent!!.getLongExtra(DetailviewActivity.ARG_ITEM_ID, -100)
                Log.i("ID_", id.toString())
                if (id == EMPTY_ID) {
                    Toast.makeText(this, "no changes", Toast.LENGTH_LONG).show()
                } else {
//                    item = repository.readItem(id)
                    if (requestCode == CALL_CREATE_ITEM) {
                        Toast.makeText(this, "item created", Toast.LENGTH_LONG).show()
//                        viewModel.addItem(item)
                    }
                    if (requestCode == CALL_EDIT_ITEM) {
//                        viewModel.updateItem(item)
                        Toast.makeText(this, "item updated", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this, "item removed", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        } // if resultCode
        viewModel.loadData()
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

    private fun showTimePicker() {
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
            showTimePicker()
            return true
        }
        if (id == R.id.clearScore) {
            viewModel.clearScore()
            return true
        }
        if (id == R.id.clearTarget) {
            viewModel.clearTargetDate()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkIfFirstStart() {
        if (SharedPreferenceHelper.firstStart) {
            Toast.makeText(this, "Welcome to Routine Developer!", Toast.LENGTH_SHORT).show()
            viewModel.setFirstStart(false)
        }
    }

    companion object {
        private const val CALL_EDIT_ITEM = 0
        private const val CALL_CREATE_ITEM = 1
        private const val CALL_MODE = "callMode"
        const val CALL_MODE_CREATE = "create"
        const val EMPTY_ID = -99L
    }
}