package online.cagocapps.prtracker;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.DashPathEffect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import online.cagocapps.prtracker.Data.ProfileContract;
import online.cagocapps.prtracker.Data.ProfileDBHelper;
import online.cagocapps.prtracker.Data.ResultObject;

/**
 * sets up and populates the main activity's recycler view
 * Created by cgehredo on 3/16/2017.
 */

public class MainActivityRecycAdapter extends RecyclerView.Adapter<MainActivityRecycAdapter.mainActivityRecycAdapterViewHolder> {

    //database vars
    private ProfileDBHelper dbHelper;
    private SQLiteDatabase dbRead;
    //display vars
    private String userID;
    private String tableName;
    private String resultID;
    private Context context;
    private float units;

    private final MainActivityRecycAdapterOnClickHandler mClickHandler;

    /**
     * handle view getting clicked, passes the tablename and activity back to MainActivity.class
     */
    public  interface MainActivityRecycAdapterOnClickHandler{
        void onClick(String tableName, String activity);
    }

    /**
     * localize click handler
     * @param clickHandler click handler to localize
     */
    public MainActivityRecycAdapter(MainActivityRecycAdapterOnClickHandler clickHandler){
        mClickHandler = clickHandler;
    }

    /**
     * links variables to views and sets up on click listener
     */
    public class mainActivityRecycAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final TextView tvActivity;
        public final ImageView ivPR;
        public final TextView tvResults;
        public final XYPlot plotResultsGraph;
        public final TextView tvNotes;
        public final TextView tvPercentile;
        public final TextView tvTableName;

        /**
         * sets up on click listener for entire view
         * listener takes the table and activity for the result
         * @param view view that gets the listener
         */
        @Override
        public void onClick(View view) {

            mClickHandler.onClick(tvTableName.getText().toString(), tvActivity.getText().toString());
        }

