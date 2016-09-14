package mahappdev.caresilabs.com.timr.views;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mahappdev.caresilabs.com.timr.FragmentType;
import mahappdev.caresilabs.com.timr.R;
import mahappdev.caresilabs.com.timr.models.ExpenditureCategory;
import mahappdev.caresilabs.com.timr.models.ExpenditureModel;
import mahappdev.caresilabs.com.timr.models.IncomeCategory;
import mahappdev.caresilabs.com.timr.models.TimeItem;

public class EditItemActivity extends AppCompatActivity {

    @BindView(R.id.etItemTitle)
    EditText etItemTitle;

    @BindView(R.id.spnrCategory)
    Spinner spnrCategory;

    @BindView(R.id.btnItemDate)
    Button btnItemDate;

    @BindView(R.id.btnItemFrom)
    Button btnItemFrom;

    @BindView(R.id.btnItemTo)
    Button btnItemTo;

    @BindString(R.string.required_fields)
    String requiredFieldsMessage;

    private FragmentType categoryType;
    private TimeItem     model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.categoryType = FragmentType.values()[extras.getInt("type", 1)];
            this.model = new Gson().fromJson(extras.getString("model", null), TimeItem.class);
        } else {
            this.categoryType = FragmentType.DETAILS_INCOME;
        }

        initUI();
    }

    private void initUI() {
        ButterKnife.bind(this);

        // Category
        String[] items = getNames(categoryType == FragmentType.DETAILS_INCOME ? IncomeCategory.class : ExpenditureCategory.class);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spnrCategory.setAdapter(adapter);

        updateFields();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        TimeItem item = new Gson().fromJson(savedInstanceState.getString("model"), TimeItem.class);
        if (item != null)
            this.model = item;

        updateFields();
    }

    private void updateFields() {
        if (this.model == null) {
            this.model = new TimeItem();
            this.model.date = Calendar.getInstance().getTime();
        } else {
            if (model.title != null)
                etItemTitle.setText(model.title);

            if (categoryType == FragmentType.DETAILS_INCOME) {
                spnrCategory.setSelection(IncomeCategory.valueOf(model.category).ordinal());
            } else {
                spnrCategory.setSelection(ExpenditureCategory.valueOf(model.category).ordinal());
            }
        }
        updateButtonTexts();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        model.title = etItemTitle.getText().toString();
        model.category = (String) spnrCategory.getSelectedItem();

        String data = new Gson().toJson(model);
        outState.putString("model", data);

        super.onSaveInstanceState(outState);
    }

    private void updateButtonTexts() {
        if (model.date != null)
            btnItemDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(model.date));
        if (model.fromTime != 0)
            btnItemFrom.setText(formatTime(model.fromTime));
        if (model.toTime != 0)
            btnItemTo.setText(formatTime(model.toTime));
    }

    @OnClick(R.id.btnItemDate)
    void onItemDateClick() {
        Calendar now = Calendar.getInstance();
        final DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar c = Calendar.getInstance();
                        c.set(year, monthOfYear, dayOfMonth);
                        model.date = c.getTime();
                        updateButtonTexts();
                    }
                },
                now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    @OnClick(R.id.btnItemFrom)
    void onItemFromClick() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                model.fromTime = (hourOfDay * 60) + minute;
                updateButtonTexts();
            }
        }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);
        tpd.show(getFragmentManager(), "FromTimepickerdialog");
    }

    @OnClick(R.id.btnItemTo)
    void onItemToClick() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                model.toTime = (hourOfDay * 60) + minute;
                updateButtonTexts();
            }
        }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);
        tpd.show(getFragmentManager(), "ToTimepickerdialog");
    }

    @OnClick(R.id.btnSaveItem)
    void onSaveItemClick() {
        // Validate input
        if (TextUtils.isEmpty(etItemTitle.getText().toString()) ||
                model.fromTime == 0 ||
                model.toTime == 0 ||
                model.date == null) {
            Snackbar.make(btnItemDate, requiredFieldsMessage, Snackbar.LENGTH_SHORT).show();
            return;
        }

        model.title = etItemTitle.getText().toString();
        model.category = (String) spnrCategory.getSelectedItem();

        // Send back model
        Intent data = new Intent();
        data.putExtra("model", new Gson().toJson(model));
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    public static String[] getNames(Class<? extends Enum<?>> e) {
        return Arrays.toString(e.getEnumConstants()).replaceAll("^.|.$", "").split(", ");
    }

    public static String formatTime(int time) {
        int hour = (int) (time / 60);
        int min = (time % 60);

        return (hour < 9 ? "0" : "") + hour + ":" + (min < 10 ? "0" : "") + min;
    }
}
