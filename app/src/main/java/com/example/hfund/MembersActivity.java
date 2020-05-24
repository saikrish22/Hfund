package com.example.hfund;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class MembersActivity extends AppCompatActivity {
   // private List<ChitUser> chitUser=new ArrayList<>();
    private RecyclerView recyclerview;
    FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);

        FloatingActionButton fab = findViewById(R.id.addMember);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //       .setAction("Action", null).show();

                Intent intent=new Intent(MembersActivity.this,AddChitMemberActivity.class);
                startActivity(intent);


            }
        });
        SharedPreferences sharedPref =getApplicationContext().getSharedPreferences("Hfund",0);
        String chitId = sharedPref.getString("chitId", "null");
        String chitName=sharedPref.getString("chitName","null");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("chits").document(chitId).collection("members");

        FirestoreRecyclerOptions<ChitUser> options = new FirestoreRecyclerOptions.Builder<ChitUser>()
                .setQuery(query, ChitUser.class)
                .build();

         adapter = new FirestoreRecyclerAdapter<ChitUser, ChitUserHolder>(options) {
             @Override
             protected void onBindViewHolder(@NonNull ChitUserHolder holder, int position, @NonNull ChitUser model) {

                 holder.setName(model.getName());
             }

            @Override
            public ChitUserHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.single_chituser_item, group, false);
                view.setBackgroundColor(ContextCompat.getColor(group.getContext(),R.color.backgroundListItem));

                return new ChitUserHolder(view);
            }
        };
        recyclerview=(RecyclerView)findViewById(R.id.recycler_view_members);
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
    public static class ChitUserHolder extends RecyclerView.ViewHolder{

        View mView;

        public ChitUserHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setName(String name){
            TextView userNameView =(TextView)mView.findViewById(R.id.chitUserName);
            userNameView.setText(name);
        }

    }
}
