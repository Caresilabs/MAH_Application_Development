package mahappdev.caresilabs.com.myfriends.net.models;

import java.util.List;
import java.util.Map;

/**
 * Created by Simon on 10/4/2016.
 */

public class Locations {

    public static class Response extends  NetMessage {

        public String group;

        public List<LocationData> location;

    }

    public static class LocationData {

        public String member;

        public String longitude;

        public String latitude;

    }
}
