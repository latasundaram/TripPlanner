package com.example.lata.tripplanner;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Lata on 21-04-2017.
 */

public class MessageDetails implements Comparator<MessageDetails>{
    String text,user_name,image_url, id,postedBy;
    Date posted_time;
    boolean post_type;
    ArrayList<String> deletedUsers;
    public String getId() {
        return id;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getPosted_time() {
        return posted_time;
    }
    public void setPosted_time(Date posted_time) {

        this.posted_time = posted_time;
    }

    public ArrayList<String> getDeletedUsers() {
        if(deletedUsers==null)
            return new ArrayList<>();
        else
            return deletedUsers;
    }

    public void setDeletedUsers(ArrayList<String> deletedUsers) {
        if(deletedUsers==null)
            deletedUsers=new ArrayList<>();
        this.deletedUsers = deletedUsers;
    }
    public void addDeletedUsers(String userid)
    {
        if(deletedUsers==null)
            deletedUsers=new ArrayList<>();

        deletedUsers.add(userid);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public boolean getPost_type() {
        return post_type;
    }

    public void setPost_type(boolean post_type) {
        this.post_type = post_type;
    }

    public MessageDetails() {
    }

   /* public Map<String,Object> toMap(){

        //Comment comment = new Comment();

        HashMap<String,Object> result = new HashMap<>();
        result.put("text",text);
        result.put("user_name",user_name);
        result.put("posted_time",posted_time);
        result.put("image_url",image_url);
        result.put("post_type",post_type);
        result.put("id",id);
        result.put("postedBy",postedBy);
        return result;
    }*/

    public MessageDetails(String text, String user_name, Date posted_time, String image_url, boolean post_type,String key,String postedBy) {
        this.text = text;
        this.user_name = user_name;
        this.posted_time = posted_time;
        this.image_url = image_url;
        this.post_type = post_type;
        this.id = key;
        this.postedBy=postedBy;
    }

    @Override
    public int compare(MessageDetails o1, MessageDetails o2) {
        return o2.getPosted_time().compareTo(o1.getPosted_time());
    }
}