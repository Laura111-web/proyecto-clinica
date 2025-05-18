package com.example.proyecto;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.Toast;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class pantallaPedirCitaCambiarCita extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Spinner tipoCita, hora;
    CalendarView calendario;
    Button volver, enviar;
    SQLiteDatabase bd;
    String ciudadSeleccionada;
    String fechaSeleccionada;
    String usuario;
    int idCita = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_pedir_cita_cambiar_cita);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tipoCita = findViewById(R.id.spinnerTipoCita);
        hora = findViewById(R.id.spinnerHoras);
        calendario = findViewById(R.id.calendarViewCita);
        volver = findViewById(R.id.buttonVolver);
        enviar = findViewById(R.id.buttonEnviar);

        volver.setOnClickListener(this);
        enviar.setOnClickListener(this);

        // Obtener ciudad seleccionada
        ciudadSeleccionada = getIntent().getStringExtra("ciudad");
        if (ciudadSeleccionada == null) {
            Toast.makeText(this, "Error: No se ha seleccionado una ciudad.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Obtener idCita recibido desde la pantalla anterior
        idCita = getIntent().getIntExtra("cambiar", -1); // Recibimos el ID de la cita a modificar
        if (idCita == -1) {
            Log.e("PantallaPedirCita", "Error: No se ha recibido el ID de la cita.");
            Toast.makeText(this, "Error: No se ha recibido el ID de la cita.", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Configurar adaptador para el Spinner de tipo de cita
        ArrayAdapter<CharSequence> adapterTipoCita = ArrayAdapter.createFromResource(
                this, R.array.tipoCita_array, android.R.layout.simple_spinner_item);
        adapterTipoCita.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipoCita.setAdapter(adapterTipoCita);

        // Configurar el calendario según la ciudad seleccionada
        configurarCalendario(ciudadSeleccionada);
        configurarHoras(ciudadSeleccionada);

        // Configurar el idioma español en toda la app
        Locale locale = new Locale("es", "ES");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        BDClinica bdClinica = new BDClinica(getApplicationContext());
        bd = bdClinica.getReadableDatabase();

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
            Intent intent = new Intent(pantallaPedirCitaCambiarCita.this, pantallaPrincipal.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_Perfil) {
            Intent intent = new Intent(pantallaPedirCitaCambiarCita.this, pantallaPerfil.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_Informes) {
            Intent intent = new Intent(pantallaPedirCitaCambiarCita.this, pantallaInformes.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_Citas) {
            Intent intent = new Intent(pantallaPedirCitaCambiarCita.this, Citas.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_pedirCitas) {
            Intent intent = new Intent(pantallaPedirCitaCambiarCita.this, pantallaCiudades.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_servicios) {
            Intent intent = new Intent(pantallaPedirCitaCambiarCita.this, ServiciosPsiquiatricos.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_contacto) {
            Intent intent = new Intent(pantallaPedirCitaCambiarCita.this, pantallaContactos.class);
            startActivity(intent);
            return true;
        }
        if (itemId == R.id.nav_acerca) {
            Intent intent = new Intent(pantallaPedirCitaCambiarCita.this, pantallaAcercaDe.class);
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
            Intent intent = new Intent(pantallaPedirCitaCambiarCita.this, inicioSesion.class);
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonVolver) {
            Intent i = new Intent(getApplicationContext(), pantallaCiudadesCambiarCita.class);
            i.putExtra("cambiar", idCita);
            startActivity(i);
        }
        if (v.getId() == R.id.buttonEnviar) {

            if (fechaSeleccionada == null || fechaSeleccionada.isEmpty()) {
                mostrarAlerta("Error", "Seleccione una fecha válida");
                //Toast.makeText(this, "Seleccione una fecha válida", Toast.LENGTH_SHORT).show();
                return; // No continuar si la fecha no ha sido seleccionada
            }

            if (!modificarCita()) {
                Log.e("PantallaPedirCita", "Error al modificar cita");
                return;
            }

            String tipo = tipoCita.getSelectedItem().toString();
            String horaSeleccionada = hora.getSelectedItem().toString();

            Log.d("PantallaPedirCita", "Modificación exitosa, pasando a pantallaConfirmacionCitas");
            // Si se modificó correctamente, se muestra la confirmación
            Intent intent = new Intent(getApplicationContext(), pantallaConfirmarCitasCambiarCita.class);
            intent.putExtra("cambiar", idCita);
            intent.putExtra("usuario", usuario);
            intent.putExtra("ciudad", ciudadSeleccionada);
            intent.putExtra("tipoCita", tipo);
            intent.putExtra("fecha", fechaSeleccionada);
            intent.putExtra("hora", horaSeleccionada);
            startActivity(intent);

        }
    }

    private void configurarCalendario(String ciudad) {

        calendario.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // Crear instancia de Calendar con la fecha seleccionada
            Calendar calendar = Calendar.getInstance();
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
            calendar.set(year, month, dayOfMonth);

            // Guardamos la fecha seleccionada
            //fechaSeleccionada = calendar.getTimeInMillis();

            // Obtener la fecha seleccionada en formato legible
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("es", "ES"));
            fechaSeleccionada = sdf.format(calendar.getTime());

            // Obtener día de la semana (1 = Sunday, ..., 7 = Saturday)
            int diaDeLaSemana = calendar.get(Calendar.DAY_OF_WEEK);

            // Variable para verificar si la fecha es válida
            boolean fechaValida = false;

            // Reglas por ciudad
            switch (ciudadSeleccionada) {
                case "Badajoz":
                    // Badajoz solo permite miércoles
                    fechaValida = (diaDeLaSemana == Calendar.WEDNESDAY);
                    break;
                case "Merida":
                    // Mérida solo permite jueves
                    fechaValida = (diaDeLaSemana == Calendar.THURSDAY);
                    break;
                case "Don Benito":
                    // Don Benito solo permite martes y viernes
                    fechaValida = (diaDeLaSemana == Calendar.TUESDAY || diaDeLaSemana == Calendar.FRIDAY);
                    break;
                default:
                    // Si la ciudad no es ninguna de las anteriores, no permitimos la selección
                    fechaValida = false;
                    break;
            }

            // Verificar si la fecha es válida para la ciudad seleccionada
            if (!fechaValida) {
                // Restaurar la fecha en el calendario
                calendario.setDate(calendar.getTimeInMillis());
                mostrarAlerta("Error", "Este día no es válido para " + ciudadSeleccionada);
                //Toast.makeText(this, "Este día no es válido para " + ciudadSeleccionada, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configurarHoras(String ciudad) {
        ArrayAdapter<CharSequence> adapterHoras;
        if (ciudad.equals("Badajoz")) {
            // Solo horas de la tarde
            ArrayAdapter<CharSequence> adapterTardes = ArrayAdapter.createFromResource(
                    this, R.array.horasTardes_array, android.R.layout.simple_spinner_item);
            adapterTardes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            hora.setAdapter(adapterTardes);
        } else if (ciudad.equals("Don Benito")) {
            // Solo horas de la mañana
            ArrayAdapter<CharSequence> adapterManana = ArrayAdapter.createFromResource(
                    this, R.array.horasMananas_array, android.R.layout.simple_spinner_item);
            adapterManana.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            hora.setAdapter(adapterManana);
        } else if (ciudad.equals("Merida")) {
            // Horas tanto de mañana como tarde
            ArrayAdapter<CharSequence> adapterCombinado = ArrayAdapter.createFromResource(
                    this, R.array.horasCombinadas_array, android.R.layout.simple_spinner_item);
            adapterCombinado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            hora.setAdapter(adapterCombinado);
        }
    }

    private boolean modificarCita() {

        Log.d("PantallaPedirCita", "Iniciando modificación de cita");
        if (fechaSeleccionada == null || fechaSeleccionada.isEmpty()) {
            mostrarAlerta("Error", "Fecha no seleccionada");
            //Toast.makeText(this, "Fecha no seleccionada", Toast.LENGTH_SHORT).show();
            return false;
        }

        String tipo = tipoCita.getSelectedItem().toString();
        String horaSeleccionada = hora.getSelectedItem().toString();

        SharedPreferences sharedPreferences = getSharedPreferences("Sesion", MODE_PRIVATE);
        usuario = sharedPreferences.getString("usuario", "Usuario no encontrado");
        Log.d("PantallaPedirCita", "Usuario: " + usuario);

        // Comprobar si ya existe una cita para la misma fecha y hora
        String query = "SELECT * FROM citas WHERE tipoCita=? AND fecha = ? AND hora = ?";
        Cursor cursor = bd.rawQuery(query, new String[]{tipo, fechaSeleccionada, horaSeleccionada});

        if (cursor.getCount() > 0) {
            mostrarAlerta("Error", "Esta cita ha sido reservada");
            //Toast.makeText(this, "Esta cita ya ha sido reservada", Toast.LENGTH_SHORT).show();
            cursor.close();
            return false;
        }
        cursor.close();

        // Comprobar si el usuario ya tiene una cita en el mismo día, sin importar la hora
        String queryUsuarioDia = "SELECT * FROM citas WHERE usuario=? AND fecha=?";
        Cursor cursorUsuarioDia = bd.rawQuery(queryUsuarioDia, new String[]{usuario, fechaSeleccionada});

        if (cursorUsuarioDia.getCount() > 0) {
            mostrarAlerta("Error", "Ya tienes una cita reservada para este día");
            //Toast.makeText(this, "Ya tienes una cita reservada para este día.", Toast.LENGTH_SHORT).show();
            cursorUsuarioDia.close();
            return false;
        }
        cursorUsuarioDia.close();

        ContentValues values = new ContentValues();
        values.put("usuario", usuario);
        values.put("ciudad", ciudadSeleccionada);
        values.put("tipoCita", tipo);
        values.put("fecha", fechaSeleccionada);
        values.put("hora", horaSeleccionada);

        Log.d("PantallaPedirCita", "Actualizando la cita en la base de datos");
        // Actualizar la cita en la base de datos
        int rowsAffected = bd.update("citas", values, "idCita=?", new String[]{String.valueOf(idCita)});

        if (rowsAffected > 0) {
            Log.d("PantallaPedirCita", "Cita modificada correctamente");
            // Si se actualizó la cita correctamente, mostrar un mensaje
            mostrarAlerta("Exito", "Cita modificada correctamente");
            //Toast.makeText(this, "Cita modificada correctamente", Toast.LENGTH_SHORT).show();

            // Opcional: Puedes retornar los datos actualizados a la pantalla anterior o abrir otra pantalla
            Intent resultIntent = new Intent();
            resultIntent.putExtra("idCita", idCita); // Puedes devolver el ID o cualquier otro dato
            setResult(RESULT_OK, resultIntent);
            finish(); // Volver a la pantalla anterior
        } else {
            Log.e("PantallaPedirCita", "Error al modificar la cita");
            // Si no se actualizó, mostrar un mensaje de error
            mostrarAlerta("Error", "Error al modificar la cita");
            //Toast.makeText(this, "Error al modificar la cita", Toast.LENGTH_SHORT).show();
        }

        return true;

    }
}