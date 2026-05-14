package com.example.myprimeraapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favoritos);
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main), (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                });

        baseDatos = new BaseDatosSQLite(this);

        recyclerFavoritos = findViewById(R.id.recyclerFavoritos);
        recyclerFavoritos.setLayoutManager(new LinearLayoutManager(this));

        listaFavoritos = new ArrayList<>();

        adapter = new DestinoAdapter(listaFavoritos);
        recyclerFavoritos.setAdapter(adapter);

        // CARGAR FAVORITOS
        cargarFavoritos();

        // NAVBAR
        BottomNavigationView barraNavegacion = findViewById(R.id.barra_navegacion);
        barraNavegacion.setSelectedItemId(R.id.nav_favoritos);
        barraNavegacion.setOnItemSelectedListener(item -> {

            int id = item.getItemId();
            if (id == R.id.nav_inicio) {

                startActivity(new Intent(FavoritosActivity.this, MainActivity.class));
                finish();
                return true;
            }

            else if (id == R.id.nav_explorar) {

                startActivity(new Intent(FavoritosActivity.this, ExplorarActivity.class
                ));
                finish();
                return true;
            }

            else if (id == R.id.nav_favoritos) {
                return true;
            }

            else if (id == R.id.nav_crear) {
                return true;
            }

            else if (id == R.id.nav_carrito) {
                startActivity(new Intent(
                        FavoritosActivity.this,
                        resumen_reserva.class
                ));

                finish();
                return true;
            }

            else if (id == R.id.nav_social) {
                return true;
            }
            return false;
        });
    }

    // RECARGAR AL VOLVER

    @Override
    protected void onResume() {
        super.onResume();
        cargarFavoritos();
    }

    // METODO CARGAR FAVORITOS
    private void cargarFavoritos() {

        listaFavoritos.clear();
        String usuario = "LP";
        Cursor cursor = baseDatos.obtenerFavoritos(usuario);
        if (cursor.moveToFirst()) {
            do {
                Destino destino = new Destino(

                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getDouble(3),
                        cursor.getDouble(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8)
                );
                listaFavoritos.add(destino);

            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }
}