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
import mahappdev.caresilabs.com.timr.FragmentType;
import mahappdev.caresilabs.com.timr.R;
import mahappdev.caresilabs.com.timr.controllers.MainController;
import mahappdev.caresilabs.com.timr.controllers.SummaryController;
import mahappdev.caresilabs.com.timr.models.ExpenditureCategory;
import mahappdev.caresilabs.com.timr.models.ExpenditureModel;
import mahappdev.caresilabs.com.timr.models.IncomeCategory;
import mahappdev.caresilabs.com.timr.models.IncomeModel;
import mahappdev.caresilabs.com.timr.models.ProfileModel;
import mahappdev.caresilabs.com.timr.models.SummaryModel;
import mahappdev.caresilabs.com.timr.models.TimeItem;

public class SummaryFragment extends Fragment {

    private MainController    mainController;
    private SummaryController controller;

    @BindView(R.id.pieSummary)
    PieChart          pieChart;

    @BindView(R.id.tvWelcomeMessage)
    TextView tvWelcomeMessage;

    @BindView(R.id.tvSummaryMessage)
    TextView tvSummaryMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_summary, container, false);

        // UI
        ButterKnife.bind(this, view);

        // Setup the controller
        this.controller = new SummaryController(mainController.getDB());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateWelcomeMessage();
        updateSummary();
    }

    private void updateSummary() {
        SummaryModel summary = controller.getSummary();
        updateSummaryMessage(summary.totalIncome - summary.totalExpenditure);

        pieChart.setUsePercentValues(true);
        pieChart.setDescription("");

        // enable hole and configure
        pieChart.setDrawHoleEnabled(true);
        //piee.setHoleColorTransparent(true);
        pieChart.setHoleRadius(7);
        pieChart.setTransparentCircleRadius(10);

        // enable rotation of the chart by touch
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);

        PieDataSet dataSet = new PieDataSet(summary.entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // Color
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.MATERIAL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        // data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);
        pieChart.highlightValues(null);
        pieChart.invalidate();
    }

    private void updateSummaryMessage(int timeLeft) {
        if (timeLeft < 0) {
            tvSummaryMessage.setText("Hate to break it to you but you are\n" + -timeLeft + "min to short!");
        } else {
            tvSummaryMessage.setText("Very good! You have " + timeLeft + "min left!");
        }
    }

    @OnClick(R.id.btnDetailedIncome)
    public void allIncomeOnClick() {
        mainController.switchFragment(FragmentType.DETAILS_INCOME);
    }

    @OnClick(R.id.btnDetailedExpenditures)
    public void allExpendituresOnClick() {
        mainController.switchFragment(FragmentType.DETAILS_EXPENDITURE);
    }

    private void updateWelcomeMessage() {
        final ProfileModel model = mainController.getPrefs().get(ProfileModel.class, 0);
        if (model != null)
            tvWelcomeMessage.setText(String.format("Welcome to Timr %s %s", model.firstName, model.lastName));
    }

    public void setController(MainController controller) {
        this.mainController = controller;
    }
}
