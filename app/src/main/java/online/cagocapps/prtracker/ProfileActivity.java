package online.cagocapps.prtracker;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import online.cagocapps.prtracker.Data.ProfileContract;
import online.cagocapps.prtracker.Data.ProfileDBHelper;

public class ProfileActivity extends AppCompatActivity {

    //views
    private EditText etUsername;
    private EditText etBirthDate;
    private EditText etWeight;
    private EditText etYearsAct;
    private Spinner spinSkill;
    private Spinner spinGender;
    private ToggleButton togUnits;


    //db vars
    private ProfileDBHelper dbHelper;
    private SQLiteDatabase dbWrite;
    //shared preferences
    private SharedPreferences sharedPreferences;

    //helpful vars
    private boolean newUser;

    private String TAG = "ProfileActivity";
    private String userEmail;
    private int userID = -1;
    private float units = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //grab views
        etUsername = (EditText) findViewById(R.id.pp_et_username);
        etBirthDate = (EditText) findViewById(R.id.pp_et_birthday);
        etWeight = (EditText) findViewById(R.id.pp_et_weight);
        etYearsAct = (EditText) findViewById(R.id.pp_et_years);
        spinSkill = (Spinner) findViewById(R.id.pp_spin_skill);
        spinGender = (Spinner) findViewById(R.id.pp_spin_gender);
        togUnits = (ToggleButton) findViewById(R.id.pp_togbutt_units);

        //set up database
        dbHelper = new ProfileDBHelper(this);
        dbWrite = dbHelper.getWritableDatabase();

        //set up shared prefs
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        userEmail = sharedPreferences.getString(getString(R.string.sp_email), "error");
        units = sharedPreferences.getFloat(getString(R.string.sp_units), 1);


        //grab intent extras
        newUser = getIntent().getBooleanExtra(getString(R.string.new_user), true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!newUser && !userEmail.equals("error")){//checks if we are making a new user or editing an old user
            String where = ProfileContract.ProfileValues.EMAIL + " = ?";
            Cursor cursor = dbWrite.query(
                    ProfileContract.ProfileValues.TABLE_NAME,
                    null,
                    where,
                    new String[] {userEmail},
                    null,
                    null,
                    null
            );
            if (cursor.moveToFirst()){
                userID = cursor.getInt(cursor.getColumnIndex(ProfileContract.ProfileValues._ID));
                etUsername.setText(cursor.getString(cursor.getColumnIndex(ProfileContract.ProfileValues.USER_NAME)));
                etBirthDate.setText( cursor.getString(cursor.getColumnIndex(ProfileContract.ProfileValues.BIRTHDATE)));
                etWeight.setText(Integer.toString(cursor.getInt(cursor.getColumnIndex(ProfileContract.ProfileValues.WEIGHT))));
                etYearsAct.setText(Integer.toString(cursor.getInt(cursor.getColumnIndex(ProfileContract.ProfileValues.YEARS_ACTIVE))));
                spinSkill.setSelection(cursor.getInt(cursor.getColumnIndex(ProfileContract.ProfileValues.SKILL))-1);
                spinGender.setSelection(cursor.getInt(cursor.getColumnIndex(ProfileContract.ProfileValues.GENDER)));
                if (units ==1) togUnits.setChecked(false);
                else togUnits.setChecked(true);
                cursor.close();

            } else {
                Log.d(TAG,"missing email in database");
            }
        }
    }

    public void SaveProfile(View view){//save/update user and return to main activity
        if(etBirthDate.getText().toString().length()==0 || etUsername.getText().toString().length()==0 || etWeight.getText().toString().length() == 0 ||
                etYearsAct.getText().toString().length() == 0){
            Toast.makeText(this,getString(R.string.pp_missing_values),Toast.LENGTH_SHORT).show();
            return;
        }
        int birthyear = Integer.valueOf(etBirthDate.getText().toString());
        String username = etUsername.getText().toString();
        String weight = etWeight.getText().toString();
        int yearsActive = Integer.valueOf(etYearsAct.getText().toString());
        if (birthyear > 1900 && birthyear < 2030) {
            ContentValues cv = new ContentValues();
            cv.put(ProfileContract.ProfileValues.EMAIL, userEmail);
            cv.put(ProfileContract.ProfileValues.USER_NAME,
                    username);
            cv.put(ProfileContract.ProfileValues.BIRTHDATE,
                    birthyear);
            cv.put(ProfileContract.ProfileValues.WEIGHT,
                    weight);
            cv.put(ProfileContract.ProfileValues.YEARS_ACTIVE,
                    yearsActive);
            cv.put(ProfileContract.ProfileValues.SKILL,
                    String.valueOf(spinSkill.getSelectedItem().toString().charAt(0)));
            int gender = 1;
            if (spinGender.getSelectedItem().toString().equals(getString(R.string.male))){
                gender = 0;
            }
            cv.put(ProfileContract.ProfileValues.GENDER, gender);
            if (userID == -1) {
                userID = (int) dbWrite.insert(ProfileContract.ProfileValues.TABLE_NAME, null, cv);
            } else
                dbWrite.update(ProfileContract.ProfileValues.TABLE_NAME, cv, ProfileContract.ProfileValues._ID + " =?", new String[]{Integer.toString(userID)});
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putInt(getString(R.string.sp_userID), userID);
            if (togUnits.isChecked()) units = (float) .453592;
            else units = 1;
            editor.putFloat(getString(R.string.sp_units), units);
            editor.commit();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else Toast.makeText(this, getString(R.string.pp_age_error), Toast.LENGTH_LONG).show();
    }
}
