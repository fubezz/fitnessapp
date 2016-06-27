package com.fubezz.fitnessapp;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fubezz.fitnessapp.model.DbHandler;
import com.fubezz.fitnessapp.model.RunSession;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;


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
    private static final int MIN_PER_KM_STATS = 1;
    private static final int ALTITUDE_STATS = 2;
    private static final int SPEED_STATS = 3;


    private static final int[] layouts = {GEN_STATS, MIN_PER_KM_STATS, SPEED_STATS};


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


        StatsAdapter adapter = new StatsAdapter(runSession,layouts,this.getContext());
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
       private final Context context;

        StatsAdapter(RunSession session, int[] dataSetTypes, Context context){
            this.session = session;
            this.dataSetTypes = dataSetTypes;
            this.context = context;
            Log.e("TEST: ", Integer.toString(dataSetTypes.length));
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v;

            if (viewType == GEN_STATS){
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_generalstats, parent, false);
                return new GenStatisticsViewHolder(v);
            }else if (viewType == MIN_PER_KM_STATS){
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_linechart, parent, false);
                return new MinPerKMStatisticsViewHolder(v);
            }else if (viewType == SPEED_STATS){
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_linechart, parent, false);
                return new SpeedStatisticsViewHolder(v);
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
                    double d = Math.round(100.0 * (dist/1000.0)) / 100.0;
                    h.distance.setText("Distance: "+ Double.toString(d) +"km");
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
            }else if(holder.getItemViewType() == MIN_PER_KM_STATS){

                List<Location> locs = session.getListofLocations();
                if (locs != null && locs.size() > 0){

                    MinPerKMStatisticsViewHolder h = (MinPerKMStatisticsViewHolder) holder;
                    h.headline.setText("Pace");
                    float minutesPerKM = 0;
                    double fullDist = 0;
                    Location lastLoc = locs.get(0);
                    int dist = 0;
                    long time = locs.get(0).getTime();
                    List<Entry> minPerKMList = new ArrayList<>();
                    int counter = 1;
                    for (int i = 0; i < locs.size(); i++){
                        Location l = locs.get(i);
                        if(i > 0){
                            dist += l.distanceTo(lastLoc);
                            fullDist += l.distanceTo(lastLoc);
                            lastLoc = l;
                            if (dist >= 1000 || i == locs.size()-1){
                                long kmTime = l.getTime() - time;
                                float val =  (kmTime/60000.0f);
                                if (i == locs.size()-1){
                                    //round to Km
                                    float help = 1.0f/(dist / 1000.0f);
                                    val *= help;
                                }
                                minutesPerKM += val;

                                minPerKMList.add(new Entry(val, counter));
                                counter++;
                                dist = 0;
                                time = l.getTime();
                            }
                            //Log.v("FullDistance " + Integer.toString(i), Double.toString(fullDist));

                        }
                    }
                    float mean = Math.round(minutesPerKM/minPerKMList.size() * 100.0f) / 100.0f;
                    h.meanVelo.setText("Ø: " + Float.toString(mean) + " min/km");



                    LineDataSet set = new LineDataSet(minPerKMList,"Minutes per Kilometer");
                    set.setAxisDependency(YAxis.AxisDependency.LEFT);


                    set.setLineWidth(1.75f);
                    set.setCircleRadius(5f);
                    set.setCircleHoleRadius(2.5f);
                    set.setColor(Color.BLUE);
                    set.setCircleColor(Color.BLUE);
                    set.setHighLightColor(Color.BLUE);
                    set.setDrawValues(true);
                    set.setDrawCubic(true);

                    ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                    dataSets.add(set);
                    ArrayList<String> xVals = new ArrayList<String>();
                    for (int i = 0; i < minPerKMList.size()+2; i++){
                        String v = Integer.toString(i) + ". km";
                        xVals.add(v);
                    }
                    LineData data = new LineData(xVals, dataSets);

                    Log.v("ListOfVelos: ", Integer.toString(minPerKMList.size()));
                    h.plot.getAxisLeft().setInverted(true);
                    h.plot.setDrawGridBackground(false);
                    h.plot.getAxisRight().setEnabled(false);
                    h.plot.setDescription("x: km, y: min/km");
                    h.plot.setData(data);
                    h.plot.invalidate();



                }


            }else if (holder.getItemViewType() == SPEED_STATS){
                List<Location> locs = session.getListofLocations();
                if (locs != null && locs.size() > 0) {
                    Location start = locs.get(0);
                    SpeedStatisticsViewHolder h = (SpeedStatisticsViewHolder) holder;
                    h.headline.setText("Speed");
                    List<Entry> speedList = new ArrayList<>();
                    float meanSpead = 0.0f;
                    for (int i = 0; i < locs.size();i++){
                        Location l = locs.get(i);
                        if(l.getSpeed() > Float.MIN_VALUE){
                            float speed = l.getSpeed() * 3.6f;
                            meanSpead += speed;
                            Entry e = new Entry(speed, i);
                            speedList.add(e);
                        }
                    }
                    meanSpead /= speedList.size();
                    meanSpead = Math.round(meanSpead * 100.0f) / 100.0f;
                    h.meanSpeed.setText("Ø: " + meanSpead + " km/h");
                    LineDataSet set = new LineDataSet(speedList,"Velocity in km/h");
                    set.setAxisDependency(YAxis.AxisDependency.LEFT);

                    set.setLineWidth(1.75f);
                    set.setCircleRadius(0.0f);
                    set.setCircleHoleRadius(0.0f);
                    set.setColor(Color.BLUE);
                    set.setCircleColor(Color.BLUE);
                    set.setHighLightColor(Color.BLUE);
                    //set.setDrawValues(true);
                    set.setDrawCubic(true);
                    set.setDrawFilled(true);

                    ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                    dataSets.add(set);
                    ArrayList<String> xVals = new ArrayList<String>();
                    double dist = 0;
                    Location lastLoc = locs.get(0);
                    for (int i = 0; i < locs.size(); i++){
                        dist += (locs.get(i).distanceTo(lastLoc) / 1000.0);
                        lastLoc = locs.get(i);
                        dist = Math.round(dist * 100) / 100.0;
                        String v = Double.toString(dist) + " km";
                        xVals.add(v);
                    }
                    LineData data = new LineData(xVals, dataSets);

                    h.plot.setDrawGridBackground(false);
                    h.plot.getAxisRight().setEnabled(false);
                    h.plot.setDescription("x: km, y: km/h");
                    h.plot.setData(data);
                    h.plot.invalidate();

                }
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

            public GenStatisticsViewHolder(View itemView) {
                super(itemView);
                distance = (TextView)itemView.findViewById(R.id.genStats_dist);
                time = (TextView)itemView.findViewById(R.id.genStats_time);
                steps = (TextView)itemView.findViewById(R.id.genStats_steps);

            }
        }

        private class MinPerKMStatisticsViewHolder extends ViewHolder{
            TextView meanVelo;
            TextView headline;
            LineChart plot;

            public MinPerKMStatisticsViewHolder(View itemView){
                super(itemView);
                meanVelo = (TextView)itemView.findViewById(R.id.card_linechart_mean);
                plot = (LineChart) itemView.findViewById(R.id.card_linechart_chart);
                headline = (TextView) itemView.findViewById(R.id.card_linechart_headline);
            }

        }

        private class SpeedStatisticsViewHolder extends ViewHolder{
            LineChart plot;
            TextView meanSpeed;
            TextView headline;

            public SpeedStatisticsViewHolder(View itemView){
                super(itemView);
                plot = (LineChart) itemView.findViewById(R.id.card_linechart_chart);
                meanSpeed = (TextView) itemView.findViewById(R.id.card_linechart_mean);
                headline = (TextView) itemView.findViewById(R.id.card_linechart_headline);
            }
        }

    }
}
