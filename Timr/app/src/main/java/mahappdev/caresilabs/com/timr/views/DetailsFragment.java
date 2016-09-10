package mahappdev.caresilabs.com.timr.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;

import com.google.gson.Gson;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mahappdev.caresilabs.com.timr.DetailedListAdapter;
import mahappdev.caresilabs.com.timr.R;
import mahappdev.caresilabs.com.timr.controllers.DetailsController;
import mahappdev.caresilabs.com.timr.controllers.MainController;
import mahappdev.caresilabs.com.timr.models.ExpenditureModel;
import mahappdev.caresilabs.com.timr.models.IncomeModel;
import mahappdev.caresilabs.com.timr.models.TimeItem;

public class DetailsFragment extends Fragment {

    private static final int EDIT_ITEM_REQUEST_CODE = 0xff;

    @BindView(R.id.tabHost)
    TabHost tabs;

    @BindView(R.id.btnFromIncome)
    Button btnFromIncome;

    @BindView(R.id.btnToIncome)
    Button btnToIncome;

    @BindView(R.id.lwIncome)
    ListView lwIncome;

    private MainController    mainController;
    private DetailsController controller;
    private int               startTab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        ButterKnife.bind(this, view);
        initTabs();
        initActionButton(view);
        refreshLists();

        this.controller = new DetailsController(this, mainController.getDB());

        return view;
    }

    private DetailedListAdapter incomeAdapter;
    private DetailedListAdapter expenditureAdapter;

    private void refreshLists() {
        // Income List
        final List<IncomeModel> rows = mainController.getDB().get(IncomeModel.class, null);

        if (lwIncome.getAdapter() == null) {
            lwIncome.setAdapter(incomeAdapter = new DetailedListAdapter(getContext(), rows));
            lwIncome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    launchEditItem(MainActivity.FragmentType.DETAILS_INCOME, (IncomeModel)incomeAdapter.getItem(position));
                }
            });
        } else {
            incomeAdapter.clear();
            incomeAdapter.addAll(rows);
        }

        // Expenditure List
    }

    private void initActionButton(View view) {
        // Floatin action bar
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tabs.getCurrentTab() == 0) {
                    launchEditItem(MainActivity.FragmentType.DETAILS_INCOME, null);
                } else {
                    launchEditItem(MainActivity.FragmentType.DETAILS_EXPENDITURE, null);
                }
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
        spec.setContent(R.id.detailedExpenditureLayout);
        spec.setIndicator("Expenditure");
        tabs.addTab(spec);

        tabs.setCurrentTab(startTab);
        tabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (tabs.getCurrentTab() == 0) {
                    ((MainActivity) getActivity()).switchFragment(MainActivity.FragmentType.DETAILS_INCOME);
                } else {
                    ((MainActivity) getActivity()).switchFragment(MainActivity.FragmentType.DETAILS_EXPENDITURE);
                }
            }
        });
    }

    @OnClick(R.id.btnFromIncome)
    void onFromIncomeClicked() {
        Calendar now = Calendar.getInstance();
        final DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        btnFromIncome.setText(date);
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
    }

    @OnClick(R.id.btnToIncome)
    void onToIncomeClicked() {
        Calendar now = Calendar.getInstance();
        final DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        btnToIncome.setText(date);
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
    }

    private void launchEditItem(MainActivity.FragmentType type, TimeItem data) {
        Intent intent = new Intent(getActivity(), EditItemActivity.class);
        intent.putExtra("type", type.ordinal());
        intent.putExtra("model", new Gson().toJson(data));
        startActivityForResult(intent, EDIT_ITEM_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_ITEM_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (1 == 1) { // TODO
                    IncomeModel model = new Gson().fromJson(data.getStringExtra("model"), IncomeModel.class);
                    controller.updateModel(model);
                } else {
                    ExpenditureModel model = new Gson().fromJson(data.getStringExtra("model"), ExpenditureModel.class);
                    controller.updateModel(model);
                }
                refreshLists();
            }
        }
    }

    public void setStartTab(int id) {
        if (tabs == null) {
            startTab = id;
        } else if (tabs.getCurrentTab() != id) {
            tabs.setCurrentTab(id);
        }
    }

    public void setController(MainController controller) {
        this.mainController = controller;
    }
}
