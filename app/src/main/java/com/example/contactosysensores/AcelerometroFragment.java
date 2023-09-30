package com.example.contactosysensores;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AcelerometroFragment extends Fragment {
    private ContactAdapter adapter;
    private RecyclerView recyclerView;
    private List<Contact> contactList = new ArrayList<>();
    private SensorManager sensorManager;
    private Sensor accelerometer;



    public AcelerometroFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_acelerometro, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ContactAdapter(getContext(), contactList);
        recyclerView.setAdapter(adapter);


        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void addContact(Contact contact) {
        if (contactList != null && adapter != null) {
            contactList.add(contact);
            adapter.notifyDataSetChanged();
        }
    }


    public void updateContactList(List<Contact> contacts) {
        this.contactList.clear();
        this.contactList.addAll(contacts);
        adapter.notifyDataSetChanged();
    }
    public void setContactList(List<Contact> contacts) {
        if (this.contactList != null) {
            this.contactList.clear();
            this.contactList.addAll(contacts);
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }
    public void removeContact(int position) {
        contactList.remove(position);
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(accelerometerListener);
        }
    }



    private static final float UMBRAL = 15f;

    private final SensorEventListener accelerometerListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float ax = event.values[0];
            float ay = event.values[1];
            float az = event.values[2];

            double totalAcceleration = Math.sqrt(ax * ax + ay * ay + az * az);
            if (totalAcceleration > UMBRAL) {
                Toast.makeText(getContext(), String.format("Su aceleración: %.2f m/s^2", totalAcceleration), Toast.LENGTH_SHORT).show();
                autoScrollContacts();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };


    private void autoScrollContacts() {
        if (recyclerView != null) {
            recyclerView.smoothScrollBy(0, 1000);
        }
    }
}