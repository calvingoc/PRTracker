package online.cagocapps.prtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import online.cagocapps.prtracker.Data.ProfileContract;
import online.cagocapps.prtracker.Data.ProfileDBHelper;

public class MainActivity extends AppCompatActivity{

    private String email;
    private String TAG = "Main Activity";

    private ProfileDBHelper dbHelper;
    private SQLiteDatabase dbWrite;

    private RecyclerView rvMainActivity;
    private MainActivityRecycAdapter mainAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new ProfileDBHelper(this);
        dbWrite = dbHelper.getWritableDatabase();

        rvMainActivity = (RecyclerView) findViewById(R.id.ma_rv_results);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        rvMainActivity.setLayoutManager(layoutManager);
        rvMainActivity.setHasFixedSize(true);
        mainAdapter = new MainActivityRecycAdapter();
        rvMainActivity.setAdapter(mainAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AddResult.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainAdapter.notifyDataSetChanged();
        email = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.sp_email), null);
        if (email == null) {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        } else {
            String where = ProfileContract.ProfileValues.EMAIL + " = ?";
            Cursor cursor = dbWrite.query(
                    ProfileContract.ProfileValues.TABLE_NAME,
                    null,
                    where,
                    new String[]{email},
                    null,
                    null,
                    null
            );
            if (cursor.moveToFirst()) {
                String ID = cursor.getString(cursor.getColumnIndex(ProfileContract.ProfileValues._ID));
                String userName = (cursor.getString(cursor.getColumnIndex(ProfileContract.ProfileValues.USER_NAME)));
                cursor.close();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                editor.putInt(getString(R.string.sp_userID), Integer.valueOf(ID));
                editor.commit();

                mainAdapter.setUserID(ID);
                TextView textViewUserName = (TextView) findViewById(R.id.ma_tv_username);
                textViewUserName.setText(userName);



            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.edit_profile){
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra(getString(R.string.new_user), false);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

}
