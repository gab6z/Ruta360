package com.example.myprimeraapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText usuario, clave;
    private CheckBox recordar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usuario = findViewById(R.id.et_usuario);
        clave = findViewById(R.id.et_clave);
        recordar = findViewById(R.id.login_cb_recordar);

        verificarSesionActiva();
    }

    private void verificarSesionActiva() {
        SharedPreferences preferencesLogin = getSharedPreferences("Credenciales", MODE_PRIVATE);
        String userGuardado = preferencesLogin.getString("userSP", "");
        String claveGuardada = preferencesLogin.getString("claveSp", "");

        if (!userGuardado.isEmpty() && !claveGuardada.isEmpty()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void guardarInfoAcceso(String user, String pass) {
        SharedPreferences preferences = getSharedPreferences("Credenciales", MODE_PRIVATE);
        SharedPreferences.Editor preferencesEdit = preferences.edit();
        preferencesEdit.putString("userSP", user);
        preferencesEdit.putString("claveSp", pass);
        preferencesEdit.apply();
    }

    public void iniciarSesion(View v) {
        String correoUser = usuario.getText().toString().trim(); // Ahora pedimos el correo
        String passUser = clave.getText().toString().trim();

        if (correoUser.isEmpty() || passUser.isEmpty()) {
            Toast.makeText(this, "Llene todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        BaseDatosSQLite db = new BaseDatosSQLite(this);

        if (db.validarLogin(correoUser, passUser)) {

            if (recordar.isChecked()) {
                guardarInfoAcceso(correoUser, passUser);
            }

            Toast.makeText(LoginActivity.this, "¡Bienvenido a Ruta 360!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("user", correoUser);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(LoginActivity.this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        }
    }

    public void registrarse(View v) {
        Intent intent = new Intent(this, Registrarse.class);
        startActivity(intent);
    }
}