package com.example.pokeinfo;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

public class Store extends Fragment {
    private String FILE_NAME = "userFile.json";
    private static final String masterballSprite = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/items/master-ball.png";
    private static final String superballSprite = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/items/great-ball.png";
    private static final String ultraballSprite = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/items/ultra-ball.png";
    private static final String pokeballSprite = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/items/poke-ball.png";
    private TextView money;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.store, container, false);

        ImageView pokeball = view.findViewById(R.id.imageView_pokeball);
        ImageView superball = view.findViewById(R.id.imageView_superball);
        ImageView ultraball = view.findViewById(R.id.imageView_ultraball);
        ImageView masterball = view.findViewById(R.id.imageView_masterball);

        money = view.findViewById(R.id.money);
        money.setText(String.valueOf(getMoney()));

        Glide.with(view).load(pokeballSprite).into(pokeball);
        Glide.with(view).load(superballSprite).into(superball);
        Glide.with(view).load(ultraballSprite).into(ultraball);
        Glide.with(view).load(masterballSprite).into(masterball);

        setBuyResponses(view);

        // updateUI(pokemons);

        return view;
    }
    public void setBuyResponses(View view) {
        Button buyPoke = view.findViewById(R.id.button_confirm_pokeball);
        Button buySuper = view.findViewById(R.id.button_confirm_superball);
        Button buyUltra = view.findViewById(R.id.button_confirm_ultraball);
        Button buyMaster = view.findViewById(R.id.button_confirm_masterball);

        EditText qtyPoke = view.findViewById(R.id.editText_pokeball_quantity);
        EditText qtySuper = view.findViewById(R.id.editText_superball_quantity);
        EditText qtyUltra = view.findViewById(R.id.editText_ultraball_quantity);
        EditText qtyMaster = view.findViewById(R.id.editText_masterball_quantity);

        buyPoke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int moneyAvailable = getMoney();
                int qtySpent;
                try {
                    qtySpent = Integer.valueOf(qtyPoke.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Invalid quantity entered. Please enter a valid number.", Toast.LENGTH_SHORT).show();
                    System.out.printf("23");
                    return;
                }
                int moneySpent = qtySpent*200;

                if (moneySpent <= moneyAvailable) {

                    Toast.makeText(getContext(), "Spent " + moneySpent + " on " + qtySpent + " pokeballs. Remaining pokécoins: " + (moneyAvailable - moneySpent), Toast.LENGTH_SHORT);
                    purchasePokeballs("pokeballs", qtySpent, moneySpent * -1);
                    money.setText(String.valueOf(moneyAvailable-moneySpent));
                }
                else {
                    Toast.makeText(getContext(), "Insufficient pokecoins",  Toast.LENGTH_SHORT).show();

                }
            }
        });

        buySuper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int moneyAvailable = getMoney();
                int qtySpent;
                try {
                    qtySpent = Integer.valueOf(qtySuper.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Please enter a valid number.", Toast.LENGTH_SHORT).show();
                    return;
                }
                int moneySpent = qtySpent*500;

                if (moneySpent <= moneyAvailable) {
                    Toast.makeText(getContext(), "Spent " + moneySpent + " on " + qtySpent + " superballs. Remaining pokécoins: " + (moneyAvailable - moneySpent), Toast.LENGTH_SHORT);
                    purchasePokeballs("superballs", qtySpent, moneySpent * -1);
                    money.setText(String.valueOf(moneyAvailable-moneySpent));
                    // updatetext
                }
                else {
                    Toast.makeText(getContext(), "Insufficient pokecoins",  Toast.LENGTH_SHORT).show();
                }
            }
        });

        buyUltra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int moneyAvailable = getMoney();
                int qtySpent;
                try {
                    qtySpent = Integer.valueOf(qtyUltra.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Invalid quantity entered. Please enter a valid number.", Toast.LENGTH_SHORT).show();
                    System.out.printf("23");
                    return;
                }
                int moneySpent = qtySpent*1500;

                if (moneySpent <= moneyAvailable) {

                    Toast.makeText(getContext(), "Spent " + moneySpent + " on " + qtySpent + " ultraballs. Remaining pokécoins: " + (moneyAvailable - moneySpent), Toast.LENGTH_SHORT);
                    purchasePokeballs("ultraballs", qtySpent, moneySpent * -1);
                    money.setText(String.valueOf(moneyAvailable-moneySpent));

                }
                else {
                    Toast.makeText(getContext(), "Insufficient pokecoins",  Toast.LENGTH_SHORT).show();
                }
            }
        });

        buyMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int moneyAvailable = getMoney();
                int qtySpent;
                try {
                    qtySpent = Integer.valueOf(qtyMaster.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Invalid quantity entered. Please enter a valid number.", Toast.LENGTH_SHORT).show();
                    return;
                }
                int moneySpent = qtySpent*100000;

                if (moneySpent <= moneyAvailable) {

                    Toast.makeText(getContext(), "Spent " + moneySpent + " on " + qtySpent + " masterballs. Remaining pokecoins: " + (moneyAvailable - moneySpent), Toast.LENGTH_SHORT);
                    purchasePokeballs("masterballs", qtySpent, moneySpent * -1);
                    money.setText(String.valueOf(moneyAvailable-moneySpent));

                }
                else {
                    Toast.makeText(getContext(), "Insufficient pokecoins",  Toast.LENGTH_SHORT).show();
                }
            }
        });
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


    private void purchasePokeballs (String pokeballType,int purchaseQty, int moneySpent){
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

            int newMoney = currentMoney + moneySpent;

            jsonObject.addProperty("money", newMoney);

            JsonArray pokeballsArray = jsonObject.has("pokeballs") ? jsonObject.getAsJsonArray("pokeballs") : new JsonArray();
            boolean found = false;

            for (int i = 0; i < pokeballsArray.size(); i++) {
                JsonObject pokeballObject = pokeballsArray.get(i).getAsJsonObject();
                if (pokeballObject.get("name").getAsString().equals(pokeballType)) {

                    int currentQty = pokeballObject.get("qty").getAsInt();
                    pokeballObject.addProperty("qty", currentQty + purchaseQty);
                    found = true;
                    break;
                }
            }

            if (!found) {

                JsonObject newPokeball = new JsonObject();
                newPokeball.addProperty("name", pokeballType);
                newPokeball.addProperty("qty", purchaseQty);
                pokeballsArray.add(newPokeball);
            }

            jsonObject.add("pokeballs", pokeballsArray);
            Toast.makeText(getContext(), "Bought "+ purchaseQty +  ". new money: " + newMoney  , Toast.LENGTH_SHORT).show();


            FileOutputStream fos = getContext().openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(jsonObject.toString());
            osw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

