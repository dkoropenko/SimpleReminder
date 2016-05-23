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

    public static final String LABELS_TABLE_NAME = "labels";
    public static final String TASKS_TABLE_NAME = "tasks";

    public static final String NAME  = "name";
    public static final String DESC = "desc";

    public static final String LABEL_NORMAL_COUNT = "normal_count";
    public static final String LABEL_IMPORTANT_COUNT = "important_count";
    public static final String LABEL_COMLETE_COUNT = "complete_count";

    public static final String TASK_STATUS = "status";
    public static final String TASK_START_DATE = "start_date";
    public static final String TASK_FINISH_DATE = "finish_date";
    public static final String TASK_COMPLETE_DATE = "comlete_date";
    public static final String TASK_PARENT_LABEL_HASH = "parent_hash";

    public static final String HASH = "hash";

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
        db.execSQL("CREATE TABLE "+ LABELS_TABLE_NAME +" (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NAME +" TEXT, " +
                DESC +" TEXT, " +
                HASH +" INTEGER, " +
                LABEL_NORMAL_COUNT +" INTEGER, " +
                LABEL_IMPORTANT_COUNT +" INTEGER, " +
                LABEL_COMLETE_COUNT +" INTEGER);");

        db.execSQL("CREATE TABLE "+ TASKS_TABLE_NAME +" (_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                NAME +" TEXT, "+
                DESC +" TEXT, "+
                TASK_STATUS +" INTEGER, "+
                TASK_PARENT_LABEL_HASH +" INTEGER, "+
                HASH +" INTEGER, "+
                TASK_START_DATE +" TEXT, "+
                TASK_FINISH_DATE +" TEXT, "+
                TASK_COMPLETE_DATE +" TEXT);");


    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Пока нечего обновлять
    }

    public boolean checkHash(int hashSumm, String tableName){
        cursor = database.query(tableName, new String[]{HASH}, HASH +" = ?", new String[]{String.valueOf(hashSumm)}, null, null, null);
        return cursor.moveToFirst();
    }

    //Работа с таблицей LABELS
    public void addLabel(String name, String description){
        ContentValues values = new ContentValues();

        values.put(NAME, name);
        values.put(DESC, description);
        values.put(HASH, values.hashCode());
        values.put(LABEL_NORMAL_COUNT, 0);
        values.put(LABEL_IMPORTANT_COUNT, 0);
        values.put(LABEL_COMLETE_COUNT, 0);

        database.insert(LABELS_TABLE_NAME, null, values);
    }
    public void deleteLabel(String name){
        database.delete(LABELS_TABLE_NAME, NAME +" = ?", new String[]{name});
    }
    public void changeLabel (int hashSumm, String newName, String newDescription){
        ContentValues values = new ContentValues();
        values.put(NAME, newName);
        if (newDescription != null) values.put(DESC, newDescription);
        values.put(HASH, values.hashCode());

        database.update(LABELS_TABLE_NAME, values, HASH +" = ?", new String[]{String.valueOf(hashSumm)});
    }
    public void changeLabelCount(int hashSumm, String count, boolean action){
        cursor = database.query(LABELS_TABLE_NAME, new String[]{count},
                HASH +" = ?",
                new String[]{String.valueOf(hashSumm)},null,null,null);

        ContentValues result = new ContentValues();

        int value = 0;
        if(cursor.moveToFirst())
            value = cursor.getInt(0);

        if (action){
            value++;
            result.put(count, value);
        } else{
            value--;
            result.put(count, value);
        }

        database.update(LABELS_TABLE_NAME, result, HASH +" = ?", new String[]{String.valueOf(hashSumm)});
    }
    public List<Map<String,Object>> getLabelElements(){
        List<Map<String,Object>> result = new ArrayList<>();
        Map<String, Object> items;

        cursor = database.query(LABELS_TABLE_NAME, new String[]{NAME, DESC,
                HASH, LABEL_NORMAL_COUNT, LABEL_IMPORTANT_COUNT, LABEL_COMLETE_COUNT},null,null,null,null,null);

        if (cursor.moveToFirst()){
            items = new HashMap<>();
            items.put(NAME, cursor.getString(0));
            items.put(DESC, cursor.getString(1));
            items.put(HASH, cursor.getInt(2));
            items.put(LABEL_NORMAL_COUNT, cursor.getInt(3));
            items.put(LABEL_IMPORTANT_COUNT, cursor.getInt(4));
            items.put(LABEL_COMLETE_COUNT, cursor.getInt(5));
            result.add(items);

            while (cursor.moveToNext()){
                items = new HashMap<>();
                items.put(NAME, cursor.getString(0));
                items.put(DESC, cursor.getString(1));
                items.put(HASH, cursor.getInt(2));
                items.put(LABEL_NORMAL_COUNT, cursor.getInt(3));
                items.put(LABEL_IMPORTANT_COUNT, cursor.getInt(4));
                items.put(LABEL_COMLETE_COUNT, cursor.getInt(5));
                result.add(items);
            }
        }
        return result;
    }

    //Работа с таблицей TASKS
    public void addTask(String name, String description, int status, long finishDate, int parentHashSumm){
        ContentValues values = new ContentValues();
        values.put(NAME, name);
        values.put(DESC, description);
        values.put(TASK_STATUS, status);
        values.put(TASK_PARENT_LABEL_HASH, parentHashSumm);
        values.put(HASH, values.hashCode());
        values.put(TASK_START_DATE, Calendar.getInstance().getTimeInMillis());
        values.put(TASK_FINISH_DATE, finishDate);
        values.put(TASK_COMPLETE_DATE, 0);

        database.insert(TASKS_TABLE_NAME, null, values);
    }
    public void addTask(ContentValues values){
        database.insert(TASKS_TABLE_NAME, null, values);
    }
    public void deleteTask (int hashSumm){
        database.delete(TASKS_TABLE_NAME, HASH +" = ?", new String[]{String.valueOf(hashSumm)});
    }
    public void changeTask (int hashSumm, String newName, String newDescription, int newStatus, int newParentHash){
        ContentValues values = new ContentValues();
        values.put(NAME, newName);
        if (newDescription != null) values.put(DESC, newDescription);
        values.put(TASK_STATUS, newStatus);
        values.put(TASK_PARENT_LABEL_HASH, newParentHash);
        values.put(HASH, values.hashCode());

        database.update(TASKS_TABLE_NAME, values, HASH +" = ?", new String[]{String.valueOf(hashSumm)});
    }
    public List<Map<String, Object>> getTasksElements(String selections, String[] args){
        List<Map<String,Object>> result = new ArrayList<>();
        Map<String, Object> items;

        cursor = database.query(TASKS_TABLE_NAME,
                new String[]{NAME,
                        DESC,
                        TASK_STATUS,
                        TASK_PARENT_LABEL_HASH,
                        HASH,
                        TASK_START_DATE,
                        TASK_FINISH_DATE,
                        TASK_COMPLETE_DATE},
                selections, args, null, null, null);

        if (cursor.moveToFirst()){
            items = new HashMap<>();
            items.put(NAME, cursor.getString(0));
            items.put(DESC, cursor.getString(1));
            items.put(TASK_STATUS, cursor.getInt(2));
            items.put(TASK_PARENT_LABEL_HASH, cursor.getInt(3));
            items.put(HASH, cursor.getInt(4));
            items.put(TASK_START_DATE, cursor.getLong(5));
            items.put(TASK_FINISH_DATE, cursor.getLong(6));
            items.put(TASK_COMPLETE_DATE, cursor.getLong(7));

            result.add(items);

            while (cursor.moveToNext()){
                items = new HashMap<>();
                items.put(NAME, cursor.getString(0));
                items.put(DESC, cursor.getString(1));
                items.put(TASK_STATUS, cursor.getInt(2));
                items.put(TASK_PARENT_LABEL_HASH, cursor.getInt(3));
                items.put(HASH, cursor.getInt(4));
                items.put(TASK_START_DATE, cursor.getLong(5));
                items.put(TASK_FINISH_DATE, cursor.getLong(6));
                items.put(TASK_COMPLETE_DATE, cursor.getLong(7));
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
