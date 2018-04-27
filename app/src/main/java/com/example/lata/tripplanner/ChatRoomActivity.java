package com.example.lata.tripplanner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

public class ChatRoomActivity extends AppCompatActivity implements ChatAdapter.Idata {
    EditText et_input;
    ImageButton im_send;
    ImageButton im_gallery;
    ListView listView;
    TextView triptitletv;
    FirebaseAuth mFirebaseAuth;
    DatabaseReference databaseReference;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseStorage storage;
    StorageReference storageReference;
    final int ACTIVITY_SELECT_IMAGE = 1234;
    ArrayList<MessageDetails> messages ;
    ChatAdapter chatAdapter;
    Trip trip;
    String fname="",lname="";
    String userId;
    ArrayList<MessageDetails> filteredmsgs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        setTitle("Chat Room");
        mFirebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        trip = (Trip) this.getIntent().getSerializableExtra("Trip");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        messages = new ArrayList<>();
        et_input = (EditText) findViewById(R.id.messageText);
        im_send = (ImageButton) findViewById(R.id.sendbtn);
        im_gallery = (ImageButton) findViewById(R.id.gallerybtn);
        listView = (ListView) findViewById(R.id.msgListV);
        triptitletv = (TextView) findViewById(R.id.triptitlechat);
        userId=mFirebaseAuth.getCurrentUser().getUid();
        filteredmsgs=new ArrayList<>();
        triptitletv.setText(trip.getTripname());

        Collections.sort(filteredmsgs,new MessageDetails());
        chatAdapter = new ChatAdapter(this, R.layout.custom_message, filteredmsgs, userId);
        listView.setAdapter(chatAdapter);
        chatAdapter.setNotifyOnChange(true);

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fname=dataSnapshot.child("fname").getValue(String.class);
                lname=dataSnapshot.child("lname").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("/trips/"+trip.getTripId());

        if(!trip.isActive()){
            et_input.setEnabled(false);
            im_gallery.setEnabled(false);
            im_send.setEnabled(false);
        }
        im_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = et_input.getText().toString();
                String title = fname+" "+lname;
                Date date = Calendar.getInstance().getTime();
                String key = UUID.randomUUID().toString();

                MessageDetails messageDetails = new MessageDetails(text, title, date, "", false, key,userId);
                databaseReference.child("messages").child(key).setValue(messageDetails);
            }
        });
        im_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), ACTIVITY_SELECT_IMAGE);
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
        final byte[] dataArray = baos.toByteArray();

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
                // Uri url = taskSnapshot.getDownloadUrl();

                String title = fname+" "+lname;
                Date date = Calendar.getInstance().getTime();
                String key = UUID.randomUUID().toString();

                MessageDetails messageDetails = new MessageDetails();
                messageDetails.setText("");
                messageDetails.setUser_name(title);
                messageDetails.setPosted_time(date);
                messageDetails.setImage_url(taskSnapshot.getDownloadUrl().toString());
                messageDetails.setPost_type(true);
                messageDetails.setId(key);
                messageDetails.setPostedBy(userId);
                databaseReference.child("messages").child(key).setValue(messageDetails);
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
            Intent i=new Intent(ChatRoomActivity.this,LoginActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/trips/"+trip.getTripId()+"/messages");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messages.clear();
                filteredmsgs.clear();
                ArrayList<MessageDetails> data = new ArrayList<MessageDetails>();
                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        //Log.d("data",snapshot.getValue(MessageDetails.class).toString());
                        data.add(snapshot.getValue(MessageDetails.class));
                    }
                    messages.addAll(data);

                    for (MessageDetails msg:messages) {
                        if(!msg.getDeletedUsers().contains(userId))
                            filteredmsgs.add(msg);
                    }
                    Collections.sort(filteredmsgs,new MessageDetails());
                    chatAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void deletemessage(MessageDetails messageDetails) {
        messageDetails.addDeletedUsers(userId);
        DatabaseReference mref = FirebaseDatabase.getInstance().getReference("/trips/"+trip.getTripId()+"/messages/"+messageDetails.getId());
        mref.setValue(messageDetails);
    }
}
