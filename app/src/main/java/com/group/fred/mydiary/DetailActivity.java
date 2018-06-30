package com.group.fred.mydiary;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailActivity extends AppCompatActivity {
    Toolbar mToolbar;
    ImageView btnEdit,btnDelete;
    String id;
    TextView txtEntryTitle,txtEntry,txtCreated,txtLastUpdated;
    DatabaseReference notesRef;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        progressBar = findViewById(R.id.progressBar3);

        mToolbar = (Toolbar)findViewById(R.id.detail_page_bar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = layoutInflater.inflate(R.layout.detail_custom,null);
        actionBar.setCustomView(action_bar_view);

        id = getIntent().getStringExtra("note_id");

        btnDelete = findViewById(R.id.delete);
        btnEdit = findViewById(R.id.edit);
        txtEntryTitle = findViewById(R.id.showHeader);
        txtEntry = findViewById(R.id.showEntry);
        txtCreated = findViewById(R.id.showCreated);
        txtLastUpdated = findViewById(R.id.showUpdated);



        mAuth = FirebaseAuth.getInstance();
        notesRef = FirebaseDatabase.getInstance().getReference().child("Notes").child(mAuth.getUid()).child(id);

        notesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String header = (String) dataSnapshot.child("header").getValue();
                String entry = (String) dataSnapshot.child("subject").getValue();
                String created = (String) dataSnapshot.child("created").getValue();
                String last_updated = (String) dataSnapshot.child("last_updated").getValue();

                txtEntryTitle.setText(header);
                txtEntry.setText(entry);
                txtCreated.setText(created);
                txtLastUpdated.setText(last_updated);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this,EditActivity.class);
                intent.putExtra("note_id",id);
                startActivity(intent);
            }
        });


        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(DetailActivity.this);
                dialog.setTitle("Delete Note");
                dialog.setMessage("Are you sure you want to delete Note??");
                dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        progressBar.setVisibility(View.VISIBLE);
                        deleteNote();


                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                dialog.show();
            }
        });

    }

    private void deleteNote() {
        notesRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(DetailActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(DetailActivity.this,HomeActivity.class));

                }

            }
        });
    }
}
