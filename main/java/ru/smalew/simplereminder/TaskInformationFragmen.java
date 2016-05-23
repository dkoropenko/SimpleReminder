package ru.smalew.simplereminder;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.smalew.simplereminder.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskInformationFragmen extends Fragment {


    public TaskInformationFragmen() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_information, container, false);
    }

}
