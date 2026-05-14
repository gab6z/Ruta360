package com.example.myprimeraapp;

public class Destino {

    private int idDestino;
    private String nombre;
    private String ubicacion;
    private double precio;
    private double calificacion;
    private String categoria;
    private String tipo;
    private String icono;
    private String color;
    private boolean favorito;

    public Destino(int idDestino,
                   String nombre,
                   String ubicacion,
                   double precio,
                   double calificacion,
                   String categoria,
                   String tipo,
                   String icono,
                   String color) {

        this.idDestino = idDestino;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.precio = precio;
        this.calificacion = calificacion;
        this.categoria = categoria;
        this.tipo = tipo;
        this.icono = icono;
        this.color = color;

        // POR DEFECTO
        this.favorito = false;
    }

    // GETTERS

    public int getIdDestino() {
        return idDestino;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public double getPrecio() {
        return precio;
    }

    public double getCalificacion() {
        return calificacion;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getTipo() {
        return tipo;
    }

    public String getIcono() {
        return icono;
    }

    public String getColor() {
        return color;
    }

    public boolean isFavorito() {
        return favorito;
    }

    // SETTERS

    public void setIdDestino(int idDestino) {
        this.idDestino = idDestino;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public void setCalificacion(double calificacion) {
        this.calificacion = calificacion;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setFavorito(boolean favorito) {
        this.favorito = favorito;
    }
}