package online.cagocapps.prtracker;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import online.cagocapps.prtracker.Data.ProfileDBHelper;

public class ProfileActivity extends AppCompatActivity {


    //db vars
    private ProfileDBHelper dbHelper;
    private SQLiteDatabase dbWrite;

    private SharedPreferences sharedPreferences;
    private boolean newUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //set up database
        dbHelper = new ProfileDBHelper(this);
        dbWrite = dbHelper.getWritableDatabase();

        //set up shared prefs
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //grab intent extras
        newUser = getIntent().getBooleanExtra(getString(R.string.new_user), true);
    }
}
