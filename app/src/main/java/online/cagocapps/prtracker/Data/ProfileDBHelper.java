package online.cagocapps.prtracker.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by cgehredo on 3/13/2017.
 */

public class ProfileDBHelper extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "prTracker.db";
    public static final int DATABASE_VERSION = 0;

    public ProfileDBHelper(Context context){ super(context, DATABASE_NAME, null, DATABASE_VERSION);}

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_PROFILE_TABLE =
                "CREATE TABLE " + ProfileContract.ProfileValues.TABLE_NAME + " (" +
                        ProfileContract.ProfileValues._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ProfileContract.ProfileValues.USER_NAME + " STRING NOT NULL, " +
                        ProfileContract.ProfileValues.BIRTHDATE + " INTEGER NOT NULL, " +
                        ProfileContract.ProfileValues.WEIGHT + " INTEGER, " +
                        ProfileContract.ProfileValues.SKILL + " INTEGER NOT NULL, " +
                        ProfileContract.ProfileValues.YEARS_ACTIVE + " INTEGER NOT NULL, " +
                        ProfileContract.ProfileValues.GENDER + " INTEGER NOT NULL, " +
                        ProfileContract.ProfileValues.EMAIL + " STRING" + ");";
        final String SQL_CREATE_BARBELL_TABLE =
                "CREATE TABLE " + ProfileContract.BarbellLifts.TABLE_NAME + " (" +
                        ProfileContract.BarbellLifts._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ProfileContract.BarbellLifts.USER_ID + " INTEGER NOT NULL, " +
                        ProfileContract.BarbellLifts.LIFT + " STRING NOT NULL, " +
                        ProfileContract.BarbellLifts.REPS + " INTEGER NOT NULL, " +
                        ProfileContract.BarbellLifts.WEIGHT + " INTEGER NOT NULL, " +
                        ProfileContract.BarbellLifts.ADJUSTED_ONE_REP_MAX + " INTEGER NOT NULL, " +
                        ProfileContract.BarbellLifts.DATE + " INTEGER NOT NULL, " +
                        ProfileContract.BarbellLifts.COMMENTS + " STRING" + ");";
        final String SQL_CREATE_DUMBELL_TABLE =
                "CREATE TABLE " + ProfileContract.DumbbellLifts.TABLE_NAME + " (" +
                        ProfileContract.DumbbellLifts._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ProfileContract.DumbbellLifts.USER_ID + " INTEGER NOT NULL, " +
                        ProfileContract.DumbbellLifts.LIFT + " STRING NOT NULL, " +
                        ProfileContract.DumbbellLifts.REPS + " INTEGER NOT NULL, " +
                        ProfileContract.DumbbellLifts.WEIGHT + " INTEGER NOT NULL, " +
                        ProfileContract.DumbbellLifts.ADJUSTED_ONE_REP_MAX + " INTEGER NOT NULL, " +
                        ProfileContract.DumbbellLifts.DATE + " INTEGER NOT NULL, " +
                        ProfileContract.DumbbellLifts.COMMENTS + " STRING" + ");";
        final String SQL_CREATE_CROSSFIT_TABLE =
                "CREATE TABLE " + ProfileContract.CrossFitStandards.TABLE_NAME + " (" +
                        ProfileContract.CrossFitStandards._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ProfileContract.CrossFitStandards.USER_ID + " INTEGER NOT NULL, " +
                        ProfileContract.CrossFitStandards.LIFT + " STRING NOT NULL, " +
                        ProfileContract.CrossFitStandards.RX + " INTEGER, " +
                        ProfileContract.CrossFitStandards.ROUNDS + " INTEGER, " +
                        ProfileContract.CrossFitStandards.REPS + " INTEGER, " +
                        ProfileContract.CrossFitStandards.TIME + " INTEGER, " +
                        ProfileContract.CrossFitStandards.DATE + " INTEGER NOT NULL, " +
                        ProfileContract.CrossFitStandards.COMMENTS + " STRING" + ");";
        final String SQL_CREATE_GYMNASTICS_TABLE =
                "CREATE TABLE " + ProfileContract.Gymnastics.TABLE_NAME + " (" +
                        ProfileContract.Gymnastics._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ProfileContract.Gymnastics.USER_ID + " INTEGER NOT NULL, " +
                        ProfileContract.Gymnastics.LIFT + " STRING NOT NULL, " +
                        ProfileContract.Gymnastics.ROUNDS + " INTEGER NOT NULL, " +
                        ProfileContract.Gymnastics.REPS + " INTEGER NOT NULL, " +
                        ProfileContract.Gymnastics.DATE + " INTEGER NOT NULL, " +
                        ProfileContract.Gymnastics.COMMENTS + " STRING" + ");";

        final String SQL_CREATE_RUNNING_TABLE =
                "CREATE TABLE " + ProfileContract.Running.TABLE_NAME + " (" +
                        ProfileContract.Running._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ProfileContract.Running.USER_ID + " INTEGER NOT NULL, " +
                        ProfileContract.Running.DISTANCE + " INTEGER NOT NULL, " +
                        ProfileContract.Running.TIME + " INTEGER NOT NULL, " +
                        ProfileContract.Running.DATE + " INTEGER NOT NULL, " +
                        ProfileContract.Running.COMMENTS + " STRING" + ");";
        final String SQL_CREATE_SWIMMING_TABLE =
                "CREATE TABLE " + ProfileContract.Swimming.TABLE_NAME + " (" +
                        ProfileContract.Swimming._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ProfileContract.Swimming.USER_ID + " INTEGER NOT NULL, " +
                        ProfileContract.Swimming.STROKE + " STRING NOT NULL, " +
                        ProfileContract.Swimming.DISTANCE + " INTEGER NOT NULL, " +
                        ProfileContract.Swimming.TIME + " INTEGER NOT NULL, " +
                        ProfileContract.Swimming.DATE + " INTEGER NOT NULL, " +
                        ProfileContract.Swimming.COMMENTS + " STRING" + ");";

        sqLiteDatabase.execSQL(SQL_CREATE_PROFILE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_BARBELL_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CROSSFIT_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_GYMNASTICS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_DUMBELL_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_RUNNING_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SWIMMING_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVer, int newVer) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ProfileContract.Swimming.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ProfileContract.Running.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ProfileContract.Gymnastics.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ProfileContract.ProfileValues.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ProfileContract.BarbellLifts.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ProfileContract.DumbbellLifts.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ProfileContract.CrossFitStandards.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }
}
