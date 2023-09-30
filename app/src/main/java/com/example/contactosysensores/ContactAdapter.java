package com.example.contactosysensores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private List<Contact> contactList;
    private Context context;


    public ContactAdapter(Context context, List<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);

        Glide.with(context)
                .load(contact.getImageUrl())
                .into(holder.fotouser);

        holder.nameuser.setText(contact.getName());
        holder.genderuser.setText(contact.getGender());
        holder.cityuser.setText(contact.getCity());
        holder.countryuser.setText(contact.getCountry());
        holder.emailuser.setText(contact.getEmail());
        holder.phoneuser.setText(contact.getPhone());

        holder.deleteContactButton.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            removeContact(currentPosition);
            if (context instanceof AppActivity) {
                ((AppActivity) context).removeContactFromActiveFragment(currentPosition);
            }
        });


    }
    public void removeContact(int position) {
        contactList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        ImageView fotouser;
        TextView nameuser;
        TextView genderuser;
        TextView cityuser;
        TextView countryuser;
        TextView emailuser;
        TextView phoneuser;
        ImageButton deleteContactButton;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            fotouser = itemView.findViewById(R.id.contactImage);
            nameuser = itemView.findViewById(R.id.contactName);
            genderuser = itemView.findViewById(R.id.contactGender);
            cityuser = itemView.findViewById(R.id.contactCity);
            countryuser = itemView.findViewById(R.id.contactCountry);
            emailuser = itemView.findViewById(R.id.contactEmail);
            phoneuser = itemView.findViewById(R.id.contactPhone);
            deleteContactButton = itemView.findViewById(R.id.deleteContactButton);



        }

    }

}