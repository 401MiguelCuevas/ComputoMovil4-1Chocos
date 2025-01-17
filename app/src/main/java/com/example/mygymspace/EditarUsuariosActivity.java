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

        // BOTON DE RETROCESO
        btnBack.setOnClickListener(v -> finish());

        // RECYCLER VIEW
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, this::onUserClick);
        recyclerView.setAdapter(userAdapter);

        // CARGAR USUARIOS DE LA BD
        fetchUsersFromDatabase();
    }

    private void fetchUsersFromDatabase() {
        // URL del archivo PHP
        String url = "http://192.168.1.77/obtener_usuarios.php";

        // CREAR SOLICITUD
        com.android.volley.RequestQueue requestQueue = com.android.volley.toolbox.Volley.newRequestQueue(this);
        com.android.volley.toolbox.StringRequest stringRequest = new com.android.volley.toolbox.StringRequest(
                com.android.volley.Request.Method.GET,
                url,
                response -> {
                    try {
                        // PARSEAR RESPUESTAS JSON
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

        // AGREGAR SOLICITUD A LA COLA
        requestQueue.add(stringRequest);
    }

    private void onUserClick(User user) {
        Intent intent = new Intent(this, EditarUsuarioActivity.class);
        intent.putExtra("userId", user.getId());
        startActivity(intent);
    }
}
