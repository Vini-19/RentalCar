package com.example.rentalcar.Modelos;

public class CarritoRenta {
    private final int id;
    private final String modelo;
    private final String marca;
    private final double precio;
    private int cantidad;

    public CarritoRenta(int id, String modelo, String marca, double precio, int cantidad) {
        if (modelo == null || marca == null) {
            throw new IllegalArgumentException("Modelo y marca no pueden ser nulos");
        }
        this.id = id;
        this.modelo = modelo;
        this.marca = marca;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    public int getId() { return id; }
    public String getModelo() { return modelo; }
    public String getMarca() { return marca; }
    public double getPrecio() { return precio; }
    public int getCantidad() { return cantidad; }

    public void setCantidad(int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("Cantidad debe ser positiva");
        this.cantidad = cantidad;
    }

    public double getSubtotal() {
        return precio * cantidad;
    }

    public void incrementarCantidad() {
        this.cantidad++;
    }

    public boolean decrementarCantidad() {
        if (this.cantidad > 1) {
            this.cantidad--;
            return true;
        }
        return false;
    }
}