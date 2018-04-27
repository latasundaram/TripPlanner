package com.example.lata.tripplanner;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TripsbyYouAdapter.IData,TripByFriendAdapter.IData{

    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference usersRef;
    DatabaseReference tripsRef;
    ArrayList<User> usersList;
    ArrayList<User> searchList;
    ArrayList<Trip> userTrips;
    ArrayList<Trip> friendTrips;
    FirebaseUser fUser;
    DatabaseReference mainRef;
    User currentUser;
    ListView yourTrips;
    ListView friendTripsll;
    TripsbyYouAdapter adapter;
    TripByFriendAdapter adapterFriend;
    Button addTripBtn;
    ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setIcon (R.drawable.ic_menu_white_24dp);
        mFirebaseAuth = FirebaseAuth.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        pb= (ProgressBar) findViewById(R.id.progressBarMain);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                fUser = FirebaseAuth.getInstance().getCurrentUser();
                if (fUser != null) {
                        invalidateOptionsMenu();
                        mainRef=FirebaseDatabase.getInstance().getReference().child("users").child(fUser.getUid());
                        mainRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                currentUser = dataSnapshot.getValue(User.class);
                                if (currentUser != null) {
                                    if (currentUser.getProfilepicUrl() != null) {
                                        if (!currentUser.getProfilepicUrl().equals("")) {
                                            Picasso.with(getApplicationContext()).load(currentUser.getProfilepicUrl())
                                                    .into((ImageView) findViewById(R.id.mainIcon), new ImageLoadedCallback(pb) {
                                                        public void onSuccess() {
                                                            if (pb != null)
                                                                pb.setVisibility(View.GONE);
                                                        }

                                                        public void onError() {
                                                            pb.setVisibility(View.GONE);
                                                        }
                                                    });
                                        } else {
                                            pb.setVisibility(View.GONE);
                                            ((ImageView) findViewById(R.id.mainIcon)).setBackgroundResource(R.drawable.ic_person_black_24dp);

                                        }
                                    } else {
                                        pb.setVisibility(View.GONE);
                                        ((ImageView) findViewById(R.id.mainIcon)).setBackgroundResource(R.drawable.ic_person_black_24dp);
                                    }

                                    tripsRef = FirebaseDatabase.getInstance().getReference().child("trips");
                                    tripsRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            userTrips = new ArrayList<Trip>();
                                            friendTrips = new ArrayList<Trip>();
                                            if (dataSnapshot.getChildrenCount() > 0 && fUser != null && currentUser != null) {
                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                    Trip trip = new Trip();
                                                    if (snapshot.child("createdByUid").getValue(String.class).equals(fUser.getUid())) {
                                                        userTrips.add(snapshot.getValue(Trip.class));
                                                    } else if (currentUser.getFriends().contains(snapshot.child("createdByUid").getValue(String.class))) {
                                                        friendTrips.add(snapshot.getValue(Trip.class));
                                                    }
                                                }
                                            } else {
                                                Log.d("demo", "exception");
                                            }
                                            if (userTrips.size() > 0) {
                                                yourTrips = (ListView) findViewById(R.id.yourTripsView);
                                                TextView t = (TextView) findViewById(R.id.tripsByYouText);
                                                t.setText("Trips created by you:");
                                                adapter = new TripsbyYouAdapter(MainActivity.this, R.layout.trip_by_you, userTrips, MainActivity.this);
                                                adapter.setNotifyOnChange(true);
                                                yourTrips.setAdapter(adapter);
                                            } else if (userTrips.size() == 0) {
                                                TextView t = (TextView) findViewById(R.id.tripsByYouText);
                                                t.setText("Trips created by you:" + "\n\n\n" + "You have not created any trips yet.");
                                                yourTrips = (ListView) findViewById(R.id.yourTripsView);
                                                adapter = new TripsbyYouAdapter(MainActivity.this, R.layout.trip_by_you, userTrips, MainActivity.this);
                                                adapter.setNotifyOnChange(true);
                                                yourTrips.setAdapter(adapter);
                                            }
                                            if (friendTrips.size() > 0) {
                                                TextView t1 = (TextView) findViewById(R.id.tripsByFriendsText);
                                                t1.setText("Trips created by your friends:");
                                                friendTripsll = (ListView) findViewById(R.id.friendsTripView);
                                                adapterFriend = new TripByFriendAdapter(MainActivity.this, R.layout.trip_by_friend, friendTrips, MainActivity.this);
                                                adapterFriend.setNotifyOnChange(true);
                                                friendTripsll.setAdapter(adapterFriend);
                                            } else if (friendTrips.size() == 0) {
                                                TextView t1 = (TextView) findViewById(R.id.tripsByFriendsText);
                                                t1.setText("Trips created by your friends:" + "\n\n\n" + "Your friends have not created any trips yet.");
                                                friendTripsll = (ListView) findViewById(R.id.friendsTripView);
                                                adapterFriend = new TripByFriendAdapter(MainActivity.this, R.layout.trip_by_friend, friendTrips, MainActivity.this);
                                                adapterFriend.setNotifyOnChange(true);
                                                friendTripsll.setAdapter(adapterFriend);
                                            }

                                        }


                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                                @Override
                                public void onCancelled (DatabaseError databaseError){

                                }

                        });


                   }
                    else {
                    Intent i=new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(i);
                }

                // ...
            }
        };

        final EditText searchText= (EditText) findViewById(R.id.searchText);

        findViewById(R.id.searchButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String st=searchText.getText().toString().toLowerCase();
                Intent i=new Intent(getApplicationContext(),SearchListActivity.class);
                i.putExtra("FUNCTION","Search");
                i.putExtra("SEARCH_TEXT",st);
                startActivity(i);
            }
        });

        addTripBtn= (Button) findViewById(R.id.addTripBtn);
        addTripBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,AddTripActivity.class);
                startActivity(i);
            }
        });
    }
    private class ImageLoadedCallback implements Callback {
        ProgressBar progressBar;

        public ImageLoadedCallback(ProgressBar pb) {
            progressBar = pb;
        }

        @Override
        public void onSuccess() {

        }

        @Override
        public void onError() {

        }
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(fUser!=null)
            menu.findItem(R.id.Profile).setTitle("View/Update Profile");
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.Profile){
            Intent intent=new Intent(MainActivity.this,ProfileActivity.class);
            startActivity(intent);
        }
        else if(item.getItemId()==R.id.Requests){
            Intent i=new Intent(getApplicationContext(),SearchListActivity.class);
            i.putExtra("FUNCTION","Pending");
            startActivity(i);
        }
        else if(item.getItemId()==R.id.FriendList){
            Intent i=new Intent(getApplicationContext(),SearchListActivity.class);
            i.putExtra("FUNCTION","Friends");
            startActivity(i);
        }
        else if(item.getItemId()==R.id.DiscoverFriends){
            Intent i=new Intent(getApplicationContext(),DiscoverFriendsActivity.class);
            startActivity(i);
        }
        else if(item.getItemId()==R.id.Logout){
            mFirebaseAuth.signOut();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void viewTrip(Trip trip) {
        Intent i=new Intent(MainActivity.this,TripHomeActivity.class);
        i.putExtra("TRIP_DETAILS",trip);
        startActivity(i);
    }

    @Override
    public void deleteTrip(Trip trip) {
        trip.setActive(false);
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("/trips/"+trip.getTripId());
        ref.setValue(trip);
    }

    @Override
    public void friendViewTrip(Trip trip) {
        Intent i=new Intent(MainActivity.this,TripHomeActivity.class);
        i.putExtra("TRIP_DETAILS",trip);
        startActivity(i);
    }

    @Override
    public void friendTripJoin(Trip trip) {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("/trips/"+trip.getTripId());
        trip.addMember(currentUser);
        ref.setValue(trip);
    }
}
