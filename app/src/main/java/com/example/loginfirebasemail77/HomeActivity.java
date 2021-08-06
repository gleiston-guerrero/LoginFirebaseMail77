package com.example.loginfirebasemail77;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    Button btn_cerrar_sesion;
    TextView email;
    String idUsuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        email=findViewById(R.id.email);
        btn_cerrar_sesion=findViewById(R.id.btn_cerrar);
        String valor = getIntent().getExtras().getString("userName");
        idUsuario= getIntent().getExtras().getString("id_usuario");
        email.setText(valor);
        btn_cerrar_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(HomeActivity.this, "Sesión Cerrada", Toast.LENGTH_SHORT).show();
                goLogin();
            }
        });
    }
    public void goLista(View view)
    {
        Intent i = new Intent(HomeActivity.this,listapacientes.class);
        i.putExtra("idUsuario",idUsuario);
        startActivity(i);
    }
    public void goMapa(View view)
    {
        Intent i = new Intent(HomeActivity.this,mapa.class);
        i.putExtra("idUsuario",idUsuario);
        startActivity(i);
    }
    public void goReconocimientoIdioma(View view)
    {
        Intent i = new Intent(HomeActivity.this,reconocerIdioma.class);
        i.putExtra("idUsuario",idUsuario);
        startActivity(i);
    }
    public void addPaciente(View view)
    {
        Intent i = new Intent(this,registrarpaciente.class);
        i.putExtra("idUsuario",idUsuario);
        startActivity(i);
    }
    public void goLogin()
    {
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
    }
}