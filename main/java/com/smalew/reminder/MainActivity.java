package com.smalew.reminder;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.smalew.reminder.database.ReminderDBHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ListView leftMenu;

    private ReminderDBHelper reminderDBHelper;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        leftMenu = (ListView) findViewById(R.id.left_menu);
        createHeaderFooter();
        leftMenu.setAdapter(getAdapter());
        leftMenu.setOnItemClickListener(new DrawerOnClickItemListener());

        TaskListsFragment taskListsFragment = new TaskListsFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frame_container, taskListsFragment);

        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_task_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Метод открывает БД, берет данные и заполняет Adapter
     *
      * @return - Адаптер для ListView выдвижной панели.
     */
    private ListAdapter getAdapter(){
        reminderDBHelper = new ReminderDBHelper(this);
        SQLiteDatabase database = reminderDBHelper.getReadableDatabase();
        cursor = database.query("labels",new String[]{"label_name", "count"}, null,null,null,null,null);

        ArrayList<HashMap<String, Object>> list = new ArrayList<>();
        HashMap<String, Object> items;

        if (cursor.moveToFirst()){
            items = new HashMap<>();
            items.put("icon", R.drawable.ic_label_black_24dp);
            items.put("text1", cursor.getString(0));

            if (cursor.getInt(1) != 0)
                items.put("text2", cursor.getInt(1));
            else
                items.put("text2", "");

            list.add(items);

            while (cursor.moveToNext()) {
                items = new HashMap<>();
                items.put("icon", R.drawable.ic_label_black_24dp);
                items.put("text1", cursor.getString(0));

                if (cursor.getInt(1) != 0)
                    items.put("text2", cursor.getInt(1));
                else
                    items.put("text2", "");

                list.add(items);
            }
        }


        SimpleAdapter simpleAdapter = new SimpleAdapter(this,list,R.layout.left_menu_list_item,
                new String[]{"icon", "text1", "text2"},
                new int[]{R.id.list_item_icon, R.id.list_item_text1, R.id.list_item_text2});

        reminderDBHelper.closeConnection(cursor, database);

        return simpleAdapter;
    }
    private void createHeaderFooter(){
        View header, footer;

        header = getLayoutInflater().inflate(R.layout.left_menu_hf,null);
        ((ImageView)header.findViewById(R.id.list_hf_item_icon)).setImageResource(R.drawable.ic_playlist_add_black_24dp);
        ((TextView)header.findViewById(R.id.list_hf_item_text)).setText(R.string.create_label);
        leftMenu.addHeaderView(header);

        footer = getLayoutInflater().inflate(R.layout.left_menu_hf,null);
        ((ImageView)footer.findViewById(R.id.list_hf_item_icon)).setImageResource(R.drawable.ic_help_black_24dp);
        ((TextView)footer.findViewById(R.id.list_hf_item_text)).setText(R.string.help);
        leftMenu.addFooterView(footer);

        footer = getLayoutInflater().inflate(R.layout.left_menu_hf,null);
        ((ImageView)footer.findViewById(R.id.list_hf_item_icon)).setImageResource(R.drawable.ic_settings_black_24dp);
        ((TextView)footer.findViewById(R.id.list_hf_item_text)).setText(R.string.options);
        leftMenu.addFooterView(footer);
    }

    private class DrawerOnClickItemListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    }
}
