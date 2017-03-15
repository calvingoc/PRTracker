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


    //db vars
    private ProfileDBHelper dbHelper;
    private SQLiteDatabase dbWrite;

    private SharedPreferences sharedPreferences;
    private boolean newUser;

    private String TAG = "ProfileActivity";
    private String userEmail;
    private String userID;


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

        //set up database
        dbHelper = new ProfileDBHelper(this);
        dbWrite = dbHelper.getWritableDatabase();

        //set up shared prefs
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        userEmail = sharedPreferences.getString(getString(R.string.sp_email), "error");


        //grab intent extras
        newUser = getIntent().getBooleanExtra(getString(R.string.new_user), true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!newUser && !userEmail.equals("error")){
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
                userID = cursor.getString(cursor.getColumnIndex(ProfileContract.ProfileValues._ID));
                etUsername.setText(cursor.getString(cursor.getColumnIndex(ProfileContract.ProfileValues.USER_NAME)));
                etBirthDate.setText( cursor.getString(cursor.getColumnIndex(ProfileContract.ProfileValues.BIRTHDATE)));
                etWeight.setText(Integer.toString(cursor.getInt(cursor.getColumnIndex(ProfileContract.ProfileValues.WEIGHT))));
                etYearsAct.setText(Integer.toString(cursor.getInt(cursor.getColumnIndex(ProfileContract.ProfileValues.YEARS_ACTIVE))));
                spinSkill.setSelection(cursor.getInt(cursor.getColumnIndex(ProfileContract.ProfileValues.SKILL))-1);
                spinGender.setSelection(cursor.getInt(cursor.getColumnIndex(ProfileContract.ProfileValues.GENDER)));
                cursor.close();

            } else {
                Log.d(TAG,"missing email in database");
            }
        }
    }

    public void SaveProfile(View view){
        int birthyear = Integer.valueOf(etBirthDate.getText().toString());
        if (birthyear > 1900 && birthyear < 2030) {
            ContentValues cv = new ContentValues();
            cv.put(ProfileContract.ProfileValues.EMAIL, userEmail);
            cv.put(ProfileContract.ProfileValues.USER_NAME,
                    etUsername.getText().toString());
            cv.put(ProfileContract.ProfileValues.BIRTHDATE,
                    etBirthDate.getText().toString());
            cv.put(ProfileContract.ProfileValues.WEIGHT,
                    etWeight.getText().toString());
            cv.put(ProfileContract.ProfileValues.YEARS_ACTIVE,
                    Integer.valueOf(etYearsAct.getText().toString()));
            cv.put(ProfileContract.ProfileValues.SKILL,
                    String.valueOf(spinSkill.getSelectedItem().toString().charAt(0)));
            int gender = 1;
            if (spinGender.getSelectedItem().toString().equals(getString(R.string.male))){
                gender = 0;
            }
            cv.put(ProfileContract.ProfileValues.GENDER, gender);
            if (userID == null) {
                dbWrite.insert(ProfileContract.ProfileValues.TABLE_NAME, null, cv);
            } else
                dbWrite.update(ProfileContract.ProfileValues.TABLE_NAME, cv, ProfileContract.ProfileValues._ID + " =?", new String[]{userID});
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else Toast.makeText(this, getString(R.string.pp_age_error), Toast.LENGTH_LONG).show();
    }
}
