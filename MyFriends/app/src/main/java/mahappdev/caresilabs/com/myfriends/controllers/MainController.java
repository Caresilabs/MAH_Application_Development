package mahappdev.caresilabs.com.myfriends.controllers;

import android.support.design.widget.Snackbar;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import mahappdev.caresilabs.com.myfriends.models.Groups;
import mahappdev.caresilabs.com.myfriends.models.LogMessage;
import mahappdev.caresilabs.com.myfriends.models.Members;
import mahappdev.caresilabs.com.myfriends.models.NetMessage;
import mahappdev.caresilabs.com.myfriends.models.Register;
import mahappdev.caresilabs.com.myfriends.net.ClientService;
import mahappdev.caresilabs.com.myfriends.net.INetworkResponseCallback;
import mahappdev.caresilabs.com.myfriends.views.GroupsFragment;
import mahappdev.caresilabs.com.myfriends.views.MainActivity;
import mahappdev.caresilabs.com.myfriends.views.MapsFragment;

/**
 * Created by Simon on 10/3/2016.
 */

public class MainController implements INetworkResponseCallback {

    private final MainActivity   activity;
    private final GroupsFragment groupsFramgent;
    private final MapsFragment   mapsFragment;

    private ClientService client;

    private Register.Response user;

    private String myName = "Apple Pie";

    public MainController(MainActivity activity, GroupsFragment groupsFragment, MapsFragment mapsFragment) {
        this.activity = activity;
        this.groupsFramgent = groupsFragment;
        this.mapsFragment = mapsFragment;
    }

    private void joinRoom(String room) {
        Register.Request join = new Register.Request(room, myName);
        request(join);
    }

    private void refreshRooms() {
        Groups.Request msg = new Groups.Request();
        request(msg);
    }

    @Override
    public void onReceive(NetMessage netMessage) {
        if (netMessage instanceof LogMessage) {
            snackbar(((LogMessage) netMessage).message);
        } else if (netMessage instanceof Groups.Response) {
            groupsFramgent.refreshGroups(((Groups.Response) netMessage).groups);
        } else if (netMessage instanceof Members.Response) {
            List<Map<String, String>> a = ((Members.Response) netMessage).members;
        } else {
            //snackbar("Unknown");
        }
    }

    private void snackbar(String text) {
        //  Snackbar.make(activity.getCurrentFocus(), text, Snackbar.LENGTH_SHORT);
    }

    public void setClient(ClientService client) {
        this.client = client;

        refreshRooms();
    }

    private void request(NetMessage msg) {
        if (client != null) {
            client.send(msg);
        } else {
            snackbar("error test request");
        }
    }
}
