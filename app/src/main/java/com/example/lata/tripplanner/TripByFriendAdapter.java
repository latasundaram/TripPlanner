package com.example.lata.tripplanner;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vinutna on 23-04-2017.
 */

public class TripByFriendAdapter extends ArrayAdapter<Trip>{
    int mResource;
    Context mContext;
    List<Trip> mData;
    IData activity;
    FirebaseUser fUser;
    ProgressBar pb;
    AlertDialog.Builder alert;

    public TripByFriendAdapter(Context context, int resource, List<Trip> objects, IData activity) {
        super(context, resource, objects);
        this.mResource=resource;
        this.mContext=context;
        this.mData=objects;
        this.activity=activity;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            LayoutInflater inflater= (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(mResource,parent,false);
        }

        final Trip trip=mData.get(position);
        pb= (ProgressBar) convertView.findViewById(R.id.tripByFriendPb);
        ImageView tripIcon= (ImageView) convertView.findViewById(R.id.friendTripIcon);
        TextView title=(TextView)convertView.findViewById(R.id.friendTripTitle);
        TextView createdBy=(TextView)convertView.findViewById(R.id.friendCreatedByText);
        Button viewBtn=(Button)convertView.findViewById(R.id.friendTripView);
        final Button joinBtn=(Button)convertView.findViewById(R.id.friendTripJoin);
        if(trip.getCoverpicUrl()!=null) {
            if(!trip.getCoverpicUrl().isEmpty()) {
                Picasso.with(mContext).load(trip.getCoverpicUrl()).error(R.drawable.ic_flight_takeoff_black_24dp)
                        .placeholder(R.drawable.ic_flight_takeoff_black_24dp)
                        .into(tripIcon, new ImageLoadedCallback(pb) {
                            public void onSuccess() {
                                if (pb != null)
                                    pb.setVisibility(View.GONE);
                            }

                            public void onError() {
                                pb.setVisibility(View.GONE);
                            }
                        });
            }
            else
            {
                pb.setVisibility(View.GONE);
                tripIcon.setBackgroundResource(R.drawable.ic_flight_takeoff_black_24dp);
            }
        }
        else {
            pb.setVisibility(View.GONE);
            tripIcon.setBackgroundResource(R.drawable.ic_flight_takeoff_black_24dp);
        }

        title.setText(trip.getTripname());
        createdBy.setText(trip.createdUserName);
        ArrayList<String> memberIds=new ArrayList<String>();
        for (User u:trip.getMembers()) {
            memberIds.add(u.getUserid());
        }
        fUser= FirebaseAuth.getInstance().getCurrentUser();
        if(!trip.isActive()){
            joinBtn.setEnabled(false);
            joinBtn.setText("Deleted");
        }
        else if(memberIds.contains(fUser.getUid())){
            joinBtn.setText("Joined");
            joinBtn.setEnabled(false);
        }

        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.friendViewTrip(trip);
            }
        });

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert=new AlertDialog.Builder(v.getContext());
                alert.setMessage("Are you sure to join the group "+trip.getTripname()+"?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                joinBtn.setText("Joined");
                                joinBtn.setEnabled(false);
                                activity.friendTripJoin(trip);
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alert.show();
            }
        });

        return convertView;
    }

    private class ImageLoadedCallback implements Callback {
        ProgressBar progressBar;

        public ImageLoadedCallback(ProgressBar pb) {
            progressBar = pb;
        }

        @Override
        public void onSuccess() {

        }

        @Override
        public void onError() {

        }
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    public interface IData{
        public void friendViewTrip(Trip trip);
        public void friendTripJoin(Trip trip);
    }
}
