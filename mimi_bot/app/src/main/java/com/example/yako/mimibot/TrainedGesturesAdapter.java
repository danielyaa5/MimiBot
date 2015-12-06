package com.example.yako.mimibot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;

import java.util.List;

/**
 * Created by yako on 12/5/15.
 */
public class TrainedGesturesAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> values;

    public TrainedGesturesAdapter (Context context, List<String> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.trained_gesture_item, parent, false);

        Switch playGestureSwitch = (Switch) rowView.findViewById(R.id.play_gesture_switch);

        String s = values.get(position);
        playGestureSwitch.setText(s);

        return rowView;
    }

}