package com.example.myprimeraapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DestinoAdapter extends RecyclerView.Adapter<DestinoAdapter.ViewHolder> {

    private ArrayList<Destino> listaDestinos;
    private String usuario;
    private BaseDatosSQLite baseDatos;

    public DestinoAdapter(ArrayList<Destino> listaDestinos, String usuario) {
        this.listaDestinos = listaDestinos;
        this.usuario = usuario;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_destino, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Destino destino = listaDestinos.get(position);

        if (baseDatos == null) {
            baseDatos = new BaseDatosSQLite(holder.itemView.getContext());
        }

        holder.txtNombre.setText(destino.getNombre());
        holder.txtPais.setText(destino.getUbicacion());
        holder.txtRating.setText("⭐ " + destino.getCalificacion());
        holder.txtPrecio.setText("$" + destino.getPrecio());
        holder.txtIcono.setText(destino.getIcono());
        holder.txtTipo.setText(destino.getIcono());
        holder.txtCategoria.setText(destino.getCategoria());

        try {
            holder.imgDestino.setBackgroundColor(Color.parseColor(destino.getColor()));
        } catch (Exception e) {
            holder.imgDestino.setBackgroundColor(Color.LTGRAY);
        }

        // =========================
        // FAVORITOS
        // =========================
        boolean esFavorito = baseDatos.esFavorito(usuario, destino.getIdDestino());

        holder.btnFavorito.setImageResource(
                esFavorito
                        ? android.R.drawable.btn_star_big_on
                        : android.R.drawable.btn_star_big_off
        );

        holder.btnFavorito.setOnClickListener(v -> {

            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            Destino d = listaDestinos.get(pos);

            boolean actual = baseDatos.esFavorito(usuario, d.getIdDestino());

            if (actual) {

                baseDatos.eliminarFavorito(usuario, d.getIdDestino());

                // 🔥 eliminar inmediato de UI
                listaDestinos.remove(pos);
                notifyItemRemoved(pos);
                notifyItemRangeChanged(pos, listaDestinos.size());

            } else {

                baseDatos.agregarFavorito(usuario, d.getIdDestino());
                notifyItemChanged(pos);
            }
        });

        // =========================
        // DIALOG RESERVA
        // =========================
        holder.btnReservar.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

            View view = LayoutInflater.from(v.getContext())
                    .inflate(R.layout.dialog_detalle_destino, null);

            builder.setView(view);

            AlertDialog dialog = builder.create();
            dialog.show();

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }

            FrameLayout layoutColor = view.findViewById(R.id.layoutColorDestino);

            TextView txtIcono = view.findViewById(R.id.txtIconoDialog);
            TextView txtNombre = view.findViewById(R.id.txtNombreDialog);
            TextView txtUbicacion = view.findViewById(R.id.txtUbicacionDialog);
            TextView txtCategoria = view.findViewById(R.id.txtCategoriaDialog);
            TextView txtRuta = view.findViewById(R.id.txtRutaDialog);
            TextView txtTipoInfo = view.findViewById(R.id.txtTipoInfoDialog);
            TextView txtRating = view.findViewById(R.id.txtRatingDialog);
            TextView txtPrecio = view.findViewById(R.id.txtPrecioDialog);

            Button btnCancelar = view.findViewById(R.id.btnCancelarDialog);
            Button btnAgregar = view.findViewById(R.id.btnCarritoDialog);

            SharedPreferences preferences =
                    v.getContext().getSharedPreferences("Ruta360Prefs", android.content.Context.MODE_PRIVATE);

            String origen = preferences.getString("origenSeleccionado", "Guayaquil");

            txtIcono.setText(destino.getIcono());
            txtNombre.setText(destino.getNombre());
            txtUbicacion.setText(destino.getUbicacion());
            txtCategoria.setText(destino.getCategoria());
            txtRuta.setText(origen + " → " + destino.getNombre());
            txtTipoInfo.setText(destino.getIcono());
            txtRating.setText("⭐ " + destino.getCalificacion());
            txtPrecio.setText("$" + destino.getPrecio());

            try {
                layoutColor.setBackgroundColor(Color.parseColor(destino.getColor()));
            } catch (Exception e) {
                layoutColor.setBackgroundColor(Color.LTGRAY);
            }

            btnCancelar.setOnClickListener(btn -> dialog.dismiss());

            btnAgregar.setOnClickListener(btn -> {
                Intent intent = new Intent(v.getContext(), resumen_reserva.class);

                intent.putExtra("nombre_destino", destino.getNombre());
                intent.putExtra("precio_destino", destino.getPrecio());
                intent.putExtra("rating_destino", destino.getCalificacion());
                intent.putExtra("ubicacion_destino", destino.getUbicacion());

                v.getContext().startActivity(intent);
                dialog.dismiss();
            });
        });
    }

    @Override
    public int getItemCount() {
        return listaDestinos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        FrameLayout imgDestino;
        ImageView btnFavorito;
        TextView txtNombre, txtPais, txtRating, txtPrecio, txtIcono, txtTipo, txtCategoria;
        Button btnReservar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgDestino = itemView.findViewById(R.id.imgDestino);
            btnFavorito = itemView.findViewById(R.id.btnFavorito);
            txtNombre = itemView.findViewById(R.id.txtNombreDestino);
            txtPais = itemView.findViewById(R.id.txtPaisDestino);
            txtRating = itemView.findViewById(R.id.txtRatingDestino);
            txtPrecio = itemView.findViewById(R.id.txtPrecioDestino);
            txtIcono = itemView.findViewById(R.id.txtIconoDestino);
            txtTipo = itemView.findViewById(R.id.txtTipoDestino);
            txtCategoria = itemView.findViewById(R.id.txtCategoriaDestino);
            btnReservar = itemView.findViewById(R.id.btnReservar);
        }
    }
}