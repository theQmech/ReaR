package com.angmar.witch_king.newforce1;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by witch-king on 23/11/16.
 */

public class AndroidListAdapterHistory extends ArrayAdapter {
    String[] model;
    String[] color;
    String[] fromTime;
    String[] toTime;
    String[] fromStand;
    String[] toStand;
    Context context;

    public AndroidListAdapterHistory(Activity context, String[] model, String[] color, String[] fromStand, String[] fromTime, String[] toStand, String[] toTime) {
        super(context, R.layout.stand_rides_custom_list_layout, model);
        this.model = model;
        this.color = color;
        this.context = context;
        this.fromStand = fromStand;
        this.fromTime = fromTime;
        this.toStand = toStand;
        this.toTime = toTime;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewRow = layoutInflater.inflate(R.layout.history_custom_list_layout, null,
                true);
        TextView mModelView = (TextView) viewRow.findViewById(R.id.model_view);
        mModelView.setText(model[i]);

        TextView mColorView = (TextView) viewRow.findViewById(R.id.color_view);
        mColorView.setText(color[i]);

        TextView fromstand = (TextView) viewRow.findViewById(R.id.fromStand);
        fromstand.setText(fromStand[i]);

        TextView tostand = (TextView) viewRow.findViewById(R.id.toStand);
        if (toStand[i].contains("null")){
            tostand.setText("Not unrented");
        }
        else {
            tostand.setText(toStand[i]);
        }

        TextView fromtime = (TextView) viewRow.findViewById(R.id.fromTime);
        fromtime.setText(fromTime[i]);

        TextView totime = (TextView) viewRow.findViewById(R.id.toTime);
        if (toTime[i].contains("null")){
            totime.setText("");
        }
        else {
            totime.setText(toTime[i]);
        }
//        TextView mCow = (TextView) viewRow.findViewById(R.id.code_view);
//        mCow.setText(null);
//        TextView mc = (TextView) viewRow.findViewById(R.id.code1);
//        mc.setText(null);
        LinearLayout lin = (LinearLayout) viewRow.findViewById(R.id.codea);
        if (lin !=null){
            lin.removeAllViews();
        }
        return viewRow;
    }
}
