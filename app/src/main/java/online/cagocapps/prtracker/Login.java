package online.cagocapps.prtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.FirebaseDatabase;

import online.cagocapps.prtracker.Data.ProfileContract;
import online.cagocapps.prtracker.Data.ProfileDBHelper;

public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener
,View.OnClickListener{
    //log in variables
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    public GoogleSignInAccount account;

    //db vars
    private ProfileDBHelper dbHelper;
    private SQLiteDatabase dbWrite;


    private String TAG = "login";

    /**
     * set up activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //set up database
        dbHelper = new ProfileDBHelper(this);
        dbWrite = dbHelper.getWritableDatabase();

        //set up log in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mAuth = FirebaseAuth.getInstance();
        //check if authentication went through correctly
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        findViewById(R.id.sign_in_button).setOnClickListener(this);
    }

    /**
     * sets up authorization listener
     */
    @Override
    protected void onStart() {
        super.onStart();
        // link authorization listener for log in
        mAuth.addAuthStateListener(mAuthListener);
    }

    /**
     * show error if log in failed
     * @param connectionResult holds result status
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        findViewById(R.id.sign_in_button).setEnabled(false);
        Toast.makeText(this, getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
    }

    /**
     * handle log in button getting clicked
     * @param v view that was clicked
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    /**
     * launch the log in through google authentication
     */
    private void signIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
        mGoogleApiClient.connect();
    }

    /**
     * handle google log in result
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    /**
     * if log in is succesful launch the correct activity to move forward
     * @param result
     */
    private void handleSignInResult(GoogleSignInResult result){
        if (result.isSuccess()){//log in worked, now see if that user has a profile
            account = result.getSignInAccount();
            firebaseAuthWithGoogle(account);
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putString(getString(R.string.sp_email), account.getEmail());
            String where = ProfileContract.ProfileValues.EMAIL + " = ?";
            Cursor cursor = dbWrite.query(
                    ProfileContract.ProfileValues.TABLE_NAME,
                    null,
                    where,
                    new String[] {account.getEmail()},
                    null,
                    null,
                    null
            );
            if (cursor.moveToFirst()){// has a profile move to main activity
                editor.putInt(getString(R.string.sp_userID), cursor.getInt(cursor.getColumnIndex(ProfileContract.ProfileValues._ID)));
                cursor.close();
                editor.commit();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } else { // no profile, bring them to the profile activity
                cursor.close();
                editor.commit();
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra(getString(R.string.new_user), true);
                startActivity(intent);
                finish();

            }
        } else {//log in failed show error message
            Toast.makeText(this, getString(R.string.failed_login),Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * set up database with successful credentials
     * @param account
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount account){
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        try {
                            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                        }catch (DatabaseException e){
                            Log.d(TAG,"Persistence exception " + e);
                        }
                        }
                    }

        );
    }



    @Override
    protected void onStop() {
        super.onStop();

        //unlink log in auth listener
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbWrite.close();
    }
}
