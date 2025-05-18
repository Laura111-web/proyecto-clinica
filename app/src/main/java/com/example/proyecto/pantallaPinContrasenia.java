package com.example.proyecto;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class pantallaPinContrasenia extends AppCompatActivity implements View.OnClickListener {

    private final String emailRemitente="clinicapsiquiatriadgm@gmail.com";
    private final String password="ajiu xlje gqvn peea";
    EditText email, clave;
    Button botonEnviar, botonVolver, botonSeguir;
    Session session;
    int pin=0;
    SQLiteDatabase bd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_pin_contrasenia);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        email=findViewById(R.id.textEmail);
        clave=findViewById(R.id.textConfirmarContrasenia);
        botonEnviar=findViewById(R.id.buttonEnviar);
        botonVolver=findViewById(R.id.buttonVolver);
        botonSeguir=findViewById(R.id.buttonSeguir);

        botonEnviar.setOnClickListener(this);
        botonVolver.setOnClickListener(this);
        botonSeguir.setOnClickListener(this);

        BDClinica bdClinica= new BDClinica(getApplicationContext());
        bd=bdClinica.getReadableDatabase();

        final boolean[] isPasswordVisible = {false};

        clave.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                int drawableEnd = clave.getRight() - clave.getPaddingEnd() - clave.getCompoundDrawables()[2].getBounds().width();
                // Si el usuario toca el icono del ojo
                if (event.getRawX() >= drawableEnd) {
                    isPasswordVisible[0] = !isPasswordVisible[0];
                    // Cambiar la visibilidad de la contraseña sin modificar el formato
                    if (isPasswordVisible[0]) {
                        clave.setTransformationMethod(HideReturnsTransformationMethod.getInstance()); // Mostrar contraseña
                        clave.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_lock_person_24, 0, R.drawable.baseline_remove_red_eye_24, 0);
                    } else {
                        clave.setTransformationMethod(PasswordTransformationMethod.getInstance()); // Ocultar contraseña
                        clave.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_lock_person_24, 0, R.drawable.baseline_remove_red_eye_24, 0);
                    }
                    // Mantener el cursor en su posición original
                    clave.setSelection(clave.getText().length());
                    return true;
                }
            }
            return false;
        });

    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.buttonEnviar){
            //Obtener el correo ingresado
            String emailIngresado=email.getText().toString().trim();
            Log.d("DEBUG", "Correo ingresado: " + emailIngresado);

            //Validar que el campo no este vacio
            if(emailIngresado.isEmpty()){
                mostrarAlerta("Error", "Ingrese su correo electrónico");
                return;
            }

            //Verificamos si el email existe en la base de datos
            Cursor cursor=bd.rawQuery("SELECT * FROM pacientes WHERE email=?", new String[]{emailIngresado});
            if(cursor.getCount()==0){
                mostrarAlerta("Error", "El correo no está registrado.");
                cursor.close();
                return;
            }
            cursor.close();

            //Generar un numero codigo aleatorio(pin)
            pin= new Random().nextInt(900000) +100000;
            Log.d("DEBUG", "PIN generado: " + pin);

            //mandar el correo electronico con el pin
            enviarCorreo(emailIngresado, "Codigo de Recuperación", "Su codigo Pin es " +pin);

            mostrarAlerta("Exíto", "Se ha enviado un Pin a su correo electrónico");

        }if(v.getId()==R.id.buttonVolver){
            Intent i=new Intent(getApplicationContext(), inicioSesion.class);
            startActivity(i);
        }if (v.getId()==R.id.buttonSeguir) {
            String pinIngresadoS = clave.getText().toString().trim();

            //Verificar que el pin no este vacio
            if (pinIngresadoS.isEmpty()) {
                mostrarAlerta("Error", "Ingrese codigo pin");
                return;
            }
            try {
                int pinIngresado = Integer.parseInt(pinIngresadoS);
                Log.d("DEBUG", "PIN ingresado: " + pinIngresado);
                if (pinIngresado == pin) {
                    //una vez que se nos pide el pin , se compara con este pin, si son iguales se permite cambiar contraseña
                    Intent i = new Intent(getApplicationContext(), pantallaOlvidarContrasenia.class);
                    startActivity(i);
                } else {
                    mostrarAlerta("Error", "Pin incorrecto");
                }
            }catch (NumberFormatException e){
                mostrarAlerta("Error", "Formato de Pin incorrecto");

            }
        }
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

    // Metodo para mostrar AlertDialog
    private void mostrarAlerta(String titulo, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", null) // "Aceptar" cierra el cuadro de diálogo
                .setCancelable(false)
                .create()
                .show();
    }
}