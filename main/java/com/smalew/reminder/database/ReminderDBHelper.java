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
        addLabel(db, "Main", "Main label");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Пока нечего обновлять
    }

    public void addLabel(SQLiteDatabase db, String labelName, String labelDesc){
        ContentValues values = new ContentValues();
        values.put("label_name", labelName);
        values.put("label_desc", labelDesc);
        values.put("count", 0);

        db.insert("labels", null, values);
    }
    public void deleteLabel(SQLiteDatabase db, String labelName){
        db.delete("label", "label_name = ?", new String[]{labelName});
    }
    public void changeLabel (SQLiteDatabase db, String labelName, String labelDesc, String oldLabelName){
        ContentValues values = new ContentValues();
        values.put("label_name", labelName);

        if (labelDesc != null)
            values.put("label_desc", labelDesc);

        db.update("labels", values,"label_name = ?", new String[]{oldLabelName});
    }
    public void changeLabelCount(SQLiteDatabase db, String labelName, boolean action){
        Cursor cursor = db.query("labels", new String[]{"count"},
                "label_name = ?",
                new String[]{labelName},null,null,null);

        int count = 0;
        if(cursor.moveToFirst()){
            count = cursor.getInt(0);
        }

        ContentValues values = new ContentValues();

        if (action){
            count++;
            values.put("count", count);
        } else{
            count--;
            values.put("count", count);
        }

        db.update("labels", values,"label_name = ?", new String[]{labelName});
    }
    public boolean checkLabelName(SQLiteDatabase db, String labelName){
        boolean result = false;

        Cursor cursor = db.query("labels", new String[]{"label_name"}, "label_name = ?", new String[]{labelName}, null,null,null);
        result = cursor.moveToFirst();

        cursor.close();

        return result;
    }

    public void closeConnection(Cursor cursor, SQLiteDatabase database){
        if(cursor != null &&!cursor.isClosed())
            cursor.close();
        if(database != null && database.isOpen())
            database.close();
    }
}
