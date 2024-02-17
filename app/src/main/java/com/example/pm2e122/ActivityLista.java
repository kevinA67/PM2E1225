package com.example.pm2e122;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.pm2e122.Clase.Contactos;
import com.example.pm2e122.Clase.SQLiteConexion;
import com.example.pm2e122.Models.persona;

import java.util.ArrayList;

public class ActivityLista extends AppCompatActivity {

    // Declaración de variables
    SQLiteConexion conexion;
    Button btnatras, btneliminar, btnVerimg, btnactualiza, btnllamando,btncompartirT;
    ListView listaContactos;
    ArrayList<persona> listapersonas;
    ArrayList<String> Arreglopersonas;
    SearchView searchView;
    persona personaSeleccionada; // Variable para almacenar el contacto seleccionado
    persona contactosPersonas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        // Inicialización de vistas y elementos
        conexion = new SQLiteConexion(this, Contactos.namedb, null, 1);
        btnatras =(Button) findViewById(R.id.btnatras);
        btneliminar =(Button) findViewById(R.id.btnEliminar);
        btnVerimg =(Button) findViewById(R.id.btnVerimg);
        btnactualiza =(Button) findViewById(R.id.btnActualizar);
        listaContactos =(ListView) findViewById(R.id.buscarcontatos);
        searchView =(SearchView) findViewById(R.id.searchView);
        btnllamando =(Button) findViewById(R.id.btnllamar);
        btncompartirT =(Button) findViewById(R.id.btnCompartir);

        ConsultarSpiner();
        ArrayAdapter<String> adp = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Arreglopersonas);
        listaContactos.setAdapter(adp);

        // Configurar el listener para el SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adp.getFilter().filter(newText); // Filtrar los datos del ArrayAdapter
                return true;
            }
        });

        // Botón de regresar
        btnatras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityLista.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Configurar el listener para la lista de contactos
        listaContactos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                contactosPersonas = listapersonas.get(position);
                // Cambiar el color de fondo del elemento seleccionado
                for (int i = 0; i < parent.getChildCount(); i++) {
                    if (i == position) {
                        parent.getChildAt(i).setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light)); // Cambia este color al deseado
                    } else {
                        parent.getChildAt(i).setBackgroundColor(Color.TRANSPARENT); // Restablecer el color de fondo de otros elementos
                    }
                }

                // Obtener el contacto seleccionado
                personaSeleccionada = listapersonas.get(position);
            }
        });

        // Configurar el OnClickListener para el botón de "Actualizar"
        btnactualiza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    // Crear un Intent para abrir una nueva actividad
                    Intent intent = new Intent(ActivityLista.this, ActualizarDatos.class);

                    // Pasar la información a través del Intent (si la persona seleccionada no es null)
                    if (contactosPersonas != null) {
                        intent.putExtra("id", contactosPersonas.getId().toString());
                        intent.putExtra("nombre", contactosPersonas.getNombre());
                        intent.putExtra("telefono", contactosPersonas.getTelefono().toString());
                        intent.putExtra("nota", contactosPersonas.getNota());

                        // Iniciar la nueva actividad
                        startActivity(intent);
                        finish();
                    } else {
                        // Manejar el caso donde no hay persona seleccionada
                        Toast.makeText(ActivityLista.this, "Selecciona un contacto primero", Toast.LENGTH_SHORT).show();
                    }
            }
        });

        // Configurar el OnClickListener para el botón de "Eliminar"
        btneliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verificar si se ha seleccionado un contacto
                if (personaSeleccionada != null) {

                    // Mostrar un diálogo de confirmación de eliminación
                    alertaEliminar();
                } else {
                    // Mostrar un mensaje si no se ha seleccionado ningún contacto
                    Toast.makeText(ActivityLista.this, "Selecciona un contacto primero", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnVerimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   Intent intent = new Intent(ActivityLista.this, Foto.class);
                intent.putExtra("id", contactosPersonas.getId().toString());
                startActivity(intent);
                   finish();

            }
        });

        // Configurar el OnClickListener para el botón de "Llamar"
        btnllamando.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verificar si se ha seleccionado un contacto
                if (personaSeleccionada != null) {
                    // Mostrar un diálogo de confirmación de llamada
                    alertaLlamar(personaSeleccionada.getTelefono());
                } else {
                    // Mostrar un mensaje si no se ha seleccionado ningún contacto
                    Toast.makeText(ActivityLista.this, "Selecciona un contacto primero", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Configurar el OnClickListener para el botón de "Compartir"
        btncompartirT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, personaSeleccionada.getNombre()+": "+personaSeleccionada.getTelefono().toString());
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        });
    }
    // Método para mostrar el diálogo de confirmación de llamada
    private void alertaLlamar(final Integer telefono) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar Llamada");
        builder.setMessage("¿Desea llamar a " + telefono + "?");

        // Agregar botón de llamar
        builder.setPositiveButton("Llamar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Iniciar la actividad de llamada con el número de teléfono
                Intent intent = new Intent(ActivityLista.this, Activityllamada.class);
                intent.putExtra("telefono", telefono);
                startActivity(intent);
            }
        });

        // Agregar botón de cancelar
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // No hacer nada o cerrar el diálogo
                dialog.dismiss();
            }
        });

        // Mostrar el diálogo de alerta
        builder.create().show();
    }


    // Método para mostrar el diálogo de confirmación de eliminación
    private void alertaEliminar() {
        final boolean[] confirmacion = {false};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar eliminación");
        builder.setMessage("¿Desea eliminar los datos del contacto seleccionado?");

        // Agregar botón de actualizar
        builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Si el usuario confirma la actualización, establecer confirmacion como true
                confirmacion[0] = true;
                if (confirmacion[0]) {
                    SQLiteConexion conexion = new SQLiteConexion(ActivityLista.this, Contactos.namedb, null, 1);
                    SQLiteDatabase db = conexion.getWritableDatabase();
                    Long resultado = Long.valueOf(db.delete(Contactos.Tabla_Contacto, Contactos.id + "=?", new String[]{String.valueOf(personaSeleccionada.getId())}));
                    db.close();
                    Toast.makeText(getApplicationContext(), "Registro eliminado correctamente " + resultado.toString(),
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ActivityLista.this, ActivityLista.class);
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


    // Métodos adicionales
    private void ConsultarSpiner() {
        SQLiteDatabase db = conexion.getReadableDatabase();
        persona datos = null;
        listapersonas = new ArrayList<>();
        Cursor cursor = db.rawQuery(Contactos.SelectTableContactos, null);

        while (cursor.moveToNext()) {
            datos = new persona();
            datos.setId(cursor.getInt(0));
            datos.setPais(cursor.getString(1));
            datos.setNombre(cursor.getString(2));
            datos.setTelefono(cursor.getInt(3));
            datos.setNota(cursor.getString(4));

            listapersonas.add(datos);
        }
        cursor.close();
        obtenerlista();
    }

    private void obtenerlista() {
        Arreglopersonas = new ArrayList<>();

        for (int i = 0; i < listapersonas.size(); i++) {
            Arreglopersonas.add(listapersonas.get(i).getId() + " - " + listapersonas.get(i).getNombre() + " | " + listapersonas.get(i).getTelefono()+
                    " |\n "+listapersonas.get(i).getNota());
        }
    }
}

