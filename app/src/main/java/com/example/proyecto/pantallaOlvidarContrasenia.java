package com.example.proyecto;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

public class pantallaOlvidarContrasenia extends AppCompatActivity implements View.OnClickListener {

    EditText  email, usuario, clave, confirmarClave;
    Button botonVolver, botonConfirmar;
    SQLiteDatabase bd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_olvidar_contrasenia);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.text), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        email=findViewById(R.id.textEmail1);
        usuario=findViewById(R.id.textUsuario1);
        clave=findViewById(R.id.textContrasenia);
        confirmarClave=findViewById(R.id.textConfirmarContrasenia);
        botonVolver=findViewById(R.id.buttonVolver);
        botonConfirmar=findViewById(R.id.buttonConfirmar);

        botonConfirmar.setOnClickListener(this);
        botonVolver.setOnClickListener(this);

        BDClinica bdClinica = new BDClinica(getApplicationContext());
        bd = bdClinica.getReadableDatabase();

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

        confirmarClave.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                int drawableEnd = confirmarClave.getRight() - confirmarClave.getPaddingEnd() - confirmarClave.getCompoundDrawables()[2].getBounds().width();
                // Si el usuario toca el icono del ojo
                if (event.getRawX() >= drawableEnd) {
                    isPasswordVisible[0] = !isPasswordVisible[0];
                    // Cambiar la visibilidad de la contraseña sin modificar el formato
                    if (isPasswordVisible[0]) {
                        confirmarClave.setTransformationMethod(HideReturnsTransformationMethod.getInstance()); // Mostrar contraseña
                        confirmarClave.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_lock_person_24, 0, R.drawable.baseline_remove_red_eye_24, 0);
                    } else {
                        confirmarClave.setTransformationMethod(PasswordTransformationMethod.getInstance()); // Ocultar contraseña
                        confirmarClave.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_lock_person_24, 0, R.drawable.baseline_remove_red_eye_24, 0);
                    }
                    // Mantener el cursor en su posición original
                    confirmarClave.setSelection(confirmarClave.getText().length());
                    return true;
                }
            }
            return false;
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonConfirmar) {
            //Obtener datos registrados
            String email1=email.getText().toString().trim();
            String usuario1=usuario.getText().toString().trim();
            String clave1=clave.getText().toString().trim();
            String claveConfirmar=confirmarClave.getText().toString().trim();

            //Validar que no haya campos vacios
            if(email1.isEmpty() || usuario1.isEmpty() || clave1.isEmpty() || claveConfirmar.isEmpty()){
                //Toast.makeText(getApplicationContext(), "Por favor, complete todos los campos", Toast.LENGTH_LONG).show();
                mostrarAlerta("Error", "Por favor, complete todos los campos");
                return;
            }

            //Verificamos si el email y usuario existen en la base de datos
            Cursor cursor=bd.rawQuery("SELECT * FROM pacientes WHERE email=?  AND usuario=?", new String[]{email1, usuario1});
            if(cursor.getCount()==0){
                mostrarAlerta("Error", "El correo o usuario no está registrado.");
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

            //Verificar que ambas contraseña coincidan
            if(!clave1.equals(claveConfirmar)){
                mostrarAlerta("Error", "Las contraseñas no coinciden");
                return;
            }

            //Encriptar la contraseña antes de guardarla
            String claveEncriptada= encriptarSHA256(clave1);

            //Actualizar la contraseña en la base de datos
            ContentValues valores= new ContentValues();
            valores.put("clave", claveEncriptada);

            int contraseñaActualizada=bd.update("pacientes", valores, "email=? AND usuario=?", new String[] {email1, usuario1});
            if(contraseñaActualizada>0){
                //Toast.makeText(getApplicationContext(), "Contraseña cambiada", Toast.LENGTH_LONG).show();
                mostrarAlerta("Exito", "Contraseña Cambiada");
                // Limpiar los campos EditText
                email.setText("");
                usuario.setText("");
                clave.setText("");
                confirmarClave.setText("");

                //Ir a la pantalla de inicio de sesion
                Intent i = new Intent(getApplicationContext(), inicioSesion.class);
                startActivity(i);
                finish();
            }else{
                mostrarAlerta("Error", "Hubo un problema al actualizar la contraseña");
            }
            bd.close();

        }
        if (v.getId() == R.id.buttonVolver) {
            Intent i = new Intent(getApplicationContext(), inicioSesion.class);
            startActivity(i);

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

    //Metodo para validar la seguridad de la contraseña
    private boolean validarContrasenia(String password){
        String patron= "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!.])(?=\\S+$).{8,}$";
        return Pattern.compile(patron).matcher(password).matches();
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