package com.example.yako.mimibot.pages;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.yako.mimibot.R;
import com.example.yako.mimibot.SshConnectResponse;
import com.example.yako.mimibot.SshManager;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String PREFS_NAME = "MyPrefsFile";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "HomeFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Button mConnectBtn, mYesBtn, mSettingsBtn, mTeachBtn, mPlayBtn;

    private LinearLayout mSetupLL, mDefaultLL;

    private ProgressBar mConnectProgress;

    private Activity mActivity;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mConnectProgress = (ProgressBar) view.findViewById(R.id.connect_progress);
        mConnectBtn = (Button) view.findViewById(R.id.connect_btn);

        switch (SshManager.connectionStatus.toInt()) {
            case 0:
                mConnectBtn.setText("Connect");
                mConnectBtn.setVisibility(View.VISIBLE);
                mConnectProgress.setVisibility(View.INVISIBLE);
                break;
            case 1:
                mConnectBtn.setVisibility(View.INVISIBLE);
                mConnectProgress.setVisibility(View.VISIBLE);
                break;
            case 2:
                mConnectProgress.setVisibility(View.INVISIBLE);
                mConnectBtn.setText("Disconnect");
                mConnectBtn.setVisibility(View.VISIBLE);
                break;
            case 3:
                mConnectProgress.setVisibility(View.INVISIBLE);
                mConnectBtn.setText("Try Again");
                mConnectBtn.setVisibility(View.VISIBLE);
                break;
            default:
                mConnectBtn.setText("Connect");
                mConnectBtn.setVisibility(View.VISIBLE);
                mConnectProgress.setVisibility(View.INVISIBLE);
        }

        mConnectBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (SshManager.connectionStatus.toInt() == 2) {
                    Log.i(TAG, "Disconnect Btn Pushed");
                } else {
                    SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
                    Log.i(TAG, "Connect Btn Pushed");
                    mConnectBtn.setVisibility(View.GONE);
                    mConnectProgress.setVisibility(View.VISIBLE);

                    SshManager.attemptConnection(settings.getString("username", ""), settings.getString("password", "")
                            , settings.getString("hostname", ""), settings.getInt("port", -1), onSshConnectResponse());
                }
            }
        });

        mYesBtn = (Button) view.findViewById(R.id.yes_btn);
        mYesBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "Yes Btn Pushed");
                if (mListener != null) {
                    mListener.onFragmentInteraction(4);
                }
            }
        });

        mSettingsBtn = (Button) view.findViewById(R.id.settings_btn);
        mSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onFragmentInteraction(4);
                }
            }
        });

        mTeachBtn = (Button) view.findViewById(R.id.teach_btn);
        mTeachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onFragmentInteraction(2);
                }
            }
        });

        mPlayBtn = (Button) view.findViewById(R.id.play_btn);
        mPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onFragmentInteraction(1);
                }
            }
        });

        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        boolean settingsHostname = settings.getString("hostname", "").length() > 0;
        boolean settingsUsername = settings.getString("username", "").length() > 0;
        boolean settingsPassword = settings.getString("password", "").length() > 0;
        boolean settingsPort = settings.getInt("port", -1) > -1;

        mSetupLL = (LinearLayout) view.findViewById(R.id.setup_ll);
        mDefaultLL = (LinearLayout) view.findViewById(R.id.default_home_ll);

        if (settingsHostname && settingsUsername && settingsPassword && settingsPort) {
            Log.i(TAG, "Opening default home");
            mDefaultLL.setVisibility(View.VISIBLE);
        } else {
            Log.i(TAG, "Opening setup home");
            mSetupLL.setVisibility(View.VISIBLE);
        }

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event

//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(int pos);

    }

    public SshConnectResponse onSshConnectResponse() {
        return new SshConnectResponse() {
            @Override
            public void sshConnectCb() {
                mConnectProgress.setVisibility(View.GONE);

                if (SshManager.connectionStatus.toInt() == 2) {
                    mConnectBtn.setText("Disconnect");
                    Toast.makeText(mActivity, "CONNECTED to MimiBot!", Toast.LENGTH_SHORT).show();
                } else {
                    mConnectBtn.setText("Try Again");
                    Toast.makeText(mActivity, "UNABLE TO CONNECT to MimiBot!", Toast.LENGTH_SHORT).show();
                }
                mConnectBtn.setVisibility(View.VISIBLE);
            }
        };
    }

}
