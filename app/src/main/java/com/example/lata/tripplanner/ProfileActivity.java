package com.example.lata.tripplanner;

import android.app.Activity;
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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {
    EditText fnameet,lnameet,emailet,pwdet;
    RadioGroup rggender;
    RadioButton rbmale,rbfemale;
    String fname,lname,gender,email,pwd,userid,imageURL;
    ImageView profileicon;
    Button cancelbtn,updatePicbtn,updateProfilebtn;
    FirebaseAuth mFirebaseAuth;
    DatabaseReference databaseReference;
    FirebaseAuth.AuthStateListener mAuthListener;
    final int ACTIVITY_SELECT_IMAGE = 1234;
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference ref;
    ProgressBar pb;
    User currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("Profile");
        mFirebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        fnameet= (EditText) findViewById(R.id.fnameedit);
        lnameet= (EditText) findViewById(R.id.lnameedit);
        emailet= (EditText) findViewById(R.id.emailedit);
        pwdet= (EditText) findViewById(R.id.pwdedit);
        cancelbtn= (Button) findViewById(R.id.cancelbtnedit);
        updatePicbtn= (Button) findViewById(R.id.updatepicbtn);
        updateProfilebtn= (Button) findViewById(R.id.updateprofilebtn);
        rggender= (RadioGroup) findViewById(R.id.genderRadiogrp);
        rbmale= (RadioButton) findViewById(R.id.male);
        rbfemale= (RadioButton) findViewById(R.id.female);
        fnameet.setEnabled(false);
        lnameet.setEnabled(false);
        emailet.setEnabled(false);
        pwdet.setEnabled(false);
        rggender.setEnabled(false);
        updatePicbtn.setEnabled(false);
        updateProfilebtn.setTag("edit");
        profileicon=(ImageView)findViewById(R.id.iv_imgpost);
        pb= (ProgressBar) findViewById(R.id.progressBarImage);
        pb.setVisibility(View.GONE);
        profileicon.setBackgroundResource(R.drawable.ic_person_black_24dp);
        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fUser != null) {
            userid = fUser.getUid();
             ref= FirebaseDatabase.getInstance().getReference("/users/"+userid);
        }
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser=dataSnapshot.getValue(User.class);
                fname=dataSnapshot.child("fname").getValue().toString();
                lname=dataSnapshot.child("lname").getValue().toString();
                pwd=dataSnapshot.child("pwd").getValue().toString();
                gender=dataSnapshot.child("gender").getValue().toString();
                if(dataSnapshot.hasChild("profilepicUrl"))
                    imageURL=dataSnapshot.child("profilepicUrl").getValue().toString();
                else
                    imageURL=null;
                email=dataSnapshot.child("email").getValue().toString();
                fnameet.setText(fname);
                lnameet.setText(lname);
                pwdet.setText(pwd);
                emailet.setText(email);
                if(gender.equals("Male"))
                    rggender.check(R.id.male);
                else if(gender.equals("Female"))
                    rggender.check(R.id.female);
                if(imageURL!=null) {
                    Picasso.with(getApplicationContext()).load(imageURL).error(R.drawable.ic_person_black_24dp)
                            .placeholder(R.drawable.ic_person_black_24dp)
                            .into(profileicon, new ImageLoadedCallback(pb) {
                                public void onSuccess() {
                                    if (pb != null)
                                        pb.setVisibility(View.GONE);
                                }

                                public void onError() {
                                    pb.setVisibility(View.GONE);
                                }
                            });
                }
                else {
                    pb.setVisibility(View.GONE);
                    profileicon.setBackgroundResource(R.drawable.ic_person_black_24dp);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        updateProfilebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getTag().equals("edit")) {
                    fnameet.setEnabled(true);
                    lnameet.setEnabled(true);
                    pwdet.setEnabled(true);
                    rggender.setEnabled(true);
                    updatePicbtn.setEnabled(true);
                    updateProfilebtn.setText("Update changes");
                    v.setTag("update");
                }
                else if(v.getTag().equals("update"))
                {
                    lname=lnameet.getText().toString();
                    fname=fnameet.getText().toString();
                    pwd=pwdet.getText().toString();
                    RadioButton rb= (RadioButton) findViewById(rggender.getCheckedRadioButtonId());
                    gender= (String) rb.getText();
                    currentUser.setLname(lname);
                    currentUser.setFname(fname);
                    currentUser.setPwd(pwd);
                    currentUser.setGender(gender);
                    currentUser.setEmail(email);
                    currentUser.setProfilepicUrl(imageURL);
                    ref.setValue(currentUser);
                    fUser.updatePassword(pwd).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                                Log.d("demo","password updated");
                        }
                    });
                    Toast.makeText(ProfileActivity.this, "Profile has been updated", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
        updatePicbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),ACTIVITY_SELECT_IMAGE);
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
            Intent i=new Intent(ProfileActivity.this,LoginActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
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
                Picasso.with(ProfileActivity.this).load(taskSnapshot.getDownloadUrl()).error(R.mipmap.ic_launcher)
                        .into(profileicon,new ImageLoadedCallback(pb) {
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
}
