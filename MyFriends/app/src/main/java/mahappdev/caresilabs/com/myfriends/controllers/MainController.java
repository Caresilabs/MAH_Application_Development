package mahappdev.caresilabs.com.myfriends.controllers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.Snackbar;
import android.support.v7.app.NotificationCompat;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import mahappdev.caresilabs.com.myfriends.R;
import mahappdev.caresilabs.com.myfriends.models.DataModel;
import mahappdev.caresilabs.com.myfriends.net.DownloadImageTask;
import mahappdev.caresilabs.com.myfriends.net.UploadImageTask;
import mahappdev.caresilabs.com.myfriends.net.models.Deregister;
import mahappdev.caresilabs.com.myfriends.net.models.Groups;
import mahappdev.caresilabs.com.myfriends.net.models.Imagechat;
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
    private       Timer          task;

    // All the cached data
    private final DataModel model;

    public MainController(MainActivity activity, GroupsFragment groupsFragment, MapsFragment mapsFragment, ChatFragment chatFragment, String savedModel) {
        this.activity = activity;
        this.groupsFramgent = groupsFragment;
        this.mapsFragment = mapsFragment;
        this.chatFragment = chatFragment;

        if (savedModel == null) {
            this.model = new DataModel();
        } else {
            this.model = new Gson().fromJson(savedModel, DataModel.class);
            updateFromSaveState();
        }
    }

    private void updateFromSaveState() {
        // Update markers
        DataModel.GroupModel group = model.groups.get(model.currentGroupName);
        if (group != null) {
            mapsFragment.updateMarkers(group, model.myName);
        }

        updateChat();
    }

    private void updateChat() {
        // Restore Chat
        chatFragment.clearChats();
        List<DataModel.ChatModel> chats = model.chats.get(model.currentGroupName);
        if (chats != null) {
            for (DataModel.ChatModel chat : chats) {
                chatFragment.addChatMessage(chat);
            }
        }
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

        // Update if you leave an active room
        if (room.equals(model.currentGroupName)) {
            boolean hasSet = false;
            for (String rm : model.userIds.keySet()) {
                if (!rm.equals(model.currentGroupName)) {
                    setActiveRoom(rm.toString());

                    hasSet = true;
                    break;
                }
            }
            if (!hasSet) {
                setActiveRoom(null);
            }
        }

        model.userIds.remove(room);
    }

    public void sendChatMessage(String text, boolean image) {
        if (model.currentGroupName != null) {
            NetMessage chat;
            if (image) {
                chat = new Imagechat.Request(model.userIds.get(model.currentGroupName), text, model.myLatitude, model.myLongitude);
            } else {
                chat = new Textchat.Request(model.userIds.get(model.currentGroupName), text);
            }
            request(chat);
        } else {
            snackbar(activity.getString(R.string.msg_join_before_chat));
        }
    }

    public void setActiveRoom(String room) {
        model.currentGroupName = room;

        mapsFragment.updateMarkers(model.groups.get(room), model.myName);
        updateChat();
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
        task = new Timer();
        task.schedule(new TimerTask() {
            @Override
            public void run() {
                // Send location to server
                updateUserLocation(model.myLatitude, model.myLongitude);

                // Refresh the groups
                refreshGroups();
            }
        }, 2000, 20000);
    }

    public void onResume() {
        startLocationPingTimer();
    }

    public void onPause() {
        task.cancel();
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
            }

            groupsFramgent.refreshGroups(model.groups.values(), model.userIds, model.currentGroupName);

        } else if (netMessage instanceof Members.Response) {
            List<Map<String, String>> members = ((Members.Response) netMessage).members;

            for (Map<String, String> member : members) {
                String memberName = member.get("member");
                model.groups.get(((Members.Response) netMessage).group)
                        .members.put(memberName, new DataModel.MemberModel(memberName));
            }

            groupsFramgent.refreshGroups(model.groups.values(), model.userIds, model.currentGroupName); //((Groups.Response) netMessage).groups

        } else if (netMessage instanceof Register.Response) {
            Register.Response register = (Register.Response) netMessage;

            model.userIds.put(register.group, register.id);
            setActiveRoom(register.group);

            refreshGroups();
            updateUserLocation(model.myLatitude, model.myLongitude);

            snackbar(activity.getString(R.string.joined_room) + " " + register.group); //+ ((Register.Response) netMessage).id);

        } else if (netMessage instanceof Deregister.Response) {
            // Do nothing now
            snackbar(activity.getString(R.string.unregistered_room));

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

            DataModel.ChatModel chat = new DataModel.ChatModel();
            chat.isUser = model.myName.equals(message.member);
            chat.member = message.member;
            chat.message = message.text;

            if (message.group != null) {
                if (message.group.equals(model.currentGroupName))
                    addChatMessage(chat);

                sendNotification(chat, message.group);
            }
        } else if (netMessage instanceof Imagechat.Response) {
            final Imagechat.Response message = ((Imagechat.Response) netMessage);

            if (message.group == null)
                return;

            new DownloadImageTask(client.getAddress(), message.port, new DownloadImageTask.IImageDownloaded() {
                @Override
                public void onDownloaded(Bitmap bitmap) {
                    DataModel.ChatModel chat = new DataModel.ChatModel();
                    chat.isUser = model.myName.equals(message.member);
                    chat.member = message.member;
                    chat.message = message.text;
                    chat.image = chatFragment.saveBitmap(bitmap);

                    if (message.group.equals(model.currentGroupName)) {
                        addChatMessage(chat);
                    }

                    sendNotification(chat, message.group);
                }
            }).execute(message.imageid);
        } else if (netMessage instanceof Imagechat.Upload) {
            byte[] imageId = ((Imagechat.Upload) netMessage).imageid.getBytes();

            new UploadImageTask(client.getAddress(), ((Imagechat.Upload) netMessage).port)
                    .execute(imageId, chatFragment.consumeNextPhoto());
        } else if (netMessage instanceof Location.Response) {
            // Do nothing
        } else {
            snackbar("Unknown type: " + netMessage.type);
        }
    }

    private void addChatMessage(DataModel.ChatModel chat) {
        if (!model.chats.containsKey(model.currentGroupName)) {
            model.chats.put(model.currentGroupName, new ArrayList<DataModel.ChatModel>());
        }

        model.chats.get(model.currentGroupName).add(chat);
        chatFragment.addChatMessage(chat);
    }

    private void snackbar(String text) {
        Snackbar.make(activity.getViewPager(), text, Snackbar.LENGTH_LONG).show();
    }

    private void request(NetMessage msg) {
        if (client != null) {
            client.send(msg);
        } else {
            snackbar(activity.getString(R.string.no_connection));
        }
    }

    public void unjoinAll() {
        for (String room : model.userIds.keySet()) {
            unjoinRoom(room);
        }
    }

    public void setClient(ClientService client) {
        this.client = client;

        // if we get the client. Refresh the groups
        refreshGroups();
    }

    private void sendNotification(DataModel.ChatModel chat, String group) {
        if (chat.member.equals(model.myName))
            return;

        // We already have chat up
        if (activity.getViewPager().getCurrentItem() == 2)
            return;

        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(activity)
                        .setSmallIcon(R.drawable.ic_icon)
                        .setContentTitle("New Message!")
                        .setOnlyAlertOnce(false)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setAutoCancel(true)
                        .setContentText("[" + group + "] " + chat.member + ": " + chat.message);


        int mNotificationId = 001;
        NotificationManager mNotifyMgr =
                (NotificationManager) activity.getSystemService(activity.NOTIFICATION_SERVICE);

        Intent resultIntent = activity.getIntent();
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resultIntent.setAction(Intent.ACTION_MAIN);

        PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }


    public String onSave() {
        return new Gson().toJson(model);
    }

    public void setMyLocation(double latitude, double longitude) {
        this.model.myLatitude = latitude;
        this.model.myLongitude = longitude;
    }

    public void setName(String name) {
        if (model.myName == null) {
            unjoinAll();
        }

        this.model.myName = name;
    }
}
