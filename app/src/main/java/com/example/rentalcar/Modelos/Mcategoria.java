package com.example.rentalcar.Modelos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Mcategoria extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "dbRentCar.db";  // Añadido .db para claridad
    private static final int DATABASE_VERSION = 2;

    // Nombre de la tabla y columnas
    public static final String TABLE_CATEGORIAS = "categorias";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOMBRE = "nombre";
    public static final String COLUMN_DESCRIPCION = "descripcion";

    // Sentencia SQL para crear la tabla
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_CATEGORIAS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NOMBRE + " TEXT NOT NULL, " +
                    COLUMN_DESCRIPCION + " TEXT" +
                    ")";

    public Mcategoria(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE);
            Log.d("DB", "Tabla creada correctamente");
        } catch (Exception e) {
            Log.e("DB", "Error al crear la tabla: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIAS);
        onCreate(db);
    }

    // Método para guardar una categoría (retorna `true` si se guardó correctamente)
    public boolean guardarCategoria(String nombre, String descripcion) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOMBRE, nombre);
        values.put(COLUMN_DESCRIPCION, descripcion);

        long resultado = -1;
        try {
            resultado = db.insertOrThrow(TABLE_CATEGORIAS, null, values);
            Log.d("DB", "Inserción exitosa, ID: " + resultado);
        } catch (Exception e) {
            Log.e("DB", "Error al insertar: " + e.getMessage());
        } finally {
            db.close(); // Siempre cerrar la conexión
        }
        return resultado != -1;
    }

    // Método para obtener todas las categorías
    public Cursor obtenerCategorias() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                TABLE_CATEGORIAS,
                new String[]{COLUMN_ID, COLUMN_NOMBRE, COLUMN_DESCRIPCION},
                null, null, null, null, null
        );
    }

    // Busca categorías por nombre (coincidencia parcial, insensible a mayúsculas)
    // Nuevo método que solo devuelve nombres
    // Verifica si la categoría existe (comparación insensible a mayúsculas)
    public boolean existeCategoria(String nombreBusqueda) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean existe = false;

        Cursor cursor = db.query(
                TABLE_CATEGORIAS,
                new String[]{COLUMN_NOMBRE},
                COLUMN_NOMBRE + " = ? COLLATE NOCASE", // Búsqueda exacta sin importar mayúsculas
                new String[]{nombreBusqueda},
                null, null, null
        );

        if (cursor != null) {
            existe = cursor.getCount() > 0; // True si encontró coincidencias
            cursor.close();
        }
        db.close();
        return existe;
    }

    // Método para obtener todas las categorías
    public Cursor obtenerTodasLasCategorias() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM categorias", null);
    }
    // Método para eliminar categoría por nombre (insensible a mayúsculas)
    public boolean eliminarCategoriaPorNombre(String nombre) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            int filasEliminadas = db.delete(
                    TABLE_CATEGORIAS,
                    COLUMN_NOMBRE + " = ? COLLATE NOCASE",
                    new String[]{nombre}
            );
            return filasEliminadas > 0;
        } catch (Exception e) {
            Log.e("DB", "Error al eliminar categoría: " + e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }
    public Cursor obtenerCategoriaPorNombre(String nombre) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                TABLE_CATEGORIAS,
                new String[]{COLUMN_ID, COLUMN_NOMBRE, COLUMN_DESCRIPCION},
                COLUMN_NOMBRE + " = ? COLLATE NOCASE",
                new String[]{nombre},
                null, null, null
        );
    }
}