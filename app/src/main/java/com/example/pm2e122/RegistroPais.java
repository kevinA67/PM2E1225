package com.example.pm2e122;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pm2e122.Clase.Contactos;
import com.example.pm2e122.Clase.SQLiteConexion;

public class RegistroPais extends AppCompatActivity {

    SQLiteConexion conexion;
    Button atras, regPais;

    EditText nombPais;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_pais);

        conexion = new SQLiteConexion(this, Contactos.namedb, null ,1);
        nombPais = (EditText) findViewById(R.id.txtRegistropais);
        atras = (Button) findViewById(R.id.btnregresar1);
        regPais = (Button) findViewById(R.id.btnRegistroPais);

        /*Registrar pais*/
        regPais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                agregarPais();
                limpiaPais();
            }
        });
        /*Retorno a principal*/
        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistroPais.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void agregarPais()
    {
        if (validar()) {
            try {
                SQLiteConexion conexion = new SQLiteConexion(this, Contactos.namedb, null, 1);
                SQLiteDatabase db = conexion.getWritableDatabase();
                ContentValues datos = new ContentValues();
                datos.put(Contactos.campo_nombre_pais, nombPais.getText().toString());

                Long resultado = db.insert(Contactos.Tabla_paises, Contactos.compo_id_pais, datos);
                Toast.makeText(this, "Se registro el Pais con Exicto", Toast.LENGTH_SHORT).show();
                db.close();
            } catch (Exception exception) {
                Toast.makeText(this, "Error en el Registro", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*Validar datos en blanco*/
    public boolean validar(){
        boolean retorna = true;
        if(nombPais.getText().toString().isEmpty()){
            nombPais.setError("No permite campo vacio Debe de Ingresa el Pais");
            retorna = false;
        }

        return retorna;

    }

    public void limpiaPais()
    {
        nombPais.setText("");

        nombPais.requestFocus();
    }

}
