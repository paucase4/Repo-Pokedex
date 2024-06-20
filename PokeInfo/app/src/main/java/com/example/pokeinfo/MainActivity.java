package com.example.pokeinfo;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private ImageView splashLogo;
    private boolean isLogo1Visible = true;
    private Handler handler = new Handler();
    private Runnable logoSwitcher = new Runnable() {
        @Override
        public void run() {
            if (isLogo1Visible) {
                splashLogo.setImageResource(R.drawable.pokemon_logo);
            } else {
                splashLogo.setImageResource(R.drawable.pokemon_logo1);
            }
            isLogo1Visible = !isLogo1Visible;
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        splashLogo = findViewById(R.id.splash_logo);
        splashLogo.setVisibility(View.VISIBLE);

        handler.post(logoSwitcher);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                splashLogo.setVisibility(View.GONE);
                handler.removeCallbacks(logoSwitcher);
                setupBottomNavigation();
            }
        }, 500);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                if (item.getItemId() == R.id.navigation_pokedex) {
                    selectedFragment = new PokedexFragment();
                } else if (item.getItemId() == R.id.navigation_trainer) {
                    selectedFragment = new Trainer();
                } else if (item.getItemId() == R.id.navigation_store) {
                    selectedFragment = new Store();
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, selectedFragment).commit();
                return true;
            }
        });

        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.navigation_pokedex);
    }
}
