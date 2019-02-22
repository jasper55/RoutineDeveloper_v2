package com.example.app.jasper.routinedeveloper_v2;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.example.app.jasper.routinedeveloper_v2.model.ListItemViewHolder;
import com.example.app.jasper.routinedeveloper_v2.model.MySharedPrefs;
import com.example.app.jasper.routinedeveloper_v2.model.SQLCRUDOperations;
import com.example.app.jasper.routinedeveloper_v2.model.Todo;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jp.wasabeef.blurry.Blurry;

public class OverviewActivity extends AppCompatActivity {

    private TimePicker timepicker;
    private TimePickerDialog.OnTimeSetListener alertTimeListener;
    private static final int CALL_NOTIFICATION_ALERT_TIME = 100;

    private ViewGroup listView;
    private ArrayAdapter<Todo> listViewAdapter;
    private List<Todo> todoList = new ArrayList<>();
    private long selectedItemId;
    private Todo selectedItem;

    private TextView challengeEndingDate;
    private DatePickerDialog.OnDateSetListener challengeEndingDateSetListener;
    private TextView textViewPlus, textViewMinus;

    private static final int CALL_EDIT_ITEM = 0;
    private static final int CALL_CREATE_ITEM = 1;

    private Todo item;
    private SQLCRUDOperations crudOperations;

    private boolean isFABOpen = false;
    private FloatingActionButton fab_add, fab_timer, fab_notification, fab_menu;
    LinearLayout fab_container_add, fab_container_notification, fab_container_timer;
    View fabOverlay;
    private MySharedPrefs myPrefs;
    private BackgroundTasks backgroundTask;


    //private ViewGroup content_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_overview);


        this.crudOperations = new SQLCRUDOperations(this);
        this.myPrefs = new MySharedPrefs(this);
        this.backgroundTask = new BackgroundTasks(this, todoList, myPrefs, crudOperations, challengeEndingDate, textViewPlus, textViewMinus);

        myPrefs.firstTimeStartingApp();

        instantiateViewElements();
        setListenersToViewElements();

        myPrefs.loadSharedPrefs();
        myPrefs.applyPrefsToView(challengeEndingDate, textViewPlus, textViewMinus);

        instantiateTimePicker();

