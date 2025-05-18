package com.example.proyecto;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class ServiciosPsiquiatricos extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    Button depresivos, ansiedad, toc, bipolar, distimia, tdah, psicoticos, insomnio, impulsos, personalidad, cerrar;
    CardView cardView;
    TextView textViewTitulo1, textView1, textView3, textView4, textView5, textView6;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_servicios_psiquiatricos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cardView=findViewById(R.id.cardView1);
        textViewTitulo1=findViewById(R.id.textViewTitulo);
        textView1=findViewById(R.id.textView1);
        textView3=findViewById(R.id.textView3);
        textView4=findViewById(R.id.textView4);
        textView5=findViewById(R.id.textView5);
        textView6=findViewById(R.id.textView6);
        depresivos=findViewById(R.id.buttonDepresivos);
        ansiedad=findViewById(R.id.buttonAnsiedad);
        toc=findViewById(R.id.buttonToc);
        bipolar=findViewById(R.id.buttonBipolar);
        distimia=findViewById(R.id.buttonDistimia);
        tdah=findViewById(R.id.buttonTdah);
        psicoticos=findViewById(R.id.buttonPsicoticos);
        insomnio=findViewById(R.id.buttonInsomnio);
        impulsos=findViewById(R.id.buttonImpulsos);
        personalidad=findViewById(R.id.buttonPersonalidad);
        cerrar=findViewById(R.id.buttonCerrar);
        cardView.setVisibility(View.GONE);

        depresivos.setOnClickListener(this);
        ansiedad.setOnClickListener(this);
        toc.setOnClickListener(this);
        bipolar.setOnClickListener(this);
        distimia.setOnClickListener(this);
        tdah.setOnClickListener(this);
        psicoticos.setOnClickListener(this);
        insomnio.setOnClickListener(this);
        impulsos.setOnClickListener(this);
        personalidad.setOnClickListener(this);
        cerrar.setOnClickListener(this);

        // Asignar el OnClickListener al botón de Cerrar
        Button cerrar = findViewById(R.id.buttonCerrar);
        cerrar.setOnClickListener(v -> {
            cardView.setVisibility(View.GONE); // Ocultar el CardView
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
            Intent intent = new Intent(ServiciosPsiquiatricos.this, pantallaPrincipal.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_Perfil) {
            Intent intent = new Intent(ServiciosPsiquiatricos.this, pantallaPerfil.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_Informes) {
            Intent intent = new Intent(ServiciosPsiquiatricos.this, pantallaInformes.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_Citas) {
            Intent intent = new Intent(ServiciosPsiquiatricos.this, Citas.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_pedirCitas) {
            Intent intent = new Intent(ServiciosPsiquiatricos.this, pantallaCiudades.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_servicios) {
            Intent intent = new Intent(ServiciosPsiquiatricos.this, ServiciosPsiquiatricos.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_contacto) {
            Intent intent = new Intent(ServiciosPsiquiatricos.this, pantallaContactos.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_acerca) {
            Intent intent = new Intent(ServiciosPsiquiatricos.this, pantallaAcercaDe.class);
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
            Intent intent = new Intent(ServiciosPsiquiatricos.this, inicioSesion.class);
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

        cardView.setVisibility(View.VISIBLE);
        // Usamos if en lugar de switch
        if (view.getId() == R.id.buttonDepresivos) {
            textViewTitulo1.setText("Trastornos depresivos");
            textView1.setText("Cuando todo pesa, estamos aquí para usted.");
            textView3.setText("La depresión no es solo trizteza, es una carga invisible que afecta cada aspecto de su vida.");
            textView4.setText("Si siente que todo es demasiado, que la energia se escapa y que nada parece tener sentido, no está solo.");
            textView5.setText("Juntos, trabajaremos para aliviar esa carga, devolviéndole la ilusió, la fuerza y el bienestar que merece.");
            textView6.setText("Porque su vida vale ser vivida plenamente.");
        } else if (view.getId() == R.id.buttonAnsiedad) {
            textViewTitulo1.setText("Trastornos de ansiedad generalizada");
            textView1.setText("La calma es posible, incluso en la tormenta.");
            textView3.setText("Vivir con ansiedad es como estar en alerta constante, con preocupaciones que no se apagan y un corazón que no descansa.");
            textView4.setText("Pero no tiene que vivir así. Estoy aquí para ayudarle a encontrar la tranquilidad que necesita, devolviéndole el control sobre sus pensamientos y su vida.");
            textView6.setText("Porque la paz mental es posible.");
        } else if (view.getId() == R.id.buttonToc) {
            textViewTitulo1.setText("Trastornos obsesivo-compulsivo (TOC)");
            textView1.setText("Cuando los pensamientos manda, es hora de recuperar el control.");
            textView3.setText("Las obsesiones y compulsiones no solo son agotadoras, también pueden dominar su día a día.");
            textView4.setText("Si siente que sus pensamientos le arrastran y las rutinas se convierten en prisiones, sepa que hay una salida.");
            textView5.setText("Con un tratamiento adecuado, puede recuperar su libertad y vivir con más serenidad.");
            textView6.setText("Porque usted merece ser quien dirige su vida, no sus pensamientos.");
        } else if (view.getId() == R.id.buttonBipolar) {
            textViewTitulo1.setText("Trastornos del espectro bipolar");
            textView1.setText("Equilibrio para vivir con estabilidad.");
            textView3.setText("El trastorno bipolar no define quién es usted, pero sí puede complicar su camino.");
            textView4.setText("Si sus emociones oscilan entre extremos, estoy aquí para ayudarle a encontrar un equilibrio que le permita vivir con estabilidad, confianza y plenitud.");
            textView6.setText("Porque merece disfrutar de los buenos momentos sin miedo a los altibajos.");
        } else if (view.getId() == R.id.buttonDistimia) {
            textViewTitulo1.setText("Distimia");
            textView1.setText("Cuando la tristeza se instala, es hora de actuar.");
            textView3.setText("La distimia puede ser como una nube constante que ensombrece su vida, sin llegar a desaparecer del todo.");
            textView4.setText("Pero no tiene que acostumbrarse a vivir así.");
            textView5.setText("Con ayuda profesional, puede recuperar el brillo en su día a día y disfrutar de una vida más plena y equilibrada.");
            textView6.setText("Porque usted merece mucho más que conformarse.");
        } else if (view.getId() == R.id.buttonTdah) {
            textViewTitulo1.setText("Trastorno por Déficit de Atención e Hiperactividad (TDAH)");
            textView1.setText("Foco y calma para su vida.");
            textView3.setText("El TDAH es una falta de esfuerzo, es una dificultad real que afecta su capacidad de concentrarse, organizarse y mantener el ritmo.");
            textView4.setText("Pero con el enfoque adecuado, es posible recuperar el control y transformar esos retos en fortalezas.");
            textView6.setText("Usted puede vivir con claridad y confianza.");
        } else if (view.getId() == R.id.buttonPsicoticos) {
            textViewTitulo1.setText("Trastornos psicóticos");
            textView1.setText("Acompañándole en el camino hacia la claridad.");
            textView3.setText("Los trastornos psicóticos pueden ser desconcertantes, tanto para usted como para quienes le rodean.");
            textView4.setText("Si siente que la realidad se desdibuja o que los pensamientos le abruman, estoy aquí para ayudarle a encontrar estabilidad y seguridad.");
            textView6.setText("Porque cada persona merece comprensión, apoyo y una vida en equilibrio.");
        } else if (view.getId() == R.id.buttonInsomnio) {
            textViewTitulo1.setText("Insomnio");
            textView1.setText("El descanso que necesita, la energía que merece.");
            textView3.setText("Dormir mal no solo afecta su cuerpo, también desgasta su mente.");
            textView4.setText("Si las noches son interminables y los días parecen aún más pesados, es momento de buscar una solución.");
            textView5.setText("Con el tratamiento adecuado, puede recuperar el descanso y la energía para afrontar cada día con fuerza.");
            textView6.setText("Porque descansar es vivir mejor.");
        } else if (view.getId() == R.id.buttonImpulsos) {
            textViewTitulo1.setText("Trastornos de control de impulsos");
            textView1.setText("Cuando el impulso manda, podemos recuperar el control.");
            textView3.setText("Perder el control en momentos clave puede generar frustración, culpa y conflictos.");
            textView4.setText("Si siente que sus impulsos le superan, hay herramientas y tratamientos que pueden ayudarle a manejar sus reacciones y tomar decisiones desde la calma.");
            textView6.setText("Porque usted merece controlar sus actos, no que ellos le controlen a usted.");
        } else if (view.getId() == R.id.buttonPersonalidad) {
            textViewTitulo1.setText("Trastornos de personalidad");
            textView1.setText("Entenderse a uno mismo es el primer paso hacia el cambio.");
            textView3.setText("Los trastornos de personalidad no son una etiqueta, son una paarte de usted que merece ser comprendida.");
            textView4.setText("Con ayuda profesional, es posible gestionar las emociones, mejorar las relaciones y construir una vida más equilibrada.");
            textView6.setText("Porque usted no es un diagnóstico, es mucho más.");
        }
    }
}