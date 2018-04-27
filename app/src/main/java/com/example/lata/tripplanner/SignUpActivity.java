package com.example.lata.tripplanner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
public class SignUpActivity extends AppCompatActivity {
    EditText fnameet, lnameet, emailet, pwdet, repwdet;
    Button cancelbtn, signupbtn;
    ImageButton gallerybtn;
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    String fname, lname,email,password,gender,imageURL;
    DatabaseReference ref;
    DatabaseReference databaseReference;
    final int ACTIVITY_SELECT_IMAGE = 1234;
    FirebaseStorage storage;
    StorageReference storageReference;
    ImageView profilepic;
    RadioGroup rggender;
    ProgressBar pb;
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
        final UploadTask uploadTask = reference.putBytes(dataArray);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                    Log.d("demo",e.getMessage().toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @SuppressWarnings("VisibleForTests")
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // imageURL = taskSnapshot.getDownloadUrl();
                Log.d("demo", taskSnapshot.getDownloadUrl() + "");
                Picasso.with(SignUpActivity.this).load(taskSnapshot.getDownloadUrl())
                        .error(R.mipmap.ic_launcher)
                        .into(profilepic, new ImageLoadedCallback(pb) {
                    public void onSuccess() {
                        if (pb != null)
                            pb.setVisibility(View.GONE);
                    }

                    public void onError() {
                        pb.setVisibility(View.GONE);
                    }
                });

                imageURL=taskSnapshot.getDownloadUrl().toString();
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
    //FirebaseStorage storage;
    //StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setTitle("Sign Up");

        fnameet = (EditText) findViewById(R.id.fnameedit);
        lnameet = (EditText) findViewById(R.id.lnametext);
        emailet = (EditText) findViewById(R.id.emailtext);
        pwdet = (EditText) findViewById(R.id.pwdtext);
        repwdet = (EditText) findViewById(R.id.repwdtext);
        cancelbtn = (Button) findViewById(R.id.cancelbtn);
        signupbtn = (Button) findViewById(R.id.signupbtn);
        gallerybtn = (ImageButton) findViewById(R.id.browsebtn);
        profilepic= (ImageView) findViewById(R.id.profileicon);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        rggender= (RadioGroup) findViewById(R.id.genderrg);
        ref= FirebaseDatabase.getInstance().getReference("/users");
        mFirebaseAuth=FirebaseAuth.getInstance();
        pb= (ProgressBar) findViewById(R.id.progressBarimg);
        pb.setVisibility(View.GONE);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                            .setDisplayName(fname + " " + lname).build();
                    user.updateProfile(changeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("Demo", "Updated");
                            }
                        }
                    });

                }
            }
        };
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fname = fnameet.getText().toString();
                lname = lnameet.getText().toString();
                RadioButton rb= (RadioButton) findViewById(rggender.getCheckedRadioButtonId());
                gender= (String) rb.getText();
                email=emailet.getText().toString();
                password=pwdet.getText().toString();
                String repwd=repwdet.getText().toString();

                if(fname.equals("")||lname.equals("")||email.equals("")||password.equals("")||repwd.equals("")|| rggender.getCheckedRadioButtonId()==-1)
                {
                    Toast.makeText(SignUpActivity.this, "Enter all details", Toast.LENGTH_SHORT).show();
                }
                else if(!password.equals(repwd))
                {
                    Toast.makeText(SignUpActivity.this, "Passwords doesn't match", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mFirebaseAuth.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(!task.isSuccessful()){
                                        Toast.makeText(SignUpActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                    }else{
                                        FirebaseUser fUser = mFirebaseAuth.getCurrentUser();
                                        Toast.makeText(SignUpActivity.this,"Account successfully created",Toast.LENGTH_SHORT).show();
                                        User user=new User(fUser.getUid(),fname,lname,email,password,gender,imageURL,null,null);
                                        Map<String, Object> postValues = user.toMap();
                                        Map<String, Object> childUpdates = new HashMap<>();
                                        childUpdates.put("/users/" + fUser.getUid(),postValues);
                                        databaseReference.updateChildren(childUpdates);
                                        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(fname + " " + lname).build();
                                        //TODO Change activity name
                                        finish();
                                    }
                                }
                            });
                }
            }
        });

        gallerybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),ACTIVITY_SELECT_IMAGE);
            }
        });

    }
}
