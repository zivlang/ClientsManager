package com.example.clientsmanager.adapters;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clientsmanager.R;
import com.example.clientsmanager.fragments.AddEditEvent;
import com.example.clientsmanager.model.Client;
import com.example.clientsmanager.model.Event;
import com.example.clientsmanager.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import static com.example.clientsmanager.MainActivity.fragmentManager;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private AddEditEvent addEditEventFrag; // an instance of addEditEvent,
    // so that it can inherit from addEditEvent and not only from Fragment
    private Context context;
    private List<Event> eventsList;
    private FragmentTransaction fragTrans;
    private Event event;

    public EventsAdapter(Context context, List<Event> eventsList, FragmentTransaction fragTrans) {
        this.fragTrans = fragTrans;
        this.context = context;
        this.eventsList = eventsList;
    }

    public EventsAdapter(Context context, List<Event> eventsList) {
        this.context = context;
        this.eventsList = eventsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //getting the cells' view
        if(!eventsList.isEmpty()) {
            View cell = LayoutInflater.from(context).inflate(R.layout.row_events,parent,false);
            return new ViewHolder(cell);
        }
        return null;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView eventTtl, eventTimeAndDate;
        LinearLayout eventRow;

        public ViewHolder(View itemView) {
            super(itemView);
            eventTtl = itemView.findViewById(R.id.eventRowTtlId);

            eventTimeAndDate = itemView.findViewById(R.id.eventRowTimeDateId);
            eventRow = itemView.findViewById(R.id.eventsListRowId);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final EventsAdapter.ViewHolder holder, final int position) {

        //creating a pop up menu for viewing and deleting events
        final Event currentEvent = eventsList.get(position);

        holder.eventTtl.setText(currentEvent.getEventTtl());
        holder.eventTimeAndDate.setText(currentEvent.getEventTimeAndDate());
        holder.eventRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                event = currentEvent; // initialization. otherwise event is null
                viewEvent();
            }
        });

        holder.eventRow.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                PopupMenu clientMenu = new PopupMenu(context, holder.eventRow);
                clientMenu.inflate(R.menu.menu_event);
                clientMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            case R.id.menuViewEventId:
                                event = currentEvent;
                                viewEvent();
                                break;

                            case R.id.menuEditEventId:
                                goToAddEditEvent();
                                break;

                            case R.id.menuDeleteEventId:
                                deleteEvent();
                                break;

                            default:
                                break;
                        }
                        return false;
                    }
                });
                clientMenu.show();
                return true;
            }

        private void deleteEvent() {

            eventsList.remove(currentEvent);
            notifyDataSetChanged();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference dbRef = database.getReference()
                    .child(Constants.EVENTS_NODE).child(Client.getCurrentClient()
                            .getEventsListId()).child(currentEvent.getEventTtl());
            dbRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(context, "The event was deleted", Toast.LENGTH_SHORT).show();
                }
            });


            }
        });
    }

    private void goToAddEditEvent() {

        if(addEditEventFrag == null) {
            addEditEventFrag = new AddEditEvent();
        }
        //transferring the list so that it can be addressed from the addEdit fragment
        addEditEventFrag.setEventsList(eventsList);
        addEditEventFrag.setCurrentEvent(event); // edit mode
        fragTrans = fragmentManager.beginTransaction();
        fragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragTrans.replace(R.id.fragsCont, addEditEventFrag);
        fragTrans.addToBackStack(null);
        fragTrans.commit();
    }

    private void viewEvent() {

        View inflatedView = LayoutInflater.from(context).inflate(R.layout.details_event, null, false);

        TextView eventTtl = inflatedView.findViewById(R.id.eventTtlId);
        TextView eventTimeAndDate = inflatedView.findViewById(R.id.eventTimeDateId);
        TextView eventDesc = inflatedView.findViewById(R.id.eventDescId);
        Button editEvent = inflatedView.findViewById(R.id.editEventBtnId);
        Button exitEvent = inflatedView.findViewById(R.id.exitDetailsBtnId);

        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setView(inflatedView);
        final AlertDialog eventDetailsAlert = alert.create();

        eventTtl.setText(event.getEventTtl());
        eventDesc.setText(event.getEventDesc());
        eventTimeAndDate.setText(event.getEventTimeAndDate());

        editEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                eventDetailsAlert.dismiss();

                goToAddEditEvent();
            }
        });

        exitEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                eventDetailsAlert.dismiss();
            }
        });

        eventDetailsAlert.show();
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }
}