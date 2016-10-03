package mahappdev.caresilabs.com.myfriends.models;

import java.util.List;
import java.util.Map;

/**
 * Created by Simon on 10/3/2016.
 */

public class Groups {
    public static class Request extends  NetMessage {
        public Request() {
            this.type = "groups";
        }
    }

    public static class Response extends  NetMessage {

        public List<Map<String, String>> groups;

    }
}
