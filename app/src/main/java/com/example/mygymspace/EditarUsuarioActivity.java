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

        // INICIAR VISTAS
        ImageButton btnBack = findViewById(R.id.btn_back);
        etName = findViewById(R.id.et_name);
        etLastName = findViewById(R.id.et_last_name);
        etAge = findViewById(R.id.et_age);
        etNewValidity = findViewById(R.id.et_new_validity);
        btnSaveChanges = findViewById(R.id.btn_save_changes);
        btnDeleteUser = findViewById(R.id.btn_delete_user);

        // BOTON DE RETROCESO
        btnBack.setOnClickListener(v -> finish());

        // RequestQueue para Volley
        requestQueue = Volley.newRequestQueue(this);

        // OBTENER EL ID DEL USUARIO
        userId = getIntent().getIntExtra("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "ID de usuario inválido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // CARGAR DATOS DEL USUARIO
        loadUserData();

        // GUARDAR DATOS DEL USUARIO
        btnSaveChanges.setOnClickListener(v -> saveUserChanges());

        // ELIMINAR USUARIO
        btnDeleteUser.setOnClickListener(v -> deleteUser());
    }

    private void loadUserData() {
        String url = "http://192.168.1.77/obtener_usuario.php?userId=" + userId;

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

        // VALIDAR VIGENCIA ANTES DE ENVIARLA
        if (!isValidDate(validity)) {
            Toast.makeText(this, "La fecha de vigencia es inválida. Por favor, ingresa una fecha correcta.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://192.168.1.77/actualizar_usuario.php";

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
        String url = "http://192.168.1.77/eliminar_usuario.php";

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

    // VALIDACION DE LA FECHA
    private boolean isValidDate(String date) {
        // VERFICAR FORMATO: YYYY-MM-DD
        String regex = "^\\d{4}-\\d{2}-\\d{2}$";
        if (!date.matches(regex)) {
            return false;
        }

        // VERIFICAR SI LA FECHA ES REAL
        try {
            String[] parts = date.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);

            // VERIFICAR RANGO DE MES
            if (month < 1 || month > 12) {
                return false;
            }

            // VALIDAR LOS DIAS DEL MES
            if (day < 1 || day > getDaysInMonth(month, year)) {
                return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private int getDaysInMonth(int month, int year) {
        switch (month) {
            case 1: case 3: case 5: case 7: case 8: case 10: case 12:
                return 31;
            case 4: case 6: case 9: case 11:
                return 30;
            case 2:
                // VERIFICAR SI EL AÑO ES BISIESTO
                if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
                    return 29;
                } else {
                    return 28;
                }
            default:
                return 0;
        }
    }
}