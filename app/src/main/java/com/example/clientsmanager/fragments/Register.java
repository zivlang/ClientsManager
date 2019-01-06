package com.example.clientsmanager.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.clientsmanager.R;
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

import static com.example.clientsmanager.MainActivity.fragmentManager;

public class Register extends Fragment{

    Context context;

    EditText txtUser, txtPass, txtCon;
    Button regBtn, cancelBtn;
    List<User> usersList;

    FragmentTransaction fragTrans;
    Fragment initialFrag, clientsListFrag;

    User checkedUser;

    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_register,container,false);

        context = getActivity();

        usersList = new ArrayList<>();

        txtUser = rootView.findViewById(R.id.regUserNameId);
        txtPass = rootView.findViewById(R.id.regPassId);
        txtCon = rootView.findViewById(R.id.regConfirmId);

        regBtn = rootView.findViewById(R.id.btnRegRegId);
        cancelBtn = rootView.findViewById(R.id.btnRegCancelId);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelRegister();
            }
        });
        return rootView;
    }

    private void cancelRegister() {

        if(initialFrag == null){
            initialFrag = new Initial();
        }
        fragTrans = fragmentManager.beginTransaction();
        fragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragTrans.replace(R.id.fragsCont, initialFrag);
        fragTrans.addToBackStack(null);
        fragTrans.commit();
    }

    private void registerUser() {

        final String typedUserName = txtUser.getText().toString();
        final String typedPass = txtPass.getText().toString();
        String typedCon = txtCon.getText().toString();

        if(typedUserName.isEmpty()){
            Toast.makeText(context,"A user name is required for registration", Toast.LENGTH_SHORT).show();
            return;
        }

        if(typedPass.isEmpty()){
            Toast.makeText(context,"A password is required for registration", Toast.LENGTH_SHORT).show();
            return;
        }

        if(typedCon.isEmpty()){
            Toast.makeText(context,"A password confirmation is required for registration", Toast.LENGTH_SHORT).show();
            return;
        }

        if(typedUserName.length()< Constants.MIN_USER_NAME_LENGTH){
            Toast.makeText(context,"The user name must include at least "+Constants.MIN_USER_NAME_LENGTH+" characters",Toast.LENGTH_SHORT).show();
            return;
        }

        if(typedPass.length()< Constants.MIN_PASSWORD_LENGTH){
            Toast.makeText(context,"The password must include at least "+Constants.MIN_PASSWORD_LENGTH+" characters",Toast.LENGTH_SHORT).show();
            return;
        }

        if(typedCon.length()< Constants.MIN_PASSWORD_LENGTH){
            Toast.makeText(context,"The password's confirmation must include at least "+Constants.MIN_PASSWORD_LENGTH+" characters",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!typedPass.equals(typedCon)){
            Toast.makeText(context,"The password and its confirmation must match", Toast.LENGTH_SHORT).show();
            return;
        }

        // building the loader
        View progressView = LayoutInflater.from(context).inflate(R.layout.bar_progress,null,false);

        AlertDialog.Builder progressAlert = new AlertDialog.Builder(context)
                .setView(progressView)
                .setCancelable(false); // so that the user can't cancel by touching the screen
        final AlertDialog loader = progressAlert.create();
        loader.show();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference dbRef = database.getReference(Constants.USERS_NODE);

        dbRef.child(typedUserName).getRef().addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User checkedUser = dataSnapshot.getValue(User.class);

                if(checkedUser != null){
                    loader.dismiss();
                    Toast.makeText(context,"The user name already exists. Choose a new one", Toast.LENGTH_SHORT).show();
                    return;
                }

                checkedUser = new User(typedUserName,typedPass);
                checkedUser.setClientsListId(UUID.randomUUID().toString());


                dbRef.child(checkedUser.getUserName()).setValue(checkedUser);
                User.setCurrentUser(checkedUser); //defining the checked user as a current user
                                                  // in order to conjugate him with his clientsList
                loader.dismiss();
                Toast.makeText(context,"The registration process has completed", Toast.LENGTH_SHORT).show();

                fragTrans = fragmentManager.beginTransaction();
                fragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                clientsListFrag = new ClientsList();
                fragTrans.replace(R.id.fragsCont, clientsListFrag);
                fragTrans.commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(context,context.getResources().getString(R.string.dbConnErr), Toast.LENGTH_SHORT).show();
                loader.dismiss();
            }
        });

    }
}