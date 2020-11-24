package com.example.app.jasper.routinedeveloper_v2

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.app.jasper.routinedeveloper_v2.model.RecyclerViewAdapter
import com.example.app.jasper.routinedeveloper_v2.model.RecyclerViewAdapter.UserActionClickListener
import com.example.app.jasper.routinedeveloper_v2.model.Todo
import com.example.app.jasper.routinedeveloper_v2.repository.SharedPreferenceHelper
import com.example.app.jasper.routinedeveloper_v2.repository.TodoListRepository
import com.example.app.jasper.routinedeveloper_v2.utils.vibratePhone
import com.example.app.jasper.routinedeveloper_v2.view.utils.RecyclerViewItemDivider
import com.example.app.jasper.routinedeveloper_v2.viewmodel.ViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.backgroundDrawable
import java.util.*

class OverviewActivity : AppCompatActivity(), CoroutineScope by MainScope() {

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

    private lateinit var lock_icon: ImageView

    private lateinit var item_menu_overlay: CardView
    private lateinit var menu_icon: ImageView

    private lateinit var clear_score: LinearLayout
    private lateinit var add_item: LinearLayout
    private lateinit var set_daily_reminder: LinearLayout
    private lateinit var delte_all_items: LinearLayout
    private lateinit var set_ending_date: LinearLayout
    private lateinit var close_dialog_button: ImageView
    private lateinit var menuOverlay: View

    private lateinit var itemMenuOverlay: View
    private lateinit var item_longpress_menu_overlay: CardView
    private lateinit var edit_item: LinearLayout
    private lateinit var clear_item_score: LinearLayout
    private lateinit var delete_item: LinearLayout
    private lateinit var item_close_button: ImageView

    private lateinit var errorPrompt: CardView
    private lateinit var errorPromptText: TextView

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

        setNavigationBarColor(BACKGROUND_COLOR_LIGHT)


        SharedPreferenceHelper.initWith(applicationContext)
        viewModel = ViewModelProvider(this).get(ViewModel::class.java)

        initRepository(this)
        initView()

