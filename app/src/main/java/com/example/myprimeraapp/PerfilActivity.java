package com.example.myprimeraapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class PerfilActivity extends AppCompatActivity {

    private TextInputEditText txtCorreo, txtCedula, txtNombres, txtApellidos, txtEdad, txtGenero, txtNacionalidad, txtEstadoCivil, txtFechaNac, txtClave;
    private String correoUsuarioActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        txtCorreo = findViewById(R.id.perfil_txtcorreo);
        txtCedula = findViewById(R.id.perfil_txtcedula);
        txtNombres = findViewById(R.id.perfil_txtnombres);
        txtApellidos = findViewById(R.id.perfil_txtapellidos);
        txtEdad = findViewById(R.id.perfil_txtedad);
        txtGenero = findViewById(R.id.perfil_txtgenero);
        txtNacionalidad = findViewById(R.id.perfil_txtnacionalidad);
        txtEstadoCivil = findViewById(R.id.perfil_txtestadocivil);
        txtFechaNac = findViewById(R.id.perfil_txtfechanac);
        txtClave = findViewById(R.id.perfil_txtclave);

        SharedPreferences preferences = getSharedPreferences("Credenciales", MODE_PRIVATE);
        correoUsuarioActual = preferences.getString("userSP", "");

        cargarDatosDelUsuario();
    }

    private void cargarDatosDelUsuario() {
        BaseDatosSQLite db = new BaseDatosSQLite(this);
        Cursor cursor = db.obtenerUsuario(correoUsuarioActual);

        if (cursor.moveToFirst()) {
            txtCorreo.setText(correoUsuarioActual);
            txtCedula.setText(cursor.getString(cursor.getColumnIndexOrThrow("cedula")));
            txtNombres.setText(cursor.getString(cursor.getColumnIndexOrThrow("nombres")));
            txtApellidos.setText(cursor.getString(cursor.getColumnIndexOrThrow("apellidos")));
            txtEdad.setText(cursor.getString(cursor.getColumnIndexOrThrow("edad")));
            txtGenero.setText(cursor.getString(cursor.getColumnIndexOrThrow("genero")));
            txtNacionalidad.setText(cursor.getString(cursor.getColumnIndexOrThrow("nacionalidad")));
            txtEstadoCivil.setText(cursor.getString(cursor.getColumnIndexOrThrow("estadoCivil")));
            txtFechaNac.setText(cursor.getString(cursor.getColumnIndexOrThrow("fechaNac")));
            txtClave.setText(cursor.getString(cursor.getColumnIndexOrThrow("contraseña")));


            android.widget.TextView txtIniciales = findViewById(R.id.txt_iniciales_perfil);
            android.widget.TextView lblNombre = findViewById(R.id.lbl_nombre_completo);
            android.widget.TextView lblCorreo = findViewById(R.id.lbl_correo_header);

            String nom = cursor.getString(cursor.getColumnIndexOrThrow("nombres"));
            String ape = cursor.getString(cursor.getColumnIndexOrThrow("apellidos"));

            lblNombre.setText(nom + " " + ape);

            lblCorreo.setText(correoUsuarioActual);

            String iniciales = "";
            if(!nom.isEmpty()) iniciales += nom.substring(0, 1);
            if(!ape.isEmpty()) iniciales += ape.substring(0, 1);
            txtIniciales.setText(iniciales.toUpperCase());
        }
        cursor.close();
    }

    public void volverAlHome(View v) {
        finish();
    }

    public void actualizarDatos(View v) {
        String nuevosNombres = txtNombres.getText().toString().trim();
        String nuevosApellidos = txtApellidos.getText().toString().trim();
        String nuevaEdad = txtEdad.getText().toString().trim();
        String nuevaNacionalidad = txtNacionalidad.getText().toString().trim();
        String nuevoGenero = txtGenero.getText().toString().trim();
        String nuevoEstado = txtEstadoCivil.getText().toString().trim();
        String nuevaFecha = txtFechaNac.getText().toString().trim();
        String nuevaClave = txtClave.getText().toString().trim();

        BaseDatosSQLite db = new BaseDatosSQLite(this);
        if (db.actualizarPerfilCompleto(correoUsuarioActual, nuevosNombres, nuevosApellidos, nuevaEdad, nuevaNacionalidad, nuevoGenero, nuevoEstado, nuevaFecha, nuevaClave)) {

            SharedPreferences preferences = getSharedPreferences("Credenciales", MODE_PRIVATE);
            preferences.edit().putString("claveSp", nuevaClave).apply();

            Toast.makeText(this, "Perfil actualizado con éxito", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al guardar los cambios", Toast.LENGTH_SHORT).show();
        }
    }

    public void eliminarCuenta(View v) {
        BaseDatosSQLite db = new BaseDatosSQLite(this);
        if (db.eliminarUsuario(correoUsuarioActual)) {
            Toast.makeText(this, "Cuenta eliminada. ¡Hasta pronto!", Toast.LENGTH_LONG).show();

            SharedPreferences preferences = getSharedPreferences("Credenciales", MODE_PRIVATE);
            preferences.edit().clear().apply();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}