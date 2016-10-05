package mahappdev.caresilabs.com.myfriends.repository;

import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.List;

import mahappdev.caresilabs.com.myfriends.models.ProfileModel;

/**
 * Created by Simon on 9/8/2016.
 */
public class PreferenceRepository implements IRepository<ProfileModel> {
    private SharedPreferences prefs;

    public PreferenceRepository(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    @Override
    public void put(ProfileModel model) {
        String values = new Gson().toJson(model);
        prefs.edit().putString(model.getClass().getSimpleName(), values).apply();
    }

    @Override
    public void remove(ProfileModel model) {
        prefs.edit()
                .remove(model.getClass().getSimpleName())
                .apply();
    }

    @Override
    public <A extends ProfileModel> A get(Class<A> model, int id) {
        String data = prefs.getString(model.getSimpleName(), "");
        A datamodel = new Gson().fromJson(data, model);
        return datamodel;
    }

    @Override
    public <A extends ProfileModel> List<A> get(Class<A> model, String where) {
        return null;
    }
}
