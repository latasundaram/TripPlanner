package com.example.lata.tripplanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class AddTripActivity extends AppCompatActivity {
    EditText titleet, locationet;
    Button addImagebtn, createtripbtn, addmembersbtn;
    String userid,imageURL;
    ImageView coverpic;
    ArrayList<User> members;
    ArrayList<String> friendlist;
    FirebaseAuth mFirebaseAuth;
    DatabaseReference databaseReference;
    FirebaseAuth.AuthStateListener mAuthListener;
    final int ACTIVITY_SELECT_IMAGE = 1234;
    FirebaseStorage storage;
    StorageReference storageReference;
   ArrayList<User> friendInfoList;
    ListView memListView;
    String userName;
    User currentUser;
    ProgressBar pb;
    TripMemberAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);
        setTitle("Add Trip");
        mFirebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        titleet = (EditText) findViewById(R.id.triptitletext);
        locationet = (EditText) findViewById(R.id.triploctext);
        addImagebtn = (Button) findViewById(R.id.trippicbtn);
        createtripbtn = (Button) findViewById(R.id.createtripbtn);
        addmembersbtn = (Button) findViewById(R.id.addmembtn);
        coverpic = (ImageView) findViewById(R.id.tripcoverim);
        friendlist=new ArrayList<>();
        members=new ArrayList<>();
        userid =mFirebaseAuth.getCurrentUser().getUid();
        imageURL="";
        friendInfoList=new ArrayList<>();
        memListView= (ListView) findViewById(R.id.memberListView);
        adapter=new TripMemberAdapter(AddTripActivity.this,R.layout.tripmember_row_item,members);
        adapter.setNotifyOnChange(true);
        memListView.setAdapter(adapter);
        pb= (ProgressBar) findViewById(R.id.progressBarImgTrip);
        pb.setVisibility(View.GONE);
        addmembersbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(friendlist.size()>0) {
                    String[] friendnames = new String[friendInfoList.size()];
                    for (int i = 0; i < friendInfoList.size(); i++)
                        friendnames[i] = friendInfoList.get(i).getFname() + " "+friendInfoList.get(i).getLname();
                    if (friendnames.length > 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddTripActivity.this);
                        builder.setTitle("Add friends to trip").setItems(friendnames, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                User selectedmember = friendInfoList.get(which);
                                members.add(selectedmember);
                                friendInfoList.remove(which);
                                adapter.notifyDataSetChanged();
                            }
                        });
                        builder.show();
                    }
                    else
                        Toast.makeText(AddTripActivity.this, "No friends to display", Toast.LENGTH_SHORT).show();
                }
            }
        });
        addImagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),ACTIVITY_SELECT_IMAGE);
            }
        });
        createtripbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tripname=titleet.getText().toString();
                String triploc=locationet.getText().toString();
                if(tripname.equals("")||triploc.equals(""))
                {
                    Toast.makeText(AddTripActivity.this, "Enter all details", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String tripId=UUID.randomUUID().toString();
                    members.add(currentUser);
                    userName=currentUser.getFname()+" "+currentUser.getLname();
                    Trip trip=new Trip(tripId,tripname,imageURL,triploc,members,userid,userName,new ArrayList<MessageDetails>(),true);
                    databaseReference.child("trips").child(tripId).setValue(trip);


                    Intent intent = new Intent(AddTripActivity.this,MainActivity.class);
                    startActivity(intent);
                }
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/users/" + userid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendlist.clear();
                currentUser=new User();
                ArrayList<String> data = new ArrayList<>();

                        currentUser.setFname(dataSnapshot.child("fname").getValue(String.class));
                        currentUser.setLname(dataSnapshot.child("lname").getValue(String.class));
                        currentUser.setEmail(dataSnapshot.child("email").getValue(String.class));
                        currentUser.setGender(dataSnapshot.child("gender").getValue(String.class));
                        currentUser.setUserid(dataSnapshot.child("userid").getValue(String.class));
                        currentUser.setProfilepicUrl(dataSnapshot.child("profilepicUrl").getValue(String.class));
                        currentUser.setPwd(dataSnapshot.child("pwd").getValue(String.class));

                        for (DataSnapshot snapshot1 : dataSnapshot.child("friends").getChildren()) {
                            data.add(snapshot1.getValue(String.class));
                        }

                    friendlist.addAll(data);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference mref = FirebaseDatabase.getInstance().getReference().child("users");
        mref.addValueEventListener(new ValueEventListener() {
            @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    friendInfoList = new ArrayList<User>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (friendlist.contains(snapshot.child("userid").getValue(String.class))) {
                            friendInfoList.add(snapshot.getValue(User.class));
                        }
                    }
                }
            }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    try {
                        pb.setVisibility(View.VISIBLE);
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                        storeImage(bitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    void storeImage(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte[] dataArray = baos.toByteArray();

        StorageReference reference = storageReference.child(UUID.randomUUID().toString() + ".png");
        UploadTask uploadTask = reference.putBytes(dataArray);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @SuppressWarnings("VisibleForTests")
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageURL = taskSnapshot.getDownloadUrl().toString();
                Log.d("demo",taskSnapshot.getDownloadUrl()+"");
                Picasso.with(AddTripActivity.this).load(taskSnapshot.getDownloadUrl()).error(R.mipmap.ic_launcher).
                        into(coverpic,new ImageLoadedCallback(pb) {
                    public void onSuccess() {
                        if (pb != null)
                            pb.setVisibility(View.GONE);
                    }

                public void onError() {
                    pb.setVisibility(View.GONE);
                }
                });
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
}
