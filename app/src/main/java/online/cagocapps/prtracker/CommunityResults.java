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
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import online.cagocapps.prtracker.Data.ProfileContract;
import online.cagocapps.prtracker.Data.ProfileDBHelper;
import online.cagocapps.prtracker.Data.ResultObject;

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
    private String tableName;
    private String compareColumn;

    private ProfileDBHelper dbHelper;
    private SQLiteDatabase dbWrite;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_results);

        dbHelper = new ProfileDBHelper(this);
        dbWrite = dbHelper.getWritableDatabase();

        mDatabase = FirebaseDatabase.getInstance().getReference();
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
                                new ArrayAdapter<String>(view.getContext(),
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
                                new ArrayAdapter<String>(view.getContext(),
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
                                new ArrayAdapter<String>(view.getContext(),
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

    private void updatePage(){
        final String userID = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.sp_userID), null);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Cursor prCursor = dbWrite.query(
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
                    int topWeight = 99999;
                    int botWeight = 0;
                    if (spinWeight.getSelectedItem().toString().contains("-") ){
                        String[] weights = spinWeight.getSelectedItem().toString().split("-");
                        botWeight = Integer.valueOf(weights[0])/10;
                        topWeight = Integer.valueOf(weights[1])/10;
                    } else if (spinWeight.getSelectedItem().toString().contains("+")){
                        botWeight = 23;
                    }
                    String yearsAct = (Integer.toString(prCursor.getInt(prCursor.getColumnIndex(ProfileContract.ProfileValues.YEARS_ACTIVE))));
                    String skill = String.valueOf(spinSkill.getSelectedItem().toString().charAt(0));
                    int gender = -1;
                    if (spinGender.getSelectedItem().toString().equals(getString(R.string.male))){
                        gender = 0;
                    } else if (spinGender.getSelectedItem().toString().equals("Female")){
                        gender = 1;
                    }
                    String email = prCursor.getString(prCursor.getColumnIndex(ProfileContract.ProfileValues.EMAIL));
                    email = email.replace(".","");
                    prCursor.close();
                    double pr = 0;
                    double below = 0.0;
                    double total = 0.0;
                    double equal = 0.0;
                    ArrayList<Integer> results = new ArrayList<Integer>();
                    for (DataSnapshot genderData : dataSnapshot.child(spinActivity.getSelectedItem().toString()).getChildren()) {
                        if(Integer.valueOf(genderData.getKey()) == gender ||
                                gender == -1) {
                            for(DataSnapshot skillData : genderData.getChildren()){
                                if (skillData.getKey().equals(skill) || skill.equals("A")){
                                    for(DataSnapshot weightData : skillData.getChildren() ){
                                        if (Integer.valueOf(weightData.getKey()) >= botWeight && Integer.valueOf(weightData.getKey()) <= topWeight){

                                        }
                                    }
                                }
                        }
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
                    tvPercentile.setText(Double.toString((below + .5 * equal * 100) / total));

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        }

    }

}
