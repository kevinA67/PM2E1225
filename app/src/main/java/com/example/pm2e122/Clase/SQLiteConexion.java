package com.example.pm2e122.Clase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.pm2e122.Clase.Contactos;

public class SQLiteConexion extends SQLiteOpenHelper {

    public SQLiteConexion(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL(Contactos.CreateTableContactos);
        sqLiteDatabase.execSQL(Contactos.CreateTablePais);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
        sqLiteDatabase.execSQL(Contactos.DropTableContactos);
        sqLiteDatabase.execSQL(Contactos.DropTablePais);
        onCreate(sqLiteDatabase);
    }
}
