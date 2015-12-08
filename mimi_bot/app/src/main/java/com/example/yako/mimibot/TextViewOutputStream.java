package com.example.yako.mimibot;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public class TextViewOutputStream extends OutputStream {
    private final static String TAG = "TextViewOutputStream";

    public TextViewOutputStream() {

    }

    @Override
    public void write(byte[] buffer, int offset, int length) throws IOException {
        final String text = new String(buffer, offset, length);
        Log.i(TAG, "Receiving SSH text --> " + text);
        Log.i(TAG, "Last command was --> " + SshManager.last_command);
        String[] lines = text.split("[\\r\\n]+");
        Log.i(TAG, "Split text: " + Arrays.toString(lines));

        for (String line : lines) {
            Log.i(TAG, "Ssh Text Received: " + line);

            boolean text_valid = (line.trim().length() > 0 && !line.equals(SshManager.last_command) && !line.contains("$"));
            Log.i(TAG, "Ssh Text is " + (text_valid ? "valid": "invalid"));


            if (SshManager.mListener != null && text_valid) {
                final String _line=line;
                (new Thread(new Runnable() {
                    @Override
                    public void run() {

                        SshManager.mListener.onStdInReceived(_line);
                    }
                })).start();
            }
        }
    }

    @Override
    public void write(int b) throws IOException {
        write(new byte[]{(byte) b}, 0, 1);
    }
}