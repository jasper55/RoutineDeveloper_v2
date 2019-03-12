package com.example.app.jasper.routinedeveloper_v2;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.app.jasper.routinedeveloper_v2.model.MySharedPrefs;
import com.example.app.jasper.routinedeveloper_v2.model.RecyclerViewAdapter;
import com.example.app.jasper.routinedeveloper_v2.model.Todo;
import com.example.app.jasper.routinedeveloper_v2.model.TodoRoomDatabase;
import com.example.app.jasper.routinedeveloper_v2.repository.TodoListRepository;
import com.example.app.jasper.routinedeveloper_v2.viewmodel.MainActivityViewModel;

import java.util.Calendar;
import java.util.List;

public class OverviewActivity extends AppCompatActivity {

    private TimePicker timepicker;
    private TimePickerDialog.OnTimeSetListener alertTimeListener;
    private static final int CALL_NOTIFICATION_ALERT_TIME = 100;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;

    private TextView challengeEndingDate;
    private DatePickerDialog.OnDateSetListener challengeEndingDateSetListener;
    private TextView textViewPlus, textViewMinus;

    private static final int CALL_EDIT_ITEM = 0;
    private static final int CALL_CREATE_ITEM = 1;
    private static final String CALL_MODE = "callMode";
    public static final String CALL_MODE_CREATE = "create";
    public static final Long EMPTY_ID = -99L;

    private Todo item;
    //    private SQLCRUDOperations crudOperations;
    private TodoListRepository repository;
    private TodoRoomDatabase roomDatabase;

    private boolean isFABOpen = false;
    private FloatingActionButton fab_add, fab_timer, fab_notification, fab_menu;
    LinearLayout fab_container_add, fab_container_notification, fab_container_timer;
    View fabOverlay;
    private MySharedPrefs myPrefs;
    private BackgroundTasks backgroundTask;
    MainActivityViewModel mainActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_overview);


        initDB();
//        crudOperations = SQLCRUDOperations.getInstance(getApplicationContext());

        myPrefs = MySharedPrefs.getInstance(this);
        myPrefs.firstTimeStartingApp(this);
        myPrefs.loadSharedPrefs();


        initViewModel();
        //myPrefs.connectViewModel(mainActivityViewModel);
        observeChangestoUiElements();
        instantiateViewElements();

        myPrefs.applyPrefsToView(mainActivityViewModel);

        backgroundTask = BackgroundTasks.getInstance(this, mainActivityViewModel);
