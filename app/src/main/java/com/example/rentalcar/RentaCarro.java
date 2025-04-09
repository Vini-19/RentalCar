package com.example.rentalcar;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.rentalcar.Modelos.Mcarros;
import com.example.rentalcar.Modelos.Mcategoria;
import com.example.rentalcar.Modelos.CarritoRenta;

import java.util.ArrayList;
import java.util.Locale;

public class RentaCarro extends AppCompatActivity {

    private Mcarros dbCarros;
    private Mcategoria dbCategorias;
    private ArrayList<CarritoRenta> carrito;
    private ArrayList<CarritoRenta> carritoRenta;
    private ListView listViewCarros, listViewDetalleRenta;
    private TextView tvSubtotal, tvISV, tvTotal;
    private ArrayAdapter<String> adapterDetalle;
    private int currentCategoryId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renta_carro);

        try {
            dbCarros = new Mcarros(this);
            dbCategorias = new Mcategoria(this);
            carrito = new ArrayList<>();
            carritoRenta = new ArrayList<>();

            listViewCarros = findViewById(R.id.listViewCarros);
            listViewDetalleRenta = findViewById(R.id.listViewDetalleRenta);
            tvSubtotal = findViewById(R.id.tvSubtotal);
            tvISV = findViewById(R.id.tvISV);
            tvTotal = findViewById(R.id.tvTotal);

            adapterDetalle = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
            listViewDetalleRenta.setAdapter(adapterDetalle);

            cargarBotonesCategorias();

            Button btnProcesar = findViewById(R.id.btnProcesar);
            if (btnProcesar != null) {
                btnProcesar.setOnClickListener(v -> procesarPago());
            }

            // Configurar el listener para eliminar items con clic largo
            listViewDetalleRenta.setOnItemLongClickListener((parent, view, position, id) -> {
                eliminarDelCarrito(position);
                return true;
            });

        } catch (Exception e) {
            Toast.makeText(this, "Error al inicializar: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void cargarBotonesCategorias() {
        LinearLayout layoutCategorias = findViewById(R.id.layoutCategorias);
        if (layoutCategorias == null) return;

        layoutCategorias.removeAllViews();

        try (Cursor cursor = dbCategorias.obtenerTodasLasCategorias()) {
            if (cursor == null) return;

            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));

                Button btnCategoria = new Button(this);
                btnCategoria.setText(nombre);
                btnCategoria.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_orange_light));
                btnCategoria.setTextColor(Color.WHITE);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 16, 0);
                btnCategoria.setLayoutParams(params);
                btnCategoria.setPadding(32, 8, 32, 8);

                btnCategoria.setOnClickListener(v -> cargarCarrosPorCategoria(id));
                layoutCategorias.addView(btnCategoria);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error cargando categorías: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarCarrosPorCategoria(int categoriaId) {
        currentCategoryId = categoriaId;

        try (Cursor cursor = dbCarros.buscarCarrosPorCategoria(categoriaId)) {
            if (cursor == null || cursor.getCount() == 0) {
                Toast.makeText(this, "No hay vehículos en esta categoría", Toast.LENGTH_SHORT).show();
                listViewCarros.setAdapter(null);
                return;
            }

            ArrayList<String> carrosList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String marca = cursor.getString(cursor.getColumnIndexOrThrow("marca"));
                String modelo = cursor.getString(cursor.getColumnIndexOrThrow("modelo"));
                double precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"));
                carrosList.add(String.format(Locale.US, "%s %s - $%.2f", marca, modelo, precio));
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    carrosList
            );
            listViewCarros.setAdapter(adapter);

            listViewCarros.setOnItemClickListener((parent, view, position, id) -> {
                try (Cursor carroCursor = dbCarros.buscarCarrosPorCategoria(currentCategoryId)) {
                    if (carroCursor != null && carroCursor.moveToPosition(position)) {
                        int carroId = carroCursor.getInt(carroCursor.getColumnIndexOrThrow("id"));
                        String marca = carroCursor.getString(carroCursor.getColumnIndexOrThrow("marca"));
                        String modelo = carroCursor.getString(carroCursor.getColumnIndexOrThrow("modelo"));
                        double precio = carroCursor.getDouble(carroCursor.getColumnIndexOrThrow("precio"));
                        agregarAlCarrito(carroId, modelo, marca, precio);
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error cargando vehículos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void agregarAlCarrito(int carroId, String modelo, String marca, double precio) {
        boolean encontrado = false;

        for (CarritoRenta item : carrito) {
            if (item.getId() == carroId) {
                item.setCantidad(item.getCantidad() + 1);
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            carrito.add(new CarritoRenta(carroId, modelo, marca, precio, 1));
        }

        agregarADetalle(new CarritoRenta(carroId, modelo, marca, precio, 1));
        actualizarTotales();
    }

    private void agregarADetalle(CarritoRenta nuevoCarro) {
        boolean encontrado = false;

        for (CarritoRenta carro : carritoRenta) {
            if (carro.getId() == nuevoCarro.getId()) {
                carro.incrementarCantidad();
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            carritoRenta.add(nuevoCarro);
        }

        actualizarDetalle();
    }

    private void eliminarDelCarrito(int position) {
        if (position >= 0 && position < carritoRenta.size()) {
            CarritoRenta item = carritoRenta.get(position);

            // Buscar el item en el carrito principal
            for (int i = 0; i < carrito.size(); i++) {
                if (carrito.get(i).getId() == item.getId()) {
                    if (item.decrementarCantidad()) {
                        // Solo reducir cantidad
                        carrito.get(i).setCantidad(carrito.get(i).getCantidad() - 1);
                    } else {
                        // Eliminar completamente
                        carrito.remove(i);
                        carritoRenta.remove(position);
                    }
                    break;
                }
            }

            actualizarDetalle();
            actualizarTotales();
            Toast.makeText(this, "Cantidad reducida", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarDetalle() {
        ArrayList<String> detalleTexto = new ArrayList<>();
        for (CarritoRenta c : carritoRenta) {
            detalleTexto.add(c.getMarca() + " " + c.getModelo() + " x" + c.getCantidad() + " - $" + String.format("%.2f", c.getSubtotal()));
        }

        adapterDetalle.clear();
        adapterDetalle.addAll(detalleTexto);
        adapterDetalle.notifyDataSetChanged();
    }

    private void actualizarTotales() {
        double subtotal = 0;
        for (CarritoRenta item : carrito) {
            subtotal += item.getSubtotal();
        }
        double isv = subtotal * 0.15;
        double total = subtotal + isv;

        tvSubtotal.setText(String.format(Locale.US, "Subtotal: $%.2f", subtotal));
        tvISV.setText(String.format(Locale.US, "ISV (15%%): $%.2f", isv));
        tvTotal.setText(String.format(Locale.US, "Total: $%.2f", total));
    }

    private void procesarPago() {
        if (carrito.isEmpty()) {
            Toast.makeText(this, "No hay vehículos en el carrito", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Toast.makeText(this, "Renta procesada: " + tvTotal.getText(), Toast.LENGTH_LONG).show();
            carrito.clear();
            carritoRenta.clear();
            actualizarTotales();
            actualizarDetalle();
        } catch (Exception e) {
            Toast.makeText(this, "Error al procesar: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void Menu(View view) {
        startActivity(new Intent(this, Menu.class));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbCarros != null) dbCarros.close();
    }
}