package com.example.actividadsemana6;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity
        implements SensorEventListener {

    // Elementos de la interfaz
    private ImageView mImageView;
    private TextView txtSensor;

    // Variables del sensor
    private SensorManager sensorManager;
    private Sensor sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Referencias a los elementos de la interfaz
        mImageView = findViewById(R.id.mImageView);
        txtSensor = findViewById(R.id.txtSensor);

        // Ajustar pantalla
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        // Inicializar SensorManager
        sensorManager = (SensorManager)
                getSystemService(Context.SENSOR_SERVICE);

        // Obtener sensor de vector de rotación
        sensor = sensorManager.getDefaultSensor(
                Sensor.TYPE_ROTATION_VECTOR
        );

        // Comprobar si el dispositivo tiene el sensor
        if (sensor == null) {
            txtSensor.setText("El dispositivo no tiene sensor de rotación.");
        }
    }

    // ---------------------------------------------------------
    // THREAD PARA DESCARGAR LA IMAGEN
    // ---------------------------------------------------------

    public void onClick(View v) {

        new Thread(new Runnable() {

            @Override
            public void run() {

                final Bitmap bitmap = loadImageFromNetwork(
                        "https://img.magnific.com/foto-gratis/primer-plano-guacamayo-escarlata-lado-primer-plano-cabeza-guacamayo-scarlata_488145-3540.jpg?semt=ais_hybrid&w=740&q=80"
                );

                // Actualizar la interfaz desde el hilo principal
                mImageView.post(new Runnable() {

                    @Override
                    public void run() {

                        if (bitmap != null) {
                            mImageView.setImageBitmap(bitmap);
                        }

                    }
                });
            }

        }).start();
    }

    // ---------------------------------------------------------
    // MÉTODO PARA DESCARGAR LA IMAGEN
    // ---------------------------------------------------------

    private Bitmap loadImageFromNetwork(String urlString) {

        try {

            URL url = new URL(urlString);

            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream =
                    connection.getInputStream();

            Bitmap bitmap =
                    android.graphics.BitmapFactory.decodeStream(
                            inputStream
                    );

            inputStream.close();

            return bitmap;

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }

    // ---------------------------------------------------------
    // SENSOR
    // ---------------------------------------------------------

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            String texto =
                    "Sensor de rotación\n\n" +
                            "X: " + x + "\n" +
                            "Y: " + y + "\n" +
                            "Z: " + z;

            txtSensor.setText(texto);
        }
    }

    @Override
    public void onAccuracyChanged(
            Sensor sensor,
            int accuracy) {

        // No necesitamos realizar ninguna acción aquí
    }

    // ---------------------------------------------------------
    // ACTIVAR SENSOR
    // ---------------------------------------------------------

    @Override
    protected void onResume() {

        super.onResume();

        if (sensor != null) {

            sensorManager.registerListener(
                    this,
                    sensor,
                    SensorManager.SENSOR_DELAY_NORMAL
            );
        }
    }

    // ---------------------------------------------------------
    // DESACTIVAR SENSOR
    // ---------------------------------------------------------

    @Override
    protected void onPause() {

        super.onPause();

        sensorManager.unregisterListener(this);
    }
}