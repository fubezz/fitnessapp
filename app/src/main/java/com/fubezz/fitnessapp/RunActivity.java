package com.fubezz.fitnessapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.fubezz.fitnessapp.model.DbHandler;
import com.fubezz.fitnessapp.model.RunSession;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class RunActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabNewRun);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RunActivity.this,NewRunActivity.class);
                startActivity(intent);
            }
        });


        ListView listView = (ListView) findViewById(R.id.RunList);
        DbHandler saver = new DbHandler(RunActivity.this);
        List<RunSession> sessionList = saver.getAllRunSessions();
        Log.v("RunListSize: ", Integer.toString(sessionList.size()));
        Log.v("FirstElem: ", sessionList.get(0).toString());
        ArrayAdapter adapter = new ArrayAdapter<RunSession>(this, android.R.layout.simple_list_item_1, sessionList);
        listView.setAdapter(adapter);


    }
    @Override
    public void onResume(){
        super.onResume();
        ListView listView = (ListView) findViewById(R.id.RunList);
        DbHandler saver = new DbHandler(RunActivity.this);
        List<RunSession> sessionList = saver.getAllRunSessions();
        Log.v("RunListSize: ", Integer.toString(sessionList.size()));
        Log.v("FirstElem: ", sessionList.get(0).toString());
        ArrayAdapter adapter = new ArrayAdapter<RunSession>(this, android.R.layout.simple_list_item_1, sessionList);
        listView.setAdapter(adapter);

    }

}
