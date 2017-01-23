package com.angmar.witch_king.newforce1;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.vision.text.Line;

import org.w3c.dom.Text;

public class AndroidListAdapter_unrentRides extends ArrayAdapter {
    String[] model;
    String[] color;
    Integer[] imagesId;
    Context context;
    String[] rideId;

    public AndroidListAdapter_unrentRides(Activity context, Integer[] imagesId, String[] model, String[] color, String[] rideId) {
        super(context, R.layout.stand_rides_custom_list_layout, model);
        this.model = model;
        this.color = color;
        this.imagesId = imagesId;
        this.context = context;
        this.rideId = rideId;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewRow = layoutInflater.inflate(R.layout.stand_rides_custom_list_layout, null,
                true);
        TextView mModelView = (TextView) viewRow.findViewById(R.id.model_view);
        ImageView mimageView = (ImageView) viewRow.findViewById(R.id.image_view);
        mModelView.setText(model[i]);
        mimageView.setImageResource(imagesId[i]);
        TextView mColorView = (TextView) viewRow.findViewById(R.id.color_view);
        mColorView.setText(color[i]);
//        TextView mCow = (TextView) viewRow.findViewById(R.id.code_view);
//        mCow.setText(null);
//        TextView mc = (TextView) viewRow.findViewById(R.id.code1);
//        mc.setText(null);
        LinearLayout lin = (LinearLayout) viewRow.findViewById(R.id.codea);
        if (lin !=null){
            lin.removeAllViews();
        }
        viewRow.setTag(rideId[i]);
        return viewRow;
    }
}
