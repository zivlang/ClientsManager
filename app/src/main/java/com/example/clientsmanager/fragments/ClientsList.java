package com.example.clientsmanager.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clientsmanager.R;
import com.example.clientsmanager.adapters.ClientsAdapter;
import com.example.clientsmanager.model.Client;
import com.example.clientsmanager.model.User;
import com.example.clientsmanager.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientsList extends Fragment {

    public static final String NAME_KEY = "name";

    Context context;

    TextView listTtl;
    RecyclerView rvClients;
    FloatingActionButton btnNewClient;
    ClientsAdapter clientsAdapter;
    List<Client> clientsList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_list_clients, container, false);

        context = getActivity();

        listTtl = rootView.findViewById(R.id.clientsListTtlId);
        btnNewClient = rootView.findViewById(R.id.clientsFabId);

        rvClients = rootView.findViewById(R.id.clientsRVId);
        clientsList = new ArrayList<>();
        clientsAdapter = new ClientsAdapter(context, clientsList);
        rvClients.setAdapter(clientsAdapter);

        // setting a layout manager
        rvClients.setLayoutManager(new LinearLayoutManager(context)); // a regular one

        // Connecting to FireBase, and getting the clients list that's saved there
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference(Constants.CLIENTS_NODE).child(User.getCurrentUser().getClientsListId());

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Client client = ds.getValue(Client.class);
                    clientsList.add(client);
                }

                clientsAdapter = new ClientsAdapter(context, clientsList);
                rvClients.setAdapter(clientsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, context.getResources().getString(R.string.dbConnErr), Toast.LENGTH_SHORT).show();
            }
        });

        //adding a new client
        btnNewClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeNewClientAlert();
            }
        });

        return rootView;
    }

    private void makeNewClientAlert() {

        View inflatedView = LayoutInflater.from(context).inflate(R.layout.client_add, null, false);

        Button saveNewClient = inflatedView.findViewById(R.id.saveNewClientId);
        Button cancelNewClient = inflatedView.findViewById(R.id.cancelNewClientId);
        final EditText newName = inflatedView.findViewById(R.id.newFullName);
        final EditText newMobile = inflatedView.findViewById(R.id.newMobileId);
        final EditText newEmail = inflatedView.findViewById(R.id.newEmailId);

        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setView(inflatedView);

        final AlertDialog newClientAlert = alert.create();

        cancelNewClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newClientAlert.dismiss();
            }
        });

        saveNewClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String typedName = newName.getText().toString();

                if(typedName.isEmpty()){
                    Toast.makeText(context,"A client name is required. The client was not added",Toast.LENGTH_SHORT).show();
                    return;
                }

                addNewClient(newName.getText().toString(),newMobile.getText().toString(),newEmail.getText().toString());
                newClientAlert.dismiss();
            }

        });

        cancelNewClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newClientAlert.dismiss();
            }
        });

        newClientAlert.show();
    }

    private void addNewClient(final String newName, final String newMobile, final String newEmail) {

        final Client newClient = new Client(newName,newMobile,newEmail);

        View progressView = LayoutInflater.from(context).inflate(R.layout.bar_progress,null,false);

        AlertDialog.Builder progressAlert = new AlertDialog.Builder(context)
                .setView(progressView)
                .setCancelable(false); // so that the user can't cancel by touching the screen
        final AlertDialog loader = progressAlert.create();
        loader.show();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // attaching a clientsList to its respective user
        final DatabaseReference dbRef = database.getReference(Constants.CLIENTS_NODE).child(User.getCurrentUser().getClientsListId());

        dbRef.child(newClient.getClientName()).getRef().addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Client addedClient = dataSnapshot.getValue(Client.class);

                if (addedClient == null) {

                    addedClient = new Client(newName,newMobile,newEmail);
                    //setting an events list id for the client
                    addedClient.setEventsListId(UUID.randomUUID().toString());

                    dbRef.child(addedClient.getClientName()).setValue(addedClient);

                    clientsList.add(addedClient);
                    clientsAdapter.notifyDataSetChanged();

                    loader.dismiss();

                    Snackbar.make(getActivity().findViewById(R.id.clientsFragId), getResources().getString(R.string.clientSavedMsg), Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, context.getResources().getString(R.string.dbConnErr), Toast.LENGTH_SHORT).show();
            }
        });
    }
}