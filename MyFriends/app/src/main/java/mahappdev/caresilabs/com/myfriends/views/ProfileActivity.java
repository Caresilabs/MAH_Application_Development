package mahappdev.caresilabs.com.myfriends.views;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mahappdev.caresilabs.com.myfriends.LocaleHelper;
import mahappdev.caresilabs.com.myfriends.R;
import mahappdev.caresilabs.com.myfriends.models.ProfileModel;
import mahappdev.caresilabs.com.myfriends.repository.PreferenceRepository;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.etAlias)
    EditText etAlias;

    @BindString(R.string.required_fields)
    String requiredFieldsMessage;

    @BindView(R.id.spnrLanguage)
    Spinner spnrLanguage;

    private PreferenceRepository                prefs;
    private ProfileModel                        model;
    private ArrayAdapter<ProfileModel.Language> languageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        this.prefs = new PreferenceRepository(getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE));
        this.model = prefs.get(ProfileModel.class, 0);

        languageAdapter = new ArrayAdapter<ProfileModel.Language>(this,
                android.R.layout.simple_spinner_dropdown_item, ProfileModel.Language.values());
        spnrLanguage.setAdapter(languageAdapter);

        spnrLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Snackbar.make(findViewById(R.id.profile_layout), "A restart is needed after changing the language.", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (model == null) {
            model = new ProfileModel();
        } else {
            etAlias.setText(model.alias);
            spnrLanguage.setSelection(model.language.ordinal());
        }
    }

    @OnClick(R.id.btnSaveProfile)
    void onSavedClicked() {
        // Validate input
        if (TextUtils.isEmpty(etAlias.getText().toString())) {
            Snackbar.make(findViewById(R.id.profile_layout), requiredFieldsMessage, Snackbar.LENGTH_LONG).show();
            return;
        }

        model.alias = etAlias.getText().toString();
        model.language = languageAdapter.getItem(spnrLanguage.getSelectedItemPosition());

        // Save it
        prefs.put(model);

        LocaleHelper.onCreate(this, model.language);
        finish();
    }

}
