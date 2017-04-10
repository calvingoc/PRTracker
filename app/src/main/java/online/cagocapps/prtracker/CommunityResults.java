package online.cagocapps.prtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import online.cagocapps.prtracker.Data.ProfileContract;
import online.cagocapps.prtracker.Data.ProfileDBHelper;
import online.cagocapps.prtracker.Data.ResultObject;

public class CommunityResults extends AppCompatActivity {
    //set up view variables
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

    //variables for SQL queries
    private String tableName;
    private String compareColumn;

    //database variables
    private ProfileDBHelper dbHelper;
    private SQLiteDatabase dbWrite;
    private DatabaseReference mDatabase;

    private final String TAG = "Community Results";


    /**
     * set up the activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_results);

        //set up database
        dbHelper = new ProfileDBHelper(this);
        dbWrite = dbHelper.getWritableDatabase();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //find views
        spinActivity = (Spinner) findViewById(R.id.cr_spin_activity);
        spinCategory = (Spinner) findViewById(R.id.cr_spin_category);
        spinGender = (Spinner) findViewById(R.id.cr_spin_gender);
        spinSkill = (Spinner) findViewById(R.id.cr_spin_skill);
        spinWeight = (Spinner) findViewById(R.id.cr_spin_weight);
        spinAge = (Spinner) findViewById(R.id.cr_spin_age);
        spinYears = (Spinner) findViewById(R.id.cr_spin_active);
        tvPR = (TextView) findViewById(R.id.cr_tv_pr_value);
        tvPercentile = (TextView) findViewById(R.id.cr_tv_percentile_value);
        tvCommBest = (TextView) findViewById(R.id.cr_tv_comm_best_value);
        tvCommAverage = (TextView) findViewById(R.id.cr_tv_comm_average_value);
        tvStandDev = (TextView) findViewById(R.id.cr_tv_stand_dev_value);

        //add listeners to spinners to the page is updated correctly when their values change.
        spinCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<String> arrayAdapter;
                switch (position) {
                    case 0:
                        tableName = ProfileContract.BarbellLifts.TABLE_NAME;
                        arrayAdapter =
                                new ArrayAdapter<String>(parent.getContext(),
                                        android.R.layout.simple_spinner_item,
                                        getResources().getStringArray(R.array.barbell_lifts_array));
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinActivity.setAdapter(arrayAdapter);
                        spinActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                compareColumn = ProfileContract.BarbellLifts.ADJUSTED_ONE_REP_MAX;
                                updatePage();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        break;
                    case 1:
                        tableName = ProfileContract.DumbbellLifts.TABLE_NAME;
                        arrayAdapter =
                                new ArrayAdapter<String>(parent.getContext(),
                                        android.R.layout.simple_spinner_item,
                                        getResources().getStringArray(R.array.dumbbell_lifts_array));
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinActivity.setAdapter(arrayAdapter);
                        spinActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                compareColumn = ProfileContract.BarbellLifts.ADJUSTED_ONE_REP_MAX;
                                updatePage();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        break;
                    case 2:
                        tableName = ProfileContract.Gymnastics.TABLE_NAME;
                        arrayAdapter =
                                new ArrayAdapter<String>(parent.getContext(),
                                        android.R.layout.simple_spinner_item,
                                        getResources().getStringArray(R.array.bodyweight_array));
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinActivity.setAdapter(arrayAdapter);
                        spinActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                if (i == 16) {
                                    compareColumn =ProfileContract.Running.TIME;
                                    updatePage();
                                }
                                else{
                                    compareColumn = ProfileContract.BarbellLifts.REPS;
                                    updatePage();
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
                                new ArrayAdapter<String>(parent.getContext(),
                                        android.R.layout.simple_spinner_item,
                                        getResources().getStringArray(R.array.running_array));
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinActivity.setAdapter(arrayAdapter);
                        spinActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                compareColumn = ProfileContract.Running.TIME;
                                updatePage();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        break;
                    case 4:
                        tableName = ProfileContract.Swimming.TABLE_NAME;
                        arrayAdapter =
                                new ArrayAdapter<String>(parent.getContext(),
                                        android.R.layout.simple_spinner_item,
                                        getResources().getStringArray(R.array.swimming_array));
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinActivity.setAdapter(arrayAdapter);
                        spinActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                compareColumn = ProfileContract.Running.TIME;
                                updatePage();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        break;
                    case 5:
                        tableName = ProfileContract.CrossFitStandards.TABLE_NAME;
                        arrayAdapter =
                                new ArrayAdapter<String>(parent.getContext(),
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
                                    updatePage();
                                }else {
                                    compareColumn = ProfileContract.BarbellLifts.REPS;
                                    updatePage();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        break;
                    case 6:
                        arrayAdapter =
                                new ArrayAdapter<String>(parent.getContext(),
                                        android.R.layout.simple_spinner_item,
                                        getResources().getStringArray(R.array.crossfit_heroes_array));
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinActivity.setAdapter(arrayAdapter);
                        spinActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                if (i == 0 || i == 1 || i == 2 || i == 3 || i == 4 || i == 6) {
                                    compareColumn = ProfileContract.Running.TIME;
                                    updatePage();
                                }else {
                                    compareColumn = ProfileContract.BarbellLifts.REPS;
                                    updatePage();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {}
                        });
                        tableName = ProfileContract.CrossFitStandards.TABLE_NAME;
                        break;
                    case 7:
                        tableName = ProfileContract.CrossFitStandards.TABLE_NAME;
                        arrayAdapter =
                                new ArrayAdapter<String>(parent.getContext(),
                                        android.R.layout.simple_spinner_item,
                                        getResources().getStringArray(R.array.crossfit_open_array));
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinActivity.setAdapter(arrayAdapter);
                        spinActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                if (i == 14 || i == 18 || i == 23 || i == 25) {
                                    compareColumn = ProfileContract.Running.TIME;
                                    updatePage();
                                }else {
                                    compareColumn = ProfileContract.BarbellLifts.REPS;
                                    updatePage();
                                }
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
        spinGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updatePage();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinSkill.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updatePage();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinWeight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updatePage();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinAge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updatePage();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinYears.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updatePage();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * update page to display the correct values for the current activity and community selection
     */
    private void updatePage(){
        //set up varibles needed to find user's PR and display page correctly
        int userIDInt = PreferenceManager.getDefaultSharedPreferences(this).getInt(getString(R.string.sp_userID), -1);
        final String userID = String.valueOf(userIDInt);
        final float units = PreferenceManager.getDefaultSharedPreferences(this).getFloat(getString(R.string.sp_units), 1);

        //calculate the stats based off of current community results
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    double pr = 0;
                    Cursor cursor = dbWrite.query(//find the user's pr for the given activity
                            tableName,
                            null,
                            ProfileContract.BarbellLifts.LIFT + " = ? and " + ProfileContract.BarbellLifts.PR + " =? and " + ProfileContract.BarbellLifts.USER_ID + " = ?",
                            new String[]{spinActivity.getSelectedItem().toString(), "1", userID},
                            null,
                            null,
                            null
                    );
                    if (cursor.moveToFirst()) {//finds the correct pr and formats it correctly based off of table and results
                        if (tableName.equals(ProfileContract.BarbellLifts.TABLE_NAME) || tableName.equals(ProfileContract.DumbbellLifts.TABLE_NAME)) {
                            String sets = cursor.getString(cursor.getColumnIndex(ProfileContract.BarbellLifts.ROUNDS));
                            String reps = cursor.getString(cursor.getColumnIndex(ProfileContract.BarbellLifts.REPS));
                            float weight = cursor.getFloat(cursor.getColumnIndex(ProfileContract.BarbellLifts.WEIGHT));
                            tvPR.setText(sets + " X " + reps + " X " + Float.toString(weight * units));
                        } else if (tableName.equals(ProfileContract.CrossFitStandards.TABLE_NAME)) {
                            String rx = "";
                            if (cursor.getInt(cursor.getColumnIndex(ProfileContract.CrossFitStandards.RX)) == 1) {
                                rx = " Rx";
                            }
                            if (cursor.getString(cursor.getColumnIndex(ProfileContract.CrossFitStandards.TIME)) == null) {
                                String sets = cursor.getString(cursor.getColumnIndex(ProfileContract.BarbellLifts.ROUNDS));
                                String reps = cursor.getString(cursor.getColumnIndex(ProfileContract.BarbellLifts.REPS));
                                tvPR.setText(sets + " Rounds " + reps + " Reps" + rx);
                            } else {
                                String sets = cursor.getString(cursor.getColumnIndex(ProfileContract.BarbellLifts.ROUNDS));
                                String reps = cursor.getString(cursor.getColumnIndex(ProfileContract.BarbellLifts.REPS));
                                int totalTime = cursor.getInt(cursor.getColumnIndex(ProfileContract.CrossFitStandards.TIME));
                                String hours = Integer.toString(totalTime / 3600) + ":";
                                String minutes = Integer.toString((totalTime % 3600) / 60) + ":";
                                String seconds = Integer.toString(((totalTime % 3600) % 60));
                                tvPR.setText(reps + " Reps in " + hours + minutes + seconds + rx);
                            }
                        } else if (tableName.equals(ProfileContract.Gymnastics.TABLE_NAME)) {
                            if (cursor.getString(cursor.getColumnIndex(ProfileContract.Gymnastics.TIME)) == null) {
                                String sets = cursor.getString(cursor.getColumnIndex(ProfileContract.Gymnastics.ROUNDS));
                                String reps = cursor.getString(cursor.getColumnIndex(ProfileContract.Gymnastics.REPS));
                                tvPR.setText(sets + " X " + reps);
                            } else {
                                int totalTime = cursor.getInt(cursor.getColumnIndex(ProfileContract.CrossFitStandards.TIME));
                                String hours = Integer.toString(totalTime / 3600) + ":";
                                String minutes = Integer.toString((totalTime % 3600) / 60) + ":";
                                String seconds = Integer.toString(((totalTime % 3600) % 60));
                                tvPR.setText(hours + minutes + seconds);
                            }
                        } else {
                            int totalTime = cursor.getInt(cursor.getColumnIndex(ProfileContract.CrossFitStandards.TIME));
                            String hours = Integer.toString(totalTime / 3600) + ":";
                            String minutes = Integer.toString((totalTime % 3600) / 60) + ":";
                            String seconds = Integer.toString(((totalTime % 3600) % 60));
                            tvPR.setText(hours + minutes + seconds);
                        }
                        pr = cursor.getDouble(cursor.getColumnIndex(compareColumn));
                    } else {
                        tvPR.setText("");
                        pr = 0;
                    }
                    cursor.close();
                    //finds weight range
                    int topWeight = 99999;
                    int botWeight = 0;
                    if (spinWeight.getSelectedItem().toString().contains("-")) {
                        String[] weights = spinWeight.getSelectedItem().toString().split("-");
                        botWeight = Integer.valueOf(weights[0]) / 10;
                        topWeight = Integer.valueOf(weights[1]) / 10;
                    } else if (spinWeight.getSelectedItem().toString().contains("+")) {
                        botWeight = 23;
                    }
                    //find age range
                    int topAge = 99999;
                    int botAge = 0;
                    if (spinAge.getSelectedItem().toString().contains("-")) {
                        String[] ages = spinAge.getSelectedItem().toString().split("-");
                        botAge = Integer.valueOf(ages[0]);
                        topAge = Integer.valueOf(ages[1]);
                    } else if (spinWeight.getSelectedItem().toString().contains("+")) {
                        botAge = 61;
                    }
                    //find years active range
                    int topAct = 99999;
                    int botAct = 0;
                    if (spinYears.getSelectedItem().toString().contains("-")) {
                        String[] years = spinYears.getSelectedItem().toString().split("-");
                        botAct = Integer.valueOf(years[0]);
                        topAct = Integer.valueOf(years[1]);
                    } else if (spinWeight.getSelectedItem().toString().contains("+")) {
                        botAge = 21;
                    }
                    //find skill level
                    String skill = String.valueOf(spinSkill.getSelectedItem().toString().charAt(0));
                    //find selected gender
                    int gender = -1;
                    if (spinGender.getSelectedItem().toString().equals(getString(R.string.male))) {
                        gender = 0;
                    } else if (spinGender.getSelectedItem().toString().equals("Female")) {
                        gender = 1;
                    }
                    //set up variables needed to calculate stats
                    double below = 0.0;
                    double total = 0.0;
                    double equal = 0.0;
                    ArrayList<Integer> results = new ArrayList<>(); //array to hold results that match criteria
                    for (DataSnapshot genderData : dataSnapshot.child(spinActivity.getSelectedItem().toString()).getChildren()) {
                        if (Integer.valueOf(genderData.getKey()) == gender ||
                                gender == -1) {
                            for (DataSnapshot skillData : genderData.getChildren()) {
                                if (skillData.getKey().equals(skill) || skill.equals("A")) {
                                    for (DataSnapshot weightData : skillData.getChildren()) {
                                        if (Integer.valueOf(weightData.getKey()) >= botWeight && Integer.valueOf(weightData.getKey()) <= topWeight) {
                                            for (DataSnapshot ageData : weightData.getChildren()) {
                                                if (Integer.valueOf(ageData.getKey()) >= botAge && Integer.valueOf(ageData.getKey()) <= topAge) {
                                                    for (DataSnapshot activeData : ageData.getChildren()) {
                                                        if (Integer.valueOf(activeData.getKey()) >= botAct && Integer.valueOf(activeData.getKey()) <= topAct) {
                                                            for (DataSnapshot resultsData : activeData.getChildren()) {
                                                                ResultObject resultObject = resultsData.getValue(ResultObject.class);
                                                                results.add(resultObject.getResult());//save result if it matches all required criteria
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //calculate percentile
                    double totalResults = 0;
                    double minValue = 999999999;
                    double maxValue = 0;
                    for (int i : results) {
                        totalResults = totalResults + i;
                        if (compareColumn.equals(ProfileContract.Running.TIME)) {
                            minValue = Math.min(minValue, i);
                        } else maxValue = Math.max(maxValue, i);
                        if (i < pr) {
                            below++;
                            total++;
                        } else if (i == pr) {
                            equal++;
                            total++;
                        } else total++;
                    }
                    //calculate and format average
                    double average = totalResults / total;
                    if (compareColumn.equals(ProfileContract.Running.TIME)) {
                        String hours = Double.toString(average / 3600) + ":";
                        String minutes = Double.toString((average % 3600) / 60) + ":";
                        String seconds = Double.toString(((average % 3600) % 60));
                        tvCommAverage.setText(hours + minutes + seconds);
                    } else if (compareColumn.equals(ProfileContract.BarbellLifts.ADJUSTED_ONE_REP_MAX)) {
                        tvCommAverage.setText(Double.toString(average * units));
                    } else tvCommAverage.setText(Double.toString(average));
                    tvPercentile.setText(Double.toString((below + .5 * equal * 100) / total));
                    //find and format community best
                    if (compareColumn.equals(ProfileContract.Running.TIME)) {
                        String hours = Double.toString(minValue / 3600) + ":";
                        String minutes = Double.toString((minValue % 3600) / 60) + ":";
                        String seconds = Double.toString(((minValue % 3600) % 60));
                        tvCommBest.setText(hours + minutes + seconds);
                    } else if (compareColumn.equals(ProfileContract.BarbellLifts.ADJUSTED_ONE_REP_MAX)) {
                        tvCommBest.setText(Double.toString(maxValue * units));
                    } else tvCommBest.setText(Double.toString(maxValue));

                    //calculate standard deviation
                    double sdTotal = 0;
                    for (int i : results) {
                        double sdTemp = average - i;
                        sdTemp = sdTemp * sdTemp;
                        sdTotal = sdTotal + sdTemp;
                    }
                    sdTotal = sdTotal / total;
                    sdTotal = Math.sqrt(sdTotal);
                    if (compareColumn.equals(ProfileContract.Running.TIME)) {
                        String hours = Double.toString(sdTotal / 3600) + ":";
                        String minutes = Double.toString((sdTotal % 3600) / 60) + ":";
                        String seconds = Double.toString(((sdTotal % 3600) % 60));
                        tvStandDev.setText(hours + minutes + seconds);
                    } else if (compareColumn.equals(ProfileContract.BarbellLifts.ADJUSTED_ONE_REP_MAX)) {
                        tvStandDev.setText(Double.toString(sdTotal * units));
                    } else tvStandDev.setText(Double.toString(sdTotal));
                }
                catch (NullPointerException e){
                    Log.d(TAG, "null pointer " +e);
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * set up menu for activity
     * @param menu menu to inflate
     * @return true if inflated correctly
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.community_results_menu, menu);
        return true;
    }

    /**
     * handle menu items getting clicked
     * @param item item that was clicked
     * @return true if click handled correctly
     */
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbWrite.close();
    }
}


