package mahappdev.caresilabs.com.timr;

import java.util.Calendar;

import mahappdev.caresilabs.com.timr.models.ExpenditureCategory;
import mahappdev.caresilabs.com.timr.models.ExpenditureModel;

/**
 * Created by Simon on 9/12/2016.
 */
public final class BarcodeScannerItemFactory {
    private BarcodeScannerItemFactory() {
    }

    public static ExpenditureModel getExpenditureFromBarcode(String code) {
        ExpenditureModel model = null;
        switch (code) {
            case "7350041089568":
                model = new ExpenditureModel();
                model.title = "Midnight Coding";
                model.fromTime = 120;
                model.toTime = 250;
                model.category = ExpenditureCategory.PROGRAMMING.name();
                model.date = Calendar.getInstance().getTime();
                break;
            case "7322390772361":
                model = new ExpenditureModel();
                model.title = "Reading lecture notes";
                model.fromTime = 480;
                model.toTime = 580;
                model.category = ExpenditureCategory.READING.name();
                model.date = Calendar.getInstance().getTime();
                break;
            case "4902505211287":
                model = new ExpenditureModel();
                model.title = "Taking lecture notes";
                model.fromTime = 520;
                model.toTime = 600;
                model.category = ExpenditureCategory.SCHOOL.name();
                model.date = Calendar.getInstance().getTime();
                break;
            default:
                break;
        }
        return model;
    }
}
