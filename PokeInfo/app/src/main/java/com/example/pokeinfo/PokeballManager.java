package com.example.pokeinfo;

import android.content.Context;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class PokeballManager {

    private static final String FILE_NAME = "userFile.json";

    // Mapping from number to pokeball type
    private static final String[] POKEBALL_TYPES = {
            "pokeballs",
            "superballs",
            "ultraballs",
            "masterballs"
    };

    public static int getPokeballQuantity(Context context, int pokeballTypeNumber) {
        if (pokeballTypeNumber < 1 || pokeballTypeNumber > POKEBALL_TYPES.length) {
            throw new IllegalArgumentException("Invalid pokeball type number");
        }

        String pokeballType = POKEBALL_TYPES[pokeballTypeNumber - 1];
        JsonObject jsonObject = loadPokeballsData(context);

        if (jsonObject != null) {
            JsonArray pokeballsArray = jsonObject.has("pokeballs") ? jsonObject.getAsJsonArray("pokeballs") : new JsonArray();
            for (int i = 0; i < pokeballsArray.size(); i++) {
                JsonObject pokeballObject = pokeballsArray.get(i).getAsJsonObject();
                if (pokeballObject.get("name").getAsString().equals(pokeballType)) {
                    return pokeballObject.get("qty").getAsInt();
                }
            }
        }

        return 0;
    }


    private static JsonObject loadPokeballsData(Context context) {
        try {
            File file = new File(context.getFilesDir(), FILE_NAME);
            if (file.exists()) {
                FileInputStream fis = context.openFileInput(FILE_NAME);
                InputStreamReader isr = new InputStreamReader(fis);
                JsonObject jsonObject = JsonParser.parseReader(isr).getAsJsonObject();
                isr.close();
                return jsonObject;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void changePokeballQuantity(Context context, int pokeballTypeNumber, int changeAmount) {
        try {
            if (pokeballTypeNumber < 1 || pokeballTypeNumber > POKEBALL_TYPES.length) {
                throw new IllegalArgumentException("Invalid pokeball type number");
            }

            String pokeballType = POKEBALL_TYPES[pokeballTypeNumber - 1];
            JsonObject jsonObject = loadPokeballsData(context);

            if (jsonObject != null) {
                JsonArray pokeballsArray = jsonObject.has("pokeballs") ? jsonObject.getAsJsonArray("pokeballs") : new JsonArray();
                boolean found = false;

                for (int i = 0; i < pokeballsArray.size(); i++) {
                    JsonObject pokeballObject = pokeballsArray.get(i).getAsJsonObject();
                    if (pokeballObject.get("name").getAsString().equals(pokeballType)) {
                        int currentQty = pokeballObject.get("qty").getAsInt();
                        int newQty = currentQty + changeAmount;
                        if (newQty < 0) {
                            newQty = 0;
                        }
                        pokeballObject.addProperty("qty", newQty);
                        found = true;
                        break;
                    }
                }
                jsonObject.add("pokeballs", pokeballsArray);


                FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
                OutputStreamWriter osw = new OutputStreamWriter(fos);
                osw.write(jsonObject.toString());
                osw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
