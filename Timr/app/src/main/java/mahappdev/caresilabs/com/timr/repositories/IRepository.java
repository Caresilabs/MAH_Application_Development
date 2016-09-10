package mahappdev.caresilabs.com.timr.repositories;

import java.util.List;

import mahappdev.caresilabs.com.timr.models.DataModel;

/**
 * Created by Simon on 9/8/2016.
 */
public interface IRepository<T extends DataModel> {

    void put(T model);

    void remove(T model);

    <A extends T> A get(Class<A> model, int id);

    <A extends T> List<A> get(Class<A> model, String where);

}
