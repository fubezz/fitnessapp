package com.fubezz.fitnessapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.fubezz.fitnessapp.model.DbHandler;
import com.fubezz.fitnessapp.model.RunSession;

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


        final ListView listView = (ListView) findViewById(R.id.RunList);
        final DbHandler saver = new DbHandler(RunActivity.this);
        final List<RunSession> sessionList = saver.getAllRunSessions();
        Log.v("RunListSize: ", Integer.toString(sessionList.size()));
        final ArrayAdapter<RunSession> adapter = new ArrayAdapter<RunSession>(RunActivity.this, android.R.layout.simple_list_item_1, sessionList);
        listView.setAdapter(adapter);
        if (listView != null) {
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    new AlertDialog.Builder(RunActivity.this)
                            .setTitle("Delete run session")
                            .setMessage("Are you sure you want to delete this session?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    final List<RunSession> sessionList = saver.getAllRunSessions();
                                    ArrayAdapter<RunSession> adp = (ArrayAdapter<RunSession>) listView.getAdapter();
                                    RunSession toDelete = sessionList.get(position);
                                    saver.deleteRunSession(toDelete);
                                    //remove from list
                                    sessionList.remove(position);
                                    adp.remove(adp.getItem(position));
                                    adp.notifyDataSetChanged();
                                    Toast.makeText(RunActivity.this, "Session: " + toDelete.getDate() + " deleted", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing

                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                    return false;
                }
            });
        }


    }
    @Override
    public void onResume(){
        super.onResume();
        ListView listView = (ListView) findViewById(R.id.RunList);
        DbHandler saver = new DbHandler(RunActivity.this);
        List<RunSession> sessionList = saver.getAllRunSessions();
        Log.v("RunListSize: ", Integer.toString(sessionList.size()));
        ArrayAdapter adapter = new ArrayAdapter<RunSession>(this, android.R.layout.simple_list_item_1, sessionList);
        listView.setAdapter(adapter);

    }

}
