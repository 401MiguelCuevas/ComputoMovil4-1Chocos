package com.example.mygymspace;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EditarUsuariosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_usuarios);

        ImageButton btnBack = findViewById(R.id.btn_back);
        recyclerView = findViewById(R.id.rv_user_list);

        // Configurar botón de retroceso
        btnBack.setOnClickListener(v -> finish());

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, this::onUserClick);
        recyclerView.setAdapter(userAdapter);

        // Cargar usuarios desde la base de datos (PHP)
        fetchUsersFromDatabase();
    }

    private void fetchUsersFromDatabase() {
        // URL del archivo PHP (ajústala según tu configuración)
        String url = "http://192.168.1.69/obtener_usuarios.php";

        // Mostrar un indicador de carga mientras se obtienen los datos (opcional)
        // Puedes usar un ProgressBar o similar

        // Crear la solicitud
        com.android.volley.RequestQueue requestQueue = com.android.volley.toolbox.Volley.newRequestQueue(this);
        com.android.volley.toolbox.StringRequest stringRequest = new com.android.volley.toolbox.StringRequest(
                com.android.volley.Request.Method.GET,
                url,
                response -> {
                    try {
                        // Parsear la respuesta JSON
                        org.json.JSONArray jsonArray = new org.json.JSONArray(response);
                        userList.clear(); // Limpiar la lista antes de agregar nuevos datos
                        for (int i = 0; i < jsonArray.length(); i++) {
                            org.json.JSONObject jsonObject = jsonArray.getJSONObject(i);
                            int id = jsonObject.getInt("id");
                            String nombre = jsonObject.getString("nombre");
                            userList.add(new User(id, nombre)); // Agregar usuario a la lista
                        }
                        userAdapter.notifyDataSetChanged(); // Actualizar el RecyclerView
                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                        // Manejar errores de parsing
                    }
                },
                error -> {
                    error.printStackTrace();
                    // Manejar errores de la solicitud (e.g., conexión fallida)
                });

        // Agregar la solicitud a la cola
        requestQueue.add(stringRequest);
    }

    private void onUserClick(User user) {
        Intent intent = new Intent(this, EditarUsuarioActivity.class);
        intent.putExtra("userId", user.getId());
        startActivity(intent);
    }
}
