package mahappdev.caresilabs.com.timr.views;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mahappdev.caresilabs.com.timr.R;
import mahappdev.caresilabs.com.timr.controllers.MainController;
import mahappdev.caresilabs.com.timr.controllers.SummaryController;
import mahappdev.caresilabs.com.timr.models.ExpenditureCategory;
import mahappdev.caresilabs.com.timr.models.ExpenditureModel;
import mahappdev.caresilabs.com.timr.models.IncomeCategory;
import mahappdev.caresilabs.com.timr.models.IncomeModel;
import mahappdev.caresilabs.com.timr.models.ProfileModel;
import mahappdev.caresilabs.com.timr.models.TimeItem;

public class SummaryFragment extends Fragment {

    private MainController    mainController;
    private SummaryController controller;

    @BindView(R.id.tvWelcomeMessage)
    TextView tvWelcomeMessage;

    private PieChart pieChart;

    @OnClick(R.id.btnDetailedIncome)
    public void allIncomeOnClick() {
        ((MainActivity) getActivity()).switchFragment(MainActivity.FragmentType.DETAILS_INCOME);
    }

    @OnClick(R.id.btnDetailedExpenditures)
    public void allExpendituresOnClick() {
        ((MainActivity) getActivity()).switchFragment(MainActivity.FragmentType.DETAILS_EXPENDITURE);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_summary, container, false);
        ButterKnife.bind(this, view);
        pieChart = (PieChart) view.findViewById(R.id.pieSummary);

        this.controller = new SummaryController(mainController.getDB());

        return view;
    }


    private void updatePieChart() {
        // Query db
        Calendar cal = Calendar.getInstance();
        long to = cal.getTime().getTime();
        cal.add(Calendar.MONTH, -1);
        long from = cal.getTime().getTime();
        String query = String.format("date >= %d AND date <= %d", from, to);
        final List<ExpenditureModel> rows = mainController.getDB().get(ExpenditureModel.class, query);

        int[] expenditureList = new int[ExpenditureCategory.values().length];
        for (int i = 0; i < rows.size(); i++) {
            TimeItem time = rows.get(i);
            int catId = ExpenditureCategory.valueOf(time.category).ordinal();

            expenditureList[catId] = expenditureList[(catId)] + Math.abs(time.toTime - time.fromTime);
        }

        pieChart.setUsePercentValues(true);
        pieChart.setDescription("Time Share");

        // enable hole and configure
        pieChart.setDrawHoleEnabled(true);
        //piee.setHoleColorTransparent(true);
        pieChart.setHoleRadius(7);
        pieChart.setTransparentCircleRadius(10);

        // enable rotation of the chart by touch
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);

        //YourData[] dataObjects = ...;

        List<PieEntry> entries = new ArrayList<PieEntry>();

        for (int i = 0; i < expenditureList.length; i++) {
            if (expenditureList[i] == 0)
                continue;

            // turn your data into Entry objects
            entries.add(new PieEntry(expenditureList[(i)], ExpenditureCategory.values()[i].toString() + " - " + expenditureList[i] + "min"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.MATERIAL_COLORS)
            colors.add(c);
        dataSet.setColors(colors);


        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        // data.setValueTextColor(Color.WHITE);
        //data.setValueTypeface(mTfLight);


        pieChart.setData(data);

        pieChart.highlightValues(null);
        pieChart.invalidate(); // refresh
    }

    @Override
    public void onResume() {
        super.onResume();
        final ProfileModel model = mainController.getPrefs().get(ProfileModel.class, 0);
        if (model != null)
            tvWelcomeMessage.setText(String.format("Welcome to Timr %s %s", model.firstName, model.lastName));

        updatePieChart();
    }

    public void setController(MainController controller) {
        this.mainController = controller;
    }
}
