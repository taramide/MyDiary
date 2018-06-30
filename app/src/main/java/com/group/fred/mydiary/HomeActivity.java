package com.group.fred.mydiary;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class HomeActivity extends AppCompatActivity {
    RecyclerView notesList;
    DatabaseReference notesReference;
    ProgressBar progressBar;
    Toolbar mToolbar;
    TextView showNoneAlert;
    FirebaseRecyclerAdapter<Note, NotesViewHolder> adapter1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            finish();
            startActivity(new Intent(HomeActivity.this,MainActivity.class));

        }

        mToolbar = (Toolbar)findViewById(R.id.home_page_bar);
        mToolbar.setTitle("My Diary");
        setSupportActionBar(mToolbar);




        progressBar = findViewById(R.id.progressBar4);
        showNoneAlert = findViewById(R.id.txtNoAlert);

        notesList = findViewById(R.id.notesList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(HomeActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        notesList.setLayoutManager(linearLayoutManager);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        notesList.setItemAnimator(itemAnimator);
        notesReference = FirebaseDatabase.getInstance().getReference().child("Notes").child(FirebaseAuth.getInstance().getUid());
        notesReference.keepSynced(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, NewNote.class));
            }
        });

        setUp();
    }


    public void setUp() {
         adapter1 = new FirebaseRecyclerAdapter<Note, NotesViewHolder>(Note.class, R.layout.notes_row
                , NotesViewHolder.class, notesReference) {

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                progressBar.setVisibility(View.GONE);
                if (adapter1.getItemCount() < 1){
                    showNoneAlert.setVisibility(View.VISIBLE);
                }
                else {
                    showNoneAlert.setVisibility(View.GONE);
                }
            }

            @Override
            protected void populateViewHolder(final NotesViewHolder viewHolder, final Note model, int position) {
                final String id = getRef(position).getKey();
                viewHolder.setHeader(model.getHeader());
                viewHolder.setSubject(model.getSubject());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(HomeActivity.this,DetailActivity.class);
                        intent.putExtra("note_id",id);
                        startActivity(intent);
                    }
                });

                viewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(HomeActivity.this,EditActivity.class);
                        intent.putExtra("note_id",id);
                        startActivity(intent);
                    }
                });

                viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(HomeActivity.this);
                        dialog.setTitle("Delete Note");
                        dialog.setMessage("Are you sure you want to delete Note??");
                        dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                progressBar.setVisibility(View.VISIBLE);
                                deleteNote(id);


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
        };
        notesList.setAdapter(adapter1);
    }

    private void deleteNote(String id) {
        notesReference.child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    adapter1.notifyDataSetChanged();
                    Toast.makeText(HomeActivity.this, "Deleted", Toast.LENGTH_SHORT).show();


                }

            }
        });
    }

    public static class NotesViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView txtHeader,txtSubject;
        ImageView btnEdit,btnDelete;

        public NotesViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            btnDelete = mView.findViewById(R.id.btnDelete);
            btnEdit = mView.findViewById(R.id.btnEdit);
        }


        public void setHeader(String header){
            txtHeader = mView.findViewById(R.id.txtHeader);
            txtHeader.setText(header);
        }

        public void setSubject(String subject){
            txtSubject = mView.findViewById(R.id.txtSubject);
            txtSubject.setText(subject);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.main_btn_logout){
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            finish();
                            startActivity(new Intent(HomeActivity.this,MainActivity.class));
                        }
                    });

        }
        return true;
    }
}