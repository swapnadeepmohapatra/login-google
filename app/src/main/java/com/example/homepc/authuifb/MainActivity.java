package com.example.homepc.authuifb;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    public static final String ANONYMOUS = "anonymous";
    public static final int RC_SIGN_IN = 1;
    private String mUsername;
    private TextView textView;
    private CircleImageView imageView;
    private SignInButton signInButton;
    private FirebaseAuth mFirebaseAuth;
    private Button button;
    private ProgressDialog progressDialog;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.hii);
        signInButton = findViewById(R.id.signInButton);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing In...");
        progressDialog.setCanceledOnTouchOutside(false);
        mUsername = ANONYMOUS;
        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    displayUserInfo();
                }
            }
        };

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                if (user != null) {
                    displayUserInfo();
                } else {
                    // User is signed out
                    mUsername = ANONYMOUS;
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
//                                    .setAvailableProviders(Arrays.asList(
                                    .setAvailableProviders(Collections.singletonList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Sign out the user
                AuthUI.getInstance().signOut(MainActivity.this);
                textView.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
                signInButton.setVisibility(View.VISIBLE);
                button.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
                displayUserInfo();
            } else if (resultCode == RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the activity
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    private void displayUserInfo() {
//        mUsername = mFirebaseAuth.getCurrentUser().getDisplayName();
        mUsername = Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getDisplayName();
        Uri mUri = mFirebaseAuth.getCurrentUser().getPhotoUrl();
        Picasso.get().load(mUri).into(imageView);
        textView.setText(mUsername);
        signInButton.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.VISIBLE);
        button.setVisibility(View.VISIBLE);
        progressDialog.dismiss();
    }
}