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

    public boolean verificarLogin(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean usuarioValido = false;

        try {

            String query = "SELECT * FROM users WHERE email = ? AND password = ?";
            cursor = db.rawQuery(query, new String[]{email, password});


            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                usuarioValido = true;
            }
        } catch (Exception e) {
            e.printStackTrace(); 
        } finally {

            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return usuarioValido;
    }
}
