package data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Kiara on 6/4/2016.
 */
public class MovieDBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "reviews";
    public static final int VERSION = 1;

    public MovieDBHelper(Context context)
    {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Put SQL create table statements here
        //REMEMBER
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.Review.TABLE_NAME + " (" +
                MovieContract.Review._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.Review.DISPLAY_TITLE + " TEXT NOT NULL, " +
                MovieContract.Review.SUMMARY_SHORT + " TEXT NOT NULL, " +
                MovieContract.Review.PUBLISH_DATE + " TEXT NOT NULL, " +
                MovieContract.Review.REVIEW_SCORE + " REAL" + " );";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.Review.TABLE_NAME);
        onCreate(db);
    }
}
