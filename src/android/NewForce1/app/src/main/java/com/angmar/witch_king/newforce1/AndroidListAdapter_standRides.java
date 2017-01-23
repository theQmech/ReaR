package com.angmar.witch_king.newforce1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by amey on 22/11/16.
 */

public class AndroidListAdapter_standRides extends ArrayAdapter {
        String[] model;
        String[] color;
        String[] code;
        Integer[] imagesId;
        Context context;
        String[] rideId;

        public AndroidListAdapter_standRides(Activity context, Integer[] imagesId, String[] model, String[] color, String[] rideId, String[] code) {
                super(context, R.layout.stand_rides_custom_list_layout, model);
                this.model = model;
                this.color = color;
                this.imagesId = imagesId;
                this.context = context;
                this.rideId = rideId;
                this.code = code;
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
                TextView mCodeView = (TextView) viewRow.findViewById(R.id.code_view);
                mCodeView.setText(code[i]);
                viewRow.setTag(rideId[i]);
                return viewRow;
        }
}
