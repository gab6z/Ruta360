package com.example.myprimeraapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class BaseDatosSQLite extends SQLiteOpenHelper {

    public static final String dbName = "Ruta360.db";
    public static final int Version = 4;

    public static final String tablaUsuario = "CREATE TABLE usuario (id INTEGER PRIMARY KEY AUTOINCREMENT, cedula TEXT, nombres TEXT,apellidos TEXT,edad INTEGER,nacionalidad TEXT, genero TEXT, estadoCivil TEXT, correo TEXT, contraseña TEXT, fechaNac TEXT, nivelIngles REAL)";

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

    public static final String tablaTarifas =
            "CREATE TABLE tarifas (" +
                    "id_tarifa INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "origen TEXT, " +
                    "destino_id INTEGER, " +
                    "precio DOUBLE)";

    public static final String tablaPaquetes = "CREATE TABLE paquetes (" +
            "id_paquete INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "destino TEXT, " +
            "alojamiento TEXT, " +
            "alimentacion TEXT, " +
            "transporte TEXT, " +
            "precio_total REAL)";

    public BaseDatosSQLite(Context context) {
        super(context, dbName, null, Version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(tablaUsuario);
        sqLiteDatabase.execSQL(tablaReservas);
        sqLiteDatabase.execSQL(tablaDestinos);
        sqLiteDatabase.execSQL(tablaFavoritos);
        sqLiteDatabase.execSQL(tablaTarifas);
        sqLiteDatabase.execSQL(tablaPaquetes);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS usuario");
        db.execSQL("DROP TABLE IF EXISTS reservas");
        db.execSQL("DROP TABLE IF EXISTS destinos");
        db.execSQL("DROP TABLE IF EXISTS favoritos");
        db.execSQL("DROP TABLE IF EXISTS tarifas");
        db.execSQL("DROP TABLE IF EXISTS paquetes");
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
        ContentValues values = new ContentValues();
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
        ContentValues values = new ContentValues();
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


    public void insertarDestinosIniciales() {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM destinos", null
        );

        if (cursor.getCount() == 0) {

            // DESTINOS
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

            // TARIFAS DESDE GUAYAQUIL
            db.execSQL("INSERT INTO tarifas VALUES(null,'Guayaquil',1,250)");
            db.execSQL("INSERT INTO tarifas VALUES(null,'Guayaquil',2,180)");
            db.execSQL("INSERT INTO tarifas VALUES(null,'Guayaquil',3,900)");
            db.execSQL("INSERT INTO tarifas VALUES(null,'Guayaquil',4,220)");
            db.execSQL("INSERT INTO tarifas VALUES(null,'Guayaquil',5,150)");
            db.execSQL("INSERT INTO tarifas VALUES(null,'Guayaquil',6,650)");
            db.execSQL("INSERT INTO tarifas VALUES(null,'Guayaquil',7,700)");
            db.execSQL("INSERT INTO tarifas VALUES(null,'Guayaquil',8,1200)");
            db.execSQL("INSERT INTO tarifas VALUES(null,'Guayaquil',9,2500)");
            db.execSQL("INSERT INTO tarifas VALUES(null,'Guayaquil',10,1800)");

            // TARIFAS DESDE QUITO
            db.execSQL("INSERT INTO tarifas VALUES(null,'Quito',1,280)");
            db.execSQL("INSERT INTO tarifas VALUES(null,'Quito',2,200)");
            db.execSQL("INSERT INTO tarifas VALUES(null,'Quito',3,950)");
            db.execSQL("INSERT INTO tarifas VALUES(null,'Quito',4,250)");
            db.execSQL("INSERT INTO tarifas VALUES(null,'Quito',5,170)");
            db.execSQL("INSERT INTO tarifas VALUES(null,'Quito',6,720)");
            db.execSQL("INSERT INTO tarifas VALUES(null,'Quito',7,760)");
            db.execSQL("INSERT INTO tarifas VALUES(null,'Quito',8,1350)");
            db.execSQL("INSERT INTO tarifas VALUES(null,'Quito',9,2700)");
            db.execSQL("INSERT INTO tarifas VALUES(null,'Quito',10,1950)");
        }

        cursor.close();
        db.close();
    }

    public ArrayList<Destino> obtenerDestinos() {
        ArrayList<Destino> listaDestinos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM destinos", null);

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

    public ArrayList obtenerDestinosPorOrigen(String origen) {

        ArrayList<Destino> listaDestinos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(

                "SELECT d.id_destino, " +
                        "d.nombre, " +
                        "d.ubicacion, " +
                        "t.precio, " +
                        "d.calificacion, " +
                        "d.categoria, " +
                        "d.tipo, " +
                        "d.icono, " +
                        "d.color " +
                        "FROM destinos d " +
                        "INNER JOIN tarifas t " +
                        "ON d.id_destino = t.destino_id " +
                        "WHERE t.origen = ?",
                new String[]{origen}

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
        db.insert("favoritos", null, values);
        db.close();
    }

    public void eliminarFavorito(String usuario, int destinoId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("favoritos", "usuario=? AND destino_id=?", new String[]{usuario, String.valueOf(destinoId)});
        db.close();
    }

    public boolean esFavorito(String usuario, int destinoId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM favoritos WHERE usuario=? AND destino_id=?",
                new String[]{usuario, String.valueOf(destinoId)}
        );
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return existe;
    }

    public Cursor obtenerFavoritos(String usuario) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT d.* FROM destinos d INNER JOIN favoritos f ON d.id_destino = f.destino_id WHERE f.usuario=?",
                new String[]{usuario}
        );
    }

    public ArrayList<Destino> obtenerDestacados() {
        ArrayList<Destino> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM destinos LIMIT 3", null);

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
                lista.add(destino);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    public ArrayList<Destino> obtenerMejoresValorados() {
        ArrayList<Destino> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM destinos ORDER BY calificacion DESC LIMIT 3", null);

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
                lista.add(destino);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }


    public long guardarPaquete(String destino, String hotel, String comida, String trans, double total) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("destino", destino);
        values.put("alojamiento", hotel);
        values.put("alimentacion", comida);
        values.put("transporte", trans);
        values.put("precio_total", total);
        long id = db.insert("paquetes", null, values);
        db.close();
        return id;
    }

    public ArrayList<String[]> obtenerPaquetes() {
        ArrayList<String[]> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM paquetes", null);
        if (cursor.moveToFirst()) {
            do {
                String[] paquete = new String[]{
                        cursor.getString(0), // id
                        cursor.getString(1), // destino
                        cursor.getString(2), // alojamiento
                        cursor.getString(3), // alimentacion
                        cursor.getString(4), // transporte
                        cursor.getString(5)  // precio_total
                };
                lista.add(paquete);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    public boolean eliminarPaquete(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int filas = db.delete("paquetes", "id_paquete=?", new String[]{String.valueOf(id)});
        db.close();
        return filas > 0;
    }

    public boolean actualizarPaquete(int id, String destino, String alojamiento, String alimentacion, String transporte, double total) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("destino", destino);
        values.put("alojamiento", alojamiento);
        values.put("alimentacion", alimentacion);
        values.put("transporte", transporte);
        values.put("precio_total", total);
        int filas = db.update("paquetes", values, "id_paquete=?", new String[]{String.valueOf(id)});
        db.close();
        return filas > 0;
    }

    public ArrayList<Destino> buscarDestinosPorNombre(String consulta) {
        ArrayList<Destino> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM destinos WHERE nombre LIKE ?", new String[]{"%" + consulta + "%"});

        if (cursor.moveToFirst()) {
            do {
                lista.add(new Destino(
                        cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                        cursor.getDouble(3), cursor.getDouble(4), cursor.getString(5),
                        cursor.getString(6), cursor.getString(7), cursor.getString(8)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }


    public boolean actualizarReserva(int idReserva, String nuevaFecha, String nuevoMetodo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("fecha_viaje", nuevaFecha);
        values.put("metodo_pago", nuevoMetodo);

        int resultado = db.update("reservas", values, "id_reserva = ?", new String[]{String.valueOf(idReserva)});
        db.close();
        return resultado > 0;
    }

    public void eliminarReserva(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("reservas", "id_reserva = ?", new String[]{String.valueOf(id)});
        db.close();
    }

}