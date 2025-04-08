package com.example.rentalcar;  // Asegúrate de que esté en el paquete correcto

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RentaCarro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renta_carro);  // Usar el XML correcto

        Button miBoton = findViewById(R.id.button5);

        if (miBoton != null) {  // Verifica que el botón existe
            miBoton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mostrarOpciones();
                }
            });
        } else {
            Toast.makeText(this, "Error: miBoton no encontrado en XML", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarOpciones() {
        // Opciones del diálogo
        String[] opciones = {"Opción 1", "Opción 2", "Opción 3"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona una opción")
                .setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Acción al seleccionar una opción
                        Toast.makeText(RentaCarro.this, "Seleccionaste: " + opciones[which], Toast.LENGTH_SHORT).show();
                    }
                });

        builder.create().show();
    }
}
