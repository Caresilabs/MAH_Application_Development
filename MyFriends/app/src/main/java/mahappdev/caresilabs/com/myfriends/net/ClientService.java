package mahappdev.caresilabs.com.myfriends.net;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import mahappdev.caresilabs.com.myfriends.net.models.LogMessage;
import mahappdev.caresilabs.com.myfriends.net.models.NetMessage;

public class ClientService extends Service {
    //public static final String IP="IP",PORT="PORT"; //

    private ThreadQueue                thread;
    private Receive                    receive;
    //private  ThreadQueue.Buffer<String> receiveBuffer; //
    private Socket                     socket;
    private DataInputStream          input;
    private DataOutputStream         output;
    private InetAddress                address;
    private int                        connectionPort;
    private String                     ip;
    private Exception                  exception;

    private INetworkResponseCallback receiveListener;
    private NetworkModelBinder modelBinder;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.ip = intent.getStringExtra("IpAddress");
        this.connectionPort = intent.getIntExtra("TcpPort", 9999);
        thread = new ThreadQueue();
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

    public void connect() {
        thread.start();
        thread.enqueue(new Connect());
    }

    public void disconnect() {
        thread.enqueue(new Disconnect());
    }

    public void send(NetMessage msg) {
        thread.enqueue(new Send(new Gson().toJson(msg)));
    }

    private void postMessage(NetMessage msg) {
        if (receiveListener != null) {
            receiveListener.onReceive(msg);
        }
    }

    private class Connect implements Runnable {
        public void run() {
            try {
                address = InetAddress.getByName(ip);
                socket = new Socket(address, connectionPort);
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());
                output.flush();

                // receiveBuffer.put("CONNECTED");
                postMessage(new LogMessage("Connected!"));

                receive = new Receive();
                receive.start();
            } catch (Exception e) { // SocketException, UnknownHostException
                exception = e;
                postMessage(new LogMessage(e.getMessage()));
                //receiveBuffer.put("EXCEPTION");
            }
        }
    }

    private class Disconnect implements Runnable {
        public void run() {
            try {
                if (input != null)
                    input.close();
                if (output != null)
                    output.close();
                if (socket != null)
                    socket.close();
                thread.stop();
                 // receiveBuffer.put("CLOSED");
                postMessage(new LogMessage("Closed"));
            } catch(IOException e) {
                exception = e;
                postMessage(new LogMessage(e.getMessage()));
                // receiveBuffer.put("EXCEPTION");
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
                exception = e;

                postMessage(new LogMessage(e.getMessage()));
                //receiveBuffer.put("EXCEPTION");
            }
        }
    }

    private class Receive extends Thread {
        public void run() {
            String result;
            try {
                while (receive != null) {
                    result = (String) input.readUTF();
                    //receiveBuffer.put(result);

                    postMessage(modelBinder.get(result));
                }
            } catch (Exception e) { // IOException, ClassNotFoundException
                receive = null;
            }
        }
    }
}
