package com.example.pm2e122;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.pm2e122.Models.pais;
import com.example.pm2e122.Clase.Contactos;
import com.example.pm2e122.Clase.SQLiteConexion;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    SQLiteConexion conexion;
    Button btnRegsitrar, btnvercontactos, btnTomarFoto; // Se agrega el botón para tomar foto
    EditText txtNombre, txtTelefono, txtnota;
    ImageView foto, agregar;
    Spinner combo;
    ArrayList<pais> listapaises;
    ArrayList<String> Arreglopaises;
    static final int REQUEST_IMAGE_CAPTURE = 1; // Código de solicitud para la captura de imagen
    private Bitmap imagenTomada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.CAMERA}, 1000);
        }

        conexion = new SQLiteConexion(this, Contactos.namedb, null, 1);

        txtNombre = findViewById(R.id.txtNombre);
        txtTelefono = findViewById(R.id.txtTelefono);
        txtnota = findViewById(R.id.txtNota);
        foto = findViewById(R.id.imgContacts);
        btnRegsitrar = findViewById(R.id.btnSalvar);
        btnvercontactos = findViewById(R.id.btnVerListaContactos);
      //  agregar = findViewById(R.id.floatAñadirPais);
        combo = findViewById(R.id.cbPais);
        btnTomarFoto = findViewById(R.id.btnTomarFoto); // Botón para tomar la foto
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatAñadirPais);

        ConsultarSpiner();

        ArrayAdapter<CharSequence> adp = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Arreglopaises);
        combo.setAdapter(adp);



        btnRegsitrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AgregarPersona();
            }
        });

        btnvercontactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ActivityLista.class);
                startActivity(intent);
                finish();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this,RegistroPais.class);
                startActivity(intent);

            }
        });

        btnTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imagenTomada = (Bitmap) extras.get("data");
            foto.setImageBitmap(imagenTomada);
        }
    }

    private void AgregarPersona() {
        if (validar()) {
            if (imagenTomada != null) {
                try {
                    SQLiteConexion conexion = new SQLiteConexion(this, Contactos.namedb, null, 1);
                    SQLiteDatabase db = conexion.getWritableDatabase();

                    // Convertir la imagen a un array de bytes
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    imagenTomada.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] imagenByteArray = stream.toByteArray();

                    ContentValues datos = new ContentValues();
                    datos.put(Contactos.nombre, txtNombre.getText().toString());
                    datos.put(Contactos.telefono, txtTelefono.getText().toString());
                    datos.put(Contactos.nota, txtnota.getText().toString());
                    datos.put(Contactos.pais, combo.getSelectedItem().toString());
                    datos.put(Contactos.imagen, imagenByteArray); // Guardar la imagen en la base de datos

                    long Resul = db.insert(Contactos.Tabla_Contacto, Contactos.id, datos);

                    Toast.makeText(this, "Datos Registrados con Exito", Toast.LENGTH_SHORT).show();
                    db.close();
                    limpiar();
                } catch (Exception exception) {
                    Toast.makeText(this, "Error no se pudo registrar", Toast.LENGTH_SHORT).show();
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Fotografía obligatoria");
                builder.setMessage("Debe tomar una fotografía antes de guardar.");
                builder.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }


    private void ConsultarSpiner() {
        SQLiteDatabase db = conexion.getReadableDatabase();
        pais country = null;
        listapaises = new ArrayList<pais>();
        Cursor cursor = db.rawQuery(Contactos.SelectTablePais, null);

        while (cursor.moveToNext()) {
            country = new pais();
            country.setId(cursor.getInt(0));
            country.setPais(cursor.getString(1));

            listapaises.add(country);

        }
        cursor.close();
        obtenerlista();
    }

    private void obtenerlista() {
        Arreglopaises = new ArrayList<String>();

        for (int i = 0; i < listapaises.size(); i++) {
            Arreglopaises.add(listapaises.get(i).getId() + " - " + listapaises.get(i).getPais());
        }

    }

    public boolean validar() {
        boolean retorna = true;
        if (txtNombre.getText().toString().isEmpty()) {
            txtNombre.setError("No permite campo vacio debe de Ingresar un Nombre");
            retorna = false;
        }
        if (txtTelefono.getText().toString().isEmpty()) {
            txtTelefono.setError("No permite campo vacio debe de Ingresar Un Telefono");
            retorna = false;
        }
        if (txtnota.getText().toString().isEmpty()) {
            txtnota.setError("No permite campo vacio debe de Ingresar una Nota");
            retorna = false;
        }
        return retorna;

    }

    public void limpiar() {
        txtTelefono.setText("");
        txtNombre.setText("");
        txtnota.setText("");
        txtNombre.requestFocus();
    }

}