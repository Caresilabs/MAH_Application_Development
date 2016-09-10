package mahappdev.caresilabs.com.timr.repositories;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.List;

import mahappdev.caresilabs.com.timr.models.DataModel;
import mahappdev.caresilabs.com.timr.models.ExpenditureModel;
import mahappdev.caresilabs.com.timr.models.IncomeModel;
import mahappdev.caresilabs.com.timr.models.ProfileModel;
import mahappdev.caresilabs.com.timr.models.TimeItem;

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
        createTable(IncomeModel.class, db);
        createTable(ExpenditureModel.class, db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TimrSQLRepository.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");

        // Drop all tables
        db.execSQL("DROP TABLE IF EXISTS " + IncomeModel.class.getSimpleName());
        db.execSQL("DROP TABLE IF EXISTS " + ExpenditureModel.class.getSimpleName());

        onCreate(db);
    }
}
