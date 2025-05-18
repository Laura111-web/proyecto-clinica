package com.example.proyecto;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

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

public class pantallaPrincipal extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_principal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
            Intent intent = new Intent(pantallaPrincipal.this, pantallaPrincipal.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_Perfil) {
            Intent intent = new Intent(pantallaPrincipal.this, pantallaPerfil.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_Informes) {
            Intent intent = new Intent(pantallaPrincipal.this, pantallaInformes.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_Citas) {
            Intent intent = new Intent(pantallaPrincipal.this, Citas.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_pedirCitas) {
            Intent intent = new Intent(pantallaPrincipal.this, pantallaCiudades.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_servicios) {
            Intent intent = new Intent(pantallaPrincipal.this, ServiciosPsiquiatricos.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_contacto) {
            Intent intent = new Intent(pantallaPrincipal.this, pantallaContactos.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_acerca) {
            Intent intent = new Intent(pantallaPrincipal.this, pantallaAcercaDe.class);
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
            Intent intent = new Intent(pantallaPrincipal.this, inicioSesion.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpia la pila de actividades
            startActivity(intent);
            finish(); // Cierra la actividad actual
        });

        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss()); // Cierra la alerta sin hacer nada

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}