package com.example.welcome;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class MainToDoListActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private static final TimeInterpolator GAUGE_ANIMATION_INTERPOLATOR = new DecelerateInterpolator(2);
    private static final int MAX_LEVEL = 100;
    private static final long GAUGE_ANIMATION_DURATION = 5000;
    private ProgressBar myProgressBar;
    private int globalPriority;
    private RadioGroup radioGroup;

    private DBHelper dbHelper;
    private Dialog dialog;

    ListView lv;
    ArrayList<States> statesList = new ArrayList<States>();
    MyAdapter plAdapter;


    public void onClickSettings(View view){ // move to Settings Window
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);

    }

    public void onClick(View view) { // check progressBar
        ObjectAnimator animator = ObjectAnimator.ofInt(myProgressBar, "progress", 0, MAX_LEVEL);
        animator.setInterpolator(GAUGE_ANIMATION_INTERPOLATOR);
        animator.setDuration(GAUGE_ANIMATION_DURATION);
        animator.start();
    }

    public void onClickAddTask(View view){
        dialog.setContentView(R.layout.dialog);
        Button createTask = (Button) dialog.findViewById(R.id.buttonSendData);
        Button dismissTask = (Button) dialog.findViewById(R.id.button_dismiss);

        dismissTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        createTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText getTaskName = dialog.findViewById(R.id.input_text_dialog);
                String taskName = getTaskName.getText().toString();
                int priorityTask;
                radioGroup = dialog.findViewById(R.id.radio_group);
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) { // need to fix
                        switch (checkedId){
                            case - 1:
                                globalPriority = 0;
                                System.out.println(globalPriority);
                                break;
                            case R.id.radioButtonFiftyPriority:
                                globalPriority = 50;
                                System.out.println(globalPriority);
                                break;
                            case R.id.radioButtonSixtyPriority:
                                globalPriority = 60;
                                System.out.println(globalPriority);
                                break;
                        }
                    }
                });
                priorityTask = globalPriority;

                if (!taskName.equals("")) {
                    statesList.add(new States(priorityTask, taskName));

                    dataBaseEnterData(taskName, String.valueOf(priorityTask));
                }
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false); // can't cancel if missclick on screen

        dialog.show(); // show dialog

        plAdapter = new MyAdapter(statesList, this); // show task on list
        lv.setAdapter(plAdapter);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_to_do_list);
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.icon);

        lv = (ListView) findViewById(R.id.listView1);
        dialog = new Dialog(MainToDoListActivity.this);
        dbHelper = new DBHelper(this);

        try {
            dataBaseReturnData();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { // check list of task
        int pos = lv.getPositionForView(buttonView);
        int del = 0;
        States s = statesList.get(pos);

        if (pos != ListView.INVALID_POSITION) {
            s = statesList.get(pos);
            statesList.remove(pos);
            s.setChecked(true);
            try {
                reWrite();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void reWrite() throws InterruptedException {
        plAdapter = new MyAdapter(statesList, this);
        Thread.sleep(1500);
        lv.setAdapter(plAdapter);
    }

    public void dataBaseEnterData(String taskName, String priority){
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_TASK, taskName);
//        contentValues.put(DBHelper.KEY_PRIORITY, priority);

        database.insert(DBHelper.TABLE_TASKS, null, contentValues);


        dbHelper.close();
    }

    public void dataBaseReturnData() throws InterruptedException {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_TASKS, null,null,null, null, null, null);

        if (cursor.moveToFirst()){
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_TASK);
            int priority = cursor.getColumnIndex(DBHelper.KEY_PRIORITY);

            do {
                Log.d("mLog", " ID = " + cursor.getInt(idIndex) +
                        ", name = " + cursor.getString(nameIndex));
                statesList.add(new States(0, cursor.getString(nameIndex)));
                reWrite();
            } while(cursor.moveToNext());

        } else
            Log.d("mLog", " empty");
        cursor.close();
    }

}