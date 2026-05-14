package com.example.myprimeraapp;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DestinoAdapter extends RecyclerView.Adapter<DestinoAdapter.ViewHolder> {
    private ArrayList<Destino> listaDestinos;
    public DestinoAdapter(ArrayList<Destino> listaDestinos) {

        this.listaDestinos = listaDestinos;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_destino, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        Destino destino = listaDestinos.get(position);
        holder.txtNombre.setText(destino.getNombre()
        );
        holder.txtPais.setText(destino.getUbicacion()
        );
        holder.txtRating.setText("⭐ " + destino.getCalificacion()
        );
        holder.txtPrecio.setText("$" + destino.getPrecio()
        );
        holder.txtIcono.setText(destino.getIcono()
        );

        try {
            holder.imgDestino.setBackgroundColor(Color.parseColor(destino.getColor()));

        } catch (Exception e) {

            holder.imgDestino.setBackgroundColor(Color.LTGRAY);
        }

        BaseDatosSQLite baseDatos = new BaseDatosSQLite(holder.itemView.getContext());

        String usuario = "LP";

        boolean esFavorito = baseDatos.esFavorito(usuario, destino.getIdDestino());

        if (esFavorito) {
            holder.btnFavorito.setImageResource(android.R.drawable.btn_star_big_on);

        } else {

            holder.btnFavorito.setImageResource(android.R.drawable.btn_star_big_off);
        }


        holder.btnFavorito.setOnClickListener(v -> {
            boolean favoritoActual = baseDatos.esFavorito(usuario, destino.getIdDestino());


            if (favoritoActual) {
                baseDatos.eliminarFavorito(usuario, destino.getIdDestino());
                holder.btnFavorito.setImageResource(android.R.drawable.btn_star_big_off);

                listaDestinos.remove(position);notifyItemRemoved(position);
                notifyItemRangeChanged(position, listaDestinos.size());
            }

            else {
                baseDatos.agregarFavorito(usuario, destino.getIdDestino()
                );

                holder.btnFavorito.setImageResource(android.R.drawable.btn_star_big_on
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaDestinos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgDestino;
        ImageView btnFavorito;
        TextView txtNombre;
        TextView txtPais;
        TextView txtRating;
        TextView txtPrecio;
        TextView txtIcono;
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
            btnReservar = itemView.findViewById(R.id.btnReservar);
        }
    }
}