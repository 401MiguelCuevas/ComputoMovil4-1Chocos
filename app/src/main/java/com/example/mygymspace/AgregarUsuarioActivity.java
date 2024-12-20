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

        //INICIALIZAR BOTONES
        etName = findViewById(R.id.et_name);
        etLastName = findViewById(R.id.et_last_name);
        etAge = findViewById(R.id.et_age);
        etValidity = findViewById(R.id.et_validity);
        etPasswordKey = findViewById(R.id.et_password_key);
        btnSave = findViewById(R.id.btn_save);

        //ACCION DE BOTON
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

        // VALIDAR VIGENCIA
        if (!isValidDate(validity)) {
            Toast.makeText(this, "La fecha de vigencia es inválida. Por favor, ingresa una fecha correcta.", Toast.LENGTH_SHORT).show();
            return;
        }

        // ENVIAR DATOS
        enviarDatosAlServidor(name, lastName, age, validity, passwordKey);
    }

    private boolean isValidDate(String date) {
        // VERIFICAR QUE LA FECHA ESTE EN EL FORMATO: YYYY-MM-DD
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

            // VALIDAR LOS DIAS DEL MES (CONSIDERANDO AÑOS BISIESTOS)
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
                // VALIDAR SI ES AÑO BISIESTO
                if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
                    return 29;
                } else {
                    return 28;
                }
            default:
                return 0;
        }
    }

    private void enviarDatosAlServidor(String nombre, String apellido, String edad, String vigencia, String palabraClave) {
        new Thread(() -> {
            try {
                // URL Y CONEXION
                URL url = new URL("http://192.168.1.69/agregarusuario.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                // DATOS A ENVIAR
                String postData = "nombre=" + nombre + "&apellido=" + apellido + "&edad=" + edad
                        + "&vigencia=" + vigencia + "&palabra_clave=" + palabraClave;

                // ENVIAR DATOS
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(postData);
                writer.flush();
                writer.close();

                // RESPUESTA DEL SERVIDOR
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

                    // PROCESAR JSON
                    org.json.JSONObject jsonResponse = new org.json.JSONObject(response.toString());
                    String status = jsonResponse.getString("status");
                    String message = jsonResponse.getString("message");

                    runOnUiThread(() -> {
                        if ("success".equals(status)) {
                            Toast.makeText(this, "Usuario guardado exitosamente", Toast.LENGTH_SHORT).show();
                            finish(); // CERRAR AL GUARDAR
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