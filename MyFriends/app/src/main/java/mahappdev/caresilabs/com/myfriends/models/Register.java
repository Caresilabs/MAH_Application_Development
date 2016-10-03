package mahappdev.caresilabs.com.myfriends.models;

/**
 * Created by Simon on 10/2/2016.
 */

public class Register {
    public static class Request extends  NetMessage {

        public Request(String group, String member) {
            this.group = group;
            this.member = member;
        }

        public String group;

        public String member;

    }

    public static class Response extends NetMessage {
        public String id;
    }
}
