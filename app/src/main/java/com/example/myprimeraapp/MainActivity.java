package com.example.myprimeraapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    TextView lblmensaje;
    Button btnBorrarDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        lblmensaje = findViewById(R.id.main_lblprincipal);
        btnBorrarDatos = findViewById(R.id.btn_borrar_datos);

        Intent infoRecibida = getIntent();
        String usuario = infoRecibida.getStringExtra("user");

        if (usuario == null) {
            SharedPreferences preferences = getSharedPreferences("Credenciales", MODE_PRIVATE);
            usuario = preferences.getString("userSP", "Viajero");
        }

        lblmensaje.setText("Hola, " + usuario);
        btnBorrarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borrarPreferenciasYSalir();
            }
        });
    }

    private void borrarPreferenciasYSalir() {
        SharedPreferences preferences = getSharedPreferences("Credenciales", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.clear();
        editor.apply();

        Toast.makeText(this, "Datos borrados. Redirigiendo...", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);

        finish();
    }
}