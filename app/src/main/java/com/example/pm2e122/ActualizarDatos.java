package com.example.pm2e122;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pm2e122.Clase.Contactos;
import com.example.pm2e122.Clase.SQLiteConexion;

public class ActualizarDatos extends AppCompatActivity {
    EditText nombreActualizar, telefonoActualizar, notaActualizar;
    SQLiteConexion conexion;
    Button btnatras, btnactualiza;

    String id;
    // Variable booleana para almacenar el resultado


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_datos);

        conexion = new SQLiteConexion(this, Contactos.namedb, null, 1);
        btnatras = (Button) findViewById(R.id.btnatras);
        btnactualiza = (Button) findViewById(R.id.btnactualiza);
        nombreActualizar = (EditText) findViewById(R.id.txtNombreActualizar);
        telefonoActualizar = (EditText) findViewById(R.id.txtTelefonoActualizar);
        notaActualizar = (EditText) findViewById(R.id.txtNotasActualizar);

        id = getIntent().getStringExtra("id");
        String nombre = getIntent().getStringExtra("nombre");
        String telefono = getIntent().getStringExtra("telefono");
        String nota = getIntent().getStringExtra("nota");

        nombreActualizar.setText(nombre);
        telefonoActualizar.setText(telefono);
        notaActualizar.setText(nota);


        btnatras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityLista.class);
                startActivity(intent);
                finish();
            }
        });
        btnactualiza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertaActualizar();
            }
        });
    }

    // Método para mostrar el diálogo de confirmación de actualización
    private void alertaActualizar() {
        final boolean[] confirmacion = {false};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar Actualización");
        builder.setMessage("¿Desea actualizar los datos del contacto seleccionado?");

        // Agregar botón de actualizar
        builder.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Si el usuario confirma la actualización, establecer confirmacion como true
                confirmacion[0] = true;
                if (confirmacion[0]) {
                    SQLiteConexion conexion = new SQLiteConexion(ActualizarDatos.this, Contactos.namedb, null, 1);
                    SQLiteDatabase db = conexion.getWritableDatabase();

                    ContentValues valores = new ContentValues();
                    valores.put(Contactos.nombre, nombreActualizar.getText().toString());
                    valores.put(Contactos.telefono, telefonoActualizar.getText().toString());
                    valores.put(Contactos.nota, notaActualizar.getText().toString());

                    Long resultado = Long.valueOf(db.update(Contactos.Tabla_Contacto, valores, Contactos.id + "=?", new String[]{String.valueOf(id)}));
                    db.close();
                    Toast.makeText(getApplicationContext(), "Registro actualizado correctamente " + resultado.toString(),
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ActualizarDatos.this, ActivityLista.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        // Agregar botón de cancelar
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Si el usuario cancela la actualización, cerrar el diálogo
                dialog.dismiss();
            }
        });

        // Mostrar el diálogo y esperar la respuesta del usuario
        AlertDialog dialog = builder.create();
        dialog.show();

    }


}



