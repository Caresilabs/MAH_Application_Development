package mahappdev.caresilabs.com.timr.views;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mahappdev.caresilabs.com.timr.R;
import mahappdev.caresilabs.com.timr.models.ProfileModel;
import mahappdev.caresilabs.com.timr.repositories.PreferenceRepository;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.etFirstName)
    EditText etFirstName;

    @BindView(R.id.etLastName)
    EditText etLastName;

    @BindView(R.id.etEmail)
    EditText etEmail;

    @BindView(R.id.etPassword)
    PasswordEditText etPassword;

    @BindString(R.string.required_fields)
    String requiredFieldsMessage;

    private PreferenceRepository prefs;
    private ProfileModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        this.prefs = new PreferenceRepository(getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE));
        this.model = prefs.get(ProfileModel.class, 0);

        if (model == null) {
            model = new ProfileModel();
        } else {
            etFirstName.setText(model.firstName);
            etLastName.setText(model.lastName);
            etEmail.setText(model.email);
            etPassword.setText(model.password);
        }
    }

    @OnClick(R.id.btnSaveProfile)
    void onSavedClicked() {
        // Validate input
        if (TextUtils.isEmpty(etFirstName.getText().toString()) ||
                TextUtils.isEmpty(etLastName.getText().toString()) ||
                TextUtils.isEmpty(etEmail.getText().toString())) {
            Snackbar.make(findViewById(R.id.profile_layout), requiredFieldsMessage, Snackbar.LENGTH_SHORT).show();
            return;
        }

        model.firstName = etFirstName.getText().toString();
        model.lastName = etLastName.getText().toString();
        model.email = etEmail.getText().toString();
        model.password = etPassword.getText().toString();

        // Save it
        prefs.put(model);

        finish();
    }

}
