package com.example.loginfirebasemail77;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class cuentapaciente extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cuentapaciente);
    }
    public void goIngresar(View view)
    {
        Intent i = new Intent(cuentapaciente.this,registroReconocimiento.class);
        startActivity(i);
    }
}