package com.example.welcome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, MainToDoListActivity.class);
        startActivity(intent);
    }

    public void onClickStart(View view){
        Intent intent = new Intent(this, MainToDoListActivity.class);
        startActivity(intent);

    }
}