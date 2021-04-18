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
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainToDoListActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private static final TimeInterpolator GAUGE_ANIMATION_INTERPOLATOR = new DecelerateInterpolator(2);
    private static final int MAX_LEVEL = 100;
    private static final long GAUGE_ANIMATION_DURATION = 5000;
    private ProgressBar myProgressBar;
    private static String globalPriority;
    private RadioGroup radioGroup;
    private int idTask;
    private String todoPhrase = "Today you have to do";
    private int counterCompleted = 0;

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
                writePriorityTask(0);
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) { // need to fix
                        switch (checkedId){
                            case - 1:
                                writePriorityTask(0);
                                globalPriority = "0";
                                System.out.println(globalPriority);
                                break;
                            case R.id.radioButtonFiftyPriority:
                                globalPriority = "50";
                                System.out.println(globalPriority);
                                break;
                            case R.id.radioButtonSixtyPriority:
                                globalPriority = "60";
                                System.out.println(globalPriority);
                                break;
                        }
                    }
                });

                if (!taskName.equals("")) {
                    dataBaseEnterData(taskName, globalPriority, getDateMyString());

                    statesList.add(new States(globalPriority, taskName, idTask));
                    haveToDoTask(1); // add one task to textView

                }
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false); // can't cancel if missclick on screen

        dialog.show(); // show dialog

        plAdapter = new MyAdapter(statesList, this); // show task on list
        lv.setAdapter(plAdapter);

    }

    private void writePriorityTask(int index){
        System.out.println("true");
        switch (index){
            case 0:
                globalPriority = "0";
                break;
            case 50:
                globalPriority = "50";
                break;
            case 60:
                globalPriority = "60";
                break;
        }

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
        haveToDoTask(0);
        checkOldTaskYet();

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
            System.out.println("IdTask is = " + s.getIdTask());
            haveToDoTask(-1);
            counterCompleted += 1; // add + 1 to completed textView
            completedTaskDataBase();
            changeTextViewCompleted();
            dataBaseDeleteDataSingle(s.getIdTask());
            try {
                reWrite();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void reWrite() throws InterruptedException {
        plAdapter = new MyAdapter(statesList, this);
        lv.setAdapter(plAdapter);
    }

    private void haveToDoTask(int add){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_TASKS, null,null,null, null, null, null);
        int toDoTaskCounter = 0;

        if(cursor.moveToFirst()){
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);

            do {
                toDoTaskCounter += 1;
            }while(cursor.moveToNext());
        }

        toDoTaskCounter += add;
        TextView textToDo = findViewById(R.id.textView2);
        textToDo.setText(todoPhrase + " " +  toDoTaskCounter);
    }

    private String getDateMyString(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("dd");
        String date = currentTime.format(calendar.getTime());

        return date;
    }

    private void checkOldTaskYet(){
        System.out.println("Delete Because old");
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_TASKS, null,null,null, null, null, null);

        if (cursor.moveToFirst()){
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int dateIndex = cursor.getColumnIndex(DBHelper.KEY_TIME);
            String dataBaseDate = cursor.getString(dateIndex);
            String nowDate = getDateMyString();

            do {
                if (!nowDate.equals(dataBaseDate)) {
                    idTask = cursor.getInt(idIndex);
                    dataBaseDeleteDataSingle(idTask);
                }
            }while (cursor.moveToNext());
        }

        cursor.close();
        dbHelper.close();
    }

    private void changeTextViewCompleted(){
        checkOldTaskYet();
        TextView text = findViewById(R.id.textView7);
        String textToInput = "Today you completed";
        text.setText(textToInput + " " + counterCompleted);
    }

    private void completedTaskDataBase(){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_COMPLETED, counterCompleted);
    }

    public void dataBaseEnterData(String taskName, String priority, String time){
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_TASK, taskName);
        contentValues.put(DBHelper.KEY_PRIORITY, priority);
        contentValues.put(DBHelper.KEY_TIME, time);

        database.insert(DBHelper.TABLE_TASKS, null, contentValues);

        Cursor cursor = database.query(DBHelper.TABLE_TASKS, null,null,null, null, null, null);

        if (cursor.moveToFirst()){
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_TASK);
//            int priorityId = cursor.getColumnIndex(DBHelper.KEY_PRIORITY);
            do {
                if (cursor.getString(nameIndex).equals(taskName))
                    idTask = cursor.getInt(idIndex);
            }while (cursor.moveToNext());
        }

        cursor.close();
        dbHelper.close();
    }

    public void dataBaseReturnData() throws InterruptedException {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_TASKS, null,null,null, null, null, null);

        if (cursor.moveToFirst()){
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_TASK);
            int priority = cursor.getColumnIndex(DBHelper.KEY_PRIORITY);
            int completedIndex = cursor.getColumnIndex(DBHelper.KEY_COMPLETED);

            do {
                Log.d("mLog", " ID = " + cursor.getInt(idIndex) +
                        ", name = " + cursor.getString(nameIndex) + " priority = " + cursor.getInt(priority));
                statesList.add(new States(cursor.getString(priority), cursor.getString(nameIndex), cursor.getInt(idIndex)));
                reWrite();
                counterCompleted = cursor.getInt(completedIndex);
                changeTextViewCompleted();
            } while(cursor.moveToNext());

        } else
            Log.d("mLog", " empty");
        cursor.close();
    }

    public void dataBaseDeleteDataSingle(int id){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_TASKS, null,null,null, null, null, null);
        database.delete(DBHelper.TABLE_TASKS, DBHelper.KEY_ID + "=" + id, null);
        System.out.println("true");
        cursor.close();
    }

}