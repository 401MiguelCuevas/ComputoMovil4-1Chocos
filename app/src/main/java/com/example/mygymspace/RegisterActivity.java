package com.example.mygymspace;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mygymspace.R;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);  // El nombre de tu layout

        // Inicialización de vistas
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        btnBack = findViewById(R.id.btn_back);

        // Acción del botón de retroceder
        btnBack.setOnClickListener(v -> finish());  // Cierra la actividad actual

        // Acción del botón de registrarse
        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validación de campos
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar que las contraseñas coincidan
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear un hilo para la solicitud HTTP
        new Thread(() -> {
            try {
                // Aquí colocamos la URL que conecta a tu servidor local
                URL url = new URL("http://192.168.1.69/register.php"); // URL de tu API PHP

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                // Escribir los parámetros de la solicitud
                String postData = "name=" + URLEncoder.encode(name, "UTF-8") +
                        "&email=" + URLEncoder.encode(email, "UTF-8") +
                        "&password=" + URLEncoder.encode(password, "UTF-8");

                connection.getOutputStream().write(postData.getBytes("UTF-8"));

                // Obtener la respuesta
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Aquí puedes manejar la respuesta del servidor, por ejemplo, mostrar un mensaje
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
                        finish();  // Cerrar la actividad y regresar
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}