package com.example.hfund;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView registerButton;
    private ProgressBar loadingProgressBar;
    private FirebaseAuth mAuth;
    private String TAG="LoginActivity";
    private String email,password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText =(EditText)findViewById(R.id.email);
        passwordEditText = (EditText)findViewById(R.id.password);
        loginButton =(Button)findViewById(R.id.loginBtn);
        loadingProgressBar = (ProgressBar)findViewById(R.id.loading);
        registerButton = (TextView) findViewById(R.id.register);
        mAuth = FirebaseAuth.getInstance();


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("LoginActivity","register button clicked");
                Intent registerIntent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email=emailEditText.getText().toString();
                password=passwordEditText.getText().toString();
                Log.d(TAG,"email : "+email);
                if(email.equals("")||password.equals("")){
                    Toast.makeText(LoginActivity.this, "Please fill the details.",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    loadingProgressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if(user.isEmailVerified()){
                                            Toast.makeText(LoginActivity.this, "Authentication success.",
                                                    Toast.LENGTH_SHORT).show();
                                            Intent mainIntent = new Intent(LoginActivity.this, HomeActivity.class);
                                            startActivity(mainIntent);
                                            finish();
                                        }
                                        else{
                                            Toast.makeText(LoginActivity.this, "Please Verify Your Email for Authentication.",
                                                    Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(LoginActivity.this, "Authentication failed." + task.getException(),
                                                Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
            }
        });
    }

}