        instantiateFABMenu();
        registerForContextMenu(listView);
    }       // onCreate() - end


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_long_item_clicked, menu);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_delete:
                crudOperations.deleteItem(selectedItemId);
                listViewAdapter.setNotifyOnChange(true);
                listViewAdapter.notifyDataSetChanged();
                return true;
            case R.id.option_edit:
                selectedItem = listViewAdapter.getItem((int) selectedItemId);
                showDetailViewForEdit(selectedItem);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void setListenersToViewElements() {
        ((ListView) listView).setAdapter(listViewAdapter);
        listViewAdapter.addAll(crudOperations.readAllItems());


        listView.setOnContextClickListener(new View.OnContextClickListener() {
            @Override
            public boolean onContextClick(View v) {
                Toast.makeText(getApplicationContext(),"LongClick",Toast.LENGTH_SHORT).show();
                //registerForContextMenu(v);
                return true;
            }
        });


//        ((ListView) listView).setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getApplicationContext(),"LongClick",Toast.LENGTH_SHORT).show();
//                registerForContextMenu(listView);
//                selectedItemId = listViewAdapter.getItemId(position);
//                selectedItem = listViewAdapter.getItem(position);
//                return true;
//            }
//        });

        setItemListClickListener();
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
                myPrefs.saveSharedPrefs(challengeEndingDate, textViewPlus, textViewMinus);
            }
        };
    }

    private void setItemListClickListener() {
        ((ListView) listView).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Todo selectedItem = listViewAdapter.getItem(position);
                Log.i("RD_Position: ", String.valueOf(position));
                Log.i("RD_ViewID: ", String.valueOf(view.getId()));
                showDetailViewForEdit(selectedItem);
            }
        });
    }

    private void instantiateViewElements() {
        setUpListView();

        challengeEndingDate = findViewById(R.id.tvDate);
        this.textViewPlus = findViewById(R.id.scorePlus);
        this.textViewMinus = findViewById(R.id.scoreMinus);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setUpListView() {
        listView = (ViewGroup) findViewById(R.id.ListView_data);
        listViewAdapter = new ArrayAdapter<Todo>(
                this, R.layout.layout_todoitem, todoList) {

            @NonNull
            @Override
            public View getView(int position, @Nullable View existingView, @NonNull ViewGroup parent) {

                Todo item = this.getItem(position);
                View itemView = existingView;
                ListItemViewHolder viewHolder;

                if (itemView == null) {
                    Log.i("OverviewActivity", "create new View for position " + position);
                    itemView = getLayoutInflater().inflate(R.layout.layout_todoitem, null);
                    viewHolder = new ListItemViewHolder(listViewAdapter, crudOperations, itemView);
                    itemView.setTag(viewHolder);
                    viewHolder.checkBox.setTag(position);
                } else {
                    Log.i("OverviewActivity", "recycle new View for position " + position);
                    viewHolder = (ListItemViewHolder) itemView.getTag();
                    viewHolder.checkBox.setTag(position);
                }
                // bind the data to the view
                viewHolder.unbind();
                viewHolder.bind(item);

                return itemView;
            }

        };
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
        removeBlurOnBackground();
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
    }

    private void applyBlurOnBackground() {
        ViewGroup content_main = findViewById(R.id.content_main);
        Blurry.with(content_main.getContext())
                .radius(1).sampling(10)
                .animate(500)
                .onto(content_main);
    }

    private void removeBlurOnBackground() {
//        ViewGroup content_main = findViewById(R.id.content_main);
//        Blurry.with(getBaseContext())
//                .radius(0).sampling(0)
//                .animate(500)
//                .onto(content_main);
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
        backgroundTask.listenForScoreUpdates();
    }       // onPause - end

    @Override
    protected void onResume() {
        super.onResume();
        backgroundTask.listenForScoreUpdates();
        myPrefs.loadSharedPrefs();
        myPrefs.applyPrefsToView(challengeEndingDate, textViewPlus, textViewMinus);
    }       //onResume - end

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        listViewAdapter.clear();
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_OK) {
            try {
                long id = intent.getLongExtra(DetailviewActivity.ARG_ITEM_ID, 1);
                item = crudOperations.readItem(id);

                if (requestCode == CALL_CREATE_ITEM) {
                    Toast.makeText(this, "new item received", Toast.LENGTH_LONG).show();
                }
                if (requestCode == CALL_EDIT_ITEM) {
                    Toast.makeText(this, "item updated", Toast.LENGTH_LONG).show();
                } else {
                    Log.i("onActivityResult", "No new item received");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } // if resultCode
        updateList(item);

    }   // onActivityResult

    protected void addItemToList(Todo item) {
        this.listViewAdapter.add(item);
        ((ListView) this.listView).setSelection(this.listViewAdapter.getPosition(item));
    }

    protected void updateList(Todo item) {
        listViewAdapter.addAll(crudOperations.readAllItems());
        ((ListView) this.listView).setSelection(this.listViewAdapter.getPosition(item));
    }

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

        long alertTimeinMillis = timeInMillis - passed_millis + time;

        long dif = alertTimeinMillis - timeInMillis;
        Log.i("notTime", String.valueOf(alertTimeinMillis));
        Log.i("notTime", String.valueOf(timeInMillis));

        //Log.i("currentTime", String.valueOf(System.currentTimeMillis()));
        Log.i("dif", String.valueOf(dif));

        Intent notificationIntent = new Intent(getApplicationContext(), NotificationReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(getApplicationContext(), CALL_NOTIFICATION_ALERT_TIME, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alertTimeinMillis, AlarmManager.INTERVAL_DAY, pIntent);
    }

    private void showDetailViewForCreate() {
        Intent createIntent = new Intent(this, DetailviewActivity.class);
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
            return true;
        }
        if (id == R.id.clearTarget) {
            backgroundTask.clearTargetDate();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
