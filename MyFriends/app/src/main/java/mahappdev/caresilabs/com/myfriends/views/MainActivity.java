package mahappdev.caresilabs.com.myfriends.views;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import mahappdev.caresilabs.com.myfriends.LocaleHelper;
import mahappdev.caresilabs.com.myfriends.R;
import mahappdev.caresilabs.com.myfriends.controllers.MainController;
import mahappdev.caresilabs.com.myfriends.models.ProfileModel;
import mahappdev.caresilabs.com.myfriends.net.ClientService;
import mahappdev.caresilabs.com.myfriends.net.INetworkResponseCallback;
import mahappdev.caresilabs.com.myfriends.net.models.NetMessage;
import mahappdev.caresilabs.com.myfriends.repository.PreferenceRepository;

public class MainActivity extends AppCompatActivity implements INetworkResponseCallback {

    public static final String PREFS_NAME = "friends.pref";

    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager            viewPager;

    private MainController controller;

    private GroupsFragment groupsFragment;
    private MapsFragment   mapsFragment;
    private ChatFragment   chatFragment;

    private PreferenceRepository prefs;

    private MyServiceConnection serviceConn;
    private ClientService       connection;
    private boolean bound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.prefs = new PreferenceRepository(getSharedPreferences(PREFS_NAME, MODE_PRIVATE));

        // update language
        final ProfileModel profile = prefs.get(ProfileModel.class, 0);
        LocaleHelper.onCreate(this, profile.language);

        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setCurrentItem(1);
        viewPager.setOffscreenPageLimit(2);

        // Restore fragments
        if (getSupportFragmentManager().getFragments() != null) {
            for (Fragment frag : getSupportFragmentManager().getFragments()) {
                if (frag instanceof GroupsFragment) {
                    this.groupsFragment = (GroupsFragment) frag;
                } else if (frag instanceof MapsFragment) {
                    this.mapsFragment = (MapsFragment) frag;
                } else if (frag instanceof ChatFragment) {
                    this.chatFragment = (ChatFragment) frag;
                }
            }
        }

        // Fragments and Controllers
        if (mapsFragment == null)
            this.mapsFragment = MapsFragment.newInstance();
        if (groupsFragment == null)
            this.groupsFragment = GroupsFragment.newInstance();
        if (chatFragment == null)
            this.chatFragment = ChatFragment.newInstance();

        this.controller = new MainController(this, groupsFragment, mapsFragment,
                chatFragment, savedInstanceState != null ? savedInstanceState.getString("model", null) : null);

        initClient(savedInstanceState);
    }

    private void initClient(Bundle savedInstanceState) {
        Intent intent = new Intent(this, ClientService.class);
        intent.putExtra("IpAddress", getString(R.string.ip_address));
        intent.putExtra("TcpPort", getResources().getInteger(R.integer.tcp_port));

        if (savedInstanceState == null)
            startService(intent);

        serviceConn = new MyServiceConnection();
        boolean result = bindService(intent, serviceConn, 0);
        if (!result)
            Log.d("Controller-constructor", "No binding");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("model", controller.onSave());
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateAndCheckUserProfile();

        groupsFragment.setController(controller);
        mapsFragment.setController(controller);
        this.chatFragment.setController(controller);

        if (connection != null) {
            // Try connecting if socket is null
            connection.connect(true);
            controller.refreshGroups();
        }

        controller.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        controller.onPause();
    }

    private void updateAndCheckUserProfile() {
        // if we haven't filled in profile yet.
        final ProfileModel profile = prefs.get(ProfileModel.class, 0);
        if (profile == null) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        } else {
            controller.setName(profile.alias);
        }
    }

    @Override
    public void onReceive(final NetMessage netMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                controller.onReceive(netMessage);
            }
        });
    }

    private class MyServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName arg0, IBinder binder) {
            ClientService.LocalService ls = (ClientService.LocalService) binder;
            connection = ls.getService();
            bound = true;

            connection.setListener(MainActivity.this);
            connection.connect(true);

            controller.setClient(connection);
        }

        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            //controller.unjoinAll();
            controller.unjoinAll();
            connection.disconnectNow();
            connection.setListener(null);
        }
        if (bound) {
            bound = false;
            unbindService(serviceConn);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public ViewPager getViewPager() {
        return viewPager;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            switch (position) {
                case 0:
                    return groupsFragment;
                case 1:
                    return mapsFragment;
                case 2:
                    return chatFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.group);
                case 1:
                    return getString(R.string.map);
                case 2:
                    return getString(R.string.chat);
            }
            return null;
        }
    }
}
