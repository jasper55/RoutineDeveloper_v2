package com.example.app.jasper.routinedeveloper_v2;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.example.app.jasper.routinedeveloper_v2.model.LocalData;
import com.example.app.jasper.routinedeveloper_v2.model.SQLCRUDOperations;
import com.example.app.jasper.routinedeveloper_v2.model.Todo;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class OverviewActivity extends AppCompatActivity {


    /*private static final String ENDING_DATE = "endingDate";
    String endingDateString;
    SharedPreferences myPrefs;*/

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String DATE = "date";
    private static final String SCOREPLUS = "scorePlus";
    private static final String SCOREMINUS = "scoreMinus";
    private String date, prefs_scorePlus, prefs_scoreMinus;

    private TimePicker timepicker;
    private TimePickerDialog.OnTimeSetListener alertTimeListener;
    private static final int CALL_NOTIFICATION_ALERT_TIME = 100;

    private ViewGroup listView;
    private ArrayAdapter<Todo> listViewAdapter;
    private List<Todo> todoList = new ArrayList<>();

    private TextView endingDate;
    private DatePickerDialog.OnDateSetListener endingDateSetListener;
    private TextView textViewPlus, textViewMinus;


    private static final int CALL_EDIT_ITEM = 0;
    private static final int CALL_CREATE_ITEM = 1;
    private static final int RESULT_DELETED = 99;

    private static final int REQUEST_PERMISSIONS = 4;
    private Todo item;
    private SQLCRUDOperations crudOperations;
    //private LocalData localData;
    private int done;
    private int undone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_overview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        this.crudOperations = new SQLCRUDOperations(this);
        //this.localData = new LocalData();

        listView = (ViewGroup) findViewById(R.id.ListView_layout);
        listViewAdapter = new ArrayAdapter<Todo>(
                this, R.layout.layout_todoitem, todoList){

            @NonNull
            @Override
            public View getView(int position, @Nullable View existingView, @NonNull ViewGroup parent) {

                Todo item = this.getItem(position);
                View itemView = existingView;
                ListItemViewHolder viewHolder;


                if (itemView == null) {
                    Log.i("OverviewActivity", "create new View for position " + position);
                    itemView = getLayoutInflater().inflate(R.layout.layout_todoitem,null);
                    viewHolder = new ListItemViewHolder((ViewGroup) itemView);
                    itemView.setTag(viewHolder);
                    viewHolder.checkBox.setTag(position);
                } else {
                    Log.i("OverviewActivity", "recycle new View for position " + position);
                    viewHolder = (ListItemViewHolder)itemView.getTag();
                    viewHolder.checkBox.setTag(position);
                }
                // bind the data to the view
                viewHolder.unbind();
                viewHolder.bind(item);

                return itemView;
            }

        };

        ((ListView)listView).setAdapter(listViewAdapter);
        listViewAdapter.addAll(crudOperations.readAllItems());

        ((ListView) listView).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Todo selectedItem = listViewAdapter.getItem(position);
                showDetailViewForEdit(selectedItem);
            }
        });



        endingDate = (TextView) findViewById(R.id.tvDate);
        this.textViewPlus = (TextView) findViewById(R.id.scorePlus);
        this.textViewMinus = (TextView) findViewById(R.id.scoreMinus);
        loadData();
        applyDataToView();


        
        endingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calender = Calendar.getInstance();
                int year = calender.get(Calendar.YEAR);
                int month = calender.get(Calendar.MONTH);
                int day = calender.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(OverviewActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        endingDateSetListener,
                        year, month, day);
                // Hintergrund transparent machen
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
                month = month + 1;
                String date = day + "." + month + "." + year;

            }
        });

        endingDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = day + "." + month + "." + year;
                endingDate.setText(date);

            }
        };





        this.timepicker = new TimePicker(this);
        alertTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                timepicker.setCurrentHour(hour);
                timepicker.setCurrentMinute(min);
                createNotificationIntent(timepicker);
            }
        };


        FloatingActionButton fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDetailViewForCreate();
            }
        });


        FloatingActionButton fab_timer = (FloatingActionButton) findViewById(R.id.fab_timer);
        fab_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEndingTime();
            }
        });

        FloatingActionButton fab_notification = (FloatingActionButton) findViewById(R.id.fab_notificaton);
        fab_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNotificationTime();
                createNotificationIntent(timepicker);
            }
        });

    }       // onCreate() - end


    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);

        date = sharedPreferences.getString(DATE,"");
        prefs_scorePlus = sharedPreferences.getString(SCOREPLUS,"0");
        prefs_scoreMinus = sharedPreferences.getString(SCOREMINUS,"0");
    }
    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(DATE,endingDate.getText().toString());
        editor.putString(SCOREPLUS,textViewPlus.getText().toString());
        editor.putString(SCOREMINUS,textViewMinus.getText().toString());

        editor.apply();
    }
    public void applyDataToView(){
        endingDate.setText(date);
        textViewPlus.setText(prefs_scorePlus);
        textViewMinus.setText(prefs_scoreMinus);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        listViewAdapter.clear();
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            try {
                long id = data.getLongExtra(DetailviewActivity.ARG_ITEM_ID, 1);
                item = crudOperations.readItem(id);

                if(requestCode == CALL_CREATE_ITEM){
                    Toast.makeText(this, "Item received", Toast.LENGTH_LONG).show();
                    addItemToList(item);
                }
                if (requestCode == CALL_EDIT_ITEM){
                    //updateList(item);
                }
                else {
                    Log.i("onActivityResult","NO new item received");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } // if resultCode
        updateList(item);

    }   // onActivityResult


    protected void addItemToList(Todo item){
        this.listViewAdapter.add(item);
        ((ListView)this.listView).setSelection(this.listViewAdapter.getPosition(item));
    }
    protected void updateList(Todo item){
        listViewAdapter.addAll(crudOperations.readAllItems());
        ((ListView)this.listView).setSelection(this.listViewAdapter.getPosition(item));
    }


    private void setEndingTime() {

            Calendar calender = Calendar.getInstance();
            int year = calender.get(Calendar.YEAR);
            int month = calender.get(Calendar.MONTH);
            int day = calender.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(OverviewActivity.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    endingDateSetListener,
                    year, month, day);
            // Hintergrund transparent machen
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
    }

    private void setNotificationTime(){

        Calendar calender = Calendar.getInstance();
        int hour = calender.get(Calendar.HOUR_OF_DAY);
        int minute = calender.get(Calendar.MINUTE);
   
        TimePickerDialog timePickerDialog = new TimePickerDialog(OverviewActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                alertTimeListener,
                hour,minute,true);

        // Hintergrund transparent machen
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.show();

        this.timepicker.setCurrentHour(hour);
        this.timepicker.setCurrentMinute(minute);
        this.timepicker.setIs24HourView(true);
    }
    private void createNotificationIntent(TimePicker timePicker){

        long time = 0;
        long hour = timePicker.getCurrentHour()*60*60*1000;
        long min = timePicker.getCurrentMinute()*60*1000;

        time = hour + min;
        long timeInMillis = Calendar.getInstance().getTimeInMillis();

        long current_hour = (Calendar.getInstance().get(Calendar.HOUR)+12)*60*60*1000;
        long current_min = (Calendar.getInstance().get(Calendar.MINUTE)-1)*60*1000;
        long current_sec = Calendar.getInstance().get(Calendar.SECOND)*1000;
        long passed_millis = current_hour + current_min + current_sec;

        long alertTime = timeInMillis - passed_millis + time;


        long dif = alertTime - timeInMillis;
        Log.i("notTime",String.valueOf(alertTime));
        Log.i("notTime",String.valueOf(timeInMillis));

        //Log.i("currentTime", String.valueOf(System.currentTimeMillis()));
        Log.i("dif",String.valueOf(dif));

        Intent notificationIntent = new Intent(getApplicationContext(),NotificationReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(getApplicationContext(),CALL_NOTIFICATION_ALERT_TIME, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alertTime, AlarmManager.INTERVAL_DAY, pIntent);
    }


    protected void summUpCheckBoxes(List<Todo> todoList){

        int doneCounter = Integer.parseInt(prefs_scorePlus);
        int undoneCounter = Integer.parseInt(prefs_scoreMinus);

        int todoListSize = todoList.size();
        int done = 0;

        for (int i = 0;  i < todoListSize; i++){
            item = todoList.get(i);
            Log.i("sumUpdone", String.valueOf(item.isDone()));
            if(item.isDone()){
                done++;
            }
        }

        if (done == todoListSize){
            Log.i("summUpCheckBoxes", "all Todos done");

            Toast.makeText(this,"All Todos done for today",Toast.LENGTH_SHORT).show();
            doneCounter = ++doneCounter;
            textViewPlus.setText(String.valueOf(doneCounter));
            saveData();
            resetCheckBoxes();
        } else {
            undoneCounter = ++undoneCounter;
            textViewMinus.setText(String.valueOf(undoneCounter));
            int undone = todoListSize - done;
            Log.i("summUpCheckBoxes", undone +  " Todos still undone");
        }

    }
    private void resetCheckBoxes() {
        int size = todoList.size();
        Todo item = null;

        for (int i = 0;  i < size; i++){
            item = todoList.get(i);
            long id =  item.getId();
            item.setDone(false);
            crudOperations.updateItem(id, item);
        }
    }




    private void showDetailViewForCreate() {
        Intent createIntent = new Intent(this,DetailviewActivity.class);
        startActivityForResult(createIntent,CALL_CREATE_ITEM);
    }
    private void showDetailViewForEdit(Todo item) {
        Intent editIntent = new Intent(this,DetailviewActivity.class);
        editIntent.putExtra(DetailviewActivity.ARG_ITEM_ID,item.getId());
        startActivityForResult(editIntent,CALL_EDIT_ITEM);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
            clearScore();
            return true;
        }
        if (id == R.id.clearTarget) {
            clearTargetDate();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void clearScore() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(DATE,endingDate.getText().toString());
        editor.putString(SCOREPLUS,"0");
        editor.putString(SCOREMINUS,"0");
        prefs_scorePlus = "0";
        prefs_scoreMinus = "0";

        editor.apply();
        applyDataToView();
    }
    public void clearTargetDate(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        date = null;
        editor.putString(DATE,date);
        editor.putString(SCOREPLUS,textViewPlus.getText().toString());
        editor.putString(SCOREMINUS,textViewMinus.getText().toString());

        editor.apply();
        applyDataToView();
    }



    private class ListItemViewHolder{

        public Todo listItem;

        private TextView todoId;
        private TextView todoName;
        private CheckBox checkBox;

        public ListItemViewHolder(View itemView) {

            this.todoId = itemView.findViewById(R.id.listitemId);
            this.todoName = itemView.findViewById(R.id.listitemName);
            this.checkBox = itemView.findViewById(R.id.listitemCheckBox);

            this.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                Todo mockItem = null;

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean b) {    // boolean b gibt null pointer exception, warum? -> boolean selbst abfragen
                    int position = (int) buttonView.getTag();
                    boolean checked = checkBox.isChecked();
                    Log.i("Checkbox listener", String.valueOf(checked));
                    Log.i("Checkbox position", String.valueOf(position));

                    mockItem = listViewAdapter.getItem(position);
                    mockItem.setDone(checked);
                    crudOperations.updateItem(mockItem.getId(),mockItem);
                    summUpCheckBoxes(todoList);
                }
            });
        }

        public void unbind() {
            this.listItem = null;
        }

        public Todo bind(Todo todoItem){
            this.todoId.setText(String.valueOf(todoItem.getId()));
            this.todoName.setText(todoItem.getName());
            this.checkBox.setChecked(todoItem.isDone());
            return todoItem;
        }
    }



}
