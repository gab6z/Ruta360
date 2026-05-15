package com.example.myprimeraapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ConstructorActivity extends AppCompatActivity {
    int paqueteIdEditar = -1;
    CardView cardEco, cardHotel, cardResort;
    CardView cardBasica, cardMedia, cardTodo;
    CardView cardBus, cardTren, cardVuelo;
    CardView cardCanada, cardArizona, cardFrancia; // ← DESTINOS

    TextView tvDetalle, tvSubtotal, tvIva, tvPrecioFinal;
    Button btnGuardar;

    double pAloj = 0, pAlim = 0, pTrans = 0, pDest = 0, totalFinal = 0;
    String nAloj = "Ninguno", nAlim = "Ninguno", nTrans = "Ninguno", nDest = "Ninguno";
    BaseDatosSQLite db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_constructor);

        db = new BaseDatosSQLite(this);
        vincularVistas();
        configurarSelecciones();

        btnGuardar.setOnClickListener(v -> {
            if (totalFinal > 0) {
                if (paqueteIdEditar > 0) {
                    // MODO EDICION
                    boolean ok = db.actualizarPaquete(paqueteIdEditar, nDest, nAloj, nAlim, nTrans, totalFinal);
                    if (ok) {
                        Toast.makeText(this, "Borrador actualizado ✅", Toast.LENGTH_SHORT).show();
                        paqueteIdEditar = -1;
                    }
                } else {
                    // MODO NUEVO
                    long id = db.guardarPaquete(nDest, nAloj, nAlim, nTrans, totalFinal);
                    Toast.makeText(this, "Borrador guardado (ID: " + id + ")", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Arma tu paquete primero", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnCarrito = findViewById(R.id.btnAgregarCarrito);
        btnCarrito.setOnClickListener(v -> {
            if (totalFinal > 0) {
                Intent resultado = new Intent();
                resultado.putExtra("paquete_nombre", nDest + " | " + nAloj + " + " + nAlim + " + " + nTrans);
                resultado.putExtra("paquete_precio", totalFinal);
                setResult(RESULT_OK, resultado);
                finish();
            } else {
                Toast.makeText(this, "Primero selecciona tus opciones", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(ConstructorActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        Button btnVerBorradores = findViewById(R.id.btnVerBorradores);
        btnVerBorradores.setOnClickListener(v -> {
            Intent intentBorradores = new Intent(ConstructorActivity.this, BorradoresActivity.class);
            startActivity(intentBorradores);
        });

        Intent intent = getIntent();
        String editarId = intent.getStringExtra("editar_id");
        if (editarId != null) {
            precargarPaquete(
                    editarId,
                    intent.getStringExtra("editar_destino"),
                    intent.getStringExtra("editar_alojamiento"),
                    intent.getStringExtra("editar_alimentacion"),
                    intent.getStringExtra("editar_transporte")
            );
        }
    }

    private void configurarSelecciones() {

        // DESTINOS
        cardCanada.setOnClickListener(v -> {
            pintarSeleccionDestino(cardCanada, cardArizona, cardFrancia);
            pDest = 700; nDest = "Canadá - Vancouver"; actualizarUI();
        });
        cardArizona.setOnClickListener(v -> {
            pintarSeleccionDestino(cardArizona, cardCanada, cardFrancia);
            pDest = 468; nDest = "EE.UU - Arizona"; actualizarUI();
        });
        cardFrancia.setOnClickListener(v -> {
            pintarSeleccionDestino(cardFrancia, cardCanada, cardArizona);
            pDest = 899; nDest = "Francia - París"; actualizarUI();
        });

        // ALOJAMIENTO
        cardEco.setOnClickListener(v -> {
            pintarSeleccion(cardEco, cardHotel, cardResort);
            pAloj = 50; nAloj = "Eco Lodge"; actualizarUI();
        });
        cardHotel.setOnClickListener(v -> {
            pintarSeleccion(cardHotel, cardEco, cardResort);
            pAloj = 120; nAloj = "Hotel City"; actualizarUI();
        });
        cardResort.setOnClickListener(v -> {
            pintarSeleccion(cardResort, cardEco, cardHotel);
            pAloj = 300; nAloj = "Resort Luxury"; actualizarUI();
        });

        // ALIMENTACION
        cardBasica.setOnClickListener(v -> {
            pintarSeleccion(cardBasica, cardMedia, cardTodo);
            pAlim = 20; nAlim = "Básico"; actualizarUI();
        });
        cardMedia.setOnClickListener(v -> {
            pintarSeleccion(cardMedia, cardBasica, cardTodo);
            pAlim = 45; nAlim = "Media Pensión"; actualizarUI();
        });
        cardTodo.setOnClickListener(v -> {
            pintarSeleccion(cardTodo, cardBasica, cardMedia);
            pAlim = 80; nAlim = "Todo Incluido"; actualizarUI();
        });

        // TRANSPORTE
        cardBus.setOnClickListener(v -> {
            pintarSeleccion(cardBus, cardTren, cardVuelo);
            pTrans = 30; nTrans = "Bus"; actualizarUI();
        });
        cardTren.setOnClickListener(v -> {
            pintarSeleccion(cardTren, cardBus, cardVuelo);
            pTrans = 85; nTrans = "Tren"; actualizarUI();
        });
        cardVuelo.setOnClickListener(v -> {
            pintarSeleccion(cardVuelo, cardBus, cardTren);
            pTrans = 250; nTrans = "Vuelo"; actualizarUI();
        });
    }

    private void pintarSeleccion(CardView seleccionada, CardView op2, CardView op3) {
        seleccionada.setCardBackgroundColor(Color.parseColor("#E1F5FE"));
        op2.setCardBackgroundColor(Color.WHITE);
        op3.setCardBackgroundColor(Color.WHITE);
    }

    private void pintarSeleccionDestino(CardView seleccionada, CardView op2, CardView op3) {
        seleccionada.setCardBackgroundColor(Color.parseColor("#F3E5F5"));
        op2.setCardBackgroundColor(Color.WHITE);
        op3.setCardBackgroundColor(Color.WHITE);
    }

    private void precargarPaquete(String id, String destino, String aloj, String alim, String trans) {
        // Guardar ID para actualizar en vez de insertar
        paqueteIdEditar = Integer.parseInt(id);

        // Preseleccionar Destino
        if (destino.equals("Canadá - Vancouver")) {
            pintarSeleccionDestino(cardCanada, cardArizona, cardFrancia);
            pDest = 700; nDest = destino;
        } else if (destino.equals("EE.UU - Arizona")) {
            pintarSeleccionDestino(cardArizona, cardCanada, cardFrancia);
            pDest = 468; nDest = destino;
        } else {
            pintarSeleccionDestino(cardFrancia, cardCanada, cardArizona);
            pDest = 899; nDest = destino;
        }

        // Preseleccionar Alojamiento
        if (aloj.equals("Eco Lodge")) {
            pintarSeleccion(cardEco, cardHotel, cardResort);
            pAloj = 50; nAloj = aloj;
        } else if (aloj.equals("Hotel City")) {
            pintarSeleccion(cardHotel, cardEco, cardResort);
            pAloj = 120; nAloj = aloj;
        } else {
            pintarSeleccion(cardResort, cardEco, cardHotel);
            pAloj = 300; nAloj = aloj;
        }

        // Preseleccionar Alimentacion
        if (alim.equals("Básico")) {
            pintarSeleccion(cardBasica, cardMedia, cardTodo);
            pAlim = 20; nAlim = alim;
        } else if (alim.equals("Media Pensión")) {
            pintarSeleccion(cardMedia, cardBasica, cardTodo);
            pAlim = 45; nAlim = alim;
        } else {
            pintarSeleccion(cardTodo, cardBasica, cardMedia);
            pAlim = 80; nAlim = alim;
        }

        // Preseleccionar Transporte
        if (trans.equals("Bus")) {
            pintarSeleccion(cardBus, cardTren, cardVuelo);
            pTrans = 30; nTrans = trans;
        } else if (trans.equals("Tren")) {
            pintarSeleccion(cardTren, cardBus, cardVuelo);
            pTrans = 85; nTrans = trans;
        } else {
            pintarSeleccion(cardVuelo, cardBus, cardTren);
            pTrans = 250; nTrans = trans;
        }

        actualizarUI();
    }

    private void actualizarUI() {
        tvDetalle.setText(nDest + " | " + nAloj + " + " + nAlim + " + " + nTrans);

        double subtotal = pDest + pAloj + pAlim + pTrans;
        double iva = subtotal * 0.15;
        totalFinal = subtotal + iva;

        tvSubtotal.setText("$" + String.format("%.2f", subtotal));
        tvIva.setText("$" + String.format("%.2f", iva));
        tvPrecioFinal.setText("$" + String.format("%.2f", totalFinal));
    }

    private void vincularVistas() {
        // DESTINOS
        cardCanada = findViewById(R.id.cardCanada);
        cardArizona = findViewById(R.id.cardArizona);
        cardFrancia = findViewById(R.id.cardFrancia);
        // ALOJAMIENTO
        cardEco = findViewById(R.id.cardEco);
        cardHotel = findViewById(R.id.cardHotel);
        cardResort = findViewById(R.id.cardResort);
        // ALIMENTACION
        cardBasica = findViewById(R.id.cardBasica);
        cardMedia = findViewById(R.id.cardMedia);
        cardTodo = findViewById(R.id.cardTodo);
        // TRANSPORTE
        cardBus = findViewById(R.id.cardBus);
        cardTren = findViewById(R.id.cardTren);
        cardVuelo = findViewById(R.id.cardVuelo);
        // RESUMEN
        tvDetalle = findViewById(R.id.tvDetalle);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvIva = findViewById(R.id.tvIva);
        tvPrecioFinal = findViewById(R.id.tvPrecioFinal);
        btnGuardar = findViewById(R.id.btnGuardarBorrador);
    }
}