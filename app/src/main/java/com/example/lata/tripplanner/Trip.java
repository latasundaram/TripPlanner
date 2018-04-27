package com.example.lata.tripplanner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lata on 21-04-2017.
 */

public class Trip implements Serializable{
    String tripId,tripname,coverpicUrl,location;
    List<User> members;
    String createdByUid,createdUserName;
    ArrayList<LocationDetails> tripPlaces;
    boolean active;
    public Trip()
    {

    }

    public Trip(String tripId,String tripname, String coverpicUrl, String location, ArrayList<User> members,
                String createdByUid,String createdUserName,ArrayList<MessageDetails> messages,boolean active) {
        this.tripId=tripId;
        this.tripname = tripname;
        this.coverpicUrl = coverpicUrl;
        this.location = location;
        this.members = members;
        this.createdByUid = createdByUid;
        this.createdUserName=createdUserName;
        this.active=active;
    }
    public void addPlaces(LocationDetails location)
    {
        if(tripPlaces==null)
            tripPlaces=new ArrayList<>();
        tripPlaces.add(location);
    }
    public void removePlace(int index)
    {
        tripPlaces.remove(index);
    }
    public ArrayList<LocationDetails> getTripPlaces() {
        if(tripPlaces==null)
            tripPlaces=new ArrayList<>();
        return tripPlaces;
    }

    public void setTripPlaces(ArrayList<LocationDetails> tripPlaces) {
        if(tripPlaces==null)
            tripPlaces=new ArrayList<>();
        this.tripPlaces = tripPlaces;
    }

    public String getCreatedUserName() {
        return createdUserName;
    }

    public void setCreatedUserName(String createdUserName) {
        this.createdUserName = createdUserName;
    }

    public List<User> getMembers() {
        if(members==null)
            members=new ArrayList<>();
        return members;
    }

    public void setMembers(ArrayList<User> members) {
        if(members==null)
            members=new ArrayList<>();
        this.members = members;
    }

    public void addMember(User member) {
        if(members==null)
            members=new ArrayList<>();
        members.add(member);
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getCreatedByUid() {
        return createdByUid;
    }

    public void setCreatedByUid(String createdByUid) {
        this.createdByUid = createdByUid;
    }

    public String getTripname() {
        return tripname;
    }

    public void setTripname(String tripname) {
        this.tripname = tripname;
    }

    public String getCoverpicUrl() {
        return coverpicUrl;
    }

    public void setCoverpicUrl(String coverpicUrl) {
        this.coverpicUrl = coverpicUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}