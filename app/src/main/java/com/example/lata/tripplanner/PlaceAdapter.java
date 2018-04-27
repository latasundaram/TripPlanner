package com.example.lata.tripplanner;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Lata on 30-04-2017.
 */

public class PlaceAdapter extends ArrayAdapter<LocationDetails> {

    ArrayList<LocationDetails> mData;
    Idata iData;
    Context mContext;
    int mResource;
    public PlaceAdapter(Context context, int resource, ArrayList<LocationDetails> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mData=objects;
        this.iData= (Idata) context;
        this.mResource=resource;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            LayoutInflater inflater= (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(mResource,parent,false);
        }
        final LocationDetails location=mData.get(position);
        TextView placeTitle= (TextView) convertView.findViewById(R.id.placenamedisplay);
        TextView placeAddress= (TextView) convertView.findViewById(R.id.placeaddrdisplay);
        ImageButton delbtn= (ImageButton) convertView.findViewById(R.id.delplacebtn);
        ImageButton nav=(ImageButton)convertView.findViewById(R.id.navigation);

        nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+location.getLocLatitude() +",+"+location.getLocLongitude()+"&mode=d");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                mContext.startActivity(mapIntent);
            }
        });

        placeTitle.setText(location.getLocationName());
        placeAddress.setText(location.getLocationAddress());

        delbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iData.deletePlace(position);
            }
        });

        return convertView;
    }

    public interface Idata{
            public void deletePlace(int position);
    }
}
