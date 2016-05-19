package ru.smalew.simplereminder;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.app.Dialog;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import ru.smalew.simplereminder.database.ReminderDBHelper;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 */
public class CreateTaskFragment extends Fragment {

    public static Calendar limitDateValue;

    public CreateTaskFragment() {
        limitDateValue = Calendar.getInstance();
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
        createSpinners();
        createDateLimit();
    }

    private void createSpinners(){
        //Выпадающий список "Важности" задачи
        ArrayAdapter<String> importChooserAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.import_chooser_elements));
        Spinner importSpinner = (Spinner) getActivity().findViewById(R.id.import_choose);
        importSpinner.setAdapter(importChooserAdapter);
        importSpinner.setSelection(0);

        //Выпадающий список ярылка задачи
        ReminderDBHelper reminderDBHelper = new ReminderDBHelper(getContext());
        List<Map<String, Object>> elements = reminderDBHelper.getLabelElements();
        reminderDBHelper.closeConnection();

        String[] label_items = new String[elements.size()+1];

        label_items[0] = getResources().getString(R.string.task_choose_label);

        for (int i = 1; i <= elements.size(); i++) {
            label_items[i] = elements.get(i-1).get(ReminderDBHelper.LABEL_NAME_IDENT).toString();
        }

        ArrayAdapter<String> labelChooserAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, label_items);
        Spinner labelSpinner = (Spinner) getActivity().findViewById(R.id.parent_label_choose);
        labelSpinner.setAdapter(labelChooserAdapter);
        labelSpinner.setSelection(0);
    }

    private void createDateLimit(){
        limitDateValue.setTimeInMillis(Calendar.getInstance().getTimeInMillis());
        TextView choosenDate = (TextView) getActivity().findViewById(R.id.task_date_chooser);
        choosenDate.setText(dateFormating());
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getContext(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            limitDateValue.set(year, monthOfYear, dayOfMonth);
            TextView choosenDate = (TextView) getActivity().findViewById(R.id.task_date_chooser);
            choosenDate.setText(dateFormating());
        }
    }

    public static String dateFormating(){
        StringBuilder resultDate = new StringBuilder();

        //Добавляем 0 перед датой, если она "одинарная"
        if (limitDateValue.get(Calendar.DAY_OF_MONTH) < 10){
            resultDate.append("0");
            resultDate.append(limitDateValue.get(Calendar.DAY_OF_MONTH));
        }
        else
            resultDate.append(limitDateValue.get(Calendar.DAY_OF_MONTH));
        resultDate.append(".");
        if (limitDateValue.get(Calendar.MONTH) < 9){
            resultDate.append("0");
            resultDate.append(limitDateValue.get(Calendar.MONTH)+1);
        }
        else
            resultDate.append(limitDateValue.get(Calendar.MONTH)+1);
        resultDate.append(".");
        resultDate.append(limitDateValue.get(Calendar.YEAR));

        return resultDate.toString();
    }
}
