package csula.kiarawestbrooks.moviereview;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.ApplicationTestCase;
import android.util.Log;

import java.io.File;
import java.util.Map;
import java.util.Set;

import data.MovieContract;
import data.MovieDBHelper;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    Context context;

    public ApplicationTest() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        context = getContext();
    }

    public void testInsertMovieData()
    {
        MovieDBHelper helper = new MovieDBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        //Create content values with Boruto: Naruto the Movie information
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.Review.DISPLAY_TITLE, "Boruto: Naruto the Movie");
        cv.put(MovieContract.Review.SUMMARY_SHORT, "Naruto has a kid, who cheats in the chunin exam");
        cv.put(MovieContract.Review.PUBLISH_DATE, "12-12-2015");
        cv.put(MovieContract.Review.REVIEW_SCORE, 7.5);

        Cursor movieCursor = null;
        try
        {
            long movieID = db.insert(MovieContract.Review.TABLE_NAME, null, cv);
            Log.d("ApplicationTest", " movie ID -> " + movieID);
            assertTrue(movieID != -1);

            movieCursor = db.query(MovieContract.Review.TABLE_NAME, null, null, null, null, null, null);
            assertTrue(validateCurrentRecord("The records match", movieCursor, cv));

        }catch(Exception error)
        {
            Log.e("ApplicationException", error.getMessage());
        }finally
        {
            Log.d("ApplicationTest", " in FINALLY clause ");
            long deleteID = db.delete(MovieContract.Review.TABLE_NAME, null, null);
            Log.d("ApplicationTest", " delete ID -> " + deleteID);

            String movieCountQuery = "SELECT * FROM " + MovieContract.Review.TABLE_NAME;
            movieCursor = db.rawQuery(movieCountQuery, null);
            int movieCount = movieCursor.getCount();
            Log.d("ApplicationTest", " rows left: " + movieCount);

            if(movieCursor != null)
            {
                movieCursor.close();
            }
            if(db != null)
            {
                db.close();
            }
        }
    }

    static boolean validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues)
    {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for(Map.Entry<String, Object> entry : valueSet)
        {
            String columnName = entry.getKey();
            int index = valueCursor.getColumnIndex(columnName);
            if(index == -1) //i.e. the record does not exist
            {
                return false;
            }
            String expectedValue = entry.getValue().toString();
            String retrievedValue = valueCursor.getString(index);
            if(!retrievedValue.equalsIgnoreCase(expectedValue))
            {
                return false;
            }
        }
        return true;
    }
}