package com.example.pokeinfo;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
public class PokedexFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.pokedex_empty, container, false);

            // Add the first fragment initially
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fragmentHolder, new Pokedex())
                    .commit();

            return view;
        }
}
