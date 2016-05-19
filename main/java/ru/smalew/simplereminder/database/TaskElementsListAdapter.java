package ru.smalew.simplereminder.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.support.annotation.IntegerRes;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import ru.smalew.simplereminder.R;

/**
 * Created by koropenkods on 12.05.16.
 */
public class TaskElementsListAdapter extends RecyclerView.Adapter<TaskElementsListAdapter.TaskElementsHolder> {

    private List<Map<String, Object>> elements;

    public TaskElementsListAdapter(Context context, String selections, String[] args){
        ReminderDBHelper reminderDBHelper = new ReminderDBHelper(context);
        elements = reminderDBHelper.getTasksElements(selections, args);
        reminderDBHelper.closeConnection();
    }

    public static class TaskElementsHolder extends RecyclerView.ViewHolder{
        CardView cv;
        TextView taskName;
        TextView taskStartDate;
        TextView taskParentLabel;
        ImageView statusInfo;

        public TaskElementsHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.task_item_conteiner);
            taskName = (TextView) itemView.findViewById(R.id.task_name);
            taskStartDate = (TextView) itemView.findViewById(R.id.task_date_info);
            taskParentLabel = (TextView) itemView.findViewById(R.id.task_parent_label);
            statusInfo = (ImageView) itemView.findViewById(R.id.task_status);
        }
    }

    @Override
    public TaskElementsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tasks_design, parent, false);
        TaskElementsHolder holder = new TaskElementsHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(TaskElementsHolder holder, int position) {
        //Элемент из списка, содержащий нужную информацию
        Map<String, Object> result = elements.get(position);

        //Указываем навание задачи
        holder.taskName.setText(result.get(ReminderDBHelper.TASK_NAME_IDENT).toString());

        //Забираем дату выполнения задачи из БД.
        //********************************************************************************************
        Calendar calendar = Calendar.getInstance();
        long startDate = Long.parseLong(result.get(ReminderDBHelper.TASK_LIMIT_DATE_IDENT).toString());
        calendar.setTimeInMillis(startDate);

        StringBuilder resultDate = new StringBuilder();

        //Добавляем 0 перед датой, если она "одинарная"
        if (calendar.get(Calendar.DAY_OF_MONTH) < 10){
            resultDate.append("0");
            resultDate.append(calendar.get(Calendar.DAY_OF_MONTH));
        }
        else
            resultDate.append(calendar.get(Calendar.DAY_OF_MONTH));
        resultDate.append(".");
        if (calendar.get(Calendar.MONTH) < 10){
            resultDate.append("0");
            resultDate.append(calendar.get(Calendar.MONTH));
        }
        else
            resultDate.append(calendar.get(Calendar.MONTH));
        resultDate.append(".");
        resultDate.append(calendar.get(Calendar.YEAR));
        //********************************************************************************************

        //Указываем дату и ярлык задачи.
        holder.taskStartDate.setText(resultDate.toString());
        holder.taskParentLabel.setText(result.get(ReminderDBHelper.TASK_PARENT_LABEL_IDENT).toString());

        //Указываем статус задачи (Важная, обычная, выполненная)
        int status = Integer.parseInt(result.get(ReminderDBHelper.TASK_STATUS_IDENT).toString());
        switch (status){
            case 0:
                holder.statusInfo.setBackgroundResource(R.color.status_usual);
                break;
            case 1:
                holder.statusInfo.setBackgroundResource(R.color.status_important);
                break;
            case 2:
                holder.statusInfo.setBackgroundResource(R.color.status_complete);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
