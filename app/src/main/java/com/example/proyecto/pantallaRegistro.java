package com.example.proyecto;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
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

import java.net.PasswordAuthentication;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

public class pantallaRegistro extends AppCompatActivity  implements View.OnClickListener {

    EditText nombre, apellidos, telefono, email, usuario, clave;
    Button botonVolver, botonRegistrar;
    SQLiteDatabase bd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_registro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.text), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nombre = findViewById(R.id.textNombre);
        apellidos = findViewById(R.id.textApellidos);
        telefono = findViewById(R.id.textTelefono);
        email = findViewById(R.id.textEmail);
        usuario = findViewById(R.id.textUsuario);
        clave = findViewById(R.id.textContrasenia);
        botonRegistrar = findViewById(R.id.buttonRegistrar);
        botonVolver = findViewById(R.id.buttonVolver);

        botonRegistrar.setOnClickListener(this);
        botonVolver.setOnClickListener(this);

        BDClinica bdClinica = new BDClinica(getApplicationContext());
        bd = bdClinica.getWritableDatabase();

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
        if (v.getId() == R.id.buttonRegistrar) {
            //Obtener datos registrados
            String nombre1= nombre.getText().toString().trim();
            String apellidos1=apellidos.getText().toString().trim();
            String telefonoStr=telefono.getText().toString().trim();
            String email1=email.getText().toString().trim();
            String usuario1=usuario.getText().toString().trim();
            String clave1=clave.getText().toString().trim();

            //Validar que no haya campos vacios
            if(nombre1.isEmpty() || apellidos1.isEmpty()|| telefonoStr.isEmpty() || email1.isEmpty() || usuario1.isEmpty() || clave1.isEmpty()){
                //Toast.makeText(getApplicationContext(), "Por favor, complete todos los campos", Toast.LENGTH_LONG).show();
                mostrarAlerta("Error", "Por favor, complete todos los campos");
                return;
            }

            //Validar que el email tenga un formato correcto
            if(!Patterns.EMAIL_ADDRESS.matcher(email1).matches()){
                //Toast.makeText(getApplicationContext(), "Ingrese un email válido", Toast.LENGTH_LONG).show();
                mostrarAlerta("Error", "Ingrese un email válido");
                return;
            }

            //Convertir telefono a entero
            int telefono1;
            if (telefonoStr.length() != 9 || !telefonoStr.matches("\\d{9}")) {
                mostrarAlerta("Error", "El número de teléfono debe tener 9 dígitos.");
                return;
            }
            try{
                telefono1=Integer.parseInt(telefonoStr);
            }catch (NumberFormatException e){
                //Toast.makeText(getApplicationContext(), "Número de telefono inválido", Toast.LENGTH_LONG).show();
                mostrarAlerta("Error", "Número de telefono inválido");
                return;
            }

            //Verificar si el usuario ya existe en la base de datos
            Cursor cursor=bd.rawQuery("SELECT * FROM pacientes WHERE usuario=?", new String[]{usuario1});
            if(cursor.getCount()>0){
                //Toast.makeText(getApplicationContext(), "El usuario ya está registrado", Toast.LENGTH_LONG).show();
                mostrarAlerta("Error", "El usuario ya esta registrado. Cambiar el nombre de usuario.");
                cursor.close();
                return;
            }
            cursor.close();

            //Validar la seguridad de la contraseña
            if(!validarContrasenia(clave1)){
                //Toast.makeText(getApplicationContext(), "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial.", Toast.LENGTH_LONG).show();
                mostrarAlerta("Error", "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial.");
                return;
            }

            //Encriptar la contraseña antes de guardarla
            String claveEncriptada= encriptarSHA256(clave1);

            //Guardar datos en la base de datos
            ContentValues valores= new ContentValues();
            valores.put("nombre", nombre1);
            valores.put("apellidos", apellidos1);
            valores.put("telefono", telefono1);
            valores.put("email", email1);
            valores.put("usuario", usuario1);
            valores.put("clave", claveEncriptada);

            long resultado=bd.insert("pacientes", null, valores);
            if(resultado==-1){
                //Toast.makeText(getApplicationContext(), "Error al registrar el paciente", Toast.LENGTH_LONG).show();
                mostrarAlerta("Error", "Error al registrar el paciente");
            }else{
                Toast.makeText(getApplicationContext(), "Paciente Registrado", Toast.LENGTH_LONG).show();

                // Limpiar los campos EditText
                nombre.setText("");
                apellidos.setText("");
                telefono.setText("");
                email.setText("");
                usuario.setText("");
                clave.setText("");

                //Ir a la pantalla de inicio de sesion
                Intent i = new Intent(getApplicationContext(), inicioSesion.class);
                startActivity(i);
                finish();
            }
            bd.close();
        }
        if (v.getId() == R.id.buttonVolver) {
            Intent i = new Intent(getApplicationContext(), inicioSesion.class);
            startActivity(i);
            finish();
        }
    }

    //Metodo para validar la seguridad de la contraseña
    private boolean validarContrasenia(String password){
        String patron= "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!.])(?=\\S+$).{8,}$";
        return Pattern.compile(patron).matcher(password).matches();
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

    //Metodo para encriptar contraseña
    private String encriptarSHA256(String password){
        try{
            MessageDigest digest=MessageDigest.getInstance("SHA-256");
            byte[] hash=digest.digest(password.getBytes());
            StringBuilder hexString=new StringBuilder();
            for(byte b: hash){
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
            return null;
        }
    }
}