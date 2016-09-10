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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mahappdev.caresilabs.com.timr.R;
import mahappdev.caresilabs.com.timr.controllers.MainController;
import mahappdev.caresilabs.com.timr.controllers.SummaryController;
import mahappdev.caresilabs.com.timr.models.ProfileModel;

public class SummaryFragment extends Fragment {

    private MainController    mainController;
    private SummaryController controller;

    @BindView(R.id.tvWelcomeMessage)
    TextView tvWelcomeMessage;

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

        this.controller = new SummaryController(mainController.getDB());

        PieChart pie = (PieChart)view.findViewById(R.id.pieSummary);

        pie.setUsePercentValues(true);
        pie.setDescription("Smartphones Market Share");

        // enable hole and configure
        pie.setDrawHoleEnabled(true);
        //pie.setHoleColorTransparent(true);
        pie.setHoleRadius(7);
        pie.setTransparentCircleRadius(10);

        // enable rotation of the chart by touch
        pie.setRotationAngle(0);
        pie.setRotationEnabled(true);

        //YourData[] dataObjects = ...;

        List<PieEntry> entries = new ArrayList<PieEntry>();

        for (int i = 0; i < 4; i++) {

            // turn your data into Entry objects
            entries.add(new PieEntry(0.25f, "Test"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Election Results");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);


        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
       // data.setValueTextColor(Color.WHITE);
        //data.setValueTypeface(mTfLight);


        pie.setData(data);

        pie.highlightValues(null);
        pie.invalidate(); // refresh

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        final ProfileModel model = mainController.getPrefs().get(ProfileModel.class, 0);
        if (model != null)
            tvWelcomeMessage.setText(String.format("Welcome to Timr %s %s", model.firstName, model.lastName));
    }

    public void setController(MainController controller) {
        this.mainController = controller;
    }
}
