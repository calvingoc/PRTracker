package online.cagocapps.prtracker;

import android.app.Application;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Switch;
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

public class ViewResults extends AppCompatActivity implements ViewResultsRecycAdapter.vrRecycAdapOnClickHandler{
    //view vars
    private Spinner spinActivity;
    private Spinner spinCategory;
    private XYPlot plotResultsGraph;
    private RecyclerView recyclerViewResults;
    private TextView prValue;
    private TextView percentileValue;
    private Button percentagesButton;

    private ViewResultsRecycAdapter mainAdapter;

    //database vars
    private ProfileDBHelper dbHelper;
    private SQLiteDatabase dbWrite;

    //utility vars
    private String compareColumn;
    private String tableName;
    private int userID;
    private float units;
    public boolean firstTime = false;
    public int positionAct;
    private double prWeight;

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
        mainAdapter = new ViewResultsRecycAdapter(this);
        recyclerViewResults.setAdapter(mainAdapter);
        dbHelper = new ProfileDBHelper(this);
        dbWrite = dbHelper.getWritableDatabase();
        prValue = (TextView) findViewById(R.id.vr_tv_prvalue);
        percentileValue = (TextView) findViewById(R.id.vr_tv_percentilevalue);
        percentagesButton = (Button) findViewById(R.id.vr_button_percentages);

        userID = PreferenceManager.getDefaultSharedPreferences(this).getInt(getString(R.string.sp_userID), 1);
        units = PreferenceManager.getDefaultSharedPreferences(this).getFloat(getString(R.string.sp_units), 1);

        spinCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {//sets up spinners
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
                                compareColumn = ProfileContract.BarbellLifts.ADJUSTED_ONE_REP_MAX;
                                percentileValue.setText("");
                                prValue.setText("");
                                weightBased();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        if (firstTime){
                            spinActivity.setSelection(positionAct);
                            firstTime = false;
                        }
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
                                compareColumn = ProfileContract.BarbellLifts.ADJUSTED_ONE_REP_MAX;
                                percentileValue.setText("");
                                prValue.setText("");
                                weightBased();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        if (firstTime){
                            spinActivity.setSelection(positionAct);
                            firstTime = false;
                        }
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
                                    compareColumn =ProfileContract.Running.TIME;
                                    percentileValue.setText("");
                                    prValue.setText("");
                                    weightBased();
                                }
                                else{
                                    compareColumn = ProfileContract.BarbellLifts.REPS;
                                    weightBased();
                                }

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        if (firstTime){
                            spinActivity.setSelection(positionAct);
                            firstTime = false;
                        }
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
                                compareColumn = ProfileContract.Running.TIME;
                                percentileValue.setText("");
                                prValue.setText("");
                                weightBased();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        if (firstTime){
                            spinActivity.setSelection(positionAct);
                            firstTime = false;
                        }
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
                                compareColumn = ProfileContract.Running.TIME;
                                percentileValue.setText("");
                                prValue.setText("");
                                weightBased();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        if (firstTime){
                            spinActivity.setSelection(positionAct);
                            firstTime = false;
                        }
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
                                        || i == 5 || i == 6 || i == 7 || i == 8 || i == 9){
                                    compareColumn = ProfileContract.Running.TIME;
                                    percentileValue.setText("");
                                    prValue.setText("");
                                    weightBased();
                                }else {
                                    compareColumn = ProfileContract.BarbellLifts.REPS;
                                    percentileValue.setText("");
                                    prValue.setText("");
                                    weightBased();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        if (firstTime){
                            spinActivity.setSelection(positionAct);
                            firstTime = false;
                        }
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
                                if (i == 0 || i == 1 || i == 2 || i == 3 || i == 4 || i == 6) {
                                    compareColumn = ProfileContract.Running.TIME;
                                    percentileValue.setText("");
                                    prValue.setText("");
                                    weightBased();
                                }else {
                                    compareColumn = ProfileContract.BarbellLifts.REPS;
                                    percentileValue.setText("");
                                    prValue.setText("");
                                    weightBased();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {}
                        });
                        if (firstTime){
                            spinActivity.setSelection(positionAct);
                            firstTime = false;
                        }
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
                                if (i == 14 || i == 18 || i == 23 || i == 25) {
                                    compareColumn = ProfileContract.Running.TIME;
                                    percentileValue.setText("");
                                    prValue.setText("");
                                    weightBased();
                                }else {
                                    compareColumn = ProfileContract.BarbellLifts.REPS;
                                    percentileValue.setText("");
                                    prValue.setText("");
                                    weightBased();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        if (firstTime){
                            spinActivity.setSelection(positionAct);
                            firstTime = false;
                        }
                        break;
                }
                spinActivity.setEnabled(true);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinActivity.setEnabled(false);

            }
        });
        String activity = getIntent().getStringExtra(getString(R.string.ar_tv_activity));
        String tableName = getIntent().getStringExtra(getString(R.string.ar_tv_category));
        if (tableName != null && activity != null) { //open to correct results if opened from main activity recycler view
            ArrayAdapter<String> arrayAdapter;
            switch (tableName){
                case ProfileContract.BarbellLifts.TABLE_NAME:
                    spinCategory.setSelection(0);
                    arrayAdapter =
                            new ArrayAdapter<String>(this,
                                    android.R.layout.simple_spinner_item,
                                    getResources().getStringArray(R.array.barbell_lifts_array));
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinActivity.setAdapter(arrayAdapter);
                    break;
                case ProfileContract.DumbbellLifts.TABLE_NAME:
                    spinCategory.setSelection(1);
                    arrayAdapter =
                            new ArrayAdapter<String>(this,
                                    android.R.layout.simple_spinner_item,
                                    getResources().getStringArray(R.array.dumbbell_lifts_array));
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinActivity.setAdapter(arrayAdapter);
                    break;
                case ProfileContract.Gymnastics.TABLE_NAME:
                    spinCategory.setSelection(2);
                    arrayAdapter =
                            new ArrayAdapter<String>(this,
                                    android.R.layout.simple_spinner_item,
                                    getResources().getStringArray(R.array.bodyweight_array));
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinActivity.setAdapter(arrayAdapter);
                    break;
                case ProfileContract.Running.TABLE_NAME:
                    spinCategory.setSelection(3);
                    arrayAdapter =
                            new ArrayAdapter<String>(this,
                                    android.R.layout.simple_spinner_item,
                                    getResources().getStringArray(R.array.running_array));
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinActivity.setAdapter(arrayAdapter);
                    break;
                case ProfileContract.Swimming.TABLE_NAME:
                    spinCategory.setSelection(4);
                    arrayAdapter =
                            new ArrayAdapter<String>(this,
                                    android.R.layout.simple_spinner_item,
                                    getResources().getStringArray(R.array.swimming_array));
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinActivity.setAdapter(arrayAdapter);
                    break;
                case ProfileContract.CrossFitStandards.TABLE_NAME:
                    spinCategory.setSelection(7);
                    arrayAdapter =
                            new ArrayAdapter<String>(this,
                                    android.R.layout.simple_spinner_item,
                                    getResources().getStringArray(R.array.crossfit_open_array));
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinActivity.setAdapter(arrayAdapter);
                    String[] selections = getResources().getStringArray(R.array.crossfit_girls_array);
                    for (int i = 0; i > selections.length; i++){
                        if (selections[i].equals(activity)){
                            spinCategory.setSelection(5);
                            arrayAdapter =
                                    new ArrayAdapter<String>(this,
                                            android.R.layout.simple_spinner_item,
                                            getResources().getStringArray(R.array.crossfit_girls_array));
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinActivity.setAdapter(arrayAdapter);
                        }
                    }
                    selections = getResources().getStringArray(R.array.crossfit_heroes_array);

                    for (int i = 0; i > selections.length; i++){
                        if (selections[i].equals(activity)){
                            spinCategory.setSelection(6);
                            arrayAdapter =
                                    new ArrayAdapter<String>(this,
                                            android.R.layout.simple_spinner_item,
                                            getResources().getStringArray(R.array.crossfit_heroes_array));
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinActivity.setAdapter(arrayAdapter);
                        }
                    }
                    break;
            }

            ArrayAdapter myAdap = (ArrayAdapter) spinActivity.getAdapter();
            positionAct = myAdap.getPosition(activity);
            firstTime = true;
        }
    }

    /**
     * somewhat poorly named activity that actually finds the results for the selected activity and populates the recycler view
     */
    private void weightBased(){
        if (compareColumn.equals(ProfileContract.BarbellLifts.ADJUSTED_ONE_REP_MAX)){
            percentagesButton.setEnabled(true);
            percentagesButton.setVisibility(View.VISIBLE);
        }else {
            percentagesButton.setEnabled(false);
            percentagesButton.setVisibility(View.INVISIBLE);
        }
        Cursor cursor = dbWrite.query(
                tableName,
                null,
                ProfileContract.BarbellLifts.LIFT + " LIKE ? AND " + ProfileContract.BarbellLifts.USER_ID + " = ?",
                new String[]{ "%" + spinActivity.getSelectedItem().toString(), Integer.toString(userID)},
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
        String[] notes = new String[cursor.getCount()];
        int i = 0;
        Number min = 10000000;
        Number max = 0;
        if(cursor.moveToLast()){
            graphResults[9-i] = cursor.getInt(cursor.getColumnIndex(compareColumn));
            allResults[i] = cursor.getInt(cursor.getColumnIndex(compareColumn));
            dates[i] = cursor.getLong(cursor.getColumnIndex(ProfileContract.BarbellLifts.DATE));
            pr[i] = cursor.getInt(cursor.getColumnIndex(ProfileContract.BarbellLifts.PR));
            resultID[i] = cursor.getInt(cursor.getColumnIndex(ProfileContract.BarbellLifts._ID));
            notes[i] = cursor.getString(cursor.getColumnIndex(ProfileContract.BarbellLifts.COMMENTS));
            if (compareColumn.equals(ProfileContract.BarbellLifts.ADJUSTED_ONE_REP_MAX)){
                graphResults[9 - i] = graphResults[9 - i].floatValue() * units;
                allResults[i] = cursor.getInt(cursor.getColumnIndex(ProfileContract.BarbellLifts.WEIGHT)) * units;
                sets[i] = cursor.getInt(cursor.getColumnIndex(ProfileContract.BarbellLifts.ROUNDS));
                reps[i] = cursor.getInt(cursor.getColumnIndex(ProfileContract.BarbellLifts.REPS));
            }
            if (graphResults[i].doubleValue() > max.doubleValue()) max = allResults[i];
            if (graphResults[i].doubleValue() < min.doubleValue()) min = allResults[i];
            if (pr[i] == 1){
                if(compareColumn.equals(ProfileContract.BarbellLifts.ADJUSTED_ONE_REP_MAX)){
                    prWeight = cursor.getDouble(cursor.getColumnIndex(ProfileContract.BarbellLifts.ADJUSTED_ONE_REP_MAX));
                    String formattedResults = Integer.toString(sets[i]) + " X " + Integer.toString(reps[i]) + " X " + Integer.toString(allResults[i].intValue());
                    prValue.setText(formattedResults);
                } else if (compareColumn.equals(ProfileContract.Running.TIME)){
                    String hours = Integer.toString(allResults[i].intValue() / 3600) + ":";
                    String minutes = Integer.toString((allResults[i].intValue() % 3600) / 60) + ":";
                    String seconds = Integer.toString(((allResults[i].intValue() % 3600) % 60));
                    prValue.setText(hours + minutes + seconds);
                } else prValue.setText(allResults[i].toString());
            }
            i++;
        }
        while (cursor.moveToPrevious()){
            if (i < 10) graphResults[9-i] = cursor.getInt(cursor.getColumnIndex(compareColumn));
            allResults[i] = cursor.getInt(cursor.getColumnIndex(compareColumn));
            dates[i] = cursor.getLong(cursor.getColumnIndex(ProfileContract.BarbellLifts.DATE));
            pr[i] = cursor.getInt(cursor.getColumnIndex(ProfileContract.BarbellLifts.PR));
            resultID[i] = cursor.getInt(cursor.getColumnIndex(ProfileContract.BarbellLifts._ID));
            notes[i] = cursor.getString(cursor.getColumnIndex(ProfileContract.BarbellLifts.COMMENTS));
            if (compareColumn.equals(ProfileContract.BarbellLifts.ADJUSTED_ONE_REP_MAX)){
                if (i < 10) graphResults[9 - i] = graphResults[9 - i].floatValue() * units;
                allResults[i] = cursor.getInt(cursor.getColumnIndex(ProfileContract.BarbellLifts.WEIGHT)) * units;
                sets[i] = cursor.getInt(cursor.getColumnIndex(ProfileContract.BarbellLifts.ROUNDS));
                reps[i] = cursor.getInt(cursor.getColumnIndex(ProfileContract.BarbellLifts.REPS));
            }
            if (graphResults[i].doubleValue() > max.doubleValue()) max = allResults[i];
            if (graphResults[i].doubleValue() < min.doubleValue()) min = allResults[i];
            if (pr[i] == 1) {
                if (compareColumn.equals(ProfileContract.BarbellLifts.ADJUSTED_ONE_REP_MAX)) {
                    prWeight = cursor.getDouble(cursor.getColumnIndex(ProfileContract.BarbellLifts.ADJUSTED_ONE_REP_MAX));
                    String formattedResults = Integer.toString(sets[i]) + " X " + Integer.toString(reps[i]) + " X " + Integer.toString(allResults[i].intValue());
                    prValue.setText(formattedResults);
                } else if (compareColumn.equals(ProfileContract.Running.TIME)) {
                    String hours = Integer.toString(allResults[i].intValue() / 3600) + ":";
                    String minutes = Integer.toString((allResults[i].intValue() % 3600) / 60) + ":";
                    String seconds = Integer.toString(((allResults[i].intValue() % 3600) % 60));
                    prValue.setText(hours + minutes + seconds);
                } else prValue.setText(allResults[i].toString());
            }
            i++;
        }
        cursor.close();
        setUpGraph(min, max, graphResults);
        mainAdapter.setVariables(allResults, dates, pr, resultID, reps, sets, compareColumn,notes);
        mainAdapter.notifyDataSetChanged();
        //finds community results
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Cursor prCursor = dbWrite.query(
                        ProfileContract.ProfileValues.TABLE_NAME,
                        null,
                        ProfileContract.ProfileValues._ID + " = ?",
                        new String[]{Integer.toString(userID)},
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
                    email = email.replace(".", "");
                    prCursor.close();
                    double pr = 0;
                    double below = 0.0;
                    double total = 0.0;
                    double equal = 0.0;
                    ArrayList<Integer> results = new ArrayList<Integer>();
                    for (DataSnapshot comResult : dataSnapshot.child(spinActivity.getSelectedItem().toString()).child(Integer.toString(gender)).child(Integer.toString(skill)).child(Integer.toString(scaledWeight))
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
                    percentileValue.setText(Double.toString((below + .5 * equal * 100) / total));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * sets up the graph
     * @param min lowest result in the range
     * @param max highest result in the range
     * @param results array of results to graph.
     */
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

    /**
     * shows pop up of weight percentages for user
     * @param view parent view of button that was pushed.
     */
    public void percentagesPopup(View view){
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.percentages_popup, null);
        final PopupWindow mPopupWindow = new PopupWindow(customView,RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setElevation(5.0f);
        TextView w100 = (TextView) customView.findViewById(R.id.per_tv_100weight);
        TextView w95 = (TextView) customView.findViewById(R.id.per_tv_95weight);
        TextView w90 = (TextView) customView.findViewById(R.id.per_tv_90weight);
        TextView w85 = (TextView) customView.findViewById(R.id.per_tv_85weight);
        TextView w80 = (TextView) customView.findViewById(R.id.per_tv_80weight);
        TextView w75 = (TextView) customView.findViewById(R.id.per_tv_75weight);
        TextView w70 = (TextView) customView.findViewById(R.id.per_tv_70weight);
        TextView w65 = (TextView) customView.findViewById(R.id.per_tv_65weight);
        TextView w60 = (TextView) customView.findViewById(R.id.per_tv_60weight);
        TextView w55 = (TextView) customView.findViewById(R.id.per_tv_55weight);
        TextView w50 = (TextView) customView.findViewById(R.id.per_tv_50weight);
        TextView w45 = (TextView) customView.findViewById(R.id.per_tv_45weight);
        Button close = (Button) customView.findViewById(R.id.per_button_close);
        w100.setText(String.format("%.2f", prWeight));
        w95.setText(String.format("%.2f",prWeight * .95));
        w90.setText(String.format("%.2f",prWeight * .90));
        w85.setText(String.format("%.2f",prWeight * .85));
        w80.setText(String.format("%.2f",prWeight * .80));
        w75.setText(String.format("%.2f",prWeight * .75));
        w70.setText(String.format("%.2f",prWeight * .70));
        w65.setText(String.format("%.2f",prWeight * .65));
        w60.setText(String.format("%.2f",prWeight * .60));
        w55.setText(String.format("%.2f",prWeight * .55));
        w50.setText(String.format("%.2f",prWeight * .5));
        w45.setText(String.format("%.2f",prWeight * .45));
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
            }
        });
        mPopupWindow.showAtLocation(findViewById(R.id.view_results_view), Gravity.CENTER,0,0);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbWrite.close();
    }


    /**
     * handles if a result is clicked, asks if user wants to delete result
     * @param deleteID ID of result to delete
     * @param date date of result
     */
    @Override
    public void onClick(final String deleteID, String date) {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        View customView = inflater.inflate(R.layout.delete_result_popup, null);
        final PopupWindow mPopupWindow = new PopupWindow(customView, RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setElevation(5.0f);
        Button yesButton = (Button) customView.findViewById(R.id.popup_button_yes);
        Button noButton = (Button) customView.findViewById(R.id.popup_button_no);
        TextView deleMes = (TextView) customView.findViewById(R.id.popup_tv_message);
        deleMes.setText("Do you want to delete the result from " + date +"?");
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
            }
        });
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor cursor = dbWrite.query(
                        ProfileContract.RecentLifts.TABLE_NAME,
                        null,
                        ProfileContract.RecentLifts._ID + " = ?",
                        new String[] {Integer.toString(userID)},
                        null,
                        null,
                        null
                );
                dbWrite.delete(tableName, ProfileContract.BarbellLifts._ID + "=" + deleteID, null);
                cursor.moveToFirst();
                if (tableName.equals(cursor.getString(cursor.getColumnIndex(ProfileContract.RecentLifts.RESULT_ONE_TABLE)))){
                    if (deleteID.equals(Integer.toString(cursor.getInt(cursor.getColumnIndex(ProfileContract.RecentLifts.RESULT_ONE_ID))))){
                        replaceResult(ProfileContract.RecentLifts.RESULT_ONE_ID, tableName, 1, cursor);
                    }
                }
                if ((tableName.equals(cursor.getString(cursor.getColumnIndex(ProfileContract.RecentLifts.RESULT_TWO_TABLE))))){
                    if (deleteID.equals(Integer.toString(cursor.getInt(cursor.getColumnIndex(ProfileContract.RecentLifts.RESULT_TWO_ID))))){
                        replaceResult(ProfileContract.RecentLifts.RESULT_TWO_ID, tableName,2, cursor);
                    }
                }
                if ((tableName.equals(cursor.getString(cursor.getColumnIndex(ProfileContract.RecentLifts.RESULT_THREE_TABLE))))){
                    if (deleteID.equals(Integer.toString(cursor.getInt(cursor.getColumnIndex(ProfileContract.RecentLifts.RESULT_THREE_ID))))){
                        replaceResult(ProfileContract.RecentLifts.RESULT_THREE_ID, tableName,3, cursor);
                    }
                }
                if ((tableName.equals(cursor.getString(cursor.getColumnIndex(ProfileContract.RecentLifts.RESULT_FOUR_TABLE))))){
                    if (deleteID.equals(Integer.toString(cursor.getInt(cursor.getColumnIndex(ProfileContract.RecentLifts.RESULT_FOUR_ID))))){
                        replaceResult(ProfileContract.RecentLifts.RESULT_FOUR_ID, tableName,4, cursor);
                    }
                }
                if ((tableName.equals(cursor.getString(cursor.getColumnIndex(ProfileContract.RecentLifts.RESULT_FIVE_TABLE))))){
                    if (deleteID.equals(Integer.toString(cursor.getInt(cursor.getColumnIndex(ProfileContract.RecentLifts.RESULT_FIVE_ID))))){
                        replaceResult(ProfileContract.RecentLifts.RESULT_FIVE_ID, tableName,5, cursor);
                    }
                }
                mPopupWindow.dismiss();
                cursor.close();
            }
        });
        mPopupWindow.showAtLocation(findViewById(R.id.view_results_view), Gravity.CENTER,0,0);
    }

    /**
     * update recent results table if delete result was recent
     * @param resultID id of deleted result
     * @param tableName table of deleted result
     * @param order how recent the result was
     * @param cursor cursor holding recent results table
     */
    private void replaceResult(String resultID, String tableName, int order, Cursor cursor){
        ContentValues cv = new ContentValues();
        if (order < 2){
            cv.put(ProfileContract.RecentLifts.RESULT_ONE_TABLE, cursor.getString(cursor.getColumnIndex(ProfileContract.RecentLifts.RESULT_TWO_TABLE)));
            cv.put(ProfileContract.RecentLifts.RESULT_ONE_ID, cursor.getInt(cursor.getColumnIndex(ProfileContract.RecentLifts.RESULT_TWO_ID)));
        }
        if (order < 3){
            cv.put(ProfileContract.RecentLifts.RESULT_TWO_TABLE,
                    cursor.getString(cursor.getColumnIndex(ProfileContract.RecentLifts.RESULT_THREE_TABLE)));
            cv.put(ProfileContract.RecentLifts.RESULT_TWO_ID,
                    cursor.getInt(cursor.getColumnIndex(ProfileContract.RecentLifts.RESULT_THREE_ID)));
        }
        if (order < 4){
            cv.put(ProfileContract.RecentLifts.RESULT_THREE_TABLE,
                    cursor.getString(cursor.getColumnIndex(ProfileContract.RecentLifts.RESULT_FOUR_TABLE)));
            cv.put(ProfileContract.RecentLifts.RESULT_THREE_ID,
                    cursor.getInt(cursor.getColumnIndex(ProfileContract.RecentLifts.RESULT_FOUR_ID)));
        }
        if (order < 5){
            cv.put(ProfileContract.RecentLifts.RESULT_FOUR_TABLE,
                    cursor.getString(cursor.getColumnIndex(ProfileContract.RecentLifts.RESULT_FIVE_TABLE)));
            cv.put(ProfileContract.RecentLifts.RESULT_FOUR_ID,
                    cursor.getInt(cursor.getColumnIndex(ProfileContract.RecentLifts.RESULT_FIVE_ID)));
        }
        Cursor newCursor = dbWrite.query(
                tableName,
                null,
                ProfileContract.BarbellLifts.LIFT + " = ? AND " + ProfileContract.BarbellLifts.USER_ID + " = ?",
                new String[]{spinActivity.getSelectedItem().toString(), Integer.toString(userID)},
                null,
                null,
                null
        );
        int tempResultID = 0;
        String tempTableName = tableName;
        if (newCursor.moveToLast()){
            tempResultID = newCursor.getInt(cursor.getColumnIndex(ProfileContract.BarbellLifts._ID));
        } else tempTableName = null;
        cv.put(ProfileContract.RecentLifts.RESULT_FIVE_TABLE,tempTableName);
        cv.put(ProfileContract.RecentLifts.RESULT_FIVE_ID, tempResultID);
        dbWrite.update(ProfileContract.RecentLifts.TABLE_NAME, cv, ProfileContract.RecentLifts._ID + " = ?",
                new String[] {Integer.toString(userID)});
        newCursor.close();
        weightBased();
        mainAdapter.notifyDataSetChanged();
    }

    /**
     * sets up menu for activity
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_results_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this)
                    .edit();
            edit.putString(getString(R.string.sp_email), null);
            edit.apply();
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);

        } else if (id == R.id.edit_profile){
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra(getString(R.string.new_user), false);
            startActivity(intent);
        } else if (id == R.id.view_results){
            Intent intent = new Intent(this, ViewResults.class);
            startActivity(intent);
        } else if (id == R.id.community_results){
            Intent intent = new Intent(this, CommunityResults.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


}
