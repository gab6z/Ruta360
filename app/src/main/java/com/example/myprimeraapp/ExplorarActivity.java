package com.example.myprimeraapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class ExplorarActivity extends AppCompatActivity {

    private RecyclerView recyclerDestinos;
    private ArrayList<Destino> listaDestinos;
    private ArrayList<Destino> listaOriginal;
    private DestinoAdapter adapter;
    private BaseDatosSQLite baseDatos;

    private Button btnTodas, btnPlaya, btnCiudad, btnMontana, btnAventura, btnInternacional;
    private TextInputEditText txtBuscar;

    private TextView txtIniciales;

    private String destinoFiltro = "";
    private String precioFiltro = "";

    private String usuarioCorreo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_explorar);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                }
        );

        // =========================
        // USUARIO
        // =========================
        txtIniciales = findViewById(R.id.txtIniciales);

        Intent infoRecibida = getIntent();
        usuarioCorreo = infoRecibida.getStringExtra("user");

        if (usuarioCorreo == null || usuarioCorreo.isEmpty()) {
            SharedPreferences preferences =
                    getSharedPreferences("Credenciales", MODE_PRIVATE);

            usuarioCorreo = preferences.getString("userSP", "");
        }

        baseDatos = new BaseDatosSQLite(this);

        Cursor cursorUser = baseDatos.obtenerUsuario(usuarioCorreo);

        if (cursorUser != null && cursorUser.moveToFirst()) {

            String nom = cursorUser.getString(cursorUser.getColumnIndexOrThrow("nombres"));
            String ape = cursorUser.getString(cursorUser.getColumnIndexOrThrow("apellidos"));

            String iniciales = "";

            if (nom != null && !nom.isEmpty()) iniciales += nom.substring(0, 1);
            if (ape != null && !ape.isEmpty()) iniciales += ape.substring(0, 1);

            txtIniciales.setText(iniciales.toUpperCase());
        }

        if (cursorUser != null) cursorUser.close();

        // =========================
        // FILTROS
        // =========================
        destinoFiltro = getIntent().getStringExtra("destino");
        precioFiltro = getIntent().getStringExtra("precio");

        String origenSeleccionado = getIntent().getStringExtra("origen");

        if (origenSeleccionado == null || origenSeleccionado.isEmpty()) {
            SharedPreferences preferences =
                    getSharedPreferences("Ruta360Prefs", MODE_PRIVATE);

            origenSeleccionado = preferences.getString("origenSeleccionado", "Guayaquil");
        }

        // BOTÓN VOLVER
        ImageView btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> {
            startActivity(new Intent(ExplorarActivity.this, MainActivity.class));
            finish();
        });

        // =========================
        // DB + RECYCLER
        // =========================
        baseDatos.insertarDestinosIniciales();

        recyclerDestinos = findViewById(R.id.recyclerDestinos);
        recyclerDestinos.setLayoutManager(new LinearLayoutManager(this));

        listaOriginal = baseDatos.obtenerDestinosPorOrigen(origenSeleccionado);
        listaDestinos = new ArrayList<>(listaOriginal);

        // FILTRO INICIAL
        if ((destinoFiltro != null && !destinoFiltro.trim().isEmpty())
                || (precioFiltro != null && !precioFiltro.trim().isEmpty())) {

            ArrayList<Destino> listaFiltrada = new ArrayList<>();
            String destinoLimpio = (destinoFiltro == null) ? "" : destinoFiltro.trim();

            double precioMax = -1;

            if (precioFiltro != null && !precioFiltro.trim().isEmpty()) {
                try {
                    precioMax = Double.parseDouble(precioFiltro.trim());
                } catch (Exception e) {
                    precioMax = -1;
                }
            }

            for (Destino d : listaOriginal) {

                boolean matchDestino =
                        destinoLimpio.isEmpty() ||
                                d.getNombre().toLowerCase().contains(destinoLimpio.toLowerCase());

                boolean matchPrecio =
                        precioMax == -1 || d.getPrecio() <= precioMax;

                if (matchDestino && matchPrecio) {
                    listaDestinos.add(d);
                }
            }
        }

        // ✅ FIX IMPORTANTE: siempre pasar usuario
        adapter = new DestinoAdapter(listaDestinos, usuarioCorreo);
        recyclerDestinos.setAdapter(adapter);

        // =========================
        // BUSCADOR
        // =========================
        txtBuscar = findViewById(R.id.txtBuscar);

        txtBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buscarDestino(s.toString());
            }

            @Override public void afterTextChanged(Editable s) {}
        });

        // =========================
        // BOTONES CATEGORÍAS
        // =========================
        btnTodas = findViewById(R.id.btnTodas);
        btnPlaya = findViewById(R.id.btnPlaya);
        btnCiudad = findViewById(R.id.btnCiudad);
        btnMontana = findViewById(R.id.btnMontana);
        btnAventura = findViewById(R.id.btnAventura);
        btnInternacional = findViewById(R.id.btnInternacional);

        btnTodas.setOnClickListener(v -> {
            restaurarLista();
            seleccionarBoton(btnTodas);
        });

        btnPlaya.setOnClickListener(v -> {
            filtrarDestinos("Playa");
            seleccionarBoton(btnPlaya);
        });

        btnCiudad.setOnClickListener(v -> {
            filtrarDestinos("Ciudad");
            seleccionarBoton(btnCiudad);
        });

        btnMontana.setOnClickListener(v -> {
            filtrarDestinos("Montaña");
            seleccionarBoton(btnMontana);
        });

        btnAventura.setOnClickListener(v -> {
            filtrarDestinos("Aventura");
            seleccionarBoton(btnAventura);
        });

        btnInternacional.setOnClickListener(v -> {
            filtrarDestinos("Internacional");
            seleccionarBoton(btnInternacional);
        });

        seleccionarBoton(btnTodas);
    }

    // =========================
    // FILTROS CORREGIDOS
    // =========================

    private void filtrarDestinos(String categoria) {

        ArrayList<Destino> listaFiltrada = new ArrayList<>();

        for (Destino destino : listaOriginal) {
            if (destino.getCategoria().equalsIgnoreCase(categoria)) {
                listaFiltrada.add(destino);
            }
        }

        adapter = new DestinoAdapter(listaFiltrada, usuarioCorreo);
        recyclerDestinos.setAdapter(adapter);
    }

    private void restaurarLista() {
        adapter = new DestinoAdapter(listaOriginal, usuarioCorreo);
        recyclerDestinos.setAdapter(adapter);
    }

    private void buscarDestino(String texto) {

        ArrayList<Destino> listaBusqueda = new ArrayList<>();

        for (Destino destino : listaOriginal) {
            if (destino.getNombre().toLowerCase().contains(texto.toLowerCase())) {
                listaBusqueda.add(destino);
            }
        }

        adapter = new DestinoAdapter(listaBusqueda, usuarioCorreo);
        recyclerDestinos.setAdapter(adapter);
    }

    private void seleccionarBoton(Button botonSeleccionado) {

        Button[] botones = {
                btnTodas, btnPlaya, btnCiudad, btnMontana, btnAventura, btnInternacional
        };

        for (Button boton : botones) {
            boton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.WHITE));
            boton.setTextColor(Color.parseColor("#00838F"));
        }

        botonSeleccionado.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(Color.parseColor("#00838F"))
        );
        botonSeleccionado.setTextColor(Color.WHITE);
    }
}