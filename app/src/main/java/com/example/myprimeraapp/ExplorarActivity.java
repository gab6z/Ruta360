package com.example.myprimeraapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.widget.TextView;

public class ExplorarActivity extends AppCompatActivity {

    private RecyclerView recyclerDestinos;
    private ArrayList<Destino> listaDestinos;
    private ArrayList<Destino> listaOriginal;
    private DestinoAdapter adapter;
    private BaseDatosSQLite baseDatos;

    // BOTONES FILTRO
    private Button btnTodas;
    private Button btnPlaya;
    private Button btnCiudad;
    private Button btnMontana;
    private Button btnAventura;
    private Button btnInternacional;

    // BUSCADOR
    private TextInputEditText txtBuscar;
    private String destinoFiltro = "";
    private String precioFiltro = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);EdgeToEdge.enable(this);
        setContentView(R.layout.activity_explorar);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main), (v, insets) -> {
                    Insets systemBars =
                            insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );
                    return insets;
                });

        TextView txtIniciales =
                findViewById(R.id.txtIniciales);

        Intent infoRecibida = getIntent();

        String usuarioCorreo =
                infoRecibida.getStringExtra("user");

        if (usuarioCorreo == null) {

            SharedPreferences preferences =
                    getSharedPreferences(
                            "Credenciales",
                            MODE_PRIVATE
                    );

            usuarioCorreo =
                    preferences.getString(
                            "userSP",
                            ""
                    );
        }

        BaseDatosSQLite db =
                new BaseDatosSQLite(this);

        Cursor cursor =
                db.obtenerUsuario(usuarioCorreo);

        if (cursor.moveToFirst()) {

            String nom =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("nombres")
                    );

            String ape =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("apellidos")
                    );

            String iniciales = "";

            if (!nom.isEmpty()) {
                iniciales += nom.substring(0, 1);
            }

            if (!ape.isEmpty()) {
                iniciales += ape.substring(0, 1);
            }

            txtIniciales.setText(
                    iniciales.toUpperCase()
            );
        }

        cursor.close();
        destinoFiltro = getIntent().getStringExtra("destino");
        precioFiltro = getIntent().getStringExtra("precio");

        // BOTON VOLVER
        ImageView btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(ExplorarActivity.this, MainActivity.class);
            startActivity(intent);

            finish();
        });

        // SQLITE
        baseDatos = new BaseDatosSQLite(this);

        // INSERTAR DESTINOS
        baseDatos.insertarDestinosIniciales();

        // RECYCLER
        recyclerDestinos = findViewById(R.id.recyclerDestinos);
        recyclerDestinos.setLayoutManager(new LinearLayoutManager(this));

        // LISTAS
        listaOriginal = baseDatos.obtenerDestinos();
        listaDestinos = new ArrayList<>(listaOriginal);

        if ((destinoFiltro != null && !destinoFiltro.trim().isEmpty())
                || (precioFiltro != null && !precioFiltro.trim().isEmpty())) {

            ArrayList<Destino> listaFiltrada = new ArrayList<>();

            String destinoLimpio = (destinoFiltro == null) ? "" : destinoFiltro.trim();

            double precioMax = -1;

            if (precioFiltro != null && !precioFiltro.trim().isEmpty()) {
                try {
                    precioMax = Double.parseDouble(precioFiltro.trim());

                    if (precioMax < 1 || precioMax > 1000000) {precioMax = -1;}

                } catch (Exception e) {
                    precioMax = -1;
                }
            }

            for (Destino d : listaOriginal) {

                boolean matchDestino = destinoLimpio.isEmpty() || d.getNombre().toLowerCase()
                        .contains(destinoLimpio.toLowerCase());

                boolean matchPrecio = precioMax == -1 || d.getPrecio() <= precioMax;

                if (matchDestino && matchPrecio) {
                    listaFiltrada.add(d);
                }
            }

            listaDestinos = listaFiltrada;
        }

        // ADAPTER
        adapter = new DestinoAdapter(listaDestinos);
        recyclerDestinos.setAdapter(adapter);

        // BUSCADOR
        txtBuscar = findViewById(R.id.txtBuscar);

        txtBuscar.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        buscarDestino(
                                s.toString()
                        );
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

        // BOTONES
        btnTodas = findViewById(R.id.btnTodas);
        btnPlaya = findViewById(R.id.btnPlaya);
        btnCiudad = findViewById(R.id.btnCiudad);
        btnMontana = findViewById(R.id.btnMontana);
        btnAventura = findViewById(R.id.btnAventura);
        btnInternacional = findViewById(R.id.btnInternacional);

        // EVENTOS FILTRO
        btnTodas.setOnClickListener(v -> {restaurarLista();seleccionarBoton(btnTodas);
        });

        btnPlaya.setOnClickListener(v -> {filtrarDestinos("Playa");seleccionarBoton(btnPlaya);
        });

        btnCiudad.setOnClickListener(v -> {filtrarDestinos("Ciudad");seleccionarBoton(btnCiudad);
        });

        btnMontana.setOnClickListener(v -> {filtrarDestinos("Montaña");seleccionarBoton(btnMontana);
        });

        btnAventura.setOnClickListener(v -> {filtrarDestinos("Aventura");seleccionarBoton(btnAventura);
        });

        btnInternacional.setOnClickListener(v -> {filtrarDestinos("Internacional");seleccionarBoton(btnInternacional);
        });

        // BOTON INICIAL
        seleccionarBoton(btnTodas);
    }

    // FILTRAR
    private void filtrarDestinos(String categoria) {

        ArrayList<Destino> listaFiltrada = new ArrayList<>();

        for (Destino destino : listaOriginal) {
            if (destino.getCategoria().equalsIgnoreCase(categoria)) {
                listaFiltrada.add(destino);
            }
        }

        adapter = new DestinoAdapter(listaFiltrada);
        recyclerDestinos.setAdapter(adapter);
    }

    // RESTAURAR
    private void restaurarLista() {

        adapter = new DestinoAdapter(listaOriginal);
        recyclerDestinos.setAdapter(adapter);
    }

    // BUSCADOR
    private void buscarDestino(String texto) {

        ArrayList<Destino> listaBusqueda = new ArrayList<>();
        for (Destino destino : listaOriginal) {
            if (destino.getNombre().toLowerCase().contains(texto.toLowerCase())) {
                listaBusqueda.add(destino);
            }
        }

        adapter = new DestinoAdapter(listaBusqueda);
        recyclerDestinos.setAdapter(adapter);
    }

    // CAMBIAR COLOR BOTON
    private void seleccionarBoton(Button botonSeleccionado) {

        Button[] botones = {
                btnTodas,
                btnPlaya,
                btnCiudad,
                btnMontana,
                btnAventura,
                btnInternacional
        };

        for (Button boton : botones) {

            boton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.WHITE));
            boton.setTextColor(Color.parseColor("#00838F"));
        }

        // ACTIVO
        botonSeleccionado.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#00838F")));
        botonSeleccionado.setTextColor(Color.WHITE);
    }
}