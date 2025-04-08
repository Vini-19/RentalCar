package com.example.rentalcar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rentalcar.Modelos.ImageHandler;
import com.example.rentalcar.Modelos.mLogin;

public class perfil extends AppCompatActivity {
    private EditText txtNombre, txtId, txtEmail, txtPassword,valPass;
    private TextView numero;
    private ImageView imgAvatar;
    private ImageHandler imageHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);
    /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.this), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/

        txtNombre = findViewById(R.id.txtNameProf);
        txtId = findViewById(R.id.txtIdProf);
        txtEmail = findViewById(R.id.txtEmailProf);
        txtPassword = findViewById(R.id.txtPassProf);
        numero = findViewById(R.id.txtNumb);
        imgAvatar = findViewById(R.id.imgAvatar2);
        valPass = findViewById(R.id.txtPassRepeat);

        imageHandler = new ImageHandler(this);

        // Mostrar avatar si ya existe
        if (mLogin.avatarUsuario != null) {
            android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(mLogin.avatarUsuario, 0, mLogin.avatarUsuario.length);
            imgAvatar.setImageBitmap(bitmap);
        }

        txtNombre.setText(mLogin.nombreUsuario);
        txtId.setText(mLogin.identidadUsuario);
        txtEmail.setText(mLogin.correoUsuario);
        txtPassword.setText(mLogin.passwordUsuario);
        numero.setText("No." + mLogin.codigoUsuario);

        // Acción para seleccionar imagen
        findViewById(R.id.imgProfile3).setOnClickListener(v -> imageHandler.requestImage());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageHandler.onImageResult(requestCode, resultCode, data, imgAvatar);
    }

    public void actualizarPerfil(View view) {
        if(!valPass.getText().toString().trim().equals(txtPassword.getText().toString().trim())){
            Toast.makeText(this, "Error las contrase;as deben coinsidir", Toast.LENGTH_SHORT).show();
        }else{
            String nuevoNombre = txtNombre.getText().toString().trim();
            String nuevaIdentidad = txtId.getText().toString().trim();
            String nuevoCorreo = txtEmail.getText().toString().trim();
            String nuevaPassword = txtPassword.getText().toString().trim();

            mLogin db = new mLogin(this, "dbRentCar", null, 1);

            byte[] nuevoAvatar = imageHandler.getImageBytes();
            if (nuevoAvatar == null) {
                nuevoAvatar = mLogin.avatarUsuario; // conserva el anterior si no se seleccionó otro
            }

            boolean exito = db.actualizarPerfil(
                    mLogin.codigoUsuario,
                    nuevoNombre,
                    nuevoCorreo,
                    nuevaPassword,
                    nuevaIdentidad,
                    nuevoAvatar
            );

            if (exito) {
                Toast.makeText(this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
                db.cargarDatosUsuario(nuevoCorreo);
            } else {
                Toast.makeText(this, "Error al actualizar perfil", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void menu(View view){
        startActivity(new Intent(this, Menu.class));
    }
}
