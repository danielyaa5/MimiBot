package com.example.yako.mimibot.pages;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.yako.mimibot.MainActivity;
import com.example.yako.mimibot.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CustomGestureFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CustomGestureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomGestureFragment extends Fragment {
    public static final String TAG = "CustomGestureFrag";

    private final static String CUSTOM_TRAINING_SET = "Custom Gestures";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Button mTeachBtn;
    private LinearLayout mNoGesturesLL, mNowListeningLL;
    private ListView mCommandHistoryList;

    private static ArrayAdapter<String> mCommandHistoryAdapter = null;

    private static List<String> commandHistory;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CustomGestureFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomGestureFragment newInstance(String param1, String param2) {
        CustomGestureFragment fragment = new CustomGestureFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public CustomGestureFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        MainActivity.activeTrainingSet = CUSTOM_TRAINING_SET;
        try {
            MainActivity.recognitionService.startClassificationMode(MainActivity.activeTrainingSet);
        } catch (RemoteException e) {
            Log.e(TAG, "Something went wrong while trying to startClassificationMode");
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_custom_gesture, container, false);

        mTeachBtn = (Button) view.findViewById(R.id.cg_teach_btn);
        mNoGesturesLL = (LinearLayout) view.findViewById(R.id.cg_no_gestures_ll);
        mNowListeningLL = (LinearLayout) view.findViewById(R.id.now_listening_ll);
        mCommandHistoryList = (ListView) view.findViewById(R.id.command_history_list);

        List<String> gestures = null;
        try {
            gestures = MainActivity.recognitionService.getGestureList(CUSTOM_TRAINING_SET);
        } catch (RemoteException e) {
            Log.e(TAG, "Something went wrong while trying to retreive the gesture list");
        }

        if (gestures == null || gestures.size() < 1) {
            mNoGesturesLL.setVisibility(View.VISIBLE);
            mTeachBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onFragmentInteraction(2);
                    }
                }
            });
        } else {
            mNowListeningLL.setVisibility(View.VISIBLE);
        }

        mCommandHistoryAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, commandHistory);
        mCommandHistoryList.setAdapter(mCommandHistoryAdapter);

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

        commandHistory = new ArrayList<String>();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public static void addCommand(String command) {
        commandHistory.add(0, command);
        mCommandHistoryAdapter.notifyDataSetChanged();
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
