package online.cagocapps.prtracker;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.androidplot.xy.XYPlot;

import java.util.ArrayList;

import online.cagocapps.prtracker.Data.ProfileContract;
import online.cagocapps.prtracker.Data.ProfileDBHelper;

public class ViewResults extends AppCompatActivity {
    private Spinner spinActivity;
    private Spinner spinCategory;
    private XYPlot plotResultsGraph;

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

    private void weightBased(){
        Cursor cursor = dbRead.query(
                tableName,
                null,
                ProfileContract.BarbellLifts.LIFT + " = ? AND " + ProfileContract.BarbellLifts.USER_ID + " = ?",
                new String[]{spinActivity.getSelectedItem().toString(), Integer.toString(userID)},
                null,
                null,
                null
        );
        int[] results = new int[cursor.getCount()];
        int i = 0;
        while (cursor.moveToNext()){
            results[i] = cursor.getInt(cursor.getColumnIndex(ProfileContract.BarbellLifts.ADJUSTED_ONE_REP_MAX));
            i++;
        }

    }
}
