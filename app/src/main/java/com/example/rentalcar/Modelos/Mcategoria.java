package com.example.rentalcar.Modelos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Mcategoria extends SQLiteOpenHelper {
    // Configuración mejorada de la base de datos
    private static final String DATABASE_NAME = "rental_car.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "Mcategoria";

    // Estructura de la tabla (manteniendo tus constantes originales)
    public static final String TABLE_CATEGORIAS = "categorias";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOMBRE = "nombre";
    public static final String COLUMN_DESCRIPCION = "descripcion";
    public static final String COLUMN_CREATED_AT = "created_at";

    // Sentencias SQL mejoradas
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_CATEGORIAS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NOMBRE + " TEXT NOT NULL UNIQUE COLLATE NOCASE, " +
                    COLUMN_DESCRIPCION + " TEXT, " +
                    COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";

    public Mcategoria(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // Habilitar foreign keys
        getWritableDatabase().execSQL("PRAGMA foreign_keys=ON;");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(SQL_CREATE_TABLE);
            Log.i(TAG, "Tabla de categorías creada correctamente");
            insertarDatosIniciales(db);
        } catch (Exception e) {
            Log.e(TAG, "Error al crear la tabla: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Actualizando BD de versión " + oldVersion + " a " + newVersion);
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIAS);
            onCreate(db);
        } catch (Exception e) {
            Log.e(TAG, "Error durante la migración: " + e.getMessage());
        }
    }

    // Métodos existentes (sin cambios en su funcionalidad)
    public boolean guardarCategoria(String nombre, String descripcion) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NOMBRE, nombre);
            values.put(COLUMN_DESCRIPCION, descripcion);

            return db.insert(TABLE_CATEGORIAS, null, values) != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error guardando categoría: " + e.getMessage());
            return false;
        }
    }

    public boolean existeCategoria(String nombreBusqueda) {
        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.query(
                     TABLE_CATEGORIAS,
                     new String[]{COLUMN_NOMBRE},
                     COLUMN_NOMBRE + " = ? COLLATE NOCASE",
                     new String[]{nombreBusqueda},
                     null, null, null)) {
            return cursor.getCount() > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error verificando categoría: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarDescripcion(String nombre, String nuevaDescripcion) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_DESCRIPCION, nuevaDescripcion);

            return db.update(
                    TABLE_CATEGORIAS,
                    values,
                    COLUMN_NOMBRE + " = ? COLLATE NOCASE",
                    new String[]{nombre}
            ) > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error actualizando categoría: " + e.getMessage());
            return false;
        }
    }

    public Cursor obtenerTodasLasCategorias() {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            return db.query(
                    TABLE_CATEGORIAS,
                    new String[]{COLUMN_ID, COLUMN_NOMBRE, COLUMN_DESCRIPCION},
                    null, null, null, null, COLUMN_NOMBRE + " ASC"
            );
        } catch (Exception e) {
            Log.e(TAG, "Error obteniendo categorías: " + e.getMessage());
            return null;
        }
    }

    public boolean eliminarCategoriaPorNombre(String nombre) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            return db.delete(
                    TABLE_CATEGORIAS,
                    COLUMN_NOMBRE + " = ? COLLATE NOCASE",
                    new String[]{nombre}
            ) > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error eliminando categoría: " + e.getMessage());
            return false;
        }
    }

    public Cursor obtenerCategoriaPorNombre(String nombre) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            return db.query(
                    TABLE_CATEGORIAS,
                    new String[]{COLUMN_ID, COLUMN_NOMBRE, COLUMN_DESCRIPCION},
                    COLUMN_NOMBRE + " = ? COLLATE NOCASE",
                    new String[]{nombre},
                    null, null, null
            );
        } catch (Exception e) {
            Log.e(TAG, "Error buscando categoría: " + e.getMessage());
            return null;
        }
    }

    // Nuevos métodos añadidos para mejor portabilidad
    private void insertarDatosIniciales(SQLiteDatabase db) {
        String[][] categoriasIniciales = {
                {"Sedán", "Vehículos de turismo de 4 puertas"},
                {"SUV", "Vehículos utilitarios deportivos"},
                {"Compacto", "Vehículos pequeños para ciudad"}
        };

        try {
            db.beginTransaction();
            for (String[] categoria : categoriasIniciales) {
                db.execSQL("INSERT INTO " + TABLE_CATEGORIAS + " (" +
                                COLUMN_NOMBRE + ", " + COLUMN_DESCRIPCION + ") VALUES (?, ?)",
                        categoria);
            }
            db.setTransactionSuccessful();
            Log.i(TAG, "Datos iniciales insertados correctamente");
        } catch (Exception e) {
            Log.e(TAG, "Error insertando datos iniciales: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    public static void resetDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
        Log.i(TAG, "Base de datos reiniciada");
    }
}