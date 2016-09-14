package mahappdev.caresilabs.com.timr.views;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mahappdev.caresilabs.com.timr.DetailedListAdapter;
import mahappdev.caresilabs.com.timr.FragmentType;
import mahappdev.caresilabs.com.timr.R;
import mahappdev.caresilabs.com.timr.controllers.DetailsController;
import mahappdev.caresilabs.com.timr.controllers.MainController;
import mahappdev.caresilabs.com.timr.models.DataModel;
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

    private DetailedListAdapter incomeAdapter;
    private DetailedListAdapter expenditureAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        // Restore data
        if (savedInstanceState != null) {
            startTab = savedInstanceState.getInt("currentTab");
        }

        this.controller = new DetailsController(this, mainController.getDB());

        // UI
        ButterKnife.bind(this, view);
        this.controller.onInit(savedInstanceState);

        // Init UI Components
        {
            initTabs();
            initLists();
            initActionButton(view);
        }

        return view;
    }

    private void initLists() {
        lwIncome.setAdapter(incomeAdapter = new DetailedListAdapter(getContext(), new ArrayList()));
        lwIncome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                launchEditItem(getActivity(), FragmentType.DETAILS_INCOME, (IncomeModel) incomeAdapter.getItem(position));
            }
        });

        lwExpenditure.setAdapter(expenditureAdapter = new DetailedListAdapter(getContext(), new ArrayList()));
        lwExpenditure.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                launchEditItem(getActivity(), FragmentType.DETAILS_EXPENDITURE, (ExpenditureModel) expenditureAdapter.getItem(position));
            }
        });

        DetailsLongClickListener listener = new DetailsLongClickListener();
        lwIncome.setOnItemLongClickListener(listener);
        lwExpenditure.setOnItemLongClickListener(listener);

        controller.refreshLists();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Save filter
        controller.onSave(outState);
        outState.putInt("currentTab", tabs.getCurrentTab());
        super.onSaveInstanceState(outState);
    }

    private void initActionButton(View view) {
        // Floating action bar
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tabs.getCurrentTab() == 0) {
                    launchEditItem(getActivity(), FragmentType.DETAILS_INCOME, null);
                } else {
                    launchEditItem(getActivity(), FragmentType.DETAILS_EXPENDITURE, null);
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
                if (getActivity() == null)
                    return;

                if (tabs.getCurrentTab() == 0) {
                    mainController.switchFragment(FragmentType.DETAILS_INCOME);
                } else {
                    mainController.switchFragment(FragmentType.DETAILS_EXPENDITURE);
                }
            }
        });
    }

    public void updateFilterText(Date toIncomeDate, Date fromIncomeDate, Date toExpenditureDate, Date fromExpenditureDate) {
        btnToIncome.setText(new SimpleDateFormat("dd/MM/yyyy").format(toIncomeDate));
        btnFromIncome.setText(new SimpleDateFormat("dd/MM/yyyy").format(fromIncomeDate));

        btnToExpenditure.setText(new SimpleDateFormat("dd/MM/yyyy").format(toExpenditureDate));
        btnFromExpenditure.setText(new SimpleDateFormat("dd/MM/yyyy").format(fromExpenditureDate));
    }

    public void refreshLists(List<IncomeModel> incomeRows, List<ExpenditureModel> expenditureRows) {
        if (getActivity() == null)
            return;

        // Income List
        incomeAdapter.clear();
        incomeAdapter.addAll(incomeRows);

        // Expenditure List
        expenditureAdapter.clear();
        expenditureAdapter.addAll(expenditureRows);
    }

    @OnClick(R.id.btnFromIncome)
    void onFromIncomeClicked() {
        final Calendar now = Calendar.getInstance();
        final DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        controller.updateFromIncomeFilter(year, monthOfYear, dayOfMonth);
                    }
                },
                now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
    }

    @OnClick(R.id.btnToIncome)
    void onToIncomeClicked() {
        final Calendar now = Calendar.getInstance();
        final DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        controller.updateToIncomeFilter(year, monthOfYear, dayOfMonth);
                    }
                },
                now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)
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
                        controller.updateFromExpenditureFilter(year, monthOfYear, dayOfMonth);
                    }
                },
                now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)
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
                        controller.updateToExpenditureFilter(year, monthOfYear, dayOfMonth);
                    }
                },
                now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)
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

    public void launchEditItem(Activity activity, FragmentType type, TimeItem data) {
        Intent intent = new Intent(activity, EditItemActivity.class);
        intent.putExtra("type", type.ordinal());
        intent.putExtra("model", new Gson().toJson(data));
        activity.startActivityForResult(intent, EDIT_ITEM_REQUEST_CODE);
    }

    public void onEditItemComplete(Intent data) {
        TimeItem model = null;
        if (tabs.getCurrentTab() == 0) {
            model = new Gson().fromJson(data.getStringExtra("model"), IncomeModel.class);
        } else {
            model = new Gson().fromJson(data.getStringExtra("model"), ExpenditureModel.class);
        }
        controller.updateModel(model);
    }

    public void setController(MainController controller) {
        this.mainController = controller;
    }

    private class DetailsLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Delete");
            builder.setMessage("Are you sure you want to delete?");
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int ii) {
                    mainController.getDB().remove((DataModel) parent.getItemAtPosition(position));
                    controller.refreshLists();
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int ii) {
                            dialog.dismiss();
                        }
                    }
            );
            builder.show();
            return true;
        }
    }
}
