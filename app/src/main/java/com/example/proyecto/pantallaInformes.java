package com.example.proyecto;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class pantallaInformes extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, AdapterView.OnItemClickListener {

    ListView listViewInformes;
    Button descargar;
    SQLiteDatabase bd;
    ArrayList<String> arrayListInformes;
    ArrayList<String> arrayListUrls;
    String selectedInformeKey = "";
    int selectedPosition = -1;
    String selectedUrl="";
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_informes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listViewInformes=findViewById(R.id.listaInformes);
        descargar=findViewById(R.id.buttonDescargar);
        descargar.setOnClickListener(this);
        listViewInformes.setOnItemClickListener(this);

        //listViewInformes.setOnItemClickListener((parent, view, position, id) -> selectedUrl = arrayListUrls.get(position));

        BDClinica bdClinica= new BDClinica(getApplicationContext());
        bd=bdClinica.getReadableDatabase();

        cargarInformes();

        drawerLayout = findViewById(R.id.main);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_view);
    }

    private void cargarInformes() {
        Cursor fila = bd.rawQuery("SELECT idInforme, usuario, fecha, archivo FROM informes", null);
        arrayListInformes = new ArrayList<>();
        arrayListUrls= new ArrayList<>();
        ArrayList<Integer> arrayListIds = new ArrayList<>();
        if (fila.moveToFirst()) {
            do {
                int idInforme = fila.getInt(0);
                String usuario = fila.getString(1);
                String fecha = fila.getString(2);
                String archivo= fila.getString(3);

                arrayListInformes.add("Usuario: " + usuario + "\nFecha: " + fecha + "\nArchivo: " + archivo);
                arrayListUrls.add(archivo);
                arrayListIds.add(idInforme);
            } while(fila.moveToNext());
        }
        fila.close();
        //ArrayAdapter<String> adapter= new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayListInformes);
        //ArrayAdapter<String> adapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayListInformes){
        listViewInformes.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayListInformes){
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                // Si esta posición está seleccionada, cambiar el color de fondo
                if (position == selectedPosition) {
                    view.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));  // Cambia el color de fondo
                } else {
                    view.setBackgroundColor(getResources().getColor(android.R.color.white));  // Restaura el color original
                }

                return view;
            }
        });
        listViewInformes.setTag(arrayListIds);
        //listViewInformes.setAdapter(adapter);
    }

    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuitem) {
        int itemId = menuitem.getItemId();
        if (itemId == R.id.nav_Principal) {
            Intent intent = new Intent(pantallaInformes.this, pantallaPrincipal.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_Perfil) {
            Intent intent = new Intent(pantallaInformes.this, pantallaPerfil.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_Informes) {
            Intent intent = new Intent(pantallaInformes.this, pantallaInformes.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_Citas) {
            Intent intent = new Intent(pantallaInformes.this, Citas.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_pedirCitas) {
            Intent intent = new Intent(pantallaInformes.this, pantallaCiudades.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_servicios) {
            Intent intent = new Intent(pantallaInformes.this, ServiciosPsiquiatricos.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_contacto) {
            Intent intent = new Intent(pantallaInformes.this, pantallaContactos.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_acerca) {
            Intent intent = new Intent(pantallaInformes.this, pantallaAcercaDe.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_CerrarSesion) {
            mostrarAlertaCerrarSesion();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return super.onOptionsItemSelected(menuitem);
    }
    private void mostrarAlertaCerrarSesion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cerrar Sesión");
        builder.setMessage("¿Está seguro de que desea cerrar sesión?");

        builder.setPositiveButton("Sí", (dialog, which) -> {
            // Aquí puedes limpiar los datos de sesión si es necesario
            Intent intent = new Intent(pantallaInformes.this, inicioSesion.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpia la pila de actividades
            startActivity(intent);
            finish(); // Cierra la actividad actual
        });

        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss()); // Cierra la alerta sin hacer nada

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.buttonDescargar) {
            if (!selectedUrl.isEmpty()) {
                descargarArchivo(selectedUrl);
            } else {
                mostrarAlerta("Error", "Seleccione un informe primero.");
            }
        }
        /*
        if (view.getId() == R.id.buttonDescargar) {
            if (!selectedInformeKey.isEmpty() && !selectedUrl.isEmpty()) {
                if (selectedUrl.startsWith("http://") || selectedUrl.startsWith("https://")) {
                    descargarArchivo(selectedUrl, selectedInformeKey);
                    mostrarAlerta("Éxito", "Descargando archivo...");
                } else {
                    mostrarAlerta("Error", "La URL del archivo no es válida.");
                }
            } else {
                mostrarAlerta("Error", "Seleccione un informe primero.");
            }
        }

         */
        /*
        if(view.getId()==R.id.buttonDescargar){

            if (!selectedInformeKey.isEmpty()) {
                if (selectedUrl.startsWith("http://") || selectedUrl.startsWith("https://")) {
                    descargarArchivo(selectedUrl);
                    mostrarAlerta("Éxito", "Descargando archivo...");
                } else {
                    mostrarAlerta("Error", "La URL del archivo no es válida.");
                }
            } else {
                mostrarAlerta("Error", "Seleccione un informe primero.");
            }

         */
            /*
            if(!selectedInformeKey.isEmpty()){
                descargarArchivo(selectedUrl);
                mostrarAlerta("Exito", "Descargar archivo.");
            }else {
                mostrarAlerta("Error", "Seleccione un informe primero.");
            }

             */


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectedPosition = position;  // Guardamos la posición seleccionada
        selectedUrl = arrayListUrls.get(position);  // Obtenemos la URL

        // Recuperar el ID del informe desde la lista guardada
        ArrayList<Integer> arrayListIds = (ArrayList<Integer>) listViewInformes.getTag();
        if (arrayListIds != null && position < arrayListIds.size()) {
            selectedInformeKey = String.valueOf(arrayListIds.get(position));
            Log.d("DEBUG", "Informe seleccionado con ID: " + selectedInformeKey);
        } else {
            Log.e("ERROR", "No se pudo obtener el idInforme.");
            mostrarAlerta("Error", "No se pudo obtener el idInforme.");
        }

        // Notificar que la lista ha cambiado para actualizar la selección visualmente
        ((ArrayAdapter) listViewInformes.getAdapter()).notifyDataSetChanged();

        /*
        //selectedUrl=arrayListUrls.get(position);
        selectedPosition = position;  // Guardamos la posición seleccionada
        // Obtener el detalle de la cita seleccionada
        String informeSeleccionado = arrayListInformes.get(position);

        // Extraemos el usuario y la fecha de la cita seleccionada
        String[] detallesCita = informeSeleccionado.split("\n");
        String usuario = detallesCita[0].split(":")[1].trim();  // Extraemos el usuario
        String fecha = detallesCita[1].split(":")[1].trim();  // Extraemos la fecha

        // Consulta SQL para obtener el idCita real usando usuario y fecha
        String query = "SELECT idInforme FROM informes WHERE usuario = ? AND fecha = ?";
        Cursor cursor = bd.rawQuery(query, new String[]{usuario, fecha});

        if (cursor.moveToFirst()) {
            // Obtener el idCita real
            int idInformes = cursor.getInt(0);  // Suponiendo que el idCita está en la primera columna
            Log.d("DEBUG", "Cita seleccionada con ID: " + idInformes);

            // Asignar el idCita real a la variable selectedCitaKey
            selectedInformeKey = String.valueOf(idInformes);
        } else {
            Log.e("ERROR", "No se encontró la cita en la base de datos.");
            mostrarAlerta("Error", "No se pudo obtener el idCita.");
        }
        cursor.close();  // Asegúrate de cerrar el cursor

        // Notificar que la lista ha cambiado (si es necesario)
        ((ArrayAdapter) listViewInformes.getAdapter()).notifyDataSetChanged();
        */
    }


    private void descargarArchivo(String rutaArchivo){

        File archivoOrigen = new File(rutaArchivo);
        File archivoDestino = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), archivoOrigen.getName());

        if (!archivoOrigen.exists()) {
            mostrarAlerta("Error", "El archivo no existe en: " + rutaArchivo);
            return;
        }

        try {
            // Mover el archivo al directorio de descargas
            if (archivoOrigen.renameTo(archivoDestino)) {
                mostrarAlerta("Éxito", "Archivo descargado en: " + archivoDestino.getAbsolutePath());

                // Notificar al sistema para que el archivo aparezca en la galería de descargas
                MediaScannerConnection.scanFile(this, new String[]{archivoDestino.getAbsolutePath()}, null, null);

            } else {
                mostrarAlerta("Error", "No se pudo mover el archivo.");
            }

        } catch (Exception e) {
            mostrarAlerta("Error", "Error al descargar el archivo: " + e.getMessage());
        }
        /*
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle("Descargando Informe " + idInforme);
        request.setDescription("Descargando archivo...");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "informe_" + idInforme + ".pdf");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
        Toast.makeText(this, "Descarga iniciada", Toast.LENGTH_LONG).show();
        */
    }

    // Metodo para mostrar AlertDialog
    private void mostrarAlerta(String titulo, String mensaje) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", null) // "Aceptar" cierra el cuadro de diálogo
                .setCancelable(false)
                .create()
                .show();
    }

    private void copiarArchivoLocal(String filePath, String idInforme) {
        try {
            // Convertir "file://..." a una ruta válida
            String rutaLimpia = filePath.replace("file://", "");
            File origen = new File(rutaLimpia);
            File destino = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "informe_" + idInforme + ".pdf");

            if (!origen.exists()) {
                Log.e("ERROR", "El archivo de origen no existe: " + rutaLimpia);
                mostrarAlerta("Error", "El archivo no existe en el emulador.");
                return;
            }

            FileInputStream fis = new FileInputStream(origen);
            FileOutputStream fos = new FileOutputStream(destino);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }

            fis.close();
            fos.close();

            mostrarAlerta("Éxito", "Informe copiado en Descargas.");
            Log.d("DEBUG", "Archivo copiado a: " + destino.getAbsolutePath());

        } catch (Exception e) {
            Log.e("ERROR", "Error al copiar el archivo: " + e.getMessage());
            mostrarAlerta("Error", "No se pudo copiar el archivo.");
        }
    }
}