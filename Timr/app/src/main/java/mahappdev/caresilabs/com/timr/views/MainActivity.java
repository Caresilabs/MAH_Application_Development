package mahappdev.caresilabs.com.timr.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import mahappdev.caresilabs.com.timr.R;
import mahappdev.caresilabs.com.timr.controllers.MainController;
import mahappdev.caresilabs.com.timr.models.ProfileModel;
import mahappdev.caresilabs.com.timr.repositories.PreferenceRepository;
import mahappdev.caresilabs.com.timr.repositories.SQLRepository;
import mahappdev.caresilabs.com.timr.repositories.TimrSQLRepository;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public enum FragmentType {
        SUMMARY, DETAILS_INCOME, DETAILS_EXPENDITURE
    }

    public static final String PREFS_NAME = ".timr";

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

        initRepositories();
        this.controller = new MainController(db, prefs);

        initUIComponents();
        initFragments(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // if we haven't filled in profile yet.
        ProfileModel profile = prefs.get(ProfileModel.class, 0);
        if (profile == null) {
            launchEditProfile();
        } else {
            tvMenuUserFullName.setText(String.format("%s %s", profile.firstName, profile.lastName));
            tvMenuUserEmail.setText(profile.email);
        }
    }

    private void initRepositories() {
        this.prefs = new PreferenceRepository(getSharedPreferences(PREFS_NAME, MODE_PRIVATE));
        this.db = new TimrSQLRepository(this);
    }

    private void initUIComponents() {
        // Bind butterknife
        ButterKnife.bind(this);

        // Header text
        tvMenuUserFullName = (TextView)navigationView.getHeaderView(0).findViewById(R.id.tvMenuUserFullName);
        tvMenuUserEmail = (TextView)navigationView.getHeaderView(0).findViewById(R.id.tvMenuUserEmail);

        // Tool bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer
        // DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Navigation view
        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initFragments(Bundle savedInstanceState) {
        // Create a new Fragment to be placed in the activity layout
        if (summaryFragment == null)
            summaryFragment = new SummaryFragment();
        if (detailsFragment == null)
            detailsFragment = new DetailsFragment();

        summaryFragment.setController(controller);
        detailsFragment.setController(controller);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_main) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            summaryFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_main, summaryFragment)
                    .commit();
        }
    }

    public void switchFragment(FragmentType type) {
        Fragment frag = null;

        switch (type){
            case SUMMARY:
                frag = summaryFragment;
                navigationView.setCheckedItem(R.id.nav_summary);
                break;
            case DETAILS_INCOME:
                frag = detailsFragment;
                detailsFragment.setStartTab(0);
                navigationView.setCheckedItem(R.id.nav_income);
                break;
            case DETAILS_EXPENDITURE:
                frag = detailsFragment;
                detailsFragment.setStartTab(1);
                navigationView.setCheckedItem(R.id.nav_expenditure);
                break;
            default:
                break;
        }

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.fragment_main, frag)
                .commit();
    }

    private void launchEditProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_barcode) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
}
