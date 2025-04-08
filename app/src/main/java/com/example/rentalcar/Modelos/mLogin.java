package com.example.rentalcar.Modelos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class mLogin extends SQLiteOpenHelper {

    // Atributos estáticos del usuario actual
    public static int codigoUsuario;
    public static String nombreUsuario;
    public static String correoUsuario;
    public static String passwordUsuario;
    public static String identidadUsuario;
    public static byte[] avatarUsuario;

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

    // Verificar credenciales
    public boolean verificarLogin(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean usuarioValido = false;

        try {
            String query = "SELECT * FROM users WHERE email = ? AND password = ?";
            cursor = db.rawQuery(query, new String[]{email, password});

            if (cursor != null && cursor.moveToFirst()) {
                usuarioValido = true;
                cargarDatosUsuario(email);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            cargarDatosUsuario(email);
            db.close();
        }

        return usuarioValido;
    }

    // Actualizar perfil
    public boolean actualizarPerfil(int codigo, String name, String email, String password, String identity, byte[] avatar) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("email", email);
        values.put("password", password);
        values.put("identity", identity);
        values.put("avatar", avatar);

        int filas = db.update("users", values, "codigo = ?", new String[]{String.valueOf(codigo)});
        db.close();
        return filas > 0;
    }

    // Cargar los datos del usuario actual
    public void cargarDatosUsuario(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String query = "SELECT * FROM users WHERE email = ?";
            cursor = db.rawQuery(query, new String[]{email});

            if (cursor != null && cursor.moveToFirst()) {
                int idx;

                if ((idx = cursor.getColumnIndex("codigo")) != -1)
                    codigoUsuario = cursor.getInt(idx);

                if ((idx = cursor.getColumnIndex("name")) != -1)
                    nombreUsuario = cursor.getString(idx);

                if ((idx = cursor.getColumnIndex("email")) != -1)
                    correoUsuario = cursor.getString(idx);

                if ((idx = cursor.getColumnIndex("password")) != -1)
                    passwordUsuario = cursor.getString(idx);

                if ((idx = cursor.getColumnIndex("identity")) != -1)
                    identidadUsuario = cursor.getString(idx);

                if ((idx = cursor.getColumnIndex("avatar")) != -1)
                    avatarUsuario = cursor.getBlob(idx);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
    }
}
