package com.example.myprimeraapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {


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

        Intent infoRecibida = getIntent();
        String usuarioCorreo = infoRecibida.getStringExtra("user");
        if (usuarioCorreo == null) {
            SharedPreferences preferences = getSharedPreferences("Credenciales", MODE_PRIVATE);
            usuarioCorreo = preferences.getString("userSP", "");
        }

        android.widget.LinearLayout btnTopPerfil = findViewById(R.id.btn_top_perfil);
        android.widget.TextView txtIniciales = findViewById(R.id.txt_iniciales_main);

        BaseDatosSQLite db = new BaseDatosSQLite(this);
        android.database.Cursor cursor = db.obtenerUsuario(usuarioCorreo);
        if (cursor.moveToFirst()) {
            String nom = cursor.getString(cursor.getColumnIndexOrThrow("nombres"));
            String ape = cursor.getString(cursor.getColumnIndexOrThrow("apellidos"));

            String iniciales = "";
            if(!nom.isEmpty()) iniciales += nom.substring(0, 1);
            if(!ape.isEmpty()) iniciales += ape.substring(0, 1);
            txtIniciales.setText(iniciales.toUpperCase());
        }
        cursor.close();

        btnTopPerfil.setOnClickListener(v -> {
            Intent intentPerfil = new Intent(MainActivity.this, PerfilActivity.class);
            startActivity(intentPerfil);
        });

        BottomNavigationView barraNavegacion = findViewById(R.id.barra_navegacion);

        if (barraNavegacion != null) {
            barraNavegacion.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_inicio) {
                    Toast.makeText(this, "Estás en Inicio", Toast.LENGTH_SHORT).show();

                } else if (id == R.id.nav_explorar) {
                    // Módulo de Palacios
                    Toast.makeText(this, "Ir a Explorar - Módulo de Palacios", Toast.LENGTH_SHORT).show();

                } else if (id == R.id.nav_crear) {
                    // Módulo de Torres
                    Toast.makeText(this, "Ir a Crear Paquete - Módulo de Torres", Toast.LENGTH_SHORT).show();

                } else if (id == R.id.nav_carrito) {
                    // Módulo de Chavez
                    Toast.makeText(this, "Ir a Carrito/Reservas - Módulo de Chavez", Toast.LENGTH_SHORT).show();

                } else if (id == R.id.nav_social) {
                    // Módulo de Noboa
                    Toast.makeText(this, "Ir a Comunidad Social - Módulo de Noboa", Toast.LENGTH_SHORT).show();
                }

                return true;
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuPrincipal = getMenuInflater();
        menuPrincipal.inflate(R.menu.menuprincipal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.mp_consultausuario) {
            Intent intent = new Intent(this, Registrarse.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.mp_acercade) {
            Toast.makeText(this, "Acerca de", Toast.LENGTH_SHORT).show();
        }
        if (item.getItemId() == R.id.mp_ayuda) {
            Toast.makeText(this, "Ayuda", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
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