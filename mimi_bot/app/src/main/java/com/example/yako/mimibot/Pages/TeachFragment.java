package com.example.yako.mimibot.pages;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.yako.mimibot.MainActivity;
import com.example.yako.mimibot.R;
import com.example.yako.mimibot.adapters.EditTrainedGesturesAdapter;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TeachFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TeachFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TeachFragment extends Fragment implements EditTrainedGesturesAdapter.OnEditTrainedGestureListener {
    private final static String TAG = "TeachFragment";

    private final static String MIMI_TRAINING_SET = "Mimi Capable Gestures";
    private final static String CUSTOM_TRAINING_SET = "Custom Gestures";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String activeGesture;

    private OnFragmentInteractionListener mListener;

    private Button mStartTrainButton;
    private Spinner mTrainingSetSpin, mGestureSpin;
    private ListView mEditGestureList;
    private TextView mNoGesturesTxt;
    private EditText mEditGestureEdit;

    private ArrayAdapter editTrainedGesturesAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TeachFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TeachFragment newInstance(String param1, String param2) {
        TeachFragment fragment = new TeachFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public TeachFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        MainActivity.activeTrainingSet = MIMI_TRAINING_SET;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teach, container, false);
//        final TextView activeTrainingSetText = (TextView) view.findViewById(R.id.activeTrainingSet);
//        final EditText trainingSetText = (EditText) view.findViewById(R.id.trainingSetName);
//        final EditText gestureNameText = (EditText) view.findViewById(R.id.gestureName);
//        final Button deleteTrainingSetButton = (Button) view.findViewById(R.id.deleteTrainingSetButton);
//        final Button changeTrainingSetButton = (Button) view.findViewById(R.id.startNewSetButton);
//        final SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBar1);

        mStartTrainButton = (Button) view.findViewById(R.id.trainButton);
        mNoGesturesTxt = (TextView) view.findViewById(R.id.no_gestures_txt);
        mEditGestureList = (ListView) view.findViewById(R.id.trained_gestures_list);
        mEditGestureEdit = (EditText) view.findViewById(R.id.gesture_edit);

        mTrainingSetSpin = (Spinner) view.findViewById(R.id.training_set_spin);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.training_sets_array, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTrainingSetSpin.setAdapter(adapter);
        MainActivity.activeTrainingSet = mTrainingSetSpin.getSelectedItem().toString();
        mTrainingSetSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String trainingSetName = parent.getItemAtPosition(position).toString();
                MainActivity.activeTrainingSet = trainingSetName;

                if (MainActivity.recognitionService != null) {
                    try {
                        MainActivity.recognitionService.startClassificationMode(MainActivity.activeTrainingSet);
                        if (trainingSetName.equals(CUSTOM_TRAINING_SET)) {
                            mGestureSpin.setVisibility(View.GONE);
                            mEditGestureEdit.setVisibility(View.VISIBLE);
                        } else {
                            mEditGestureEdit.setVisibility(View.GONE);
                            mGestureSpin.setVisibility(View.VISIBLE);
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                populateEditGestureList(view);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (MainActivity.recognitionService != null) {
            try {
                MainActivity.recognitionService.startClassificationMode(MainActivity.activeTrainingSet);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        populateEditGestureList(view);

        mGestureSpin = (Spinner) view.findViewById(R.id.gesture_spin);
        adapter = ArrayAdapter.createFromResource(getActivity(), R.array.mimi_capable_gestures_array, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGestureSpin.setAdapter(adapter);

        mStartTrainButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "Start Training Pushed");
                if (MainActivity.recognitionService != null) {
                    try {
                        if (!MainActivity.recognitionService.isLearning()) {
                            Log.i(TAG, "Started Training");
                            mStartTrainButton.setText("Stop Training");
//                            gestureNameText.setEnabled(false);
//                            deleteTrainingSetButton.setEnabled(false);
//                            changeTrainingSetButton.setEnabled(false);
//                            trainingSetText.setEnabled(false);
                            if (mTrainingSetSpin.getSelectedItem().toString().equals(CUSTOM_TRAINING_SET)) {
                                activeGesture = mEditGestureEdit.getText().toString();
                                View view = getActivity().getCurrentFocus();
                                if (view != null) {
                                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                }
                            } else {
                                activeGesture = mGestureSpin.getSelectedItem().toString();
                            }
                            MainActivity.recognitionService.startLearnMode(MainActivity.activeTrainingSet, activeGesture);

                        } else {
                            Log.i(TAG, "Stopped Training");
                            mStartTrainButton.setText("Start Training");
                            populateEditGestureList(v);
//                            gestureNameText.setEnabled(true);
//                            deleteTrainingSetButton.setEnabled(true);
//                            changeTrainingSetButton.setEnabled(true);
//                            trainingSetText.setEnabled(true);
                            MainActivity.recognitionService.stopLearnMode();
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

//        seekBar.setVisibility(View.INVISIBLE);
//        seekBar.setMax(20);
//        seekBar.setProgress(20);
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//
//                try {
//                    MainActivity.recognitionService.setThreshold(progress / 10.0f);
//                } catch (RemoteException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//
//            }
//        });

//        changeTrainingSetButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                MainActivity.activeTrainingSet = trainingSetText.getText().toString();
//                activeTrainingSetText.setText(MainActivity.activeTrainingSet);
//
//                if (MainActivity.recognitionService != null) {
//                    try {
//                        MainActivity.recognitionService.startClassificationMode(MainActivity.activeTrainingSet);
//                    } catch (RemoteException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });

//        deleteTrainingSetButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                builder.setMessage("You really want to delete the training set?").setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        if (MainActivity.recognitionService != null) {
//                            try {
//                                MainActivity.recognitionService.deleteTrainingSet(MainActivity.activeTrainingSet);
//                            } catch (RemoteException e) {
//                                // TODO Auto-generated catch block
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
//                builder.create().show();
//            }
//        });

        return view;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.edit_gestures:
//                Intent editGesturesIntent = new Intent().setClass(getActivity(), GestureOverview.class);
//                editGesturesIntent.setPackage("de.dfki.ccaal.gestures");
//                editGesturesIntent.putExtra("trainingSetName", MainActivity.activeTrainingSet);
//                startActivity(editGesturesIntent);
//                return true;
//
//            default:
//                return false;
//        }
//    }
//    // TODO: Rename method, update argument and hook method into UI event
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

        if (MainActivity.recognitionService != null) {
            try {
                if (MainActivity.recognitionService.isLearning()) {
                    Log.i(TAG, "Stopping learn mode.");
                    MainActivity.recognitionService.stopLearnMode();
                }
                if (MainActivity.recognitionService.isClassifying()) {
                    Log.i(TAG, "Stopping classification mode.");
                    MainActivity.recognitionService.stopClassificationMode();
                }
            } catch (RemoteException e) {
                Log.e(TAG, "Something went wrong when trying to stop learn mode after fragment detach");
            }
        }
    }

    /* HELPERS */
    private void populateEditGestureList(View view) {
        Log.i(TAG, "populateEditGestureList()");

        try {
            List<String> items = MainActivity.recognitionService.getGestureList(MainActivity.activeTrainingSet);
            if (items.size() > 0) {
                editTrainedGesturesAdapter = new EditTrainedGesturesAdapter(getActivity(), items, this, view);
                mEditGestureList.setAdapter(editTrainedGesturesAdapter);
                mNoGesturesTxt.setVisibility(View.GONE);
                mEditGestureList.setVisibility(View.VISIBLE);
                mEditGestureList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.i(TAG, parent.getSelectedItem().toString() + "was pushed");

                        try {
                            MainActivity.recognitionService.deleteGesture(MainActivity.activeTrainingSet, parent.getSelectedItem().toString());
                            populateEditGestureList(view);
                        } catch (RemoteException e) {
                            Log.e(TAG, "Something went wrong when trying to delete a gesture");
                            e.printStackTrace();
                        }
                    }
                });
                Log.i(TAG, "gestureList = " + items.toString());
            } else {
                mEditGestureList.setVisibility(View.GONE);
                mNoGesturesTxt.setVisibility(View.VISIBLE);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Error getting gesture list");
            e.printStackTrace();
        }
    }

    @Override
    public void onEditTrainedGestureInteraction(View v) {
        populateEditGestureList(v);
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
