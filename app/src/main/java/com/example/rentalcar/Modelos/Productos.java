package com.example.rentalcar.Modelos;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;
public class Productos extends SQLiteOpenHelper {

    // Constructor
    public Productos(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // Método onCreate
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear tabla productos
        String createTableProductos = "CREATE TABLE IF NOT EXISTS productos (" +
                "idProducto INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT, " +
                "idCategoria INTEGER, " + // Relacionado con la tabla categorias
                "descripcion TEXT, " +
                "precio REAL, " + // El precio será un valor flotante
                "FOREIGN KEY (idCategoria) REFERENCES categorias(id));"; // Clave foránea con la tabla categorias
        db.execSQL(createTableProductos);
    }

    // Método onUpgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Si la tabla existe, la eliminamos y creamos una nueva
        db.execSQL("DROP TABLE IF EXISTS productos");
        onCreate(db);
    }

    // Método para insertar un producto
    public boolean insertarProducto(String nombre, int idCategoria, String descripcion, double precio) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean productoInsertado = false;

        try {
            // Usamos ContentValues para insertar el producto
            ContentValues values = new ContentValues();
            values.put("nombre", nombre);
            values.put("idCategoria", idCategoria);
            values.put("descripcion", descripcion);
            values.put("precio", precio);

            long result = db.insert("productos", null, values); // Insertamos en la tabla
            if (result != -1) {
                productoInsertado = true;
            }
        } catch (Exception e) {
            e.printStackTrace(); // Captura cualquier excepción
        } finally {
            db.close(); // Cerramos la base de datos
        }

        return productoInsertado;
    }

    // Método para obtener todos los productos
    public Cursor obtenerProductos() {
        SQLiteDatabase db = this.getReadableDatabase();
        // Consulta para obtener todos los productos junto con su categoría
        String query = "SELECT * FROM productos p JOIN categorias c ON p.idCategoria = c.id";
        return db.rawQuery(query, null);
    }

    // Método para obtener un producto por su ID
    public Cursor obtenerProductoPorId(int idProducto) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Consulta para obtener el producto por su ID
        String query = "SELECT * FROM productos WHERE idProducto = ?";
        return db.rawQuery(query, new String[]{String.valueOf(idProducto)});
    }

    // Método para actualizar un producto
    public boolean actualizarProducto(int idProducto, String nombre, int idCategoria, String descripcion, double precio) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean productoActualizado = false;

        try {
            // Usamos ContentValues para actualizar el producto
            ContentValues values = new ContentValues();
            values.put("nombre", nombre);
            values.put("idCategoria", idCategoria);
            values.put("descripcion", descripcion);
            values.put("precio", precio);

            int result = db.update("productos", values, "idProducto = ?", new String[]{String.valueOf(idProducto)});
            if (result > 0) {
                productoActualizado = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close(); // Cerramos la base de datos
        }

        return productoActualizado;
    }

    // Método para eliminar un producto
    public boolean eliminarProducto(int idProducto) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean productoEliminado = false;

        try {
            int result = db.delete("productos", "idProducto = ?", new String[]{String.valueOf(idProducto)});
            if (result > 0) {
                productoEliminado = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close(); // Cerramos la base de datos
        }

        return productoEliminado;
    }
}
