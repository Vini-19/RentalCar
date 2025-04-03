package com.example.rentalcar.Modelos;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class mClientes extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "dbRentCar.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TablaClientes= "Clientes" ;
    public static final String ColumnIdcliente= "idCliente" ;
    public static final String ColumnNumCuenta= "numCuenta" ;
    public static final String ColumnNombre= "NomCliente" ;
    public static final String ColumnTelefono= "Telefono" ;
    public static final String ColumnCorreo= "Correo" ;
    public static final String ColumnFecha= "Fecha" ;
    private static final String CREATE_TABLE_CLIENTES =
            "CREATE TABLE IF NOT EXISTS " + TablaClientes+ " (" +
                    ColumnIdcliente + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ColumnNumCuenta + " TEXT NOT NULL, " +
                    ColumnNombre + " TEXT NOT NULL, " +
                    ColumnTelefono + " TEXT NOT NULL, " +
                    ColumnCorreo + " TEXT NOT NULL UNIQUE," +
                    ColumnFecha + " TEXT NOT NULL" +
                    ")";
    public mClientes(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_CLIENTES);
            Log.d("DB", "Tabla 'Clientes' creada correctamente");
        } catch (Exception e) {
            Log.e("DB", "Error al crear la tabla 'Clientes': " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TablaClientes);
        onCreate(db);
    }
}
