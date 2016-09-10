package mahappdev.caresilabs.com.timr.views;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mahappdev.caresilabs.com.timr.R;
import mahappdev.caresilabs.com.timr.models.ExpenditureCategory;
import mahappdev.caresilabs.com.timr.models.IncomeCategory;
import mahappdev.caresilabs.com.timr.models.TimeItem;

public class EditItemActivity extends AppCompatActivity {

    @BindView(R.id.etItemTitle)
    EditText etItemTitle;

    @BindView(R.id.spnrCategory)
    Spinner spnrCategory;

    private MainActivity.FragmentType categoryType;
    private TimeItem                  model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.categoryType = MainActivity.FragmentType.values()[extras.getInt("type", 1)];
            this.model = new Gson().fromJson(extras.getString("model", null), TimeItem.class);
        } else {
            this.categoryType = MainActivity.FragmentType.DETAILS_INCOME;
        }

        initUI();

        if (this.model == null) {
            this.model = new TimeItem();
        } else {
            etItemTitle.setText(model.title);
            //spnrCategory.setId();
        }
    }

    private void initUI() {
        ButterKnife.bind(this);

        // Category
        String[] items = getNames(categoryType == MainActivity.FragmentType.DETAILS_INCOME ? IncomeCategory.class : ExpenditureCategory.class);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this,
                android.R.layout.simple_spinner_dropdown_item, items);
        spnrCategory.setAdapter(adapter);
    }

    @OnClick(R.id.btnItemDate)
    void onItemDateClick() {
        Calendar now = Calendar.getInstance();
        final DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        //btnToIncome.setText(date);

                        Calendar c = Calendar.getInstance();
                        c.set(year, monthOfYear, dayOfMonth);
                        model.date = c.getTime();
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
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
            }
        }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);
        tpd.show(getFragmentManager(), "ToTimepickerdialog");
    }

    @OnClick(R.id.btnSaveItem)
    void onSaveItemClick() {
        // TODO check everything

        // TODO set everything
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
}
