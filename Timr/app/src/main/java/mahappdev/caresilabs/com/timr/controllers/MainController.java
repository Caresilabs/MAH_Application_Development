package mahappdev.caresilabs.com.timr.controllers;

import android.content.Intent;

import mahappdev.caresilabs.com.timr.FragmentType;
import mahappdev.caresilabs.com.timr.models.ProfileModel;
import mahappdev.caresilabs.com.timr.repositories.PreferenceRepository;
import mahappdev.caresilabs.com.timr.repositories.SQLRepository;
import mahappdev.caresilabs.com.timr.views.DetailsFragment;
import mahappdev.caresilabs.com.timr.views.MainActivity;
import mahappdev.caresilabs.com.timr.views.ProfileActivity;
import mahappdev.caresilabs.com.timr.views.SummaryFragment;

/**
 * Created by Simon on 9/10/2016.
 */
public class MainController {
    private final SQLRepository db;
    private final PreferenceRepository prefs;

    private final MainActivity    activity;

    public MainController(MainActivity activity, SQLRepository db, PreferenceRepository prefs) {
        this.db = db;
        this.prefs = prefs;
        this.activity = activity;
    }

    public void updateAndCheckUserProfile() {
        // if we haven't filled in profile yet.
        final ProfileModel profile = prefs.get(ProfileModel.class, 0);
        if (profile == null) {
            activity.launchEditProfile();
        } else {
            activity.updateDrawerUserInfo(String.format("%s %s", profile.firstName, profile.lastName), profile.email);
        }
    }

    public void switchFragment(FragmentType type) {
        activity.switchFragment(type);
    }

    public SQLRepository getDB() {
        return db;
    }

    public PreferenceRepository getPrefs() {
        return prefs;
    }


}
