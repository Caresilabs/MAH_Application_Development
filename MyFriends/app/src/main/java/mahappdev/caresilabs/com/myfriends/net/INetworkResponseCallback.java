package mahappdev.caresilabs.com.myfriends.net;

import mahappdev.caresilabs.com.myfriends.net.models.NetMessage;

/**
 * Created by Simon on 10/2/2016.
 */

public interface INetworkResponseCallback {

    void onReceive(NetMessage netMessage);

}
