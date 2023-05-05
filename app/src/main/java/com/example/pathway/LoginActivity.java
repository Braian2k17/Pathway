package com.example.pathway;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    public final int enteranim = R.anim.desplazamiento_derecha_a_izquierda;
    public final int exitanim = R.anim.salida_izquierda;
    public final int EnterAnim = R.anim.desplazamiento_izquierda_a_derecha;
    public final int ExitAnim = R.anim.salida_derecha;

    FirstFragment firstFragment = new FirstFragment();
    SecondFragment secondFragment = new SecondFragment();
    ThirdFragment thirdFragment = new ThirdFragment();
    FourthFragment fourthFragment = new FourthFragment();

    FirebaseDatabase firebase;
    DatabaseReference Automatico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnItemSelectedListener(mOnNavigationItemSelectedListener);
        Fragment primero = new Fragment();
        loadFragment(firstFragment,0,0,primero);
    }

    int anterior = 1;
    int actual = 0;
    Fragment fragmentanterior = firstFragment;
    private final NavigationBarView.OnItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            firebase = FirebaseDatabase.getInstance();
            Automatico = firebase.getReference("Opciones");
            switch (item.getItemId()){
                case R.id.firstFragment:
                    actual = 1;
                    Automatico.setValue(0);
                    if(anterior < actual){
                        loadFragment(firstFragment,enteranim,exitanim,fragmentanterior);
                    }
                    else{
                        loadFragment(firstFragment,EnterAnim,ExitAnim,fragmentanterior);
                    }
                    anterior = 1;
                    fragmentanterior = firstFragment;
                    return true;

                case R.id.secondFragment:
                    Automatico.setValue(1);
                    actual = 2;
                    if(anterior < actual){
                        loadFragment(secondFragment,enteranim,exitanim, fragmentanterior);
                    }
                    else{
                        loadFragment(secondFragment,EnterAnim,ExitAnim, fragmentanterior);
                    }
                    anterior = 2;
                    fragmentanterior = secondFragment;
                    return true;

                case R.id.thirdFragment:
                    actual = 4;
                    if (anterior < actual) {
                        loadFragment(thirdFragment, enteranim, exitanim, fragmentanterior);
                    }
                    else{
                        loadFragment(thirdFragment, EnterAnim, ExitAnim, fragmentanterior);
                    }
                    anterior = 4;
                    fragmentanterior = thirdFragment;
                    return true;

                case R.id.fourthFragment:
                    actual = 3;
                    if(anterior < actual) {
                        loadFragment(fourthFragment, enteranim, exitanim, fragmentanterior);
                    }
                    else{
                        loadFragment(fourthFragment, EnterAnim, ExitAnim, fragmentanterior);
                    }
                    anterior = 3;
                    fragmentanterior = fourthFragment;
                    return true;

            }
            return false;
        }
    };


    public void loadFragment(Fragment fragment, int enterAnimation, int exitAnimation, Fragment currentFragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().setCustomAnimations(enterAnimation,exitAnimation);
//        transaction.replace(R.id.frame_container, fragment);

        if(fragment.isAdded()){
            transaction.hide(currentFragment).show(fragment);
        }
        else {
            System.out.println("tercero");
            transaction
                    .hide(currentFragment)
                    .add(R.id.frame_container, fragment, null);
        }
        transaction.commit();
    }

}