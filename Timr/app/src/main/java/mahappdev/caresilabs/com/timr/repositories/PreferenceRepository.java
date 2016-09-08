package mahappdev.caresilabs.com.timr.repositories;

import android.content.SharedPreferences;

import com.google.gson.Gson;

import mahappdev.caresilabs.com.timr.models.DataModel;

/**
 * Created by Simon on 9/8/2016.
 */
public class PreferenceRepository implements IRepository<DataModel> {
    private SharedPreferences prefs;

    public PreferenceRepository(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    @Override
    public void insert(DataModel model) {
        String values = new Gson().toJson(model);
        prefs.edit().putString(model.getClass().getName(), values).apply();
    }

    @Override
    public void update(DataModel model) {
        String values = new Gson().toJson(model);
        prefs.edit().putString(model.getClass().getName(), values).apply();
    }

    @Override
    public void remove(DataModel model) {
        prefs.edit()
                .remove(model.getClass().getName())
                .apply();
    }
}
