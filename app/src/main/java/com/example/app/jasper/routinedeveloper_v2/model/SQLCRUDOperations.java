package com.example.app.jasper.routinedeveloper_v2.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SQLCRUDOperations {


    /////////////////  SQL Data Base  //////////////////////
    private SQLiteDatabase db;
    private static final String TABLE_DATAITEM = "DATAITEMS";
    private static final String ID = "ID";
    private static final String NAME = "NAME";
    private static final String DONE = "DONE";

    private static final String [] ALL_COLUMS = new String[]{ID, NAME, DONE};

    private static final String CREATION_QUERY =
            "CREATE TABLE DATAITEMS " +
                    "(ID INTEGER PRIMARY KEY, " +
                    "NAME TEXT, " +
                    "DONE BOOLEAN)";

    // Im Konstrukter wird die SQLiteDAtenbank entweder geöffnet oder erstellt, wenn sie noch nicht exitiert
    public SQLCRUDOperations(Context applicationContext){

        //Mode_PRIVATE: Ist eine vor-implementierte Konstante von Klasse Context
        this.db = applicationContext.openOrCreateDatabase("myToDoDB.sqlite", Context.MODE_PRIVATE, null);
        if (db.getVersion() == 0){
            db.setVersion(1);
            db.execSQL(CREATION_QUERY);
        }
    }

    private static SQLCRUDOperations instance;

    public static SQLCRUDOperations getInstance(Context applicationContext){
        if(instance==null) {
            instance=new SQLCRUDOperations(applicationContext);
        }
        return instance;
    }

    //////////// CRUD Operations ///////////
    public long createItem(Todo item) {             // item kommt vom databinding, hat also schon namen und done, aber keine id

        ContentValues values = new ContentValues();
        values.put(NAME, item.getName());
        values.put(DONE, item.isDone());
        // Beim Schreiben einer Zeile in einer Datenbank durch die insert
        // Methode wird ein long wert automatisch zurückgegeben    --- primary key
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
        values.put(NAME, item.getName());
        values.put(DONE, item.isDone());

        db.update(TABLE_DATAITEM,values,"ID=?",new String[]{String.valueOf(id)});   // wird benötigt, da sonst mehrere items mit gleicher Id erzeugt werden
        item.setId(id);
        values.put(ID,id);
        Log.i("SQL/updateItem mockId", String.valueOf(id));

        return true;
    }

    public void deleteItem(long id){
        db.delete(TABLE_DATAITEM,ID + "=" + id,null);
    }


    ///////////   support methods   ////////////
    private Todo getItemValuesFromCursor(Cursor cursor) {
        Todo mockTodo = new Todo();
        long id = cursor.getLong(cursor.getColumnIndex("ID"));
        String name = cursor.getString(cursor.getColumnIndex("NAME"));
        boolean done = cursor.getLong(cursor.getColumnIndex("DONE"))>0;

        mockTodo.setId(id);
        mockTodo.setName(name);
        mockTodo.setDone(done);

        return mockTodo;
    }

    private Todo updateDataItemFromCursor(Todo item, Cursor cursor) {

        long id = cursor.getLong(cursor.getColumnIndex("ID"));
        String name = cursor.getString(cursor.getColumnIndex("NAME"));
        boolean done = cursor.getLong(cursor.getColumnIndex("DONE"))>0;

        item.setId(id);
        item.setName(name);
        item.setDone(done);
        return item;
    }
}


