package com.example.clientsmanager.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TimePicker;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.clientsmanager.MainActivity.fragmentManager;

public class AddEditEvent extends Fragment {

    Context context;

    Fragment eventsListFrag;
    List<Event> eventsList;
    EventsAdapter eventsAdapter;
    Event currentEvent; //the vent to edit
    EditText eventTtl;
    EditText eventDesc;
    TimePicker timePicker;
    CalendarView calendarView;

    //initialization of the list
    public void setEventsList(List<Event> eventsList){
        this.eventsList = eventsList;
    }

    // for differentiating between add and edit modes
    public void setCurrentEvent(Event event){
        this.currentEvent = event;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_event_edit_add, container, false);

        context = getActivity();

        calendarView = rootView.findViewById(R.id.addEventDate);
        eventTtl = rootView.findViewById(R.id.addEventTtlId);
        eventDesc = rootView.findViewById(R.id.addEventDescNotesId);
        timePicker = rootView.findViewById(R.id.addEventTime);
        Button saveNewEvent = rootView.findViewById(R.id.saveNewEventId);
        Button cancelNewEvent = rootView.findViewById(R.id.cancelNewEventId);
        eventsAdapter = new EventsAdapter(context,eventsList);

        if(currentEvent != null){ // in edit mode
            eventTtl.setText(currentEvent.getEventTtl());
            eventTtl.setEnabled(false);
            eventDesc.setText(currentEvent.getEventDesc());
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy " +"  HH:mm");
            try {
                Date eventDate = formatter.parse(currentEvent.getEventTimeAndDate());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(eventDate);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    timePicker.setHour(calendar.get(Calendar.HOUR));
                    timePicker.setMinute(calendar.get(Calendar.MINUTE));
                }
                calendarView.setDate(calendar.getTimeInMillis());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        // in case the current event is null: creating a new event
        eventsList = new ArrayList<>();

        timePicker.setIs24HourView(true);

        final Calendar calendar = Calendar.getInstance();
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            }
        });

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
            }
        });

        cancelNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(eventsListFrag == null) {
                    eventsListFrag = new ClientEvents();
                }
                fragmentManager.popBackStack(); // transferring fragment without stacking the current one
            }
        });
        saveNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String typedTitle = eventTtl.getText().toString();

                if (typedTitle.isEmpty()) {
                    Toast.makeText(context, "A title is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                addEditNewEvent(eventTtl.getText().toString(),calendar.getTime(),eventDesc.getText().toString());
            }
        });

        return rootView;
    }

    private void addEditNewEvent(final String newTitle, Date date, final String newDesc) {

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy " +"  HH:mm");
        final String newDateString = formatter.format(date);

        final Event newEvent = new Event(newTitle,newDateString,newDesc);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // attaching an events list with its respective client
        final DatabaseReference dbRef = database.getReference(Constants.EVENTS_NODE).child(Client.getCurrentClient().getEventsListId());

        dbRef.child(newEvent.getEventTtl()).getRef().addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                View progressView = LayoutInflater.from(context).inflate(R.layout.bar_progress,null,false);

                AlertDialog.Builder progressAlert = new AlertDialog.Builder(context)
                        .setView(progressView)
                        .setCancelable(false); // so that the user can't cancel by touching the screen
                final AlertDialog loader = progressAlert.create();
                loader.show();

                Event addedEvent = dataSnapshot.getValue(Event.class);

                if (addedEvent == null) {
                    addedEvent = new Event(newTitle,newDateString,newDesc);
                    eventsList.add(addedEvent);
                    dbRef.child(addedEvent.getEventTtl()).setValue(addedEvent);
                    Snackbar.make(getActivity().findViewById(R.id.fragAddEditId), getResources().getString(R.string.eventSavedMsg), Snackbar.LENGTH_SHORT).show();
                    eventsList.add(addedEvent);
                    eventsAdapter.notifyDataSetChanged();
                }
                else{
                    addedEvent.setEventTimeAndDate(newDateString);
                    addedEvent.setEventDesc(newDesc);
                    dbRef.child(addedEvent.getEventTtl()).setValue(addedEvent);
                    Snackbar.make(getActivity().findViewById(R.id.fragAddEditId), getResources().getString(R.string.eventEdited), Snackbar.LENGTH_SHORT).show();
                    eventsAdapter.notifyDataSetChanged();
                }
                //moving to the previous fragment is done here since the actions in this method are
                //asynchronous (previously it was above and these action did not take place)
                if(eventsListFrag == null) {
                    eventsListFrag = new ClientEvents();
                }

                eventTtl.setText("");
                eventDesc.setText("");

                    Calendar calendar = Calendar.getInstance();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        timePicker.setHour(calendar.get(Calendar.HOUR));
                        timePicker.setMinute(calendar.get(Calendar.MINUTE));
                    }
                    calendarView.setDate(calendar.getTimeInMillis());

                loader.dismiss();

                fragmentManager.popBackStack(); // pops the AddEditFragment out of the stack
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, context.getResources().getString(R.string.dbConnErr), Toast.LENGTH_SHORT).show();
            }
        });
    }
}