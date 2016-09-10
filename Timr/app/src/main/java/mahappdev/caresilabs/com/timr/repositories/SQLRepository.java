package mahappdev.caresilabs.com.timr.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mahappdev.caresilabs.com.timr.models.DataModel;
import mahappdev.caresilabs.com.timr.models.ProfileModel;

/**
 * Created by Simon on 9/8/2016.
 */
public abstract class SQLRepository extends SQLiteOpenHelper implements IRepository<DataModel> {
    private static final int DATABASE_VERSION = 3;

    protected SQLiteDatabase db;

    public SQLRepository(Context context, String dbName) {
        super(context, dbName, null, DATABASE_VERSION);
        db = getWritableDatabase();
    }

    @Override
    public void put(DataModel model) {
        ContentValues values = new ContentValues();
        for (Field f : model.getClass().getFields()) {
            String name = f.getName();

            // Don't allow setting id
            if (name.equals("id"))
                continue;;

            try {
                if (int.class.isAssignableFrom(f.getType())) {
                    values.put(name, f.getInt(model));
                } else if (float.class.isAssignableFrom(f.getType())) {
                    values.put(name, f.getFloat(model));
                } else if (String.class.isAssignableFrom(f.getType())) {
                    values.put(name, (String) f.get(model));
                } else if (Date.class.isAssignableFrom(f.getType())) {
                    values.put(name, ((Date) f.get(model)).getTime());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (model.id == 0) {
            db.insert(model.getClass().getSimpleName(), null, values);
        } else {
            db.update(model.getClass().getSimpleName(), values, "id=" + model.id, null);
        }
    }

    @Override
    public void remove(DataModel model) {
        db.delete(model.getClass().getSimpleName(), "id=" + model.id, null);
    }

    protected void createTable(Class<? extends DataModel> model, SQLiteDatabase createDb) {
        String query = "CREATE TABLE " + model.getSimpleName() + " (\n";
        for (Field f : model.getFields()) {
            if (f.getName().equals("id")) {
                query += f.getName() + " integer primary key autoincrement,\n";
            } else if (int.class.isAssignableFrom(f.getType())) {
                query += f.getName() + " integer,\n";
            } else if (float.class.isAssignableFrom(f.getType())) {
                query += f.getName() + " decimal,\n";
            } else if (String.class.isAssignableFrom(f.getType())) {
                query += f.getName() + " varchar(255),\n";
            } else if (Date.class.isAssignableFrom(f.getType())) {
                query += f.getName() + " integer,\n";
            }
        }
        query = query.substring(0, query.length() - 2) + "\n);";

        createDb.execSQL(query);
    }

    @Override
    public <A extends DataModel> A get(Class<A> model, int id) {
        Cursor cursor = db.query(model.getSimpleName(), null, "id=" + id, null, null, null, null);
        try {
            A a = model.newInstance();

            if (cursor.moveToFirst()) {
                for (Field f : model.getFields()) {
                    int index = cursor.getColumnIndex(f.getName());

                    Object o = null;
                    if (int.class.isAssignableFrom(f.getType())) {
                        o = cursor.getInt(index);
                    } else if (float.class.isAssignableFrom(f.getType())) {
                        o = cursor.getFloat(index);
                    } else if (String.class.isAssignableFrom(f.getType())) {
                        o = cursor.getString(index);
                    } else if (Date.class.isAssignableFrom(f.getType())) {
                        o = new Date(cursor.getLong(index));
                    }
                    f.set(a, o);
                }

                cursor.close();
                return a;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        cursor.close();
        return null;
    }

    @Override
    public <A extends DataModel> List<A> get(Class<A> model, String where) {
        Cursor cursor = db.query(model.getSimpleName(), null, where, null, null, null, null);

        List<A> list = new ArrayList<>();

        while (cursor.moveToNext()) {
            try {
                A a = model.newInstance();
                for (Field f : model.getFields()) {
                    int index = cursor.getColumnIndex(f.getName());

                    if (index == -1)
                        continue;

                    Object o = null;
                    if (int.class.isAssignableFrom(f.getType())) {
                        o = cursor.getInt(index);
                    } else if (float.class.isAssignableFrom(f.getType())) {
                        o = cursor.getFloat(index);
                    } else if (String.class.isAssignableFrom(f.getType())) {
                        o = cursor.getString(index);
                    } else if (Date.class.isAssignableFrom(f.getType())) {
                        o = new Date(cursor.getLong(index));
                    }
                    f.set(a, o);
                }
                list.add(a);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        cursor.close();
        return list;
    }
}
