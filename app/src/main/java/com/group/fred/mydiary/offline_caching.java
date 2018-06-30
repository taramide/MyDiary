package com.group.fred.mydiary;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class offline_caching extends Application {

    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    @Override
    public void onCreate() {
        super.onCreate();


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);




    }
}
