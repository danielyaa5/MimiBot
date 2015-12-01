package com.example.yako.mimibot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by yako on 12/1/15.
 */
public class DrawerItemAdapter  extends ArrayAdapter<String> {
    private Context context;
    private ArrayList<String> TextValue = new ArrayList<String>();

    public DrawerItemAdapter(Context context, ArrayList<String> TextValue) {
        super(context, R.layout.drawer_list_item, TextValue);
        this.context = context;
        this.TextValue= TextValue;

    }


    @Override
    public View getView(int position, View coverView, ViewGroup parent) {
        // TODO Auto-generated method stub

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.drawer_list_item,
                parent, false);

        TextView drawer_txt = (TextView)rowView.findViewById(R.id.drawer_item_txt);
        drawer_txt.setText(TextValue.get(position));

        ImageView drawer_ic = (ImageView) rowView.findViewById(R.id.drawer_item_ic);
        if (position == 0) drawer_ic.setImageResource(R.drawable.ic_home_white_36dp);
        else if (position == 1) drawer_ic.setImageResource(R.drawable.ic_videogame_asset_white_36dp);
        else if (position == 2) drawer_ic.setImageResource(R.drawable.ic_account_balance_white_36dp);
        else if (position == 3) drawer_ic.setImageResource(R.drawable.ic_wifi_tethering_white_36dp);
        else drawer_ic.setImageResource(R.drawable.ic_settings_applications_white_36dp);

        return rowView;

    }

}