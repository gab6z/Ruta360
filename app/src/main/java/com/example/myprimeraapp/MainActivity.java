package com.example.myprimeraapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.app.AlertDialog;


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

            PopupMenu popupMenu = new PopupMenu(MainActivity.this, btnTopPerfil);
            popupMenu.getMenuInflater().inflate(R.menu.menuprincipal, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.mp_perfil) {
                    Intent intent = new Intent(this, PerfilActivity.class);
                    startActivity(intent);
                    return true;
                }
                if (id == R.id.mp_acercade) {
                    mostrarAcercaDe();
                    return true;
                }
                if (id == R.id.mp_ayuda) {
                    mostrarAyuda();
                    return true;
                }
                if (item.getItemId() == R.id.mp_cerrar_sesion) {
                    borrarPreferenciasYSalir();
                    return true;
                }
                return false;
            });
            popupMenu.show();
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

        if (item.getItemId() == R.id.mp_perfil) {
            Intent intent = new Intent(this, PerfilActivity.class);
            startActivity(intent);
            return true;
        }
        if (item.getItemId() == R.id.mp_acercade) {
            mostrarAcercaDe();
            return true;
        }
        if (item.getItemId() == R.id.mp_ayuda) {
            mostrarAyuda();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void mostrarAcercaDe() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_acercade, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        Button btnCerrar = view.findViewById(R.id.btnCerrar);
        btnCerrar.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void mostrarAyuda()
    {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        View view =
                getLayoutInflater().inflate(
                        R.layout.dialog_ayuda,
                        null
                );
        builder.setView(view);
        AlertDialog dialog = builder.create();
        Button btnCerrarAyuda =
                view.findViewById(R.id.btnCerrarAyuda);
        btnCerrarAyuda.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
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