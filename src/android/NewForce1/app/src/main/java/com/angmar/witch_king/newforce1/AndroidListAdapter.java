package com.angmar.witch_king.newforce1;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.security.acl.Owner;

/**
 * Created by amey on 21/11/16.
 */

//public class AndroidListAdapter extends ArrayAdapter {
//    String[] model;
//    String[] color;
//    String[] rideStatus;
//    Integer[] imagesId;
//    Context context;
//    String[] rideId;
//    public static String s0 = "Safely Home";
//    public static String s1 = "Lend Requested";
//    public static String s2 = "Lent";
//    public static String s3 = "Unlend Requested";
//    public static String b0 = "Lend";
//    public static String b1 = "Cancel Request";
//    public static String b2 = "Unlend";
//    public static String b3 = "Cancel Request";
//
//    public AndroidListAdapter(Activity context, Integer[] imagesId, String[] model, String[] color, String[] rideStatus, String[] rideId) {
//        super(context, R.layout.custom_list_item_layout, model);
//        this.model = model;
//        this.color = color;
//        this.rideStatus = rideStatus;
//        this.imagesId = imagesId;
//        this.context = context;
//        this.rideId = rideId;
//    }
//
//    @Override
//    public View getView(int i, View view, ViewGroup viewGroup) {
//        LayoutInflater layoutInflater = (LayoutInflater) context
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View viewRow = layoutInflater.inflate(R.layout.owned_rides_custom_list_layout, null,
//                true);
//        TextView mModelView = (TextView) viewRow.findViewById(R.id.model_view);
//        ImageView mimageView = (ImageView) viewRow.findViewById(R.id.image_view);
//        mModelView.setText(model[i]);
//        mimageView.setImageResource(imagesId[i]);
//        TextView mColorView = (TextView) viewRow.findViewById(R.id.color_view);
//        mColorView.setText(color[i]);
//        TextView mStatusView = (TextView) viewRow.findViewById(R.id.status_view);
//        Button mRideStatus = (Button) viewRow.findViewById(R.id.ride_status);
//        mRideStatus.setTag(rideId[i]);
//        switch (Integer.parseInt(rideStatus[i])){
//
//            case 0 : mStatusView.setText(s0);
//                    mRideStatus.setText(b0);
//                    break;
//            case 1 : mStatusView.setText(s1);
//                mRideStatus.setText(b1);
//                break;
//            case 2 : mStatusView.setText(s2);
//                mRideStatus.setText(b2);
//                break;
//            case 3 : mStatusView.setText(s3);
//                mRideStatus.setText(b3);
//                break;
//        }
//        mRideStatus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final String status = ((TextView) ((LinearLayout)v.getParent()).findViewById(R.id.status_view)).getText().toString();
//                Log.e("Status",status);
//                Button b = (Button) v;
//                Integer temp = 0;
//                String Op = null;
//                String OpType = null;
//                if(status.equals(AndroidListAdapter.s0)) {
//                    Op = "Lend";
//                    OpType = "Request";
//                }
//                else if (status.equals(AndroidListAdapter.s1)) {
//                    Op = "Lend";
//                    OpType = "Cancel";
//                }
//                else if (status.equals(AndroidListAdapter.s2)) {
//                    Op = "Unlend";
//                    OpType = "Request";
//                }
//                else if (status.equals(AndroidListAdapter.s3)) {
//                    Op = "Unlend";
//                    OpType = "Cancel";
//                }
////                OwnedRidesActivity.UpdateStatusTask mUpdateTask = new OwnedRidesActivity.UpdateStatusTask(Op, OpType, (String)v.getTag(),v);
////                mUpdateTask.execute();
//            }
//        });
////        mStatusView.setText(rideStatus[i]);
//        return viewRow;
//    }
//}
