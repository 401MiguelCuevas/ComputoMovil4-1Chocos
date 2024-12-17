package com.example.mygymspace;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mygymspace.R;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class AgregarUsuarioActivity extends AppCompatActivity {

    private EditText etName, etLastName, etAge, etValidity, etPasswordKey;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_usuario);

        // Vinculación de vistas
        etName = findViewById(R.id.et_name);
        etLastName = findViewById(R.id.et_last_name);
        etAge = findViewById(R.id.et_age);
        etValidity = findViewById(R.id.et_validity);
        etPasswordKey = findViewById(R.id.et_password_key);
        btnSave = findViewById(R.id.btn_save);

        // Acción del botón
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarUsuario();
            }
        });
    }

    private void guardarUsuario() {
        String name = etName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String validity = etValidity.getText().toString().trim();
        String passwordKey = etPasswordKey.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(age)
                || TextUtils.isEmpty(validity) || TextUtils.isEmpty(passwordKey)) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        enviarDatosAlServidor(name, lastName, age, validity, passwordKey);
    }

    private void enviarDatosAlServidor(String nombre, String apellido, String edad, String vigencia, String palabraClave) {
        new Thread(() -> {
            try {
                // URL del servidor
                URL url = new URL("http://192.168.157.97/agregarusuario.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                // Configuración de la conexión
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                // Datos a enviar (en español)
                String postData = "nombre=" + nombre + "&apellido=" + apellido + "&edad=" + edad
                        + "&vigencia=" + vigencia + "&palabra_clave=" + palabraClave;

                // Enviar datos
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(postData);
                writer.flush();
                writer.close();

                // Leer respuesta del servidor
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    java.io.BufferedReader reader = new java.io.BufferedReader(
                            new java.io.InputStreamReader(conn.getInputStream())
                    );
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Procesar respuesta JSON
                    org.json.JSONObject jsonResponse = new org.json.JSONObject(response.toString());
                    String status = jsonResponse.getString("status");
                    String message = jsonResponse.getString("message");

                    runOnUiThread(() -> {
                        if ("success".equals(status)) {
                            Toast.makeText(this, "Usuario guardado exitosamente", Toast.LENGTH_SHORT).show();
                            finish(); // Opcional: cerrar la actividad tras guardar
                        } else {
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Error al guardar usuario", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}