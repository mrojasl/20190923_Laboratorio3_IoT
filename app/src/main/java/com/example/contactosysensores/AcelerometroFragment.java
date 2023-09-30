package com.example.contactosysensores;


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



    public AcelerometroFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_acelerometro, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Aquí puedes inicializar elementos de la vista y agregar lógica relacionada con el acelerómetro
        TextView tvAccelerometerData = view.findViewById(R.id.tvAccelerometerData);
        // TODO: Actualizar tvAccelerometerData con datos reales del sensor

        recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ContactAdapter(getContext(), contactList);
        recyclerView.setAdapter(adapter);

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
}