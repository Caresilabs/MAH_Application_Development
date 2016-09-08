package mahappdev.caresilabs.com.timr.repositories;

import mahappdev.caresilabs.com.timr.models.DataModel;

/**
 * Created by Simon on 9/8/2016.
 */
public interface IRepository<T extends DataModel> {
    void insert(T model);

    void update(T model);

    void remove(T model);
}
