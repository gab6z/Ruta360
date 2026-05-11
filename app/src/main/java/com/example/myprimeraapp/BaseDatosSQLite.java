package com.example.myprimeraapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseDatosSQLite extends SQLiteOpenHelper {

    public static final String dbName = "Ruta360.db";
    public static final int Version = 1;
    public static final String tablaUsuario = "CREATE TABLE usuario (id INTEGER PRIMARY KEY AUTOINCREMENT, cedula TEXT, nombres TEXT,apellidos TEXT,edad INTEGER,nacionalidad TEXT, genero TEXT, estadoCivil TEXT, correo TEXT, contraseña TEXT, fechaNac TEXT, nivelIngles REAL)";

    public static final String tablaReservas = "CREATE TABLE reservas (" +
            "id_reserva INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "usuario_id TEXT, " +
            "destino TEXT, " +
            "fecha_viaje TEXT, " +
            "metodo_pago TEXT, " +
            "total_pagar DOUBLE)";
    public BaseDatosSQLite(Context context) {
        super(context, dbName, null, Version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(tablaUsuario);
        sqLiteDatabase.execSQL(tablaReservas);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS usuario");
        db.execSQL("DROP TABLE IF EXISTS reservas");
        onCreate(db);
    }

    public boolean validarLogin(String correo, String clave) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM usuario WHERE correo=? AND contraseña=?", new String[]{correo, clave});
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        return existe;
    }
    public Cursor obtenerUsuario(String correo) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM usuario WHERE correo=?", new String[]{correo});
    }
    public boolean actualizarUsuario(String correoActual, String nuevosNombres, String nuevaClave) {
        SQLiteDatabase db = this.getWritableDatabase();
        android.content.ContentValues values = new android.content.ContentValues();
        values.put("nombres", nuevosNombres);
        values.put("contraseña", nuevaClave);

        int filasAfectadas = db.update("usuario", values, "correo=?", new String[]{correoActual});
        db.close();
        return filasAfectadas > 0;
    }
    public boolean eliminarUsuario(String correo) {
        SQLiteDatabase db = this.getWritableDatabase();
        int filasBorradas = db.delete("usuario", "correo=?", new String[]{correo});
        db.close();
        return filasBorradas > 0;
    }
    public boolean actualizarPerfilCompleto(String correoActual, String nombres, String apellidos, String edad, String nacionalidad, String genero, String estadoCivil, String fechaNac, String nuevaClave) {
        SQLiteDatabase db = this.getWritableDatabase();
        android.content.ContentValues values = new android.content.ContentValues();
        values.put("nombres", nombres);
        values.put("apellidos", apellidos);
        values.put("edad", edad);
        values.put("nacionalidad", nacionalidad);
        values.put("genero", genero);
        values.put("estadoCivil", estadoCivil);
        values.put("fechaNac", fechaNac);
        values.put("contraseña", nuevaClave);

        int filasAfectadas = db.update("usuario", values, "correo=?", new String[]{correoActual});
        db.close();
        return filasAfectadas > 0;
    }

    public void eliminarReserva(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("reservas", "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}