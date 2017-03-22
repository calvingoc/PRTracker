package online.cagocapps.prtracker;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;

import online.cagocapps.prtracker.Data.ProfileContract;
import online.cagocapps.prtracker.Data.ProfileDBHelper;

public class ViewResults extends AppCompatActivity {
    private Spinner spinActivity;
    private Spinner spinCategory;
    private XYPlot plotResultsGraph;
    private RecyclerView recyclerViewResults;

    private ViewResultsRecycAdapter mainAdapter;

    private ProfileDBHelper dbHelper;
    private SQLiteDatabase dbRead;


    private String tableName;
    private int userID;
    private float units;
    private boolean plank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_results);

        //find views;
        spinActivity = (Spinner) findViewById(R.id.vr_spin_activity);
        spinCategory = (Spinner) findViewById(R.id.vr_spin_category);
        plotResultsGraph = (XYPlot) findViewById(R.id.vr_graph);
        recyclerViewResults = (RecyclerView) findViewById(R.id.vr_rv_results);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        recyclerViewResults.setLayoutManager(layoutManager);
        recyclerViewResults.setHasFixedSize(true);
        mainAdapter = new ViewResultsRecycAdapter();
        recyclerViewResults.setAdapter(mainAdapter);
        dbHelper = new ProfileDBHelper(this);
        dbRead = dbHelper.getReadableDatabase();

        userID = PreferenceManager.getDefaultSharedPreferences(this).getInt(getString(R.string.sp_userID), 1);
        units = PreferenceManager.getDefaultSharedPreferences(this).getFloat(getString(R.string.sp_units), 1);

        spinCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<String> arrayAdapter;
                switch (position) {
                    case 0:
                        tableName = ProfileContract.BarbellLifts.TABLE_NAME;
                        arrayAdapter =
                                new ArrayAdapter<String>(view.getContext(),
                                        android.R.layout.simple_spinner_item,
                                        getResources().getStringArray(R.array.barbell_lifts_array));
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinActivity.setAdapter(arrayAdapter);
                        spinActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                weightBased(ProfileContract.BarbellLifts.ADJUSTED_ONE_REP_MAX);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        break;
                    case 1:
                        tableName = ProfileContract.DumbbellLifts.TABLE_NAME;
                        arrayAdapter =
                                new ArrayAdapter<String>(view.getContext(),
                                        android.R.layout.simple_spinner_item,
                                        getResources().getStringArray(R.array.dumbbell_lifts_array));
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinActivity.setAdapter(arrayAdapter);
                        spinActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                weightBased(ProfileContract.BarbellLifts.ADJUSTED_ONE_REP_MAX);
                                plank = false;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        break;
                    case 2:
                        tableName = ProfileContract.Gymnastics.TABLE_NAME;
                        arrayAdapter =
                                new ArrayAdapter<String>(view.getContext(),
                                        android.R.layout.simple_spinner_item,
                                        getResources().getStringArray(R.array.bodyweight_array));
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinActivity.setAdapter(arrayAdapter);
                        spinActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                if (i == 16) {
                                    weightBased(ProfileContract.Running.TIME);
                                    plank = true;
                                }
                                else{
                                    weightBased(ProfileContract.BarbellLifts.REPS);
                                    plank = false;
                                }

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        break;
                    case 3:
                        tableName = ProfileContract.Running.TABLE_NAME;
                        arrayAdapter =
                                new ArrayAdapter<String>(view.getContext(),
                                        android.R.layout.simple_spinner_item,
                                        getResources().getStringArray(R.array.running_array));
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinActivity.setAdapter(arrayAdapter);
                        spinActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                weightBased(ProfileContract.Running.TIME);
                                plank = false;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        break;
                    case 4:
                        tableName = ProfileContract.Swimming.TABLE_NAME;
                        arrayAdapter =
                                new ArrayAdapter<String>(view.getContext(),
                                        android.R.layout.simple_spinner_item,
                                        getResources().getStringArray(R.array.swimming_array));
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinActivity.setAdapter(arrayAdapter);
                        spinActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                weightBased(ProfileContract.Running.TIME);
                                plank = false;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        break;
                    case 5:
                        tableName = ProfileContract.CrossFitStandards.TABLE_NAME;
                        arrayAdapter =
                                new ArrayAdapter<String>(view.getContext(),
                                        android.R.layout.simple_spinner_item,
                                        getResources().getStringArray(R.array.crossfit_girls_array));
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinActivity.setAdapter(arrayAdapter);
                        spinActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                if (i == 0 || i == 2 || i == 3 || i == 4
                                        || i == 5 || i == 6 || i == 7 || i == 8 || i == 9)
                                    weightBased(ProfileContract.Running.TIME);
                                else weightBased(ProfileContract.BarbellLifts.REPS);
                                plank = false;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        break;
                    case 6:
                        arrayAdapter =
                                new ArrayAdapter<String>(view.getContext(),
                                        android.R.layout.simple_spinner_item,
                                        getResources().getStringArray(R.array.crossfit_heroes_array));
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinActivity.setAdapter(arrayAdapter);
                        spinActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                if (i == 0 || i == 1 || i == 2 || i == 3 || i == 4 || i == 6)
                                    weightBased(ProfileContract.Running.TIME);
                                else weightBased(ProfileContract.BarbellLifts.REPS);
                                plank = false;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {}
                        });
                        tableName = ProfileContract.CrossFitStandards.TABLE_NAME;
                        break;
                    case 7:
                        tableName = ProfileContract.CrossFitStandards.TABLE_NAME;
                        arrayAdapter =
                                new ArrayAdapter<String>(view.getContext(),
                                        android.R.layout.simple_spinner_item,
                                        getResources().getStringArray(R.array.crossfit_open_array));
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinActivity.setAdapter(arrayAdapter);
                        spinActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                if (i == 14 || i == 18 || i == 23 || i == 25)
                                    weightBased(ProfileContract.Running.TIME);
                                else weightBased(ProfileContract.BarbellLifts.REPS);
                                plank = false;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        break;
                }
                spinActivity.setEnabled(true);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinActivity.setEnabled(false);

            }
        });
    }

    private void weightBased(String compareColumn){
        Cursor cursor = dbRead.query(
                tableName,
                null,
                ProfileContract.BarbellLifts.LIFT + " = ? AND " + ProfileContract.BarbellLifts.USER_ID + " = ?",
                new String[]{spinActivity.getSelectedItem().toString(), Integer.toString(userID)},
                null,
                null,
                null
        );
        Number[] graphResults = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        Number[] allResults = new Number[cursor.getCount()];
        Long[] dates = new Long[cursor.getCount()];
        int[] pr = new int[cursor.getCount()];
        int[] resultID = new int[cursor.getCount()];
        int[] sets = new int[cursor.getCount()];
        int[] reps = new int[cursor.getCount()];
        int i = 0;
        Number min = 10000000;
        Number max = 0;
        if(cursor.moveToLast()){
            graphResults[9-i] = cursor.getInt(cursor.getColumnIndex(compareColumn));
            allResults[i] = cursor.getInt(cursor.getColumnIndex(compareColumn));
            dates[i] = cursor.getLong(cursor.getColumnIndex(ProfileContract.BarbellLifts.DATE));
            pr[i] = cursor.getInt(cursor.getColumnIndex(ProfileContract.BarbellLifts.PR));
            resultID[i] = cursor.getInt(cursor.getColumnIndex(ProfileContract.BarbellLifts._ID));
            if (compareColumn.equals(ProfileContract.BarbellLifts.ADJUSTED_ONE_REP_MAX)){
                graphResults[9 - i] = graphResults[9 - i].floatValue() * units;
                allResults[i] = cursor.getInt(cursor.getColumnIndex(ProfileContract.BarbellLifts.WEIGHT)) * units;
                sets[i] = cursor.getInt(cursor.getColumnIndex(ProfileContract.BarbellLifts.ROUNDS));
                reps[i] = cursor.getInt(cursor.getColumnIndex(ProfileContract.BarbellLifts.REPS));
            }
            if (graphResults[i].doubleValue() > max.doubleValue()) max = allResults[i];
            if (graphResults[i].doubleValue() < min.doubleValue()) min = allResults[i];
            i++;
        }
        while (cursor.moveToPrevious()){
            if (i < 10) graphResults[9-i] = cursor.getInt(cursor.getColumnIndex(compareColumn));
            allResults[i] = cursor.getInt(cursor.getColumnIndex(compareColumn));
            dates[i] = cursor.getLong(cursor.getColumnIndex(ProfileContract.BarbellLifts.DATE));
            pr[i] = cursor.getInt(cursor.getColumnIndex(ProfileContract.BarbellLifts.PR));
            resultID[i] = cursor.getInt(cursor.getColumnIndex(ProfileContract.BarbellLifts._ID));
            if (compareColumn.equals(ProfileContract.BarbellLifts.ADJUSTED_ONE_REP_MAX)){
                if (i < 10) graphResults[9 - i] = graphResults[9 - i].floatValue() * units;
                allResults[i] = allResults[i].floatValue() * units;
                sets[i] = cursor.getInt(cursor.getColumnIndex(ProfileContract.BarbellLifts.ROUNDS));
                reps[i] = cursor.getInt(cursor.getColumnIndex(ProfileContract.BarbellLifts.REPS));
            }
            if (graphResults[i].doubleValue() > max.doubleValue()) max = allResults[i];
            if (graphResults[i].doubleValue() < min.doubleValue()) min = allResults[i];
            i++;
        }
        setUpGraph(min, max, graphResults);
        mainAdapter.setVariables(allResults, dates, pr, resultID, reps, sets, compareColumn);
        mainAdapter.notifyDataSetChanged();

    }


    private void setUpGraph(Number min, Number max, final Number[] results){
        max = max.doubleValue() + (max.doubleValue()/10.0);
        min = min.doubleValue() - (min.doubleValue()/10);
        final Number[] domainLabels = {1, 2, 3, 6, 7, 8, 9, 10, 13, 14};
        XYSeries resultsSeries = new SimpleXYSeries(
                Arrays.asList(results), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Last Ten Results");
        LineAndPointFormatter resultsFormat = new LineAndPointFormatter(this, R.xml.line_poit_formatter_with_labels);
        resultsFormat.getLinePaint().setPathEffect(new DashPathEffect(new float[]{
                PixelUtils.dpToPix(10),
                PixelUtils.dpToPix(5)}, 0
        ));
        plotResultsGraph.addSeries(resultsSeries, resultsFormat);
        plotResultsGraph.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM)
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
        plotResultsGraph.setRangeBoundaries(min, max, BoundaryMode.FIXED);
    }
}
