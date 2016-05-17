package ru.smalew.simplereminder;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

import java.util.List;

import layout.CreateTaskFragment;
import ru.smalew.simplereminder.database.ReminderDBHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    FloatingActionButton createElement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        createElement = (FloatingActionButton) findViewById(R.id.fab);
        createElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null){
            Fragment mainFragment = null;
            ReminderDBHelper reminderDBHelper = new ReminderDBHelper(this);
            List elements = reminderDBHelper.getTasksElements(null, null);
            reminderDBHelper.closeConnection();

            if (elements.size() == 0){
                mainFragment = new CreateTaskFragment();
            }

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frame_content, mainFragment);
            ft.addToBackStack(null);
            ft.commit();
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

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        Bundle arguments = new Bundle();

        if (id == R.id.nav_all_tasks) {
            toolbar.setTitle(R.string.nav_all_tasks);
            fragment = new TaskElementsList();
            arguments.putString(TaskElementsList.STATUS_SELECTIONS, "");
            arguments.putString(TaskElementsList.STATUS_STATE, "");
        }
        if (id == R.id.nav_important){
            toolbar.setTitle(R.string.nav_important);
            fragment = new TaskElementsList();
            arguments.putString(TaskElementsList.STATUS_SELECTIONS, "status = ?");
            arguments.putString(TaskElementsList.STATUS_STATE, "1");
        }
        if (id == R.id.nav_complete_tasks){
            toolbar.setTitle(R.string.nav_complete_task);
            fragment = new TaskElementsList();
            arguments.putString(TaskElementsList.STATUS_SELECTIONS, "status = ?");
            arguments.putString(TaskElementsList.STATUS_STATE, "2");
        }
        if (id == R.id.nav_parent_labels){
            toolbar.setTitle(R.string.nav_task_parent_labels);
            fragment = new LabelElementsList();
        }

        fragment.setArguments(arguments);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_content, fragment);
        ft.addToBackStack(null);
        ft.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
