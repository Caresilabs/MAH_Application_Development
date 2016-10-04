package mahappdev.caresilabs.com.myfriends.net.models;

/**
 * Created by Simon on 10/4/2016.
 */

public class Deregister {
    public static class Request extends NetMessage {

        public Request() {
            this.type = "unregister";
        }

        public Request(String id) {
            this();
            this.id = id;
        }

        public String id;

    }

    public static class Response extends Request {

    }
}
