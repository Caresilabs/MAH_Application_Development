package mahappdev.caresilabs.com.myfriends.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Simon on 10/3/2016.
 */

public class DataModel {

    public List<String> userIds = new ArrayList<>();

    public Map<String, GroupModel> groups = new HashMap<>();

    public static class GroupModel {

        public GroupModel(String name) {
            this.name = name;
        }

        public String name;

        public List<MemberModel> members = new ArrayList<>();
    }

    public static class MemberModel {

        public MemberModel(String name) {
            this.name = name;
        }

        public String name;

        public double longitude;

        public double latitude;
    }
}
