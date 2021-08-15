package com.example.loginfirebasemail77;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.loginfirebasemail77.modelos.sensores;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class informaciondispositivo extends AppCompatActivity {

    List<sensores> list=new ArrayList<>();
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String mac="2C:F4:32:19:78:F6";
    TextView DistanciaDerecha;
    TextView DistanciaFrente;
    TextView DistanciaIzquierda;
    TextView cayo;
    Switch estado;
    Button bntGraficas;
    ImageView imgView;
    String idUsuario;
    TextView  nombres, genero, mac2, nombredispo,nombreTutor, fecha;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.informacion_dispositivo);
        inicializarFirebase();
        informacionSensores();
        informacionUbicacion();
        idUsuario=getIntent().getExtras().getString("idUsuario");
        nombres=findViewById(R.id.txtPerfilNombre);
        genero=findViewById(R.id.TxtPerfilGenero);
        mac2=findViewById(R.id.txtPerfilMacdispositivo);
        nombredispo=findViewById(R.id.txtPerfilDeciveName);
        imgView=findViewById(R.id.imgFotoPaciente);
        fecha=findViewById(R.id.TxtPerfilNacimiento);
        estado=findViewById(R.id.switch1);
        bntGraficas=findViewById(R.id.bntGraficas);

        fecha.setText(getIntent().getExtras().getString("fecha"));
        nombres.setText(getIntent().getExtras().getString("nombres"));
        genero.setText(getIntent().getExtras().getString("genero"));
        mac2.setText(getIntent().getExtras().getString("mac"));
        nombredispo.setText(getIntent().getExtras().getString("nombredispositivo"));
        Picasso.get().load(getIntent().getExtras().getString("img")).resize(100,100).centerCrop().into(imgView);

        bntGraficas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(informaciondispositivo.this,graficas.class);
                i.putExtra("idUsuario",idUsuario);
                startActivity(i);
            }
        });

    }

    private void informacionUbicacion() {
        databaseReference.child("ubicaciones").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot objShaptshot : snapshot.getChildren())
                {
                    TextView coox= findViewById(R.id.latitude);
                    TextView cooY= findViewById(R.id.longitud);
                    coox.setText(objShaptshot.child("coorX").getValue().toString().substring(0,10));
                    cooY.setText(objShaptshot.child("coorY").getValue().toString().substring(0,10));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("elementos: "+error.getMessage());
            }
        });
    }
    public  void goReconocimientoTimeReal(View view)
    {
        Intent i = new Intent(informaciondispositivo.this,reconocimientoTimeReal.class);
        startActivity(i);
    }
    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        DistanciaDerecha=findViewById(R.id.txtSensorDerecho);
        DistanciaFrente=findViewById(R.id.txtSensorFrontal);
        DistanciaIzquierda=findViewById(R.id.txtSensorIzquierdo);
        cayo=findViewById(R.id.txtCaida);
    }
    private void informacionSensores() {

        databaseReference.child("Sensores").orderByKey().equalTo("2C:F4:32:19:78:F6").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot objShaptshot : snapshot.getChildren())
                {
                    System.out.println("objShaptshot : "+objShaptshot);
                    DistanciaDerecha.setText(objShaptshot.child("DistanciaDerecha").getValue().toString()+"\n cm");
                    DistanciaFrente.setText(objShaptshot.child("DistanciaFrente").getValue().toString()+"\n cm");
                    DistanciaIzquierda.setText(objShaptshot.child("DistanciaIzquierda").getValue().toString()+"\n cm");
                    String estado=(objShaptshot.child("cayo").getValue().toString());

                    if(estado.equals("sigue de pie"))
                    {
                        cayo.setText("Buen estado");
                    }else
                    {
                        cayo.setText("Alerta");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("elementos: "+error.getMessage());
            }
        });
    }

}