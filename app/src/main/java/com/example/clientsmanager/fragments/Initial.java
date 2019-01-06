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
import android.widget.Toast;

import com.example.clientsmanager.R;
import com.example.clientsmanager.model.User;
import com.example.clientsmanager.utils.Constants;
import com.example.clientsmanager.utils.SharedPrefsManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.clientsmanager.MainActivity.fragmentManager;

public class Initial extends Fragment{

    Context context;

    EditText password, userName;
    Button btnLog, btnReg;

    FragmentTransaction fragTrans;

    Fragment clientsListFrag, regFrag;
    // for saving username and password, and automatically tryLogin
    private SharedPrefsManager sp;

    User beingConnUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_initial,container,false);

        context = getActivity();
        sp = SharedPrefsManager.getInstance(context);

        userName = rootView.findViewById(R.id.logInUserNameId);
        password = rootView.findViewById(R.id.logInPassId);

        btnLog = rootView.findViewById(R.id.LogInBtnId);
        btnReg = rootView.findViewById(R.id.logInBtnRegisterId);
        // defining username and password strings in order to save them later
        String sharedUser, sharedPass;
        sharedUser = sp.loadUserName();
        sharedPass = sp.loadPassword();

        if(sharedUser != null && sharedPass != null){
            // check if they exist in firebase
            tryLogin(sharedUser, sharedPass);
        }

        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkUserForLogin();
            }
        });

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registration();
            }
        });
        return rootView;
    }

    private void registration() {

        if(regFrag == null) {
            regFrag = new Register();
        }
        fragTrans = fragmentManager.beginTransaction();//showing fragment_register
        fragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragTrans.replace(R.id.fragsCont, regFrag);
        fragTrans.addToBackStack(null);
        fragTrans.commit();
    }

    private void checkUserForLogin() {

        final String typedUser = userName.getText().toString();
        final String typedPass = password.getText().toString();

        if(typedUser.isEmpty()){
            Toast.makeText(context,"A user name is needed in order to log in", Toast.LENGTH_SHORT).show();
            return;
        }

        if(typedPass.isEmpty()){
            Toast.makeText(context,"A password is needed in order to log in", Toast.LENGTH_SHORT).show();
            return;
        }

        if(typedUser.length()< Constants.MIN_USER_NAME_LENGTH){
            Toast.makeText(context,"The user name must include al least 3 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if(typedPass.length()<Constants.MIN_PASSWORD_LENGTH){
            Toast.makeText(context,"The password must include at least 3 characters", Toast.LENGTH_SHORT).show();
            return;
        }
        tryLogin(typedUser,typedPass);
    }

    private void tryLogin(final String user, final String pass){
        // building a loader
        View progressView = LayoutInflater.from(context).inflate(R.layout.bar_progress,null,false);

        AlertDialog.Builder progressAlert = new AlertDialog.Builder(context)
                .setView(progressView)
                .setCancelable(false); // so that the user can't cancel by touching the screen
        final AlertDialog loader = progressAlert.create();
        loader.show();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference dbRef = database.getReference(Constants.USERS_NODE);

        dbRef.child(user).getRef().addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User checkedUser = dataSnapshot.getValue(User.class);

                if (checkedUser != null && checkedUser.getPassword().equals(pass)) {

                    beingConnUser = checkedUser;

                    dbRef.child(checkedUser.getUserName()).setValue(checkedUser); // adding the user

                    User.setCurrentUser(checkedUser); //defining the checked user as a
                    // current user in order to match respective clientsList list

                    fragTrans = fragmentManager.beginTransaction();
                    fragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    clientsListFrag = new ClientsList();
                    fragTrans.replace(R.id.fragsCont, clientsListFrag);
                    fragTrans.commit();

                    loader.dismiss();

                    sp.saveUserName(user);
                    sp.savePassword(pass);

                    Toast.makeText(context, "You are logged in", Toast.LENGTH_SHORT).show();
                }

                else{
                    loader.dismiss();
                    Toast.makeText(context, "The typed password, the user name, or both are incorrect", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loader.dismiss();
                Toast.makeText(context,context.getResources().getString(R.string.dbConnErr), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
