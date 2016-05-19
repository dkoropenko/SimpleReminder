package ru.smalew.simplereminder.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.SystemClock;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by koropenkods on 25.04.16.
 */
public class ReminderDBHelper extends SQLiteOpenHelper {

    public static final String LABEL_NAME_IDENT = "lable_name";
    public static final String LABEL_DESC_IDENT = "lable_desc";
    public static final String LABEL_COUNT_USUAL_IDENT = "lable_usual_count";
    public static final String LABEL_COUNT_IMPORT_IDENT = "lable_important_count";
    public static final String LABEL_COUNT_COMLETE_IDENT = "lable_complete_count";

    public static final String TASK_NAME_IDENT = "task_name";
    public static final String TASK_DESC_IDENT = "task_desc";
    public static final String TASK_STATUS_IDENT = "status";
    public static final String TASK_START_DATE_IDENT = "start_date";
    public static final String TASK_LIMIT_DATE_IDENT = "limit_date";
    public static final String TASK_COMPLETE_DATE_IDENT = "comlete_date";
    public static final String TASK_PARENT_LABEL_IDENT = "parent_label";

    public static final String DB_NAME = "reminder";
    public static final int DB_VERSION = 1;

    private SQLiteDatabase database;
    private Cursor cursor;

    public ReminderDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        createConnection();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE labels (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LABEL_NAME_IDENT +" TEXT, " +
                LABEL_DESC_IDENT +" TEXT, " +
                LABEL_COUNT_USUAL_IDENT +" INTEGER, " +
                LABEL_COUNT_IMPORT_IDENT +" INTEGER, " +
                LABEL_COUNT_COMLETE_IDENT +" INTEGER);");

