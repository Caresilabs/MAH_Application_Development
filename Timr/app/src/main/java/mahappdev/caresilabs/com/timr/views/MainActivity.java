package mahappdev.caresilabs.com.timr.views;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import mahappdev.caresilabs.com.timr.BarcodeScannerItemFactory;
import mahappdev.caresilabs.com.timr.FragmentType;
import mahappdev.caresilabs.com.timr.R;
import mahappdev.caresilabs.com.timr.controllers.MainController;
import mahappdev.caresilabs.com.timr.models.ExpenditureModel;
import mahappdev.caresilabs.com.timr.repositories.PreferenceRepository;
import mahappdev.caresilabs.com.timr.repositories.SQLRepository;
import mahappdev.caresilabs.com.timr.repositories.TimrSQLRepository;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final  String PREFS_NAME                   = ".timr";
    private static final int    BARCODE_SCANNER_REQUEST_CODE = 0xff1;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    private TextView tvMenuUserFullName;
    private TextView tvMenuUserEmail;

    private SummaryFragment summaryFragment;
    private DetailsFragment detailsFragment;

    private MainController controller;

    private PreferenceRepository prefs;
    private SQLRepository        db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.initRepositories();
        this.controller = new MainController(this, db, prefs);

        this.initUIComponents();
        this.initFragments(savedInstanceState);
    }

    private void initRepositories() {
        this.prefs = new PreferenceRepository(getSharedPreferences(PREFS_NAME, MODE_PRIVATE));
        this.db = new TimrSQLRepository(this);
    }

    private void initUIComponents() {
        // Bind butterknife
        ButterKnife.bind(this);

        // Header text
        tvMenuUserFullName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvMenuUserFullName);
        tvMenuUserEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvMenuUserEmail);

        // Tool bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Navigation view
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_summary);
    }

    private void initFragments(Bundle savedInstanceState) {
        summaryFragment = (SummaryFragment) getSupportFragmentManager().findFragmentByTag("summaryFragment");
        detailsFragment = (DetailsFragment) getSupportFragmentManager().findFragmentByTag("detailsFragment");

        // Create a new Fragment to be placed in the activity layout
        if (summaryFragment == null)
            summaryFragment = new SummaryFragment();
        if (detailsFragment == null)
            detailsFragment = new DetailsFragment();

        summaryFragment.setController(controller);
        detailsFragment.setController(controller);

        if (savedInstanceState != null) {
            return;
        }

        summaryFragment.setArguments(getIntent().getExtras());

        // Add the fragment to the 'fragment_main' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_main, summaryFragment, "summaryFragment")
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        controller.updateAndCheckUserProfile();
    }

    public void switchFragment(FragmentType type) {
        Fragment frag = null;
        String tag = null;

        switch (type) {
            case SUMMARY:
                frag = summaryFragment;
                navigationView.setCheckedItem(R.id.nav_summary);
                tag = "summaryFragment";
                break;
            case DETAILS_INCOME:
                frag = detailsFragment;
                navigationView.setCheckedItem(R.id.nav_income);
                tag = "detailsFragment";
                detailsFragment.setStartTab(0);
                break;
            case DETAILS_EXPENDITURE:
                frag = detailsFragment;
                navigationView.setCheckedItem(R.id.nav_expenditure);
                tag = "detailsFragment";
                detailsFragment.setStartTab(1);
                break;
            default:
                break;
        }

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.fragment_main, frag, tag)
                .commit();

    }

    public void updateDrawerUserInfo(String name, String email) {
        tvMenuUserFullName.setText(name);
        tvMenuUserEmail.setText(email);
    }

    public void launchEditProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    private void launchBarcodeScanner() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, BARCODE_SCANNER_REQUEST_CODE);
        } else {
            Intent intent = new Intent(this, BarcodeScannerActivity.class);
            startActivityForResult(intent, BARCODE_SCANNER_REQUEST_CODE);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_barcode) {
            launchBarcodeScanner();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case BARCODE_SCANNER_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchBarcodeScanner();
                } else {
                    Toast.makeText(this, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
                }
                return;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BARCODE_SCANNER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String code = data.getStringExtra("barcodeData");

                final ExpenditureModel model = BarcodeScannerItemFactory.getExpenditureFromBarcode(code);
                if (model != null) {
                    switchFragment(FragmentType.DETAILS_EXPENDITURE);
                    detailsFragment.launchEditItem(this, FragmentType.DETAILS_EXPENDITURE, model);
                }
            }
        } else if (requestCode == DetailsFragment.EDIT_ITEM_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                detailsFragment.onEditItemComplete(data);
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_income) {
            switchFragment(FragmentType.DETAILS_INCOME);
        } else if (id == R.id.nav_expenditure) {
            switchFragment(FragmentType.DETAILS_EXPENDITURE);
        } else if (id == R.id.nav_summary) {
            switchFragment(FragmentType.SUMMARY);
        } else if (id == R.id.nav_profile) {
            launchEditProfile();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

}
