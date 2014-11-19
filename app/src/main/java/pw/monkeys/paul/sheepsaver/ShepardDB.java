package pw.monkeys.paul.sheepsaver;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul on 11/19/2014.
 */
public class ShepardDB {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {
            MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_DOMAIN,
            MySQLiteHelper.COLUMN_USERNAME,
            MySQLiteHelper.COLUMN_PASSWORD,
            MySQLiteHelper.COLUMN_STRENGTH
            };

    public ShepardDB(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public PasswordItem createStoredPassword(String domain, String username, String cryptPassword, int Strength) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_DOMAIN, domain);
        values.put(MySQLiteHelper.COLUMN_USERNAME, username);
        values.put(MySQLiteHelper.COLUMN_PASSWORD, cryptPassword);
        values.put(MySQLiteHelper.COLUMN_STRENGTH, Strength);
        long insertId = database.insert(MySQLiteHelper.TABLE_COMMENTS, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_COMMENTS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        PasswordItem newPasswordItem = cursorToStoredPassword(cursor);
        cursor.close();
        return newPasswordItem;
    }

    public void deleteStoredPassword(long id) {
        System.out.println("StoredPassword deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_COMMENTS, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<PasswordItem> getAllStoredPasswords() {
        List<PasswordItem> passwords = new ArrayList<PasswordItem>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_COMMENTS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            PasswordItem password = cursorToStoredPassword(cursor);
            passwords.add(password);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return passwords;
    }

    private PasswordItem cursorToStoredPassword(Cursor cursor) {
        PasswordItem password = new PasswordItem();
        password.setId(cursor.getLong(0));
        password.setStoredPassword(cursor.getString(1));
        return password;
    }
}