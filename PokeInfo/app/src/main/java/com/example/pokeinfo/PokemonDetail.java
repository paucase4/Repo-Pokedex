package com.example.pokeinfo;



import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.data.DataFetcher;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Random;

public class PokemonDetail extends Fragment {
    private static final String FILE_NAME = "userFile.json";
    private static final String masterballSprite = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/items/master-ball.png";
    private static final String superballSprite = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/items/great-ball.png";
    private static final String ultraballSprite = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/items/ultra-ball.png";
    private static final String pokeballSprite = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/items/poke-ball.png";
    private int type;
    private String desc;

    public static PokemonDetail newInstance(String name, int hp, int attack, int defense, int specialAttack, int specialDefense, int speed, String frontImage, String backImage, String abilityName) {
        PokemonDetail fragment = new PokemonDetail();
        Bundle args = new Bundle();
        args.putString("name",name);
        args.putString("front",frontImage);
        args.putString("back",backImage);
        args.putInt("hp",hp);
        args.putInt("attack",attack);
        args.putInt("defense",defense);
        args.putInt("specialattack",specialAttack);
        args.putInt("specialdefense",specialDefense);
        args.putInt("speed",speed);
        args.putString("ability",abilityName);
        fragment.setArguments(args);
        return fragment;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.pokemondetail, container, false);

        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragmentHolder, new Pokedex())
                        .addToBackStack(null)
                        .commit();
            }
        });
        String namePokemon = getArguments().get("name").toString();
        TextView description = view.findViewById(R.id.description);
        getExtraData(namePokemon, description);
        // PUT ALL DATA INTO SCREEN
        ImageView front = view.findViewById(R.id.pokemonImageView);
        ImageView back = view.findViewById(R.id.pokemonImageView2);

        Glide.with(view).load(getArguments().get("front").toString()).into(front);
        Glide.with(view).load(getArguments().get("back").toString()).into(back);
        TextView name = view.findViewById(R.id.namePokemon);
        name.setText(namePokemon);

        TextView ability = view.findViewById(R.id.ability);

        ability.setText(getArguments().get("ability").toString().toUpperCase());

        TextView num1 = view.findViewById(R.id.num1);
        TextView num2 = view.findViewById(R.id.num2);
        TextView num3 = view.findViewById(R.id.num3);
        TextView num4 = view.findViewById(R.id.num4);
        TextView num5 = view.findViewById(R.id.num5);

        ProgressBar progressBar1 = view.findViewById(R.id.progressBar1);
        ProgressBar progressBar2 = view.findViewById(R.id.progressBar2);
        ProgressBar progressBar3 = view.findViewById(R.id.progressBar3);
        ProgressBar progressBar4 = view.findViewById(R.id.progressBar4);
        ProgressBar progressBar5 = view.findViewById(R.id.progressBar5);

        num1.setText(getArguments().get("hp").toString());
        progressBar1.setProgress(Integer.parseInt(getArguments().get("hp").toString()));

        num2.setText(getArguments().get("attack").toString());
        progressBar2.setProgress(Integer.parseInt(getArguments().get("attack").toString()));

        num3.setText(getArguments().get("defense").toString());
        progressBar3.setProgress(Integer.parseInt(getArguments().get("defense").toString()));

        num4.setText(getArguments().get("specialattack").toString());
        progressBar4.setProgress(Integer.parseInt(getArguments().get("specialattack").toString()));

        num5.setText(getArguments().get("specialdefense").toString());
        progressBar5.setProgress(Integer.parseInt(getArguments().get("specialdefense").toString()));


        // fill on top ^^


        loadImages(view);



        return view;
    }

    private void getExtraData(String name, TextView description) {
        String url = "https://pokeapi.co/api/v2/pokemon-species/" + name.toLowerCase();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JsonParser parser = new JsonParser();
                        JsonObject obj = parser.parse(response).getAsJsonObject();
                        String url2 = obj.get("evolution_chain").getAsJsonObject().get("url").getAsString();
                        JsonArray Jarr = obj.get("flavor_text_entries").getAsJsonArray();
                        for (int i = 0; i < Jarr.size(); i++) {
                            if (Jarr.get(i).getAsJsonObject().get("language").getAsJsonObject().get("name").getAsString().equals("en"))
                            {
                                desc = Jarr.get(i).getAsJsonObject().get("flavor_text").getAsString();
                                break;
                            }
                        }

                        desc = desc.replace("\n", " ");
                        description.setText(desc);
                        boolean legendary = obj.get("is_legendary").getAsBoolean();
                        if (legendary) {
                            type = 4;
                        }
                        else {
                            RequestQueue queue = Volley.newRequestQueue(getActivity());
                            StringRequest stringRequest = new StringRequest(Request.Method.GET, url2, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    JsonParser parser = new JsonParser();
                                    JsonObject obj = parser.parse(response).getAsJsonObject();
                                    JsonObject j = obj.get("chain").getAsJsonObject();
                                    String first = j.get("species").getAsJsonObject().get("name").getAsString();
                                    String second = obj.get("chain").getAsJsonObject().get("evolves_to").getAsJsonArray().get(0).getAsJsonObject().get("species").getAsJsonObject().get("name").getAsString();
                                    if (name.equalsIgnoreCase(first)) {
                                        type = 1;
                                    } else {
                                        if (name.equalsIgnoreCase(second)) {
                                            type = 2;
                                        } else {
                                            type = 3;
                                        }
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    System.out.printf("Error gettingt types");
                                    return;
                                }
                            });
                            queue.add(stringRequest);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
                public void onErrorResponse(VolleyError error) {
                    return;
                }
                });
            queue.add(stringRequest);
        }

    



    private void capturePokemon( int pokeball, String name, View view) {
        PokeballManager.changePokeballQuantity(getContext(), pokeball,-1);

        int[] randomNumberHolder = new int[1];
        Random r = new Random();

        if (!(calculateChances(type, pokeball, randomNumberHolder) >= r.nextDouble())) {
            Toast.makeText(getContext(),"Pokemon capture failed!", Toast.LENGTH_SHORT).show();
            if (PokeballManager.getPokeballQuantity(getContext(),1) <= 0) {
                loadImages(view);
            }
            return;
        }
        try {
            JsonObject jsonObject;

            File file = new File(getContext().getFilesDir(), FILE_NAME);
            if (file.exists()) {
                FileInputStream fis = getContext().openFileInput(FILE_NAME);
                InputStreamReader isr = new InputStreamReader(fis);
                jsonObject = JsonParser.parseReader(isr).getAsJsonObject();
                isr.close();
            } else {
                jsonObject = new JsonObject();
            }
            int currentMoney = jsonObject.has("money") ? jsonObject.get("money").getAsInt() : 0;

            int moneyEarned = moneyEarned(randomNumberHolder[0]);
            int newMoney = currentMoney + moneyEarned;

            jsonObject.addProperty("money", newMoney);
            JsonObject pokemons = jsonObject.has("pokemons") ? jsonObject.getAsJsonObject("pokemons") : new JsonObject();

            if (pokemons.size() < 6) {
                if (!pokemons.has(name)) {
                    pokemons.addProperty(name, pokeball);
                    Toast.makeText(getContext(), "You have captured " + name + "! Pokecoins earned: " + moneyEarned, Toast.LENGTH_SHORT).show();
                    jsonObject.add("pokemons", pokemons);
                    FileOutputStream fos = getContext().openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
                    OutputStreamWriter osw = new OutputStreamWriter(fos);
                    osw.write(jsonObject.toString());
                    osw.close();
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragmentHolder, new Pokedex())
                            .addToBackStack(null)
                            .commit();
                } else {
                    Toast.makeText(getContext(), "You have captured " + name + " already!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "You have captured 6 Pokémon. You need to free one to capture " + name + "! Money earned: " + newMoney, Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean pokemonAlreadyCaptured(JsonArray pokemonArray, String pokemon) {
        for (int i = 0; i < pokemonArray.size(); i++) {
            String name = pokemonArray.get(i).getAsString();
            if (name.equals(pokemon)) return true;
        }
        return false;
    }

    private double calculateChances(int type, int pokeball, int[]randomNumberHolder){
        Random random = new Random();
        int randomNumber = 0;
        switch (type) {
            case 1:
                randomNumber = random.nextInt((80 - 20) + 1) + 20;
                break;
            case 2:
                randomNumber = random.nextInt((200 - 80) + 1) + 80;
                break;
            case 3:
                randomNumber = random.nextInt((350 - 200) + 1) + 200;
                break;
            case 4:
                randomNumber = random.nextInt((500 - 350) + 1) + 350;
                break;
        }

        randomNumberHolder[0] = randomNumber;

        switch (pokeball) {
            case 1:
                return (600 - randomNumber)/600.0f;
            case 2:
                return (600 - randomNumber)/600.0f*1.5f;
            case 3:
                return (600 - randomNumber)/600.0f*2.0f;
            case 4:
                return 1;
        }
        return 0;
    }
    public int moneyEarned(int randomNumber){
        return 400 + 100 * randomNumber;
    }
    private void loadImages(View view){
        ImageButton pokeball = view.findViewById(R.id.pokeball);
        ImageButton superball = view.findViewById(R.id.superball);
        ImageButton ultraball = view.findViewById(R.id.ultraball);
        ImageButton masterball = view.findViewById(R.id.masterball);
        ImageButton back = view.findViewById(R.id.refresh);


        int pokeballQuantity = PokeballManager.getPokeballQuantity(getContext(), 1);
        int superballQuantity = PokeballManager.getPokeballQuantity(getContext(), 2);
        int ultraballQuantity = PokeballManager.getPokeballQuantity(getContext(), 3);
        int masterballQuantity = PokeballManager.getPokeballQuantity(getContext(), 4);

        int grayColor = Color.parseColor("#80000000");
        if (pokeballQuantity>0){
            pokeball.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = getArguments().get("name").toString();
                    if (getArguments().get("front").toString().contains("shiny")) {
                        name = name.toUpperCase();
                    }
                    capturePokemon( 1,name, view);
                }
            });
        }
        else {
            pokeball.setColorFilter(grayColor);
        }

        if (superballQuantity>0){
            superball.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = getArguments().get("name").toString();
                    if (getArguments().get("front").toString().contains("shiny")) {
                        name = name.toUpperCase();
                    }
                    capturePokemon( 2,name, view);
                }
            });
        }
        else {
            superball.setColorFilter(grayColor);
        }
        if (ultraballQuantity>0){
            ultraball.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = getArguments().get("name").toString();
                    if (getArguments().get("front").toString().contains("shiny")) {
                        name = name.toUpperCase();
                    }
                    capturePokemon( 3,name, view);                }
            });
        }
        else {
            ultraball.setColorFilter(grayColor);
        }

        if (masterballQuantity>0){
            masterball.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = getArguments().get("name").toString();
                    if (getArguments().get("front").toString().contains("shiny")) {
                        name = name.toUpperCase();
                    }
                    capturePokemon( 4,name, view);                }
            });
        }
        else {
            masterball.setColorFilter(grayColor);
        }



        Glide.with(view).load(pokeballSprite).into(pokeball);
        Glide.with(view).load(superballSprite).into(superball);
        Glide.with(view).load(ultraballSprite).into(ultraball);
        Glide.with(view).load(masterballSprite).into(masterball);
    }
}
