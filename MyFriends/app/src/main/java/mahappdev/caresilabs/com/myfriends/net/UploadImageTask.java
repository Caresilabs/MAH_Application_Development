package mahappdev.caresilabs.com.myfriends.net;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Simon on 10/6/2016.
 */

public class UploadImageTask extends AsyncTask<byte[], Void, Boolean> {
    private InetAddress address;
    private int         port;

    public UploadImageTask(InetAddress ip, String port) {
        this.address = ip;
        this.port = Integer.parseInt(port);
    }


    @Override
    protected Boolean doInBackground(byte[]... params) {
        String imageID = bytesToString(params[0]);
        byte[] uploadArray = params[1];

        if (imageID == null)
            return false;

        Socket socket = null;
        try {
            socket = new Socket(address, port);

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            output.writeUTF(imageID);
            output.flush();
            output.writeObject(uploadArray);
            output.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }

    public static String bytesToString(byte[] b) {
        try {
            return new String(b, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
        }

        return null;
    }
}
