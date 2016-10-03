package mahappdev.caresilabs.com.myfriends.models;

import java.util.List;
import java.util.Map;

/**
 * Created by Simon on 10/3/2016.
 */

public class Members {
    public static class Request extends  NetMessage {

        public Request(String group) {
            this.type = "members";
            this.group = group;
        }

        public String group;

    }

    public static class Response extends  NetMessage {

        public String group;

        public List<Map<String, String>> members;

    }
}
