package mahappdev.caresilabs.com.timr.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.Field;

import mahappdev.caresilabs.com.timr.models.DataModel;
import mahappdev.caresilabs.com.timr.models.ProfileModel;

/**
 * Created by Simon on 9/8/2016.
 */
public abstract class SQLRepository extends SQLiteOpenHelper implements IRepository<DataModel> {
    private static final int DATABASE_VERSION = 1;

    protected SQLiteDatabase db;

    public SQLRepository(Context context, String dbName) {
        super(context, dbName, null, DATABASE_VERSION);
        db = getWritableDatabase();
    }

    @Override
    public void insert(DataModel model) {
        ContentValues values = new ContentValues();
        for (Field f :model.getClass().getFields()) {
            String name = f.getName();

            try {
                if (Integer.class.isAssignableFrom(f.getType())) {
                    values.put(name, f.getInt(model));
                } else if (Float.class.isAssignableFrom(f.getType())) {
                    values.put(name, f.getFloat(model));
                } else if (String.class.isAssignableFrom(f.getType())) {
                    values.put(name, (String)f.get(model));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        db.insert(model.getClass().getName(), null, values );
    }

    @Override
    public void update(DataModel model) {

    }

    @Override
    public void remove(DataModel model) {
        db.delete(model.getClass().getName(), "id=" + model.id ,null);
    }

    protected void createTable(Class<? extends  DataModel> model) {
        String query = "CREATE TABLE " + model.getName() + " (\n";
        for (Field f :model.getClass().getFields()) {
            if (Integer.class.isAssignableFrom(f.getType())) {
                query += f.getName() + " int,\n";
            } else if (Float.class.isAssignableFrom(f.getType())) {
                query += f.getName() + " decimal,\n";
            } else if (String.class.isAssignableFrom(f.getType())) {
                query += f.getName() + " varchar(255),\n";
            }
        }
        query += "\n);";

        db.execSQL(query);
    }

}
