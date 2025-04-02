package com.example.rentalcar.Modelos;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class mLogin extends SQLiteOpenHelper {

    // Constructor
    public mLogin(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // Método onCreate
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS users (codigo INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, email TEXT, password TEXT, identity TEXT, avatar BLOB)");
    }

    // Método onUpgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    // Método para verificar el login
    public boolean verificarLogin(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean usuarioValido = false;

        try {
            // Consulta SQL para verificar si el usuario con ese email y contraseña existe
            String query = "SELECT * FROM users WHERE email = ? AND password = ?";
            cursor = db.rawQuery(query, new String[]{email, password});

            // Si hay registros que coinciden
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst(); // Nos aseguramos de movernos al primer registro encontrado
                usuarioValido = true;  // Si encuentra el usuario, es válido
            }
        } catch (Exception e) {
            e.printStackTrace(); // Captura cualquier excepción
        } finally {
            // Cerramos el cursor y la base de datos de manera segura
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return usuarioValido;
    }
}
