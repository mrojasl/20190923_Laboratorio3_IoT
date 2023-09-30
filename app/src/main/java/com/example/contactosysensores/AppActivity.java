package com.example.contactosysensores;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

        loadMagnetometerFragment();

        btnSwitchSensor.setOnClickListener(v -> {
            if (tvSensorTitle.getText().equals("Magnetómetro")) {
                loadAccelerometerFragment();
            } else {
                loadMagnetometerFragment();
            }
        });


        btnAddContact.setOnClickListener(v -> {

            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);


            btnAddContact.setEnabled(false);
            btnSwitchSensor.setEnabled(false);

            btnAddContact.setAlpha(0.9f);
            btnSwitchSensor.setAlpha(0.9f);

            fetchRandomContact();



        });




        ImageView eyeIcon = findViewById(R.id.ic_ojo);
        eyeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFragmentDescriptionDialog();
            }
        });

    }



    private void loadMagnetometerFragment() {
        MagnetometroFragment fragment = new MagnetometroFragment();
        fragment.setContactList(magnetometroContactList);

        tvSensorTitle.setText("Magnetómetro");
        btnSwitchSensor.setText("Ir a Acelerómetro");

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    private void loadAccelerometerFragment() {
        AcelerometroFragment fragment = new AcelerometroFragment();
        fragment.setContactList(acelerometroContactList);

        tvSensorTitle.setText("Acelerómetro");
        btnSwitchSensor.setText("Ir a Magnetómetro");

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
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

                    contact.setName(name);
                    contact.setGender(gender);
                    contact.setCity(city);
                    contact.setCountry(country);
                    contact.setEmail(email);
                    contact.setPhone(phone);
                    contact.setImageUrl(imageUrl);


                    runOnUiThread(() -> {


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


                        ProgressBar progressBar = findViewById(R.id.progressBar);
                        progressBar.setVisibility(View.GONE);



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


    public void removeContactFromActiveFragment(int position) {
        Fragment activeFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (activeFragment instanceof AcelerometroFragment) {
            acelerometroContactList.remove(position);
        } else if (activeFragment instanceof MagnetometroFragment) {
            magnetometroContactList.remove(position);
        }
    }


    private void showFragmentDescriptionDialog() {
        String title;
        String message;

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (currentFragment instanceof MagnetometroFragment) {
            title = "Detalles - Magnetómetro";
            message = "Haga CLICK en 'Añadir' para agregar contactos a su lista. Esta aplicación está utilizando el ACELERÓMETRO de su dispositivo.\n\n" +
                    "De este forma, la lista hará scroll hacia abajo, cuando agite su dispositivo.";
        } else if (currentFragment instanceof AcelerometroFragment) {
            title = "Detalles - Acelerómetro";
            message = "Haga CLICK en 'Añadir' para agregar contactos a su lista. Esta aplicación está utilizando el MAGNETÓMETRO de su dispositivo.\n\n" +
                    "De esta forma, la lsita se mostrará al 100% cuando se apunte al NORTE. Caso contrario, se desvanecerá...";
        } else {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("Aceptar", null)
                .show();
    }

}