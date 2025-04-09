package com.example.rentalcar.Modelos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Mcarros extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "dbRentCar.db";
    private static final int DATABASE_VERSION = 10;

    // Tabla carros
    public static final String TABLE_CARROS = "carros";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_MARCA = "marca";
    public static final String COLUMN_MODELO = "modelo";
    public static final String COLUMN_ANIO = "anio";
    public static final String COLUMN_PRECIO = "precio";
    public static final String COLUMN_CATEGORIA_ID = "categoria_id";

    // Tabla categorías
    private static final String TABLE_CATEGORIAS = "categorias";
    private static final String COLUMN_NOMBRE = "nombre";

    public Mcarros(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CARROS_TABLE = "CREATE TABLE " + TABLE_CARROS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_MARCA + " TEXT NOT NULL," +
                COLUMN_MODELO + " TEXT NOT NULL UNIQUE," +
                COLUMN_ANIO + " INTEGER NOT NULL," +
                COLUMN_PRECIO + " REAL NOT NULL," +
                COLUMN_CATEGORIA_ID + " INTEGER," +
                "FOREIGN KEY(" + COLUMN_CATEGORIA_ID + ") REFERENCES " +
                TABLE_CATEGORIAS + "(" + COLUMN_ID + "))";

        db.execSQL(CREATE_CARROS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARROS);
        onCreate(db);
    }

    public boolean agregarCarro(String marca, String modelo, int anio, double precio, int categoriaId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_MARCA, marca);
        values.put(COLUMN_MODELO, modelo);
        values.put(COLUMN_ANIO, anio);
        values.put(COLUMN_PRECIO, precio);
        values.put(COLUMN_CATEGORIA_ID, categoriaId);

        long result = db.insert(TABLE_CARROS, null, values);
        db.close();
        return result != -1;
    }

    public Cursor obtenerTodosCarros() {
        return getReadableDatabase().rawQuery(
                "SELECT c.*, cat." + COLUMN_NOMBRE + " as categoria_nombre " +
                        "FROM " + TABLE_CARROS + " c " +
                        "LEFT JOIN " + TABLE_CATEGORIAS + " cat ON c." + COLUMN_CATEGORIA_ID + " = cat." + COLUMN_ID,
                null
        );
    }

    public boolean actualizarCarro(int id, String marca, String modelo, int anio, double precio, int categoriaId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_MARCA, marca);
        values.put(COLUMN_MODELO, modelo);
        values.put(COLUMN_ANIO, anio);
        values.put(COLUMN_PRECIO, precio);
        values.put(COLUMN_CATEGORIA_ID, categoriaId);

        int affectedRows = db.update(
                TABLE_CARROS,
                values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        db.close();
        return affectedRows > 0;
    }

    public boolean eliminarCarro(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int affectedRows = db.delete(
                TABLE_CARROS,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        db.close();
        return affectedRows > 0;
    }
    // ... (código anterior se mantiene igual)

    // ... (código anterior se mantiene igual)

    public Cursor buscarCarros(String criterio) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Búsqueda por modelo o marca (insensible a mayúsculas)
        String query = "SELECT c.*, cat." + COLUMN_NOMBRE + " as categoria_nombre " +
                "FROM " + TABLE_CARROS + " c " +
                "LEFT JOIN " + TABLE_CATEGORIAS + " cat ON c." + COLUMN_CATEGORIA_ID + " = cat." + COLUMN_ID + " " +
                "WHERE c." + COLUMN_MODELO + " LIKE ? COLLATE NOCASE OR " +
                "c." + COLUMN_MARCA + " LIKE ? COLLATE NOCASE";

        return db.rawQuery(query, new String[]{"%" + criterio + "%", "%" + criterio + "%"});
    }

    public Cursor buscarCarrosPorCategoria(int categoriaId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT c.*, cat." + COLUMN_NOMBRE + " as categoria_nombre " +
                "FROM " + TABLE_CARROS + " c " +
                "LEFT JOIN " + TABLE_CATEGORIAS + " cat ON c." + COLUMN_CATEGORIA_ID + " = cat." + COLUMN_ID + " " +
                "WHERE c." + COLUMN_CATEGORIA_ID + " = ?";

        return db.rawQuery(query, new String[]{String.valueOf(categoriaId)});
    }

// ... (el resto del código se mantiene igual)

// ... (el resto del código se mantiene igual)

    public Cursor obtenerCategorias() {
        return getReadableDatabase().query(
                TABLE_CATEGORIAS,
                new String[]{COLUMN_ID, COLUMN_NOMBRE},
                null, null, null, null, COLUMN_NOMBRE + " ASC"
        );
    }
}