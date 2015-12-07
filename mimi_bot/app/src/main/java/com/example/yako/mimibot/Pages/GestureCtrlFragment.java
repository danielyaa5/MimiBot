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
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.yako.mimibot.MainActivity;
import com.example.yako.mimibot.R;
import com.example.yako.mimibot.adapters.TrainedGesturesAdapter;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GestureCtrlFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GestureCtrlFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GestureCtrlFragment extends Fragment {
    private static final String MIMI_TRAINING_SET = "Mimi Capable Gestures";
    private static final String TAG = "GestureCtrlFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private LinearLayout mNoGesturesLL, mGestureCtrlsLL;
    private ListView mGestureCtrlsList;
    private Button mTeachBtn;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GestureCtrlFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GestureCtrlFragment newInstance(String param1, String param2) {
        GestureCtrlFragment fragment = new GestureCtrlFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public GestureCtrlFragment() {
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
        View view = inflater.inflate(R.layout.fragment_gesture_control, container, false);

        mGestureCtrlsLL = (LinearLayout) view.findViewById(R.id.gesture_ctrls_ll);
        mNoGesturesLL = (LinearLayout) view.findViewById(R.id.no_gestures_ll);
        mGestureCtrlsList = (ListView) view.findViewById(R.id.gesture_ctrls_list);
        mTeachBtn = (Button) view.findViewById(R.id.gc_teach_btn);

        List<String> items = null;
        try {
            items = MainActivity.recognitionService.getGestureList(MIMI_TRAINING_SET);
        } catch (RemoteException e) {
            Log.e(TAG, "Something went wrong when getting gesture list");
            e.printStackTrace();
        }

        if (items != null && items.size() > 0) {
            TrainedGesturesAdapter adapter = new TrainedGesturesAdapter(getActivity(), items);
            mGestureCtrlsList.setAdapter(adapter);
            mGestureCtrlsLL.setVisibility(View.VISIBLE);
        } else {
            mNoGesturesLL.setVisibility(View.VISIBLE);
            mTeachBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onFragmentInteraction(2);
                    }
                }
            });
        }

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
