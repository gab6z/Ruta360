package com.example.myprimeraapp;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;

public class resumen_reserva extends AppCompatActivity {
    private Button btnCalendario, btnPagar;
    private Spinner spMoneda;
    private RadioGroup rgMetodo;
    private CheckBox cbTerminos;
    private String fechaViaje = "";
    private androidx.cardview.widget.CardView cardInfoEfectivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_reserva);

        btnCalendario = findViewById(R.id.btnSeleccionarFecha);
        btnPagar = findViewById(R.id.btnConfirmarCheckout);
        spMoneda = findViewById(R.id.spMoneda);
        rgMetodo = findViewById(R.id.rgMetodoPago);
        cbTerminos = findViewById(R.id.cbTerminosReserva);
        cardInfoEfectivo = findViewById(R.id.cardInfoEfectivo);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.monedas_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMoneda.setAdapter(adapter);

        btnCalendario.setOnClickListener(v -> showDatePickerDialog());

        rgMetodo.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbTarjeta) {
                cardInfoEfectivo.setVisibility(View.GONE);
                btnPagar.setText("Proceder al Pago");
            } else if (checkedId == R.id.rbTransferencia) {
                cardInfoEfectivo.setVisibility(View.VISIBLE);
                btnPagar.setText("Confirmar Reserva (Efectivo)");
            }
        });

        btnPagar.setOnClickListener(v -> {
            if (!cbTerminos.isChecked() || fechaViaje.isEmpty()) {
                Toast.makeText(this, "Complete la fecha y acepte los términos", Toast.LENGTH_SHORT).show();
                return;
            }
            if (rgMetodo.getCheckedRadioButtonId() == R.id.rbTarjeta) {
                mostrarDialogoPago();
            } else {
                procesarReserva();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuprincipal, menu);
        menu.add(0, 999, 0, "Historial de Reservas").setIcon(android.R.drawable.ic_menu_agenda);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mp_acercade) {
            mostrarAcercaDe();
            return true;
        }
        if (id == R.id.mp_ayuda) {
            mostrarAyuda();
            return true;
        }
        if (id == R.id.mp_cerrar_sesion) {
            borrarPreferenciasYSalir();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    private void mostrarAcercaDe() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_acercade, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        view.findViewById(R.id.btnCerrar).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void mostrarAyuda() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_ayuda, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        view.findViewById(R.id.btnCerrarAyuda).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void procesarReserva() {
        SharedPreferences pref = getSharedPreferences("Credenciales", MODE_PRIVATE);
        String cedulaUsuario = pref.getString("userSP", "Desconocido");

        int idSeleccionado = rgMetodo.getCheckedRadioButtonId();
        RadioButton rb = findViewById(idSeleccionado);
        String metodo = (rb != null) ? rb.getText().toString() : "Efectivo";

        BaseDatosSQLite helper = new BaseDatosSQLite(this);
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("usuario_id", cedulaUsuario);
        values.put("destino", "Ruta del Spondylus");
        values.put("fecha_viaje", fechaViaje);
        values.put("metodo_pago", metodo);
        values.put("total_pagar", 150.00);

        long resultado = db.insert("reservas", null, values);

        if (resultado != -1) {
            mostrarDialogoExito(String.valueOf(resultado));
        }


    }

    private void mostrarDialogoPago() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pasarela de Pago");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText etTarjeta = new EditText(this);
        etTarjeta.setHint("16 dígitos de tarjeta");
        etTarjeta.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(etTarjeta);

        builder.setView(layout);
        builder.setPositiveButton("Pagar", (d, w) -> {
            if (etTarjeta.getText().length() == 16) procesarReserva();
            else Toast.makeText(this, "Datos inválidos", Toast.LENGTH_SHORT).show();
        });
        builder.show();
    }

    private void borrarPreferenciasYSalir() {
        SharedPreferences pref = getSharedPreferences("Credenciales", MODE_PRIVATE);
        pref.edit().clear().apply();
        Intent i = new Intent(this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance((view, year, month, day) -> {
            fechaViaje = day + "/" + (month + 1) + "/" + year;
            btnCalendario.setText("Fecha: " + fechaViaje);
        });
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

}