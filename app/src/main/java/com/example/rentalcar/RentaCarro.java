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
        setContentView(R.layout.activity_main);

        Button miBoton = findViewById(R.id.miBoton);

        miBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarOpciones();
            }
        });
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
                        Toast.makeText(MainActivity.this, "Seleccionaste: " + opciones[which], Toast.LENGTH_SHORT).show();
                    }
                });

        builder.create().show();
    }
}
