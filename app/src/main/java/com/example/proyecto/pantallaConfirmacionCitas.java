package com.example.proyecto;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class pantallaConfirmacionCitas extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private final String emailRemitente="clinicapsiquiatriadgm@gmail.com";
    private final String password="ajiu xlje gqvn peea";
    Session session;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView cita;
    Button volver, verCita;
    SQLiteDatabase bd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_confirmacion_citas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        drawerLayout = findViewById(R.id.main);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        cita=findViewById(R.id.textCitaConfirmada);
        volver=findViewById(R.id.buttonVolver);
        verCita=findViewById(R.id.buttonVerCita);

        volver.setOnClickListener(this);
        verCita.setOnClickListener(this);

        // Recuperar los datos de la cita de la base de datos
        BDClinica bdClinica = new BDClinica(getApplicationContext());
        bd = bdClinica.getReadableDatabase();

        mostrarCita();

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
            Intent intent = new Intent(pantallaConfirmacionCitas.this, pantallaPrincipal.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_Perfil) {
            Intent intent = new Intent(pantallaConfirmacionCitas.this, pantallaPerfil.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_Informes) {
            Intent intent = new Intent(pantallaConfirmacionCitas.this, pantallaInformes.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_Citas) {
            Intent intent = new Intent(pantallaConfirmacionCitas.this, Citas.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_pedirCitas) {
            Intent intent = new Intent(pantallaConfirmacionCitas.this, pantallaCiudades.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_servicios) {
            Intent intent = new Intent(pantallaConfirmacionCitas.this, ServiciosPsiquiatricos.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_contacto) {
            Intent intent = new Intent(pantallaConfirmacionCitas.this, pantallaContactos.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_acerca) {
            Intent intent = new Intent(pantallaConfirmacionCitas.this, pantallaAcercaDe.class);
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
            Intent intent = new Intent(pantallaConfirmacionCitas.this, inicioSesion.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpia la pila de actividades
            startActivity(intent);
            finish(); // Cierra la actividad actual
        });

        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss()); // Cierra la alerta sin hacer nada

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonVolver) {
            Intent i = new Intent(getApplicationContext(), pantallaPrincipal.class);
            startActivity(i);
        }
        if (v.getId() == R.id.buttonVerCita) {
            Intent i = new Intent(getApplicationContext(), Citas.class);
            startActivity(i);
        }

    }
    private void mostrarCita() {

        // Recuperar los datos del Intent
        Intent intent = getIntent();
        String usuario = intent.getStringExtra("usuario");
        String ciudad = intent.getStringExtra("ciudad");
        String tipoCita = intent.getStringExtra("tipoCita");
        String fechaSeleccionada = intent.getStringExtra("fecha"); // La fecha en milisegundos
        String horaSeleccionada = intent.getStringExtra("hora");

        Log.d("DEBUG", "mostrarCita: Datos recibidos - Usuario: " + usuario + ", Ciudad: " + ciudad);

        if (usuario == null || usuario.isEmpty()) {
            Log.e("ERROR", "El usuario está vacío o es nulo. No se puede mostrar la cita.");
            return; // Salir del metodo si el usuario es null
        }

        //Recuperar la ultima cita de la base de datos
        Cursor fila = bd.rawQuery("SELECT * FROM citas WHERE usuario= ? ORDER BY idCita DESC LIMIT 1", new String[]{usuario});
        if (fila.moveToFirst()) {
            do {
                usuario = fila.getString(1);
                ciudad = fila.getString(2);
                tipoCita = fila.getString(3);
                fechaSeleccionada=fila.getString(4);
                String hora = fila.getString(5);
                int precio = fila.getInt(6);
                String personal = fila.getString(7);

                // Obtener la calle y el teléfono desde la tabla 'ciudades'
                Cursor ciudadCursor = bd.rawQuery("SELECT calle, telefono FROM ciudades WHERE ciudad = ?", new String[]{ciudad});

                String calle = "No disponible";
                int telefono = 0;
                if (ciudadCursor.moveToFirst()) {
                    calle = ciudadCursor.getString(0); // Recuperamos la calle
                    telefono = ciudadCursor.getInt(1);
                } else {
                    Log.d("DB_DEBUG", "No se encontró la ciudad: " + ciudad);
                }
                ciudadCursor.close(); // Cerrar el cursor de la ciudad

                // Mostrar los datos en el TextView
                cita.setText("\nUsuario: " + usuario + "\nCiudad: " + ciudad + "\nCalle: " + calle +"\nTipo de cita: " + tipoCita +
                        "\nFecha: " + fechaSeleccionada + "\nHora: " + hora +
                        "\nPrecio: " + precio + " €\nPersonal: " + personal+ "\nTelefono:" +telefono);

                Log.d("DEBUG", "mostrarCita: Cita mostrada con éxito.");

                // Obtener los detalles de la cita
                String detallesCita = cita.getText().toString();
                Log.d("DEBUG", "Detalles de la cita: " + detallesCita);

                // Obtener el correo y enviar notificación
                String destinatario = obtenerCorreoPaciente(usuario);
                if (destinatario != null && !destinatario.isEmpty()) {
                    Log.d("DEBUG", "Correo encontrado: " + destinatario);
                    enviarCorreo(destinatario, "Confirmación de Cita", "Su cita ha sido reservada.\n\nDatos de la cita:\n " + detallesCita);
                } else {
                    Log.e("ERROR", "No se encontró correo para el usuario");
                }
            } while (fila.moveToNext());
        }
        fila.close();
    }

    private String obtenerCorreoPaciente(String usuario) {
        if (usuario == null || usuario.isEmpty()) {
            Log.e("ERROR", "Usuario vacío al obtener el correo.");
            return null;
        }

        String emailPaciente=null;

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