        checkIfFirstStart()
        observeLiveData()
        launch(Dispatchers.IO) {
            viewModel.loadRecentData()
        }
    }


    private fun observeLiveData() {
        viewModel.todoList.observe(this, Observer { todos: List<Todo> ->
            if (todos.isNotEmpty()) {
                val sortedList = todos.sortedBy { it.position }
                recyclerViewAdapter.setList(sortedList)
            }
        })
        viewModel.doneCounter.observe(this, Observer { textViewPlus.text = it.toString() })
        viewModel.undoneCounter.observe(this, Observer { textViewMinus.text = it.toString() })
        viewModel.challengeEndingDate.observe(this, Observer { challengeEndingDate.text = it })
        viewModel.isListLocked.observe(this, Observer {
            if (it == true) {
                lock_icon.backgroundDrawable = getDrawable(R.drawable.ic_lock_black)
                lock_icon.backgroundDrawable!!.setTint(getColor(R.color.textDark))
                viewModel.errorMessage.value = getString(R.string.lock_activated)
            } else {
                lock_icon.backgroundDrawable = getDrawable(R.drawable.ic_lock_open_black)
            }
        })
        viewModel.errorMessage.observe(this, Observer { errorMessage ->
            errorPromptText.text = errorMessage
        })
    }


    private fun initRepository(context: Context) {
        repository = TodoListRepository.getInstance(context)
    }


    private fun setEndingDateListener() {
        challengeEndingDate = findViewById(R.id.tvDate)
        challengeEndingDate.setOnClickListener {
            if (viewModel.isListLocked.value!!) {
                showErrorPrompt()
            } else {
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
    }

    private fun showErrorPrompt() {
        val timer = object: CountDownTimer(1500, 100) {
            override fun onFinish() { errorPrompt.visibility = View.GONE }

            override fun onTick(p0: Long) {
                errorPrompt.visibility = View.VISIBLE
                errorPrompt.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.shake))
                vibratePhone()
            }
        }
        timer.start()
    }

    private fun initView() {
        initRecyclerViewList()
        instantiateTimePicker()
        instantiateFABMenu()
        setEndingDateListener()
        initActionBar()
        initListMenu()
        initItemMenu()
        initErrorPrompt()
    }

    private fun initItemMenu() {
        itemMenuOverlay = findViewById(R.id.itemMenuOverlay)
        item_longpress_menu_overlay = findViewById(R.id.item_longpress_menu_overlay)
        clear_item_score = findViewById(R.id.clear_item_score)
        delete_item = findViewById(R.id.delete_item)
        edit_item = findViewById(R.id.edit_item)
        item_close_button = findViewById(R.id.item_close_button)

        item_close_button.setOnClickListener {
            item_longpress_menu_overlay.visibility = View.GONE
            itemMenuOverlay.visibility = View.GONE
        }


    }

    private fun initErrorPrompt() {
        errorPrompt = findViewById(R.id.errorPrompt)
        errorPromptText = findViewById(R.id.errorPromptText)
    }

    private fun initListMenu() {
        menu_icon = findViewById(R.id.menu_icon)
        lock_icon = findViewById(R.id.lock_icon)
        menuOverlay = findViewById(R.id.menuOverlay)

        item_menu_overlay = findViewById(R.id.item_menu_overlay)
        add_item = findViewById(R.id.add_item)
        clear_score = findViewById(R.id.clear_score)
        set_daily_reminder = findViewById(R.id.daily_reminder)
        set_ending_date = findViewById(R.id.ending_date)
        delte_all_items = findViewById(R.id.delete_items)
        close_dialog_button = findViewById(R.id.close_button)

        menu_icon.setOnClickListener {
            if (viewModel.isListLocked.value!!) {
                showErrorPrompt()
            } else {
                item_menu_overlay.visibility = View.VISIBLE
                menuOverlay.visibility = View.VISIBLE
            }
        }

        add_item.setOnClickListener {
            showDetailViewForCreate(viewModel.todoList.value!!.size + 1)
            item_menu_overlay.visibility = View.GONE
            menuOverlay.visibility = View.GONE

        }
        clear_score.setOnClickListener {
            viewModel.clearScore()
            item_menu_overlay.visibility = View.GONE
            menuOverlay.visibility = View.GONE
        }
        set_daily_reminder.setOnClickListener {
            showTimePicker()
            item_menu_overlay.visibility = View.GONE
            menuOverlay.visibility = View.GONE
        }
        set_ending_date.setOnClickListener {
                setChallengeEndingTime()
                item_menu_overlay.visibility = View.GONE
                menuOverlay.visibility = View.GONE
        }
        delte_all_items.setOnClickListener {

            for (item in viewModel.todoList.value!!) {
                viewModel.deleteItem(item.id)
            }
            item_menu_overlay.visibility = View.GONE
            menuOverlay.visibility = View.GONE
        }
        close_dialog_button.setOnClickListener {
            item_menu_overlay.visibility = View.GONE
            menuOverlay.visibility = View.GONE
        }

        lock_icon.setOnClickListener {
            viewModel.toggleLockList()
        }
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
                if (viewModel.isListLocked.value!!) {
                    showErrorPrompt()
                } else {
                    edit_item.setOnClickListener {
                        item_longpress_menu_overlay.visibility = View.GONE
                        itemMenuOverlay.visibility = View.GONE
                        showDetailViewForEdit(recyclerViewAdapter.getItem(position))
                    }
                    item_longpress_menu_overlay.visibility = View.VISIBLE
                    itemMenuOverlay.visibility = View.VISIBLE
                    clear_item_score.setOnClickListener {
                        viewModel.clearItemScores(recyclerViewAdapter.getItem(position))
                        Log.d("SCORE", "${recyclerViewAdapter.getItem(position).name}")
                        item_longpress_menu_overlay.visibility = View.GONE
                        itemMenuOverlay.visibility = View.GONE
                    }
                    delete_item.setOnClickListener {
                        viewModel.deleteItem(recyclerViewAdapter.getItem(position).id)
                        item_longpress_menu_overlay.visibility = View.GONE
                        itemMenuOverlay.visibility = View.GONE
                    }
                }
            }
        }

        recyclerView = findViewById(R.id.recycler_view_data)
        recyclerViewAdapter = RecyclerViewAdapter(
                userActionClickListener,
                ArrayList(0),
                viewModel)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = recyclerViewAdapter

        val diver: ItemDecoration = RecyclerViewItemDivider(ResourcesCompat.getDrawable(resources, R.drawable.item_list_divider, null)!!)
        recyclerView.addItemDecoration(diver)

        val touchCallback = object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.DOWN or
                        ItemTouchHelper.UP or
                        ItemTouchHelper.START or
                        ItemTouchHelper.END,
                ItemTouchHelper.END) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                val position1 = viewHolder.adapterPosition + 1
                val item1 = recyclerViewAdapter.getItem(viewHolder.adapterPosition)
                val item2 = recyclerViewAdapter.getItem(target.adapterPosition)
                val position2 = target.adapterPosition + 1
                viewModel.swapPositions(item1, position1, item2, position2)
                Toast.makeText(applicationContext, "positions swapped", Toast.LENGTH_SHORT).show()
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                Toast.makeText(applicationContext, "item deleted", Toast.LENGTH_SHORT).show()
                val position = viewHolder.adapterPosition
                val item = recyclerViewAdapter.getItem(position)
                launch(Dispatchers.IO) {
                    viewModel.deleteItem(item.id)
                    viewModel.loadRecentData()
                }
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
            showDetailViewForCreate(viewModel.todoList.value!!.size + 1)
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
        setNavigationBarColor(BACKGROUND_COLOR_DARK)
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
        setNavigationBarColor(BACKGROUND_COLOR_LIGHT)
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
        if (viewModel.checkHasDateChanged()) {
            viewModel.sumUpCheckBoxes()
        }
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
                    if (requestCode == CALL_CREATE_ITEM) {
                        Toast.makeText(this, "item created", Toast.LENGTH_LONG).show()
                    }
                    if (requestCode == CALL_EDIT_ITEM) {
                        Toast.makeText(this, "item updated", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this, "item removed", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        } // if resultCode
        launch(Dispatchers.IO) {
            viewModel.loadRecentData()
        }
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


    private fun showDetailViewForCreate(listSizePlusOne: Int) {
        val createIntent = Intent(this, DetailviewActivity::class.java)
        createIntent.putExtra(CALL_MODE, CALL_MODE_CREATE)
        createIntent.putExtra(POSITION, listSizePlusOne)
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

    private fun setNavigationBarColor(color: Int) {
        window.navigationBarColor = ResourcesCompat.getColor(resources, color, null)
    }

    companion object {
        private const val CALL_EDIT_ITEM = 0
        private const val CALL_CREATE_ITEM = 1
        private const val CALL_MODE = "callMode"
        const val POSITION = "POSITION"
        const val CALL_MODE_CREATE = "create"
        const val EMPTY_ID = -99L
        const val BACKGROUND_COLOR_LIGHT = R.color.colorBackground
        const val BACKGROUND_COLOR_DARK = R.color.colorTransBg
    }
}