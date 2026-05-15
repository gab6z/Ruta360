package com.example.myprimeraapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerDestacados;
    private RecyclerView recyclerMejores;
    private DestinoAdapter adapterDestacados;
    private DestinoAdapter adapterMejores;
    private ArrayList<Destino> listaDestacados;
    private ArrayList<Destino> listaMejores;

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
            if (!nom.isEmpty()) {iniciales += nom.substring(0, 1);}
            if (!ape.isEmpty()) {iniciales += ape.substring(0, 1);}
            txtIniciales.setText(iniciales.toUpperCase());
        }

        recyclerDestacados = findViewById(R.id.recyclerDestacados);
        recyclerMejores = findViewById(R.id.recyclerMejores);

        LinearLayoutManager layoutDestacados =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        recyclerDestacados.setLayoutManager(layoutDestacados);
        recyclerDestacados.setHasFixedSize(true);
        recyclerDestacados.setNestedScrollingEnabled(false);

        LinearLayoutManager layoutMejores =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerMejores.setLayoutManager(layoutMejores);
        recyclerMejores.setHasFixedSize(true);
        recyclerMejores.setNestedScrollingEnabled(false);

        listaDestacados = db.obtenerDestacados();
        listaMejores = db.obtenerMejoresValorados();

        adapterDestacados = new DestinoAdapter(listaDestacados);
        adapterMejores = new DestinoAdapter(listaMejores);

        recyclerDestacados.setAdapter(adapterDestacados);
        recyclerMejores.setAdapter(adapterMejores);

        recyclerDestacados.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(android.graphics.Rect outRect,
                                       android.view.View view,
                                       androidx.recyclerview.widget.RecyclerView parent,
                                       androidx.recyclerview.widget.RecyclerView.State state) {
                outRect.right = 24;
            }
        });

        recyclerMejores.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(android.graphics.Rect outRect,
                                       android.view.View view,
                                       androidx.recyclerview.widget.RecyclerView parent,
                                       androidx.recyclerview.widget.RecyclerView.State state) {
                outRect.bottom = 20;
            }
        });

        Button btnBuscarHome = findViewById(R.id.btnBuscarHome);
        TextInputEditText txtBuscarDestino = findViewById(R.id.txtBuscarDestino);
        TextInputEditText txtPresupuesto = findViewById(R.id.txtPresupuesto);

        btnBuscarHome.setOnClickListener(v -> {
            String destino = txtBuscarDestino.getText().toString().trim();
            String precio = txtPresupuesto.getText().toString().trim();
            Intent intent = new Intent(MainActivity.this, ExplorarActivity.class);
            intent.putExtra("destino", destino);
            intent.putExtra("precio", precio);
            startActivity(intent);
        });

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
                if (id == R.id.mp_historial) {
                    mostrarHistorialDialogo();
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

                    Intent intent = new Intent(MainActivity.this, ExplorarActivity.class);
                    startActivity(intent);

                } else if (id == R.id.nav_favoritos) {

                    Intent intent = new Intent(MainActivity.this, FavoritosActivity.class);
                    startActivity(intent);

                } else if (id == R.id.nav_crear) {
                    Intent intent = new Intent(MainActivity.this, ConstructorActivity.class);
                    startActivity(intent);

                } else if (id == R.id.nav_carrito) {

                    Intent intent = new Intent(MainActivity.this, resumen_reserva.class);
                    startActivity(intent);
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
        if (item.getItemId() == R.id.mp_historial) {
            mostrarHistorialDialogo();
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

    private void mostrarHistorialDialogo() {
        Toast.makeText(this, "Intentando abrir historial...", Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Mis Reservas Recientes");

        ListView lv = new ListView(this);
        ArrayList<String> lista = new ArrayList<>();
        ArrayList<Integer> ids = new ArrayList<>();

        BaseDatosSQLite helper = new BaseDatosSQLite(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM reservas", null);

        if (c.getCount() == 0) {
            Toast.makeText(this, "No tienes reservas registradas aún", Toast.LENGTH_LONG).show();
            c.close();
            return;
        }

        while (c.moveToNext()) {
            ids.add(c.getInt(0));
            lista.add("Ticket #" + c.getInt(0) + "\nDestino: " + c.getString(2));
        }
        c.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lista);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener((p, v, pos, id) -> {
            String idSeleccionado = String.valueOf(ids.get(pos));
            mostrarDialogoExito(idSeleccionado);
        });
        builder.setView(lv);
        builder.setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void mostrarDialogoExito(String reservaId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.activity_exito, null);
        builder.setView(view);

        ImageView ivQR = view.findViewById(R.id.ivCodigoQR);
        Button btnCerrar = view.findViewById(R.id.btnVolverInicio);
        btnCerrar.setText("Aceptar");

        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap("Ticket-Ruta360-ID:" + reservaId, BarcodeFormat.QR_CODE, 500, 500);
            ivQR.setImageBitmap(bitmap);
        } catch (Exception e) { e.printStackTrace(); }

        AlertDialog dialog = builder.create();
        btnCerrar.setOnClickListener(v -> dialog.dismiss());
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