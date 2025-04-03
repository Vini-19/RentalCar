package com.example.rentalcar;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rentalcar.Modelos.Mcategoria;

import java.util.List;

public class Categorias extends AppCompatActivity {
    private EditText txtNombre, txtDescripcion;
    private EditText TxtRes;
    private Mcategoria dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);

        txtNombre = findViewById(R.id.txtNombre);
        txtDescripcion = findViewById(R.id.txtDescripc);
        TxtRes = findViewById(R.id.Resultado);

        dbHelper = new Mcategoria(this); // Inicializar el helper de la base de datos
    }

    public void guardarCategoria(View view) {
        String nombre = txtNombre.getText().toString().trim();
        String descripcion = txtDescripcion.getText().toString().trim();

        // Validar campos vacíos
        if (nombre.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Intentar guardar en la base de datos
        boolean exito = dbHelper.guardarCategoria(nombre, descripcion);

        if (exito) {
            Toast.makeText(this, "Categoría guardada correctamente", Toast.LENGTH_SHORT).show();
            txtNombre.setText("");
            txtDescripcion.setText("");
        } else {
            Toast.makeText(this, "Error al guardar. Revise el LogCat.", Toast.LENGTH_LONG).show();
        }
    }
    public void buscarCategoria(View view) {
        String textoBusqueda = txtNombre.getText().toString().trim();

        if (textoBusqueda.isEmpty()) {
            TxtRes.setText("🔍 Ingresa un nombre para buscar");
            return;
        }

        // Verifica si existe la categoría (búsqueda exacta, insensible a mayúsculas)
        boolean existe = dbHelper.existeCategoria(textoBusqueda);

        if (existe) {
            TxtRes.setText(textoBusqueda); // Muestra el texto buscado
            Toast.makeText(this, "✅ Categoría encontrada", Toast.LENGTH_SHORT).show(); // Mensaje emergente con ícono
        } else {
            TxtRes.setText("❌ No existe: \"" + textoBusqueda + "\"");
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close(); // Cerrar la conexión a la BD al salir
        super.onDestroy();
    }
}