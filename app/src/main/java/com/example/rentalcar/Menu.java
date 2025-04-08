package com.example.rentalcar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rentalcar.Modelos.mLogin;

public class Menu extends AppCompatActivity {

    TextView txtNombre;
    String messg =  "Bienvenido " +mLogin.nombreUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu); // 👈 Aquí se carga la vista

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtNombre = findViewById(R.id.txtName);
        txtNombre.setText(messg);
    }

    public void Categoria(View view){
        startActivity(new Intent(this, Categorias.class));
    }

    public void Login(View view){
        startActivity(new Intent(this, Login.class));
    }

    public void Clientes(View view){
        startActivity(new Intent(this, Clientes.class));
    }

}
