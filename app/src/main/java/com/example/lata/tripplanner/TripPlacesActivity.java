package com.example.lata.tripplanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TripPlacesActivity extends AppCompatActivity implements PlaceAdapter.Idata {
    TextView tripnameet;
    Trip trip;
    PlaceAdapter adapter;
    DatabaseReference databaseReference;
    ArrayList<LocationDetails> placesList;
    FirebaseAuth mFirebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_places);
        mFirebaseAuth = FirebaseAuth.getInstance();
        trip= (Trip) this.getIntent().getSerializableExtra("viewplaces");
        tripnameet= (TextView) findViewById(R.id.placeslabel);
        tripnameet.setText("Places of "+trip.getTripname());
        ListView listView= (ListView) findViewById(R.id.placesListView);
        placesList=new ArrayList<>();
        adapter=new PlaceAdapter(this,R.layout.place_row_item,placesList);
        listView.setAdapter(adapter);
        adapter.setNotifyOnChange(true);

        databaseReference=FirebaseDatabase.getInstance().getReference().child("trips").child(trip.getTripId()).child("tripPlaces");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                placesList.clear();
                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        placesList.add(snapshot.getValue(LocationDetails.class));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

    }

    @Override
    public void deletePlace(int position) {
            trip.removePlace(position);
            DatabaseReference myref=FirebaseDatabase.getInstance().getReference().child("trips").child(trip.getTripId());
            myref.setValue(trip);
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
            Intent i=new Intent(TripPlacesActivity.this,LoginActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