//        backgroundTask.init(this,
//                mainActivityViewModel.getScorePlus().toString(),
//                mainActivityViewModel.getScoreMinus().toString());

        setClickListenersToViewElements();
        instantiateTimePicker();
        instantiateFABMenu();
    }       // onCreate() - end

    private void initDB() {
        roomDatabase = TodoRoomDatabase.getInstance(getApplicationContext());
        repository = new TodoListRepository(getApplicationContext());
//        roomDatabase = Room.databaseBuilder(getApplicationContext(),
//                TodoRoomDatabase.class, "todoDB")
//                .allowMainThreadQueries().build();

//        new AsyncTask<Void, Void, TodoListRepository>(){
//            @Override
//            protected TodoListRepository doInBackground(Void... voids) {
//                repository = new TodoListRepository(getApplicationContext());
//                return repository;
//            }
//        }.execute();



    }


    private void initViewModel() {
        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        mainActivityViewModel.receiveDataFromRepo(repository);
        mainActivityViewModel.initUiElements(myPrefs);
    }

    private void observeChangestoUiElements() {
        observeTodoList();
        observeScoreCounter();
        observeEndingDate();
    }

    public void observeTodoList() {
        mainActivityViewModel.getTodoList().observe(this, new Observer<List<Todo>>() {
            @Override
            public void onChanged(@Nullable List<Todo> todos) {
                recyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }

    public void observeScoreCounter() {
        mainActivityViewModel.getScorePlus().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textViewPlus.setText(s);
            }
        });
        mainActivityViewModel.getScoreMinus().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textViewMinus.setText(s);
            }
        });
    }

    public void observeEndingDate() {
        mainActivityViewModel.getEndingDate().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                challengeEndingDate.setText(s);
            }
        });
    }

    private void setClickListenersToViewElements() {
        //setItemListClickListener();
        setEndingDateListener();
    }

    private void setEndingDateListener() {
        challengeEndingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calender = Calendar.getInstance();
                int year = calender.get(Calendar.YEAR);
                int month = calender.get(Calendar.MONTH);
                int day = calender.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(OverviewActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        challengeEndingDateSetListener,
                        year, month, day);
                // Hintergrund transparent machen
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        challengeEndingDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = day + "." + month + "." + year;
                challengeEndingDate.setText(date);
                myPrefs.saveSharedPrefs();
            }
        };
    }

    private void instantiateViewElements() {
        initRecyclerViewList();
        challengeEndingDate = findViewById(R.id.tvDate);
        initActionBar();
    }

    private void initActionBar() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_layout);
        initScoreContainer();
    }

    private void initScoreContainer() {
        this.textViewPlus = findViewById(R.id.scorePlus);
        this.textViewMinus = findViewById(R.id.scoreMinus);
    }

    private void initRecyclerViewList() {
        recyclerView = findViewById(R.id.recycler_view_data);
        recyclerViewAdapter = new RecyclerViewAdapter(this, mainActivityViewModel.getTodoList().getValue(), new RecyclerViewAdapter.CustomItemClickListener() {

            @Override
            public void onItemClick(int position) {
                showDetailViewForEdit(recyclerViewAdapter.getItem(position));
            }

            @Override
            public void onLongItemClick(int position) {
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void instantiateTimePicker() {
        this.timepicker = new TimePicker(this);
        alertTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                timepicker.setCurrentHour(hour);
                timepicker.setCurrentMinute(min);
                createNotificationIntent(timepicker);
            }
        };
    }

    private void instantiateFABMenu() {

        fab_container_add = findViewById(R.id.fab_container_add);
        fab_container_notification = findViewById(R.id.fab_container_notification);
        fab_container_timer = findViewById(R.id.fab_container_timer);
        fabOverlay = findViewById(R.id.fabOverlay);

        fab_menu = findViewById(R.id.fab_menu);
        fab_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });

        fabOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
            }
        });

        fab_add = findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDetailViewForCreate();
                closeFABMenu();
            }
        });

        fab_timer = findViewById(R.id.fab_timer);
        fab_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setChallengeEndingTime();
                closeFABMenu();
            }
        });

        fab_notification = findViewById(R.id.fab_notification);
        fab_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNotificationTime();
                createNotificationIntent(timepicker);
                closeFABMenu();
            }
        });
    }

    @SuppressLint("RestrictedApi")
    private void showFABMenu() {
        isFABOpen = true;
        //applyBlurOnBackground();
        fab_container_add.setVisibility(View.VISIBLE);
        fab_container_timer.setVisibility(View.VISIBLE);
        fab_container_notification.setVisibility(View.VISIBLE);
        fabOverlay.setVisibility(View.VISIBLE);

        fab_menu.animate().rotationBy(270).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        fab_container_add.animate().translationY(-getResources().getDimension(R.dimen.standard_175));
        fab_container_notification.animate().translationY(-getResources().getDimension(R.dimen.standard_120));
        fab_container_timer.animate().translationY(-getResources().getDimension(R.dimen.standard_65));
    }

    @SuppressLint("RestrictedApi")
    private void closeFABMenu() {
//        removeBlurOnBackground();
        isFABOpen = false;
        fabOverlay.setVisibility(View.GONE);
        fab_container_add.animate().translationY(0);
        fab_container_notification.animate().translationY(0);
        fab_container_timer.animate().translationY(0);
        fab_menu.animate().rotationBy(-270).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFABOpen) {
                    fab_container_add.setVisibility(View.GONE);
                    fab_container_notification.setVisibility(View.GONE);
                    fab_container_timer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        backgroundTask.changeDate();
    }

    @Override
    public void onBackPressed() {
        if (isFABOpen) {
            closeFABMenu();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("RD_", "onPause");
    }       // onPause - end

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("RD_", "onRestart");
        if (backgroundTask.checkHasDateChanged()) {
            backgroundTask.summUpCheckBoxes();
        }
        myPrefs.applyPrefsToView(mainActivityViewModel);
    }

    @Override
    protected void onStop() {
        super.onStop();
        myPrefs.saveSharedPrefs();
        Log.i("RD_", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myPrefs.saveSharedPrefs();
        Log.i("RD_", "onDestroy");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("RD_", "onResume");
        if (backgroundTask.checkHasDateChanged()) {
            backgroundTask.summUpCheckBoxes();
        }
        myPrefs.applyPrefsToView(mainActivityViewModel);
    }       //onResume - end

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {

        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_OK) {
            int position = recyclerViewAdapter.getPosition();

            try {
                long id = intent.getLongExtra(DetailviewActivity.ARG_ITEM_ID, -100);
                Log.i("ID_", String.valueOf(id));
                if (id == EMPTY_ID) {
                    Toast.makeText(this, "no changes", Toast.LENGTH_LONG).show();
                } else {

//                    item = crudOperations.readItem(id);
                    item = repository.readItem(id);

                    if (requestCode == CALL_CREATE_ITEM) {
                        Toast.makeText(this, "new item received", Toast.LENGTH_LONG).show();
                        recyclerViewAdapter.addItem(item);
                    }

                    if (requestCode == CALL_EDIT_ITEM) {
                        recyclerViewAdapter.updateList(item, position);
                        Toast.makeText(this, "item updated", Toast.LENGTH_LONG).show();
                    } else {
                    }
                }
            } catch (Exception e) {
                recyclerViewAdapter.removeItem(position);
                Toast.makeText(this, "item removed " + String.valueOf(position), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

        } // if resultCode

    }   // onActivityResult

    private void setChallengeEndingTime() {

        Calendar calender = Calendar.getInstance();
        int year = calender.get(Calendar.YEAR);
        int month = calender.get(Calendar.MONTH);
        int day = calender.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(OverviewActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                challengeEndingDateSetListener,
                year, month, day);
        // Hintergrund transparent machen
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
        closeFABMenu();
    }

    private void setNotificationTime() {

        Calendar calender = Calendar.getInstance();
        int hour = calender.get(Calendar.HOUR_OF_DAY);
        int minute = calender.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(OverviewActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                alertTimeListener,
                hour, minute, true);

        // Hintergrund transparent machen
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.show();

        this.timepicker.setCurrentHour(hour);
        this.timepicker.setCurrentMinute(minute);
        this.timepicker.setIs24HourView(true);
    }

    private void createNotificationIntent(TimePicker timePicker) {
        long time;
        long hour = timePicker.getCurrentHour() * 60 * 60 * 1000;
        long min = timePicker.getCurrentMinute() * 60 * 1000;

        time = hour + min;
        long timeInMillis = Calendar.getInstance().getTimeInMillis();

        long current_hour = (Calendar.getInstance().get(Calendar.HOUR) + 12) * 60 * 60 * 1000;
        long current_min = (Calendar.getInstance().get(Calendar.MINUTE) - 1) * 60 * 1000;
        long current_sec = Calendar.getInstance().get(Calendar.SECOND) * 1000;
        long passed_millis = current_hour + current_min + current_sec;

        long alertTimeInMillis = timeInMillis - passed_millis + time;

        Intent notificationIntent = new Intent(getApplicationContext(), NotificationReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(getApplicationContext(), CALL_NOTIFICATION_ALERT_TIME, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alertTimeInMillis, AlarmManager.INTERVAL_DAY, pIntent);
    }

    private void showDetailViewForCreate() {
        Intent createIntent = new Intent(this, DetailviewActivity.class);
        createIntent.putExtra(CALL_MODE, CALL_MODE_CREATE);
        startActivityForResult(createIntent, CALL_CREATE_ITEM);
    }

    private void showDetailViewForEdit(Todo item) {
        Intent editIntent = new Intent(this, DetailviewActivity.class);
        editIntent.putExtra(DetailviewActivity.ARG_ITEM_ID, item.getId());
        startActivityForResult(editIntent, CALL_EDIT_ITEM);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_overview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.alertTime) {
            setNotificationTime();
            createNotificationIntent(this.timepicker);
            return true;
        }

        if (id == R.id.clearScore) {
            backgroundTask.clearScore();
            myPrefs.applyPrefsToView(mainActivityViewModel);
            return true;
        }
        if (id == R.id.clearTarget) {
            backgroundTask.clearTargetDate();
            myPrefs.applyPrefsToView(mainActivityViewModel);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
