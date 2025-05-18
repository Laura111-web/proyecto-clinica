package com.example.proyecto;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class pantallaContactos extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Button volver, enviar;
    Session session;
    SQLiteDatabase bd;
    EditText telefono, email;
    TextView consulta;
    String correoDestino="clinicapsiquiatriadgm@gmail.com";
    String password="ajiu xlje gqvn peea";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_contactos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        drawerLayout = findViewById(R.id.main);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        telefono=findViewById(R.id.textTelefono);
        email=findViewById(R.id.textEmail);
        consulta=findViewById(R.id.textConsulta);
        volver=findViewById(R.id.buttonVolver);
        enviar=findViewById(R.id.buttonEnviar);

        volver.setOnClickListener(this);
        enviar.setOnClickListener(this);

        BDClinica bdClinica= new BDClinica(getApplicationContext());
        bd=bdClinica.getReadableDatabase();

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
            Intent intent = new Intent(pantallaContactos.this, pantallaPrincipal.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_Perfil) {
            Intent intent = new Intent(pantallaContactos.this, pantallaPerfil.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_Informes) {
            Intent intent = new Intent(pantallaContactos.this, pantallaInformes.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_Citas) {
            Intent intent = new Intent(pantallaContactos.this, Citas.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_pedirCitas) {
            Intent intent = new Intent(pantallaContactos.this, pantallaCiudades.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_servicios) {
            Intent intent = new Intent(pantallaContactos.this, ServiciosPsiquiatricos.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_contacto) {
            Intent intent = new Intent(pantallaContactos.this, pantallaContactos.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_acerca) {
            Intent intent = new Intent(pantallaContactos.this, pantallaAcercaDe.class);
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
            Intent intent = new Intent(pantallaContactos.this, inicioSesion.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpia la pila de actividades
            startActivity(intent);
            finish(); // Cierra la actividad actual
        });

        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss()); // Cierra la alerta sin hacer nada

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", null) // "Aceptar" cierra el cuadro de diálogo
                .setCancelable(false)
                .create()
                .show();
    }

    private void mostrarAlerta1(String titulo, String mensaje, boolean irAPantallaPrincipal) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titulo)
                .setMessage(mensaje)
                .setCancelable(false)
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    if (irAPantallaPrincipal) {
                        Intent intent = new Intent(pantallaContactos.this, pantallaPrincipal.class);
                        startActivity(intent);
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonVolver) {
            Intent i = new Intent(getApplicationContext(), pantallaPrincipal.class);
            startActivity(i);
        }
        if (v.getId() == R.id.buttonEnviar) {
            existePaciente();
        }

    }

    private void existePaciente(){
        //Obtener datos registrados
        String telefonoPaciente=telefono.getText().toString().trim();
        String emailPaciente=email.getText().toString().trim();
        String consultaPaciente=consulta.getText().toString().trim();

        //Validar que no haya campos vacios
        if(telefonoPaciente.isEmpty() || emailPaciente.isEmpty() || consultaPaciente.isEmpty()){
            //Toast.makeText(getApplicationContext(), "Por favor, complete todos los campos", Toast.LENGTH_LONG).show();
            mostrarAlerta("Error", "Por favor, complete todos los campos");
            return;
        }

        //Verificar si el usuario y la contrasena coinciden con datos en la base de datos
        Cursor cursor=bd.rawQuery("SELECT nombre, apellidos FROM pacientes WHERE telefono=? AND email=?", new String[]{telefonoPaciente,emailPaciente});
        if (cursor.moveToFirst()) {
            // Obtener los valores del paciente
            String nombrePaciente = cursor.getString(0);
            String apellidosPaciente = cursor.getString(1);
            cursor.close();

            // Enviar el correo con los datos completos
            enviarCorreo(nombrePaciente, apellidosPaciente, emailPaciente, telefonoPaciente, consultaPaciente);

            mostrarAlerta1("Éxito", "Consulta enviada", true);
        } else {
            mostrarAlerta("Error", "Teléfono o email incorrectos.");
            cursor.close();
        }
    }

    private void enviarCorreo(String nombrePaciente, String apellidosPaciente, String emailPaciente, String telefonoPaciente, String consultaPaciente) {
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

        // Dirección de correo destino y remitente
        final String correoDestino = "clinicapsiquiatriadgm@gmail.com"; // El correo al que se envía
        final String emailRemitente = emailPaciente; // El correo del paciente que se ingresa
        final String asunto = "Consulta desde la aplicación"; // Asunto del correo
        final String mensaje = "Mensaje del paciente (Consulta desde la aplicación).\n" +
                "Nombre: " + nombrePaciente + " " + apellidosPaciente + "\n" +
                "Teléfono: " + telefonoPaciente + "\n" +
                "Correo: " + emailPaciente + "\n" +
                "Consulta: " + consultaPaciente; // Cuerpo del mensaje

        try{
            Log.d("DEBUG", "Iniciando sesión para enviar correo...");
            session= Session.getDefaultInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(correoDestino, password);
                }
            });

            if(session!=null){
                Message message=new MimeMessage(session);
                message.setFrom(new InternetAddress(emailRemitente));
                message.setReplyTo(InternetAddress.parse(emailPaciente)); // Cuando respondan, irá al paciente
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correoDestino));
                message.setSubject(asunto);
                message.setText(mensaje);
                Transport.send(message);
                Log.d("DEBUG", "Correo enviado desde: " + emailRemitente + " a: " + correoDestino);
            }else {
                Log.e("ERROR", "No se pudo iniciar la sesión de correo");
            }

        }catch (Exception e){
            Log.e("ERROR", "Error al enviar correo: " + e.getMessage());
            e.printStackTrace();
        }
    }

}