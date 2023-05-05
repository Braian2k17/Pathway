package com.example.pathway;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirstFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public FirstFragment() {
        // Required empty public constructor
    }
    public static FirstFragment newInstance(String param1, String param2) {
        FirstFragment fragment = new FirstFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    FirebaseDatabase database;
    DatabaseReference Manual;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_first, container, false);

        // Implementacion DataBase
        database = FirebaseDatabase.getInstance();
        // BOTON PARA IR A ADELANTE
        FloatingActionButton adelante,atras,izquierda,derecha;
        adelante = (FloatingActionButton)root.findViewById(R.id.BotonAdelante);
        Manual = database.getReference("MovRobot");
        adelante.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Manual.setValue(1);

                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    Manual.setValue(0);
                }
                return false;
            }
        });

// BOTON PARA IR HACIA ATRAS
        atras = (FloatingActionButton)root.findViewById(R.id.BotonAtras);
        atras.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Manual.setValue(2);
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    Manual.setValue(0);
                }
                return false;
            }
        });

// BOTON PARA IR HACIA LA IZQUIERDA
        izquierda = (FloatingActionButton)root.findViewById(R.id.BotonIzquierda);
        izquierda.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Manual.setValue(3);
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    Manual.setValue(0);
                }
                return false;
            }
        });
        // BOTON PARA IR HACIA LA DERECHA
        derecha = (FloatingActionButton)root.findViewById(R.id.BotonDerecha);
        derecha.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Manual.setValue(4);
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    Manual.setValue(0);
                }
                return false;
            }
        });


        return root;
    }
}