package com.example.app.jasper.routinedeveloper_v2.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static com.example.app.jasper.routinedeveloper_v2.OverviewActivity.POSITION;

public class SQLDatabaseHelper {

    /////////////////  SQL Data Base  //////////////////////
    private SQLiteDatabase db;
    private static final String TABLE_DATAITEM = "DATAITEMS";
    private static final String ID = "ID";
    private static final String NAME = "NAME";
    private static final String IS_CHECKED = "IS_CHECKED";
    private static final String DONE_COUNTER = "DONE_COUNTER";
    private static final String UNDONE_COUNTER = "UNDONE_COUNTER";

    private static final String [] ALL_COLUMS = new String[]{ID, POSITION, NAME, IS_CHECKED, DONE_COUNTER, UNDONE_COUNTER};

    private static final String CREATION_QUERY =
            "CREATE TABLE DATAITEMS " +
                    "(ID INTEGER PRIMARY KEY, " +
                    "POSITION INTEGER,"+
                    "NAME TEXT, " +
                    "IS_CHECKED BOOLEAN," +
                    "DONE_COUNTER," +
                    "UNDONE_COUNTER)";

    private static SQLDatabaseHelper instance;

    public static SQLDatabaseHelper getInstance(Context applicationContext){
        if(instance==null) {
            instance=new SQLDatabaseHelper(applicationContext);
        }
        return instance;
    }

    // Im Konstrukter wird die SQLiteDAtenbank entweder geöffnet oder erstellt, wenn sie noch nicht exitiert
    public SQLDatabaseHelper(Context applicationContext){

        this.db = applicationContext.openOrCreateDatabase("test3.sqlite", Context.MODE_PRIVATE, null);
        if (db.getVersion() == 0){
            db.setVersion(1);
            db.execSQL(CREATION_QUERY);
        }
    }

    //////////// CRUD Operations ///////////
    public long createItem(Todo item) {

        ContentValues values = new ContentValues();
        values.put(POSITION, item.getPosition());
        values.put(NAME, item.getName());
        values.put(IS_CHECKED, item.isChecked());
        values.put(DONE_COUNTER, item.getDoneCounts());
        values.put(UNDONE_COUNTER, item.getUndoneCounts());
        long id = db.insert(TABLE_DATAITEM, null, values);
        item.setId(id);
        return id;
    }

    public Todo readItem(long id){

        Cursor cursor = db.query(TABLE_DATAITEM,ALL_COLUMS,"ID=?",new String[]{String.valueOf(id)},null,null,null);

        if (cursor.getCount()>0){
            cursor.moveToFirst();
        }
        return getItemValuesFromCursor(cursor);
    }


    public List<Todo> readAllItems(){
        List<Todo> items = new ArrayList<Todo>();

        Cursor cursor = db.query(TABLE_DATAITEM,ALL_COLUMS,null,null,null,null,null);
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            boolean next = false;
            do {
                items.add(getItemValuesFromCursor(cursor));
                next = cursor.moveToNext();
            }while (next);
        }
        else {
            Log.i("Cursor Items", "No items found");
        }
        return items;
    }

    public boolean updateItem(long id, Todo item){

        Log.i("SQL/updateItem id:", String.valueOf(id));

        // Daten vom dataBinding "abgreifen"
        ContentValues values = new ContentValues();
        values.put(POSITION, item.getPosition());
        values.put(NAME, item.getName());
        values.put(IS_CHECKED, item.isChecked());
        values.put(DONE_COUNTER, item.getDoneCounts());
        values.put(UNDONE_COUNTER, item.getUndoneCounts());

        db.update(TABLE_DATAITEM,values,"ID=?",new String[]{String.valueOf(id)});   // wird benötigt, da sonst mehrere items mit gleicher Id erzeugt werden
        item.setId(id);
        values.put(ID,id);

        return true;
    }



    public void deleteItem(long id){
        Log.d("DATABASE", "item deleted");
        db.delete(TABLE_DATAITEM,ID + "=" + id,null);
    }


    ///////////   support methods   ////////////
    private Todo getItemValuesFromCursor(Cursor cursor) {
        Todo mockTodo = new Todo();
        long id = cursor.getLong(cursor.getColumnIndex(ID));
        int position = cursor.getInt(cursor.getColumnIndex(POSITION));
        String name = cursor.getString(cursor.getColumnIndex(NAME));
        boolean isChecked = cursor.getLong(cursor.getColumnIndex(IS_CHECKED))>0;
        int doneCounts = cursor.getInt(cursor.getColumnIndex(DONE_COUNTER));
        int undoneCounts = cursor.getInt(cursor.getColumnIndex(UNDONE_COUNTER));

        mockTodo.setId(id);
        mockTodo.setPosition(position);
        mockTodo.setName(name);
        mockTodo.setChecked(isChecked);
        mockTodo.setDoneCounts(doneCounts);
        mockTodo.setUndoneCounts(undoneCounts);

        return mockTodo;
    }
}


