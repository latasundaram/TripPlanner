package com.example.lata.tripplanner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Lata on 22-04-2017.
 */

public class TripMemberAdapter extends ArrayAdapter<User>{
    List<User> mData;
    Context mContext;
    int mResource;
    FirebaseUser fUser;
    public TripMemberAdapter(Context context, int resource, List<User> objects) {
        super(context, resource, objects);
        this.mData=objects;
        this.mContext=context;
        this.mResource=resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null)
        {
            LayoutInflater inflater= (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(mResource,parent,false);
        }
        User user=mData.get(position);
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        ImageView img=(ImageView) convertView.findViewById(R.id.profileiconmem);
        TextView name=(TextView)convertView.findViewById(R.id.usernameMem);
        if(user.getProfilepicUrl()!=null) {
            Picasso.with(mContext).load(user.getProfilepicUrl()).into(img);
        }
        else
            img.setBackgroundResource(R.drawable.ic_person_black_24dp);

        name.setText(user.getFname()+" "+user.getLname());

        return convertView;
    }

    @Override
    public int getCount() {
        return mData.size();
    }
}
