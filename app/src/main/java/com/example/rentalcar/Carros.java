package com.example.rentalcar;

import static com.example.rentalcar.Modelos.Mcarros.*;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.rentalcar.Modelos.Mcarros;
import java.util.*;

public class Carros extends AppCompatActivity {
    private Mcarros dbHelper;
    private EditText etMarca, etModelo, etAnio, etPrecio;
    private Spinner spinnerCategorias;
    private TableLayout tablaCarros;
    private int selectedCategoriaId = -1;
    private final Map<String, Integer> categoriasMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carros);

        initViews();
        dbHelper = new Mcarros(this);
        setupSpinner();
        cargarCarros();
    }

    private void initViews() {
        etMarca = findViewById(R.id.txtMarca);
        etModelo = findViewById(R.id.txtModelo);
        etAnio = findViewById(R.id.txtAnio);
        etPrecio = findViewById(R.id.txtPrecio);
        spinnerCategorias = findViewById(R.id.spinnerCategorias);
        tablaCarros = findViewById(R.id.TablaCarros);
    }

    private void setupSpinner() {
        cargarCategorias();
        spinnerCategorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategoriaId = position > 0 ?
                        categoriasMap.get(parent.getItemAtPosition(position).toString()) : -1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategoriaId = -1;
            }
        });
    }

    private void cargarCategorias() {
        try (Cursor cursor = dbHelper.obtenerCategorias()) {
            List<String> categorias = new ArrayList<>();
            categorias.add("Seleccione una categoría");
            categoriasMap.clear();

            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String nombre = cursor.getString(1);
                categorias.add(nombre);
                categoriasMap.put(nombre, id);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, categorias);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategorias.setAdapter(adapter);
        }
    }

    private void cargarCarros() {
        clearTable();

        try (Cursor cursor = dbHelper.obtenerTodosCarros()) {
            if (cursor.getCount() == 0) {
                agregarMensajeTabla("No hay carros registrados");
                return;
            }

            while (cursor.moveToNext()) {
                tablaCarros.addView(createTableRow(cursor));
            }
        }
    }

    private void clearTable() {
        int childCount = tablaCarros.getChildCount();
        if (childCount > 1) {
            tablaCarros.removeViews(1, childCount - 1);
        }
    }

    private TableRow createTableRow(Cursor cursor) {
        TableRow row = new TableRow(this);

        row.addView(crearTextView(String.valueOf(cursor.getInt(0)), 1f));     // ID
        row.addView(crearTextView(cursor.getString(1), 1.5f));               // Marca
        row.addView(crearTextView(cursor.getString(2), 1.5f));               // Modelo
        row.addView(crearTextView(String.valueOf(cursor.getInt(3)), 1f));   // Año
        row.addView(crearTextView("$" + cursor.getDouble(4), 1.5f));        // Precio
        row.addView(crearTextView(cursor.getString(6), 2f));                // Categoría

        return row;
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
        if (!validarCampos()) return;

        try {
            int anio = Integer.parseInt(etAnio.getText().toString());
            double precio = Double.parseDouble(etPrecio.getText().toString());

            boolean success = dbHelper.agregarCarro(
                    etMarca.getText().toString(),
                    etModelo.getText().toString(),
                    anio,
                    precio,
                    selectedCategoriaId
            );

            mostrarMensaje(success ? "Carro guardado exitosamente" : "Error al guardar el carro");

            if (success) {
                limpiarCampos();
                cargarCarros();
            }
        } catch (NumberFormatException e) {
            mostrarMensaje("Año y precio deben ser números válidos");
        }
    }

    private boolean validarCampos() {
        if (etMarca.getText().toString().trim().isEmpty() ||
                etModelo.getText().toString().trim().isEmpty() ||
                etAnio.getText().toString().trim().isEmpty() ||
                etPrecio.getText().toString().trim().isEmpty()) {
            mostrarMensaje("Todos los campos son obligatorios");
            return false;
        }

        if (selectedCategoriaId == -1) {
            mostrarMensaje("Seleccione una categoría");
            return false;
        }

        return true;
    }

    public void eliminarCarro(View view) {
        String modelo = etModelo.getText().toString().trim();
        if (modelo.isEmpty()) {
            mostrarMensaje("Ingrese el modelo a eliminar");
            return;
        }

        try (Cursor cursor = dbHelper.getReadableDatabase().query(
                TABLE_CARROS,
                new String[]{COLUMN_ID},
                COLUMN_MODELO + " = ? COLLATE NOCASE",
                new String[]{modelo},
                null, null, null
        )) {
            if (cursor.moveToFirst()) {
                int carroId = cursor.getInt(0);
                boolean eliminado = dbHelper.eliminarCarro(carroId);

                mostrarMensaje(eliminado ?
                        "Carro eliminado correctamente" : "Error al eliminar el carro");

                if (eliminado) cargarCarros();
            } else {
                mostrarMensaje("No se encontró el carro con ese modelo");
            }
        }
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
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

    public void LimpiarFiltros(View view) {
        limpiarCampos();
        cargarCategorias();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }

    // ... (código anterior se mantiene igual)

    public void buscarCarro(View view) {
        String criterio = etModelo.getText().toString().trim();

        if (criterio.isEmpty()) {
            mostrarMensaje("Ingrese un modelo para buscar");
            cargarCarros(); // Mostrar todos si no hay criterio
            return;
        }

        clearTable();

        try (Cursor cursor = dbHelper.buscarCarros(criterio)) {
            if (cursor.getCount() == 0) {
                agregarMensajeTabla("No se encontraron carros con: " + criterio);
                return;
            }

            while (cursor.moveToNext()) {
                tablaCarros.addView(createTableRow(cursor));
            }

            mostrarMensaje("Encontrados: " + cursor.getCount() + " carros");
        }
    }

// ... (el resto del código se mantiene igual)


}