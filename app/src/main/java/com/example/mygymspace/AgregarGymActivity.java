package com.example.mygymspace;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class AgregarGymActivity extends AppCompatActivity {

    private EditText etGymName;
    private Button btnAddGym;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_nuevo_gym);

        // Inicialización de vistas
        etGymName = findViewById(R.id.et_gym_name);
        btnAddGym = findViewById(R.id.btn_add_gym);
        btnBack = findViewById(R.id.btn_back);

        // Configurar botón de retroceder
        btnBack.setOnClickListener(v -> finish());

        // Configurar acción del botón "Agregar nuevo Gym"
        btnAddGym.setOnClickListener(v -> {
            String gymName = etGymName.getText().toString().trim();
            if (validateGymName(gymName)) {
                addGymToDatabase(gymName);
            }
        });
    }

    private boolean validateGymName(String gymName) {
        if (gymName.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa un nombre para el gimnasio", Toast.LENGTH_SHORT).show();
            return false;
        } else if (gymName.length() < 3) {
            Toast.makeText(this, "El nombre del gimnasio debe tener al menos 3 caracteres", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void addGymToDatabase(String gymName) {
        String url = "http://192.168.1.69/add_gym.php";

        // Crear una solicitud JSON
        JSONObject postData = new JSONObject();
        try {
            postData.put("nombre", gymName);
        } catch (JSONException e) {
            Toast.makeText(this, "Error al crear la solicitud", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                // Gimnasio agregado correctamente
                                Toast.makeText(AgregarGymActivity.this, "Gimnasio agregado exitosamente", Toast.LENGTH_SHORT).show();

                                // Devolver el resultado a InicioActivity
                                Intent intent = new Intent();
                                intent.putExtra("gymName", gymName);
                                setResult(RESULT_OK, intent);
                                finish(); // Finaliza la actividad
                            } else {
                                // Mostrar mensaje de error enviado por el servidor
                                String message = response.optString("message", "Error desconocido");
                                Toast.makeText(AgregarGymActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(AgregarGymActivity.this, "Error al procesar la respuesta del servidor", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AgregarGymActivity.this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }
        );

        // Agregar la solicitud a la cola
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}