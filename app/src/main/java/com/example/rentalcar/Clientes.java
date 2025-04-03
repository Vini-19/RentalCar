package com.example.rentalcar;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rentalcar.Modelos.Mcategoria;
import com.example.rentalcar.Modelos.mClientes;
public class Clientes extends AppCompatActivity {
    private EditText ob_numCuenta,ob_nombre,ob_telefono,ob_correo,ob_fecha;
    private mClientes dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_clientes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        ob_numCuenta = (EditText)findViewById(R.id.txtNumcuenta);
        ob_nombre = (EditText)findViewById(R.id.txtNombre);
        ob_telefono = (EditText)findViewById(R.id.txtTelefono);
        ob_correo = (EditText)findViewById(R.id.txtCorreo);
        ob_fecha = (EditText)findViewById(R.id.txtFecha);

        dbHelper = new mClientes(this);


    }

    public  void Guardar(View view){

        String cuenta  = ob_numCuenta.getText().toString();
        String nombre = ob_nombre.getText().toString();
        String telefono = ob_telefono.getText().toString();
        String correo = ob_correo.getText().toString();
        String fecha = ob_fecha.getText().toString();


        if(!cuenta.isEmpty() && !nombre.isEmpty() && !telefono.isEmpty()&& !correo.isEmpty()&& !fecha.isEmpty()){

            ContentValues guardar =new ContentValues(); //registar los datos

            guardar.put("numCuenta",cuenta);
            guardar.put("NomCliente",nombre);
            guardar.put("Telefono",telefono);
            guardar.put("Correo",correo);
            guardar.put("Fecha",fecha);
            SQLiteDatabase db = dbHelper.getWritableDatabase();


            Toast.makeText(this,"DATOS GUARDADOS",Toast.LENGTH_SHORT).show();
            db.insert("Clientes",null,guardar);
            db.close();
            ob_numCuenta.setText(""); //Borra los editext
            ob_nombre.setText(""); //Borra los Editext
            ob_telefono.setText(""); //Borra los Editext
            ob_correo.setText("");
            ob_fecha.setText("");

        }else{

            Toast.makeText(this,"TODOS LOS CAMPOS DEBEN DE LLENARSE",Toast.LENGTH_SHORT).show();

        }
    }

    public  void Buscar(View view){

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String cuenta = ob_numCuenta.getText().toString(); //obtenemos  codigo ingresado en el teclado



        if(!cuenta.isEmpty()){

            Cursor fila = db.rawQuery("select * from Clientes where numCuenta ="+ cuenta,null);

            if(fila.moveToFirst()){
                ob_nombre.setText(fila.getString(2));
                ob_telefono.setText(fila.getString(3));
                ob_correo.setText(fila.getString(4));
                ob_fecha.setText(fila.getString(5));
                db.close();

            }else{
                Toast.makeText(this,"EL CODIGO INGRESADO NO EXISTE",Toast.LENGTH_SHORT).show();
                ob_numCuenta.setText(""); //Borra los editext
                ob_nombre.setText(""); //Borra los Editext
                ob_telefono.setText("");//Borra los Editext
                ob_correo.setText("");
                ob_fecha.setText("");
            }

        }else{
            Toast.makeText(this,"INGRESE EL CODIGO DEL ALUMNO",Toast.LENGTH_SHORT).show();

        }
    }

    public void eliminar (View view) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String cuenta = ob_numCuenta.getText().toString();

        if (!cuenta.isEmpty()){
            int id = db.delete("Clientes", "numCuenta=" + cuenta, null);
            db.close();

            ob_numCuenta.setText(""); //Borra los editext
            ob_nombre.setText(""); //Borra los Editext
            ob_telefono.setText("");//Borra los Editext
            ob_correo.setText("");
            ob_fecha.setText("");


            if (id==1) {
                Toast.makeText(this, "ALUMNO ELIMINADO", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "ALUMNO NO EXISTE", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Debe de ingresar el codigo del Alumno", Toast.LENGTH_SHORT).show();
        }

    } //Fin del metodo eliminar

    public void modificar (View view){


        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String cuenta = ob_numCuenta.getText().toString(); //obtenemos  codigo ingresado en el teclado
        String nombre = ob_nombre.getText().toString();
        String telefono = ob_telefono.getText().toString() ;
        String correo = ob_correo.getText().toString() ;
        String fecha = ob_fecha.getText().toString() ;

        if(!cuenta.isEmpty() && !nombre.isEmpty() && !telefono.isEmpty()&& !correo.isEmpty()&& !fecha.isEmpty()){

            ContentValues modificar = new ContentValues(); //contenedor de registro

            modificar.put("numCuenta",cuenta);
            modificar.put("NomCliente",nombre);
            modificar.put("Telefono",telefono);
            modificar.put("Correo",correo);
            modificar.put("Fecha",fecha);


            int actualizar  = db.update("Clientes",modificar,"numCuenta="+cuenta,null);
            db.close();

            if(actualizar==1){
                Toast.makeText(this,"REGISTRO ACTUALIZADO CON EXITO",Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(this,"Codigo no Existe",Toast.LENGTH_SHORT).show();

            }
        }else{
            Toast.makeText(this,"DEBE DE LLENAR TODOS LOS CAMPOS",Toast.LENGTH_SHORT).show();
        }
    }






}