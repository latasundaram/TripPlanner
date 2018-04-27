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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by vinutna on 22-04-2017.
 */

public class pendingAdapter extends ArrayAdapter<User>{
    int mResource;
    Context mContext;
    List<User> mData;
    IData activity;
    FirebaseUser fUser;

    public pendingAdapter(Context context, int resource, List<User> objects, IData activity) {
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
        final User user=mData.get(position);
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        ImageView img=(ImageView) convertView.findViewById(R.id.pendingIcon);
        TextView name=(TextView)convertView.findViewById(R.id.pendingName);
        final Button acceptButton=(Button)convertView.findViewById(R.id.pendingAccept);
        final Button rejectButton=(Button)convertView.findViewById(R.id.pendingReject);

        Picasso.with(mContext).load(user.getProfilepicUrl()).into(img);
        name.setText(user.getFname()+" "+user.getLname());

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptButton.setEnabled(false);
                rejectButton.setEnabled(false);
                activity.acceptRequest(user);
            }
        });

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptButton.setEnabled(false);
                rejectButton.setEnabled(false);
                activity.rejectRequest(user);
            }
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    public interface IData{
        public void acceptRequest(User user);
        public void rejectRequest(User user);
    }
}
