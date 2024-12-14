package com.example.mygymspace;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class InicioActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private RecyclerView recyclerView;
    private Button btnAddGym;
    private GymAdapter adapter;
    private List<String> gymList;
    private static final int REQUEST_ADD_GYM = 100; // Código de solicitud para la actividad de agregar gimnasio

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        // Inicialización de vistas
        tvWelcome = findViewById(R.id.tv_welcome);
        recyclerView = findViewById(R.id.recyclerView);
        btnAddGym = findViewById(R.id.btn_add_gym);

        // Configuración inicial
        tvWelcome.setText("Bienvenid@");

        // Configurar el RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        gymList = new ArrayList<>();
        adapter = new GymAdapter(gymList);
        recyclerView.setAdapter(adapter);

        // Cargar datos desde la API
        loadGymsFromAPI();

        // Acción del botón "Agregar nuevo gym"
        btnAddGym.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, AgregarGymActivity.class);
            startActivityForResult(intent, REQUEST_ADD_GYM); // Esperar resultado de la actividad
        });
    }

    private void loadGymsFromAPI() {
        String url = "http://192.168.1.69/get_gyms.php";

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        gymList.clear(); // Limpia la lista antes de agregar nuevos datos
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject gym = response.getJSONObject(i);
                                gymList.add(gym.getString("nombre"));
                            }
                            adapter.notifyDataSetChanged(); // Notifica al adaptador que los datos han cambiado
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(InicioActivity.this, "Error al procesar datos", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(InicioActivity.this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }
        );

        queue.add(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADD_GYM && resultCode == RESULT_OK && data != null) {
            // Recibir el nombre del gimnasio agregado
            String newGymName = data.getStringExtra("gymName");
            if (newGymName != null && !newGymName.isEmpty()) {
                // Agregar el nuevo gimnasio a la lista
                gymList.add(newGymName);
                adapter.notifyDataSetChanged(); // Actualizar el RecyclerView
                Toast.makeText(this, "Gimnasio añadido: " + newGymName, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "El nombre del gimnasio está vacío", Toast.LENGTH_SHORT).show();
            }
        }
    }
}