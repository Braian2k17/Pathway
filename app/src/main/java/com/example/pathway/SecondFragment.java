package com.example.pathway;


import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import Proyecto.Conduccion;
import Proyecto.Points;


public class SecondFragment extends Fragment implements OnMapReadyCallback {

    static ArrayList<Points> ruta = new ArrayList<Points>();
    static int Correccion = 180;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ArrayList<Marker>  markers = new ArrayList<>();
    private boolean locationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private String mParam1;
    private String mParam2;

    public SecondFragment() {
        // Required empty public constructor
    }

    public static SecondFragment newInstance(String param1, String param2) {
        SecondFragment fragment = new SecondFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    float Latitud, Longitud;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference Automatico, Comprobar, Distancia, TotalUbicaciones, Angulo, BorrarGuardados, otro;
    double[] Angulos = new double[15];
    double[] Distancias = new double[15];
    ArrayList<String> NombresRutas = new ArrayList<String>();
    GoogleMap mGoogleMap;
    MapView mMapView;
    Marker markername;
    int i = 0;


    PolygonOptions arraypoligonos =  new PolygonOptions();
    MarkerOptions markerOptions = new MarkerOptions();


    View root;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Conduccion Cortadora = new Conduccion();
        root = inflater.inflate(R.layout.fragment_second, container, false);
        Button boton = (Button) root.findViewById(R.id.Inicio);
        FloatingActionButton boton_guardar = (FloatingActionButton)root.findViewById(R.id.guardados);
        mMapView = (MapView) root.findViewById(R.id.google_map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Points Origen = new Points(Latitud,Longitud);
                recorrerRuta(Origen,Cortadora);
                dibujarruta(mGoogleMap);
                mostrarAlerta(Gravity.CENTER);
            }
        });

        boton_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertaGuardados(Gravity.TOP);
            }
        });
        return root;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        Automatico = FirebaseDatabase.getInstance().getReference();
        Automatico.child("PosicionCortadora").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Latitud = snapshot.child("Latitud").getValue(float.class);
                Longitud = snapshot.child("Longitud").getValue(float.class);
                LatLng CortadoraUbicacionRT = new LatLng(Latitud,Longitud);
                if (markername!=null){
                    markername.remove();
                }
                markername = googleMap.addMarker(new MarkerOptions().position(CortadoraUbicacionRT).title("Cortadora").icon(BitmapDescriptorFactory.fromResource(R.mipmap.cortadora_iconoc)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                markerOptions.position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.flag)).anchor(0.8f,1f).draggable(true);
                trayectoria(markerOptions,latLng,1);
                ////////////////////////////////////////////////
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 30));
                googleMap.addMarker(markerOptions);
            }
        });

        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(@NonNull Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(@NonNull Marker marker) {
                LatLng newposition = marker.getPosition();
                trayectoria(markerOptions,newposition,0);
            }

            @Override
            public void onMarkerDragStart(@NonNull Marker marker) {

            }
        });

        if (ContextCompat.checkSelfPermission(this.getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    public void recorrerRuta(Points Origen, Conduccion conduccion){
        database = FirebaseDatabase.getInstance();
        Angulo = database.getReference("ModoAutomatico/Angulo/" + 0);
        Angulos[0] = (conduccion.CalcularAngulo(Origen,ruta.get(0)) + Correccion);
        Angulo.setValue(Angulos[0]);
        Distancia = database.getReference("ModoAutomatico/Distancia/" + 0);
        Distancias[0] = conduccion.CalcularDistancia(Origen,ruta.get(0));
        Distancia.setValue(Distancias[0]);
        for (int k = 0; k < ruta.size() - 1; k++){
            Angulo = database.getReference("ModoAutomatico/Angulo/" + (k+1));
            Angulos[k+1] = conduccion.CalcularAngulo(ruta.get(k),ruta.get(k+1)) + Correccion - Angulos[k];
            Angulo.setValue(Angulos[k+1]);
            Distancia = database.getReference("ModoAutomatico/Distancia/" + (k+1));
            Distancias[k+1]=conduccion.CalcularDistancia(ruta.get(k),ruta.get(k+1));
            Distancia.setValue(Distancias[k+1]);
        }
        TotalUbicaciones = database.getReference("ModoAutomatico/TotalPuntos/");
        TotalUbicaciones.setValue(ruta.size());
    }

    public void dibujarruta(@NonNull GoogleMap googleMap){
            arraypoligonos.strokeColor(Color.BLACK);
            arraypoligonos.strokeWidth(5);
            arraypoligonos.fillColor(Color.rgb(7,74,68));
            Polygon poligono = googleMap.addPolygon(arraypoligonos);
    }

    public void trayectoria(MarkerOptions markerOptions,LatLng latLng,int var){
        i = i + var;
        double Latitud = latLng.latitude;
        double Longitud = latLng.longitude;
        Automatico = database.getReference("Objetivo" + i + "/Latitud");
        Automatico.setValue(Latitud);
        Automatico = database.getReference("Objetivo" + i +"/Longitud");
        Automatico.setValue(Longitud);
        markerOptions.title("Destino: " + i);
        Points destino = new Points(Latitud, Longitud);
        if(var == 1) {
            ruta.add(destino);
            arraypoligonos.add(new LatLng(Latitud, Longitud));
        } else if (var == 0) {
            int lastIndex = ruta.size() - 1;
            ruta.set(lastIndex,destino);
            arraypoligonos.getPoints().set(lastIndex, new LatLng(Latitud,Longitud));
        }
    }

    private void mostrarAlerta(int gravity){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alerta_guardar);
        Window window = dialog.getWindow();
        if(window == null){
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);
        dialog.show();
        Button b_aceptar = dialog.findViewById(R.id.boton_aceptar);
        Button b_denegar = dialog.findViewById(R.id.boton_denegar);
        EditText tag_ruta = dialog.findViewById(R.id.nombre_ruta);

        b_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombre_ruta = tag_ruta.getText().toString();
                GuardarRutas(nombre_ruta);
                dialog.dismiss();
            }
        });

        b_denegar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void AlertaGuardados(int gravity){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.saved_way);
        Window window = dialog.getWindow();
        if(window == null){
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);
        dialog.show();
        ComprobarGuardados(dialog);
    }

    private void GuardarRutas(String nombre){
        for(int i = 0; i < ruta.size(); i++){
            Angulo = database.getReference("RutasGuardadas/" + nombre + "/Angulo/"+i);
            Angulo.setValue(Angulos[i]);
            Distancia = database.getReference("RutasGuardadas/" + nombre + "/Distancia/" + i);
            Distancia.setValue(Distancias[i]);
        }
    }

    private void ComprobarGuardados(Dialog dialog){
        Comprobar = FirebaseDatabase.getInstance().getReference();
        Comprobar.child("RutasGuardadas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String name = snapshot.getKey();
                        NombresRutas.add(name);
                    }
                    Set<String> setSinDuplicados = new LinkedHashSet<String>(NombresRutas);
                    NombresRutas.clear();
                    List<String> listaSinDuplicados = new ArrayList<String>(setSinDuplicados);
                    TextView[] textViews = new TextView[5];
                    FloatingActionButton[] buttons = new FloatingActionButton[5];
                    FloatingActionButton[] buttons_e = new FloatingActionButton[5];

                    for(int k = 0; k < listaSinDuplicados.size() && k < textViews.length;k++){
                        int j = k;
                        textViews[k] = dialog.findViewById(getResources().getIdentifier("Name" + k, "id", getContext().getPackageName()));
                        buttons[k] = dialog.findViewById(getResources().getIdentifier("ButtonF" + k, "id", getContext().getPackageName()));
                        buttons_e[k] = dialog.findViewById(getResources().getIdentifier("ButtonE" + k, "id", getContext().getPackageName()));
                        textViews[k].setText(listaSinDuplicados.get(k));
                        textViews[k].setVisibility(View.VISIBLE);
                        buttons[k].setVisibility(View.VISIBLE);
                        buttons_e[k].setVisibility(View.VISIBLE);
                        buttons_e[k].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                BorrarGuardados = FirebaseDatabase.getInstance().getReference("RutasGuardadas/"+listaSinDuplicados.get(j));
                                BorrarGuardados.removeValue();
                                int ultimo = listaSinDuplicados.size()-1;
                                textViews[ultimo].setVisibility(View.GONE);
                                buttons[ultimo].setVisibility(View.GONE);
                                buttons_e[ultimo].setVisibility(View.GONE);
                            }
                        });
                    }

                } else {
                    // La rama RutasGuardadas no existe
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar errores de lectura de Firebase
            }
        });
    }
}





