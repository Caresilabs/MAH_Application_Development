package mahappdev.caresilabs.com.timr.controllers;

import mahappdev.caresilabs.com.timr.repositories.PreferenceRepository;
import mahappdev.caresilabs.com.timr.repositories.SQLRepository;

/**
 * Created by Simon on 9/10/2016.
 */
public class MainController {
    private final SQLRepository db;
    private final PreferenceRepository prefs;

    public MainController(SQLRepository db, PreferenceRepository prefs) {
        this.db = db;
        this.prefs = prefs;
    }

    public SQLRepository getDB() {
        return db;
    }

    public PreferenceRepository getPrefs() {
        return prefs;
    }
}
