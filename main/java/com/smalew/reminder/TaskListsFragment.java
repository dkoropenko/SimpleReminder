package com.smalew.reminder;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.Toast;

import com.smalew.reminder.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskListsFragment extends Fragment {

    private TabHost tabHost;


    public TaskListsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_lists, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (tabHost == null){
            tabHost = (TabHost) getActivity().findViewById(android.R.id.tabhost);
            tabHost.setup();

            TabHost.TabSpec tabSpec;

            tabSpec = tabHost.newTabSpec("important");
            tabSpec.setIndicator("Важные");
            tabSpec.setContent(R.id.listImportant);
            tabHost.addTab(tabSpec);

            tabSpec = tabHost.newTabSpec("usual");
            tabSpec.setIndicator("Обычные");
            tabSpec.setContent(R.id.listCasual);
            tabHost.addTab(tabSpec);

            tabSpec = tabHost.newTabSpec("Complete");
            tabSpec.setIndicator("Выполнено");
            tabSpec.setContent(R.id.listComlete);
            tabHost.addTab(tabSpec);

            tabHost.setCurrentTabByTag("important");

            tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
                @Override
                public void onTabChanged(String tabId) {
                    Toast.makeText(getActivity().getBaseContext(), "tabID = "+ tabId, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
