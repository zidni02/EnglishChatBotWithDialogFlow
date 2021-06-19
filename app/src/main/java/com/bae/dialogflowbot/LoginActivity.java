package com.bae.dialogflowbot;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    private int RC_SIGN_IN = 1;
    private GoogleSignInClient mGooglesigninClient;
    private SignInButton signInButton;
    private ProgressDialog mProgressDialog;
    private ImageView passShow, passHide;
    private TextView createAccTxt, forgotPassTxt;
    private Button mLoginButton, googleLoginButton;
    private EditText userEmail, userPass;
    private String TAG = "LoginActivity";
    //Fb Auth
    private static final String EMAIL = "email";
    private LoginButton loginButtonFb;
    private CallbackManager callbackManager;

    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeFields();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        actionBar = getSupportActionBar();
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().hide();

        //hide show pass
        passShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPass.setTransformationMethod(new PasswordTransformationMethod());
                passShow.setVisibility(View.GONE);
                passHide.setVisibility(View.VISIBLE);
            }
        });
        passHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPass.setTransformationMethod(null);
                passHide.setVisibility(View.GONE);
                passShow.setVisibility(View.VISIBLE);
            }
        });


        mAuth = FirebaseAuth.getInstance();
        //fb login
//        fbloginButton = (LoginButton) findViewById(R.id.login_button);
        loginButtonFb = findViewById(R.id.login_button_fb);
        callbackManager = CallbackManager.Factory.create();
        loginButtonFb.setPermissions(Arrays.asList(EMAIL));
        loginButtonFb.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });


        //google login
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("929828819095-gkpvabkd7kfu3ons6qrpjb1deb7q1lqf.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGooglesigninClient = GoogleSignIn.getClient(this, gso);

        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });


        findViewById(R.id.forgot_pass_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ForgotPassword.class));
            }
        });


        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allowUser2Login();
            }
        });

        createAccTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createAccIntent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(createAccIntent);
            }
        });


    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user.getUid(), user.getDisplayName());
                            SharedPreferences.Editor editor = getSharedPreferences("users", MODE_PRIVATE).edit();
                            editor.putString("personName", user.getDisplayName());
                            editor.apply();
//                            ;
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.putExtra("username", user.getDisplayName());
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }
                    }
                });

    }

    private void googleSignIn() {

        Intent intentSignin = mGooglesigninClient.getSignInIntent();
        startActivityForResult(intentSignin, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInresult(task);
        }
    }

    private void handleSignInresult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
//            Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in Users's information
//                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user.getUid(), user.getDisplayName());
                            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                            String personName = account.getDisplayName();
                            SharedPreferences.Editor editor = getSharedPreferences("users", MODE_PRIVATE).edit();
                            editor.putString("personName", personName);
                            editor.apply();
                            if (account != null) {

                                String personGivenName = account.getGivenName();
                                String personFamilyName = account.getFamilyName();
                                String personEmail = account.getEmail();
                                String personId = account.getId();
                                Uri personPhoto = account.getPhotoUrl();
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                intent.putExtra("username", personName);
                                startActivity(intent);
                                finish();
                            }
                            DatabaseReference myRef;
//                            updateUI(user);
//                            Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_LONG).show();
//                            btnsignout.setVisibility(View.VISIBLE);
                        } else {
                            // If sign in fails, display a message to the Users.
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            updateUI(null);
                        }
                    }
                });
    }

    private void initializeFields() {

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mProgressDialog = new ProgressDialog(this);

        createAccTxt = findViewById(R.id.create_account_text);
        forgotPassTxt = findViewById(R.id.forgot_pass_text);
        mLoginButton = findViewById(R.id.login_btn);
        googleLoginButton = findViewById(R.id.google_login);
        userEmail = findViewById(R.id.login_email_edt_txt);
        userPass = findViewById(R.id.login_pass_edt_txt);
        passHide=findViewById(R.id.passToggleHide);
        passShow=findViewById(R.id.passToggleShow);
//        fbloginButton=findViewById(R.id.login_button_fb);
    }

    //Back navigation
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void allowUser2Login() {
        String email = userEmail.getText().toString().trim();
        String pass = userPass.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            userEmail.setError("Enter your email!");
            return;
        }

        if (TextUtils.isEmpty(pass)) {
            userPass.setError("Enter your password!");
            return;

        } else {
            mProgressDialog.setTitle("Sign In!");
            mProgressDialog.setMessage("Please wait while sign in..");
            mProgressDialog.setCanceledOnTouchOutside(true);
            mProgressDialog.show();

            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                String currentUid = mAuth.getCurrentUser().getUid();
//                                String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                send2HomeActivity();
                                Toast.makeText(getApplicationContext(), "User Login successful", Toast.LENGTH_SHORT).show();
                                mProgressDialog.dismiss();
//                                userRef.child(currentUid).child("device_token")
//                                        .setValue(deviceToken)
//                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//
//                                                if(task.isSuccessful()){
//
//                                                    send2HomeActivity();
//                                                    Toast.makeText(getApplicationContext(), "User Login successful", Toast.LENGTH_SHORT).show();
//                                                    mProgressDialog.dismiss();
//                                                }
//                                            }
//                                        });


                            } else {
                                String message = task.getException().toString();
                                Toast.makeText(getApplicationContext(), "User not found!", Toast.LENGTH_SHORT).show();
                                mProgressDialog.dismiss();
                            }

                        }
                    });
        }
    }

    private void send2HomeActivity() {
        Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        if (currentUser != null) {
//            send2HomeActivity();
//        }
//        else {
//            super.onStart();
//        }
    }

    private void updateUI(String userID, String username) {
        mProgressDialog.setTitle("Updating Data");
        mProgressDialog.setMessage("Please wait while updating information");
        mProgressDialog.setCanceledOnTouchOutside(true);
        mProgressDialog.show();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String userid = firebaseUser.getUid();
        rootRef = FirebaseDatabase.getInstance().getReference("MyUsers").child(userid);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("id", userID);
        hashMap.put("username", username);

        rootRef.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                }
                mProgressDialog.dismiss();
            }
        });
    }
}