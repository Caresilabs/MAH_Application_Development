package mahappdev.caresilabs.com.timr.views;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import mahappdev.caresilabs.com.timr.R;

public class SummaryFragment extends Fragment {

    public SummaryFragment() {
    }

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
        return view;
    }

}
