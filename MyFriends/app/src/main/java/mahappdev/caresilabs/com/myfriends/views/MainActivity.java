package mahappdev.caresilabs.com.myfriends.views;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
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
import mahappdev.caresilabs.com.myfriends.net.models.NetMessage;
import mahappdev.caresilabs.com.myfriends.net.ClientService;
import mahappdev.caresilabs.com.myfriends.net.INetworkResponseCallback;

public class MainActivity extends AppCompatActivity implements INetworkResponseCallback {

    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager            viewPager;

    private MainController controller;

    private GroupsFragment groupsFragment;
    private MapsFragment   mapsFragment;
    private ChatFragment   chatFragment;


    private MyServiceConnection serviceConn;
    private boolean bound = false;
    private ClientService connection;
    private boolean       connected;

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


        /////////

        // Fragments and Controllers
        {
            this.mapsFragment = MapsFragment.newInstance();
            this.groupsFragment = GroupsFragment.newInstance();
            this.chatFragment = ChatFragment.newInstance();

            this.controller = new MainController(this, groupsFragment, mapsFragment, chatFragment);

           /* this.groupsFragment.setController(controller);
            this.mapsFragment.setController(controller);
            this.chatFragment.setController(controller);*/
        }

        Intent intent = new Intent(this, ClientService.class);
        intent.putExtra("IpAddress", getString(R.string.ip_address));
        intent.putExtra("TcpPort", getResources().getInteger(R.integer.tcp_port));

        if (savedInstanceState == null)
            startService(intent);
        else
            connected = savedInstanceState.getBoolean("CONNECTED", false);

        serviceConn = new MyServiceConnection();
        boolean result = bindService(intent, serviceConn, 0);
        if (!result)
            Log.d("Controller-constructor", "No binding");
    }

    @Override
    protected void onResume() {
        super.onResume();
        groupsFragment.setController(controller);
        mapsFragment.setController(controller);
        this.chatFragment.setController(controller);

        controller.refreshGroups();
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
            connection.disconnect();
            connection.setListener(null);

            bound = false;
            unbindService(serviceConn);
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


    public ViewPager getViewPager() {
        return viewPager;
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
