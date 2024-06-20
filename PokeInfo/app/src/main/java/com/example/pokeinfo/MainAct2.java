package com.example.pokeinfo;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainAct2 extends AppCompatActivity {

    private RecyclerView mChatRecyclerView;
    private PokeAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pokedexlayout);

        mChatRecyclerView = (RecyclerView)findViewById(R.id.crime_recycler_view);
        mChatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<Pokemon> pokemons = new ArrayList<>();
        for (int i = 1; i < 21; i++) {
            pokemons.add(new Pokemon(i));
        }
        updateUI(pokemons);
    }

    private void updateUI(List<Pokemon> pokemons) {
        mAdapter = new PokeAdapter(pokemons);
        mChatRecyclerView.setAdapter(mAdapter);
    }

    // Adapter
    private class PokeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private List<Pokemon> mChats;

        public PokeAdapter(List<Pokemon> crimes) {
            mChats = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new CrimeHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Pokemon pokemon = mChats.get(position);
            holder.bind(pokemon);
        }

        @Override
        public int getItemCount() {
            return mChats.size();
        }
    }


    // View Holder
    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Pokemon mChat;

        private TextView mName;
        private TextView mTypes;
        private TextView mMessage;
        private TextView mNumMessages;
        private ImageView imageView;
        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.repeated_element, parent, false));
            itemView.setOnClickListener(this);
            imageView = (ImageView) itemView.findViewById(R.id.pokemonImageView);
            mName = (TextView) itemView.findViewById(R.id.name);
            mTypes = (TextView) itemView.findViewById(R.id.types);
        }

        public void bind(Pokemon crime) {
            mChat = crime;

            // aaa();
            //mMessage.setText("Hellloo");

            String url = "https://pokeapi.co/api/v2/pokemon/" + mChat.getID();
            RequestQueue queue = Volley.newRequestQueue(MainAct2.this);
            lookUp(url, queue, imageView);

        }

        public void lookUp(String url, RequestQueue queue, ImageView imageView){
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.

                            JsonParser parser = new JsonParser();
                            JsonObject obj = parser.parse(response).getAsJsonObject();

                            JsonArray all_types = obj.getAsJsonArray("types");//.get("type").getAsJsonObject().get("name").getAsString();
                            String sprite = obj.get("sprites").getAsJsonObject().get("other").getAsJsonObject().get("official-artwork").getAsJsonObject().get("front_default").getAsString();
                            String name = obj.get("name").getAsString();
                            name = name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
                            mName.setText(name);

                            String type = "";
                            JsonObject object;
                            for (int i=0; i<all_types.size();i++) {
                                String type1 = all_types.get(i).getAsJsonObject().get("type").getAsJsonObject().get("name").getAsString();
                                if (type1 != null) type += type1;
                                if (i != all_types.size() - 1) type += ", ";
                            }
                            Glide.with(MainAct2.this)
                                    .load(sprite)
                                    .into(imageView);

                            type = type.substring(0,1).toUpperCase() + type.substring(1).toLowerCase();
                            mTypes.setText(type);
                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mName.setText("That didn't work!");
                    // .setText("No type");
                }
            });
            queue.add(stringRequest);
        }



        @Override
        public void onClick(View view) {

        }
    }

}

