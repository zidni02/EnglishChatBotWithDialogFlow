package com.bae.dialogflowbot;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {
    ActionBar actionBar;

    private TextView loginHereTxt;
    private Button createAccountButton;
    private EditText userEmail, userPass, username, phonenumber;

    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private ImageView passShow, passHide;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initializeFields();
        getSupportActionBar().hide();

        //getActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        actionBar = getSupportActionBar();
        getSupportActionBar().setTitle("Registration");

        //pass show hide
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


        loginHereTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send2LoginActivity();
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });

    }


    //Back navigation
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initializeFields() {
        //firebase initialization
        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        mProgressDialog = new ProgressDialog(this);

        loginHereTxt = findViewById(R.id.login_here_txt);
        createAccountButton = findViewById(R.id.reg_btn);
        userEmail = findViewById(R.id.reg_email_edt_txt);
        userPass = findViewById(R.id.reg_pass_edt_text);
        username = findViewById(R.id.reg_username_edt_txt);
        phonenumber = findViewById(R.id.reg_phone_editTex);
        passHide=findViewById(R.id.passToggleHide);
        passShow=findViewById(R.id.passToggleShow);
    }


    private void createNewAccount() {

        String email = userEmail.getText().toString().trim();
        String pass = userPass.getText().toString().trim();
        String user = username.getText().toString().trim();
        String phone = phonenumber.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            userEmail.setError("Enter your email!");
            return;
        }
        if (TextUtils.isEmpty(user)) {
            userPass.setError("Enter your username!");
        }
        if (TextUtils.isEmpty(phone)) {
            userPass.setError("Enter your phone number!");
        }
        if (userPass.length() <= 6) {
            userPass.setError("Password shouldn't be less than 6");
        } else {
            mProgressDialog.setTitle("Creating new account");
            mProgressDialog.setMessage("Please wait while creating new account..");
            mProgressDialog.setCanceledOnTouchOutside(true);
            mProgressDialog.show();

//            mAuth.createUserWithEmailAndPassword(email, pass)
//                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if (task.isSuccessful()) {
//
//                                String currentUid = mAuth.getCurrentUser().getUid();
////                                String deviceToken = FirebaseInstanceId.getInstance().getToken();
////
////                                rootRef.child("Users").child(currentUid).child("device_token")
////                                        .setValue(deviceToken);
//
//                                String currentUserID = mAuth.getCurrentUser().getUid();
//                                rootRef.child("Users").child(currentUserID).setValue("");
//
//                                Toast.makeText(getApplicationContext(), "Account created Successfully!", Toast.LENGTH_SHORT).show();
//                                mProgressDialog.dismiss();
//                                send2HomeActivity();
//
//                            }
//                            else {
//                                String message = task.getException().toString();
//                                Toast.makeText(getApplicationContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
            mAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                String userid = firebaseUser.getUid();
                                rootRef = FirebaseDatabase.getInstance().getReference("MyUsers").child(userid);
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("id", userid);
                                hashMap.put("username", user);
                                hashMap.put("email", email);
                                hashMap.put("phone number", phone);
                                hashMap.put("password", pass);

                                rootRef.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            startActivity(new Intent(RegistrationActivity.this, HomeActivity.class)
                                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                            finish();
                                            mProgressDialog.dismiss();
                                        }
                                    }
                                });
//                                SharedPreferences.Editor editor = getSharedPreferences("users", MODE_PRIVATE).edit();
//                                editor.putString("personName", user);
//                                editor.apply();

                            } else {
                                Toast.makeText(RegistrationActivity.this, "Inavlid!",
                                        Toast.LENGTH_LONG).show();
                                mProgressDialog.dismiss();
                            }
                        }
                    });
        }
    }

    private void send2LoginActivity() {
        Intent createAccIntent = new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(createAccIntent);
    }

    private void send2HomeActivity() {
        Intent homeIntent = new Intent(RegistrationActivity.this, HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();
    }
}
