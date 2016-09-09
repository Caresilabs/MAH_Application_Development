package mahappdev.caresilabs.com.timr.views;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import butterknife.BindView;
import butterknife.ButterKnife;
import mahappdev.caresilabs.com.timr.R;

public class DetailsFragment extends Fragment {

    @BindView(R.id.tabHost)
    TabHost tabs;

    private int startTab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        ButterKnife.bind(this, view);
        initTabs();
        initActionButton(view);

        return view;
    }

    private void initActionButton(View view) {
        // FLoatin action bar
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void initTabs() {
        tabs.setup();

        //Tab 1
        TabHost.TabSpec spec = tabs.newTabSpec("All Income");
        spec.setContent(R.id.detailedIncomeLayout);
        spec.setIndicator("Income");
        tabs.addTab(spec);

        //Tab 2
        spec = tabs.newTabSpec("All Expenditure");
        spec.setContent(R.id.detailedIncomeLayout);
        spec.setIndicator("Expenditure");
        tabs.addTab(spec);

        tabs.setCurrentTab(startTab);
        tabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (tabs.getCurrentTab() == 0 ) {
                    ((MainActivity) getActivity()).switchFragment(MainActivity.FragmentType.DETAILS_INCOME);
                } else {
                    ((MainActivity) getActivity()).switchFragment(MainActivity.FragmentType.DETAILS_EXPENDITURE);
                }
            }
        });
    }

    public void setStartTab(int id) {
        if (tabs == null) {
            startTab = id;
        } else {
            tabs.setCurrentTab(id);
        }
    }
}
