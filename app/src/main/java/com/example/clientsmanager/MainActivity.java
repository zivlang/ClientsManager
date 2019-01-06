package com.example.clientsmanager;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.clientsmanager.fragments.Initial;

public class MainActivity extends AppCompatActivity {

    Context context;

    public static FragmentManager fragmentManager;
    public static FragmentTransaction fragTrans;

    Fragment initial_frag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setPointer();
    }

    private void setPointer() {

        context = this;

        fragmentManager = getFragmentManager();
        fragTrans = fragmentManager.beginTransaction();

        initial_frag = new Initial();
        fragTrans.add(R.id.fragsCont,initial_frag);
        fragTrans.commit();//saving
        //showing the initial fragment
        fragTrans = fragmentManager.beginTransaction();
        fragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragTrans.replace(R.id.fragsCont,initial_frag);
        fragTrans.commit();
    }
}
