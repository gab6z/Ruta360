package com.example.myprimeraapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;

public class Registrarse extends AppCompatActivity {

    private TextInputEditText txtCedula, txtNombre, txtApellidos, txtEdad, txtNacimiento, txtCorreo, txtClave;
    private Spinner spnNacionalidad, spnGenero;
    private RadioGroup rgEstadoCivil;
    private RadioButton rbSoltero, rbCasado, rbOtro;
    private RatingBar rtgIngles;
    private ImageButton btnCalendario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrarse);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        txtCedula = findViewById(R.id.reg_txtcedula);
        txtNombre = findViewById(R.id.reg_txtnombre);
        txtApellidos = findViewById(R.id.reg_txtapellidos);
        txtEdad = findViewById(R.id.reg_txtedad);
        txtNacimiento = findViewById(R.id.reg_txtnacimiento);
        txtCorreo = findViewById(R.id.reg_txtcorreo);
        txtClave = findViewById(R.id.reg_txtclave);


        spnNacionalidad = findViewById(R.id.reg_spn_nacionalidad);
        spnGenero = findViewById(R.id.reg_spn_genero);

        rgEstadoCivil = findViewById(R.id.reg_rg_estadocivil);
        rbSoltero = findViewById(R.id.rb_soltero);
        rbCasado = findViewById(R.id.rb_casado);
        rbOtro = findViewById(R.id.rb_otro);

        rtgIngles = findViewById(R.id.rtg_ingles);
        btnCalendario = findViewById(R.id.btn_calendario);

        btnCalendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePicker();
            }
        });
    }

    private void mostrarDatePicker() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                final String fechaSeleccionada = day + "/" + (month + 1) + "/" + year;
                txtNacimiento.setText(fechaSeleccionada);
            }
        });
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

     public void guardarSD(String data){
        try{
            File file = new File(getExternalFilesDir(null), "RegistroUsuario");
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file,true));
            out.write(data);
            out.close();
            Toast.makeText(this, "Los datos han sido guardados en la SD", Toast.LENGTH_LONG).show();

        }catch(Exception e ){
            Log.e("SD", "Error al guardar en SD");
            Toast.makeText(this, "Ocurrió un error al guradar en la SD", Toast.LENGTH_LONG).show();
        }

    }

    public void registrarDatos(View v) {
        String cedula = txtCedula.getText().toString();
        String nombres = txtNombre.getText().toString();
        String apellidos = txtApellidos.getText().toString();
        String edad = txtEdad.getText().toString();
        String fechaNac = txtNacimiento.getText().toString();
        String correo = txtCorreo.getText().toString().trim();
        String clave = txtClave.getText().toString().trim();

        String nacionalidad = spnNacionalidad.getSelectedItem().toString();
        String genero = spnGenero.getSelectedItem().toString();
        float nivelIngles = rtgIngles.getRating();

        String estadoCivil = "No especificado";
        int idSeleccionado = rgEstadoCivil.getCheckedRadioButtonId();
        if (idSeleccionado == R.id.rb_soltero) estadoCivil = "Soltero/a";
        else if (idSeleccionado == R.id.rb_casado) estadoCivil = "Casado/a";
        else if (idSeleccionado == R.id.rb_otro) estadoCivil = "Otro";

        if(correo.isEmpty() || clave.isEmpty()){
            Toast.makeText(this, "Debe ingresar correo y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        String data = cedula + ";" + nombres + ";" + apellidos + ";" + edad + ";" +
                nacionalidad + ";" + genero + ";" + estadoCivil + ";" + fechaNac + ";" +
                nivelIngles + ";" + correo + ";" + clave + "\n";

        Log.d("REGISTRO_APP", data);
        guardarSD(data);

        guardarBD(cedula, nombres, apellidos, edad, fechaNac, nacionalidad, genero, estadoCivil, nivelIngles, correo, clave);
        finish();
    }

    public String leerSD() {
        String linea = "";
        StringBuilder datos = new StringBuilder();

        try {
            File file = new File(getExternalFilesDir(null), "RegistroUsuario");
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

            while ((linea = br.readLine()) != null) {
                String[] campos = linea.split(";");

                datos.append("Cédula: ").append(campos[0]).append("\n");
                datos.append("Nombres: ").append(campos[1]).append("\n");
                datos.append("Apellidos: ").append(campos[2]).append("\n");
                datos.append("Edad: ").append(campos[3]).append("\n");
                datos.append("Nacionalidad: ").append(campos[4]).append("\n");
                datos.append("Género: ").append(campos[5]).append("\n");
                datos.append("Estado Civil: ").append(campos[6]).append("\n");
                datos.append("Fecha Nacimiento: ").append(campos[7]).append("\n");
                datos.append("Nivel Inglés: ").append(campos[8]).append("\n");
                datos.append("------------------------\n");
            }

            br.close();

        } catch (Exception e) {
            Log.e("SD", "Error al leer archivo");
        }

        return datos.toString();
    }
    public void guardarBD(String cedula, String nombres, String apellidos, String edad,
                          String fechaNac, String nacionalidad, String genero, String estadoCivil,
                          float nivelIngles, String correo, String contraseña){
        BaseDatosSQLite db = new BaseDatosSQLite(this);
        final SQLiteDatabase db360Write = db.getWritableDatabase();

        if(db360Write != null){
            ContentValues values = new ContentValues();
            values.put("cedula", cedula);
            values.put("nombres", nombres);
            values.put("apellidos", apellidos);
            values.put("edad", edad);
            values.put("nacionalidad", nacionalidad);
            values.put("genero", genero);
            values.put("estadoCivil", estadoCivil);
            values.put("correo", correo);
            values.put("contraseña", contraseña);
            values.put("fechaNac", fechaNac);
            values.put("nivelIngles", nivelIngles);

            db360Write.insert("usuario", null, values);
            Toast.makeText(this, "Los datos han sido ingresados correctamente en la base de datos", Toast.LENGTH_LONG).show();
            db360Write.close();
        }

    }
    public void mostrarDatos(View v) {
        String datos = leerSD();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Datos Registrados");
        builder.setMessage(datos);
        builder.setPositiveButton("Cerrar", null);
        builder.show();
    }

    public void borrarFormulario(View v) {
        txtCedula.setText("");
        txtNombre.setText("");
        txtApellidos.setText("");
        txtEdad.setText("");
        txtNacimiento.setText("");

        spnNacionalidad.setSelection(0);
        spnGenero.setSelection(0);
        rgEstadoCivil.clearCheck();
        rtgIngles.setRating(0);
    }

    public void cancelarRegistro(View v) {
        finish();
    }


}