package com.example.rentalcar.Modelos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class Mcategoria extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "dbRentCar.db";
    private static final int DATABASE_VERSION = 9;

    // Tabla categorías
    public static final String TABLE_CATEGORIAS = "categorias";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOMBRE = "nombre";
    public static final String COLUMN_DESCRIPCION = "descripcion";

    // Crear la tabla SQL
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_CATEGORIAS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NOMBRE + " TEXT NOT NULL UNIQUE COLLATE NOCASE, " +
                    COLUMN_DESCRIPCION + " TEXT" +
                    ")";

    private Context context;

    public Mcategoria(Context context) {
        super(context, getDatabasePath(context), null, DATABASE_VERSION);
        this.context = context;
    }

    // Método para obtener la ruta de la base de datos en el almacenamiento externo
    private static String getDatabasePath(Context context) {
        // Usamos el contexto para obtener el path
        return context.getExternalFilesDir(null).getPath() + "/" + DATABASE_NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 9) {
            // Si es necesario, puedes agregar nuevas columnas u otras modificaciones aquí
            db.execSQL("ALTER TABLE " + TABLE_CATEGORIAS + " ADD COLUMN nueva_columna TEXT");
        }

        // Asegura que la tabla sea recreada correctamente
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIAS);
        onCreate(db);
    }
    public boolean guardarCategoria(String nombre, String descripcion) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NOMBRE, nombre);
            values.put(COLUMN_DESCRIPCION, descripcion);

            return db.insert(TABLE_CATEGORIAS, null, values) != -1;
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
        }
    }

    public Cursor obtenerTodasLasCategorias() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                TABLE_CATEGORIAS,
                new String[]{COLUMN_ID, COLUMN_NOMBRE, COLUMN_DESCRIPCION},
                null, null, null, null, COLUMN_NOMBRE + " ASC"
        );
    }

    public boolean eliminarCategoriaPorNombre(String nombre) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            return db.delete(
                    TABLE_CATEGORIAS,
                    COLUMN_NOMBRE + " = ? COLLATE NOCASE",
                    new String[]{nombre}
            ) > 0;
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