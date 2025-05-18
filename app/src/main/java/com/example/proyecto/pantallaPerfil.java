package com.example.proyecto;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class pantallaPerfil extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    ImageView imagen;
    TextView nombre, apellidos, telefono, email, usuario;
    Button botonVolver;
    SQLiteDatabase bd;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_perfil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imagen= findViewById(R.id.imageView2);
        nombre = findViewById(R.id.textNombre);
        apellidos = findViewById(R.id.textApellidos);
        telefono = findViewById(R.id.textTelefono);
        email = findViewById(R.id.textEmail);
        usuario = findViewById(R.id.textUsuario);
        botonVolver = findViewById(R.id.buttonVolver);

        botonVolver.setOnClickListener(this);
        imagen.setOnClickListener(v -> seleccionarImagen());

        BDClinica bdClinica = new BDClinica(getApplicationContext());
        bd = bdClinica.getWritableDatabase();

        SharedPreferences sharedPreferences = getSharedPreferences("Sesion", MODE_PRIVATE);
        String Usuario = sharedPreferences.getString("usuario", "Usuario no encontrado");
        usuario.setText(Usuario);
        Log.d("pantallaPerfil", "Usuario obtenido: " + Usuario);

        // Obtener datos del usuario desde SQLite
        try {
            Cursor cursor = bd.rawQuery("SELECT nombre, apellidos, telefono, email FROM pacientes WHERE usuario = ?", new String[]{Usuario});
            if (cursor.moveToFirst()) {
                nombre.setText(cursor.getString(0));
                apellidos.setText(cursor.getString(1));
                telefono.setText(cursor.getString(2));
                email.setText(cursor.getString(3));
                Log.d("pantallaPerfil", "Datos de usuario obtenidos correctamente");
            } else {
                Log.d("pantallaPerfil", "No se encontraron datos para el usuario");
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("pantallaPerfil", "Error al obtener datos de SQLite: " + e.getMessage());
        }

        // Cargar imagen guardada
        try {
            String imagenGuardada = sharedPreferences.getString("imagenPerfil", null);
            if (imagenGuardada != null) {
                imagen.setImageURI(Uri.parse(imagenGuardada));
                Log.d("pantallaPerfil", "Imagen de perfil cargada correctamente");
            }
        } catch (Exception e) {
            Log.e("pantallaPerfil", "Error al cargar la imagen de perfil: " + e.getMessage());
        }

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
            Intent intent = new Intent(pantallaPerfil.this, pantallaPrincipal.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_Perfil) {
            Intent intent = new Intent(pantallaPerfil.this, pantallaPerfil.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_Informes) {
            Intent intent = new Intent(pantallaPerfil.this, pantallaInformes.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_Citas) {
            Intent intent = new Intent(pantallaPerfil.this, Citas.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_pedirCitas) {
            Intent intent = new Intent(pantallaPerfil.this, pantallaCiudades.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_servicios) {
            Intent intent = new Intent(pantallaPerfil.this, ServiciosPsiquiatricos.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_contacto) {
            Intent intent = new Intent(pantallaPerfil.this, pantallaContactos.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_acerca) {
            Intent intent = new Intent(pantallaPerfil.this, pantallaAcercaDe.class);
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
            Intent intent = new Intent(pantallaPerfil.this, inicioSesion.class);
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
            finish();
        }
    }

    private void seleccionarImagen() {
        Log.d("pantallaPerfil", "Seleccionando imagen");
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            imagen.setImageURI(imageUri);
            guardarImagen(imageUri);
            Log.d("pantallaPerfil", "Imagen seleccionada y guardada");
        } else {
            Log.d("pantallaPerfil", "Selección de imagen cancelada");
        }
    }

    private void guardarImagen(Uri imageUri) {
        SharedPreferences sharedPreferences = getSharedPreferences("Sesion", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("imagenPerfil", imageUri.toString());
        editor.apply();
        Log.d("pantallaPerfil", "Imagen guardada en SharedPreferences");
    }
}