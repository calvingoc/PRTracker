package online.cagocapps.prtracker;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import online.cagocapps.prtracker.Data.ProfileContract;
import online.cagocapps.prtracker.Data.ProfileDBHelper;
import online.cagocapps.prtracker.Data.ResultObject;

public class AddResult extends AppCompatActivity {

    //set up view vars
    private Spinner spinCategory;
    private Spinner spinActivity;
    private TextView tvRounds;
    private EditText etSets;
    private EditText etReps;
    private EditText etWeight;
    private TextView tvTime;
    private EditText etHours;
    private EditText etMinutes;
    private EditText etSeconds;
    private CheckBox checkRX;
    private TextView tvDistance;
    private Spinner spinDistance;
    private EditText etNotes;
    private ToggleButton togUnits;

    //for saving result
    private int resultType;
    private String tableName;
    private boolean plank = false;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private float units;
    //db vars
    public ProfileDBHelper dbHelper;
    private SQLiteDatabase dbWrite;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_result);

        units = PreferenceManager.getDefaultSharedPreferences(this).getFloat(getString(R.string.sp_units),1);

        //find views;
        spinActivity = (Spinner) findViewById(R.id.ar_spin_activities);
        spinCategory = (Spinner) findViewById(R.id.ar_spin_categories);
        tvRounds = (TextView) findViewById(R.id.ar_tv_rounds);
        etSets = (EditText) findViewById(R.id.ar_et_rounds);
        etReps = (EditText) findViewById(R.id.ar_et_reps);
        etWeight = (EditText) findViewById(R.id.ar_et_weight);
        tvTime = (TextView) findViewById(R.id.ar_tv_time);
        etHours = (EditText) findViewById(R.id.ar_et_hours);
        etMinutes = (EditText) findViewById(R.id.ar_et_minutes);
        etSeconds = (EditText) findViewById(R.id.ar_et_secs);
        checkRX = (CheckBox) findViewById(R.id.ar_check_rx);
        tvDistance = (TextView) findViewById(R.id.ar_tv_distance);
        spinDistance = (Spinner) findViewById(R.id.ar_spin_distance);
        etNotes = (EditText) findViewById(R.id.ar_et_notes);
        togUnits = (ToggleButton) findViewById(R.id.ar_tog_units);
        if (units == 1) togUnits.setChecked(false);
        else togUnits.setChecked(true);


        spinCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<String> arrayAdapter;
                switch (position) {
                    case 0:
                        arrayAdapter =
                                new ArrayAdapter<String>(view.getContext(),
                                        android.R.layout.simple_spinner_item,
                                        getResources().getStringArray(R.array.barbell_lifts_array));
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinActivity.setAdapter(arrayAdapter);
                        spinActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                weightBased();
                                plank = false;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        tableName = ProfileContract.BarbellLifts.TABLE_NAME;
                        break;
                    case 1:
                        arrayAdapter =
                                new ArrayAdapter<String>(view.getContext(),
                                        android.R.layout.simple_spinner_item,
                                        getResources().getStringArray(R.array.dumbbell_lifts_array));
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinActivity.setAdapter(arrayAdapter);
                        spinActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                weightBased();
                                plank = false;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        tableName = ProfileContract.DumbbellLifts.TABLE_NAME;
                        break;
                    case 2:
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
                                    timeBased();
                                    plank = true;
                                }
                                else{
                                    bodyWeightBased();
                                    plank = false;
                                }

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        tableName = ProfileContract.Gymnastics.TABLE_NAME;
                        break;
                    case 3:
                        arrayAdapter =
                                new ArrayAdapter<String>(view.getContext(),
                                        android.R.layout.simple_spinner_item,
                                        getResources().getStringArray(R.array.running_array));
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinActivity.setAdapter(arrayAdapter);
                        spinActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                timeBased();
                                plank = false;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        tableName = ProfileContract.Running.TABLE_NAME;
                        break;
                    case 4:
                        arrayAdapter =
                                new ArrayAdapter<String>(view.getContext(),
                                        android.R.layout.simple_spinner_item,
                                        getResources().getStringArray(R.array.swimming_array));
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinActivity.setAdapter(arrayAdapter);
                        spinActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                swimmingBased();
                                plank = false;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        tableName = ProfileContract.Swimming.TABLE_NAME;
                        break;
                    case 5:
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
                                    crossFitBased();
                                else crossFitAMRAP();
                                plank = false;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        tableName = ProfileContract.CrossFitStandards.TABLE_NAME;
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
                                    crossFitBased();
                                else crossFitAMRAP();
                                plank = false;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {}
                        });
                        tableName = ProfileContract.CrossFitStandards.TABLE_NAME;
                        break;
                    case 7:
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
                                    crossFitBased();
                                else crossFitAMRAP();
                                plank = false;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        tableName = ProfileContract.CrossFitStandards.TABLE_NAME;
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
    //sets up page for weightBased exercises
    private void weightBased(){
        tvRounds.setEnabled(true);
        etSets.setEnabled(true);
        etReps.setEnabled(true);
        etWeight.setEnabled(true);
        tvTime.setEnabled(false);
        etHours.setEnabled(false);
        etMinutes.setEnabled(false);
        etSeconds.setEnabled(false);
        checkRX.setEnabled(false);
        tvDistance.setEnabled(false);
        spinDistance.setEnabled(false);
        clearDisabled();
        resultType = 0;
    }

    //sets up page for body weight based exercises
    private void bodyWeightBased(){
        tvRounds.setEnabled(true);
        etSets.setEnabled(true);
        etReps.setEnabled(true);
        etWeight.setEnabled(false);
        tvTime.setEnabled(false);
        etHours.setEnabled(false);
        etMinutes.setEnabled(false);
        etSeconds.setEnabled(false);
        checkRX.setEnabled(false);
        spinDistance.setEnabled(false);
        tvDistance.setEnabled(false);
        clearDisabled();
        resultType = 1;
    }

    //sets up page for time Based exercises
    private void timeBased(){
        tvRounds.setEnabled(false);
        etSets.setEnabled(false);
        etReps.setEnabled(false);
        etWeight.setEnabled(false);
        tvTime.setEnabled(true);
        etHours.setEnabled(true);
        etMinutes.setEnabled(true);
        etSeconds.setEnabled(true);
        checkRX.setEnabled(false);
        spinDistance.setEnabled(false);
        tvDistance.setEnabled(false);
        clearDisabled();
        resultType = 2;
    }

    //sets up page for swimming Based exercises
    private void swimmingBased(){
        tvRounds.setEnabled(false);
        etSets.setEnabled(false);
        etReps.setEnabled(false);
        etWeight.setEnabled(false);
        tvTime.setEnabled(true);
        etHours.setEnabled(true);
        etMinutes.setEnabled(true);
        etSeconds.setEnabled(true);
        checkRX.setEnabled(false);
        spinDistance.setEnabled(true);
        tvDistance.setEnabled(true);
        clearDisabled();
        resultType = 3;
    }

    //sets up page for CrossFit for Time Based exercises
    private void crossFitBased(){
        tvRounds.setEnabled(true);
        etSets.setEnabled(false);
        etReps.setEnabled(true);
        etWeight.setEnabled(false);
        tvTime.setEnabled(true);
        etHours.setEnabled(true);
        etMinutes.setEnabled(true);
        etSeconds.setEnabled(true);
        checkRX.setEnabled(true);
        spinDistance.setEnabled(false);
        tvDistance.setEnabled(false);
        clearDisabled();
        resultType = 4;
    }

    //sets up page for CrossFit AMRAP Based exercises
    private void crossFitAMRAP(){
        tvRounds.setEnabled(true);
        etSets.setEnabled(false);
        etReps.setEnabled(true);
        etWeight.setEnabled(false);
        tvTime.setEnabled(false);
        etHours.setEnabled(false);
        etMinutes.setEnabled(false);
        etSeconds.setEnabled(false);
        checkRX.setEnabled(true);
        spinDistance.setEnabled(false);
        tvDistance.setEnabled(false);
        clearDisabled();
        resultType = 5;
    }

    //clear disabled editviews
    private void clearDisabled(){
        if(!etSets.isEnabled()){
            etSets.setText(null);
            etSets.setTextColor(getColor(R.color.colorAccent));
            etSets.setHintTextColor(getColor(R.color.colorAccent));
        } else  {
            etSets.setTextColor(getColor(R.color.colorPrimaryDark));
            etSets.setHintTextColor(getColor(R.color.colorPrimaryDark));
        }
        if(!etReps.isEnabled()){
            etReps.setText(null);
            etReps.setTextColor(getColor(R.color.colorAccent));
            etReps.setHintTextColor(getColor(R.color.colorAccent));
        } else {
            etReps.setTextColor(getColor(R.color.colorPrimaryDark));
            etReps.setHintTextColor(getColor(R.color.colorPrimaryDark));
        }
        if(!etWeight.isEnabled()){
            etWeight.setText(null);
            etWeight.setTextColor(getColor(R.color.colorAccent));
            etWeight.setHintTextColor(getColor(R.color.colorAccent));
        } else{
            etWeight.setTextColor(getColor(R.color.colorPrimaryDark));
            etWeight.setHintTextColor(getColor(R.color.colorPrimaryDark));
        }
        if(!etHours.isEnabled()){
            etHours.setText(null);
            etHours.setTextColor(getColor(R.color.colorAccent));
            etHours.setHintTextColor(getColor(R.color.colorAccent));
        } else {
            etHours.setTextColor(getColor(R.color.colorPrimaryDark));
            etHours.setHintTextColor(getColor(R.color.colorPrimaryDark));
        }
        if(!etMinutes.isEnabled()) {
            etMinutes.setText(null);
            etMinutes.setHintTextColor(getColor(R.color.colorAccent));
            etMinutes.setTextColor(getColor(R.color.colorAccent));
        } else{
            etMinutes.setTextColor(getColor(R.color.colorPrimaryDark));
            etMinutes.setHintTextColor(getColor(R.color.colorPrimaryDark));
        }
        if(!etSeconds.isEnabled()){
            etSeconds.setText(null);
            etSeconds.setTextColor(getColor(R.color.colorAccent));
            etSeconds.setHintTextColor(getColor(R.color.colorAccent));
        } else{
            etSeconds.setHintTextColor(getColor(R.color.colorPrimaryDark));
            etSeconds.setTextColor(getColor(R.color.colorPrimaryDark));
        }
        checkRX.setChecked(checkRX.isEnabled());
        if (checkRX.isEnabled()) checkRX.setTextColor(getColor(R.color.colorPrimaryDark));
        else checkRX.setTextColor(getColor(R.color.colorAccent));
        if(!spinDistance.isEnabled()){
            spinDistance.setPrompt(null);
        }
    }

    public void saveResult(View view){
        //set up database
        dbHelper = new ProfileDBHelper(this);
        dbWrite = dbHelper.getWritableDatabase();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        if(togUnits.isChecked()) units = (float) .453592;
        else units = 1;

        long workoutID;
        double convertedOneRepMaxDouble;
        int convertedOneRepMax = 0;
        String activity = spinActivity.getSelectedItem().toString();
        int sets = 1;
        if (etSets.getText().length() != 0)
            sets = Integer.valueOf(etSets.getText().toString());
        double reps = 1;
        if (etReps.getText().length()!= 0)
            reps = Integer.valueOf(etReps.getText().toString());
        double weight = 1;
        if (etWeight.getText().length() != 0)
            weight = Integer.valueOf(etWeight.getText().toString())/units;
        int hours = 0;
        if (etHours.getText().length() != 0)
            hours = Integer.valueOf(etHours.getText().toString());
        int minutes = 0;
        if (etMinutes.getText().length() != 0)
            minutes = Integer.valueOf(etMinutes.getText().toString());
        int seconds= 0;
        if (etSeconds.getText().length() != 0)
            seconds = Integer.valueOf(etSeconds.getText().toString());
        int totalTime = (hours * 3600) + (minutes * 60) + seconds;
        int rx = 0;
        if (checkRX.isChecked()) rx = 1;
        String distance = spinDistance.getSelectedItem().toString();
        String notes = etNotes.getText().toString();
        ContentValues cv = new ContentValues();
        int userID = PreferenceManager.getDefaultSharedPreferences(this).getInt(getString(R.string.sp_userID), -1);
        cv.put(ProfileContract.BarbellLifts.USER_ID, userID);
        cv.put(ProfileContract.BarbellLifts.COMMENTS, notes);
        cv.put(ProfileContract.BarbellLifts.DATE, System.currentTimeMillis());
        switch (resultType) {
            case 0:
                convertedOneRepMaxDouble = weight / (1.0278 - (.0278 * reps));
                convertedOneRepMax = (int) convertedOneRepMaxDouble;
                cv.put(ProfileContract.BarbellLifts.LIFT, activity);
                cv.put(ProfileContract.BarbellLifts.REPS, reps);
                cv.put(ProfileContract.BarbellLifts.WEIGHT, weight);
                cv.put(ProfileContract.BarbellLifts.ROUNDS, sets);
                cv.put(ProfileContract.BarbellLifts.ADJUSTED_ONE_REP_MAX, convertedOneRepMax);
                break;

            case 1:
                cv.put(ProfileContract.Gymnastics.LIFT, activity);
                cv.put(ProfileContract.Gymnastics.REPS, reps);
                cv.put(ProfileContract.Gymnastics.ROUNDS, sets);
                break;
            case 2:
                cv.put(ProfileContract.Running.DISTANCE, activity);
                cv.put(ProfileContract.Running.TIME, totalTime);
                break;
            case 3:
                String swim = distance + " " + activity;
                cv.put(ProfileContract.Swimming.STROKE, swim);
                cv.put(ProfileContract.Swimming.TIME, totalTime);
                break;
            case 4:
                cv.put(ProfileContract.CrossFitStandards.ROUNDS, sets);
                cv.put(ProfileContract.CrossFitStandards.LIFT, activity);
                cv.put(ProfileContract.CrossFitStandards.RX, rx);
                cv.put(ProfileContract.CrossFitStandards.REPS, reps);
                cv.put(ProfileContract.CrossFitStandards.TIME, totalTime);
                break;
            case 5:
                cv.put(ProfileContract.CrossFitStandards.ROUNDS, sets);
                cv.put(ProfileContract.CrossFitStandards.LIFT, activity);
                cv.put(ProfileContract.CrossFitStandards.RX, rx);
                cv.put(ProfileContract.CrossFitStandards.REPS, reps);

        }
        cv = isPR(cv, activity, resultType, convertedOneRepMax, reps,totalTime, rx, sets);
        workoutID = dbWrite.insert(tableName, null, cv);
        cv.clear();
        Cursor cursor = dbWrite.query(
                ProfileContract.RecentLifts.TABLE_NAME,
                null,
                ProfileContract.RecentLifts._ID + " = ?",
                new String[] {Integer.toString(userID)},
                null,
                null,
                null
        );
        if (cursor.moveToFirst()){
            cv.put(ProfileContract.RecentLifts.RESULT_ONE_TABLE, tableName);
            cv.put(ProfileContract.RecentLifts.RESULT_ONE_ID, workoutID);
            cv.put(ProfileContract.RecentLifts.RESULT_TWO_TABLE,
                    cursor.getString(cursor.getColumnIndex(ProfileContract.RecentLifts.RESULT_ONE_TABLE)));
            cv.put(ProfileContract.RecentLifts.RESULT_TWO_ID,
                    cursor.getInt(cursor.getColumnIndex(ProfileContract.RecentLifts.RESULT_ONE_ID)));
            cv.put(ProfileContract.RecentLifts.RESULT_THREE_TABLE,
                    cursor.getString(cursor.getColumnIndex(ProfileContract.RecentLifts.RESULT_TWO_TABLE)));
            cv.put(ProfileContract.RecentLifts.RESULT_THREE_ID,
                    cursor.getInt(cursor.getColumnIndex(ProfileContract.RecentLifts.RESULT_TWO_ID)));
            cv.put(ProfileContract.RecentLifts.RESULT_FOUR_TABLE,
                    cursor.getString(cursor.getColumnIndex(ProfileContract.RecentLifts.RESULT_THREE_TABLE)));
            cv.put(ProfileContract.RecentLifts.RESULT_FOUR_ID,
                    cursor.getInt(cursor.getColumnIndex(ProfileContract.RecentLifts.RESULT_THREE_ID)));
            cv.put(ProfileContract.RecentLifts.RESULT_FIVE_TABLE,
                    cursor.getString(cursor.getColumnIndex(ProfileContract.RecentLifts.RESULT_FOUR_TABLE)));
            cv.put(ProfileContract.RecentLifts.RESULT_FIVE_ID,
                    cursor.getInt(cursor.getColumnIndex(ProfileContract.RecentLifts.RESULT_FOUR_ID)));
            dbWrite.update(ProfileContract.RecentLifts.TABLE_NAME, cv, ProfileContract.RecentLifts._ID + " = ?",
                    new String[] {Integer.toString(userID)});
        }
        else {
            cv.put(ProfileContract.RecentLifts._ID, userID);
            cv.put(ProfileContract.RecentLifts.RESULT_ONE_TABLE, tableName);
            cv.put(ProfileContract.RecentLifts.RESULT_ONE_ID, workoutID);
            dbWrite.insert(ProfileContract.RecentLifts.TABLE_NAME, null, cv);
        }
        cursor.close();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    private ContentValues isPR(ContentValues cv, String activity, int resultType, int oneRepMax,
                               double reps, int totalTime, int rx, int sets){
        Boolean newPr = false;
        int prValue = 0;
        Cursor cursor = dbWrite.query(
                tableName,
                null,
                ProfileContract.BarbellLifts.LIFT + " = ? and " + ProfileContract.BarbellLifts.PR + " =?",
                new String[]{activity, "1"},
                null,
                null,
                null
        );
        if (cursor.moveToFirst()){
            switch (resultType){
                case 0:
                    if(cursor.getInt(cursor.getColumnIndex(ProfileContract.BarbellLifts.ADJUSTED_ONE_REP_MAX)) < oneRepMax){
                        cv.put(ProfileContract.BarbellLifts.PR, "1");
                        newPr = true;
                        prValue=oneRepMax;
                    }
                    break;
                case 1:
                    if(cursor.getInt(cursor.getColumnIndex(ProfileContract.Gymnastics.REPS)) < reps){
                        cv.put(ProfileContract.BarbellLifts.PR, "1");
                        newPr = true;
                        prValue= (int) reps;
                    }
                    break;
                case 2:
                    if(plank){
                        if (cursor.getInt(cursor.getColumnIndex(ProfileContract.Running.TIME)) < totalTime){
                            cv.put(ProfileContract.BarbellLifts.PR, "1");
                            newPr = true;
                            prValue = totalTime;
                        }
                    } else {
                        if (cursor.getInt(cursor.getColumnIndex(ProfileContract.Running.TIME)) > totalTime){
                            cv.put(ProfileContract.BarbellLifts.PR, "1");
                            newPr = true;
                            prValue = totalTime;
                        }
                    }
                    break;
                case 3:
                    if (cursor.getInt(cursor.getColumnIndex(ProfileContract.Swimming.TIME)) > totalTime){
                        cv.put(ProfileContract.BarbellLifts.PR, "1");
                        newPr = true;
                        prValue = totalTime;
                    }
                    break;
                case 4:
                    if(cursor.getInt(cursor.getColumnIndex(ProfileContract.CrossFitStandards.RX)) < rx){
                        cv.put(ProfileContract.BarbellLifts.PR, "1");
                        newPr = true;
                        prValue = totalTime;
                    } else if(cursor.getInt(cursor.getColumnIndex(ProfileContract.CrossFitStandards.TIME)) > totalTime){
                        cv.put(ProfileContract.BarbellLifts.PR, "1");
                        newPr = true;
                        prValue = totalTime;
                    } else if (cursor.getInt(cursor.getColumnIndex(ProfileContract.CrossFitStandards.TIME)) == totalTime){
                        if (cursor.getInt(cursor.getColumnIndex(ProfileContract.CrossFitStandards.ROUNDS)) < sets){
                            cv.put(ProfileContract.BarbellLifts.PR, "1");
                            newPr = true;
                            prValue = totalTime;
                        } else if (cursor.getInt(cursor.getColumnIndex(ProfileContract.CrossFitStandards.ROUNDS)) == sets) {
                            if (cursor.getInt(cursor.getColumnIndex(ProfileContract.CrossFitStandards.REPS)) < reps) {
                                cv.put(ProfileContract.BarbellLifts.PR, "1");
                                newPr = true;
                                prValue = totalTime;
                            }
                        }
                    }
                    break;
                case 5:
                    if(cursor.getInt(cursor.getColumnIndex(ProfileContract.CrossFitStandards.RX)) < rx) {
                        cv.put(ProfileContract.BarbellLifts.PR, "1");
                        newPr = true;
                        prValue = sets;
                    } else if (cursor.getInt(cursor.getColumnIndex(ProfileContract.CrossFitStandards.ROUNDS)) < sets){
                        cv.put(ProfileContract.BarbellLifts.PR, "1");
                        newPr = true;
                        prValue = sets;
                    } else if (cursor.getInt(cursor.getColumnIndex(ProfileContract.CrossFitStandards.ROUNDS)) == sets) {
                        if (cursor.getInt(cursor.getColumnIndex(ProfileContract.CrossFitStandards.REPS)) < reps) {
                            cv.put(ProfileContract.BarbellLifts.PR, "1");
                            newPr = true;
                            prValue = sets;
                        }
                    }
                    break;
            }
        } else{
            cv.put(ProfileContract.BarbellLifts.PR, "1");
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String userEmail = sharedPreferences.getString(getString(R.string.sp_email), "error");
            String where = ProfileContract.ProfileValues.EMAIL + " = ?";
            Cursor prCursor = dbWrite.query(
                    ProfileContract.ProfileValues.TABLE_NAME,
                    null,
                    where,
                    new String[] {userEmail},
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
                prCursor.close();
                userEmail = userEmail.replace(".", "");
                ResultObject resultObject = new ResultObject();
                resultObject.setResult(prValue);
                reference.child(activity).child(Integer.toString(gender)).child(Integer.toString(skill)).child(Integer.toString(scaledWeight))
                        .child(Integer.toString(age)).child(yearsAct).child(userEmail).setValue(resultObject);
                reference.setValue(resultObject);
            }
        }

        if (newPr){
            ContentValues update = new ContentValues();
            update.put(ProfileContract.BarbellLifts.PR, "0");
            dbWrite.update(tableName, update, ProfileContract.BarbellLifts._ID + "=?",
                    new String[]{Integer.toString(cursor.getInt(cursor.getColumnIndex(ProfileContract.BarbellLifts._ID)))});

            //set up shared prefs
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String userEmail = sharedPreferences.getString(getString(R.string.sp_email), "error");
            String where = ProfileContract.ProfileValues.EMAIL + " = ?";
            Cursor prCursor = dbWrite.query(
                    ProfileContract.ProfileValues.TABLE_NAME,
                    null,
                    where,
                    new String[] {userEmail},
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
                prCursor.close();
                ResultObject resultObject = new ResultObject();
                resultObject.setResult(prValue);
                String email = userEmail.replace(".","");
                reference.child(activity).child(Integer.toString(gender)).child(Integer.toString(skill)).child(Integer.toString(scaledWeight))
                        .child(Integer.toString(age)).child(yearsAct).child(email).setValue(resultObject);
            }
        }
        cursor.close();
        return cv;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbWrite.close();
    }
}
