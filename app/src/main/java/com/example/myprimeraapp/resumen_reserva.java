package com.example.myprimeraapp;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher; // Nuevo import
import androidx.activity.result.contract.ActivityResultContracts; //

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class resumen_reserva extends AppCompatActivity {
    private Button btnCalendario, btnPagar;
    private Spinner spMoneda;
    private RadioGroup rgMetodo;
    private CheckBox cbTerminos;
    private String fechaViaje = "";
    private androidx.cardview.widget.CardView cardInfoEfectivo;

    private TextView tvDestinoNombre, tvDestinoPrecio;
    private String nombreDestinoRecibido;
    private double precioDestinoRecibido;

    private double precioPaqueteExtra = 0;
    private String detallePaqueteExtra = "";
    private androidx.cardview.widget.CardView cardResumenPaquete;
    private TextView tvDetallePaqueteExtra;

    private final ActivityResultLauncher<Intent> constructorLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            precioPaqueteExtra = result.getData().getDoubleExtra("paquete_precio", 0.0);
                            detallePaqueteExtra = result.getData().getStringExtra("paquete_nombre");

                            // Actualizamos la UI
                            cardResumenPaquete.setVisibility(View.VISIBLE);
                            tvDetallePaqueteExtra.setText(detallePaqueteExtra + " (+$" + precioPaqueteExtra + ")");

                            // ACTUALIZAR EL TOTAL SUMADO
                            double sumaTotal = precioDestinoRecibido + precioPaqueteExtra;
                            tvDestinoPrecio.setText("Total a Pagar: $" + String.format("%.2f", sumaTotal));
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_reserva);

        tvDestinoNombre = findViewById(R.id.tvDestinoSeleccionado);
        tvDestinoPrecio = findViewById(R.id.tvPrecioBase);

        nombreDestinoRecibido = getIntent().getStringExtra("nombre_destino");
        precioDestinoRecibido = getIntent().getDoubleExtra("precio_destino", 0.0);
        String ubicacion = getIntent().getStringExtra("ubicacion_destino");

        if (nombreDestinoRecibido != null) {
            tvDestinoNombre.setText("📍 Destino: " + nombreDestinoRecibido + " (" + ubicacion + ")");
            tvDestinoPrecio.setText("Total a Pagar: $" + String.format("%.2f", precioDestinoRecibido));
        }

        btnCalendario = findViewById(R.id.btnSeleccionarFecha);
        btnPagar = findViewById(R.id.btnConfirmarCheckout);
        spMoneda = findViewById(R.id.spMoneda);
        rgMetodo = findViewById(R.id.rgMetodoPago);
        cbTerminos = findViewById(R.id.cbTerminosReserva);
        cardInfoEfectivo = findViewById(R.id.cardInfoEfectivo);
        cardResumenPaquete = findViewById(R.id.cardResumenPaquete);
        tvDetallePaqueteExtra = findViewById(R.id.tvDetallePaqueteExtra);
        Button btnIrAConstructor = findViewById(R.id.btnIrAConstructor);

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

        btnIrAConstructor.setOnClickListener(v -> {
            Intent intent = new Intent(this, ConstructorActivity.class);
            constructorLauncher.launch(intent);
        });
        ImageView btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> onBackPressed());

        Button btcancelarReserva = findViewById(R.id.btnCerrar);
        btcancelarReserva.setOnClickListener(v -> onBackPressed());
    }

    private void procesarReserva() {
        SharedPreferences pref = getSharedPreferences("Credenciales", MODE_PRIVATE);
        String cedulaUsuario = pref.getString("userSP", "Desconocido");

        int idSeleccionado = rgMetodo.getCheckedRadioButtonId();
        RadioButton rb = findViewById(idSeleccionado);
        String metodo = (rb != null) ? rb.getText().toString() : "Efectivo";
        double totalReal = precioDestinoRecibido + precioPaqueteExtra;
        String destinoFinal = nombreDestinoRecibido + " + Paquete Custom";

        BaseDatosSQLite helper = new BaseDatosSQLite(this);
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("usuario_id", cedulaUsuario);
        values.put("destino", nombreDestinoRecibido);
        values.put("fecha_viaje", fechaViaje);
        values.put("metodo_pago", metodo);
        values.put("total_pagar", precioDestinoRecibido);
        values.put("destino", destinoFinal);
        values.put("total_pagar", totalReal);

        long resultado = db.insert("reservas", null, values);

        if (resultado != -1) {
            mostrarDialogoExito(String.valueOf(resultado));
        } else {
            Toast.makeText(this, "Error al guardar reserva", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDialogoPago() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pasarela de Pago");

        android.widget.ScrollView scrollView = new android.widget.ScrollView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 40, 60, 20);

        final EditText etNombre = new EditText(this);
        etNombre.setHint("Nombre del Titular");
        etNombre.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        layout.addView(etNombre);

        final EditText etCedula = new EditText(this);
        etCedula.setHint("Cédula del Titular");
        etCedula.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(etCedula);

        final EditText etTarjeta = new EditText(this);
        etTarjeta.setHint("16 dígitos de tarjeta");
        etTarjeta.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(etTarjeta);

        final EditText etCvv = new EditText(this);
        etCvv.setHint("CVV (3-4 dígitos)");
        etCvv.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(etCvv);

        final EditText etCorreo = new EditText(this);
        etCorreo.setHint("Correo Electrónico");
        etCorreo.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        layout.addView(etCorreo);

        scrollView.addView(layout);
        builder.setView(scrollView);

        builder.setPositiveButton("Finalizar Pago", (d, w) -> {
            String nombre = etNombre.getText().toString().trim();
            String cedula = etCedula.getText().toString().trim();
            String tarjeta = etTarjeta.getText().toString().trim();
            String cvv = etCvv.getText().toString().trim();
            String correo = etCorreo.getText().toString().trim();

            if (nombre.isEmpty() || cedula.isEmpty() || tarjeta.length() < 16 || cvv.length() < 3 || !correo.contains("@")) {
                Toast.makeText(this, "Por favor, verifique todos los campos", Toast.LENGTH_LONG).show();
            } else {
                procesarReserva();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void mostrarDialogoExito(String reservaId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.activity_exito, null);
        builder.setView(view);

        ImageView ivQR = view.findViewById(R.id.ivCodigoQR);
        Button btnCerrarExito = view.findViewById(R.id.btnVolverInicio);

        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            String contenidoQR = "Ticket-Ruta360\nID:" + reservaId + "\nDestino:" + nombreDestinoRecibido;
            Bitmap bitmap = barcodeEncoder.encodeBitmap(contenidoQR, BarcodeFormat.QR_CODE, 500, 500);
            ivQR.setImageBitmap(bitmap);
        } catch (Exception e) { e.printStackTrace(); }

        AlertDialog dialog = builder.create();
        btnCerrarExito.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });
        dialog.show();
    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance((view, year, month, day) -> {
            fechaViaje = day + "/" + (month + 1) + "/" + year;
            btnCalendario.setText("Fecha: " + fechaViaje);
        });
        newFragment.show(getSupportFragmentManager(), "datePicker");
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
        if (id == R.id.mp_acercade) { mostrarAcercaDe(); return true; }
        if (id == R.id.mp_ayuda) { mostrarAyuda(); return true; }
        if (id == R.id.mp_cerrar_sesion) { borrarPreferenciasYSalir(); return true; }
        return super.onOptionsItemSelected(item);
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

    private void borrarPreferenciasYSalir() {
        SharedPreferences pref = getSharedPreferences("Credenciales", MODE_PRIVATE);
        pref.edit().clear().apply();
        Intent i = new Intent(this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}