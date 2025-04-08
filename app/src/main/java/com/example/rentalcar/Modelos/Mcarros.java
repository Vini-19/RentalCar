package com.example.rentalcar.Modelos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class Mcarros extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "dbRentCar.db";
    private static final int DATABASE_VERSION = 2; // Incrementado a 2

    // Nombre de la tabla y columnas
    public static final String TABLE_CARROS = "carros";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_MARCA = "marca";
    public static final String COLUMN_MODELO = "modelo";
    public static final String COLUMN_ANIO = "anio";
    public static final String COLUMN_PRECIO = "precio";
    public static final String COLUMN_CATEGORIA_ID = "categoria_id";

    public Mcarros(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_CARROS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_MARCA + " TEXT," +
                COLUMN_MODELO + " TEXT," +
                COLUMN_ANIO + " INTEGER," +
                COLUMN_PRECIO + " REAL," +
                COLUMN_CATEGORIA_ID + " INTEGER)";
        db.execSQL(CREATE_TABLE);
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
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT carros.*, categorias.nombre as categoria_nombre FROM carros " +
                "LEFT JOIN categorias ON carros.categoria_id = categorias.id", null);
    }

    public boolean actualizarCarro(int id, String marca, String modelo, int anio, double precio, int categoriaId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MARCA, marca);
        values.put(COLUMN_MODELO, modelo);
        values.put(COLUMN_ANIO, anio);
        values.put(COLUMN_PRECIO, precio);
        values.put(COLUMN_CATEGORIA_ID, categoriaId);

        int result = db.update(TABLE_CARROS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    public boolean eliminarCarro(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_CARROS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }


    public Cursor obtenerCategorias() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM categorias", null);
    }
}