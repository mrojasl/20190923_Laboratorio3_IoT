package com.example.contactosysensores;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;



public class MagnetometroFragment extends Fragment {
    private List<Contact> contactList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ContactAdapter adapter;
    private SensorManager sensorManager;
    private Sensor magnetometer;



    public MagnetometroFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_magnetometro, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvMagnetometerData = view.findViewById(R.id.tvMagnetometerData);

        recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ContactAdapter(getContext(), contactList);
        recyclerView.setAdapter(adapter);

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
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


    private final SensorEventListener magnetometerListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            updateVisibilityBasedOnAngle(x, y);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(magnetometerListener, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(magnetometerListener);
        }
    }


    private void updateVisibilityBasedOnAngle(float x, float y) {
        double angle = Math.toDegrees(Math.atan2(y, x));
        double adjustedAngle = angle - 90;

        if (adjustedAngle < 0) {
            adjustedAngle += 360;
        }

        float visibilityPercentage;
        if (adjustedAngle < 0.05 * 180) {
            visibilityPercentage = 1f;
        } else if (adjustedAngle < 0.25 * 180) {
            visibilityPercentage = 0.75f;
        } else if (adjustedAngle < 0.5 * 180) {
            visibilityPercentage = 0.5f;
        } else if (adjustedAngle < 0.75 * 180) {
            visibilityPercentage = 0.25f;
        } else {
            visibilityPercentage = 0f;
        }

        recyclerView.setAlpha(visibilityPercentage);
    }
}