package com.example.hfund;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class AddChitFragment extends Fragment {
    private EditText nameEditText,amountEditText,interestEditText,membersEditText,monthsEditText;
    private Button addChatButton;
    private String TAG="AddChitFragment";
    @ServerTimestamp
    Date timestamp;

    public AddChitFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root=inflater.inflate(R.layout.fragment_addchit, container, false);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

       nameEditText=(EditText)root.findViewById(R.id.name);
       amountEditText=(EditText)root.findViewById(R.id.amount);
       interestEditText=(EditText)root.findViewById(R.id.interest);
       membersEditText=(EditText)root.findViewById(R.id.members);
       monthsEditText=(EditText)root.findViewById(R.id.months);
       addChatButton=(Button)root.findViewById(R.id.addChitBtn);
       addChatButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String name=nameEditText.getText().toString();
               String amount= amountEditText.getText().toString();
               String interest= interestEditText.getText().toString();
               String members= membersEditText.getText().toString();
               String months=monthsEditText.getText().toString();
               if(name.equals("")||amount.equals("")||interest.equals("")||members.equals("")||months.equals("")){
                   Snackbar.make(v, "Enter all the fields", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
               }
               else{

                   FirebaseAuth mAuth=FirebaseAuth.getInstance();
                   FirebaseUser currentUser=mAuth.getCurrentUser();
                   // Create a new user with a first and last name
                   Map<String, Object> chit = new HashMap<>();
                   chit.put("admin",currentUser.getUid() );
                   chit.put("name", name);
                   chit.put("amount", Long.parseLong(amount));
                   chit.put("interest",Double.parseDouble(interest));
                   chit.put("members",Long.parseLong(members));
                   chit.put("months",Long.parseLong(months));
                   chit.put("createdOn",new Timestamp(new Date()));
                   chit.put("withDraws", Arrays.asList());
                   db.collection("chits")
                           .document()
                           .set(chit)
                           .addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   Log.d(TAG,"Data Added Successfully");
                                   Toast.makeText(getContext(),"Chit Created Successfully",Toast.LENGTH_SHORT).show();
                                   final FragmentTransaction ft = getFragmentManager().beginTransaction();
                                   ft.replace(R.id.nav_host_fragment, new ChitsFragment(), "NewFragmentTag");
                                   ft.addToBackStack(null);
                                   ft.commit();
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
        return root;
    }
}
