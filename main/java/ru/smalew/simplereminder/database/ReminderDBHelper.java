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

    public static final String LABEL_NAME_IDENT = "lableName";
    public static final String LABEL_DESC_IDENT = "lableDesc";
    public static final String LABEL_COUNT_USUAL_IDENT = "lableUsualCount";
    public static final String LABEL_COUNT_IMPORT_IDENT = "lableImportantCount";
    public static final String LABEL_COUNT_COMLETE_IDENT = "lableCompleteCount";

    public static final String TASK_NAME_IDENT = "taskName";
    public static final String TASK_DESC_IDENT = "taskDesc";
    public static final String TASK_STATUS_IDENT = "status";
    public static final String TASK_STARTDATE_IDENT = "startDate";
    public static final String TASK_COMPLETEDATE_IDENT = "comleteDate";
    public static final String TASK_PARENT_LABEL_IDENT = "parentLabel";

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
                "label_name TEXT, " +
                "label_desc TEXT, " +
                "usual_count INTEGER, " +
                "import_count INTEGER, " +
                "complete_count INTEGER);");

        db.execSQL("CREATE TABLE tasks (_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "task_name TEXT, "+
                "task_desc TEXT, "+
                "status TEXT, "+
                "start_date INTEGER, "+
                "complete_date INTEGER, "+
                "label TEXT);");

        //Заполняем таблицу первоначальными данными тестовыми данными.
        ContentValues values = new ContentValues();
        values.put("label_name", "Main");
        values.put("label_desc", "Main Label");
        values.put("usual_count", 5);
        values.put("import_count", 1);
        values.put("complete_count", 3);
        db.insert("labels", null, values);

        values = new ContentValues();
        values.put("label_name", "Main Test");
        values.put("label_desc", "Main Test Label");
        values.put("usual_count", 1);
        values.put("import_count", 2);
        values.put("complete_count", 3);
        db.insert("labels", null, values);

        long time = Calendar.getInstance().getTimeInMillis();

//        testInitData(db, "Вынести мусор", "За угол дома", 2, time, "");
//        testInitData(db, "Купить хлеб", "Белый, черный", 1, time, "Магазин");
//        testInitData(db, "Купить селедку", "13,50 за кг", 0, time, "Магази");
//        testInitData(db, "Справить ДР", "дома", 1, time, "Праздник");

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Пока нечего обновлять
    }

    public void testInitData(SQLiteDatabase db, String taskName, String taskDesc, int imp, long date, String label){
        ContentValues values = new ContentValues();
        values.put("task_name", taskName);
        values.put("task_desc", taskDesc);
        values.put("status", imp);
        values.put("start_date", date);
        values.put("complete_date", 0);
        values.put("label", label);
        db.insert("tasks", null, values);
    }

    //Работа с таблицей LABELS
    public void addLabel(String labelName, String labelDesc){
        ContentValues values = new ContentValues();
        values.put("label_name", labelName);
        values.put("label_desc", labelDesc);
        values.put("count", 0);

        database.insert("labels", null, values);
    }
    public void deleteLabel(String labelName){
        database.delete("label", "label_name = ?", new String[]{labelName});
    }
    public void changeLabel (String labelName, String labelDesc, String oldLabelName){
        ContentValues values = new ContentValues();
        values.put("label_name", labelName);

        if (labelDesc != null)
            values.put("label_desc", labelDesc);

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
    public boolean checkLabelName(SQLiteDatabase db, String labelName){
        cursor = db.query("labels", new String[]{"label_name"}, "label_name = ?", new String[]{labelName}, null,null,null);
        return cursor.moveToFirst();
    }
    public List<Map<String,Object>> getLabelElements(){
        List<Map<String,Object>> result = new ArrayList<>();
        Map<String, Object> items;

        cursor = database.query("labels", new String[]{"label_name", "label_desc", "usual_count", "import_count", "complete_count"},null,null,null,null,null);

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
    public void addTask(String task, String desc, int status, int startDate, String parentLabel){
        ContentValues values = new ContentValues();
        values.put("task_name", task);
        values.put("tasc_desc", desc);
        values.put("status", status);
        values.put("start_date", startDate);
        values.put("complete_date", 0);
        values.put("label", parentLabel);
        database.insert("tasks", null, values);
    }
    public void deleteTask (String taskName){
        database.delete("tasks", "task_name = ?", new String[]{taskName});
    }
    public void changeTask (String taskName, String desc, int status, String parentLabel){
        ContentValues values = new ContentValues();
        values.put("task_name", taskName);
        if (desc != null)
            values.put("tasc_desc", desc);
        values.put("status", status);
        values.put("label", parentLabel);

        database.update("tasks", values,"task_name = ?", new String[]{taskName});
    }
    public List<Map<String, Object>> getTasksElements(String selections, String[] args){
        
        List<Map<String,Object>> result = new ArrayList<>();
        Map<String, Object> items;

        cursor = database.query("tasks",
                new String[]{"task_name", "task_desc", "status", "start_date", "complete_date", "label"},
                selections, args, null, null, null);

        if (cursor.moveToFirst()){
            items = new HashMap<>();
            items.put(TASK_NAME_IDENT, cursor.getString(0));
            items.put(TASK_DESC_IDENT, cursor.getString(1));
            items.put(TASK_STATUS_IDENT, cursor.getString(2));
            items.put(TASK_STARTDATE_IDENT, cursor.getString(3));
            items.put(TASK_COMPLETEDATE_IDENT, cursor.getString(4));
            items.put(TASK_PARENT_LABEL_IDENT, cursor.getString(5));
            result.add(items);

            while (cursor.moveToNext()){
                items = new HashMap<>();
                items.put(TASK_NAME_IDENT, cursor.getString(0));
                items.put(TASK_DESC_IDENT, cursor.getString(1));
                items.put(TASK_STATUS_IDENT, cursor.getString(2));
                items.put(TASK_STARTDATE_IDENT, cursor.getString(3));
                items.put(TASK_COMPLETEDATE_IDENT, cursor.getString(4));
                items.put(TASK_PARENT_LABEL_IDENT, cursor.getString(5));
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
