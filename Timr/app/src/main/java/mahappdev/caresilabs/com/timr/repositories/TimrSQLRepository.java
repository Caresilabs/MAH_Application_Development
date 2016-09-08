package mahappdev.caresilabs.com.timr.repositories;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import mahappdev.caresilabs.com.timr.models.ProfileModel;

/**
 * Created by Simon on 9/8/2016.
 */
public class TimrSQLRepository extends  SQLRepository {
    public static final String DB_NAME = "timr.db";

    public TimrSQLRepository(Context context) {
        super(context, DB_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(ProfileModel.class);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TimrSQLRepository.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");

        // Drop all tables
        db.execSQL("DROP TABLE IF EXISTS " + ProfileModel.class.getName());

        onCreate(db);
    }
}
