package com.example.welcome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainToDoListActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    ListView lv;
    ArrayList<States> statesList;
    MyAdapter plAdapter;


    public void onClickSettings(View view){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_to_do_list);

        lv = (ListView) findViewById(R.id.listView1);
        displayList();

    }

    private void displayList(){
        statesList = new ArrayList<States>();
        statesList.add(new States(50,"hello"));
        statesList.add(new States(60,"world"));

        plAdapter = new MyAdapter(statesList, this);
        lv.setAdapter(plAdapter);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int pos = lv.getPositionForView(buttonView);

        if (pos != ListView.INVALID_POSITION) {
            States s = statesList.get(pos);
            s.setChecked(true);
        }
    }
}