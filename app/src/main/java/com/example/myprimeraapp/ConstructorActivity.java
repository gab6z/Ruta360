package com.example.myprimeraapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ConstructorActivity extends AppCompatActivity {

    CardView cardEco, cardHotel, cardResort;
    CardView cardBasica, cardMedia, cardTodo;
    CardView cardBus, cardTren, cardVuelo;

    TextView tvDetalle, tvSubtotal, tvIva, tvPrecioFinal;
    Button btnGuardar;

    double pAloj = 0, pAlim = 0, pTrans = 0, totalFinal = 0;
    String nAloj = "Ninguno", nAlim = "Ninguno", nTrans = "Ninguno";
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
                long id = db.guardarPaquete(nAloj, nAlim, nTrans, totalFinal);
                Toast.makeText(this, "Paquete guardado (ID: " + id + ")", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Arma tu paquete primero", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnCarrito = findViewById(R.id.btnAgregarCarrito);
        btnCarrito.setOnClickListener(v -> {
            if (totalFinal > 0) {
                Intent resultado = new Intent();
                resultado.putExtra("paquete_nombre", nAloj + " + " + nAlim + " + " + nTrans);
                resultado.putExtra("paquete_precio", totalFinal);

                setResult(RESULT_OK, resultado);
                finish();
            } else {
                Toast.makeText(this, "Primero selecciona tus opciones", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configurarSelecciones() {
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
        seleccionada.setCardBackgroundColor(Color.parseColor("#E1F5FE")); // Celeste
        op2.setCardBackgroundColor(Color.WHITE);
        op3.setCardBackgroundColor(Color.WHITE);
    }

    private void actualizarUI() {
        tvDetalle.setText(nAloj + " + " + nAlim + " + " + nTrans);

        double subtotal = pAloj + pAlim + pTrans;
        double iva = subtotal * 0.15; // 15% IVA Ecuador
        totalFinal = subtotal + iva;

        tvSubtotal.setText("$" + String.format("%.2f", subtotal));
        tvIva.setText("$" + String.format("%.2f", iva));
        tvPrecioFinal.setText("$" + String.format("%.2f", totalFinal));
    }

    private void vincularVistas() {
        cardEco = findViewById(R.id.cardEco);
        cardHotel = findViewById(R.id.cardHotel);
        cardResort = findViewById(R.id.cardResort);
        cardBasica = findViewById(R.id.cardBasica);
        cardMedia = findViewById(R.id.cardMedia);
        cardTodo = findViewById(R.id.cardTodo);
        cardBus = findViewById(R.id.cardBus);
        cardTren = findViewById(R.id.cardTren);
        cardVuelo = findViewById(R.id.cardVuelo);
        tvDetalle = findViewById(R.id.tvDetalle);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvIva = findViewById(R.id.tvIva);
        tvPrecioFinal = findViewById(R.id.tvPrecioFinal);
        btnGuardar = findViewById(R.id.btnGuardarBorrador);
    }
}