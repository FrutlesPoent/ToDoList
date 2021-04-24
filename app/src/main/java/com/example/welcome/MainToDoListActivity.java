package com.example.welcome;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.TimeInterpolator;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainToDoListActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private static final TimeInterpolator GAUGE_ANIMATION_INTERPOLATOR = new DecelerateInterpolator(2);
    private static final int MAX_LEVEL = 100;
    private static final long GAUGE_ANIMATION_DURATION = 5000;
    private ProgressBar myProgressBar;
    private  static int globalPriority;
    private int idTask;
    final int progressBar = calculateProgress();
    private int toDoTaskCounter;
    private String todoPhrase = "Today you have to do";
    private int counterCompleted = 0;
    private ImageView imageView;

    private DBHelper dbHelper;
    private Dialog dialog;

    ListView lv;
    ArrayList<States> statesList = new ArrayList<States>();
    MyAdapter plAdapter;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_to_do_list);
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.icon);


        myProgressBar = findViewById(R.id.progressBar);
        myProgressBar.setMin(0);
        myProgressBar.setMax(100);
        lv = (ListView) findViewById(R.id.listView1);
        dialog = new Dialog(MainToDoListActivity.this);
        dbHelper = new DBHelper(this);
        getImage();
        getCompletedTaskDataBase();
        changeTextViewCompleted();
        haveToDoTask(0);
        myProgressBar.setProgress(calculateProgress());


        try {
            dataBaseReturnData();
            haveToDoTask(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onStart(){
        super.onStart();
        getImage();
    }

    public void onClickSettings(View view){ // move to Settings Window
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);

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
                RadioButton lowPriority = dialog.findViewById(R.id.radioButtonFiftyPriority);
                RadioButton mediumPriority = dialog.findViewById(R.id.radioButtonSixtyPriority);
                RadioButton highPriority = dialog.findViewById(R.id.radioButtonSeventy);
                RadioGroup radioGroup = dialog.findViewById(R.id.radio_group);
                if (lowPriority.isChecked())
                    globalPriority = 50;

                if (mediumPriority.isChecked())
                    globalPriority = 60;

                if (highPriority.isChecked())
                    globalPriority = 70;

                plAdapter.notifyDataSetChanged();
                String taskName = getTaskName.getText().toString();
                System.out.println("Global" + globalPriority);

                if (!taskName.equals("")) {
                    dataBaseEnterData(taskName, globalPriority, getDateMyString());

                    statesList.add(new States(globalPriority, taskName, idTask));
                    haveToDoTask(1); // add one task to textView
                    myProgressBar.setProgress(calculateProgress());

                }
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false); // can't cancel if missclick on screen

        dialog.show(); // show dialog
//        plAdapter.notifyDataSetChanged();
        plAdapter = new MyAdapter(statesList, this); // show task on list

        lv.setAdapter(plAdapter);
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { // check list of task
        int pos = lv.getPositionForView(buttonView);
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

    private int calculateProgress(){
        if (counterCompleted == 0)
            return 0;

        if (toDoTaskCounter == 0)
            return 100;
        int summ = toDoTaskCounter + counterCompleted;
        double a = (counterCompleted * 100) / summ;
        System.out.println((int)a);
        return (int) a;
    }


    private void reWrite() throws InterruptedException {
        plAdapter = new MyAdapter(statesList, this);
        myProgressBar.setProgress(calculateProgress());
        if (plAdapter !=null){
            plAdapter.notifyDataSetChanged(); // to not drop app
        }
        lv.setAdapter(plAdapter);
    }

    private void haveToDoTask(int add){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_TASKS, null,null,null, null, null, null);
        int valAddTask = 0;

        if(add >= 0) {
            if (cursor.moveToFirst()) {
                do {
                    valAddTask += 1;
                } while (cursor.moveToNext());
            }
            valAddTask -= toDoTaskCounter;
        }else if(add < 0){
            toDoTaskCounter += add;
        }

        toDoTaskCounter += valAddTask;
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
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_TASKS, null,null,null, null, null, null);

        if (cursor.moveToFirst()){
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int dateIndex = cursor.getColumnIndex(DBHelper.KEY_TIME);
            String dataBaseDate = cursor.getString(dateIndex);
            String nowDate = getDateMyString();

            do {
                if (!nowDate.equals(dataBaseDate)) {
                    counterCompleted = 0; // to zero "completed" table
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

    private void getImage(){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_IMAGE, null,null,null, null, null, null);
        if (cursor.moveToFirst()){
            byte[] image = cursor.getBlob(1);
            Bitmap newImage = BitmapFactory.decodeByteArray(image, 0, image.length);
            imageView = findViewById(R.id.imageView);
            Bitmap bMapScaled = Bitmap.createScaledBitmap(newImage, 500, 500, false);
            imageView.setImageBitmap(bMapScaled);
        }
    }

    private void completedTaskDataBase(){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_COMPLETED, counterCompleted);
        Cursor cursor = database.query(DBHelper.TABLE_COMPLETED, null,null,null, null, null, null);
        if (cursor.getCount() > 0){
            database.update(DBHelper.TABLE_COMPLETED, contentValues,DBHelper.KEY_COMPLETED + "=" + DBHelper.KEY_COMPLETED, null);
        } else{
            database.insert(DBHelper.TABLE_COMPLETED, null, contentValues);
        }
        cursor.close();
        database.close();
    }

    private void getCompletedTaskDataBase(){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_COMPLETED,null,null,null, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_COMPLETED);
            do {
                counterCompleted = cursor.getInt(idIndex);
            } while (cursor.moveToNext());
        }

    }

    private void dataBaseEnterData(String taskName, int priority, String time){
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
            do {
                if (cursor.getString(nameIndex).equals(taskName))
                    idTask = cursor.getInt(idIndex);
            }while (cursor.moveToNext());
        }

        cursor.close();
        dbHelper.close();
    }

    private void dataBaseReturnData() throws InterruptedException {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_TASKS, null,null,null, null, null, DBHelper.KEY_PRIORITY + " DESC");

        if (cursor.moveToFirst()){
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_TASK);
            int priority = cursor.getColumnIndex(DBHelper.KEY_PRIORITY);

            do {
                Log.d("mLog", " ID = " + cursor.getInt(idIndex) +
                        ", name = " + cursor.getString(nameIndex) + " priority = " + cursor.getInt(priority));
                statesList.add(new States(cursor.getInt(priority), cursor.getString(nameIndex), cursor.getInt(idIndex)));
                reWrite();
                changeTextViewCompleted();
            } while(cursor.moveToNext());

        } else
            Log.d("mLog", " empty");
        cursor.close();
    }

    private void dataBaseDeleteDataSingle(int id){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_TASKS, null,null,null, null, null, null);
        database.delete(DBHelper.TABLE_TASKS, DBHelper.KEY_ID + "=" + id, null);
        cursor.close();
    }



}