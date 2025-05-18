package com.example.proyecto;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class BDClinica extends SQLiteOpenHelper {

    //datos que debemos poner
    //private String ip="192.xxx.x.xx" direccion de ip
    //private string usuario="usuario de la base de datos"
    //private string password="contraseña de la base de datos"
    //private string basedatos="nombre servidor de la base de datos"

    /*
    @SuppressLint("NewApi")
    public Connection connect(){
        Connection connection=null;
        String connectionURL=null;
        try{
            StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connectionURL="jdbc:jtds:sqlserver://" + this.ip+"/"+this.basedatos+ ";user=" +this.usuario+ ";password=" + this.password+ ";";
            connection=DriverManager.getConnection(connectionURL);
        } catch (Exception e){
            e.printStackTrace();
            Log.e("Error de conexion SQL: ", e.getMessage());
        }
        return connection;
        debemos meter la libreria jbdc en la carpeta librerias del proyecto
    }
    */

    Context contexto;

    static String createBDSQL="CREATE TABLE pacientes (nombre text, apellidos text, telefono text, email text, usuario text, clave text,  primary key(usuario))";
    static String createBDSQL1="CREATE TABLE ciudades (ciudad text primary key, calle text, telefono integer default 687801214)";
    static String createBDSQL2="CREATE TABLE citas (idCita integer primary key autoincrement, usuario text , ciudad text, tipoCita text, fecha text, hora text, precio integer default 70, personal text default 'David Gimeno Montes', foreign key (usuario) references pacientes(usuario), foreign key (ciudad) references ciudades(ciudad))";
    static String createBDSQL3="CREATE TABLE informes (idInforme integer primary key autoincrement, usuario text, fecha text, archivo text, foreign key (usuario) references pacientes(usuario), foreign key (usuario, fecha) references citas(usuario, fecha))";

    public BDClinica(Context context){
        super(context.getApplicationContext(),"BDClinica",null,1 );
        contexto=context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            sqLiteDatabase.execSQL(createBDSQL);
            sqLiteDatabase.execSQL(createBDSQL1);
            sqLiteDatabase.execSQL(createBDSQL2);
            sqLiteDatabase.execSQL(createBDSQL3);

            sqLiteDatabase.execSQL("INSERT INTO ciudades (ciudad, calle) VALUES ('Badajoz', 'Calle Angel Quintanilla Ulla Nº1 Portal 6 Entreplanta A Edificio Montevideo')");
            sqLiteDatabase.execSQL("INSERT INTO ciudades (ciudad, calle) VALUES ('Merida', 'Calle Almendralejo Nº8')");
            sqLiteDatabase.execSQL("INSERT INTO ciudades (ciudad, calle) VALUES ('Don Benito', 'Avenida Alonso Martin Portal 17 Nº1D')");

            sqLiteDatabase.execSQL("INSERT INTO informes (usuario, fecha, archivo) VALUES ('laura93','15 agosto 2025','/sdcard/Download/informes.pdf')");
            sqLiteDatabase.execSQL("INSERT INTO informes (usuario, fecha, archivo) VALUES ('laura93','15 agosto 2025','/sdcard/Download/informes1.pdf')");

        }catch (SQLException e){
            Toast.makeText(contexto, "Error al crear la base de datos "+e, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        try {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS pacientes");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS ciudades");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS citas");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS informes");
            onCreate((sqLiteDatabase));
        }catch (SQLException e){
            Toast.makeText(contexto, "Error al actualizar la base de datos "+e,Toast.LENGTH_SHORT).show();
        }
    }

}
