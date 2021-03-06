package com.example.yako.mimibot;

import android.os.AsyncTask;
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
    static String username, hostname, password;
    static int port;
    static SshConnectResponse listener;
    static ByteArrayOutputStream baos = new ByteArrayOutputStream();
    static Session session;

    public static String last_command = "";

    private static final String TAG = "SshManager";

    public static SshConnectStat connectionStatus = SshConnectStat.DISCONNECTED;

    public static OnStdInReceivedListener mListener = null;

    public enum SshConnectStat {
        DISCONNECTED("Disconnected", 0),
        CONNECTING("Connecting", 1),
        CONNECTED("Connected", 2),
        FAILED("Failed", 3);

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

        public int toInt() {
            return intValue;
        }
    }

    public interface OnTaskCompleted {
        void onTaskCompleted();
    }

    public static void disconnect() {
        Log.i(TAG, "Attempting Disconnect From SSH");
        if (connectionStatus.toInt() == 2 || connectionStatus.toInt() == 1) {
            commander.close();
            session.disconnect();
            connectionStatus = SshConnectStat.DISCONNECTED;
            if (SshConnect.listener != null) {
                SshConnect.listener.sshConnectCb();
            }
            Log.i(TAG, "Already Disconnect From SSH");
        } else {
            Log.i(TAG, "Already Disconnected From SSH");
        }
//        attemptConnection(username, password,hostname, port, listener);
    }

    public static void setOnStdReceivedListener(OnStdInReceivedListener listener) {
        mListener = listener;
    }

    public static void removeStdReceivedListener() {
        mListener = null;
    }

    public static void attemptConnection(String username, String password, String hostname, int port, SshConnectResponse listener) {
        SshManager.username = username;
        SshManager.password = password;
        SshManager.hostname = hostname;
        SshManager.port = port;
        SshManager.listener = listener;

        Log.i(TAG, username + "- " + password + "- " + hostname + "- " + String.valueOf(port));
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

        TextViewOutputStream tvos = new TextViewOutputStream();
        PrintStream tvps = new PrintStream(tvos);
        channelssh.setOutputStream(tvos, true);
        System.setOut(tvps);

        channelssh.setOutputStream(baos);

        channelssh.setOutputStream(System.out, true);


        channelssh.connect();
        //commander.close();        //session.disconnect();
        return baos.toString();
    }

    public static void sendCommand(String command) {
        if (connectionStatus.toInt() == 2) {
            last_command = command;
            commander.println(command);
        } else {
            Log.e(TAG, "Cant send command while not connected");
        }
    }

    public static class SshConnect extends AsyncTask<Integer, Void, Void> {
        String username, password, hostname;
        int port;
        SshConnect asyncObject;

        public static SshConnectResponse listener = null;

        public SshConnect(String username, String password, String hostname, int port, SshConnectResponse listener) {
            this.username = username;
            this.password = password;
            this.hostname = hostname;
            this.port = port;
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {

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
            if (connectionStatus.toInt() != 2) {
                connectionStatus = SshConnectStat.FAILED;
            }

            if (listener != null) listener.sshConnectCb();
            Log.i(TAG, "Finished connection task, result = " + SshManager.connectionStatus.toString());
        }
    }

    public interface OnStdInReceivedListener {
        public void onStdInReceived(String stdIn);
    }


}
