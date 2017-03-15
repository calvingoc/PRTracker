package online.cagocapps.prtracker.Data;

import android.provider.BaseColumns;

/**
 * Created by cgehredo on 3/10/2017.
 */

public class ProfileContract {
    public static final class ProfileValues implements BaseColumns{
        public static final String TABLE_NAME = "userProfile";
        public static final String USER_NAME ="userName";
        public static final String BIRTHDATE = "birthdate";
        public static final String WEIGHT = "weight";
        public static final String SKILL = "skill";
        public static final String YEARS_ACTIVE = "yearsActive";
        public static final String EMAIL = "email";
        public static final String GENDER = "gender";
    }

    public static final class BarbellLifts implements BaseColumns{
        public static final String TABLE_NAME = "barbellLifts";
        public static final String USER_ID = "userID";
        public static final String REPS = "reps";
        public static final String WEIGHT = "weight";
        public static final String ADJUSTED_ONE_REP_MAX = "adjustedOneRepMax";
        public static final String LIFT = "lift";
        public static final String DATE = "date";
        public static final String COMMENTS = "comments";
        public static final String ROUNDS = "rounds";


        public static final String SQUAT_SNATCH = "squatSnatch";
        public static final String POWER_SNATCH = "powerSnatch";
        public static final String HANG_SQUAT_SNATCH = "hangSquatSnatch";
        public static final String HANG_POWER_SNATCH = "hangPowerSnatch";
        public static final String SQUAT_CLEAN = "squatClean";
        public static final String POWER_CLEAN = "powerClean";
        public static final String HANG_SQUAT_CLEAN = "hangSquatClean";
        public static final String HANG_POWER_CLEAN = "hangPowerClean";
        public static final String CLEAN_JERK = "cleanAndJerk";
        public static final String BACK_SQUAT = "backSquat";
        public static final String FRONT_SQUAT = "frontSquat";
        public static final String OVERHEAD_SQUAT = "overheadSquat";
        public static final String BENCH = "bench";
        public static final String INCLINE_BENCH = "incline_bench";
        public static final String DECLINE_BENCH = "decline_bench";
        public static final String CLOSE_GRIP_BENCH = "closeGripBench";
        public static final String SKULL_CRUSHER = "skullCrusher";
        public static final String SHOULDER_PRESS ="shoulderPress";
        public static final String JERK_SHOULDER_PRESS ="jerkShoulderPress";
        public static final String DEADLIFT = "deadlift";
        public static final String CURL = "Curl";
        public static final String RDL = "RDL";
        public static final String ROW = "Row";
    }

    public static final class DumbbellLifts implements BaseColumns{
        public static final String TABLE_NAME = "dumbbellLifts";
        public static final String USER_ID = BarbellLifts.USER_ID;
        public static final String LIFT = BarbellLifts.LIFT;
        public static final String REPS = BarbellLifts.REPS;
        public static final String WEIGHT = BarbellLifts.WEIGHT;
        public static final String ADJUSTED_ONE_REP_MAX = BarbellLifts.ADJUSTED_ONE_REP_MAX;
        public static final String DATE = BarbellLifts.DATE;
        public static final String COMMENTS = BarbellLifts.COMMENTS;
        public static final String ROUNDS = BarbellLifts.ROUNDS;


        public static final String SQUAT_SNATCH = "squatSnatch";
        public static final String BENCH = "bench";
        public static final String INCLINE_BENCH = "incline_bench";
        public static final String DECLINE_BENCH = "decline_bench";
        public static final String SKULL_CRUSHER = "skullCrusher";
        public static final String SHOULDER_PRESS ="shoulderPress";
        public static final String POWER_CLEAN = "powerClean";
        public static final String TRI_EXTENSION = "triExtension";
        public static final String ROW = "Row";
        public static final String GOBLET_SQUAT = "gobletSquat";
        public static final String CURL = "Curl";
    }

    public static final class CrossFitStandards implements BaseColumns{
        public static final String TABLE_NAME = "crossFit";
        public static final String USER_ID = BarbellLifts.USER_ID;
        public static final String LIFT = BarbellLifts.LIFT;
        public static final String RX = "rx";
        public static final String ROUNDS = BarbellLifts.ROUNDS;
        public static final String REPS = BarbellLifts.REPS;
        public static final String TIME = "time";
        public static final String DATE = BarbellLifts.DATE;
        public static final String COMMENTS = BarbellLifts.COMMENTS;


        public static final String FRAN = "fran";
        public static final String CINDY = "cindy";
    }

    public static final class Gymnastics implements BaseColumns{
        public static final String TABLE_NAME = "gymnastics";
        public static final String USER_ID = BarbellLifts.USER_ID;
        public static final String LIFT = BarbellLifts.LIFT;
        public static final String ROUNDS = BarbellLifts.ROUNDS;
        public static final String REPS = BarbellLifts.REPS;
        public static final String DATE = BarbellLifts.DATE;
        public static final String COMMENTS = BarbellLifts.COMMENTS;
    }

    public static final class Running implements BaseColumns{
        public static final String TABLE_NAME = "running";
        public static final String USER_ID = BarbellLifts.USER_ID;
        public static final String DISTANCE = BarbellLifts.LIFT;
        public static final String TIME = CrossFitStandards.TIME;
        public static final String DATE = BarbellLifts.DATE;
        public static final String COMMENTS = BarbellLifts.COMMENTS;
    }

    public static final class Swimming implements BaseColumns{
        public static final String TABLE_NAME = "swimming";
        public static final String USER_ID = BarbellLifts.USER_ID;
        public static final String STROKE = BarbellLifts.LIFT;
        public static final String TIME = CrossFitStandards.LIFT;
        public static final String DATE = BarbellLifts.DATE;
        public static final String COMMENTS = BarbellLifts.COMMENTS;
    }

    public static final class RecentLifts implements BaseColumns{
        public static final String TABLE_NAME = "recentLifts";
        public static final String RESULT_ONE_TABLE = "resultOneTable";
        public static final String RESULT_ONE_ID = "resultOneID";
        public static final String RESULT_TWO_TABLE = "resultTwoTable";
        public static final String RESULT_TWO_ID = "resultTwoID";
        public static final String RESULT_THREE_TABLE = "resultThreeTable";
        public static final String RESULT_THREE_ID = "resultThreeID";
        public static final String RESULT_FOUR_TABLE = "resultFourTable";
        public static final String RESULT_FOUR_ID = "resultFourID";
        public static final String RESULT_FIVE_TABLE = "resultFiveTable";
        public static final String RESULT_FIVE_ID = "resultFiveID";
    }
}
