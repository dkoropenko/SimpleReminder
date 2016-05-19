package ru.smalew.simplereminder;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import ru.smalew.simplereminder.database.ReminderDBHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;

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

        if (savedInstanceState == null){
            ReminderDBHelper reminderDBHelper = new ReminderDBHelper(this);
            List elements = reminderDBHelper.getTasksElements(null, null);
            reminderDBHelper.closeConnection();

            if (elements.isEmpty()) createNewFragment(R.id.create_task_toolbar_btn);
            else createNewFragment(R.id.nav_all_tasks);
        }

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
        if (id == R.id.create_label_toolbar_btn){}
        if (id == R.id.delete_label_toolbar_btn){}


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        createNewFragment(id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void createNewFragment(int id){
        Bundle arguments = new Bundle();
        //Для создания фрагментов и работы с ними
        Fragment fragment = null;
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

    //Создание новой задачи\ Запись в БД
    public void createTask (View view){
        TextView taskNameView = (TextView)findViewById(R.id.name_text_task);
        String taskName = taskNameView.getText().toString();

        ReminderDBHelper reminderDBHelper = new ReminderDBHelper(this);

        if (!reminderDBHelper.checkTaskName(taskName)){
            String descName = ((TextView)findViewById(R.id.desc_text_task)).getText().toString();
            long dateLimitTask = CreateTaskFragment.limitDateValue.getTimeInMillis();;
            int importTask;
            String parentLabelTask;

            Spinner importTaskView = (Spinner) findViewById(R.id.import_choose);
            Spinner parentLabelTaskView = (Spinner) findViewById(R.id.parent_label_choose);

            importTask = importTaskView.getSelectedItemPosition();
            parentLabelTask = ((TextView)parentLabelTaskView.getSelectedView()).getText().toString();

            reminderDBHelper.addTask(taskName, descName, importTask, dateLimitTask, parentLabelTask);
            createNewFragment(R.id.nav_all_tasks);
        }
        else{
            taskNameView.setBackgroundResource(R.color.error);
            Snackbar.make(view, "Sorry, but this task already exist. Please change name.", Snackbar.LENGTH_LONG).show();
        }
        reminderDBHelper.closeConnection();
    }
}
