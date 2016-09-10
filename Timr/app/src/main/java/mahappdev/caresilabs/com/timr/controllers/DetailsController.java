package mahappdev.caresilabs.com.timr.controllers;

import mahappdev.caresilabs.com.timr.models.TimeItem;
import mahappdev.caresilabs.com.timr.repositories.SQLRepository;
import mahappdev.caresilabs.com.timr.repositories.TimrSQLRepository;
import mahappdev.caresilabs.com.timr.views.DetailsFragment;

/**
 * Created by Simon on 9/10/2016.
 */
public class DetailsController {
    private SQLRepository db;

    public DetailsController(DetailsFragment detailsFragment, SQLRepository db ) {
        this.db = db;
    }

    public void updateModel(TimeItem model) {
        db.put(model);
    }
}
