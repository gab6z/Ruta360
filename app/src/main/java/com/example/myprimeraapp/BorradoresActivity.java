package com.example.myprimeraapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class BorradoresActivity extends AppCompatActivity {

    RecyclerView recyclerBorradores;
    TextView tvVacio;
    BaseDatosSQLite db;
    ArrayList<String[]> listaPaquetes;
    BorradoresAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borradores);

        db = new BaseDatosSQLite(this);
        recyclerBorradores = findViewById(R.id.recyclerBorradores);
        tvVacio = findViewById(R.id.tvVacio);

        ImageView btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(BorradoresActivity.this, ConstructorActivity.class);
            startActivity(intent);
            finish();
        });

        recyclerBorradores.setLayoutManager(new LinearLayoutManager(this));
        cargarBorradores();
    }

    private void cargarBorradores() {
        listaPaquetes = db.obtenerPaquetes();
        if (listaPaquetes.isEmpty()) {
            tvVacio.setVisibility(View.VISIBLE);
            recyclerBorradores.setVisibility(View.GONE);
        } else {
            tvVacio.setVisibility(View.GONE);
            recyclerBorradores.setVisibility(View.VISIBLE);
            adapter = new BorradoresAdapter();
            recyclerBorradores.setAdapter(adapter);
        }
    }

    private void abrirConstructorParaEditar(String[] paquete) {
        Intent intent = new Intent(BorradoresActivity.this, ConstructorActivity.class);
        intent.putExtra("editar_id", paquete[0]);
        intent.putExtra("editar_destino", paquete[1]);
        intent.putExtra("editar_alojamiento", paquete[2]);
        intent.putExtra("editar_alimentacion", paquete[3]);
        intent.putExtra("editar_transporte", paquete[4]);
        startActivity(intent);
    }

    private int buscarIndice(String[] array, String valor) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(valor)) return i;
        }
        return 0;
    }

    private double calcularTotal(String dest, String aloj, String alim, String trans) {
        double pDest = dest.equals("Canadá - Vancouver") ? 700 :
                dest.equals("EE.UU - Arizona")    ? 468 : 899;

        double pAloj = aloj.equals("Eco Lodge")     ? 50  :
                aloj.equals("Hotel City")    ? 120 : 300;

        double pAlim = alim.equals("Básico")        ? 20  :
                alim.equals("Media Pensión") ? 45  : 80;

        double pTrans = trans.equals("Bus")  ? 30  :
                trans.equals("Tren") ? 85  : 250;

        double subtotal = pDest + pAloj + pAlim + pTrans;
        return subtotal + (subtotal * 0.15);
    }

    class BorradoresAdapter extends RecyclerView.Adapter<BorradoresAdapter.ViewHolder> {

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvId, tvDestino, tvAlojamiento, tvAlimentacion, tvTransporte, tvTotal;
            Button btnEditar, btnEliminar;

            ViewHolder(View v) {
                super(v);
                tvId           = v.findViewById(R.id.tvIdPaquete);
                tvDestino      = v.findViewById(R.id.tvDestinoPaquete);
                tvAlojamiento  = v.findViewById(R.id.tvAlojamientoPaquete);
                tvAlimentacion = v.findViewById(R.id.tvAlimentacionPaquete);
                tvTransporte   = v.findViewById(R.id.tvTransportePaquete);
                tvTotal        = v.findViewById(R.id.tvTotalPaquete);
                btnEditar      = v.findViewById(R.id.btnEditar);
                btnEliminar    = v.findViewById(R.id.btnEliminar);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_borrador, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String[] p = listaPaquetes.get(position);
            // p: [0]id [1]destino [2]alojamiento [3]alimentacion [4]transporte [5]precio

            holder.tvId.setText("#" + p[0]);
            holder.tvDestino.setText(p[1]);
            holder.tvAlojamiento.setText("Alojamiento: " + p[2]);
            holder.tvAlimentacion.setText("Alimentación: " + p[3]);
            holder.tvTransporte.setText("Transporte: " + p[4]);
            holder.tvTotal.setText("$" + String.format("%.2f", Double.parseDouble(p[5])));

            holder.btnEditar.setOnClickListener(v -> abrirConstructorParaEditar(p));

            holder.btnEliminar.setOnClickListener(v -> {
                new AlertDialog.Builder(BorradoresActivity.this)
                        .setTitle("Eliminar borrador")
                        .setMessage("¿Seguro que deseas eliminar este paquete?")
                        .setPositiveButton("Eliminar", (d, w) -> {
                            db.eliminarPaquete(Integer.parseInt(p[0]));
                            Toast.makeText(BorradoresActivity.this,
                                    "Borrador eliminado", Toast.LENGTH_SHORT).show();
                            cargarBorradores();
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            });
        }

        @Override
        public int getItemCount() { return listaPaquetes.size(); }
    }
}