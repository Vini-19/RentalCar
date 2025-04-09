package com.example.rentalcar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rentalcar.Modelos.ImageHandler;
import com.example.rentalcar.Modelos.mLogin;

public class profTrack extends AppCompatActivity {
    private EditText txtNombre, txtId, txtEmail, txtPassword, valPass;
    private TextView numero;
    private ImageView imgAvatar;
    private ImageHandler imageHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_prof_track);

        // Inicialización de vistas
        txtNombre = findViewById(R.id.txtNameProf2);
        txtId = findViewById(R.id.txtIdProf2);
        txtEmail = findViewById(R.id.txtEmailProf2);
        txtPassword = findViewById(R.id.txtPassProf2);
        valPass = findViewById(R.id.txtPassRepeat2);
        numero = findViewById(R.id.textView12);
        imgAvatar = findViewById(R.id.imgAvatar3);

        imageHandler = new ImageHandler(this);

        // Cargar datos del usuario
        cargarDatosUsuario();

        // Acción para seleccionar imagen
        findViewById(R.id.imgProfile4).setOnClickListener(v -> imageHandler.requestImage());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageHandler.onImageResult(requestCode, resultCode, data, imgAvatar);
    }

    public void actualizarPerfil(View view) {
        String nuevaPassword = txtPassword.getText().toString().trim();
        String repetirPassword = valPass.getText().toString().trim();

        if (!nuevaPassword.equals(repetirPassword)) {
            Toast.makeText(this, "Error: las contraseñas deben coincidir", Toast.LENGTH_SHORT).show();
            return;
        }

        String nuevoNombre = txtNombre.getText().toString().trim();
        String nuevaIdentidad = txtId.getText().toString().trim();
        String nuevoCorreo = txtEmail.getText().toString().trim();

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
            cargarDatosUsuario(); // Actualizar la UI con los nuevos datos
        } else {
            Toast.makeText(this, "Error al actualizar perfil", Toast.LENGTH_SHORT).show();
        }
    }

    public void menu(View view) {
        startActivity(new Intent(this, Menu.class));
    }

    private void cargarDatosUsuario() {
        if (mLogin.nombreUsuario != null) txtNombre.setText(mLogin.nombreUsuario);
        if (mLogin.identidadUsuario != null) txtId.setText(mLogin.identidadUsuario);
        if (mLogin.correoUsuario != null) txtEmail.setText(mLogin.correoUsuario);
        if (mLogin.passwordUsuario != null) txtPassword.setText(mLogin.passwordUsuario);
        numero.setText("No." + mLogin.codigoUsuario);

        if (mLogin.avatarUsuario != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(mLogin.avatarUsuario, 0, mLogin.avatarUsuario.length);
            imgAvatar.setImageBitmap(bitmap);
        }
    }
}
