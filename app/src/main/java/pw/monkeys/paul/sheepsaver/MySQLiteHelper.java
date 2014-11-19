package pw.monkeys.paul.sheepsaver;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Paul on 11/19/2014.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Passwords";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_COMMENTS = "passwords";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DOMAIN = "domain";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_STRENGTH = "strength";
    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_COMMENTS + "(" + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_DOMAIN + " text not null,"
            + COLUMN_USERNAME + " text,"  //should be nullable
            + COLUMN_PASSWORD + " text not null,"
            + COLUMN_STRENGTH + " INT not null);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
        onCreate(db);
    }

}