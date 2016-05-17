package ru.smalew.simplereminder;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.smalew.simplereminder.R;
import ru.smalew.simplereminder.database.LabelElementsListAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class LabelElementsList extends Fragment {

    RecyclerView rv;

    public LabelElementsList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_label_elements_list, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        rv = (RecyclerView) getActivity().findViewById(R.id.recycler_view_label);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(manager);

        LabelElementsListAdapter adapter = new LabelElementsListAdapter(getContext());
        rv.setAdapter(adapter);
    }
}
