package com.example.yako.mimibot;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    public static final String TAG = "SettingsFragment";
    public static final String PREFS_NAME = "MyPrefsFile";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button mFinishedBtn;

    private EditText mHostnameEt, mUsernameEt, mPasswordEt, mPortEt;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SettingsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);

        mHostnameEt = (EditText) view.findViewById(R.id.hostname_et);
        mUsernameEt = (EditText) view.findViewById(R.id.username_et);
        mPasswordEt = (EditText) view.findViewById(R.id.password_et);
        mPortEt = (EditText) view.findViewById(R.id.port_et);

        mFinishedBtn = (Button) view.findViewById(R.id.finished_btn);
        mFinishedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences _settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = _settings.edit();

                if (mPortEt.getText().toString().trim().length() > 0 && mHostnameEt.getText().toString().trim().length() > 3 && mUsernameEt.getText().toString().trim().length() > 0 && mPasswordEt.getText().toString().trim().length() > 0) {
                    Log.i(TAG, "Saving prefrences");
                    editor.putString("hostname", mHostnameEt.getText().toString());
                    editor.putString("username", mUsernameEt.getText().toString());
                    editor.putString("password", mPasswordEt.getText().toString());
                    editor.putInt("port", Integer.parseInt(mPortEt.getText().toString()));
                    editor.commit();

                    if (mListener != null) {
                        mListener.onFragmentInteraction(0);
                    }
                }
            }
        });


        String hostname = settings.getString("hostname", "");
        Log.i(TAG, "loaded hostname:" + hostname);
        String username = settings.getString("username", "");
        String password = settings.getString("password", "");
        int port = settings.getInt("port", -1);

        if(hostname.length() > 0) {
            mHostnameEt.setText(hostname);
        }
        if(username.length() > 0) {
            mUsernameEt.setText(username);
        }
        if(password.length() > 0) {
            mPasswordEt.setText(password);
        }
        if(port > -1) {
            mPortEt.setText(String.valueOf(port));
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
