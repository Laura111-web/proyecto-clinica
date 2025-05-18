package com.example.proyecto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class inicioSesion extends AppCompatActivity implements View.OnClickListener {

    EditText usuario, clave;
    Button btiniciar;
    TextView registrar, olvidarClave;
    SQLiteDatabase bd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio_sesion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.text), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usuario=findViewById(R.id.textUsuario);
        clave=findViewById(R.id.textContrasenia);
        btiniciar=findViewById(R.id.buttonInicio);
        registrar=findViewById(R.id.textRegistar);
        olvidarClave=findViewById(R.id.textOlvidar);

        btiniciar.setOnClickListener(this);
        registrar.setOnClickListener(this);
        olvidarClave.setOnClickListener(this);

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
        if(v.getId()==R.id.buttonInicio){
            //Obtener datos registrados
            String usuario1=usuario.getText().toString().trim();
            String clave1=clave.getText().toString().trim();

            //Validar que no haya campos vacios
            if(usuario1.isEmpty() || clave1.isEmpty()){
                //Toast.makeText(getApplicationContext(), "Por favor, complete todos los campos", Toast.LENGTH_LONG).show();
                mostrarAlerta("Error", "Por favor, complete todos los campos");
                return;
            }

            //Verificar si el usuario y la contrasena coinciden con datos en la base de datos
            Cursor cursor=bd.rawQuery("SELECT * FROM pacientes WHERE usuario=? AND clave=?", new String[]{usuario1, encriptarSHA256(clave1)});
            if(cursor.getCount()==0){
                //Toast.makeText(getApplicationContext(), "El usuario ya está registrado", Toast.LENGTH_LONG).show();
                mostrarAlerta("Error", "Usuario o contraseña incorrectos.");
                cursor.close();
                return;
            }

            SharedPreferences sharedPreferences = getSharedPreferences("Sesion", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("usuario", usuario1); // Guarda el nombre del usuario
            editor.apply();
            cursor.close();

            // Limpiar los campos EditText
            usuario.setText("");
            clave.setText("");

            //Ir a la pantalla de inicio de sesion
            Intent i= new Intent(getApplicationContext(), pantallaPrincipal.class);
            startActivity(i);
            finish();
        }if(v.getId()==R.id.textRegistar){
            Intent i=new Intent(getApplicationContext(), pantallaRegistro.class);
            startActivity(i);
        }if (v.getId()==R.id.textOlvidar){
            Intent i=new Intent(getApplicationContext(), pantallaPinContrasenia.class);
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