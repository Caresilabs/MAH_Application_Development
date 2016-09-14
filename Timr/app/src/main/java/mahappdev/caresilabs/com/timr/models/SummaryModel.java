package mahappdev.caresilabs.com.timr.models;

import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Simon on 9/14/2016.
 */
public class SummaryModel {
    public int totalIncome;
    public int totalExpenditure;

    public final List<PieEntry> entries = new ArrayList<PieEntry>();

}
