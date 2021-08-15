package com.example.loginfirebasemail77;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class reconocimientoTimeReal extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reconocimiento_time_real);
        inicializarFirebase();
        esp32cam();
    }
    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
    }

    private void esp32cam() {
        databaseReference.child("esp32cam").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot objShaptshot : snapshot.getChildren())
                {
                    System.out.println("objShaptshot : "+objShaptshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("elementos: "+error.getMessage());
            }
        });
    }
}