        db.execSQL("CREATE TABLE tasks (_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                TASK_NAME_IDENT +" TEXT, "+
                TASK_DESC_IDENT +" TEXT, "+
                TASK_STATUS_IDENT +" TEXT, "+
                TASK_START_DATE_IDENT +" TEXT, "+
                TASK_LIMIT_DATE_IDENT +" TEXT, "+
                TASK_COMPLETE_DATE_IDENT +" TEXT, "+
                TASK_PARENT_LABEL_IDENT +" TEXT);");


    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Пока нечего обновлять
    }

    //Работа с таблицей LABELS
    public void addLabel(String labelName, String labelDesc){
        ContentValues values = new ContentValues();
        values.put(LABEL_NAME_IDENT, labelName);
        values.put(LABEL_DESC_IDENT, labelDesc);
        values.put(LABEL_COUNT_USUAL_IDENT, 0);
        values.put(LABEL_COUNT_IMPORT_IDENT, 0);
        values.put(LABEL_COUNT_COMLETE_IDENT, 0);

        database.insert("labels", null, values);
    }
    public void deleteLabel(String labelName){
        database.delete("label", "label_name = ?", new String[]{labelName});
    }
    public void changeLabel (String labelName, String labelDesc, String oldLabelName){
        ContentValues values = new ContentValues();
        values.put(LABEL_NAME_IDENT, labelName);

        if (labelDesc != null)
            values.put(LABEL_DESC_IDENT, labelDesc);

        database.update("labels", values,"label_name = ?", new String[]{oldLabelName});
    }
    public void changeLabelCount(String labelName, String countName, boolean action){
        cursor = database.query("labels", new String[]{countName},
                "label_name = ?",
                new String[]{labelName},null,null,null);

        int count = 0;
        if(cursor.moveToFirst())
            count = cursor.getInt(0);

        ContentValues values = new ContentValues();

        if (action){
            count++;
            values.put(countName, count);
        } else{
            count--;
            values.put(countName, count);
        }

        database.update("labels", values,"label_name = ?", new String[]{labelName});
    }
    public boolean checkLabelName(String labelName){
        cursor = database.query("labels", new String[]{LABEL_NAME_IDENT}, LABEL_NAME_IDENT +" = ?", new String[]{labelName}, null,null,null);
        return cursor.moveToFirst();
    }
    public List<Map<String,Object>> getLabelElements(){
        List<Map<String,Object>> result = new ArrayList<>();
        Map<String, Object> items;

        cursor = database.query("labels", new String[]{LABEL_NAME_IDENT, LABEL_DESC_IDENT,
                LABEL_COUNT_USUAL_IDENT, LABEL_COUNT_IMPORT_IDENT, LABEL_COUNT_COMLETE_IDENT},null,null,null,null,null);

        if (cursor.moveToFirst()){
            items = new HashMap<>();
            items.put(LABEL_NAME_IDENT, cursor.getString(0));
            items.put(LABEL_DESC_IDENT, cursor.getString(1));
            items.put(LABEL_COUNT_USUAL_IDENT, cursor.getInt(2));
            items.put(LABEL_COUNT_IMPORT_IDENT, cursor.getInt(3));
            items.put(LABEL_COUNT_COMLETE_IDENT, cursor.getInt(4));
            result.add(items);

            while (cursor.moveToNext()){
                items = new HashMap<>();
                items.put(LABEL_NAME_IDENT, cursor.getString(0));
                items.put(LABEL_DESC_IDENT, cursor.getString(1));
                items.put(LABEL_COUNT_USUAL_IDENT, cursor.getInt(2));
                items.put(LABEL_COUNT_IMPORT_IDENT, cursor.getInt(3));
                items.put(LABEL_COUNT_COMLETE_IDENT, cursor.getInt(4));
                result.add(items);
            }
        }
        return result;
    }

    //Работа с таблицей TASKS
    public void addTask(String task, String desc, int status, long limitDate, String parentLabel){
        ContentValues values = new ContentValues();
        values.put(TASK_NAME_IDENT, task);
        values.put(TASK_DESC_IDENT, desc);
        values.put(TASK_STATUS_IDENT, status);
        values.put(TASK_START_DATE_IDENT, Calendar.getInstance().getTimeInMillis());
        values.put(TASK_LIMIT_DATE_IDENT, limitDate);
        values.put(TASK_COMPLETE_DATE_IDENT, 0);
        values.put(TASK_PARENT_LABEL_IDENT, parentLabel);
        database.insert("tasks", null, values);
    }
    public void deleteTask (String taskName){
        database.delete("tasks", "task_name = ?", new String[]{taskName});
    }
    public void changeTask (String taskName, String desc, int status, String parentLabel){
        ContentValues values = new ContentValues();
        values.put(TASK_NAME_IDENT, taskName);
        if (desc != null)
            values.put(TASK_DESC_IDENT, desc);
        values.put(TASK_STATUS_IDENT, status);
        values.put(TASK_PARENT_LABEL_IDENT, parentLabel);

        database.update("tasks", values,"task_name = ?", new String[]{taskName});
    }
    public boolean checkTaskName(String taskName){
        cursor = database.query("tasks", new String[]{TASK_NAME_IDENT}, TASK_NAME_IDENT +" = ?", new String[]{taskName}, null, null, null);
        return cursor.moveToFirst();
    }
    public List<Map<String, Object>> getTasksElements(String selections, String[] args){
        
        List<Map<String,Object>> result = new ArrayList<>();
        Map<String, Object> items;

        cursor = database.query("tasks",
                new String[]{TASK_NAME_IDENT, TASK_DESC_IDENT, TASK_STATUS_IDENT, TASK_START_DATE_IDENT,
                        TASK_LIMIT_DATE_IDENT, TASK_COMPLETE_DATE_IDENT, TASK_PARENT_LABEL_IDENT},
                selections, args, null, null, null);

        if (cursor.moveToFirst()){
            items = new HashMap<>();
            items.put(TASK_NAME_IDENT, cursor.getString(0));
            items.put(TASK_DESC_IDENT, cursor.getString(1));
            items.put(TASK_STATUS_IDENT, cursor.getString(2));
            items.put(TASK_START_DATE_IDENT, cursor.getString(3));
            items.put(TASK_LIMIT_DATE_IDENT, cursor.getString(4));
            items.put(TASK_COMPLETE_DATE_IDENT, cursor.getString(5));
            items.put(TASK_PARENT_LABEL_IDENT, cursor.getString(6));
            result.add(items);

            while (cursor.moveToNext()){
                items = new HashMap<>();
                items.put(TASK_NAME_IDENT, cursor.getString(0));
                items.put(TASK_DESC_IDENT, cursor.getString(1));
                items.put(TASK_STATUS_IDENT, cursor.getString(2));
                items.put(TASK_START_DATE_IDENT, cursor.getString(3));
                items.put(TASK_LIMIT_DATE_IDENT, cursor.getString(4));
                items.put(TASK_COMPLETE_DATE_IDENT, cursor.getString(5));
                items.put(TASK_PARENT_LABEL_IDENT, cursor.getString(6));
                result.add(items);
            }
        }
        return result;
    }

    public void createConnection(){
        database = this.getWritableDatabase();
    }
    public void closeConnection(){
        if(cursor != null &&!cursor.isClosed())
            cursor.close();
        if(database != null && database.isOpen())
            database.close();
    }
}
