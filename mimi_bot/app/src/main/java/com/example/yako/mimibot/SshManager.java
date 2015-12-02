package com.example.yako.mimibot;

import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.Log;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Properties;

/**
 * Created by yako on 12/1/15.
 */
public class SshManager {
    static PrintStream commander;
    static ByteArrayOutputStream baos = new ByteArrayOutputStream();
    static Session session;

    private static final String TAG = "SshManager";

    public static SshConnectStat connectionStatus = SshConnectStat.DISCONNECTED;
    ;

    public enum SshConnectStat {
        DISCONNECTED("Disconnected", 0),
        CONNECTING("Connecting", 1),
        CONNECTED("Connected", 2);

        private String stringValue;
        private int intValue;
        private SshConnectStat(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }

        public int toInt() { return intValue; }
    }

    public interface OnTaskCompleted{
        void onTaskCompleted();
    }

    public static void attemptConnection(String username, String password, String hostname, int port, SshConnectResponse listener) {
        connectionStatus = SshConnectStat.CONNECTING;

        SshConnect mySshConnect = new SshConnect(username, password, hostname, port, listener);
        mySshConnect.execute(1);

    }



    private static String executeRemoteCommand(String username, String password, String hostname, int port) throws Exception {
        JSch jsch = new JSch();
        session = jsch.getSession(username, hostname, port);
        session.setPassword(password);

        // Avoid asking for key confirmation
        Properties prop = new Properties();
        prop.put("StrictHostKeyChecking", "no");
        session.setConfig(prop);

        session.connect();

        // SSH Channel
        ChannelShell channelssh = (ChannelShell) session.openChannel("shell");
        OutputStream inputstream_for_the_channel = channelssh.getOutputStream();
        commander = new PrintStream(inputstream_for_the_channel, true);

        channelssh.setOutputStream(baos);

        channelssh.setOutputStream(System.out, true);

        channelssh.connect();


        //commander.close();        //session.disconnect();
        return baos.toString();
    }

    public static class SshConnect extends AsyncTask<Integer, Void, Void> {
        String username, password, hostname;
        int port;
        SshConnect asyncObject;

        public SshConnectResponse listener = null;

        public SshConnect(String username, String password, String hostname, int port, SshConnectResponse listener) {
            this.username = username;
            this.password = password;
            this.hostname = hostname;
            this.port = port;
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
            asyncObject = this;
            new CountDownTimer(6000, 1000) {
                public void onTick(long millisUntilFinished) {
                    // You can monitor the progress here as well by changing the onTick() time
                }

                public void onFinish() {
                    // stop async task if not in progress
                    if (asyncObject.getStatus() == AsyncTask.Status.RUNNING) {
                        connectionStatus = SshConnectStat.DISCONNECTED;
                        Log.i(TAG, "Finished connection task, result = " + SshManager.connectionStatus.toString());
                        if (listener != null) {
                            listener.sshConnectCb();
                        }
                        asyncObject.cancel(true);
                        // Add any specific task you wish to do as your extended class variable works here as well.
                    }
                }
            }.start();
        }

        @Override
        protected Void doInBackground(Integer... params) {

            try {
                executeRemoteCommand(username, password, hostname, port);
                connectionStatus = SshConnectStat.CONNECTED;
            } catch (Exception e) {
                connectionStatus = SshConnectStat.DISCONNECTED;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (listener != null) listener.sshConnectCb();
            Log.i(TAG, "Finished connection task, result = " + SshManager.connectionStatus.toString());
        }
    }


}
