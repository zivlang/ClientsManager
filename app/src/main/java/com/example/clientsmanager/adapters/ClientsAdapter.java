package com.example.clientsmanager.adapters;

import android.app.FragmentTransaction;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clientsmanager.R;
import com.example.clientsmanager.fragments.ClientEvents;
import com.example.clientsmanager.model.Client;
import com.example.clientsmanager.model.Event;
import com.example.clientsmanager.model.User;
import com.example.clientsmanager.utils.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import static com.example.clientsmanager.MainActivity.fragmentManager;
import static com.example.clientsmanager.utils.Constants.TAG;

public class ClientsAdapter extends RecyclerView.Adapter<ClientsAdapter.ViewHolder> {

    private Context context;
    private List<Client> clientsList;
    private FragmentTransaction fragTrans;
    private ClientEvents eventsListFrag;
    private List<Event> eventsList;

    public ClientsAdapter(Context context, List<Client> clientsList, ClientEvents eventsListFrag, FragmentTransaction fragTrans,
                          List<Event> eventsList) {

        this.context = context;
        this.clientsList = clientsList;
        this.eventsListFrag = eventsListFrag;
        this.fragTrans = fragTrans;
        this.eventsList = eventsList;
    }

    public ClientsAdapter(Context context, List<Client> clientsList) {
        this.context = context;
        this.clientsList = clientsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //getting the view that holds the cells's data
            View cell = LayoutInflater.from(context).inflate(R.layout.row_clients, parent,false);
            return new ViewHolder(cell);
            }

    //an inner class that holds information for the cells
    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView clientName; //,eventsCount;
        LinearLayout clientRow;

        public ViewHolder(View itemView) {
            super(itemView);

            clientName = itemView.findViewById(R.id.clientNameId);
            //eventsCount = itemView.findViewById(R.id.eventsCountId);
            clientRow = itemView.findViewById(R.id.clientsListRowId);

        }
    }

    // for manipulations on the page's elements to a data item - a cell in this case
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final Client currentClient = clientsList.get(position);

        holder.clientName.setText(currentClient.getClientName());

        //todo: add setText(currentClient.getEventsCount());

        holder.clientRow.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                PopupMenu clientMenu = new PopupMenu(context, holder.clientRow);
                clientMenu.inflate(R.menu.menu_client);
                clientMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            case R.id.viewClient:
                                Client.setCurrentClient(currentClient);
                                viewClient();
                                break;

                            case R.id.deleteClient:
                                deleteClient();
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

            private void deleteClient() {

                clientsList.remove(currentClient);
                notifyDataSetChanged();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference dbRef = database.getReference().child(Constants.CLIENTS_NODE).child(User.getCurrentUser().getClientsListId()).child(currentClient.getClientName());
                dbRef.removeValue();

                Toast.makeText(context, "The client was deleted", Toast.LENGTH_SHORT).show();
            }
        });

        holder.clientRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "OnClick is on");
                // attaching the clicked client with its events list by defining the current client
                Client.setCurrentClient(currentClient);

                fragTrans = fragmentManager.beginTransaction();
                fragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                eventsListFrag = new ClientEvents();
                eventsListFrag.setClient(currentClient);
                fragTrans.replace(R.id.fragsCont, eventsListFrag);
                fragTrans.addToBackStack(null);
                fragTrans.commit();
            }
        });
    }

    private void viewClient() {

        fragTrans = fragmentManager.beginTransaction();
        fragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        eventsListFrag = new ClientEvents();
        fragTrans.replace(R.id.fragsCont, eventsListFrag);
        fragTrans.addToBackStack(null);
        fragTrans.commit();

    }

    @Override
    public int getItemCount() {
        return clientsList.size();
    }
}