package com.example.lata.tripplanner;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    EditText email;
    EditText password;
    DatabaseReference databaseReference;
    ArrayList<String> signedUpUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                signedUpUsers = new ArrayList<String>();
                if(dataSnapshot.getChildrenCount() > 0){
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        signedUpUsers.add(snapshot.child("email").getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("680507748513-t8tjh9g4g210qcec7tlevm2pqo23dqng.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Plus.API)
                .build();

        findViewById(R.id.googleSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        email= (EditText) findViewById(R.id.emailText);
        password=(EditText)findViewById(R.id.passwordText);

        findViewById(R.id.loginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailText = email.getText().toString();
                String passwordText = password.getText().toString();

                if(email.equals("") || password.equals("")){
                    Toast.makeText(LoginActivity.this, "Invalid Inputs", Toast.LENGTH_SHORT).show();
                }else{
                    mFirebaseAuth.signInWithEmailAndPassword(emailText,passwordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(LoginActivity.this,"Log in not successful",Toast.LENGTH_SHORT).show();
                            }else{
                               finish();
                            }
                        }
                    });
                }
            }
        });

        findViewById(R.id.signUpButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),SignUpActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void signIn() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, 123);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 123) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                handleSignInResult(result);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void handleSignInResult(final GoogleSignInResult result){
        if(result.isSuccess()){
            final GoogleSignInAccount acc = result.getSignInAccount();
            final Person person  = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            //txtHello.setText("Hello, "+acc.getDisplayName());
            AuthCredential credential = GoogleAuthProvider.getCredential(acc.getIdToken(), null);
            mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("demo", "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("demo", "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            if (!signedUpUsers.contains(acc.getEmail())) {
                                String gender = "";
                                if (person != null) {
                                    if (person.getGender() == 0)
                                        gender = "Male";
                                    else if (person.getGender() == 1)
                                        gender = "Female";
                                }
                                FirebaseUser fUser = mFirebaseAuth.getCurrentUser();
                                User user = new User(fUser.getUid(),acc.getGivenName(), acc.getFamilyName(), acc.getEmail(), "",
                                        gender, acc.getPhotoUrl().toString(),null,null);
                                Log.d("demo", user.toString());
                                Map<String, Object> postValues = user.toMap();
                                Map<String, Object> childUpdates = new HashMap<>();
                                childUpdates.put("/users/" + fUser.getUid(), postValues);
                                databaseReference.updateChildren(childUpdates);
                                finish();
                            }
                            else {
                                finish();
                            }
                        }
                    }
                });
        }
    }
}
