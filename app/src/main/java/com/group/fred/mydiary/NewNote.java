package com.group.fred.mydiary;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

public class NewNote extends AppCompatActivity {
    Toolbar mToolbar;
    EditText txtEntryHeader,txtEntry;
    DatabaseReference notesRef;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        mToolbar = (Toolbar)findViewById(R.id.new_note_bar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = layoutInflater.inflate(R.layout.new_custom,null);
        actionBar.setCustomView(action_bar_view);

        ImageView btnSave = findViewById(R.id.save);
        TextView titleText = findViewById(R.id.txt);
        titleText.setText("New Note");
        txtEntryHeader = findViewById(R.id.txtTitle);
        txtEntry = findViewById(R.id.txtDetails);
        mAuth = FirebaseAuth.getInstance();
        notesRef = FirebaseDatabase.getInstance().getReference().child("Notes").child(mAuth.getUid());


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        });



    }

    private void saveNote() {
        String title = txtEntryHeader.getText().toString();
        String details = txtEntry.getText().toString();
        String date = String.valueOf(Calendar.getInstance().getTime());
        String created[] = date.split("G");
        String c = created[0] + String.valueOf(Calendar.getInstance().get(Calendar.YEAR));


        if (title.isEmpty()){
            Toast.makeText(this, "Please enter Title", Toast.LENGTH_SHORT).show();
            txtEntryHeader.requestFocus();
        }
        else if (details.isEmpty()){
            Toast.makeText(this, "Please enter Subject", Toast.LENGTH_SHORT).show();
            txtEntry.requestFocus();
        }
        else {
            Note note = new Note(title,details, c,c);
            String key = notesRef.push().getKey();
            notesRef.child(key).setValue(note).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(NewNote.this, "Added Note", Toast.LENGTH_SHORT).show();
                        txtEntry.setText("");
                        txtEntryHeader.setText("");
                    }
                    else {
                        Toast.makeText(NewNote.this, "Error occured", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }
}
