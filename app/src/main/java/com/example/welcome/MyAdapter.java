package com.example.welcome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;


public class MyAdapter extends ArrayAdapter<States> {

    protected List<States> taskStates;
    private Context context;

    public MyAdapter(ArrayList<States> statesList, Context context) {
        super(context, R.layout.check_box, statesList);
        this.taskStates = statesList;
        this.context = context;
    }

    private static class StatesHolder {
        public TextView taskName;
        public TextView priority;
        public CheckBox checkBox;
        public ImageButton settingsTaskButton;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        StatesHolder holder = new StatesHolder();

        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.check_box, null);
            holder.taskName = (TextView) v.findViewById(R.id.taskName);
            holder.priority = (TextView) v.findViewById(R.id.priority);
            holder.checkBox = (CheckBox) v.findViewById(R.id.checkbox);
            holder.settingsTaskButton = (ImageButton) v.findViewById(R.id.button_settings_task);

            holder.checkBox.setOnCheckedChangeListener((MainToDoListActivity) context);


        }else {
            holder = (StatesHolder) v.getTag();
        }

        States name = taskStates.get(position);

        if(holder == null){ // to prevent null pointer exception while scrolling
            return v;
        }

        holder.taskName.setText(name.getTaskText());
        holder.priority.setText(String.valueOf(name.getPriority())); // need to change cause there will be icons of priority, but this will do for now
        holder.checkBox.setChecked(name.getChecked());

        return v;
    }
}
