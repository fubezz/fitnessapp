package com.fubezz.fitnessapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fubezz.fitnessapp.listener.ItemClickSupport;
import com.fubezz.fitnessapp.model.DbHandler;
import com.fubezz.fitnessapp.model.RunSession;

import java.util.List;

public class RunActivity extends AppCompatActivity {

    private RecyclerView rv;
    private DbHandler saver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        Toolbar toolbar = (Toolbar) findViewById(R.id.run_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabNewRun);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RunActivity.this,NewRunActivity.class);
                startActivity(intent);
            }
        });


       // final ListView listView = (ListView) findViewById(R.id.RunList);
        rv = (RecyclerView) findViewById(R.id.run_recycleView);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);


        saver = new DbHandler(RunActivity.this);
        final List<RunSession> sessionList = saver.getAllRunSessions();
        Log.v("RunListSize: ", Integer.toString(sessionList.size()));
        //final ArrayAdapter<RunSession> adapter = new ArrayAdapter<RunSession>(RunActivity.this, android.R.layout.simple_list_item_1, sessionList);
        //listView.setAdapter(adapter);
        final RVAdapter adapter = new RVAdapter(sessionList);

        if (rv != null) {
            rv.setAdapter(adapter);
            ItemClickSupport.addTo(rv).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                    Intent intent = new Intent(RunActivity.this,TabActivity.class);
                    RunSession sessionClicked = sessionList.get(position);
                    intent.putExtra("runsession", sessionClicked.getDateLong());
                    startActivity(intent);
                }
            });
            ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {


                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return true;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                    int position = viewHolder.getLayoutPosition();
                    RunSession toDelete = adapter.sessions.get(position);
                    Log.v("Delete Position: ", Integer.toString(position));
                    saver.deleteRunSession(toDelete);
                                    //remove from list
                    sessionList.remove(position);
                    adapter.notifyItemRemoved(viewHolder.getLayoutPosition());

                }
            };
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
            itemTouchHelper.attachToRecyclerView(rv);

        }


    }
    @Override
    public void onResume(){
        super.onResume();
        if (rv != null){
            List<RunSession> sessionList = saver.getAllRunSessions();
            ((RVAdapter)rv.getAdapter()).sessions.clear();
            ((RVAdapter)rv.getAdapter()).sessions.addAll(sessionList);
            rv.getAdapter().notifyDataSetChanged();
        }

    }

    private static class RVAdapter extends RecyclerView.Adapter<RVAdapter.RunSessionViewHolder>{

        List<RunSession> sessions;

        RVAdapter(List<RunSession> sessions){
            this.sessions = sessions;
        }

        @Override
        public RunSessionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardrun_layout, parent, false);
            RunSessionViewHolder pvh = new RunSessionViewHolder(v);
            return pvh;
        }

        @Override
        public void onBindViewHolder(RunSessionViewHolder holder, int position) {
            holder.sessionName.setText(sessions.get(position).getName());
            holder.sessionDate.setText("Date: " + sessions.get(position).getDate());
            int dist =  sessions.get(position).getDistance();
            if(dist > 1000){
                float d = dist/1000;
                holder.sessionDist.setText(", Distance: "+ Float.toString(d) +"km");
            }else{
                holder.sessionDist.setText(", Distance: "+ Integer.toString(dist) +"m");
            }

        }

        @Override
        public int getItemCount() {
            return sessions.size();
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }


        public static class RunSessionViewHolder extends RecyclerView.ViewHolder {
            TextView sessionDate;
            TextView sessionDist;
            TextView sessionName;

            RunSessionViewHolder(View itemView) {
                super(itemView);
                sessionDate = (TextView)itemView.findViewById(R.id.card_date);
                sessionDist = (TextView)itemView.findViewById(R.id.card_dist);
                sessionName = (TextView)itemView.findViewById(R.id.card_name);

            }
        }

    }




}
