package online.cagocapps.prtracker;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
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

import online.cagocapps.prtracker.Data.ProfileContract;
import online.cagocapps.prtracker.Data.ProfileDBHelper;

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

    //for saving result
    private int resultType;
    private String tableName;

    //db vars
    private ProfileDBHelper dbHelper;
    private SQLiteDatabase dbWrite;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_result);

        //find views;
        spinActivity = (Spinner) findViewById(R.id.ar_spin_activities);
        spinCategory = (Spinner) findViewById(R.id.ar_spin_categories);
        tvRounds = (TextView) findViewById(R.id.ar_tv_rounds);
        etSets = (EditText) findViewById(R.id.ar_et_secs);
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
                                if (i == 16) timeBased();
                                else bodyWeightBased();
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
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
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
        etSets.setEnabled(true);
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
        etSets.setEnabled(true);
        etReps.setEnabled(true);
        etWeight.setEnabled(true);
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
        if(!etSets.isEnabled()) etSets.setText(null);
        if(!etReps.isEnabled()) etReps.setText(null);
        if(!etWeight.isEnabled()) etWeight.setText(null);
        if(!etHours.isEnabled()) etHours.setText(null);
        if(!etMinutes.isEnabled()) etMinutes.setText(null);
        if(!etSeconds.isEnabled()) etSeconds.setText(null);
        checkRX.setChecked(checkRX.isEnabled());
        if(!spinDistance.isEnabled()) spinDistance.setPrompt(null);
    }

    public void saveResult(View view){
        long workoutID;
        double convertedOneRepMaxDouble;
        int convertedOneRepMax;
        String activity = spinActivity.getSelectedItem().toString();
        int sets = 1;
        if (etSets.getText().toString() != null)
            sets = Integer.valueOf(etSets.getText().toString());
        double reps = 1;
        if (etReps.getText().toString() != null)
            reps = Integer.valueOf(etReps.getText().toString());
        double weight = 1;
        if (etWeight.getText().toString() != null)
            weight = Integer.valueOf(etWeight.getText().toString());
        int hours;
        if (etHours.getText().toString() != null)
            hours = Integer.valueOf(etHours.getText().toString());
        int minutes;
        if (etMinutes.getText().toString() != null)
            minutes = Integer.valueOf(etMinutes.getText().toString());
        int seconds;
        if (etSeconds.getText().toString() != null)
            seconds = Integer.valueOf(etSeconds.getText().toString());
        boolean Rx = checkRX.isChecked();
        String distance = spinDistance.getSelectedItem().toString();
        String notes = etNotes.getText().toString();
        int userID = PreferenceManager.getDefaultSharedPreferences(this).getInt(getString(R.string.sp_userID), -1);
        switch (resultType){
            case 0:
                convertedOneRepMaxDouble = weight /(1.0278 - (.0278 * reps));
                convertedOneRepMax = (int) convertedOneRepMaxDouble;
                ContentValues cv = new ContentValues();
                cv.put(ProfileContract.BarbellLifts.USER_ID, userID);
                cv.put(ProfileContract.BarbellLifts.LIFT, activity);
                cv.put(ProfileContract.BarbellLifts.REPS, reps);
                cv.put(ProfileContract.BarbellLifts.WEIGHT, weight);
                cv.put(ProfileContract.BarbellLifts.ROUNDS, sets);
                cv.put(ProfileContract.BarbellLifts.ADJUSTED_ONE_REP_MAX, convertedOneRepMax);
                cv.put(ProfileContract.BarbellLifts.COMMENTS, notes);
                cv.put(ProfileContract.BarbellLifts.DATE, System.currentTimeMillis());
                workoutID = dbWrite.insert(tableName, null, cv);
        }
    }
}
