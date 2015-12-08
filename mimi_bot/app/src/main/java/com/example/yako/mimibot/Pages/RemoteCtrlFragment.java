package com.example.yako.mimibot.pages;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.yako.mimibot.R;
import com.example.yako.mimibot.SshManager;

import java.lang.reflect.Field;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RemoteCtrlFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RemoteCtrlFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RemoteCtrlFragment extends Fragment implements SshManager.OnStdInReceivedListener{
    private static final String TAG = "RemoteCtrlFragment";

    private final String PREFS_NAME = "MyPrefsFile";


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private LinearLayout mRemoteGestureBtnsLL, mInnerTerminalLL, mTerminalLL;
    private ScrollView mTerminalScroll;

    private SharedPreferences settings;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RemoteCtrlFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RemoteCtrlFragment newInstance(String param1, String param2) {
        RemoteCtrlFragment fragment = new RemoteCtrlFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public RemoteCtrlFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        SshManager.setOnStdReceivedListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_remote_ctrl, container, false);

        mRemoteGestureBtnsLL = (LinearLayout) view.findViewById(R.id.remote_gesture_btns_ll);
        mInnerTerminalLL = (LinearLayout) view.findViewById(R.id.inner_terminal_ll);
        mTerminalScroll = (ScrollView) view.findViewById(R.id.terminal_scroll);
        mTerminalLL = (LinearLayout) view.findViewById(R.id.terminal_ll);

        String[] mimiGestures = getActivity().getResources().getStringArray(R.array.mimi_capable_gestures_array);

        LinearLayout mimiBtnsLL = null;
        Button mimiBtn;
        for (int i=0; i < mimiGestures.length; i++) {
            if (i%2 == 0) {
                mimiBtnsLL = new LinearLayout(getActivity());
                mimiBtnsLL.setOrientation(LinearLayout.HORIZONTAL);
                mimiBtnsLL.setGravity(Gravity.CENTER_HORIZONTAL);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER_HORIZONTAL;
                mimiBtnsLL.setLayoutParams(params);
            }

            mimiBtn = new Button(getActivity());
            mimiBtn.setText(mimiGestures[i]);
            mimiBtn.setHeight(125);
            mimiBtn.setWidth(250);
            mimiBtn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            mimiBtn.setId(i);
            final int j = i;
            mimiBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "Button " + String.valueOf(j) + " pressed");
                    switch (j) {
                        case 0:
                            SshManager.sendCommand("rosrun example_robot_interface test_abby_senderm1");
                            break;
                        case 2:
                            SshManager.sendCommand("rosrun example_robot_interface test_abby_senderm2");
                            break;
                        case 3:
                            SshManager.sendCommand("rosrun example_robot_interface test_abby_senderm3");
                            break;
                        case 1:
                            SshManager.sendCommand("rosrun example_robot_interface test_abby_senderm3");
                            break;
                        default:
                            Log.i(TAG, "unrecognized button");
                    }
                }
            });

            mimiBtnsLL.addView(mimiBtn);

            if (i%2 == 1) {
                mRemoteGestureBtnsLL.addView(mimiBtnsLL);
            }
        }

        promptCommand();

        mInnerTerminalLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Pressed inside inner terminal LL");
                EditText editText = (EditText) mInnerTerminalLL.getChildAt(mInnerTerminalLL.getChildCount() - 1);
                editText.setFocusableInTouchMode(true);
                editText.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);

            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        SshManager.removeStdReceivedListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        SshManager.removeStdReceivedListener();
    }

    public void promptCommand() {
        LinearLayout terminalLL = new LinearLayout(getActivity());

        String hostname = settings.getString("hostname", "");
        String username = settings.getString("username", "");
        final String user = username + "@" + hostname + ": ";

        TextView user_text = new TextView(getActivity());
        user_text.setText(user);
        user_text.setTypeface(Typeface.DEFAULT_BOLD);
        user_text.setTextColor(getResources().getColor(R.color.terminal_green));
        user_text.setClickable(false);

        mInnerTerminalLL.addView(user_text);

        final EditText input = new EditText(getActivity());
        input.setBackgroundColor(Color.TRANSPARENT);
        input.setTextColor(getResources().getColor(R.color.terminal_green));
        input.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        input.setTextSize(14);
        input.setTypeface(Typeface.DEFAULT_BOLD);
        input.setClickable(false);
        input.setCursorVisible(true);
        try {
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(input, R.drawable.terminal_cursor);
        } catch (Exception ignored) {
        }

        input.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER) &&
                        input.getText().toString().trim().length() > 0) {
                    // Perform action on key press
                    sendCommand(user, input.getText().toString());
                    input.setText("");
                    return true;
                }
                return false;
            }
        });

        mInnerTerminalLL.addView(input);
    }

    private void sendCommand(String user, String command) {
        SshManager.sendCommand(command);

        TextView tv = new TextView(getActivity());
        tv.setText(user+command);
        tv.setTextColor(getResources().getColor(R.color.terminal_green));
        tv.setTextSize(14);
        tv.setTypeface(Typeface.DEFAULT_BOLD);

        mInnerTerminalLL.addView(tv, 0);
    }

    @Override
    public void onStdInReceived(final String stdIn) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                TextView tv = new TextView(getActivity());
                tv.setText(stdIn);
                tv.setTextColor(getResources().getColor(R.color.terminal_green));
                tv.setTextSize(14);
                tv.setTypeface(Typeface.DEFAULT_BOLD);
                mInnerTerminalLL.addView(tv, mInnerTerminalLL.getChildCount() - 2);
                mTerminalScroll.fullScroll(View.FOCUS_DOWN);
            }
        });

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
        public void onFragmentInteraction(int pos);
    }

}
