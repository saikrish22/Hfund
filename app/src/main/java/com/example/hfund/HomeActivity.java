package com.example.hfund;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth mAuth;

    private String TAG="HomeActivity";
    ProgressDialog mProgressDialog;
    FirebaseFirestore db;
    private FirebaseUser currentUser;

    private TextView usernameText,emailText;
    private  View navHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_balance,R.id.history)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        mAuth=FirebaseAuth.getInstance();

        navHeader = navigationView.getHeaderView(0);
        usernameText=(TextView)navHeader.findViewById(R.id.username);
        emailText=(TextView)navHeader.findViewById(R.id.email);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            usernameText.setText(user.getDisplayName());
            emailText.setText(user.getEmail());
        }
        /*  APP UPDATE CODE */

        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        DocumentReference docRef = db.collection("app").document("current_version");

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            String app_version_code= Objects.requireNonNull(document.get("app_version_code")).toString();
                            String app_version_name= Objects.requireNonNull(document.get("app_version_name")).toString();
                            String app_link= Objects.requireNonNull(document.get("app_link")).toString();
                            Log.d("remote_app_version : ",app_version_code+"."+app_version_name);
                            try {
                                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                                if ((Integer.parseInt(app_version_code) == pInfo.versionCode && Double.parseDouble(app_version_name) > Double.parseDouble(pInfo.versionName))
                                        ||Integer.parseInt(app_version_code) >pInfo.versionCode ) {
                                    Log.d("Update Message : ", "App Update Available");
                                    Toast.makeText(HomeActivity.this, "App Update Available", Toast.LENGTH_SHORT).show();
                                    try {
                                        FirebaseStorage storage = FirebaseStorage.getInstance();
                                        StorageReference storageRef = storage.getReferenceFromUrl(app_link);

                                        String fileName = "Hfund.apk";
                                        File dir = getFilesDir();
                                        final File file = new File(dir, fileName);

                                        final Uri uri = FileProvider.getUriForFile(HomeActivity.this, "com.example.hfund.fileprovider", file);

                                        mProgressDialog = new ProgressDialog(HomeActivity.this);
                                        mProgressDialog.setMessage("App Update Available Downloading...");
                                        mProgressDialog.setIndeterminate(true);
                                        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                        mProgressDialog.setCancelable(true);
                                        mProgressDialog.show();
                                        storageRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                Log.d("firebase ", ";local tem file created " + file.toString());
                                                Toast.makeText(HomeActivity.this, "App Update Downloaded Ready for Installation.", Toast.LENGTH_LONG).show();
                                                mProgressDialog.dismiss();
                                                AppUpdate appUpdate = new AppUpdate();
                                                appUpdate.updateApp(HomeActivity.this, uri);

                                                Toast.makeText(HomeActivity.this,"App Updated Successfully",Toast.LENGTH_LONG).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                Toast.makeText(HomeActivity.this, "App Downloading Failed.", Toast.LENGTH_SHORT).show();
                                                Log.d("firebase ", ";local tem file not created " + exception.toString());
                                            }
                                        });
                                    } catch (Exception e) {
                                        Log.d(TAG, e.toString());
                                    }
                                }
                                String version = pInfo.versionCode + "." + pInfo.versionName;
                                Log.d("versions", "app_verison : " + version + " remote_app_version : " + Integer.parseInt(app_version_code) + "." + Double.parseDouble(app_version_name));
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });



        /*  END OF APP UPDATE */

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                Toast.makeText(HomeActivity.this,"Logging Out... ",Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
                return true;
            case R.id.action_version:
                Intent aboutIntent = new Intent(HomeActivity.this, AboutActivity.class);
                startActivity(aboutIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        if(currentUser == null) {
            Intent mainIntent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(mainIntent);
            finish();
        }
    }
}
