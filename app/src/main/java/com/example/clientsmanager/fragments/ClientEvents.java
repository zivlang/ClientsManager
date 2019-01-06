package com.example.clientsmanager.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clientsmanager.R;
import com.example.clientsmanager.adapters.EventsAdapter;
import com.example.clientsmanager.model.Client;
import com.example.clientsmanager.model.Event;
import com.example.clientsmanager.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.clientsmanager.MainActivity.fragmentManager;

public class ClientEvents extends Fragment {

    public static final String EVENT_KEY = "event";

    Context context;

    RecyclerView rvEvents;
    FloatingActionButton btnNewEvent;
    EventsAdapter eventsAdapter;
    List<Event> eventsList;
    private FragmentTransaction ft;
    AddEditEvent addEditEventFrag; // an instance of addEditEvent,
    // so that it can inherit from addEditEvent and not only from Fragment

    TextView name, mobile, email, eventsCount;

    Client client; //in order to show the client's data

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_list_events,container,false);

        context = getActivity();
        
        client = Client.getCurrentClient();

        name = rootView.findViewById(R.id.clientFragNameId);
        mobile = rootView.findViewById(R.id.clientMobileId);
        email = rootView.findViewById(R.id.clientEmailId);
        //eventsCount = rootView.findViewById(R.id.eventsCountId);

        btnNewEvent = rootView.findViewById(R.id.eventsFabId);
        rvEvents = rootView.findViewById(R.id.eventListId);

        name.setText(client.getClientName());
        mobile.setText(client.getMobileNumber());
        email.setText(client.getEmail());

        eventsList = new ArrayList<>();

        eventsAdapter = new EventsAdapter(context,eventsList);
        rvEvents.setAdapter(eventsAdapter);
        rvEvents.setLayoutManager(new LinearLayoutManager(context));

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference(Constants.EVENTS_NODE).child(Client.getCurrentClient().getEventsListId());

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Event ev = ds.getValue(Event.class);
                    eventsList.add(ev);
                }

                eventsAdapter = new EventsAdapter(context,eventsList);
                rvEvents.setAdapter(eventsAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, context.getResources().getString(R.string.dbConnErr), Toast.LENGTH_SHORT).show();
            }
        });

        btnNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(addEditEventFrag == null) {
                    addEditEventFrag = new AddEditEvent();
                }
                //transferring the list so that it can be addressed from the addEdit fragment
                addEditEventFrag.setEventsList(eventsList);
                ft = fragmentManager.beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.replace(R.id.fragsCont, addEditEventFrag);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        return rootView;
    }



    public void setClient(Client client) {
        this.client = client;
    }


}