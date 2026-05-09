package com.example.myprimeraapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.PopupMenu;

public class MainActivity extends AppCompatActivity {

    TextView lblmensaje;
    Button btnBorrarDatos;
    ImageButton btnMenu;

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

        lblmensaje = findViewById(R.id.main_lblprincipal);
        btnBorrarDatos = findViewById(R.id.btn_borrar_datos);
        btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(MainActivity.this, btnMenu);
            popupMenu.getMenuInflater().inflate(R.menu.menuprincipal, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> onOptionsItemSelected(item));
            popupMenu.show();
        });

        Intent infoRecibida = getIntent();
        String usuario = infoRecibida.getStringExtra("user");

        if (usuario == null) {
            SharedPreferences preferences = getSharedPreferences("Credenciales", MODE_PRIVATE);
            usuario = preferences.getString("userSP", "Viajero");
        }

        lblmensaje.setText("Hola, " + usuario);
        btnBorrarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borrarPreferenciasYSalir();
            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuPrincipal = getMenuInflater();
        menuPrincipal.inflate(R.menu.menuprincipal,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.mp_inicio)
        {
            Toast.makeText(this,"Ha presionado sobre la opción. Inicio",Toast.LENGTH_LONG).show();
        }

        if(item.getItemId() == R.id.mp_ayuda)
        {
            mostrarAyuda();
        }

        if(item.getItemId() == R.id.mp_acercade)
        {
            mostrarAcercaDe();
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

}