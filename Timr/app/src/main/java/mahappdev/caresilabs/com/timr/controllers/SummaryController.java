package mahappdev.caresilabs.com.timr.controllers;

import com.github.mikephil.charting.data.PieEntry;

import java.util.Calendar;
import java.util.List;

import mahappdev.caresilabs.com.timr.models.ExpenditureCategory;
import mahappdev.caresilabs.com.timr.models.ExpenditureModel;
import mahappdev.caresilabs.com.timr.models.IncomeCategory;
import mahappdev.caresilabs.com.timr.models.IncomeModel;
import mahappdev.caresilabs.com.timr.models.SummaryModel;
import mahappdev.caresilabs.com.timr.models.TimeItem;
import mahappdev.caresilabs.com.timr.repositories.SQLRepository;

/**
 * Created by Simon on 9/8/2016.
 */
public class SummaryController {
    private final SQLRepository db;

    public SummaryController(SQLRepository db) {
        this.db = db;
    }

    public SummaryModel getSummary() {
        // Query db
        Calendar cal = Calendar.getInstance();
        long to = cal.getTime().getTime();
        cal.add(Calendar.MONTH, -1);
        long from = cal.getTime().getTime();
        String query = String.format("date >= %d AND date <= %d", from, to);

        final List<ExpenditureModel> expenditureRows = db.get(ExpenditureModel.class, query);
        int totalExpenditure = 0;
        int[] expenditureList = new int[ExpenditureCategory.values().length];

        for (int i = 0; i < expenditureRows.size(); i++) {
            TimeItem time = expenditureRows.get(i);
            int catId = ExpenditureCategory.valueOf(time.category).ordinal();

            int min = Math.abs(time.toTime - time.fromTime);
            expenditureList[catId] = expenditureList[(catId)] + min;

            totalExpenditure += min;
        }

        final List<IncomeModel> incomeRows = db.get(IncomeModel.class, query);
        int totalincome = 0;
        int[] incomeList = new int[IncomeCategory.values().length];

        for (int i = 0; i < incomeRows.size(); i++) {
            TimeItem time = incomeRows.get(i);
            int catId = IncomeCategory.valueOf(time.category).ordinal();

            int min = Math.abs(time.toTime - time.fromTime);
            incomeList[catId] = incomeList[(catId)] + min;

            totalincome += min;
        }

        SummaryModel model = new SummaryModel();
        model.totalIncome = totalincome;
        model.totalExpenditure = totalExpenditure;

        int timeLeft = Math.max(0, totalincome - totalExpenditure);

        for (int i = 0; i < expenditureList.length; i++) {
            if (expenditureList[i] <= 0)
                continue;

            // turn your data into Entry objects
            model.entries.add(new PieEntry(expenditureList[(i)],
                    ExpenditureCategory.values()[i].toString() + " - " + expenditureList[i] + "min"));
        }

        if (timeLeft > 0)
            model.entries.add(new PieEntry(timeLeft, "Time Left - " + timeLeft + "min"));

        return model;
    }
}
