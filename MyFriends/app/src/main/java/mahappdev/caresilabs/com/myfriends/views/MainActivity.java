package mahappdev.caresilabs.com.myfriends.views;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import mahappdev.caresilabs.com.myfriends.R;
import mahappdev.caresilabs.com.myfriends.controllers.MainController;
import mahappdev.caresilabs.com.myfriends.models.Groups;
import mahappdev.caresilabs.com.myfriends.models.NetMessage;
import mahappdev.caresilabs.com.myfriends.net.ClientService;
import mahappdev.caresilabs.com.myfriends.net.INetworkResponseCallback;

public class MainActivity extends AppCompatActivity implements INetworkResponseCallback {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter sectionsPagerAdapter;

    private ViewPager viewPager;



    private MainController controller;

    private GroupsFragment groupsFragment;
    private MapsFragment    mapsFragment;


    private ServiceConn serviceConn;
    private boolean bound = false;
    private ClientService connection;
    private boolean connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setCurrentItem(1);

        mapsFragment = MapsFragment.newInstance();
        groupsFragment = GroupsFragment.newInstance();

        /////////

        this.controller = new MainController(this, groupsFragment, mapsFragment);

        Intent intent = new Intent(this, ClientService.class);
        intent.putExtra("IpAddress", getString(R.string.ip_address));
        intent.putExtra("TcpPort", getResources().getInteger(R.integer.tcp_port));

        if(savedInstanceState==null)
            startService(intent);
        else
            connected = savedInstanceState.getBoolean("CONNECTED", false);

        serviceConn = new ServiceConn();
        boolean result = bindService(intent, serviceConn, 0);
        if (!result)
            Log.d("Controller-constructor", "No binding");
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    private class ServiceConn implements ServiceConnection {
        public void onServiceConnected(ComponentName arg0, IBinder binder) {
            ClientService.LocalService ls = (ClientService.LocalService) binder;
            connection = ls.getService();
            bound = true;

            connection.setListener(MainActivity.this);
            connection.connect();

            controller.setClient(connection);
        }

        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bound) {
            unbindService(serviceConn);
            connection.disconnect();
            connection.setListener(null);
            bound = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
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
                    return PlaceholderFragment.newInstance(position + 1);
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
                    return "Groups";
                case 1:
                    return "Map";
                case 2:
                    return "Chat";
            }
            return null;
        }
    }
}
