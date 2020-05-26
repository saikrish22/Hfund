package com.example.hfund;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class UpdatePayActivity extends AppCompatActivity {

    private RecyclerView recyclerview;
    FirestoreRecyclerAdapter adapter;
    String chitId;
    String TAG="UpdatePayActivity";
    ArrayList<String> paidStatus;
    long members,months,amount,emi=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pay);

        SharedPreferences sharedPref =getApplicationContext().getSharedPreferences("Hfund",0);
        chitId = sharedPref.getString("chitId", "null");
        String chitName=sharedPref.getString("chitName","null");
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("chits").document(chitId).collection("members");

        FirestoreRecyclerOptions<ChitUser> options = new FirestoreRecyclerOptions.Builder<ChitUser>()
                .setQuery(query, ChitUser.class)
                .build();

        final Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.get(Calendar.YEAR);
        cal.get(Calendar.MONTH);

        TextView t=(TextView)findViewById(R.id.text);
        String mon[]={"Jan","Feb","Mar","Apr","May","June","July","Aug","Sept","Oct","Nov","Dec"};
        t.setText("Payment Status for : " +mon[cal.get(Calendar.MONTH)]+"/"+cal.get(Calendar.YEAR));

        DocumentReference docRef2 = db.collection("chits").document(chitId);
        docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        amount=Long.parseLong(document.get("amount").toString());
                        members=Long.parseLong(document.get("members").toString());
                        months=Long.parseLong(document.get("months").toString());

                        emi=(amount)/(members*months);
                        Log.d(TAG, "amount,emi : " + amount+" "+emi);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        adapter = new FirestoreRecyclerAdapter<ChitUser, UpdatePayActivity.ChitUserHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final UpdatePayActivity.ChitUserHolder holder, int position, @NonNull final ChitUser model) {
                Log.d("...kk : ",model.getId());
                DocumentReference docRef = db.collection("chits").document(chitId).collection("members").document(model.getId());
                docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                                paidStatus = (ArrayList<String>) snapshot.get("payments");
                                if(paidStatus==null){
                                    holder.setName(model.getName(), model.getId(), false,emi);
                                    Log.d(TAG, "payment not found : "+model.getName());
                                }
                                else if(paidStatus.contains(cal.get(Calendar.YEAR) + "" + (cal.get(Calendar.MONTH) + 1))){
                                    holder.setName(model.getName(), model.getId(), true,emi);
                                    Log.d(TAG, "payment found");
                                }else{
                                    holder.setName(model.getName(), model.getId(), false,emi);
                                    Log.d(TAG, "payment not found : "+model.getName());
                                }
                        } else {
                            Log.d(TAG, "Current data:null");
                        }
                    }
                });

            }

            @Override
            public UpdatePayActivity.ChitUserHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.single_chituser_status, group, false);
                view.setBackgroundColor(ContextCompat.getColor(group.getContext(),R.color.backgroundListItem));

                return new UpdatePayActivity.ChitUserHolder(view);
            }
        };
        recyclerview=(RecyclerView)findViewById(R.id.recycler_view_members_payment_status);
        RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManger);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
    public class ChitUserHolder extends RecyclerView.ViewHolder{

        View mView;

        public ChitUserHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setName(String name, final String id, Boolean status, final long emi){
            Log.d("emi  :",String.valueOf(emi));
            TextView userNameView =(TextView)mView.findViewById(R.id.chitUserName);
            final Button payBtn=(Button)mView.findViewById(R.id.payBtn);
            userNameView.setText(name);
            if(status){
                payBtn.setEnabled(false);
                payBtn.setBackgroundColor(Color.GREEN);
                payBtn.setText("PAID "+emi);
            }
            else {
                payBtn.setEnabled(true);
                payBtn.setBackgroundColor(Color.RED);
                payBtn.setText("PAY "+emi);
            }
            payBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("chits").document(chitId)
                            .collection("members")
                            .document(id)
                            .update("payments", FieldValue.arrayUnion(calendar.get(Calendar.YEAR)+""+(calendar.get(Calendar.MONTH)+1)))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("paid status","paid status updated successfully");
                                    Toast.makeText(UpdatePayActivity.this,"paid status updated.",Toast.LENGTH_SHORT).show();
                                    payBtn.setEnabled(false);
                                    payBtn.setText("paid "+emi);
                                    payBtn.setBackgroundColor(Color.GREEN);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Log.d("paid status","not updated"+e);
                                }
                            });

                }
            });
        }

    }
}
