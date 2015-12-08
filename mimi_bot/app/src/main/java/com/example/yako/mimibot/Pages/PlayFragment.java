package com.example.yako.mimibot.pages;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.yako.mimibot.MainActivity;
import com.example.yako.mimibot.R;
import com.example.yako.mimibot.SshManager;
import com.example.yako.mimibot.adapters.TrainedGesturesAdapter;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlayFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayFragment extends Fragment {
    private static final String TAG = "PlayFragment";

    private static final String REMOTE_DICONNECTED_BTN_TXT = "REMOTE Control Mimi\n(must be connected)";

    private static final String GESTURE_CTRL_DISCONNECTED_BTN_TXT = "GESTURE Control Mimi\n(must be connected)";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String trainingSet;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TrainedGesturesAdapter trainedGesturesAdapter;

    private OnFragmentInteractionListener mListener;

    private Button mGestureCtrlBtn, mRemoteCtrlBtn, mTestCustomBtn;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlayFragment newInstance(String param1, String param2) {
        PlayFragment fragment = new PlayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public PlayFragment() {
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
        Log.i(TAG, "onCreateView()");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_play, container, false);

        try {
            trainingSet = MainActivity.activeTrainingSet;
            Log.i(TAG, "trainingSet = " + trainingSet);
            List<String> items = MainActivity.recognitionService.getGestureList(trainingSet);
            trainedGesturesAdapter = new TrainedGesturesAdapter(getActivity(), items);
            Log.i(TAG, "gestureList = " + items.toString());
        } catch (RemoteException e) {
            Log.e(TAG, "Error getting gesture list");
            e.printStackTrace();
        }

        mGestureCtrlBtn = (Button) view.findViewById(R.id.gesture_ctrl_btn);
        mRemoteCtrlBtn = (Button) view.findViewById(R.id.remote_ctrl_btn);
        mTestCustomBtn = (Button) view.findViewById(R.id.test_gesture_btn);

        if (SshManager.connectionStatus.toInt() != 2) {
            mGestureCtrlBtn.append("\n(must be connected)");
            mGestureCtrlBtn.setEnabled(false);

            mRemoteCtrlBtn.append("\n(must be connect)");
            mRemoteCtrlBtn.setEnabled(false);
        }

        mGestureCtrlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onFragmentInteraction(5);
                }
            }
        });

        mRemoteCtrlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onFragmentInteraction(6);
                }
            }
        });

        mTestCustomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onFragmentInteraction(7);
                }
            }
        });

//        mMotion1 = (Button) view.findViewById(R.id.motion_1_btn);
//        mMotion2 = (Button) view.findViewById(R.id.motion_2_btn);
//        mMotion3 = (Button) view.findViewById(R.id.motion_3_btn);
//        mMotion4 = (Button) view.findViewById(R.id.motion_4_btn);
//
//        mMotion1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SshManager.sendCommand("7");
//                SshManager.sendCommand("rosrun example_robot_interface test_abby_senderm1");
//            }
//        });
//        mMotion2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SshManager.sendCommand("7");
//                SshManager.sendCommand("rosrun example_robot_interface test_abby_senderm2");
//            }
//        });
//        mMotion3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SshManager.sendCommand("7");
//                SshManager.sendCommand("rosrun example_robot_interface test_abby_senderm3");
//            }
//        });
//        mMotion4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SshManager.sendCommand("rosrun example_robot_interface test_abby_senderm4");
//            }
//        });


        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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

}