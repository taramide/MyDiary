package com.group.fred.mydiary;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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

import java.util.Calendar;

public class EditActivity extends AppCompatActivity {
    String id;
    DatabaseReference notesRef;
    Toolbar mToolbar;
    EditText txtEntryTitle,txtEntry;
    FirebaseAuth mAuth;
    String created;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        mToolbar = (Toolbar)findViewById(R.id.edt_page_bar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = layoutInflater.inflate(R.layout.new_custom,null);
        actionBar.setCustomView(action_bar_view);

        id = getIntent().getStringExtra("note_id");

        ImageView btnSave = findViewById(R.id.save);
        TextView titleText = findViewById(R.id.txt);
        titleText.setText("Edit Note");
        txtEntry = findViewById(R.id.edtTxtDetails);
        txtEntryTitle = findViewById(R.id.edtTxtTitle);
        progressBar = findViewById(R.id.progressBar3);



        mAuth = FirebaseAuth.getInstance();
        notesRef = FirebaseDatabase.getInstance().getReference().child("Notes").child(mAuth.getUid()).child(id);

        notesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String header = (String) dataSnapshot.child("header").getValue();
                String entry = (String) dataSnapshot.child("subject").getValue();
                created = (String) dataSnapshot.child("created").getValue();


                txtEntryTitle.setText(header);
                txtEntry.setText(entry);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = txtEntryTitle.getText().toString();
                String details = txtEntry.getText().toString();

                if (title.isEmpty()){
                    Toast.makeText(EditActivity.this, "Please enter Title", Toast.LENGTH_SHORT).show();

                }
                else if (details.isEmpty()){

                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    String date = String.valueOf(Calendar.getInstance().getTime());
                    String createds[] = date.split("G");
                    String c = createds[0] + String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
                    Note note = new Note(title,details, created,c);
                    notesRef.setValue(note).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(EditActivity.this, "Updated Note", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(EditActivity.this,HomeActivity.class));
                            }
                            else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(EditActivity.this, "Error occured", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });
    }
}
