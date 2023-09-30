package com.example.contactosysensores;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class AppActivity extends AppCompatActivity {
    private TextView tvSensorTitle;
    private Button btnSwitchSensor;
    private List<Contact> magnetometroContactList = new ArrayList<>();
    private List<Contact> acelerometroContactList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);



        tvSensorTitle = findViewById(R.id.tvSensorTitle);
        btnSwitchSensor = findViewById(R.id.btnSwitchSensor);
        Button btnAddContact = findViewById(R.id.btnAddContact);

        // Cargar fragmento inicial
        loadMagnetometerFragment();

        btnSwitchSensor.setOnClickListener(v -> {
            if (tvSensorTitle.getText().equals("Magnetómetro")) {
                loadAccelerometerFragment();
            } else {
                loadMagnetometerFragment();
            }
        });

        // TODO: Lógica para el botón "Añadir Contactos"

        btnAddContact.setOnClickListener(v -> {
            // Deshabilita los botones
            btnAddContact.setEnabled(false);
            btnSwitchSensor.setEnabled(false);

            // Cambia la opacidad de los botones a 0.5 para indicar que están deshabilitados
            btnAddContact.setAlpha(0.9f);
            btnSwitchSensor.setAlpha(0.9f);

            // Ahora inicia la solicitud para agregar un contacto
            fetchRandomContact();




        });


    }



    private void loadMagnetometerFragment() {
        MagnetometroFragment fragment = new MagnetometroFragment();
        fragment.setContactList(magnetometroContactList);

        // Cambiar el título y el texto del botón
        tvSensorTitle.setText("Magnetómetro");
        btnSwitchSensor.setText("Ir a Acelerómetro");

        // Cargar el fragmento del magnetómetro
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)  // Nota que estamos usando la instancia 'fragment' aquí
                .commit();
    }

    private void loadAccelerometerFragment() {
        AcelerometroFragment fragment = new AcelerometroFragment();
        fragment.setContactList(acelerometroContactList);

        // Cambiar el título y el texto del botón
        tvSensorTitle.setText("Acelerómetro");
        btnSwitchSensor.setText("Ir a Magnetómetro");

        // Cargar el fragmento del acelerómetro
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)  // Nota que estamos usando la instancia 'fragment' aquí
                .commit();
    }






    private void fetchRandomContact() {
        new Thread(() -> {
            try {
                URL url = new URL("https://randomuser.me/api/");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONArray resultsArray = jsonObject.getJSONArray("results");
                    JSONObject contactJson = resultsArray.getJSONObject(0);

                    // Parse the contact details
                    JSONObject nameObj = contactJson.getJSONObject("name");
                    String name = nameObj.getString("title") + " " + nameObj.getString("first") + " " + nameObj.getString("last");
                    String gender = contactJson.getString("gender");
                    JSONObject locationObj = contactJson.getJSONObject("location");
                    String city = locationObj.getString("city");
                    String country = locationObj.getString("country");
                    String email = contactJson.getString("email");
                    String phone = contactJson.getString("phone");
                    String imageUrl = contactJson.getJSONObject("picture").getString("large");

                    Contact contact = new Contact(name, gender, city, country, email, phone, imageUrl);

                // Set data to the contact object
                    contact.setName(name);
                    contact.setGender(gender);
                    contact.setCity(city);
                    contact.setCountry(country);
                    contact.setEmail(email);
                    contact.setPhone(phone);
                    contact.setImageUrl(imageUrl);




                    // Update the UI on the main thread
                    runOnUiThread(() -> {
                        // Here we'll add the contact to the active fragment's list and notify the adapter


                        Fragment activeFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

                        if (activeFragment instanceof AcelerometroFragment) {
                            acelerometroContactList.add(contact);
                            AcelerometroFragment acelerometroFragment = (AcelerometroFragment) activeFragment;
                            acelerometroFragment.updateContactList(acelerometroContactList);
                        } else if (activeFragment instanceof MagnetometroFragment) {
                            magnetometroContactList.add(contact);
                            MagnetometroFragment magnetometroFragment = (MagnetometroFragment) activeFragment;
                            magnetometroFragment.updateContactList(magnetometroContactList);
                        }

                        Button btnAddContact = findViewById(R.id.btnAddContact);

                        btnAddContact.setEnabled(true);
                        btnSwitchSensor.setEnabled(true);

                        btnAddContact.setAlpha(1.0f);
                        btnSwitchSensor.setAlpha(1.0f);


                    });


                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(AppActivity.this, "Error al agregar contacto", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(AppActivity.this, "Ocurrió un error", Toast.LENGTH_SHORT).show();
                });
            }

        }).start();
    }


}