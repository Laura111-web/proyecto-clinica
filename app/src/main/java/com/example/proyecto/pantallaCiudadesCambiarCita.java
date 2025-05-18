package com.example.proyecto;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

public class pantallaCiudadesCambiarCita extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Button badajoz, merida, donBenito;
    TextView cogerUsuario;
    SQLiteDatabase bd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_ciudades_cambiar_cita);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        badajoz=findViewById(R.id.buttonBadajoz);
        merida=findViewById(R.id.buttonMerida);
        donBenito=findViewById(R.id.buttonDonBenito);
        cogerUsuario=findViewById(R.id.textInsertarNombre);

        badajoz.setOnClickListener(this);
        merida.setOnClickListener(this);
        donBenito.setOnClickListener(this);

        BDClinica bdClinica= new BDClinica(getApplicationContext());
        bd=bdClinica.getReadableDatabase();

        SharedPreferences sharedPreferences = getSharedPreferences("Sesion", MODE_PRIVATE);
        String nombreUsuario = sharedPreferences.getString("usuario", "Usuario no encontrado");
        cogerUsuario.setText(nombreUsuario);

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
            Intent intent = new Intent(pantallaCiudadesCambiarCita.this, pantallaPrincipal.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_Perfil) {
            Intent intent = new Intent(pantallaCiudadesCambiarCita.this, pantallaPerfil.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_Informes) {
            Intent intent = new Intent(pantallaCiudadesCambiarCita.this, pantallaInformes.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_Citas) {
            Intent intent = new Intent(pantallaCiudadesCambiarCita.this, Citas.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_pedirCitas) {
            Intent intent = new Intent(pantallaCiudadesCambiarCita.this, pantallaCiudades.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_servicios) {
            Intent intent = new Intent(pantallaCiudadesCambiarCita.this, ServiciosPsiquiatricos.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_contacto) {
            Intent intent = new Intent(pantallaCiudadesCambiarCita.this, pantallaContactos.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_acerca) {
            Intent intent = new Intent(pantallaCiudadesCambiarCita.this, pantallaAcercaDe.class);
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
            Intent intent = new Intent(pantallaCiudadesCambiarCita.this, inicioSesion.class);
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
        String ciudad = "";
        int idCita = getIntent().getIntExtra("cambiar", -1);

        // Verificamos si el idCita es válido
        if (idCita == -1) {
            Log.e("pantallaCiudadesCambiarCita", "Error: No se ha recibido el ID de la cita.");
        }

        if (v.getId() == R.id.buttonBadajoz) {
            Intent i = new Intent(getApplicationContext(), pantallaPedirCitaCambiarCita.class);
            i.putExtra("cambiar", idCita);
            i.putExtra("ciudad", "Badajoz");
            startActivity(i);
        }
        if (v.getId() == R.id.buttonMerida) {
            Intent i = new Intent(getApplicationContext(), pantallaPedirCitaCambiarCita.class);
            i.putExtra("cambiar", idCita);
            i.putExtra("ciudad", "Merida");
            startActivity(i);
        }
        if (v.getId() == R.id.buttonDonBenito) {
            Intent i = new Intent(getApplicationContext(), pantallaPedirCitaCambiarCita.class);
            i.putExtra("cambiar", idCita);
            i.putExtra("ciudad", "Don Benito");
            startActivity(i);
        }

    }
}