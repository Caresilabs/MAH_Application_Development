package mahappdev.caresilabs.com.myfriends.controllers;

import android.support.design.widget.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mahappdev.caresilabs.com.myfriends.models.DataModel;
import mahappdev.caresilabs.com.myfriends.net.models.Groups;
import mahappdev.caresilabs.com.myfriends.net.models.Locations;
import mahappdev.caresilabs.com.myfriends.net.models.LogMessage;
import mahappdev.caresilabs.com.myfriends.net.models.Members;
import mahappdev.caresilabs.com.myfriends.net.models.NetMessage;
import mahappdev.caresilabs.com.myfriends.net.models.Register;
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

   private DataModel model;

    public MainController(MainActivity activity, GroupsFragment groupsFragment, MapsFragment mapsFragment) {
        this.activity = activity;
        this.groupsFramgent = groupsFragment;
        this.mapsFragment = mapsFragment;

       this.model = new DataModel();
    }

    public void joinRoom(String room) {
        Register.Request join = new Register.Request(room, myName);
        request(join);
    }

    public void refreshGroups() {
        Groups.Request msg = new Groups.Request();
        request(msg);
    }

    public void updateUserLocation(double lgt,  double lat) {
        for (String id : model.userIds) {
            Locations.Request location = new Locations.Request(id, lgt, lat);
            request(location);
        }
    }

    private void refreshMembers(String room) {
        Members.Request members = new Members.Request(room);
        request(members);
    }

    @Override
    public void onReceive(NetMessage netMessage) {
        if (netMessage instanceof LogMessage) {
            snackbar(((LogMessage) netMessage).message);
        } else if (netMessage instanceof Groups.Response) {
            model.groups.clear();

            for (Map<String, String> group : ((Groups.Response) netMessage).groups) {
                String name = group.get("group");
                model.groups.put(name, new DataModel.GroupModel(name));

                refreshMembers(name);
            }

        } else if (netMessage instanceof Members.Response) {
            List<Map<String, String>> members = ((Members.Response) netMessage).members;

            for (Map<String, String> member : members) {
                model.groups.get(((Members.Response) netMessage).group)
                        .members.add(new DataModel.MemberModel(member.get("member")));
            }

            groupsFramgent.refreshGroups(model.groups.values()); //((Groups.Response) netMessage).groups

        } else if (netMessage instanceof Register.Response) {
           // userGroups.add((Register.Response) netMessage);
            model.userIds.add(((Register.Response) netMessage).id);
            snackbar("Joined Room as " + ((Register.Response) netMessage).id);

            refreshGroups();


            // test
            updateUserLocation(55.477783, 12.996270);
        } else if (netMessage instanceof Locations.Response) {

        } else {
            snackbar("Unknown type: " + netMessage.type);
        }
    }

    private void snackbar(String text) {
        Snackbar.make(activity.getViewPager(), text, Snackbar.LENGTH_SHORT).show();
    }

    public void setClient(ClientService client) {
        this.client = client;
    }

    private void request(NetMessage msg) {
        if (client != null) {
            client.send(msg);
        } else {
            snackbar("error test request");
        }
    }

}
