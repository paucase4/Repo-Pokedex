package com.example.pokeinfo;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class Trainer extends Fragment {
    private ImageButton button;
    private EditText newName;
    private TextView username;
    private static final String masterballSprite = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/items/master-ball.png";
    private static final String superballSprite = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/items/great-ball.png";
    private static final String ultraballSprite = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/items/ultra-ball.png";
    private static final String pokeballSprite = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/items/poke-ball.png";
    private static final String FILE_NAME = "userFile.json";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.trainer, container, false);
        button = view.findViewById(R.id.refresh);
        newName = view.findViewById(R.id.newName);
        username = view.findViewById(R.id.username);
        TextView money = view.findViewById(R.id.num_pokecoins);
        username.setText(getName().toUpperCase());

        ArrayList<String> pokemons = getPokemonsFromFile(new ArrayList<>());

        money.setText(String.valueOf(getMoney()));
        drawPokemons(view, pokemons);
        setImages(view);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateName();
            }
        });
        return view;
    }

    private void updateName() {
        try {
            // Read the JSON file from internal storage
            File file = new File(getContext().getFilesDir(), FILE_NAME);
            JsonObject jsonObject;
            if (file.exists()) {
                FileInputStream fis = getContext().openFileInput(FILE_NAME);
                InputStreamReader isr = new InputStreamReader(fis);
                jsonObject = JsonParser.parseReader(isr).getAsJsonObject();
                isr.close();
            } else {
                jsonObject = new JsonObject();
            }

            // Modify or add the field
            jsonObject.addProperty("name", newName.getText().toString());

            // Save the modified JSON file to internal storage
            FileOutputStream fos = getContext().openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(jsonObject.toString());
            osw.close();

            // Update the TextView with the new name.
            username.setText(jsonObject.get("name").getAsString().toUpperCase());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getMoney() {
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
            FileOutputStream fos = getContext().openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(jsonObject.toString());
            osw.close();
            return currentMoney;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }


    private String getName() {
        try {
            InputStreamReader is = new InputStreamReader(getContext().openFileInput(FILE_NAME));
            JsonObject jsonObject = JsonParser.parseReader(is).getAsJsonObject();
            is.close();
            if (jsonObject.has("name")) return jsonObject.get("name").getAsString();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ArrayList<String> getPokemonsFromFile(ArrayList<Integer> pokeballs) {
        ArrayList<String> pokemons = new ArrayList<>();

        try {

            File file = new File(getContext().getFilesDir(), FILE_NAME);
            if (file.exists()) {

                FileInputStream fis = getContext().openFileInput(FILE_NAME);
                InputStreamReader isr = new InputStreamReader(fis);
                JsonObject jsonObject = JsonParser.parseReader(isr).getAsJsonObject();
                isr.close();

                if (jsonObject.has("pokemons")) {
                    JsonObject pokemonHashmap = jsonObject.getAsJsonObject("pokemons");

                    for (Map.Entry<String, JsonElement> entry : pokemonHashmap.entrySet()) {
                        pokemons.add(entry.getKey());
                        pokeballs.add(entry.getValue().getAsInt());
                        //int pokeball = entry.getValue().getAsInt();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pokemons;
    }

    private void drawPokemons(View view, ArrayList<String> pokemonNames) {

        if (!pokemonNames.isEmpty()) {
            ImageView photo = (ImageView) view.findViewById(R.id.photopokemon1);
            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    freePokemon(pokemonNames.get(0), view);
                }
            });
            TextView name1 = (TextView) (view.findViewById(R.id.namepokemon1));

            name1.setText(pokemonNames.get(0).substring(0, 1).toUpperCase() + pokemonNames.get(0).substring(1).toLowerCase());


        } else {
            ImageView photo = view.findViewById(R.id.photopokemon1);
            photo.setVisibility(View.INVISIBLE);
            ImageView pokeball = view.findViewById(R.id.pokeball1);
            pokeball.setVisibility(View.INVISIBLE);
            TextView name1 = view.findViewById(R.id.namepokemon1);
            name1.setText("");
        }
        if (pokemonNames.size() > 1) {
            ImageView photo = view.findViewById(R.id.photopokemon2);
            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    freePokemon(pokemonNames.get(1), view);
                }
            });
            TextView name2 = (TextView) (view.findViewById(R.id.namepokemon2));
            name2.setText(pokemonNames.get(1).substring(0, 1).toUpperCase() + pokemonNames.get(1).substring(1).toLowerCase());

        } else {
            ImageView photo = (ImageView) view.findViewById(R.id.photopokemon2);
            photo.setVisibility(View.INVISIBLE);
            ImageView pokeball = view.findViewById(R.id.pokeball2);
            pokeball.setVisibility(View.INVISIBLE);
            TextView name1 = (TextView) (view.findViewById(R.id.namepokemon2));
            name1.setText("");
        }
        if (pokemonNames.size() > 2) {

            ImageView photo = (ImageView) view.findViewById(R.id.photopokemon3);
            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    freePokemon(pokemonNames.get(2), view);
                }
            });
            TextView name2 = (TextView) (view.findViewById(R.id.namepokemon3));

            name2.setText(pokemonNames.get(2).substring(0, 1).toUpperCase() + pokemonNames.get(2).substring(1).toLowerCase());

        } else {
            ImageView photo = (ImageView) view.findViewById(R.id.photopokemon3);
            photo.setVisibility(View.INVISIBLE);
            ImageView pokeball = view.findViewById(R.id.pokeball3);
            pokeball.setVisibility(View.INVISIBLE);
            TextView name1 = (TextView) (view.findViewById(R.id.namepokemon3));
            name1.setText("");
        }
        if (pokemonNames.size() > 3) {

            ImageView photo = (ImageView) view.findViewById(R.id.photopokemon4);
            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    freePokemon(pokemonNames.get(3), view);
                }
            });
            TextView name2 = (TextView) (view.findViewById(R.id.namepokemon4));

            name2.setText(pokemonNames.get(3).substring(0, 1).toUpperCase() + pokemonNames.get(3).substring(1).toLowerCase());


        } else {
            ImageView photo = (ImageView) view.findViewById(R.id.photopokemon4);
            photo.setVisibility(View.INVISIBLE);
            ImageView pokeball = view.findViewById(R.id.pokeball4);
            pokeball.setVisibility(View.INVISIBLE);
            TextView name1 = (TextView) (view.findViewById(R.id.namepokemon4));
            name1.setText("");
        }
        if (pokemonNames.size() > 4) {

            ImageView photo = (ImageView) view.findViewById(R.id.photopokemon5);
            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    freePokemon(pokemonNames.get(4), view);
                }
            });
            TextView name2 = (TextView) (view.findViewById(R.id.namepokemon5));

            name2.setText(pokemonNames.get(4).substring(0, 1).toUpperCase() + pokemonNames.get(4).substring(1).toLowerCase());

        } else {
            ImageView photo = (ImageView) view.findViewById(R.id.photopokemon5);
            photo.setVisibility(View.INVISIBLE);
            ImageView pokeball = view.findViewById(R.id.pokeball5);
            pokeball.setVisibility(View.INVISIBLE);
            TextView name1 = (TextView) (view.findViewById(R.id.namepokemon5));
            name1.setText("");
        }
        if (pokemonNames.size() > 5) {

            ImageView photo = (ImageView) view.findViewById(R.id.photopokemon6);
            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    freePokemon(pokemonNames.get(5), view);
                }
            });
            TextView name2 = (TextView) (view.findViewById(R.id.namepokemon6));

            name2.setText(pokemonNames.get(5).substring(0, 1).toUpperCase() + pokemonNames.get(5).substring(1).toLowerCase());

        } else {
            ImageView photo = (ImageView) view.findViewById(R.id.photopokemon6);
            photo.setVisibility(View.INVISIBLE);
            ImageView pokeball = view.findViewById(R.id.pokeball6);
            pokeball.setVisibility(View.INVISIBLE);
            TextView name1 = (TextView) (view.findViewById(R.id.namepokemon6));
            name1.setText("");
        }
    }

    private void freePokemon(String toDelete, View v) {
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

            JsonObject pokemonHashmap = jsonObject.has("pokemons") ? jsonObject.getAsJsonObject("pokemons") : new JsonObject();
            if (pokemonHashmap.size()>1) {
                if (pokemonHashmap.has(toDelete)) {
                    pokemonHashmap.remove(toDelete);
                    Toast.makeText(getContext(), toDelete + " has been freed!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), toDelete + " not found!", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getContext(), "You can't free the pokemon.\nYou need to have at least one pokemon", Toast.LENGTH_SHORT).show();
            }

            jsonObject.add("pokemons", pokemonHashmap);

            FileOutputStream fos = getContext().openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(jsonObject.toString());
            osw.close();

            drawPokemons(v, getPokemonsFromFile(new ArrayList<>()));

        } catch (IOException e) {
            e.printStackTrace();
        }
        setImages(this.getView());
    }

    private void setImages(View v) {
        ArrayList<Integer> pokeballs = new ArrayList<>();
        ArrayList<String> pokemons = getPokemonsFromFile(pokeballs);
        ImageView i1 = v.findViewById(R.id.pokeballQty);
        ImageView i2 = v.findViewById(R.id.pokeballQty2);
        ImageView i3 = v.findViewById(R.id.pokeballQty3);
        ImageView i4 = v.findViewById(R.id.pokeballQty4);

        TextView qty1 = v.findViewById(R.id.pokeballQtyText);
        TextView qty2 = v.findViewById(R.id.pokeballQtyText2);
        TextView qty3 = v.findViewById(R.id.pokeballQtyText3);
        TextView qty4 = v.findViewById(R.id.pokeballQtyText4);

        qty1.setText(String.valueOf(PokeballManager.getPokeballQuantity(getContext(),1)));
        qty2.setText(String.valueOf(PokeballManager.getPokeballQuantity(getContext(),2)));
        qty3.setText(String.valueOf(PokeballManager.getPokeballQuantity(getContext(),3)));
        qty4.setText(String.valueOf(PokeballManager.getPokeballQuantity(getContext(),4)));

        Glide.with(v).load(pokeballSprite).into(i1);
        Glide.with(v).load(superballSprite).into(i2);
        Glide.with(v).load(ultraballSprite).into(i3);
        Glide.with(v).load(masterballSprite).into(i4);


        int i = 0;

        int photos[] = {R.id.photopokemon1,R.id.photopokemon2,R.id.photopokemon3,R.id.photopokemon4,R.id.photopokemon5,R.id.photopokemon6};
        int photos2[] = {R.id.pokeball1,R.id.pokeball2,R.id.pokeball3,R.id.pokeball4,R.id.pokeball5,R.id.pokeball6};

        for (String pokemon : getPokemonsFromFile(new ArrayList<>())) {
            switch (pokeballs.get(i)) {
                case 1:
                    Glide.with(v).load(pokeballSprite).into((ImageView) v.findViewById(photos2[i]));
                    break;
                case 2:
                    Glide.with(v).load(superballSprite).into((ImageView) v.findViewById(photos2[i]));
                    break;
                case 3:
                    Glide.with(v).load(ultraballSprite).into((ImageView) v.findViewById(photos2[i]));
                    break;
                case 4:
                    Glide.with(v).load(masterballSprite).into((ImageView) v.findViewById(photos2[i]));
                    break;

            }

            ImageView imageView = v.findViewById(photos[i]);
            String url = "https://pokeapi.co/api/v2/pokemon/" + pokemon.toLowerCase();
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JsonParser parser = new JsonParser();
                            JsonObject obj = parser.parse(response).getAsJsonObject();
                            if (pokemon.toUpperCase().equals(pokemon)) {
                                Glide.with(getContext())
                                        .load(obj.get("sprites").getAsJsonObject().get("front_shiny").getAsString())
                                        .into(imageView);
                            }
                            else {
                                Glide.with(getContext())
                                        .load(obj.get("sprites").getAsJsonObject().get("front_default").getAsString())
                                        .into(imageView);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    return;
                }
            });
            queue.add(stringRequest);
            i++;
        }

    }
}


