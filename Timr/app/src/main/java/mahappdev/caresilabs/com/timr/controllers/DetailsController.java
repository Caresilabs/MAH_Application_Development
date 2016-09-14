package mahappdev.caresilabs.com.timr.controllers;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import mahappdev.caresilabs.com.timr.DetailedListAdapter;
import mahappdev.caresilabs.com.timr.models.ExpenditureModel;
import mahappdev.caresilabs.com.timr.models.IncomeModel;
import mahappdev.caresilabs.com.timr.models.TimeItem;
import mahappdev.caresilabs.com.timr.repositories.SQLRepository;
import mahappdev.caresilabs.com.timr.repositories.TimrSQLRepository;
import mahappdev.caresilabs.com.timr.views.DetailsFragment;
import mahappdev.caresilabs.com.timr.views.MainActivity;

/**
 * Created by Simon on 9/10/2016.
 */
public class DetailsController {
    private final DetailsFragment fragment;
    private final SQLRepository   db;

    private Date fromIncomeDate;
    private Date toIncomeDate;

    private Date fromExpenditureDate;
    private Date toExpenditureDate;

    public DetailsController(DetailsFragment detailsFragment, SQLRepository db) {
        this.db = db;
        this.fragment = detailsFragment;
    }

    public void onInit(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Restore data
            fromIncomeDate = new Date(savedInstanceState.getLong("fromIncomeDate"));
            toIncomeDate = new Date(savedInstanceState.getLong("toIncomeDate"));
            fromExpenditureDate = new Date(savedInstanceState.getLong("fromExpenditureDate"));
            toExpenditureDate = new Date(savedInstanceState.getLong("toExpenditureDate"));
        } else {
            // Income Time
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);

            toIncomeDate = new Date(cal.getTime().getTime());
            cal.add(Calendar.MONTH, -1);
            fromIncomeDate = new Date(cal.getTime().getTime());

            // Expenditure Time
            cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);

            toExpenditureDate = new Date(cal.getTime().getTime());
            cal.add(Calendar.MONTH, -1);
            fromExpenditureDate = new Date(cal.getTime().getTime());
        }

        updateFilterTexts();
    }

    public void onSave(Bundle outState) {
        outState.putLong("fromIncomeDate", fromIncomeDate.getTime());
        outState.putLong("toIncomeDate", toIncomeDate.getTime());
        outState.putLong("fromExpenditureDate", fromExpenditureDate.getTime());
        outState.putLong("toExpenditureDate", toExpenditureDate.getTime());
    }

    public void updateModel(TimeItem model) {
        db.put(model);
        refreshLists();
    }

    public void updateFromIncomeFilter(int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        Calendar cal = Calendar.getInstance();
        cal.set(year, monthOfYear, dayOfMonth, 0, 0);
        fromIncomeDate = new Date(cal.getTime().getTime());
        updateFilterTexts();
        refreshLists();
    }

    public void updateToIncomeFilter(int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        Calendar cal = Calendar.getInstance();
        cal.set(year, monthOfYear, dayOfMonth, 0, 0);
        toIncomeDate = new Date(cal.getTime().getTime());
        updateFilterTexts();
        refreshLists();
    }

    public void updateFromExpenditureFilter(int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        Calendar cal = Calendar.getInstance();
        cal.set(year, monthOfYear, dayOfMonth, 0, 0);
        fromExpenditureDate = new Date(cal.getTime().getTime());
        updateFilterTexts();
        refreshLists();
    }

    public void updateToExpenditureFilter(int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        Calendar cal = Calendar.getInstance();
        cal.set(year, monthOfYear, dayOfMonth, 0, 0);
        toExpenditureDate = new Date(cal.getTime().getTime());
        updateFilterTexts();
        refreshLists();
    }

    private void updateFilterTexts() {
        fragment.updateFilterText(toIncomeDate, fromIncomeDate, toExpenditureDate, fromExpenditureDate);
    }

    public void refreshLists() {
        String query = String.format("date >= %d AND date <= %d", fromIncomeDate.getTime(), toIncomeDate.getTime());
        final List<IncomeModel> incomeRows = db.get(IncomeModel.class, query);

        query = String.format("date >= %d AND date <= %d", fromExpenditureDate.getTime(), toExpenditureDate.getTime());
        final List<ExpenditureModel> expenditureRows = db.get(ExpenditureModel.class, query);

        fragment.refreshLists(incomeRows, expenditureRows);
    }

}
