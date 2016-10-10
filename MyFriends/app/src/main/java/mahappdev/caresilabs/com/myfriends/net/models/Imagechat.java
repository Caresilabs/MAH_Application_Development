package mahappdev.caresilabs.com.myfriends.net.models;

/**
 * Created by Simon on 10/6/2016.
 */

public class Imagechat {
    public static class Request extends NetMessage {
        public Request() {
            this.type = "imagechat";
        }

        public Request(String id, String text,  double latitude, double longitude) {
            this();
            this.id = id;
            this.text = text;
            this.longitude = String.valueOf(longitude);
            this.latitude = String.valueOf(latitude);
        }

        public String id;

        public String text;

        public String longitude;

        public String latitude;

    }

    public static class Response extends Imagechat.Request {
        public String group;

        public String member;

        public String port;

        public String imageid;
    }

    public static class Upload  extends NetMessage{
        public String port;

        public String imageid;
    }
}
