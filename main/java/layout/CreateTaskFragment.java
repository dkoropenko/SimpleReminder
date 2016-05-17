package layout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import ru.smalew.simplereminder.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 */
public class CreateTaskFragment extends Fragment {
    private String[] lablel_items = {"Test Label 1", "Test Label 2", "Test Label 3"};

    public CreateTaskFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_task, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        String usual = getResources().getString(R.string.task_choose_usual);
        String important = getResources().getString(R.string.task_choose_import);
        String[] importance_items = {usual, important};

        ArrayAdapter<String> importChooserAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, importance_items);
        Spinner importSpinner = (Spinner) getActivity().findViewById(R.id.import_choose);
        importSpinner.setAdapter(importChooserAdapter);
        importSpinner.setSelection(1);

        ArrayAdapter<String> labelChooserAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, lablel_items);
        Spinner labelSpinner = (Spinner) getActivity().findViewById(R.id.parent_label_choose);
        labelSpinner.setAdapter(labelChooserAdapter);
        labelSpinner.setSelection(1);
    }
}
