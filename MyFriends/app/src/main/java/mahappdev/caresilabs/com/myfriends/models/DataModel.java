package mahappdev.caresilabs.com.myfriends.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Simon on 10/3/2016.
 */

public class DataModel {

    public String myName;

    public double myLatitude, myLongitude;

    public Map<String, String> userIds = new HashMap<>();

    public String currentGroupName;

    public Map<String, GroupModel> groups = new HashMap<>();

    public Map<String, List<ChatModel>> chats = new HashMap<>();

    public static class GroupModel {

        public GroupModel(String name) {
            this.name = name;
        }

        public String name;

        public Map<String, MemberModel> members = new HashMap<>();

    }

    public static class MemberModel {

        public MemberModel(String name) {
            this.name = name;
        }

        public String name;

        public String longitude;

        public String latitude;
    }

    public static class ChatModel {
        public String member;

        public String group;

        public String message;

        public boolean isUser;

        public String image;
    }
}
