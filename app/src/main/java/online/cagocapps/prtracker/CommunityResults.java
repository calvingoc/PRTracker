package online.cagocapps.prtracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import online.cagocapps.prtracker.Data.ProfileContract;

public class CommunityResults extends AppCompatActivity {
    private Spinner spinActivity;
    private Spinner spinCategory;
    private Spinner spinGender;
    private Spinner spinSkill;
    private Spinner spinWeight;
    private Spinner spinAge;
    private Spinner spinYears;
    private TextView tvPR;
    private TextView tvPercentile;
    private TextView tvCommBest;
    private TextView tvCommAverage;
    private TextView tvStandDev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_results);
        spinActivity = (Spinner) findViewById(R.id.cr_spin_activity);
        spinCategory = (Spinner) findViewById(R.id.cr_spin_category);
        spinGender = (Spinner) findViewById(R.id.cr_spin_gender);
        spinSkill = (Spinner) findViewById(R.id.cr_spin_skill);
        spinWeight = (Spinner) findViewById(R.id.cr_spin_age);
        spinAge = (Spinner) findViewById(R.id.cr_spin_age);
        spinYears = (Spinner) findViewById(R.id.cr_spin_active);
        tvPR = (TextView) findViewById(R.id.cr_tv_pr_value);
        tvPercentile = (TextView) findViewById(R.id.cr_tv_percentile_value);
        tvCommBest = (TextView) findViewById(R.id.cr_tv_comm_best_value);
        tvCommAverage = (TextView) findViewById(R.id.cr_tv_comm_average_value);
        tvStandDev = (TextView) findViewById(R.id.cr_tv_stand_dev_value);
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
                                compareColumn = ProfileContract.BarbellLifts.ADJUSTED_ONE_REP_MAX;
                                percentileValue.setText("");
                                prValue.setText("");
                                weightBased();
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
                                compareColumn = ProfileContract.BarbellLifts.ADJUSTED_ONE_REP_MAX;
                                percentileValue.setText("");
                                prValue.setText("");
                                weightBased();
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
                                    compareColumn =ProfileContract.Running.TIME;
                                    percentileValue.setText("");
                                    prValue.setText("");
                                    weightBased();

                                    plank = true;
                                }
                                else{
                                    compareColumn = ProfileContract.BarbellLifts.REPS;
                                    weightBased();
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
                                compareColumn = ProfileContract.Running.TIME;
                                percentileValue.setText("");
                                prValue.setText("");
                                weightBased();
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
                                compareColumn = ProfileContract.Running.TIME;
                                percentileValue.setText("");
                                prValue.setText("");
                                weightBased();
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
}
