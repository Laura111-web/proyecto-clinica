package com.example.proyecto;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Citas extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, AdapterView.OnItemClickListener {

    private final String emailRemitente="clinicapsiquiatriadgm@gmail.com";
    private final String password="ajiu xlje gqvn peea";
    Session session;
    ListView listViewCitas;
    Button anular, cambiar;
    SQLiteDatabase bd;
    ArrayList<String> arrayListCitas;
    HashMap<String, String> citasMap;  // Utilizamos un HashMap para almacenar citas con clave (usuario, fecha)
    String selectedCitaKey = "";  // Variable para almacenar la clave de la cita seleccionada
    int selectedPosition = -1;  // Variable para almacenar la posición seleccionada en el ListView
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_citas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listViewCitas=findViewById(R.id.listaCitas);
        anular=findViewById(R.id.buttonAnular);
        cambiar=findViewById(R.id.buttonCambiar);
        anular.setOnClickListener(this);
        cambiar.setOnClickListener(this);
        listViewCitas.setOnItemClickListener(this);

        BDClinica bdClinica= new BDClinica(getApplicationContext());
        bd=bdClinica.getReadableDatabase();

        cargarCitas();

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
            Intent intent = new Intent(Citas.this, pantallaPrincipal.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_Perfil) {
            Intent intent = new Intent(Citas.this, pantallaPerfil.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_Informes) {
            Intent intent = new Intent(Citas.this, pantallaInformes.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_Citas) {
            Intent intent = new Intent(Citas.this, Citas.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_pedirCitas) {
            Intent intent = new Intent(Citas.this, pantallaCiudades.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_servicios) {
            Intent intent = new Intent(Citas.this, ServiciosPsiquiatricos.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_contacto) {
            Intent intent = new Intent(Citas.this, pantallaContactos.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_acerca) {
            Intent intent = new Intent(Citas.this, pantallaAcercaDe.class);
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
            Intent intent = new Intent(Citas.this, inicioSesion.class);
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
        if(view.getId()==R.id.buttonAnular){
            Log.d("DEBUG", "Botón Anular presionado");
            if(!selectedCitaKey.isEmpty()){
                Log.d("DEBUG", "Cita seleccionada: " + selectedCitaKey);
                mostrarAlertaAnular();
            }else{
                Log.e("ERROR", "No se seleccionó ninguna cita.");
                mostrarAlerta("Error", "Seleccione una cita primero");
            }
        }
        if(view.getId()==R.id.buttonCambiar){
            if(!selectedCitaKey.isEmpty()){
                mostrarAlertaCambiar();
            }else{
                mostrarAlerta("Error", "Seleccione una cita primero");
            }
        }

    }

    private void cargarCitas() {
        // Obtener el usuario desde las SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("Sesion", MODE_PRIVATE);
        String usuarioSesion = sharedPreferences.getString("usuario", "");

        // Modificar la consulta SQL para filtrar por el usuario que ha iniciado sesión
        String query = "SELECT citas.*, ciudades.calle, ciudades.telefono FROM citas " +
                "INNER JOIN ciudades ON citas.ciudad = ciudades.ciudad " +
                "WHERE citas.usuario = ? ";

        // Ejecutar la consulta con el usuario actual como parámetro
        Cursor fila = bd.rawQuery(query, new String[]{usuarioSesion});
        //Cursor fila = bd.rawQuery("SELECT citas.*, ciudades.calle, ciudades.telefono FROM citas INNER JOIN ciudades ON citas.ciudad = ciudades.ciudad  ORDER BY citas.fecha DESC", null);
        arrayListCitas = new ArrayList<>();
        citasMap=new HashMap<>();

        if (fila.moveToFirst()) {
            do {
                int idCita=fila.getInt(0);
                String usuario = fila.getString(1);
                String ciudad = fila.getString(2);
                String tipoCita=fila.getString(3);
                String fecha = fila.getString(4);

                // Formateamos la fecha con el formato deseado
                //SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("es", "ES"));
                //String fechaFormateada = sdf.format(fecha);

                String hora=fila.getString(5);
                int precio= fila.getInt(6);
                String personal=fila.getString(7);
                String calle=fila.getString(8);
                int telefono=fila.getInt(9);

                // Almacenamos la cita en el HashMap
                citasMap.put(String.valueOf(idCita), "Usuario: " + usuario + "\nCiudad: " + ciudad + "\nCalle: " + calle + "\nTipo de Cita: " + tipoCita + "\nFecha: " + fecha + "\nHora: " + hora + "\nPrecio: " + precio + "€\nPersonal: " + personal + "\nTelefono: " + telefono);

                arrayListCitas.add("Usuario: " + usuario + "\nCiudad: " + ciudad + "\nCalle: " + calle + "\nTipo de Cita: " + tipoCita + "\nFecha: " + fecha + "\nHora: " + hora + "\nPrecio: " + precio + "€\nPersonal: " +personal+"\nTelefono: " +telefono);
            } while(fila.moveToNext());
        }
        fila.close();
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayListCitas){
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
        };
        listViewCitas.setAdapter(adapter);
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        selectedPosition = position;  // Guardamos la posición seleccionada
        // Obtener el detalle de la cita seleccionada
        String citaSeleccionada = arrayListCitas.get(position);

        // Extraemos el usuario y la fecha de la cita seleccionada
        String[] detallesCita = citaSeleccionada.split("\n");
        String usuario = detallesCita[0].split(":")[1].trim();  // Extraemos el usuario
        String fecha = detallesCita[4].split(":")[1].trim();  // Extraemos la fecha

        // Consulta SQL para obtener el idCita real usando usuario y fecha
        String query = "SELECT idCita FROM citas WHERE usuario = ? AND fecha = ?";
        Cursor cursor = bd.rawQuery(query, new String[]{usuario, fecha});

        if (cursor.moveToFirst()) {
            // Obtener el idCita real
            int idCita = cursor.getInt(0);  // Suponiendo que el idCita está en la primera columna
            Log.d("DEBUG", "Cita seleccionada con ID: " + idCita);

            // Asignar el idCita real a la variable selectedCitaKey
            selectedCitaKey = String.valueOf(idCita);
        } else {
            Log.e("ERROR", "No se encontró la cita en la base de datos.");
            mostrarAlerta("Error", "No se pudo obtener el idCita.");
        }
        cursor.close();  // Asegúrate de cerrar el cursor

        // Notificar que la lista ha cambiado (si es necesario)
        ((ArrayAdapter) listViewCitas.getAdapter()).notifyDataSetChanged();
    }

    private void mostrarAlertaAnular() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Anular Cita");
        builder.setMessage("¿Está seguro de que desea anular esta cita?");

        builder.setPositiveButton("Sí", (dialog, which) -> {
            if (!selectedCitaKey.isEmpty()) {
                int idCita=Integer.parseInt(selectedCitaKey);
                Log.d("DEBUG", "Cita seleccionada para cambiar, idCita: " + idCita);

                // Obtener los detalles de la cita
                String detallesCita = citasMap.get(selectedCitaKey);
                Log.d("DEBUG", "Detalles de la cita: " + detallesCita);
                // Obtener el correo y enviar notificación
                String destinatario = obtenerCorreoPaciente(idCita);
                if (destinatario != null && !destinatario.isEmpty()) {
                    Log.d("DEBUG", "Correo encontrado: " + destinatario);
                    enviarCorreo(destinatario, "Anulación de Cita", "Su cita ha sido anulada.\n\nDatos de la cita:\n " + detallesCita);
                } else {
                    Log.e("ERROR", "No se encontró correo para el usuario");
                }

                    // Consulta SQL para eliminar la cita
                String sql = "DELETE FROM citas WHERE idCita = ?";
                SQLiteStatement statement = bd.compileStatement(sql);
                statement.bindLong(1, idCita);
                int filasAfectadas=statement.executeUpdateDelete();
                Log.d("EliminarCita", "Filas afectadas por la eliminación: " + filasAfectadas);

                if (filasAfectadas > 0) {
                    // Eliminar la cita de la lista y refrescar el ListView
                    arrayListCitas.remove(selectedPosition);
                    citasMap.remove(selectedCitaKey);

                    // Reiniciar la clave y posición seleccionada
                    selectedCitaKey = "";
                    selectedPosition = -1;

                    // Notificar que los datos han cambiado
                    ((ArrayAdapter) listViewCitas.getAdapter()).notifyDataSetChanged();

                    // Mostrar alerta de éxito
                    mostrarAlerta("Cita anulada", "La cita ha sido anulada correctamente.");

                    // Mostrar alerta para preguntar si se desea reservar otra cita
                    mostrarAlertaCitaAnulada();
                }
            } else {
                // Si la cita seleccionada está vacía
                Log.d("EliminarCita", "No se pudo eliminar la cita. No se encontraron coincidencias.");
                mostrarAlerta("Error", "No se pudo eliminar la cita. Intente nuevamente.");
            }
        });

        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    // Nueva alerta para preguntar si desea reservar otra cita
    private void mostrarAlertaCitaAnulada() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cita anulada");
        builder.setMessage("La cita ha sido eliminada correctamente.");

        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            mostrarAlertaReservarOtra();
        });
        builder.setCancelable(false); // Evita que se cierre sin pulsar aceptar
        builder.create().show();
    }

    // Nueva alerta para preguntar si desea reservar otra cita
    private void mostrarAlertaReservarOtra() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reservar otra cita");
        builder.setMessage("¿Desea reservar otra cita?");
        builder.setPositiveButton("Sí", (dialog, which) -> {
            Intent intent = new Intent(Citas.this, pantallaCiudades.class);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void mostrarAlertaCambiar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cambiar Cita");
        builder.setMessage("¿Está seguro de que desea cambiar esta cita?");

        builder.setPositiveButton("Sí", (dialog, which) -> {
            if (!selectedCitaKey.isEmpty()) {
                int idCita=Integer.parseInt(selectedCitaKey);
                // Pasamos la clave de la cita seleccionada para poder actualizarla después
                Log.d("DEBUG", "Cita seleccionada para cambiar, idCita: " + idCita);
                Intent intent = new Intent(Citas.this, pantallaCiudadesCambiarCita.class);
                intent.putExtra("cambiar", idCita);  // Pasamos la clave de la cita seleccionada
                Log.d("DEBUG", "Pasando el idCita al Intent: " + idCita);
                startActivity(intent);
            } else {
                mostrarAlerta("Error", "Seleccione una cita primero");
            }
        });

        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data!=null){
            // Obtener los datos de la nueva cita desde el Intent
            String nuevaCiudad = data.getStringExtra("nuevaCiudad");
            String nuevoTipo= data.getStringExtra("nuevoTipo");
            String nuevaFecha = data.getStringExtra("nuevaFecha");
            String nuevaHora = data.getStringExtra("nuevaHora");

            // Ahora actualizamos la cita seleccionada en la base de datos
            int idCita = Integer.parseInt(selectedCitaKey);

            String sql = "UPDATE citas SET ciudad=?, tipo =?, fecha = ?, hora = ? WHERE idCita = ?";
            SQLiteStatement statement = bd.compileStatement(sql);
            statement.bindLong(1, idCita);
            statement.bindString(2, nuevaCiudad);
            statement.bindString(3, nuevoTipo);
            statement.bindString(4, nuevaFecha);
            statement.bindString(5, nuevaHora);

            int filasAfectadas = statement.executeUpdateDelete();
            if (filasAfectadas > 0) {
                // Actualizar la lista de citas
                cargarCitas();
                // Mostrar alerta de éxito
                mostrarAlerta("Cita modificada", "La cita ha sido modificada correctamente.");
            } else {
                mostrarAlerta("Error", "No se pudo modificar la cita. Intente nuevamente.");
            }
        }
    }

    private String obtenerCorreoPaciente(int idCita) {
        String usuario = "";
        try {
            // Obtener el usuario a partir del idCita
            Cursor cursor = bd.rawQuery("SELECT usuario FROM citas WHERE idCita = ?", new String[]{String.valueOf(idCita)});
            if (cursor.moveToFirst()) {
                usuario = cursor.getString(0);  // Obtener el usuario
            } else {
                Log.e("ERROR", "No se encontró usuario para idCita: " + idCita);
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("ERROR", "Error al obtener usuario para idCita: " + e.getMessage());
        }

        String emailPaciente = null;
        try {
            Cursor cursor = bd.rawQuery("SELECT email FROM pacientes WHERE usuario = ?", new String[]{usuario});
            if (cursor.moveToFirst()) {
                emailPaciente = cursor.getString(0);
            } else {
                Log.e("ERROR", "No se encontró el correo para el usuario: " + usuario);
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("ERROR", "Error al obtener correo: " + e.getMessage());
        }

        return emailPaciente;
    }
    private void enviarCorreo(String destinatario, String asunto, String mensaje) {
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Properties properties=new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465"); // O 587 si usas STARTTLS
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.enable", "true"); // Habilita SSL/TLS
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2"); // Asegura que se use TLS 1.2
        properties.put("mail.smtp.starttls.enable", "true"); // Para puertos 587 (STARTTLS)
        properties.put("mail.smtp.socketFactory.port","465");
        properties.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.host", "smtp.gmail.com"); // Cambia según tu servidor SMTP
        properties.put("mail.smtp.auth", "true");

        try{
            Log.d("DEBUG", "Iniciando sesión para enviar correo...");
            session= Session.getDefaultInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailRemitente, password);
                }
            });

            if(session!=null){
                Message message=new MimeMessage(session);
                message.setFrom(new InternetAddress(emailRemitente));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
                message.setSubject(asunto);
                message.setText(mensaje);
                Transport.send(message);
                Log.d("DEBUG", "Correo enviado desde: " + emailRemitente + " a: " + destinatario);
            }else {
                Log.e("ERROR", "No se pudo iniciar la sesión de correo");
            }

        }catch (Exception e){
            Log.e("ERROR", "Error al enviar correo: " + e.getMessage());
            e.printStackTrace();
        }

    }
}