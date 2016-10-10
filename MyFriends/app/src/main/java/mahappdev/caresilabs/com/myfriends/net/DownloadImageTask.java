package mahappdev.caresilabs.com.myfriends.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Simon on 10/6/2016.
 */


public class DownloadImageTask extends AsyncTask<String, Void, byte[]> {
    private final IImageDownloaded callback;
    private final InetAddress      address;
    private final int              port;

    public DownloadImageTask(InetAddress ip, String port, IImageDownloaded callback) {
        this.address = ip;
        this.port = Integer.parseInt(port);
        this.callback = callback;
    }

    @Override
    protected byte[] doInBackground(String... params) {
        String imageID = params[0];
        byte[] downloadArray = null; // the image from the server will be stored here
        Socket socket = null;

        try {
            socket = new Socket(address, port);
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

            output.flush();
            output.writeUTF(imageID);
            output.flush();
            downloadArray = (byte[]) input.readObject();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return downloadArray;
    }

    @Override
    protected void onPostExecute(byte[] bytes) {
        super.onPostExecute(bytes);
        Bitmap bit = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        callback.onDownloaded(bit);
    }

    public interface IImageDownloaded {
        void onDownloaded(Bitmap bitmap);
    }

}
