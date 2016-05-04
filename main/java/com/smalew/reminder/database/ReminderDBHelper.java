package com.smalew.reminder.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by koropenkods on 25.04.16.
 */
public class ReminderDBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "reminder";
    public static final int DB_VERSION = 1;

    public ReminderDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE labels (_id INTEGER PRIMARY KEY AUTOINCREMENT, label_name TEXT, label_desc TEXT, count INTEGER);");

        db.execSQL("CREATE TABLE tasks (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "task_name TEXT, " +
                "task_desc TEXT, " +
                "importance NUMERIC, " +
                "start_date INTEGER, " +
                "complete_date INTEGER, " +
                "label TEXT);");

        //Заполняем таблицу тестовыми данными.
        db.insert("labels", null, addDatas("Main", "Main Label", 3));
        db.insert("labels", null, addDatas("One", "One Label", 2));
        db.insert("labels", null, addDatas("Two", "Two Label", 0));
        db.insert("labels", null, addDatas("Three", "Three Label", 10));
        db.insert("labels", null, addDatas("Four", "Four Label", 25));
        db.insert("labels", null, addDatas("Five", "Five Label", 0));
        db.insert("labels", null, addDatas("Six", "Six Label", 9));
        db.insert("labels", null, addDatas("Seven", "Seven Label", 6));
        db.insert("labels", null, addDatas("Eight", "Eight Label", 7));
        db.insert("labels", null, addDatas("Ten", "Ten Label", 0));
    }

    private ContentValues addDatas(String name, String desc, int count){
        ContentValues values = new ContentValues();
        values.put("label_name", name);
        values.put("label_desc", desc);
        values.put("count", count);

        return values;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Пока нечего обновлять
    }

    public void closeConnection(Cursor cursor, SQLiteDatabase database){
        if(!cursor.isClosed())
            cursor.close();
        if(database.isOpen())
            database.close();
    }
}