        /**
         * links vars to views
         * @param itemView view that holds the layout
         */
        public mainActivityRecycAdapterViewHolder(View itemView) {
            super(itemView);
            tvActivity = (TextView) itemView.findViewById(R.id.mai_tv_activity);
            ivPR = (ImageView) itemView.findViewById(R.id.mai_iv_pr);
            tvResults = (TextView) itemView.findViewById(R.id.mai_tv_results);
            plotResultsGraph = (XYPlot) itemView.findViewById(R.id.mai_plot_results);
            tvNotes = (TextView) itemView.findViewById(R.id.mai_tv_notes);
            tvPercentile = (TextView) itemView.findViewById(R.id.mai_tv_percentile_val);
            tvTableName = (TextView) itemView.findViewById(R.id.mai_tv_tablename);
            itemView.setOnClickListener(this);


        }
    }

    /**
     * sets up varibles to use in the view and inflates each item
     * @param parent parent view
     * @param viewType not used
     * @return
     */
    @Override
    public mainActivityRecycAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        dbHelper = new ProfileDBHelper(context);
        dbRead = dbHelper.getReadableDatabase();
        int layoutIdForListItem = R.layout.main_activity_result_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new mainActivityRecycAdapterViewHolder(view);
    }

    /**
     * sets up each item with the correct results
     * @param holder current view
     * @param position where in the list of results we are
     */
    @Override
    public void onBindViewHolder(final mainActivityRecycAdapterViewHolder holder, int position) {
        if (userID != null) {
            Cursor recentCursor = dbRead.query(//find user's recent results
                    ProfileContract.RecentLifts.TABLE_NAME,
                    null,
                    ProfileContract.RecentLifts._ID + " = ?",
                    new String[]{userID},
                    null,
                    null,
                    null
            );
            if (recentCursor.moveToFirst()) {
                switch (position) {//pull in the correct results based off of position
                    case 0:
                        tableName = ProfileContract.RecentLifts.RESULT_ONE_TABLE;
                        resultID = ProfileContract.RecentLifts.RESULT_ONE_ID;
                        break;
                    case 1:
                        tableName = ProfileContract.RecentLifts.RESULT_TWO_TABLE;
                        resultID = ProfileContract.RecentLifts.RESULT_TWO_ID;
                        break;
                    case 2:
                        tableName = ProfileContract.RecentLifts.RESULT_THREE_TABLE;
                        resultID = ProfileContract.RecentLifts.RESULT_THREE_ID;
                        break;
                    case 3:
                        tableName = ProfileContract.RecentLifts.RESULT_FOUR_TABLE;
                        resultID = ProfileContract.RecentLifts.RESULT_FOUR_ID;
                        break;
                    case 4:
                        tableName = ProfileContract.RecentLifts.RESULT_FIVE_TABLE;
                        resultID = ProfileContract.RecentLifts.RESULT_FIVE_ID;
                        break;
                }

                tableName = recentCursor.getString(recentCursor.getColumnIndex(tableName));
                resultID = recentCursor.getString(recentCursor.getColumnIndex(resultID));
                holder.tvTableName.setText(tableName);
                recentCursor.close();
                if (tableName != null && tableName.length()!= 0 && !tableName.equals("")) {//finds actual results
                    Cursor thisResult = dbRead.query(
                            tableName,
                            null,
                            ProfileContract.BarbellLifts._ID + " = ?",
                            new String[]{resultID},
                            null,
                            null,
                            null
                    );
                    thisResult.moveToFirst();//finds result and formats it correctly
                    final String activity = thisResult.getString(thisResult.getColumnIndex(ProfileContract.BarbellLifts.LIFT));
                    holder.tvActivity.setText(activity);
                    if(thisResult.getString(thisResult.getColumnIndex(ProfileContract.BarbellLifts.COMMENTS)).length() != 0){
                        holder.tvNotes.setText(thisResult.getString(thisResult.getColumnIndex(ProfileContract.BarbellLifts.COMMENTS)));
                    } else holder.tvNotes.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    if (thisResult.getInt(thisResult.getColumnIndex(ProfileContract.BarbellLifts.PR)) == 1) {//show logo if a PR
                        holder.ivPR.setVisibility(View.VISIBLE);
                    } else holder.ivPR.setVisibility(View.INVISIBLE);
                    if (tableName.equals(ProfileContract.BarbellLifts.TABLE_NAME) || tableName.equals(ProfileContract.DumbbellLifts.TABLE_NAME)) {
                        String sets = thisResult.getString(thisResult.getColumnIndex(ProfileContract.BarbellLifts.ROUNDS));
                        String reps = thisResult.getString(thisResult.getColumnIndex(ProfileContract.BarbellLifts.REPS));
                        float weight = thisResult.getFloat(thisResult.getColumnIndex(ProfileContract.BarbellLifts.WEIGHT));
                        holder.tvResults.setText(sets + " X " + reps + " X " + Float.toString(weight * units));
                    } else if (tableName.equals(ProfileContract.CrossFitStandards.TABLE_NAME)) {
                        String rx = "";
                        if (thisResult.getInt(thisResult.getColumnIndex(ProfileContract.CrossFitStandards.RX)) == 1) {
                            rx = " Rx";
                        }
                        if (thisResult.getString(thisResult.getColumnIndex(ProfileContract.CrossFitStandards.TIME)) == null) {
                            String sets = thisResult.getString(thisResult.getColumnIndex(ProfileContract.BarbellLifts.ROUNDS));
                            String reps = thisResult.getString(thisResult.getColumnIndex(ProfileContract.BarbellLifts.REPS));
                            holder.tvResults.setText(sets + " Rounds " + reps + " Reps" + rx);
                        } else {
                            String reps = thisResult.getString(thisResult.getColumnIndex(ProfileContract.BarbellLifts.REPS));
                            int totalTime = thisResult.getInt(thisResult.getColumnIndex(ProfileContract.CrossFitStandards.TIME));
                            String hours = Integer.toString(totalTime / 3600) + ":";
                            String minutes = Integer.toString((totalTime % 3600) / 60) + ":";
                            String seconds = Integer.toString(((totalTime % 3600) % 60));
                            if (seconds.equals("0")) seconds = "00";
                            String result = minutes + seconds + rx;
                            if (!hours.equals("0:")) result = hours + result;
                            if (!reps.equals("1")) result = reps + " Reps in " + result;
                            holder.tvResults.setText(result);
                        }
                    } else if (tableName.equals(ProfileContract.Gymnastics.TABLE_NAME)) {
                        if (thisResult.getString(thisResult.getColumnIndex(ProfileContract.Gymnastics.TIME)) == null) {
                            String sets = thisResult.getString(thisResult.getColumnIndex(ProfileContract.Gymnastics.ROUNDS));
                            String reps = thisResult.getString(thisResult.getColumnIndex(ProfileContract.Gymnastics.REPS));
                            holder.tvResults.setText(sets + " X " + reps);
                        } else {
                            int totalTime = thisResult.getInt(thisResult.getColumnIndex(ProfileContract.CrossFitStandards.TIME));
                            String hours = Integer.toString(totalTime / 3600) + ":";
                            String minutes = Integer.toString((totalTime % 3600) / 60) + ":";
                            String seconds = Integer.toString(((totalTime % 3600) % 60));
                            if (seconds.equals("0")) seconds = "00";
                            String result = minutes + seconds;
                            if (!hours.equals("0:")) result = hours + result;
                            holder.tvResults.setText(result);
                        }
                    } else {
                        int totalTime = thisResult.getInt(thisResult.getColumnIndex(ProfileContract.CrossFitStandards.TIME));
                        String hours = Integer.toString(totalTime / 3600) + ":";
                        String minutes = Integer.toString((totalTime % 3600) / 60) + ":";
                        String seconds = Integer.toString(((totalTime % 3600) % 60));
                        if (seconds.equals("0")) seconds = "00";
                        String result = minutes + seconds;
                        if (!hours.equals("0:")) result = hours + result;
                        holder.tvResults.setText(result);
                    }
                    thisResult.close();
                    Cursor graphCursor = dbRead.query(//finds the last ten results of this lift to graph
                            tableName,
                            null,
                            ProfileContract.BarbellLifts.LIFT + " = ?",
                            new String[]{activity},
                            null,
                            null,
                            null
                    );
                    Number[] results = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                    for (int i = 0; i < 10; i++) {//set up graph arrays
                        if (i == 0){
                            if(graphCursor.moveToLast()) {
                                if (tableName.equals(ProfileContract.BarbellLifts.TABLE_NAME) || tableName.equals(ProfileContract.DumbbellLifts.TABLE_NAME)) {
                                    results[9 - i] = graphCursor.getInt(graphCursor.getColumnIndex(ProfileContract.BarbellLifts.ADJUSTED_ONE_REP_MAX));
                                } else if (tableName.equals(ProfileContract.CrossFitStandards.TABLE_NAME)) {
                                    if (graphCursor.getString(graphCursor.getColumnIndex(ProfileContract.CrossFitStandards.TIME)) == null) {
                                        double reps = graphCursor.getDouble(graphCursor.getColumnIndex(ProfileContract.CrossFitStandards.REPS));
                                        results[9 - i] = reps;
                                    } else {
                                        results[9 - i] = graphCursor.getInt(graphCursor.getColumnIndex(ProfileContract.CrossFitStandards.TIME));
                                    }
                                } else if (tableName.equals(ProfileContract.Gymnastics.TABLE_NAME)) {
                                    if (graphCursor.getString(graphCursor.getColumnIndex(ProfileContract.Gymnastics.TIME)) == null) {
                                        results[9 - i] = graphCursor.getInt(graphCursor.getColumnIndex(ProfileContract.Gymnastics.REPS));
                                    } else
                                        results[9 - i] = graphCursor.getInt(graphCursor.getColumnIndex(ProfileContract.Gymnastics.TIME));
                                } else {
                                    results[9 - i] = graphCursor.getInt(graphCursor.getColumnIndex(ProfileContract.Gymnastics.TIME));
                                }
                            }
                        }
                        else {if (graphCursor.moveToPrevious()) {
                                if (tableName.equals(ProfileContract.BarbellLifts.TABLE_NAME) || tableName.equals(ProfileContract.DumbbellLifts.TABLE_NAME)) {
                                    results[9 - i] = graphCursor.getInt(graphCursor.getColumnIndex(ProfileContract.BarbellLifts.ADJUSTED_ONE_REP_MAX));
                                } else if (tableName.equals(ProfileContract.CrossFitStandards.TABLE_NAME)) {
                                    if (graphCursor.getString(graphCursor.getColumnIndex(ProfileContract.CrossFitStandards.TIME)) == null) {
                                        double reps = graphCursor.getDouble(graphCursor.getColumnIndex(ProfileContract.CrossFitStandards.REPS));
                                        double rounds = graphCursor.getDouble(graphCursor.getColumnIndex(ProfileContract.CrossFitStandards.ROUNDS));
                                        while (reps > 1) reps = reps / 10;
                                        results[9 - i] = reps + rounds;
                                    } else {
                                        results[9 - i] = graphCursor.getInt(graphCursor.getColumnIndex(ProfileContract.CrossFitStandards.TIME));
                                    }
                                } else if (tableName.equals(ProfileContract.Gymnastics.TABLE_NAME)) {
                                    if (graphCursor.getString(graphCursor.getColumnIndex(ProfileContract.Gymnastics.TIME)) == null) {
                                        results[9 - i] = graphCursor.getInt(graphCursor.getColumnIndex(ProfileContract.Gymnastics.REPS));
                                    } else
                                        results[9 - i] = graphCursor.getInt(graphCursor.getColumnIndex(ProfileContract.Gymnastics.TIME));
                                } else {
                                    results[9 - i] = graphCursor.getInt(graphCursor.getColumnIndex(ProfileContract.Gymnastics.TIME));
                                }
                            }
                        }
                    }//find min and max results of last ten results
                    Number max = 0;
                    Number min = 1000000;
                    for (Number value : results) {
                        if (max.doubleValue() < value.doubleValue()) max = value;
                        if (min.doubleValue() > value.doubleValue()) min = value;
                    }
                    min = min.doubleValue() - (min.doubleValue()/10);
                    if (min.doubleValue() < 0) min = 0;
                    max = max.doubleValue() + (max.doubleValue()/10.0);
                    graphCursor.close();
                    //set up graph
                    final Number[] domainLabels = {1, 2, 3, 6, 7, 8, 9, 10, 13, 14};
                    XYSeries resultsSeries = new SimpleXYSeries(
                            Arrays.asList(results), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Last Ten Results");
                    LineAndPointFormatter resultsFormat = new LineAndPointFormatter(holder.itemView.getContext(), R.xml.line_poit_formatter_with_labels);
                    resultsFormat.getLinePaint().setPathEffect(new DashPathEffect(new float[]{
                            PixelUtils.dpToPix(10),
                            PixelUtils.dpToPix(5)}, 0
                    ));
                    holder.plotResultsGraph.addSeries(resultsSeries, resultsFormat);
                    holder.plotResultsGraph.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM)
                            .setFormat(new Format() {
                                @Override
                                public StringBuffer format(Object o, @NonNull StringBuffer stringBuffer, @NonNull FieldPosition fieldPosition) {
                                    int i = Math.round(((Number) o).floatValue());
                                    return stringBuffer.append(domainLabels[i]);
                                }

                                @Override
                                public Object parseObject(String s, @NonNull ParsePosition parsePosition) {
                                    return null;
                                }
                            });
                    holder.plotResultsGraph.setRangeBoundaries(min, max, BoundaryMode.FIXED);
                    //read from realtime data base to find what percentile you are in.
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Cursor prCursor = dbRead.query(
                                    ProfileContract.ProfileValues.TABLE_NAME,
                                    null,
                                    ProfileContract.ProfileValues._ID + " = ?",
                                    new String[]{userID},
                                    null,
                                    null,
                                    null
                            );
                            if (prCursor.moveToFirst()) {
                                String birthdate = prCursor.getString(prCursor.getColumnIndex(ProfileContract.ProfileValues.BIRTHDATE));
                                Date now = new Date();
                                int year = now.getYear() + 1900;
                                int age = year - Integer.valueOf(birthdate);
                                String weight = (Integer.toString(prCursor.getInt(prCursor.getColumnIndex(ProfileContract.ProfileValues.WEIGHT))));
                                int scaledWeight = Integer.valueOf(weight) / 10;
                                String yearsAct = (Integer.toString(prCursor.getInt(prCursor.getColumnIndex(ProfileContract.ProfileValues.YEARS_ACTIVE))));
                                int skill = (prCursor.getInt(prCursor.getColumnIndex(ProfileContract.ProfileValues.SKILL)));
                                int gender = (prCursor.getInt(prCursor.getColumnIndex(ProfileContract.ProfileValues.GENDER)));
                                String email = prCursor.getString(prCursor.getColumnIndex(ProfileContract.ProfileValues.EMAIL));
                                email = email.replace(".","");
                                prCursor.close();
                                double pr = 0;
                                double below = 0.0;
                                double total = 0.0;
                                double equal = 0.0;
                                ArrayList<Integer> results = new ArrayList<Integer>();
                                for (DataSnapshot comResult : dataSnapshot.child(activity).child(Integer.toString(gender)).child(Integer.toString(skill)).child(Integer.toString(scaledWeight))
                                        .child(Integer.toString(age)).child(yearsAct).getChildren()) {
                                    ResultObject resultObject = comResult.getValue(ResultObject.class);
                                    results.add(resultObject.getResult());
                                    String curEmail = comResult.getKey();
                                    if (curEmail.equals(email)) {
                                        pr = resultObject.getResult();
                                    }
                                }
                                for (int i : results) {
                                    if (i < pr) {
                                        below++;
                                        total++;
                                    } else if (i == pr) {
                                        equal++;
                                        total++;
                                    } else total++;
                                }
                                holder.tvPercentile.setText(Double.toString((below + .5 * equal * 100) / total));
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
        }
    }

    /**
     * returns number of items in recycler view
     * @return number of items always 5 for recent results
     */
    @Override
    public int getItemCount() {
        return 5;
    }

    /**
     * localizes information needed to set up view
     * @param id current user ID
     * @param units preferred units
     */
    public void setUserID(String id, float units){
        this.units = units;
        userID = id;
    }


}
