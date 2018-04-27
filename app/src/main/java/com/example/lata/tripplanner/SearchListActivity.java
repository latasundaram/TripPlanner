package com.example.lata.tripplanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchListActivity extends AppCompatActivity implements searchFriendsAdapter.IData,pendingAdapter.IData{

    ArrayList<User> usersList;
    ArrayList<User> searchList;
    ArrayList<User> friendsList;
    ArrayList<User> pendingList;
    String searchText;
    String function;
    searchFriendsAdapter adapter;
    pendingAdapter pendAdapter;
    ListView listView;
    FirebaseUser fUser;
    DatabaseReference ref;
    DatabaseReference usersRef;
    User currentUser;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_list);
        mFirebaseAuth = FirebaseAuth.getInstance();
        function=this.getIntent().getExtras().getString("FUNCTION");
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        ref=FirebaseDatabase.getInstance().getReference().child("users").child(fUser.getUid());
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
                if(function.equals("Search")){
                    searchText= SearchListActivity.this.getIntent().getExtras().getString("SEARCH_TEXT");
                    searchList=new ArrayList<User>();
                    for (User u: usersList) {
                        if(!searchText.equals("")) {
                            if (u.getFname().toLowerCase().contains(searchText) || u.getLname().toLowerCase().contains(searchText)) {
                                searchList.add(u);
                            }
                        }
                    }
                    if(searchList.size()>0) {
                        listView= (ListView) findViewById(R.id.searchListView);
                        adapter = new searchFriendsAdapter(SearchListActivity.this, R.layout.friendlistitem, searchList,SearchListActivity.this);
                        adapter.setNotifyOnChange(true);
                        listView.setAdapter(adapter);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "No matches found!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                else if(function.equals("Friends")){
                    friendsList=new ArrayList<User>();
                    for (User u:usersList){
                        if(currentUser.getFriends().contains(u.getUserid())){
                            friendsList.add(u);
                        }
                    }
                    if(friendsList.size()>=0){
                        listView= (ListView) findViewById(R.id.searchListView);
                        adapter = new searchFriendsAdapter(SearchListActivity.this, R.layout.friendlistitem, friendsList,SearchListActivity.this);
                        adapter.setNotifyOnChange(true);
                        listView.setAdapter(adapter);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "You don't have any friends to display!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                else if(function.equals("Pending")){
                    pendingList=new ArrayList<User>();
                    for (User u:usersList){
                        if(currentUser.getPendingFriends().contains(u.getUserid())){
                            pendingList.add(u);
                        }
                    }
                    if(pendingList.size()>=0){
                        listView= (ListView) findViewById(R.id.searchListView);
                        pendAdapter = new pendingAdapter(SearchListActivity.this, R.layout.pending_item, pendingList,SearchListActivity.this);
                        pendAdapter.setNotifyOnChange(true);
                        listView.setAdapter(pendAdapter);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "You don't have any pending friend request to display!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.logout_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.Logout){
            mFirebaseAuth.signOut();
            Intent i=new Intent(SearchListActivity.this, LoginActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void sendRequest(User user) {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("users").child(user.getUserid());
        user.addPendingFriends(currentUser.getUserid());
        ref.setValue(user);
        //ref.child("pendingRequest").child(fUser.getUid()).setValue(currentUser.getUserid());
    }

    @Override
    public void removeFriend(User user) {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("users").child(user.getUserid());
        user.removeFriend(fUser.getUid());
        ref.setValue(user);
        //ref.child("friends").child(fUser.getUid()).removeValue();

        DatabaseReference ref1=FirebaseDatabase.getInstance().getReference().child("users").child(fUser.getUid());
        currentUser.removeFriend(user.getUserid());
        ref1.setValue(currentUser);
       // ref1.child("friends").child(user.getUserid()).removeValue();
    }

    @Override
    public void acceptRequest(User user) {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("users").child(user.getUserid());
        user.addFriend(currentUser.getUserid());
        ref.setValue(user);
       // ref.child("friends").child(fUser.getUid()).setValue(currentUser.getUserid());

        DatabaseReference ref1=FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUserid());
        currentUser.addFriend(user.getUserid());
        currentUser.removePendingFriends(user.getUserid());
        ref1.setValue(currentUser);
       // ref1.child("pendingRequest").child(user.getUserid()).removeValue();
    }

    @Override
    public void rejectRequest(User user) {
        DatabaseReference ref1=FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUserid());
        currentUser.removePendingFriends(user.getUserid());
        ref.setValue(currentUser);
        // ref1.child("pendingRequest").child(user.getUserid()).removeValue();
    }
}
