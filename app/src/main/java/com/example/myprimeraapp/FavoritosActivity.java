package com.example.myprimeraapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class FavoritosActivity extends AppCompatActivity {

    private RecyclerView recyclerFavoritos;
    private ArrayList<Destino> listaFavoritos;
    private DestinoAdapter adapter;
    private BaseDatosSQLite baseDatos;

    private String usuarioCorreo;
    private TextView txtIniciales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favoritos);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main),
                (v, insets) -> {
                    v.setPadding(
                            insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                            insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                            insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                            insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
                    );
                    return insets;
                });

        // =========================
        // USUARIO REAL
        // =========================
        Intent intent = getIntent();
        usuarioCorreo = intent.getStringExtra("user");

        if (usuarioCorreo == null || usuarioCorreo.isEmpty()) {
            SharedPreferences prefs =
                    getSharedPreferences("Credenciales", MODE_PRIVATE);

            usuarioCorreo = prefs.getString("userSP", "");
        }

        // =========================
        // DB
        // =========================
        baseDatos = new BaseDatosSQLite(this);

        // =========================
        // AVATAR (FIX REAL: NOMBRE + APELLIDO)
        // =========================
        txtIniciales = findViewById(R.id.txtIniciales);

        String iniciales = "U";

        Cursor cursorUser = baseDatos.obtenerUsuario(usuarioCorreo);

        if (cursorUser != null && cursorUser.moveToFirst()) {

            String nombre = cursorUser.getString(cursorUser.getColumnIndexOrThrow("nombres"));
            String apellido = cursorUser.getString(cursorUser.getColumnIndexOrThrow("apellidos"));

            String ini = "";

            if (nombre != null && !nombre.isEmpty()) {
                ini += nombre.charAt(0);
            }

            if (apellido != null && !apellido.isEmpty()) {
                ini += apellido.charAt(0);
            }

            if (!ini.isEmpty()) {
                iniciales = ini.toUpperCase();
            }

            cursorUser.close();
        }

        txtIniciales.setText(iniciales);

        // =========================
        // RECYCLER
        // =========================
        recyclerFavoritos = findViewById(R.id.recyclerFavoritos);
        recyclerFavoritos.setLayoutManager(new LinearLayoutManager(this));

        listaFavoritos = new ArrayList<>();

        adapter = new DestinoAdapter(listaFavoritos, usuarioCorreo);
        recyclerFavoritos.setAdapter(adapter);

        cargarFavoritos();

        // =========================
        // NAVBAR
        // =========================
        BottomNavigationView barraNavegacion = findViewById(R.id.barra_navegacion);

        barraNavegacion.setSelectedItemId(R.id.nav_favoritos);

        barraNavegacion.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            }

            if (id == R.id.nav_explorar) {
                startActivity(new Intent(this, ExplorarActivity.class));
                finish();
                return true;
            }

            if (id == R.id.nav_favoritos) {
                return true;
            }

            if (id == R.id.nav_carrito) {
                startActivity(new Intent(this, resumen_reserva.class));
                finish();
                return true;
            }

            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarFavoritos();
    }

    private void cargarFavoritos() {

        listaFavoritos.clear();

        Cursor cursor = baseDatos.obtenerFavoritos(usuarioCorreo);

        if (cursor != null && cursor.moveToFirst()) {

            do {
                listaFavoritos.add(new Destino(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getDouble(3),
                        cursor.getDouble(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8)
                ));
            } while (cursor.moveToNext());
        }

        if (cursor != null) cursor.close();

        adapter.notifyDataSetChanged();
    }
}