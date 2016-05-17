package ru.smalew.simplereminder;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.smalew.simplereminder.database.TaskElementsListAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskElementsList extends Fragment {

    public static String STATUS_STATE = "status";
    public static String STATUS_SELECTIONS = "selections";

    private RecyclerView rv;
    private Bundle parameters;
    private String selections;
    private String[] args;


    public TaskElementsList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_elements_list, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        rv = (RecyclerView) getActivity().findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(layoutManager);

        selections = null;
        args = null;

        parameters = getArguments();
        if (!parameters.getString(STATUS_STATE).equals("")) {
            selections = parameters.getString(STATUS_SELECTIONS);
            args = new String[]{parameters.getString(STATUS_STATE)};
        }

        TaskElementsListAdapter adapter = new TaskElementsListAdapter(getContext(), selections, args);
        rv.setAdapter(adapter);
    }
}
