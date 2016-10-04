package mahappdev.caresilabs.com.myfriends.net.models;

/**
 * Created by Simon on 10/4/2016.
 */

public class Textchat {
    public static class Request extends NetMessage {
        public Request() {
            this.type = "textchat";
        }

        public Request(String id, String text) {
            this();
            this.id = id;
            this.text = text;
        }

        public String id;

        public String text;

    }

    public static class Response extends Request {
        public String group;

        public String member;
    }
}
