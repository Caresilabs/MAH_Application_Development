package mahappdev.caresilabs.com.myfriends.net.models;

/**
 * Created by Simon on 10/3/2016.
 */

public class LogMessage extends NetMessage {
    public LogMessage() {
        this.type = "exception";
    }

    public LogMessage(String message) {
        super();
        this.message = message;
    }

    public String message;
}
