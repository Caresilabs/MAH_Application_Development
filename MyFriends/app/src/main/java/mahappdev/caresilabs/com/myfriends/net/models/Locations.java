package mahappdev.caresilabs.com.myfriends.net.models;

/**
 * Created by Simon on 10/3/2016.
 */

public class Locations {
    public static class Request extends  NetMessage {

        public Request(String id, double longitude, double latitude) {
            this.id = id;
            this.type = "location";
            this.longitude = String.valueOf(longitude);
            this.latitude = String.valueOf(latitude);
        }

        public String id;

        public String longitude;

        public String latitude;
    }

    public static class Response extends  NetMessage {

        public String id;

        public String longitude;

        public String latitude;

    }
}
