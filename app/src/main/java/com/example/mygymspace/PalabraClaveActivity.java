package com.example.mygymspace;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class PalabraClaveActivity extends AppCompatActivity {

    private EditText etPalabraClave;
    private Button btnValidar;
    private ConstraintLayout layoutContainer;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palabra_clave);

        // INICIALIZAR VISTAS
        ImageButton btnBack = findViewById(R.id.btn_back);
        etPalabraClave = findViewById(R.id.et_keyword);
        btnValidar = findViewById(R.id.btn_validate);
        layoutContainer = findViewById(R.id.cl_main_container);

        // BOTON DE RETROCESO
        btnBack.setOnClickListener(v -> finish());

        // RequestQueue PARA VOLLEY
        requestQueue = Volley.newRequestQueue(this);

        // BOTON DE VALIDAR
        btnValidar.setOnClickListener(v -> validarVigencia());
    }

    private void validarVigencia() {
        String palabraClave = etPalabraClave.getText().toString().trim();

        if (TextUtils.isEmpty(palabraClave)) {
            Toast.makeText(this, "Por favor, ingresa la palabra clave", Toast.LENGTH_SHORT).show();
            return;
        }

        // URL DEL PHP
        String url = "http://192.168.1.77/consultar_vigencia.php?palabra_clave=" + palabraClave;

        // PETICION AL SERVIDOR
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                String estado = response.getString("estado");

                                // CAMBIAR EL FONDO SEGUN EL ESTADO
                                switch (estado) {
                                    case "rojo":
                                        layoutContainer.setBackgroundColor(Color.RED);
                                        break;
                                    case "amarillo":
                                        layoutContainer.setBackgroundColor(Color.YELLOW);
                                        break;
                                    case "verde":
                                        layoutContainer.setBackgroundColor(Color.GREEN);
                                        break;
                                    default:
                                        layoutContainer.setBackgroundColor(Color.WHITE);
                                        Toast.makeText(PalabraClaveActivity.this, "Estado desconocido", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                String message = response.getString("message");
                                Toast.makeText(PalabraClaveActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(PalabraClaveActivity.this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PalabraClaveActivity.this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                    }
                });

        // AGREGAR SOLICITUD A LA COLA
        requestQueue.add(request);
    }
}