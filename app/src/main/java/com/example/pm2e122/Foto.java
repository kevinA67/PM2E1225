package com.example.pm2e122;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.pm2e122.Clase.Contactos;
import com.example.pm2e122.Clase.SQLiteConexion;

public class Foto extends AppCompatActivity {
    Button btnregresarImg;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto);

        btnregresarImg = (Button) findViewById(R.id.btnregresar1);
        imageView = (ImageView) findViewById(R.id.imageView);

        String id = getIntent().getStringExtra("id");


        // Recuperar la imagen de la base de datos

        byte[] imagenByteArray = null;
        SQLiteConexion conexion = new SQLiteConexion(this, Contactos.namedb, null, 1);
        SQLiteDatabase db = conexion.getReadableDatabase();
        String[] projection = {Contactos.imagen};
        String selection = Contactos.id + " = ?";
        String[] selectionArgs = {id};
        Cursor cursor = db.query(Contactos.Tabla_Contacto, projection, selection,
                selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            imagenByteArray = cursor.getBlob(cursor.getColumnIndexOrThrow(Contactos.imagen));
            cursor.close();
        }

        // Convertir el array de bytes a un Bitmap
        if (imagenByteArray != null) {
            Bitmap imagenBitmap = BitmapFactory.decodeByteArray(imagenByteArray, 0, imagenByteArray.length);

            // Asignar el Bitmap al ImageView
            imageView.setImageBitmap(imagenBitmap);
        } else {
            // Si no hay imagen guardada, puedes establecer una imagen de placeholder o hacer otra acción apropiada
            //   imageView.setImageResource(R.drawable.placeholder_image);
        }

        db.close();


        btnregresarImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityLista.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
