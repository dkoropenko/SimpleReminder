package ru.smalew.simplereminder.database;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import ru.smalew.simplereminder.R;

/**
 * Created by koropenkods on 13.05.16.
 */
public class LabelElementsListAdapter extends RecyclerView.Adapter<LabelElementsListAdapter.LabelElementsHolder> {

    private List<Map<String, Object>> elements;

    public LabelElementsListAdapter(Context context){
        ReminderDBHelper reminderDBHelper = new ReminderDBHelper(context);
        elements = reminderDBHelper.getLabelElements();
        reminderDBHelper.closeConnection();
    }

    public static class LabelElementsHolder extends RecyclerView.ViewHolder {
        TextView labelName;
        TextView usualCount;
        TextView importantCount;
        TextView completeCount;

        public LabelElementsHolder(View itemView) {
            super(itemView);
            labelName = (TextView) itemView.findViewById(R.id.label_name);
            usualCount = (TextView) itemView.findViewById(R.id.label_usual_count);
            importantCount = (TextView) itemView.findViewById(R.id.label_important_count);
            completeCount = (TextView) itemView.findViewById(R.id.label_complete_count);
        }
    }

    @Override
    public LabelElementsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.label_design, parent, false);
        LabelElementsHolder holder = new LabelElementsHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(LabelElementsHolder holder, int position) {
//        Map<String, Object> result = elements.get(position);
//        holder.labelName.setText(result.get(ReminderDBHelper.LABEL_NAME_IDENT).toString());
//        holder.usualCount.setText(result.get(ReminderDBHelper.LABEL_COUNT_USUAL_IDENT).toString());
//        holder.importantCount.setText(result.get(ReminderDBHelper.LABEL_COUNT_IMPORT_IDENT).toString());
//        holder.completeCount.setText(result.get(ReminderDBHelper.LABEL_COUNT_COMLETE_IDENT).toString());
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
