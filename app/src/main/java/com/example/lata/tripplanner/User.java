package com.example.lata.tripplanner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Lata on 21-04-2017.
 */

public class User implements Serializable{
    String userid,fname,lname,email,pwd,gender,profilepicUrl;
    ArrayList<String> friends;
    ArrayList<String> pendingFriends;

    @Override
    public String toString() {
        return "User{" +
                "fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", email='" + email + '\'' +
                ", pwd='" + pwd + '\'' +
                ", gender='" + gender + '\'' +
                ", profilepicUrl='" + profilepicUrl + '\'' +
                ", friends=" + friends +
                '}';
    }

    public User() {
    }
    public HashMap<String,Object> toMap()
    {
        HashMap<String,Object> result=new HashMap<>();
        result.put("userid",userid);
        result.put("fname",fname);
        result.put("lname",lname);
        result.put("email",email);
        result.put("pwd",pwd);
        result.put("gender",gender);
        result.put("profilepicUrl",profilepicUrl);
        result.put("friends",friends);
        result.put("pendingFriends",pendingFriends);
        return result;
    }

    public ArrayList<String> getPendingFriends() {
        if(this.pendingFriends==null)
            this.pendingFriends=new ArrayList<String>();
        return pendingFriends;
    }

    public void setPendingFriends(ArrayList<String> pendingFriends) {
        if(this.pendingFriends==null)
            this.pendingFriends=new ArrayList<String>();
        this.pendingFriends=pendingFriends;
    }

    public void addPendingFriends(String userId) {
        if(this.pendingFriends==null)
            this.pendingFriends=new ArrayList<String>();
        this.pendingFriends.add(userId);
    }

    public void removePendingFriends(String userId) {
        if(this.pendingFriends==null)
            this.pendingFriends=new ArrayList<String>();
        this.pendingFriends.remove(userId);
    }

    public User(String userid, String fname, String lname, String email, String pwd, String gender,
                String profilepicUrl, ArrayList<String> friends, ArrayList<String> pendingFriends ) {
        this.userid=userid;
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.pwd = pwd;
        this.gender = gender;
        this.profilepicUrl=profilepicUrl;
        this.friends=friends;
        this.pendingFriends=pendingFriends;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getProfilepicUrl() {
        return profilepicUrl;
    }

    public void setProfilepicUrl(String profilepicUrl) {
        this.profilepicUrl = profilepicUrl;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public ArrayList<String> getFriends() {
        if(this.friends==null)
            this.friends = new ArrayList<String>();
        return friends;
    }

    public void setFriends(ArrayList<String> friends) {
        if(this.friends==null)
            this.friends = new ArrayList<String>();
        this.friends=friends;
    }

    public void addFriend(String userId) {
        if(this.friends==null)
            this.friends = new ArrayList<String>();
        this.friends.add(userId);
    }

    public void removeFriend(String userId) {
        if(this.friends==null)
            this.friends = new ArrayList<String>();
        this.friends.remove(userId);
    }
}
