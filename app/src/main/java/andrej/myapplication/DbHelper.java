package andrej.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Andrej on 25. 8. 2015.
 */
public class DbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Data.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        String createQuery = "CREATE TABLE btns ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, text TEXT NOT NULL, language TEXT NOT NULL)";
        db.execSQL(createQuery);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

//    public void addBtnEntry(SQLiteDatabase db, String name, String text, String language) {
//        String addQuery = "INSERT INTO btns VALUES (" + name + ", " + text + ", " + language + ")";
//        db.execSQL(addQuery);
//    }
//
//    public void rmvBtnEntry(SQLiteDatabase db, Integer id) {
//        String addQuery = "DELETE FROM btns WHERE _id=" + id.toString();
//        db.execSQL(addQuery);
//    }
}