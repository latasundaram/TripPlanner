package com.example.lata.tripplanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TripHomeActivity extends AppCompatActivity implements PlaceAdapter.Idata {
    TextView titletv,createdBytv,tripStatus;
    ImageView coverpicim;
    List<User> memberList;
    Trip trip;
    ListView memberListView;
    FirebaseAuth mFirebaseAuth;
    DatabaseReference databaseReference;
    FirebaseAuth.AuthStateListener mAuthListener;
    String userId,userName,loggeduserId;
    ImageButton chatroombtn;
    TripMemberAdapter adapter;
    final static int PLACE_PICKER_REQUEST=1;
    ProgressBar pb;
    PlaceAdapter placeadapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_home);
        setTitle("Trip Details");
        mFirebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        memberList=new ArrayList<>();
        titletv= (TextView) findViewById(R.id.triptitle);
        createdBytv= (TextView) findViewById(R.id.tripcreatedby);
        coverpicim= (ImageView) findViewById(R.id.tripcoverim);
        memberListView= (ListView) findViewById(R.id.membersListView);
        chatroombtn= (ImageButton) findViewById(R.id.chatroombtn);
        tripStatus= (TextView) findViewById(R.id.tripStatus);
        trip= (Trip) this.getIntent().getSerializableExtra("TRIP_DETAILS");
        pb= (ProgressBar) findViewById(R.id.triphomeprogressbar);
        //ListView listView= (ListView) findViewById(R.id.placesListView);
       /* placeadapter=new PlaceAdapter(this,R.layout.place_row_item,trip.getTripPlaces());
        listView.setAdapter(placeadapter);
        placeadapter.setNotifyOnChange(true);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.trip_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.searchAddPlaces)
        {
            if(trip.isActive()) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    Intent i = builder.build(TripHomeActivity.this);
                    startActivityForResult(i, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    Log.d("demo", e.getMessage());
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                    Log.d("demo", e.getMessage());
                }
            }
            else
            {
                Toast.makeText(this, "This trip has been deleted", Toast.LENGTH_SHORT).show();
            }
        }
        else if(item.getItemId()==R.id.viewPlaces)
        {
            if(trip.getTripPlaces().size()==0){
                Toast.makeText(this, "No places added yet!", Toast.LENGTH_SHORT).show();
            }
            else {
                Intent i = new Intent(this, TripPlacesActivity.class);
                i.putExtra("viewplaces", trip);
                startActivity(i);
            }
        }
        else if(item.getItemId()==R.id.viewRoute)
        {
            if(trip.getTripPlaces().size()>0) {
                Intent i = new Intent(this, MapsActivity.class);
                i.putExtra("placesList", trip);
                startActivity(i);
            }
            else
                Toast.makeText(this, "No places added!", Toast.LENGTH_SHORT).show();
        }
        else if(item.getItemId()==R.id.Logout){
            mFirebaseAuth.signOut();
            Intent i=new Intent(TripHomeActivity.this,LoginActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("trips").child(trip.getTripId());
                Place place = PlacePicker.getPlace(this, data);
                LocationDetails location=new LocationDetails(place.getId(),place.getName().toString(),place.getAddress().toString(),place.getLatLng().latitude,place.getLatLng().longitude);
                trip.addPlaces(location);
                ref.setValue(trip);
            }
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("trips").child(trip.getTripId());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                trip=dataSnapshot.getValue(Trip.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        userId=trip.getCreatedByUid();
        chatroombtn.setEnabled(false);
        mFirebaseAuth = FirebaseAuth.getInstance();
        loggeduserId=mFirebaseAuth.getCurrentUser().getUid();
        userName=trip.getCreatedUserName();
        memberList=trip.getMembers();
        titletv.setText(trip.getTripname());

        if(!trip.isActive())
            tripStatus.setText("Trip has been deleted");
        else
            tripStatus.setText("");
        createdBytv.setText("Created By : "+userName);
        if(trip.getCoverpicUrl()!=null&&!trip.getCoverpicUrl().equals(""))
            Picasso.with(this).load(trip.getCoverpicUrl()).into(coverpicim,new ImageLoadedCallback(pb) {
                public void onSuccess() {
                    if (pb != null)
                        pb.setVisibility(View.GONE);
                }

                public void onError() {
                    pb.setVisibility(View.GONE);
                }
            });
        else {
            coverpicim.setBackgroundResource(R.drawable.ic_flight_takeoff_black_24dp);
            pb.setVisibility(View.GONE);
        }
        for (User user:memberList) {
            if(user.getUserid().equals(loggeduserId))
                chatroombtn.setEnabled(true);
        }

        chatroombtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(TripHomeActivity.this,ChatRoomActivity.class);
                intent.putExtra("Trip",trip);
                startActivity(intent);
            }
        });
        adapter=new TripMemberAdapter(TripHomeActivity.this,R.layout.tripmember_row_item,memberList);
        adapter.setNotifyOnChange(true);
        memberListView.setAdapter(adapter);

    }

    @Override
    public void deletePlace(int position) {
        trip.removePlace(position);
        DatabaseReference myref=FirebaseDatabase.getInstance().getReference().child("trips").child(trip.getTripId());
        myref.setValue(trip);
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
}
