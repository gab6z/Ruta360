package com.example.myprimeraapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class BaseDatosSQLite extends SQLiteOpenHelper {

    public static final String dbName = "Ruta360.db";
    public static final int Version = 2;
    public static final String tablaUsuario = "CREATE TABLE usuario (id INTEGER PRIMARY KEY AUTOINCREMENT, cedula TEXT, nombres TEXT,apellidos TEXT,edad INTEGER,nacionalidad TEXT, genero TEXT, estadoCivil TEXT, correo TEXT, contraseña TEXT, fechaNac TEXT, nivelIngles REAL)";

    //nueva tabla
    public static final String tablaReservas = "CREATE TABLE reservas (" +
            "id_reserva INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "usuario_id TEXT, " +
            "destino TEXT, " +
            "fecha_viaje TEXT, " +
            "metodo_pago TEXT, " +
            "total_pagar DOUBLE)";

    public static final String tablaDestinos =
            "CREATE TABLE destinos (" +
                    "id_destino INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nombre TEXT, " +
                    "ubicacion TEXT, " +
                    "precio DOUBLE, " +
                    "calificacion DOUBLE, " +
                    "categoria TEXT, " +
                    "tipo TEXT, " +
                    "icono TEXT, " +
                    "color TEXT)";

    public static final String tablaFavoritos =
            "CREATE TABLE favoritos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "usuario TEXT, " +
                    "destino_id INTEGER)";
    public BaseDatosSQLite(Context context) {
        super(context, dbName, null, Version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(tablaUsuario);
        sqLiteDatabase.execSQL(tablaReservas);
        sqLiteDatabase.execSQL(tablaDestinos);
        sqLiteDatabase.execSQL(tablaFavoritos);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS usuario");
        db.execSQL("DROP TABLE IF EXISTS reservas");
        db.execSQL("DROP TABLE IF EXISTS destinos");
        db.execSQL("DROP TABLE IF EXISTS favoritos");
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

    public void insertarDestinosIniciales() {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM destinos", null);
        if (cursor.getCount() == 0) {
            db.execSQL("INSERT INTO destinos VALUES(null,'Montañita','Ecuador',250,4.8,'Playa','Nacional','🌴','#03A9F4')");
            db.execSQL("INSERT INTO destinos VALUES(null,'Baños','Ecuador',180,4.7,'Montaña','Nacional','⛰️','#8D6E63')");
            db.execSQL("INSERT INTO destinos VALUES(null,'Galápagos','Ecuador',900,5.0,'Playa','Nacional','🐢','#00ACC1')");
            db.execSQL("INSERT INTO destinos VALUES(null,'Cuenca','Ecuador',220,4.6,'Ciudad','Nacional','🏙️','#546E7A')");
            db.execSQL("INSERT INTO destinos VALUES(null,'Mindo','Ecuador',150,4.5,'Montaña','Nacional','🌿','#43A047')");
            db.execSQL("INSERT INTO destinos VALUES(null,'Cusco','Perú',650,4.9,'Montaña','Internacional','🦙','#6D4C41')");
            db.execSQL("INSERT INTO destinos VALUES(null,'Cartagena','Colombia',700,4.8,'Playa','Internacional','🏖️','#039BE5')");
            db.execSQL("INSERT INTO destinos VALUES(null,'Cancún','México',1200,4.9,'Lujo','Internacional','✨','#FBC02D')");
            db.execSQL("INSERT INTO destinos VALUES(null,'París','Francia',2500,5.0,'Ciudad','Internacional','🗼','#5C6BC0')");
            db.execSQL("INSERT INTO destinos VALUES(null,'Bali','Indonesia',1800,4.9,'Playa','Internacional','🌺','#26C6DA')");
        }
        cursor.close();
        db.close();
    }

    public ArrayList<Destino> obtenerDestinos() {

        ArrayList<Destino> listaDestinos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM destinos", null
        );

        if (cursor.moveToFirst()) {
            do {
                Destino destino = new Destino(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getDouble(3),
                        cursor.getDouble(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8)
                );
                listaDestinos.add(destino);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listaDestinos;
    }

    public void agregarFavorito(String usuario, int destinoId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("usuario", usuario);
        values.put("destino_id", destinoId);
        db.insert("favoritos", null, values
        );
        db.close();
    }

    public void eliminarFavorito(String usuario, int destinoId) {
        SQLiteDatabase db =
                this.getWritableDatabase();
        db.delete(
                "favoritos", "usuario=? AND destino_id=?",
                new String[]{usuario, String.valueOf(destinoId)
                }
        );
        db.close();
    }

    public boolean esFavorito(String usuario, int destinoId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM favoritos WHERE usuario=? AND destino_id=?",
                new String[]{usuario, String.valueOf(destinoId)
                }
        );

        boolean existe = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return existe;
    }

    public Cursor obtenerFavoritos(String usuario) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(

                "SELECT d.* FROM destinos d " + "INNER JOIN favoritos f " +
                        "ON d.id_destino = f.destino_id " + "WHERE f.usuario=?",
                new String[]{usuario}
        );
    }

    public ArrayList<Destino> obtenerDestacados() {

        ArrayList<Destino> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(

                "SELECT * FROM destinos LIMIT 3",
                null
        );

        if (cursor.moveToFirst()) {
            do {
                Destino destino =
                        new Destino(
                                cursor.getInt(0),
                                cursor.getString(1),
                                cursor.getString(2),
                                cursor.getDouble(3),
                                cursor.getDouble(4),
                                cursor.getString(5),
                                cursor.getString(6),
                                cursor.getString(7),
                                cursor.getString(8)
                        );
                lista.add(destino);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    public ArrayList<Destino> obtenerMejoresValorados() {

        ArrayList<Destino> lista =
                new ArrayList<>();
        SQLiteDatabase db =
                this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM destinos ORDER BY calificacion DESC LIMIT 3",
                null
        );

        if (cursor.moveToFirst()) {
            do {
                Destino destino =
                        new Destino(
                                cursor.getInt(0),
                                cursor.getString(1),
                                cursor.getString(2),
                                cursor.getDouble(3),
                                cursor.getDouble(4),
                                cursor.getString(5),
                                cursor.getString(6),
                                cursor.getString(7),
                                cursor.getString(8)
                        );
                lista.add(destino);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }


}