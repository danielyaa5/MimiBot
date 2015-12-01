package com.example.yako.mimibot;

/**
 * Created by yako on 12/1/15.
 */
public class SshManager {
    private SshConnectStat connectionStatus;

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
    }

    public SshManager() {
        attemptConnection();
    }

    private void attemptConnection() {
        connectionStatus = SshConnectStat.CONNECTING;
    }

    public int getStatus() {
        return connectionStatus.intValue;
    }
}
