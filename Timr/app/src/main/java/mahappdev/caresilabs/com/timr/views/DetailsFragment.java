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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    public static final int EDIT_ITEM_REQUEST_CODE = 0xff;

    @BindView(R.id.tabHost)
    TabHost tabs;

    @BindView(R.id.btnFromIncome)
    Button btnFromIncome;

    @BindView(R.id.btnToIncome)
    Button btnToIncome;

    @BindView(R.id.lwIncome)
    ListView lwIncome;

    @BindView(R.id.btnFromExpenditure)
    Button btnFromExpenditure;

    @BindView(R.id.btnToExpenditure)
    Button btnToExpenditure;

    @BindView(R.id.lwExpenditure)
    ListView lwExpenditure;

    private MainController    mainController;
    private DetailsController controller;
    private int               startTab;

    private Date fromIncomeDate;
    private Date toIncomeDate;

    private Date fromExpenditureDate;
    private Date toExpenditureDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        if (savedInstanceState != null) {
            fromIncomeDate = new Date(savedInstanceState.getLong("fromIncomeDate"));
            toIncomeDate =  new Date(savedInstanceState.getLong("toIncomeDate"));
            fromExpenditureDate = new Date(savedInstanceState.getLong("fromExpenditureDate"));
            toExpenditureDate = new Date( savedInstanceState.getLong("toExpenditureDate"));
            startTab = savedInstanceState.getInt("currentTab");
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

        // UI
        ButterKnife.bind(this, view);
        initTabs();
        initActionButton(view);
        refreshLists();

        btnToIncome.setText(new SimpleDateFormat("dd/MM/yyyy").format(toIncomeDate));
        btnFromIncome.setText(new SimpleDateFormat("dd/MM/yyyy").format(fromIncomeDate));

        btnToExpenditure.setText(new SimpleDateFormat("dd/MM/yyyy").format(toExpenditureDate));
        btnFromExpenditure.setText(new SimpleDateFormat("dd/MM/yyyy").format(fromExpenditureDate));

        this.controller = new DetailsController(this, mainController.getDB());

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Save filter
        outState.putLong("fromIncomeDate", fromIncomeDate.getTime());
        outState.putLong("toIncomeDate", toIncomeDate.getTime());
        outState.putLong("fromExpenditureDate", fromExpenditureDate.getTime());
        outState.putLong("toExpenditureDate", toExpenditureDate.getTime());

        outState.putInt("currentTab", tabs.getCurrentTab());
        super.onSaveInstanceState(outState);
    }

    private DetailedListAdapter incomeAdapter;
    private DetailedListAdapter expenditureAdapter;

    private void refreshLists() {
        if (getActivity() == null)
            return;

        // Income List
        String query = String.format("date >= %d AND date <= %d", fromIncomeDate.getTime(), toIncomeDate.getTime());
        final List<IncomeModel> incomeRows = mainController.getDB().get(IncomeModel.class, query);

        if (lwIncome.getAdapter() == null) {
            lwIncome.setAdapter(incomeAdapter = new DetailedListAdapter(getContext(), incomeRows));
            lwIncome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    launchEditItem(getActivity() ,MainActivity.FragmentType.DETAILS_INCOME, (IncomeModel) incomeAdapter.getItem(position));
                }
            });
        } else {
            incomeAdapter.clear();
            incomeAdapter.addAll(incomeRows);
        }

        // Expenditure List
        query = String.format("date >= %d AND date <= %d", fromExpenditureDate.getTime(), toExpenditureDate.getTime());
        final List<ExpenditureModel> expenditureRows  = mainController.getDB().get(ExpenditureModel.class, query);

        if (lwExpenditure.getAdapter() == null) {
            lwExpenditure.setAdapter(expenditureAdapter = new DetailedListAdapter(getContext(), expenditureRows));
            lwExpenditure.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    launchEditItem(getActivity(), MainActivity.FragmentType.DETAILS_EXPENDITURE, (ExpenditureModel) expenditureAdapter.getItem(position));
                }
            });
        } else {
            expenditureAdapter.clear();
            expenditureAdapter.addAll(expenditureRows);
        }
    }

    private void initActionButton(View view) {
        // Floatin action bar
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tabs.getCurrentTab() == 0) {
                    launchEditItem(getActivity(), MainActivity.FragmentType.DETAILS_INCOME, null);
                } else {
                    launchEditItem(getActivity() ,MainActivity.FragmentType.DETAILS_EXPENDITURE, null);
                }
            }
        });
    }

    private void initTabs() {
        tabs.setup();

        //Tab 1
        TabHost.TabSpec spec = tabs.newTabSpec("My Leisure");
        spec.setContent(R.id.detailedIncomeLayout);
        spec.setIndicator("Leisure");
        tabs.addTab(spec);

        //Tab 2
        spec = tabs.newTabSpec("My Expenditure");
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
                        Calendar cal = Calendar.getInstance();
                        cal.set(year, monthOfYear, dayOfMonth, 0, 0 );
                        fromIncomeDate = new Date(cal.getTime().getTime());

                        btnFromIncome.setText(date);

                        refreshLists();
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

                        Calendar cal = Calendar.getInstance();
                        cal.set(year, monthOfYear, dayOfMonth, 0, 0);
                        toIncomeDate = new Date(cal.getTime().getTime());

                        btnToIncome.setText(date);

                        refreshLists();
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
    }

    @OnClick(R.id.btnFromExpenditure)
    void onFromExpenditureClicked() {
        Calendar now = Calendar.getInstance();
        final DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        Calendar cal = Calendar.getInstance();
                        cal.set(year, monthOfYear, dayOfMonth, 0, 0);
                        fromExpenditureDate = new Date(cal.getTime().getTime());

                        btnFromExpenditure.setText(date);

                        refreshLists();
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
    }

    @OnClick(R.id.btnToExpenditure)
    void onToExpenditureClicked() {
        Calendar now = Calendar.getInstance();
        final DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;

                        Calendar cal = Calendar.getInstance();
                        cal.set(year, monthOfYear, dayOfMonth, 0, 0);
                        toExpenditureDate = new Date(cal.getTime().getTime());

                        btnToExpenditure.setText(date);

                        refreshLists();
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
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

    public void launchEditItem(Activity activity, MainActivity.FragmentType type, TimeItem data) {
        Intent intent = new Intent(activity, EditItemActivity.class);
        intent.putExtra("type", type.ordinal());
        intent.putExtra("model", new Gson().toJson(data));
        activity.startActivityForResult(intent, EDIT_ITEM_REQUEST_CODE);
    }

    public void onEditItemComplete(Intent data) {
        if (tabs.getCurrentTab() == 0) {
            IncomeModel model = new Gson().fromJson(data.getStringExtra("model"), IncomeModel.class);
            controller.updateModel(model);
        } else {
            ExpenditureModel model = new Gson().fromJson(data.getStringExtra("model"), ExpenditureModel.class);
            controller.updateModel(model);
        }
        refreshLists();
    }
}
