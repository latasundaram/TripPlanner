package com.example.lata.tripplanner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DiscoverFriendsActivity extends AppCompatActivity implements  searchFriendsAdapter.IData{
    ArrayList<User> usersList;
    ArrayList<User> filteredList;
    searchFriendsAdapter adapter;
    ListView listView;
    FirebaseUser fUser;
    DatabaseReference ref;
    DatabaseReference usersRef;
    User currentUser;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_friends);
        mFirebaseAuth = FirebaseAuth.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        ref= FirebaseDatabase.getInstance().getReference().child("users").child(fUser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser=dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        usersRef=FirebaseDatabase.getInstance().getReference().child("users");
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usersList=new ArrayList<User>();
                if(dataSnapshot.getChildrenCount() > 0){
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        usersList.add(snapshot.getValue(User.class));
                    }
                }
                    filteredList=new ArrayList<User>();
                    for (User u:usersList){
                        if((!currentUser.getFriends().contains(u.getUserid()))&&(u.getUserid()!=currentUser.getUserid())){
                            filteredList.add(u);
                        }
                    }
                    listView= (ListView) findViewById(R.id.usersListView);
                    adapter = new searchFriendsAdapter(DiscoverFriendsActivity.this, R.layout.friendlistitem, filteredList,DiscoverFriendsActivity.this);
                    adapter.setNotifyOnChange(true);
                    listView.setAdapter(adapter);
                }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void sendRequest(User user) {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("users").child(user.getUserid());
        user.addPendingFriends(currentUser.getUserid());
        ref.setValue(user);
    }

    @Override
    public void removeFriend(User user) {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("users").child(user.getUserid());
        user.removeFriend(fUser.getUid());
        ref.setValue(user);
    }
}
