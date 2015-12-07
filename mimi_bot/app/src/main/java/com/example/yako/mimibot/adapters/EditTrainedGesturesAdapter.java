package com.example.yako.mimibot.adapters;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.yako.mimibot.MainActivity;
import com.example.yako.mimibot.R;

import java.util.List;

/**
 * Created by yako on 12/6/15.
 */
public class EditTrainedGesturesAdapter extends ArrayAdapter<String> {
    private static final String TAG = "EditTrainedGestAdapter";

    private final Context context;
    private final List<String> values;

    private OnEditTrainedGestureListener mListener;

    public EditTrainedGesturesAdapter(Context context, List<String> values, OnEditTrainedGestureListener listener, View v) {
        super(context, -1, values);
        this.context = context;
        this.values = values;

        mListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.edit_trained_gesture_item, parent, false);

        TextView editTrainedGestureItemTV = (TextView) rowView.findViewById(R.id.edit_gesture_item_tv);

        final String s = values.get(position);
        editTrainedGestureItemTV.setText(s);

        ImageButton deleteImgBtn = (ImageButton) rowView.findViewById(R.id.delete_gesture_item_btn);
        deleteImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MainActivity.recognitionService.deleteGesture(MainActivity.activeTrainingSet, s);

                    if (mListener != null) {
                        mListener.onEditTrainedGestureInteraction(v);
                    }
                } catch (RemoteException e) {
                    Log.e(TAG, "Something went wrong when trying to delete a gesture");
                    e.printStackTrace();
                }
            }
        });

        return rowView;
    }

    public interface OnEditTrainedGestureListener {
        public void onEditTrainedGestureInteraction(View v);
    }

}
