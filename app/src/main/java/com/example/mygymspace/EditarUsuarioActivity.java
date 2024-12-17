package com.example.mygymspace;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditarUsuarioActivity extends AppCompatActivity {

    private EditText etName, etLastName, etAge, etNewValidity;
    private Button btnSaveChanges, btnDeleteUser;
    private int userId; // ID del usuario recibido desde la anterior Activity
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_usuario);

        // Inicializar vistas
        ImageButton btnBack = findViewById(R.id.btn_back);
        etName = findViewById(R.id.et_name);
        etLastName = findViewById(R.id.et_last_name);
        etAge = findViewById(R.id.et_age);
        etNewValidity = findViewById(R.id.et_new_validity);
        btnSaveChanges = findViewById(R.id.btn_save_changes);
        btnDeleteUser = findViewById(R.id.btn_delete_user);

        // Configurar el botón de retroceso
        btnBack.setOnClickListener(v -> finish());

        // Inicializar RequestQueue para Volley
        requestQueue = Volley.newRequestQueue(this);

        // Obtener el ID del usuario de la Intent
        userId = getIntent().getIntExtra("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "ID de usuario inválido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Cargar datos del usuario
        loadUserData();

        // Guardar cambios al usuario
        btnSaveChanges.setOnClickListener(v -> saveUserChanges());

        // Eliminar usuario
        btnDeleteUser.setOnClickListener(v -> deleteUser());
    }

    private void loadUserData() {
        String url = "http://192.168.157.97/obtener_usuario.php?userId=" + userId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("error")) {
                            Toast.makeText(this, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            etName.setText(jsonObject.getString("nombre"));
                            etLastName.setText(jsonObject.getString("apellido"));
                            etAge.setText(jsonObject.getString("edad"));
                            etNewValidity.setText(jsonObject.getString("vigencia"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error al procesar datos del usuario", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error al cargar datos del usuario", Toast.LENGTH_SHORT).show());

        requestQueue.add(request);
    }

    private void saveUserChanges() {
        String name = etName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String validity = etNewValidity.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(age) || TextUtils.isEmpty(validity)) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://192.168.157.97/actualizar_usuario.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("success")) {
                            Toast.makeText(this, jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                            // Redirigir a GymActivity
                            Intent intent = new Intent(EditarUsuarioActivity.this, GymActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (jsonObject.has("error")) {
                            Toast.makeText(this, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(userId));
                params.put("nombre", name);
                params.put("apellido", lastName);
                params.put("edad", age);
                params.put("vigencia", validity);
                return params;
            }
        };

        requestQueue.add(request);
    }

    private void deleteUser() {
        String url = "http://192.168.157.97/eliminar_usuario.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("success")) {
                            Toast.makeText(this, jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                            // Redirigir a GymActivity
                            Intent intent = new Intent(EditarUsuarioActivity.this, GymActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (jsonObject.has("error")) {
                            Toast.makeText(this, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(userId));
                return params;
            }
        };

        requestQueue.add(request);
    }
}