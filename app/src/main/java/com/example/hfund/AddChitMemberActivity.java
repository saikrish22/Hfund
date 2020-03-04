package com.example.hfund;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddChitMemberActivity extends AppCompatActivity {

    private EditText nameEditText,addressEditText,mobileEditText;
    private Button addBtn;
    private String TAG="AddChitMemberActivity";
    private String docId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chit_member);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        nameEditText=(EditText)findViewById(R.id.memberName);
        mobileEditText=(EditText)findViewById(R.id.memberPhone);
        addressEditText=(EditText)findViewById(R.id.memberAddress);
        addBtn=(Button) findViewById(R.id.addMemberBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name=nameEditText.getText().toString();
                final String mobile= mobileEditText.getText().toString();
                final String address= addressEditText.getText().toString();
                if(name.equals("")||mobile.equals("")||address.equals("")){
                    Snackbar.make(v, "Enter all the fields", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else{
                    SharedPreferences sharedPref =getApplicationContext().getSharedPreferences("Hfund",0);
                    String chitId = sharedPref.getString("chitId", "null");
                    Log.d(TAG,chitId);
                    String id = db.collection("chits").document(chitId).collection("members").document().getId();

                                            FirebaseAuth mAuth=FirebaseAuth.getInstance();
                                            FirebaseUser currentUser=mAuth.getCurrentUser();
                                            // Create a new user with a first and last name
                                            Map<String, Object> chit = new HashMap<>();
                                            chit.put("id",id);
                                            chit.put("name", name);
                                            chit.put("mobile", Long.parseLong(mobile));
                                            chit.put("address",address);
                                            db.collection("chits")
                                                    .document(chitId)
                                                    .collection("members")
                                                    .document(id)
                                                    .set(chit)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d(TAG,"Data Added Successfully");
                                                            Toast.makeText(AddChitMemberActivity.this,"Member Created Successfully",Toast.LENGTH_SHORT).show();
                                                            Intent i1=new Intent(AddChitMemberActivity.this,ChitActivity.class);
                                                            startActivity(i1);
                                                            finish();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d(TAG,"Data not added "+e);
                                                        }
                                                    });
                }

            }
        });

    }
}
