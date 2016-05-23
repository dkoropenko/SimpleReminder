package ru.smalew.simplereminder;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ru.smalew.simplereminder.database.ReminderDBHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private Fragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        if (savedInstanceState == null){
//            ReminderDBHelper reminderDBHelper = new ReminderDBHelper(this);
//            List elements = reminderDBHelper.getTasksElements(null, null);
//            reminderDBHelper.closeConnection();
//
//            if (elements.isEmpty()) createNewFragment(R.id.create_task_toolbar_btn);
//            else createNewFragment(R.id.nav_all_tasks);
//        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.complete_task_toolbar_btn){}
        if (id == R.id.create_task_toolbar_btn){
            createNewFragment(R.id.create_task_toolbar_btn);
        }
        if (id == R.id.delete_task_toolbar_btn){}
        if (id == R.id.create_label_toolbar_btn){
            createNewFragment(R.id.create_label_toolbar_btn);
        }
        if (id == R.id.delete_label_toolbar_btn){}


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        createNewFragment(item.getItemId());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void createNewFragment(int id){
        Bundle arguments = new Bundle();

        if (id == R.id.nav_all_tasks) {
            toolbar.setTitle(getResources().getString(R.string.toolbar_all_tasks));
            fragment = new TaskElementsList();
            arguments.putString(TaskElementsList.STATUS_SELECTIONS, "");
            arguments.putString(TaskElementsList.STATUS_STATE, "");
            fragment.setArguments(arguments);
        }
        if (id == R.id.nav_important){
            toolbar.setTitle(getResources().getString(R.string.toolbar_important_tasks));
            fragment = new TaskElementsList();
            arguments.putString(TaskElementsList.STATUS_SELECTIONS, "status = ?");
            arguments.putString(TaskElementsList.STATUS_STATE, "1");
            fragment.setArguments(arguments);
        }
        if (id == R.id.nav_complete_tasks){
            toolbar.setTitle(getResources().getString(R.string.toolbar_complete_tasks));
            fragment = new TaskElementsList();
            arguments.putString(TaskElementsList.STATUS_SELECTIONS, "status = ?");
            arguments.putString(TaskElementsList.STATUS_STATE, "2");
            fragment.setArguments(arguments);
        }
        if (id == R.id.nav_parent_labels){
            toolbar.setTitle(getResources().getString(R.string.toolbar_labels));
            fragment = new LabelElementsList();
        }
        if (id == R.id.create_task_toolbar_btn){
            toolbar.setTitle(getResources().getString(R.string.toolbar_create_task));
            fragment = new CreateTaskFragment();
        }
        if (id == R.id.create_label_toolbar_btn){
            toolbar.setTitle(getResources().getString(R.string.toolbar_create_label));
            fragment = new CreateLabelFragment();
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_content, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    //Выбор даты для создания новой задачи.
    public void chooseLimitDate(View view){
        DialogFragment dateFragment = new CreateTaskFragment.DatePickerFragment();
        dateFragment.show(getSupportFragmentManager(), "dateFragment");
    }

    //Создание новой задачи \ Запись в БД
    public void createTask (View view){

        //Забираем введенные данные для анализа
        String taskName = ((TextView)findViewById(R.id.name_text_task)).getText().toString();
        String taskDesc = ((TextView)findViewById(R.id.desc_text_task)).getText().toString();
        int taskStatus = ((Spinner) findViewById(R.id.import_choose)).getSelectedItemPosition();


        ReminderDBHelper reminderDBHelper = new ReminderDBHelper(this);
        int labelSpinerSelected = ((Spinner) findViewById(R.id.parent_label_choose)).getSelectedItemPosition();
        int parentLabelHash = 0;

        if (labelSpinerSelected != 0){
            List<Map<String, Object>> labelsElements = reminderDBHelper.getLabelElements();
            parentLabelHash = (int)labelsElements.get(labelSpinerSelected-1).get(ReminderDBHelper.HASH);
            reminderDBHelper.closeConnection();
        }

        ContentValues result = new ContentValues();
        result.put(ReminderDBHelper.NAME, taskName);
        result.put(ReminderDBHelper.DESC, taskDesc);
        result.put(ReminderDBHelper.TASK_STATUS, taskStatus);
        result.put(ReminderDBHelper.TASK_PARENT_LABEL_HASH, parentLabelHash);

        if (!reminderDBHelper.checkHash(result.hashCode(), ReminderDBHelper.LABELS_TABLE_NAME)){

            result.put(ReminderDBHelper.HASH, result.hashCode());
            result.put(ReminderDBHelper.TASK_START_DATE, Calendar.getInstance().getTimeInMillis());
            result.put(ReminderDBHelper.TASK_FINISH_DATE, CreateTaskFragment.finishDate.getTimeInMillis());
            result.put(ReminderDBHelper.TASK_COMPLETE_DATE, 0);

            reminderDBHelper.addTask(result);
            reminderDBHelper.closeConnection();

            createNewFragment(R.id.nav_all_tasks);
        }
        else{
            reminderDBHelper.closeConnection();
            Snackbar.make(view, R.string.task_error, Snackbar.LENGTH_LONG).show();
        }
        reminderDBHelper.closeConnection();
    }

    //Метод создания нового ярлыка \ запись в БД
//    public void createLabel(View view){
//        TextView labelNameView = (TextView)findViewById(R.id.name_text_label);
//        String labelName = labelNameView.getText().toString();
//
//        ReminderDBHelper reminderDBHelper = new ReminderDBHelper(this);
//
//        if (!reminderDBHelper.checkLabelName(labelName)){
//            String labelDesc = ((TextView)findViewById(R.id.desc_text_label)).getText().toString();
//
//            reminderDBHelper.addLabel(labelName, labelDesc);
//            createNewFragment(R.id.nav_parent_labels);
//        }
//        else{
//            labelNameView.setBackgroundResource(R.color.error);
//            labelNameView.requestFocus();
//            Snackbar.make(view, R.string.label_error, Snackbar.LENGTH_LONG).show();
//        }
//        reminderDBHelper.closeConnection();
//    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
}
