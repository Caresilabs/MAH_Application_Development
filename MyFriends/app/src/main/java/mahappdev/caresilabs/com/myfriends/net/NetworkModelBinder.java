package mahappdev.caresilabs.com.myfriends.net;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;

import mahappdev.caresilabs.com.myfriends.models.LogMessage;
import mahappdev.caresilabs.com.myfriends.models.Groups;
import mahappdev.caresilabs.com.myfriends.models.Members;
import mahappdev.caresilabs.com.myfriends.models.NetMessage;
import mahappdev.caresilabs.com.myfriends.models.Register;

/**
 * Created by Simon on 10/2/2016.
 */

public class NetworkModelBinder {

    private final HashMap<String, Class<? extends NetMessage>> types;

    public NetworkModelBinder() {
        this.types = new HashMap<>();
        bind();
    }

    private void bind() {
        types.put("register", Register.Response.class);
        types.put("groups", Groups.Response.class);
        types.put("members", Members.Response.class);
        types.put("exception", LogMessage.class);
    }

    public NetMessage get(String json) {
        Gson gson = new Gson();

        JsonObject elem =  gson.fromJson(json, JsonObject.class);
        String type = elem.get("type").getAsString();

        return gson.fromJson(elem, types.get(type));
    }
}
