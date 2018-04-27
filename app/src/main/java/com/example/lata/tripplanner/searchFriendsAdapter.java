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
 * Created by vinutna on 21-04-2017.
 */

public class searchFriendsAdapter extends ArrayAdapter<User> {
    int mResource;
    Context mContext;
    List<User> mData;
    IData activity;
    FirebaseUser fUser;

    public searchFriendsAdapter(Context context, int resource, List<User> objects, IData activity) {
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

        ImageView img=(ImageView) convertView.findViewById(R.id.profileIcon);
        TextView name=(TextView)convertView.findViewById(R.id.profileName);
        final Button sendRequest=(Button)convertView.findViewById(R.id.friendStatusButton);

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        /*ArrayList<String> friendsList=new ArrayList<String>();
        if(user.friends!=null) {
            for (User u : user.friends) {
                friendsList.add(u.getUserid());
            }
        }
        ArrayList<String> pendingRequests=new ArrayList<String>();
        if(user.pendingFriends!=null) {
            for (User u : user.pendingFriends) {
                pendingRequests.add(u.getUserid());
            }
        }*/
        if(user.getUserid().equals(fUser.getUid())){
            sendRequest.setVisibility(View.GONE);
        }
        else if(user.getFriends().contains(fUser.getUid())) {
            sendRequest.setText("Remove Friend");
        }
        else if (user.getPendingFriends().contains(fUser.getUid())) {
            sendRequest.setText("Request Sent");
            sendRequest.setEnabled(false);
        }
        else
            sendRequest.setText("Add Friend");
        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sendRequest.getText().equals("Remove Friend")){
                    sendRequest.setText("Add Friend");
                    activity.removeFriend(user);
                }
                else if(sendRequest.getText().equals("Add Friend")){
                    sendRequest.setText("Request Sent");
                    sendRequest.setEnabled(false);
                    activity.sendRequest(user);
                }
            }
        });

        Picasso.with(mContext).load(user.getProfilepicUrl()).into(img);
        name.setText(user.getFname()+" "+user.getLname());

        return convertView;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    public interface IData{
        public void sendRequest(User user);
        public void removeFriend(User user);
    }
}
