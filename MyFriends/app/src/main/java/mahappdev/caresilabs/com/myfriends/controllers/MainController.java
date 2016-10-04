package mahappdev.caresilabs.com.myfriends.controllers;

import android.support.design.widget.Snackbar;
import android.text.Editable;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import mahappdev.caresilabs.com.myfriends.models.DataModel;
import mahappdev.caresilabs.com.myfriends.net.models.Deregister;
import mahappdev.caresilabs.com.myfriends.net.models.Groups;
import mahappdev.caresilabs.com.myfriends.net.models.Location;
import mahappdev.caresilabs.com.myfriends.net.models.Locations;
import mahappdev.caresilabs.com.myfriends.net.models.LogMessage;
import mahappdev.caresilabs.com.myfriends.net.models.Members;
import mahappdev.caresilabs.com.myfriends.net.models.NetMessage;
import mahappdev.caresilabs.com.myfriends.net.models.Register;
import mahappdev.caresilabs.com.myfriends.net.ClientService;
import mahappdev.caresilabs.com.myfriends.net.INetworkResponseCallback;
import mahappdev.caresilabs.com.myfriends.net.models.Textchat;
import mahappdev.caresilabs.com.myfriends.views.ChatFragment;
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
    private final ChatFragment   chatFragment;
    private       ClientService  client;

    // All the cached data
    private DataModel model;

    private double myLatitude, myLongitude;

    public MainController(MainActivity activity, GroupsFragment groupsFragment, MapsFragment mapsFragment, ChatFragment chatFragment) {
        this.activity = activity;
        this.groupsFramgent = groupsFragment;
        this.mapsFragment = mapsFragment;
        this.chatFragment = chatFragment;

        this.model = new DataModel();

        startLocationPingTimer();
    }

    public void updateSubscription(String room, boolean join) {
        if (join)
            joinRoom(room);
        else
            unjoinRoom(room);
    }

    private void joinRoom(String room) {
        Register.Request join = new Register.Request(room, model.myName);
        request(join);
    }

    private void unjoinRoom(String room) {
        Deregister.Request unregister = new Deregister.Request(model.userIds.get(room));
        request(unregister);

        model.userIds.remove(room);
    }

    public void sendChatMessage(String text) {
        if (model.currentGroupName != null) {
            Textchat.Request chat = new Textchat.Request(model.userIds.get(model.currentGroupName), text);
            request(chat);
        } else {
            snackbar("Please join a room before you start chatting!");
        }
    }

    public void setActiveRoom(String room) {
        model.currentGroupName = room;
    }

    public void refreshGroups() {
        Groups.Request msg = new Groups.Request();
        request(msg);
    }

    private void updateUserLocation(double lat, double lgt) {
        for (String id : model.userIds.values()) {
            Location.Request location = new Location.Request(id, lat, lgt);
            request(location);
        }
    }

    private void refreshMembers(String room) {
        Members.Request members = new Members.Request(room);
        request(members);
    }

    private void startLocationPingTimer() {
        Timer task = new Timer();
        task.schedule(new TimerTask() {
            @Override
            public void run() {
                // Send location to server
                updateUserLocation(myLatitude, myLongitude);

                // Refresh the groups
                refreshGroups();
            }
        }, 1000, 20000);
    }

    @Override
    public void onReceive(NetMessage netMessage) {
        if (netMessage instanceof LogMessage) {
            snackbar(((LogMessage) netMessage).message);
        } else if (netMessage instanceof Groups.Response) {
            // Clear all groups before
            model.groups.clear();

            final List<Map<String, String>> groups = ((Groups.Response) netMessage).groups;

            if (groups.size() != 0) {
                for (Map<String, String> group : groups) {
                    String name = group.get("group");
                    model.groups.put(name, new DataModel.GroupModel(name));

                    refreshMembers(name);
                }
            } else {
                // if there is not groups. update list instead of members
                groupsFramgent.refreshGroups(model.groups.values(), model.myName, model.currentGroupName);
            }

        } else if (netMessage instanceof Members.Response) {
            List<Map<String, String>> members = ((Members.Response) netMessage).members;

            for (Map<String, String> member : members) {
                String memberName = member.get("member");
                model.groups.get(((Members.Response) netMessage).group)
                        .members.put(memberName, new DataModel.MemberModel(memberName));
            }

            groupsFramgent.refreshGroups(model.groups.values(), model.myName, model.currentGroupName); //((Groups.Response) netMessage).groups

        } else if (netMessage instanceof Register.Response) {
            Register.Response register = (Register.Response) netMessage;

            model.userIds.put(register.group, register.id);
            model.currentGroupName = register.group;

            refreshGroups();
            updateUserLocation(myLatitude, myLongitude);

            snackbar("Joined Room as " + ((Register.Response) netMessage).id);

        } else if (netMessage instanceof Deregister.Response) {
            // Do nothing now
            snackbar("Unregistered Room as User: " + ((Deregister.Response) netMessage).id);

            refreshGroups();
        } else if (netMessage instanceof Locations.Response) {
            Locations.Response location = ((Locations.Response) netMessage);
            DataModel.GroupModel group = model.groups.get(location.group);

            if (location.location != null) {
                // group.members.clear();
                for (Locations.LocationData loc : location.location) {
                    DataModel.MemberModel member = group.members.get(loc.member);
                    if (member != null) {
                        member.longitude = loc.longitude;
                        member.latitude = loc.latitude;
                    }
                }

                if (location.group.equals(model.currentGroupName)) {
                    mapsFragment.updateMarkers(group, model.myName);
                }
            }
        } else if (netMessage instanceof Textchat.Response) {
            Textchat.Response message = ((Textchat.Response) netMessage);

            if (message.group != null && message.group.equals(model.currentGroupName)) {
                chatFragment.addChatMessage(message.member, message.text, model.myName.equals(message.member));
            }
        } else if (netMessage instanceof Location.Response) {
            // Do nothing
        } else {
            snackbar("Unknown type: " + netMessage.type);
        }
    }

    private void snackbar(String text) {
        Snackbar.make(activity.getViewPager(), text, Snackbar.LENGTH_SHORT).show();
    }

    private void request(NetMessage msg) {
        if (client != null) {
            client.send(msg);
        } else {
            snackbar("error test request");
        }
    }

   /* public  void setCurrentRoom(String room) {
        model.currentGroup = model.groups.get(room);
    }*/

    public void setClient(ClientService client) {
        this.client = client;

        // if we get the client. Refresh the groups
        refreshGroups();
    }

    public void setMyLocation(double latitude, double longitude) {
        this.myLatitude = latitude;
        this.myLongitude = longitude;
    }

}
