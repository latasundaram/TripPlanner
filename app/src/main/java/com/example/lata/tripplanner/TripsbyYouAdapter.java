package com.example.lata.tripplanner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by vinutna on 22-04-2017.
 */

public class TripsbyYouAdapter extends ArrayAdapter<Trip> {
    int mResource;
    Context mContext;
    List<Trip> mData;
    IData activity;
    FirebaseUser fUser;

    public TripsbyYouAdapter(Context context, int resource, List<Trip> objects, IData activity) {
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
        ImageView tripIcon= (ImageView) convertView.findViewById(R.id.yourTripIcon);
        TextView title=(TextView)convertView.findViewById(R.id.yourTripTitle);
        TextView createdBy=(TextView)convertView.findViewById(R.id.createdByText);
        Button viewBtn=(Button)convertView.findViewById(R.id.yourTripView);
        Button deleteBtn=(Button)convertView.findViewById(R.id.yourTripDelete);
        if(!trip.isActive()){
            deleteBtn.setText("Deleted");
            deleteBtn.setEnabled(false);
        }
        if(trip.getCoverpicUrl()!=null){
        if(!trip.getCoverpicUrl().equals(""))
            Picasso.with(mContext).load(trip.getCoverpicUrl()).into(tripIcon);
        else
            tripIcon.setBackgroundResource(R.drawable.ic_flight_takeoff_black_24dp);
        }
        else
            tripIcon.setBackgroundResource(R.drawable.ic_flight_takeoff_black_24dp);
        title.setText(trip.getTripname());
        createdBy.setText("Created by: "+trip.createdUserName);

        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.viewTrip(trip);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.deleteTrip(trip);
            }
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    public interface IData{
        public void viewTrip(Trip trip);
        public void deleteTrip(Trip trip);
    }
}
