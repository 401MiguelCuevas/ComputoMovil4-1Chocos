package com.example.mygymspace;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GymActivity extends AppCompatActivity {

    private TextView tvGymName;
    private Button btnPalabraClave;
    private Button btnEditUser;
    private Button btnAddUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym);

        // Inicializar vistas
        tvGymName = findViewById(R.id.tv_gym_name);
        btnPalabraClave = findViewById(R.id.btn_palabra_clave);
        btnEditUser = findViewById(R.id.btn_edit_user);
        btnAddUser = findViewById(R.id.btn_add_user);

        // Configurar evento para el botón "palabra clave"
        btnPalabraClave.setOnClickListener(v -> {
            Intent intent = new Intent(GymActivity.this, PalabraClaveActivity.class);
            startActivity(intent);
        });

        // Configurar evento para el botón "Editar usuario"
        btnEditUser.setOnClickListener(v -> {
            Intent intent = new Intent(GymActivity.this, EditarUsuariosActivity.class);
            startActivity(intent);
        });

        // Configurar evento para el botón "Agregar usuario"
        btnAddUser.setOnClickListener(v -> {
            Intent intent = new Intent(GymActivity.this, AgregarUsuarioActivity.class);
            startActivity(intent);
        });
    }
}