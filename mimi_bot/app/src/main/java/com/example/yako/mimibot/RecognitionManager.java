package com.example.yako.mimibot;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import de.dfki.ccaal.gestures.GestureRecognitionService;
import de.dfki.ccaal.gestures.IGestureRecognitionListener;
import de.dfki.ccaal.gestures.IGestureRecognitionService;
import de.dfki.ccaal.gestures.classifier.Distribution;

/**
 * Created by yako on 12/6/15.
 */
public class RecognitionManager {
    private static final String TAG = "RecognitionManager";

    public static ServiceConnection recognitionConnection = null;
    private static IBinder gestureListenerStub = null;
    private static IGestureRecognitionService recognitionService;
    private static String activeTrainingSet;

    public static void setupRecognitionConnection() {
        Log.i(TAG, "setupRecognitionConnection()");
        if (recognitionConnection == null) {
            recognitionConnection = new ServiceConnection() {

                public void onServiceConnected(ComponentName className, IBinder service) {
                    Log.i(TAG, "onServiceConnected");
                    recognitionService = IGestureRecognitionService.Stub.asInterface(service);
                    try {
                        recognitionService.startClassificationMode(activeTrainingSet);
                        Log.i(TAG, "gestureConnection service established!");
                    } catch (RemoteException e) {
                        Log.e(TAG, "gestureConnection service failed to establish!");
                        e.printStackTrace();
                    }
                }

                public void onServiceDisconnected(ComponentName className) {
                    Log.i(TAG, "gestureConnection service disconnected!");
                    recognitionService = null;
                }
            };
        }
    }

    public static void stopRecognitionConnection(Context appContext) {
        Log.i(TAG, "startRecognitionConnection()");
        if (recognitionConnection != null) {
            appContext.unbindService(recognitionConnection);
        }
    }

    public static void startRecognitionConnection(Context appContext, Context activityContext) {
        Intent bindIntent = new Intent(activityContext, GestureRecognitionService.class);
        appContext.bindService(bindIntent, recognitionConnection, Context.BIND_AUTO_CREATE);
    }

    public static void setRecognitionService(IGestureRecognitionService recService) {
        recognitionService = recService;
    }



    public static void unregisterRecognitionListerner() {
        if (recognitionService != null){
            try {
                recognitionService.unregisterListener(IGestureRecognitionListener.Stub.asInterface(gestureListenerStub));
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        recognitionService = null;
    }

    public static void registerTestRecognitionListener(final Activity activity) {
        gestureListenerStub = new IGestureRecognitionListener.Stub() {

            @Override
            public void onGestureLearned(String gestureName) throws RemoteException {
                Toast.makeText(activity, String.format("Gesture %s learned", gestureName), Toast.LENGTH_SHORT).show();
                System.err.println("Gesture %s learned");
            }

            @Override
            public void onTrainingSetDeleted(String trainingSet) throws RemoteException {
                Toast.makeText(activity, String.format("Training set %s deleted", trainingSet), Toast.LENGTH_SHORT).show();
                System.err.println(String.format("Training set %s deleted", trainingSet));
            }

            @Override
            public void onGestureRecognized(final Distribution distribution) throws RemoteException {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, String.format("%s: %f", distribution.getBestMatch(), distribution.getBestDistance()), Toast.LENGTH_LONG).show();
                        System.err.println(String.format("%s: %f", distribution.getBestMatch(), distribution.getBestDistance()));
                    }
                });
            }
        };
    }
}
