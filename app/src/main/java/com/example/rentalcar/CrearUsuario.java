package com.example.rentalcar;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.rentalcar.Modelos.ImageHandler;
import com.example.rentalcar.Modelos.mLogin;

public class CrearUsuario extends AppCompatActivity {
    private ImageHandler imageHandler;
    private ImageView imgAvatar;
    private EditText obNomb, obMail, obPass, opId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_usuario);

        imgAvatar = findViewById(R.id.imgAvatar);
        ImageButton btnSelectImage = findViewById(R.id.imgProfile);
        imageHandler = new ImageHandler(this);

        btnSelectImage.setOnClickListener(v -> imageHandler.requestImage());

        obNomb = findViewById(R.id.txtname);
        obMail = findViewById(R.id.txtEmail);
        obPass = findViewById(R.id.txtpass);
        opId = findViewById(R.id.txtId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageHandler.onImageResult(requestCode, resultCode, data, imgAvatar);
    }

    public void ingresar(View view) {
        mLogin admin = new mLogin(this, "dbRentCar", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        String nombre = obNomb.getText().toString();
        String email = obMail.getText().toString();
        String password = obPass.getText().toString();
        String ident = opId.getText().toString();

        if (!nombre.isEmpty() && !email.isEmpty() && !password.isEmpty() && !ident.isEmpty()) {
            ContentValues registro = new ContentValues();
            registro.put("name", nombre);
            registro.put("email", email);
            registro.put("password", password);
            registro.put("identity", ident);

            byte[] avatar = imageHandler.getImageBytes();
            if (avatar != null) {
                registro.put("avatar", avatar);
            }

            try {
                db.insert("users", null, registro);
                Toast.makeText(this, "DATOS GUARDADOS", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al guardar los datos", Toast.LENGTH_SHORT).show();
            } finally {
                db.close();
            }

            Intent go = new Intent(this, Login.class);
            startActivity(go);
        } else {
            Toast.makeText(this, "TODOS LOS CAMPOS DEBEN DE LLENARSE", Toast.LENGTH_SHORT).show();
        }
    }

    public void back(View view) {
        Intent login = new Intent(this, Login.class);
        startActivity(login);
    }
}
