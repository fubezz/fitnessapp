package com.fubezz.fitnessapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fubezz.fitnessapp.model.DbHandler;
import com.fubezz.fitnessapp.model.RunSession;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StatisticFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StatisticFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatisticFragment extends Fragment {

    private static final String RUN_SESSION = "runSessionId";

    private static final int GEN_STATS = 0;


    // TODO: Rename and change types of parameters
    private long runSessionId;
    private RunSession runSession;
    private RecyclerView rv;

    private OnFragmentInteractionListener mListener;

    public StatisticFragment() {
        // Required empty public constructor
    }


    public static StatisticFragment newInstance(long runSession) {
        StatisticFragment fragment = new StatisticFragment();
        Bundle args = new Bundle();
        args.putLong(RUN_SESSION, runSession);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            runSessionId = getArguments().getLong(RUN_SESSION);
            runSession = new DbHandler(this.getContext()).getRunSession(runSessionId);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View rootView = inflater.inflate(R.layout.fragment_statistic, container, false);
        rv = (RecyclerView) rootView.findViewById(R.id.run_statisticsView);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(rootView.getContext());
        rv.setLayoutManager(llm);

        int[] layouts = {GEN_STATS};
        StatsAdapter adapter = new StatsAdapter(runSession,layouts);
        rv.setAdapter(adapter);


        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.ViewHolder>{

       private final RunSession session;
       private final int[] dataSetTypes;

        StatsAdapter(RunSession session, int[] dataSetTypes){
            this.session = session;
            this.dataSetTypes = dataSetTypes;
            Log.e("TEST: ", Integer.toString(dataSetTypes.length));
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v;

            if (viewType == GEN_STATS){
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardgeneralstats_layout, parent, false);
                return new GenStatisticsViewHolder(v);
            }
            return null;
        }


        @Override
        public void onBindViewHolder(ViewHolder holder,final int position) {
            Log.e("Statistics: ", "GenStats Loaded");
            if (holder.getItemViewType() == GEN_STATS){
                GenStatisticsViewHolder h = (GenStatisticsViewHolder) holder;
                int dist = session.getDistance();
                if(dist > 1000){
                    float d = dist/1000;
                    h.distance.setText("Distance: "+ Float.toString(d) +"km");
                }else{
                    h.distance.setText("Distance: "+ Integer.toString(dist) +"m");
                }
                long time = Long.parseLong(session.getTime());
                int secs = (int) time / 1000;
                int hours = secs / 3600;
                secs = secs % 3600;
                int mins = secs / 60;
                secs = secs % 60;
                String runningTime = hours + ":" + mins + ":" + secs;
                h.time.setText("Running Time: " + runningTime);
                h.steps.setText("Steps: " + Integer.toString(session.getSteps()));
            }

        }

        @Override
        public int getItemCount() {

            return dataSetTypes.length;
        }

        @Override
        public int getItemViewType(int position) {
            return dataSetTypes[position];
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View v) {
                super(v);
            }
        }

        private class GenStatisticsViewHolder extends ViewHolder {
            TextView distance;
            TextView time;
            TextView steps;

            GenStatisticsViewHolder(View itemView) {
                super(itemView);
                distance = (TextView)itemView.findViewById(R.id.genStats_dist);
                time = (TextView)itemView.findViewById(R.id.genStats_time);
                steps = (TextView)itemView.findViewById(R.id.genStats_steps);

            }
        }

    }
}
