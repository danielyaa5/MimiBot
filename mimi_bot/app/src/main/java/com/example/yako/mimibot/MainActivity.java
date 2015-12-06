package com.example.yako.mimibot;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.yako.mimibot.pages.HomeFragment;
import com.example.yako.mimibot.pages.PlayFragment;
import com.example.yako.mimibot.pages.SettingsFragment;
import com.example.yako.mimibot.pages.TeachFragment;

import java.util.ArrayList;
import java.util.Arrays;

import de.dfki.ccaal.gestures.GestureRecognitionService;
import de.dfki.ccaal.gestures.IGestureRecognitionListener;
import de.dfki.ccaal.gestures.IGestureRecognitionService;
import de.dfki.ccaal.gestures.classifier.Distribution;

public class MainActivity extends Activity implements HomeFragment.OnFragmentInteractionListener, SettingsFragment.OnFragmentInteractionListener, TeachFragment.OnFragmentInteractionListener {
    private final String TAG = "MainActivity";

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mActionTitles;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    /* Gesture Recognition Framework */
    public static String activeTrainingSet = "default";
    public static IGestureRecognitionService recognitionService;
    private final ServiceConnection serviceConnection = setupGestureConnection();
    private IBinder gestureListenerStub = setupGestureListener();

    private int mCurrFrag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActionTitles = getResources().getStringArray(R.array.actions_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new DrawerItemAdapter(MainActivity.this, new ArrayList<String>(Arrays.asList(mActionTitles))));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
            case R.id.action_websearch:
                // create intent to perform web search for this planet
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
                // catch event that there's no activity to handle intent
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onFragmentInteraction(int pos) {
        Log.i(TAG, "fragment interaction heard, change fragment to " + String.valueOf(pos));
        selectItem(pos);
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        mCurrFrag = position;
        Fragment fragment = null;

        Class fragmentClass;
        String action = mActionTitles[position];
        switch(action) {
            case "Home":
                Log.i(TAG, "Navbar --> Home pressed");
                fragmentClass = HomeFragment.class;
                break;
            case "Settings":
                Log.i(TAG, "Navbar --> Settings pressed");
                fragmentClass = SettingsFragment.class;
                break;
            case "Teach":
                Log.i(TAG, "Navbar --> Teach pressed");
                fragmentClass = TeachFragment.class;
                break;
            case "Play":
                Log.i(TAG, "Navbar --> Play pressed");
                fragmentClass = PlayFragment.class;
                break;
            default:
                fragmentClass = HomeFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(action);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause() called");
        if (recognitionService != null) {
            try {
                recognitionService.unregisterListener(IGestureRecognitionListener.Stub.asInterface(gestureListenerStub));
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        recognitionService = null;
        getApplicationContext().unbindService(serviceConnection);
        super.onPause();
    }

    @Override
    public void onResume() {
        Intent bindIntent = new Intent(this, GestureRecognitionService.class);
        getApplicationContext().bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        super.onResume();
    }

    private ServiceConnection setupGestureConnection() {
        return new ServiceConnection() {

            public void onServiceConnected(ComponentName className, IBinder service) {
                recognitionService = IGestureRecognitionService.Stub.asInterface(service);
                try {
                    recognitionService.startClassificationMode(activeTrainingSet);
                    recognitionService.registerListener(IGestureRecognitionListener.Stub.asInterface(gestureListenerStub));
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

    private IBinder setupGestureListener() {
        return new IGestureRecognitionListener.Stub() {

            @Override
            public void onGestureLearned(String gestureName) throws RemoteException {
                Toast.makeText(MainActivity.this, String.format("Gesture %s learned", gestureName), Toast.LENGTH_SHORT).show();
                Log.i(TAG, String.format("Gesture %s learned", gestureName));
            }

            @Override
            public void onTrainingSetDeleted(String trainingSet) throws RemoteException {
                Toast.makeText(MainActivity.this, String.format("Training set %s deleted", trainingSet), Toast.LENGTH_SHORT).show();
                Log.e(TAG, String.format("Training set %s deleted", trainingSet));
            }

            @Override
            public void onGestureRecognized(final Distribution distribution) throws RemoteException {
                if (mCurrFrag == 1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, String.format("%s: %f", distribution.getBestMatch(), distribution.getBestDistance()), Toast.LENGTH_LONG).show();
                            System.err.println(String.format("%s: %f", distribution.getBestMatch(), distribution.getBestDistance()));
                        }
                    });
                }
            }
        };
    }

}
