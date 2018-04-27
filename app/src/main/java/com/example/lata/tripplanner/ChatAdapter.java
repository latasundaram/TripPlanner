package com.example.lata.tripplanner;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;

/**
 * Created by Lata on 21-04-2017.
 */

public class ChatAdapter extends ArrayAdapter<MessageDetails>{
    ArrayList<MessageDetails> mData;
    Idata iData;
    Context mContext;
    String userId;
    public ChatAdapter(Context context, int resource, ArrayList<MessageDetails> objects,String userId) {
        super(context, resource, objects);
        this.mContext = context;
        this.mData = objects;
        this.iData = (Idata) context;
        this.userId=userId;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final MessageDetails messageDetails = mData.get(position);
        View view;
            if (mData.get(position).getPost_type()) {
                //image
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.custom_img_message, parent, false);

                TextView msg_img_name = (TextView) view.findViewById(R.id.tv_nameimg);
                TextView msg_imgpostedtime = (TextView) view.findViewById(R.id.tv_imgpostedtime);
                ImageView msg_iv = (ImageView) view.findViewById(R.id.iv_imgpost);
                ImageButton deleteBtn=(ImageButton)view.findViewById(R.id.imageDeleteButton);

                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alert=new AlertDialog.Builder(v.getContext());
                        alert.setTitle("Delete Message")
                                .setMessage("Are you sure you want to delete the message?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        iData.deletemessage(messageDetails);
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

                if(messageDetails.getPostedBy().equals(userId))
                    msg_img_name.setText("Posted by you");
                else
                    msg_img_name.setText(messageDetails.getUser_name());
                PrettyTime p=new PrettyTime();
                msg_imgpostedtime.setText(p.format(messageDetails.getPosted_time()));
                Picasso.with(getContext()).load(messageDetails.getImage_url()).into(msg_iv);

            } else {
                //Text msg

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.custom_message,parent,false);

                TextView msg_text= (TextView) view.findViewById(R.id.tv_msg_text);
                TextView msg_name= (TextView) view.findViewById(R.id.tv_msg_name);
                TextView msg_posttime= (TextView) view.findViewById(R.id.tv_msg_updated_time);
                ImageButton deleteBtn=(ImageButton)view.findViewById(R.id.messageDeleteButton);

                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alert=new AlertDialog.Builder(v.getContext());
                        alert.setTitle("Delete Message")
                                .setMessage("Are you sure you want to delete the message?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        iData.deletemessage(messageDetails);
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

                if(messageDetails.getPostedBy().equals(userId))
                    msg_name.setText("Posted by you");
                else
                    msg_name.setText(messageDetails.getUser_name());
                msg_text.setText(messageDetails.getText());
                PrettyTime p=new PrettyTime();
                msg_posttime.setText(p.format(messageDetails.getPosted_time()));
            }
            return view;
    }
    public interface Idata {
        public void deletemessage(MessageDetails messageDetails);
    }
    @Override
    public int getCount() {
        return mData.size();
    }
}
