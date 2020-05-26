package com.example.hfund;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BalanceFragment extends Fragment {
    private List<Chits> chits=new ArrayList<>();
    private RecyclerView recyclerview;
    private MyAdapter mAdapter;
    private long collected=0,total=0,count;
    private long amount,members,months;
    private Double interest;
    private ArrayList<String> paymentsArray;
    private String chitId;

    private ConstraintLayout main;

    public BalanceFragment() {
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
        final View root=inflater.inflate(R.layout.fragment_balance, container, false);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        final FirebaseUser currentUser = mAuth.getCurrentUser();

        Query query = FirebaseFirestore.getInstance()
                .collection("chits")
                .whereEqualTo("admin",currentUser.getUid());
        total=0;
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //Log.d("BalanceFragment", document.getId() + " => " + document.get("amount"));
                        total=total+Integer.parseInt(document.get("amount").toString());
                    }

                    //Log.d("amount: ",total+"");
                    TextView totaltv=(TextView)root.findViewById(R.id.total);
                    totaltv.setText("Total (All Chits)  :  "+total);
                }
            }
        });

        Query query1 = FirebaseFirestore.getInstance()
                .collection("chits")
                .whereEqualTo("admin",currentUser.getUid());

        collected=0;
        query1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        amount=0;members=0;months=0;interest=0.0;chitId="";
                        //Log.d("BalanceFragment  : ", document.getId() + " => " + document.get("amount"));
                        amount = Long.parseLong(document.get("amount").toString());
                        members = Long.parseLong(document.get("members").toString());
                        months = Long.parseLong(document.get("months").toString());
                        interest = Double.parseDouble(document.get("interest").toString());
                        chitId = document.get("id").toString();

                        db.collection("chits").document(chitId).collection("members")
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                                if (task1.isSuccessful()) {
                                    for (QueryDocumentSnapshot document1 : task1.getResult()) {
                                        paymentsArray = (ArrayList<String>) document1.get("payments");
                                        if(paymentsArray!=null) {
                                            //Log.d("chitId : ",chitId+""+paymentsArray);
                                            count = paymentsArray.size();
                                            collected = collected + (amount / (members * months)) * count;
                                            //Log.d("coll,amt,memb,mon,ct ", collected +" "+amount+" "+members+" "+months+" "+count);
                                            TextView collectedtv = (TextView) root.findViewById(R.id.collected);
                                            collectedtv.setText("Collected  : " + collected);
                                        }
                                    }
                                } else {
                                    Log.d("BalanceFragment : ", "Error getting documents: ", task1.getException());
                                }
                            }
                        });

                    }
                }else{
                    Log.d("BalanceFragment: ", "Error getting documents: ", task.getException());

                }
            }
        });
        return root;
    }
}
