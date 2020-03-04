package com.example.hfund;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class UpdatePayActivity extends AppCompatActivity {

    private RecyclerView recyclerview;
    FirestoreRecyclerAdapter adapter;
    String chitId;
    String TAG="UpdatePayActivity";
    ArrayList<String> paidStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pay);

        SharedPreferences sharedPref =getApplicationContext().getSharedPreferences("Hfund",0);
        chitId = sharedPref.getString("chitId", "null");
        String chitName=sharedPref.getString("chitName","null");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("chits").document(chitId).collection("members");

        FirestoreRecyclerOptions<ChitUser> options = new FirestoreRecyclerOptions.Builder<ChitUser>()
                .setQuery(query, ChitUser.class)
                .build();

        final Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.get(Calendar.YEAR);
        cal.get(Calendar.MONTH);

        final DocumentReference docRef = db.collection("chits").document(chitId);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.get(cal.get(Calendar.YEAR)+""+(cal.get(Calendar.MONTH)+1)));
                    paidStatus=(ArrayList<String>)snapshot.get(cal.get(Calendar.YEAR)+""+(cal.get(Calendar.MONTH)+1));
                } else {
                    Log.d(TAG, "Current data:null");
                }
            }
        });

        adapter = new FirestoreRecyclerAdapter<ChitUser, UpdatePayActivity.ChitUserHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UpdatePayActivity.ChitUserHolder holder, int position, @NonNull ChitUser model) {
                int f=0;
                try {
                    for (String s : paidStatus) {
                        if (s.equals(model.getId())) {
                            f = 1;
                            holder.setName(model.getName(),model.getId(),true);
                            break;
                        }
                    }
                }
                catch (Exception e){
                    Log.d("Exception :",e.getMessage());
                }
                if (f == 0) holder.setName(model.getName(), model.getId(),false);
            }

            @Override
            public UpdatePayActivity.ChitUserHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.single_chituser_status, group, false);

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

        public void setName(String name, final String id,Boolean status){
            TextView userNameView =(TextView)mView.findViewById(R.id.chitUserName);
            final Button payBtn=(Button)mView.findViewById(R.id.payBtn);
            userNameView.setText(name);
            if(status){
                payBtn.setEnabled(false);
                payBtn.setTextColor(Color.GRAY);
            }
            else {
                payBtn.setEnabled(true);
                payBtn.setTextColor(Color.BLUE);
            }
            payBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("chits").document(chitId)
                            .update(calendar.get(Calendar.YEAR)+""+(calendar.get(Calendar.MONTH)+1), FieldValue.arrayUnion(id))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("paid status","paid status updated successfully");
                                    payBtn.setEnabled(false);
                                    payBtn.setTextColor(Color.GRAY);
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
