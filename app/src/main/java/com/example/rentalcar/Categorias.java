package com.example.rentalcar;

import static com.example.rentalcar.Modelos.Mcategoria.COLUMN_DESCRIPCION;
import static com.example.rentalcar.Modelos.Mcategoria.COLUMN_NOMBRE;
import static com.example.rentalcar.Modelos.Mcategoria.TABLE_CATEGORIAS;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rentalcar.Modelos.Mcategoria;

public class Categorias extends AppCompatActivity {
    private EditText txtNombre, txtDescripcion;
    private Mcategoria dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);

        txtNombre = findViewById(R.id.txtNombre);
        txtDescripcion = findViewById(R.id.txtDescripc); // ¡Corrige esto! Estaba usando txtNombre dos veces


        dbHelper = new Mcategoria(this);
        cargarCategorias();
    }

    public void guardarCategoria(View view) {
        String nombre = txtNombre.getText().toString().trim();
        String descripcion = txtDescripcion.getText().toString().trim();

        // Validaciones básicas
        if (nombre.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar si existe
        if (dbHelper.existeCategoria(nombre)) {
            new AlertDialog.Builder(this)
                    .setTitle("Categoría existente")
                    .setMessage("La categoría '" + nombre + "' ya existe. ¿Desea actualizar su descripción?")
                    .setPositiveButton("Actualizar", (dialog, which) -> {
                        if (actualizarCategoria(nombre, descripcion)) {
                            Toast.makeText(this, "Descripción actualizada", Toast.LENGTH_SHORT).show();
                            cargarCategorias();
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        } else {
            // Guardar nueva categoría
            if (dbHelper.guardarCategoria(nombre, descripcion)) {
                Toast.makeText(this, "Categoría creada", Toast.LENGTH_SHORT).show();
                cargarCategorias();
            }
        }
    }

    private boolean actualizarCategoria(String nombre, String nuevaDescripcion) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_DESCRIPCION, nuevaDescripcion);

            int filasActualizadas = db.update(
                    TABLE_CATEGORIAS,
                    values,
                    COLUMN_NOMBRE + " = ? COLLATE NOCASE",
                    new String[]{nombre}
            );

            return filasActualizadas > 0;
        } finally {
            db.close();
        }
    }
    public void buscarCategoria(View view) {
        String textoBusqueda = txtNombre.getText().toString().trim();
        TableLayout tabla = findViewById(R.id.TablaCategorias);

        // Limpiar todas las filas excepto los encabezados
        int childCount = tabla.getChildCount();
        if (childCount > 1) {
            tabla.removeViews(1, childCount - 1);
        }

        if (textoBusqueda.isEmpty()) {
            mostrarMensajeSimple(tabla, "🔍 Ingresa un nombre para buscar");
            return;
        }

        try (Cursor cursor = dbHelper.obtenerCategoriaPorNombre(textoBusqueda)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Obtener datos de la categoría encontrada
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
                    String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));

                    // Crear nueva fila
                    TableRow fila = new TableRow(this);
                    fila.setLayoutParams(new TableRow.LayoutParams(
                            TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT));

                    // Configurar celdas
                    TextView tvId = crearCeldaSimple(String.valueOf(id), 1f);
                    TextView tvNombre = crearCeldaSimple(nombre, 2f);
                    TextView tvDescripcion = crearCeldaSimple(descripcion, 3f);

                    // Añadir celdas a la fila
                    fila.addView(tvId);
                    fila.addView(tvNombre);
                    fila.addView(tvDescripcion);

                    // Añadir fila a la tabla
                    tabla.addView(fila);

                } while (cursor.moveToNext());

                Toast.makeText(this, "✅ Categoría encontrada", Toast.LENGTH_SHORT).show();
            } else {
                mostrarMensajeSimple(tabla, "❌ No existe: \"" + textoBusqueda + "\"");
            }
        } catch (Exception e) {
            mostrarMensajeSimple(tabla, "Error al buscar categoría");
            Log.e("Categorias", "Error al buscar categoría", e);
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close(); // Cerrar la conexión a la BD al salir
        super.onDestroy();
    }


    public void Menu(View view){

        Intent Menu = new Intent(this,Menu.class);
        startActivity(Menu);
    }

    private void cargarCategorias() {
        TableLayout tabla = findViewById(R.id.TablaCategorias);

        // Limpiar todas las filas excepto los encabezados (posición 0)
        int childCount = tabla.getChildCount();
        if (childCount > 1) {
            tabla.removeViews(1, childCount - 1);
        }

        try (Cursor cursor = dbHelper.obtenerTodasLasCategorias()) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Obtener datos de cada categoría
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
                    String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));

                    // Crear nueva fila
                    TableRow fila = new TableRow(this);
                    fila.setLayoutParams(new TableRow.LayoutParams(
                            TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT));

                    // Configurar celdas
                    TextView tvId = crearCeldaSimple(String.valueOf(id), 1f);
                    TextView tvNombre = crearCeldaSimple(nombre, 2f);
                    TextView tvDescripcion = crearCeldaSimple(descripcion, 3f);

                    // Añadir celdas a la fila
                    fila.addView(tvId);
                    fila.addView(tvNombre);
                    fila.addView(tvDescripcion);

                    // Añadir fila a la tabla
                    tabla.addView(fila);

                } while (cursor.moveToNext());
            } else {
                mostrarMensajeSimple(tabla, "No hay categorías registradas");
            }
        } catch (Exception e) {
            mostrarMensajeSimple(tabla, "Error al cargar datos");
            Log.e("Categorias", "Error al cargar categorías", e);
        }
    }

    // Método auxiliar simplificado para crear celdas
    private TextView crearCeldaSimple(String texto, float peso) {
        TextView tv = new TextView(this);
        tv.setText(texto);
        tv.setPadding(16, 12, 16, 12);
        tv.setTextSize(14);

        TableRow.LayoutParams params = new TableRow.LayoutParams(
                0, TableRow.LayoutParams.WRAP_CONTENT, peso);
        tv.setLayoutParams(params);

        return tv;
    }

    // Método para mostrar mensajes
    private void mostrarMensajeSimple(TableLayout tabla, String mensaje) {
        TableRow fila = new TableRow(this);
        TextView tv = new TextView(this);
        tv.setText(mensaje);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(0, 20, 0, 20);

        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        params.span = 3;

        fila.addView(tv, params);
        tabla.addView(fila);
    }
    public void eliminarCategoria(View view) {
        String nombre = txtNombre.getText().toString().trim();

        if (nombre.isEmpty()) {
            Toast.makeText(this, "Ingrese el nombre de la categoría a eliminar", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean exito = dbHelper.eliminarCategoriaPorNombre(nombre);

        if (exito) {
            Toast.makeText(this, "Categoría eliminada correctamente", Toast.LENGTH_SHORT).show();
            txtNombre.setText("");
            cargarCategorias(); // Actualizar la tabla
        } else {
            Toast.makeText(this, "No se encontró la categoría o hubo un error", Toast.LENGTH_SHORT).show();
        }
    }

    public void LimpiarFiltros(View view) {
        txtNombre.setText("");
        txtDescripcion.setText("");
        cargarCategorias(); // Recargar todas las categorías
    }
}