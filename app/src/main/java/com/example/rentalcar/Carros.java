package com.example.rentalcar;

import static com.example.rentalcar.Modelos.Mcarros.COLUMN_ID;
import static com.example.rentalcar.Modelos.Mcarros.COLUMN_MODELO;
import static com.example.rentalcar.Modelos.Mcarros.TABLE_CARROS;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rentalcar.Modelos.Mcarros;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Carros extends AppCompatActivity {
    private Mcarros dbHelper;
    private EditText etMarca, etModelo, etAnio, etPrecio;
    private Spinner spinnerCategorias;
    private TableLayout tablaCarros;
    private int selectedCategoriaId = -1;
    private Map<String, Integer> categoriasMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carros);

        dbHelper = new Mcarros(this);

        etMarca = findViewById(R.id.txtMarca);
        etModelo = findViewById(R.id.txtModelo);
        etAnio = findViewById(R.id.txtAnio);
        etPrecio = findViewById(R.id.txtPrecio);
        spinnerCategorias = findViewById(R.id.spinnerCategorias);
        tablaCarros = findViewById(R.id.TablaCarros);

        cargarCategorias();
        cargarCarros();

    }

    private void cargarCategorias() {
        Cursor cursor = dbHelper.obtenerCategorias();
        List<String> categorias = new ArrayList<>();
        categorias.add("Seleccione una categoría");
        categoriasMap.clear();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String nombre = cursor.getString(1);
                categorias.add(nombre);
                categoriasMap.put(nombre, id);
            } while (cursor.moveToNext());
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategorias.setAdapter(adapter);

        spinnerCategorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String categoria = parent.getItemAtPosition(position).toString();
                    selectedCategoriaId = categoriasMap.get(categoria);
                } else {
                    selectedCategoriaId = -1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategoriaId = -1;
            }
        });
    }

    private void cargarCarros() {
        // Limpiar tabla excepto encabezados
        int childCount = tablaCarros.getChildCount();
        if (childCount > 1) {
            tablaCarros.removeViews(1, childCount - 1);
        }

        Cursor cursor = dbHelper.obtenerTodosCarros();
        if (cursor.moveToFirst()) {
            do {
                TableRow row = new TableRow(this);

                TextView tvId = crearTextView(String.valueOf(cursor.getInt(0)), 1);
                TextView tvMarca = crearTextView(cursor.getString(1), 1.5f);
                TextView tvModelo = crearTextView(cursor.getString(2), 1.5f);
                TextView tvAnio = crearTextView(String.valueOf(cursor.getInt(3)), 1);
                TextView tvPrecio = crearTextView("$" + cursor.getDouble(4), 1.5f);
                TextView tvCategoria = crearTextView(cursor.getString(6), 2);

                row.addView(tvId);
                row.addView(tvMarca);
                row.addView(tvModelo);
                row.addView(tvAnio);
                row.addView(tvPrecio);
                row.addView(tvCategoria);

                tablaCarros.addView(row);
            } while (cursor.moveToNext());
        } else {
            agregarMensajeTabla("No hay carros registrados");
        }
        cursor.close();
    }

    private TextView crearTextView(String text, float weight) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                0, TableRow.LayoutParams.WRAP_CONTENT, weight);
        textView.setLayoutParams(params);
        return textView;
    }

    private void agregarMensajeTabla(String mensaje) {
        TableRow row = new TableRow(this);
        TextView textView = new TextView(this);
        textView.setText(mensaje);
        textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        params.span = 6;
        row.addView(textView, params);
        tablaCarros.addView(row);
    }

    public void guardarCarros(View view) {
        String marca = etMarca.getText().toString().trim();
        String modelo = etModelo.getText().toString().trim();
        String anioStr = etAnio.getText().toString().trim();
        String precioStr = etPrecio.getText().toString().trim();

        if (marca.isEmpty() || modelo.isEmpty() || anioStr.isEmpty() || precioStr.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedCategoriaId == -1) {
            Toast.makeText(this, "Seleccione una categoría", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int anio = Integer.parseInt(anioStr);
            double precio = Double.parseDouble(precioStr);

            boolean success = dbHelper.agregarCarro(marca, modelo, anio, precio, selectedCategoriaId);
            if (success) {
                Toast.makeText(this, "Carro guardado exitosamente", Toast.LENGTH_SHORT).show();
                limpiarCampos();
                cargarCarros();
            } else {
                Toast.makeText(this, "Error al guardar el carro", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Año y precio deben ser números válidos", Toast.LENGTH_SHORT).show();
        }
    }

    public void buscarCarro(View view) {
        String marca = etMarca.getText().toString().trim();
        // Implementar búsqueda según necesidad
        Toast.makeText(this, "Funcionalidad de búsqueda en desarrollo", Toast.LENGTH_SHORT).show();
    }

    public void eliminarCarro(View view) {
        String modelo = etModelo.getText().toString().trim(); // Aquí tomas el texto del EditText
        if (modelo.isEmpty()) {
            Toast.makeText(this, "Ingrese el modelo a eliminar", Toast.LENGTH_SHORT).show();
            return;
        }

        // Buscar el carro por su modelo para obtener su ID
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_CARROS,
                new String[]{COLUMN_ID},
                COLUMN_MODELO + " = ? COLLATE NOCASE",
                new String[]{modelo},
                null, null, null

        );

        if (cursor != null && cursor.moveToFirst()) {
            int carroId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            cursor.close();

            // Ahora eliminamos el carro utilizando el ID
            boolean eliminado = dbHelper.eliminarCarro(carroId);
            if (eliminado) {
                Toast.makeText(this, "Carro eliminado correctamente", Toast.LENGTH_SHORT).show();
                cargarCarros();
                // Aquí puedes actualizar la lista de carros si tienes alguna vista que las muestre
            } else {
                Toast.makeText(this, "Error al eliminar el carro", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (cursor != null) {
                cursor.close();
            }
            Toast.makeText(this, "No se encontró el carro con ese modelo", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }


    private void limpiarCampos() {
        etMarca.setText("");
        etModelo.setText("");
        etAnio.setText("");
        etPrecio.setText("");
        spinnerCategorias.setSelection(0);
    }

    public void Cat(View view) {
        startActivity(new Intent(this, Categorias.class));
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }

    public void LimpiarFiltros(View view) {
        etMarca.setText("");
        etModelo.setText("");
        etAnio.setText("");
        etPrecio.setText("");
        cargarCategorias(); // Recargar todas las categorías
    }


}