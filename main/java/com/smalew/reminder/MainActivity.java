package com.smalew.reminder;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
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

public class MainActivity extends Activity {

    private ListView leftMenu;

    private ReminderDBHelper reminderDBHelper;
    private Cursor cursor;

    private Fragment tasks;
    private String labelName;

    private Fragment addLabelFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        leftMenu = (ListView) findViewById(R.id.left_menu);
        createHeaderFooter();
        leftMenu.setAdapter(getAdapter());
        leftMenu.setOnItemClickListener(new DrawerOnClickItemListener());

        labelName = "Main";
        createTaskFragment();
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
            items.put("mainText", cursor.getString(0));
            items.put("count", cursor.getInt(1));
            list.add(items);

            while (cursor.moveToNext()) {
                items = new HashMap<>();
                items.put("icon", R.drawable.ic_label_black_24dp);
                items.put("mainText", cursor.getString(0));
                items.put("count", cursor.getInt(1));
                list.add(items);
            }
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(this,list,R.layout.left_menu_list_item,
                new String[]{"icon", "mainText", "count"},
                new int[]{R.id.list_item_icon, R.id.list_item_main_text, R.id.list_item_count});

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

            if (position == 0){
                TextView name = (TextView) view.findViewById(R.id.list_hf_item_text);
                labelName = name.getText().toString();

                addLabelFragment = new AddLabelFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.frame_container, addLabelFragment);
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }

            DrawerLayout leftMenu = (DrawerLayout) findViewById(R.id.drawer_layout);
            ListView leftMenuList = (ListView) findViewById(R.id.left_menu);
            leftMenu.closeDrawer(leftMenuList);
        }
    }

    //Добавление ярлыка. Берутся значения, введенные пользователем, проверяются и заносятся в БД.
    public void addLabel(View view){
        TextView mainLabelText = (TextView) findViewById(R.id.add_label_name);
        TextView mainLabelDesc = (TextView) findViewById(R.id.add_label_desc);

        String nameText = mainLabelText.getText().toString();
        String nameDesc = mainLabelDesc.getText().toString();

        ReminderDBHelper reminderDBHelper = new ReminderDBHelper(this);
        SQLiteDatabase db = reminderDBHelper.getWritableDatabase();

        if (!nameText.equals("") && !reminderDBHelper.checkLabelName(db, nameText)){
            reminderDBHelper.addLabel(db, nameText, nameDesc);
            reminderDBHelper.closeConnection(null, db);

            this.recreate();
            createTaskFragment();
        }else{
            mainLabelText.setBackgroundColor(getResources().getColor(R.color.important_list_background));
        }
    }

    private void createTaskFragment(){
        tasks = new TaskListsFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frame_container, tasks);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }
}
