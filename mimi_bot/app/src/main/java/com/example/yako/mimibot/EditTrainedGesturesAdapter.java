package com.example.yako.mimibot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

/**
 * Created by yako on 12/6/15.
 */
public class EditTrainedGesturesAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> values;

    public EditTrainedGesturesAdapter (Context context, List<String> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.edit_trained_gesture_item, parent, false);

        TextView editTrainedGestureItemTV = (TextView) rowView.findViewById(R.id.edit_gesture_item_tv);

        String s = values.get(position);
        editTrainedGestureItemTV.setText(s);

        return rowView;
    }

}
