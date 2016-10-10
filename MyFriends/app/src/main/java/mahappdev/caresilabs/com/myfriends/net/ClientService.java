package mahappdev.caresilabs.com.myfriends.net;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import mahappdev.caresilabs.com.myfriends.R;
import mahappdev.caresilabs.com.myfriends.net.models.LogMessage;
import mahappdev.caresilabs.com.myfriends.net.models.NetMessage;

public class ClientService extends Service {
    private ThreadQueue      thread;
    private Receive          receive;
    private Socket           socket;
    private DataInputStream  input;
    private DataOutputStream output;
    private InetAddress      address;
    private int              connectionPort;
    private String           ip;

    private INetworkResponseCallback receiveListener;
    private NetworkModelBinder       modelBinder;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null == intent) {
            String source = null == intent ? "intent" : "action";
            Log.e("Friends", source + " was null, flags=" + flags + " bits=" + Integer.toBinaryString(flags));
            return START_STICKY;
        }

        this.ip = intent.getStringExtra("IpAddress");
        this.connectionPort = intent.getIntExtra("TcpPort", 9999);

        //receiveBuffer = new ThreadQueue.Buffer<String>();
        this.modelBinder = new NetworkModelBinder();
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new LocalService();
    }

    public void setListener(INetworkResponseCallback listener) {
        this.receiveListener = listener;
    }

    public class LocalService extends Binder {
        public ClientService getService() {
            return ClientService.this;
        }
    }

    public void connect(boolean onlyOnce) {
        if (isOnline()) {
           if (onlyOnce && socket != null) {
           /*    if (thread != null)
                   thread.stop();
               if (receive != null)
                   receive.interrupt();

               thread = new ThreadQueue();
               thread.start();
               receive = new Receive();
               receive.start();*/
               thread = new ThreadQueue();
               thread.start();
               return;
           }

            thread = new ThreadQueue();
            thread.start();
            thread.enqueue(new Connect());
        } else {
            postMessage(new LogMessage(getString(R.string.no_connection)));
        }
    }

    public void disconnect() {
        thread.enqueue(new Disconnect());
    }

    public void disconnectNow() {
        new Disconnect().run();
    }

    public void send(NetMessage msg) {
        if (output != null) {
            thread.enqueue(new Send(new Gson().toJson(msg)));
        } else {
            //  postMessage(new LogMessage("Error sending message: No Connection"));
            postMessage(new LogMessage(getString(R.string.no_connection)));
        }
    }

    private void postMessage(NetMessage msg) {
        if (receiveListener != null) {
            receiveListener.onReceive(msg);
        }
    }

    private boolean isOnline() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public InetAddress getAddress() {
        return address;
    }

    private class Connect implements Runnable {
        public void run() {
            try {

                address = InetAddress.getByName(ip);
                socket = new Socket(address, connectionPort);
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());
                output.flush();

                postMessage(new LogMessage(getString(R.string.connected)));

                receive = new Receive();
                receive.start();
            } catch (Exception e) { // SocketException, UnknownHostException
                postMessage(new LogMessage(e.getMessage()));
            }
        }
    }

    private class Disconnect implements Runnable {
        public void run() {
            try {
                if (thread != null)
                    thread.stop();
                if (receive != null)
                    receive.interrupt();

                if (input != null)
                    input.close();
                if (output != null)
                    output.close();
                if (socket != null)
                    socket.close();

                postMessage(new LogMessage(getString(R.string.closed)));
            } catch (IOException e) {
                postMessage(new LogMessage(e.getMessage()));
            }
        }
    }

    private class Send implements Runnable {
        private String data;

        public Send(String data) {
            this.data = data;
        }

        public void run() {
            try {
                output.writeUTF(data);
                output.flush();
            } catch (IOException e) {
                if (e.getMessage().contains("Broken Pipe") || e instanceof SocketException) {
                    postMessage(new LogMessage( getString(R.string.reconnecting)));
                    disconnectNow();
                    connect(false);
                } else {
                    postMessage(new LogMessage(e.getMessage()));
                }
            }
        }
    }

    private class Receive extends Thread {
        public void run() {
            String result;
            try {
                while (receive != null) {
                    result = (String) input.readUTF();
                    postMessage(modelBinder.get(result));
                }
            } catch (Exception e) {
                receive = null;
            }
        }
    }

}
