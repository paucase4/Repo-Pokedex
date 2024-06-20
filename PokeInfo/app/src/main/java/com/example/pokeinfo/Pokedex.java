package com.example.pokeinfo;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.BoringLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Pokedex extends Fragment {

    private RecyclerView mChatRecyclerView;
    private PokeAdapter mAdapter;
    private static final String FILE_NAME = "userFile.json";
    private boolean loadingItems;
    private static final String masterballSprite = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/items/master-ball.png";
    private static final String superballSprite = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/items/great-ball.png";
    private static final String ultraballSprite = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/items/ultra-ball.png";
    private static final String pokeballSprite = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/items/poke-ball.png";
    private ArrayList<Integer> pokeballs = new ArrayList<>();
    private LinearLayout generalLayout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        File file = new File(getContext().getFilesDir(), FILE_NAME);
        if (!file.exists()) {
            FileOutputStream fos = null;
            try {
                fos = getContext().openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
                OutputStreamWriter osw = new OutputStreamWriter(fos);
                JsonObject j = new JsonObject();
                j.addProperty("money", 500);
                osw.write(j.toString());
                osw.close();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        View view = inflater.inflate(R.layout.pokedexlayout, container, false);

        Button search = view.findViewById(R.id.searchButton);
        EditText nameLookup = view.findViewById(R.id.searchedName);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Pokemon> pokemons = new ArrayList<>();
                pokemons.add(new Pokemon(nameLookup.getText().toString()));
                updateUI(true, pokemons);
                mAdapter.addPokemons(pokemons, view, true);
            }
        });

        ArrayList<String> capturedPokemons = getPokemonsFromFile(pokeballs);

        mChatRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        mChatRecyclerView.setLayoutManager(layoutManager);
        updateUI(false, new ArrayList<>());
        ArrayList<Pokemon> pokemons = loadMorePokemons(1);
        mAdapter.addPokemons(pokemons,view, false);

        loadingItems = false;
        mChatRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if (!loadingItems && totalItemCount <= (lastVisibleItem + 1)) {
                    loadingItems=true;
                    ArrayList<Pokemon> newPokes = loadMorePokemons(totalItemCount+1);
                    mAdapter.addPokemons(newPokes, view, false);
                }
            }
        });

        return view;
    }

    private void updateUI(boolean search, ArrayList<Pokemon> pokemons) {
        mAdapter = new PokeAdapter(pokemons, search);
        mChatRecyclerView.setAdapter(mAdapter);
    }

    private ArrayList<Pokemon> loadMorePokemons(int given) {
        ArrayList<Pokemon> newPokemons = new ArrayList<>();
        for (int i = given; i < given + 15; i++) {
            newPokemons.add(new Pokemon(i));
        }
        return newPokemons;
    }

    private class PokemonHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private PokemonData pokemonData;
        private TextView mName;
        private TextView mTypesText;
        private ImageView mTypes;
        private ImageView imageView;
        private ImageView pokeballView;
        private ImageView mTypes2;


        public PokemonHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.repeated_element, parent, false));
            itemView.setOnClickListener(this);
            imageView = (ImageView) itemView.findViewById(R.id.pokemonImageView);
            mName = (TextView) itemView.findViewById(R.id.name);
            mTypesText = (TextView) itemView.findViewById(R.id.typetext);
            mTypes = (ImageView) itemView.findViewById(R.id.types);
            mTypes2 = (ImageView) itemView.findViewById(R.id.types2);
            pokeballView = itemView.findViewById(R.id.pokeballImage);
        }

        public void bind(PokemonData pokedata) {
            pokemonData = pokedata;
            setComps();

        }

        public void setComps() {
            Glide.with(getContext())
                    .load(pokemonData.frontSprite)
                    .into(imageView);
            String types = pokemonData.type;
            mTypesText.setText(types);
            String[] types_string = types.split(", ");

            mName.setText(pokemonData.name);
            ArrayList<Integer> pokeballs = new ArrayList<>();
            ArrayList<String> pokemons = getPokemonsFromFile(pokeballs);
            boolean image = false;
            for (String s : pokemons) {
                if (s.replace("\"", "").toLowerCase().equals(pokemonData.name.toLowerCase())) {
                    for (int i = 0; i < pokeballs.size(); i++) {
                        if (pokemons.get(i).replace("\"", "").toLowerCase().equals(pokemonData.name.toLowerCase())) {
                            pokeballView.setVisibility(View.VISIBLE);
                            image = true;
                            switch (pokeballs.get(i)) {
                                case 1:
                                    Glide.with(getContext()).load(pokeballSprite).into(pokeballView);

                                    break;
                                case 2:
                                    Glide.with(getContext()).load(superballSprite).into(pokeballView);

                                    break;
                                case 3:
                                    Glide.with(getContext()).load(ultraballSprite).into(pokeballView);

                                    break;
                                case 4:
                                    Glide.with(getContext()).load(masterballSprite).into(pokeballView);

                                    break;
                            }
                            if (pokemons.get(i).toUpperCase().equals(pokemons.get(i))) {
                                Glide.with(getContext()).load(pokemonData.frontShiny).into(imageView);
                            }
                        }
                    }
                }
            }
            if (!image) pokeballView.setVisibility(View.INVISIBLE);
            synchronized (Pokedex.class){
                int second = 0;
                boolean plus_elements = false;
                if (types_string.length>1)plus_elements = true;
                for (String st : types_string) {
                    st = st.toLowerCase();
                    switch (st) {
                        case "grass":
                            if(second == 0) {
                                mTypes.setImageResource(R.drawable.grass);
                                second = 1;
                            }else if (second==1 && plus_elements){
                                mTypes2.setImageResource(R.drawable.grass);
                                mTypes2.setVisibility(View.VISIBLE);
                                second = 0;
                            }
                            if(!plus_elements){
                                mTypes2.setVisibility(View.INVISIBLE);
                            }
                            break;

                        case "bug":
                            if(second == 0) {
                                mTypes.setImageResource(R.drawable.bug);

                                second = 1;
                            }else if (second==1 && plus_elements){
                                mTypes2.setImageResource(R.drawable.bug);
                                mTypes.setVisibility(View.VISIBLE);

                                second = 0;
                            }
                            if(!plus_elements){
                                mTypes2.setVisibility(View.INVISIBLE);
                            }

                            break;

                        case "dark":
                            if(second == 0) {
                                mTypes.setImageResource(R.drawable.dark);
                                mTypes.setVisibility(View.VISIBLE);

                                second = 1;
                            }else if (second==1 && plus_elements){
                                mTypes2.setImageResource(R.drawable.dark);
                                mTypes.setVisibility(View.VISIBLE);

                                second = 0;
                            }
                            if(!plus_elements){
                                mTypes2.setVisibility(View.INVISIBLE);
                            }

                            break;

                        case "dragon":
                            if(second == 0) {
                                mTypes.setImageResource(R.drawable.dragon);
                                second = 1;
                            }else if (second==1 && plus_elements){
                                mTypes2.setImageResource(R.drawable.dragon);
                                mTypes2.setVisibility(View.VISIBLE);
                                second = 0;
                            }
                            if(!plus_elements){
                                mTypes2.setVisibility(View.INVISIBLE);
                            }

                            break;

                        case "electric":
                            if(second == 0) {
                                mTypes.setImageResource(R.drawable.electric);
                                second = 1;
                            }else if (second==1 && plus_elements){
                                mTypes2.setImageResource(R.drawable.electric);
                                mTypes2.setVisibility(View.VISIBLE);
                                second = 0;
                            }
                            if(!plus_elements){
                                mTypes2.setVisibility(View.INVISIBLE);
                            }
                            break;

                        case "fairy":
                            if(second == 0) {
                                mTypes.setImageResource(R.drawable.fairy);
                                second = 1;
                            }else if (second==1 && plus_elements){
                                mTypes2.setImageResource(R.drawable.fairy);
                                mTypes2.setVisibility(View.VISIBLE);
                                second = 0;
                            }
                            if(!plus_elements){
                                mTypes2.setVisibility(View.INVISIBLE);
                            }
                            break;

                        case "fighting":
                            if(second == 0) {
                                mTypes.setImageResource(R.drawable.fighting);
                                second = 1;
                            }else if (second==1 && plus_elements){
                                mTypes2.setImageResource(R.drawable.fighting);
                                mTypes2.setVisibility(View.VISIBLE);

                                second = 0;
                            }
                            if(!plus_elements){
                                mTypes2.setVisibility(View.INVISIBLE);
                            }

                            break;

                        case "fire":
                            if(second == 0) {
                                mTypes.setImageResource(R.drawable.fire);
                                second = 1;
                            }else if (second==1 && plus_elements){
                                mTypes2.setImageResource(R.drawable.fire);
                                mTypes2.setVisibility(View.VISIBLE);

                                second = 0;
                            }
                            if(!plus_elements){
                                mTypes2.setVisibility(View.INVISIBLE);
                            }
                            break;

                        case "flying":
                            if(second == 0) {
                                mTypes.setImageResource(R.drawable.flying);
                                second = 1;
                            }else if (second==1 && plus_elements){
                                mTypes2.setImageResource(R.drawable.flying);
                                mTypes2.setVisibility(View.VISIBLE);

                                second = 0;
                            }
                            if(!plus_elements){
                                mTypes2.setVisibility(View.INVISIBLE);
                            }
                            break;

                        case "ghost":
                            if(second == 0) {
                                mTypes.setImageResource(R.drawable.ghost);
                                second = 1;
                            }else if (second==1 && plus_elements){
                                mTypes2.setImageResource(R.drawable.ghost);
                                mTypes2.setVisibility(View.VISIBLE);

                                second = 0;
                            }
                            if(!plus_elements){
                                mTypes2.setVisibility(View.INVISIBLE);
                            }
                            break;

                        case "ground":
                            if(second == 0) {
                                mTypes.setImageResource(R.drawable.ground);
                                second = 1;
                            }else if (second==1 && plus_elements){
                                mTypes2.setImageResource(R.drawable.ground);
                                mTypes2.setVisibility(View.VISIBLE);

                                second = 0;
                            }
                            if(!plus_elements){
                                mTypes2.setVisibility(View.INVISIBLE);
                            }
                            break;

                        case "ice":
                            if(second == 0) {
                                mTypes.setImageResource(R.drawable.ice);
                                second = 1;
                            }else if (second==1 && plus_elements){
                                mTypes2.setImageResource(R.drawable.ice);
                                mTypes2.setVisibility(View.VISIBLE);

                                second = 0;
                            }
                            if(!plus_elements){
                                mTypes2.setVisibility(View.INVISIBLE);
                            }
                            break;

                        case "normal":
                            if(second == 0) {
                                mTypes.setImageResource(R.drawable.normal);
                                second = 1;
                            }else if (second==1 && plus_elements){
                                mTypes2.setImageResource(R.drawable.normal);
                                mTypes2.setVisibility(View.VISIBLE);

                                second = 0;
                            }
                            if(!plus_elements){
                                mTypes2.setVisibility(View.INVISIBLE);
                            }
                            break;

                        case "poison":
                            if(second == 0) {
                                mTypes.setImageResource(R.drawable.poison);
                                second = 1;
                            }else if (second==1 && plus_elements){
                                mTypes2.setImageResource(R.drawable.poison);
                                mTypes2.setVisibility(View.VISIBLE);

                                second = 0;
                            }
                            if(!plus_elements){
                                mTypes2.setVisibility(View.INVISIBLE);
                            }
                            break;

                        case "psychic":
                            if(second == 0) {
                                mTypes.setImageResource(R.drawable.psychic);
                                second = 1;
                            }else if (second==1 && plus_elements){
                                mTypes2.setImageResource(R.drawable.psychic);
                                mTypes2.setVisibility(View.VISIBLE);

                                second = 0;
                            }
                            if(!plus_elements){
                                mTypes2.setVisibility(View.INVISIBLE);
                            }
                            break;

                        case "rock":
                            if(second == 0) {
                                mTypes.setImageResource(R.drawable.rock);
                                second = 1;
                            }else if (second==1 && plus_elements){
                                mTypes2.setImageResource(R.drawable.rock);
                                mTypes2.setVisibility(View.VISIBLE);
                                second = 0;
                            }
                            if(!plus_elements){
                                mTypes2.setVisibility(View.INVISIBLE);
                            }
                            break;

                        case "steel":
                            if(second == 0) {
                                mTypes.setImageResource(R.drawable.steel);
                                second = 1;
                            }else if (second==1 && plus_elements){
                                mTypes2.setImageResource(R.drawable.steel);
                                mTypes2.setVisibility(View.VISIBLE);
                                second = 0;
                            }
                            if(!plus_elements){
                                mTypes2.setVisibility(View.INVISIBLE);
                            }
                            break;

                        case "water":
                            if(second == 0) {
                                mTypes.setImageResource(R.drawable.water);
                                second = 1;
                            }else if (second==1 && plus_elements){
                                mTypes2.setImageResource(R.drawable.water);
                                mTypes2.setVisibility(View.VISIBLE);
                                second = 0;
                            }
                            if(!plus_elements){
                                mTypes2.setVisibility(View.INVISIBLE);
                            }
                            break;
                    }
                }
                loadingItems=false;
            }
        }

        private void showPokemon() {
            PokemonDetail pokemonDetail = PokemonDetail.newInstance(pokemonData.name, pokemonData.hp, pokemonData.attack, pokemonData.defense, pokemonData.specialAttack, pokemonData.specialDefense, pokemonData.speed, pokemonData.frontSprite, pokemonData.backSprite, pokemonData.abilityName);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentHolder, pokemonDetail)
                    .addToBackStack(null)
                    .commit();
        }
        @Override
        public void onClick(View view) {
            if (!pokemonData.clickDisabled) {
                String name = (String) mName.getText();
                showPokemon();
            }
        }
    }

        // DAOS
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


    private class PokeAdapter extends RecyclerView.Adapter<PokemonHolder> {

        private List<Pokemon> pokemonList;
        private List<PokemonData> pokemonDataList =  new ArrayList<>();
        private boolean search;

        public PokeAdapter(List<Pokemon> pokemons, boolean search) {
            pokemonList = pokemons;
            this.search = search;

        }

        public void addPokemons(List<Pokemon> newPokemons, View view, boolean search) {
            if (this.search && !search) return;
            view.findViewById(R.id.loadingIcon).setVisibility(View.VISIBLE);
            view.findViewById(R.id.colorOverlay).setVisibility(View.VISIBLE);
           // pokemonList.addAll(newPokemons);
            int i = 0;
            ArrayList<Boolean> b = new ArrayList<>();
            for (Pokemon pokemon: newPokemons) {
                new PokemonData().getDataPokemon(search, getContext(), pokemon, pokemonDataList);
                i++;
            }
        }
        public void reload(ArrayList<PokemonData> pokemonDataList) {
            this.pokemonDataList = pokemonDataList;
            notifyItemRangeInserted(getItemCount()-15, getItemCount());
        }
        @Override
        public PokemonHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            return new PokemonHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(PokemonHolder holder, int position) {
            if (pokemonDataList.isEmpty()) return;
            PokemonData pokemon = pokemonDataList.get(position);
            holder.bind(pokemon);
        }

        @Override
        public int getItemCount() {
            return pokemonDataList.size();
            }
    }




    public class PokemonData {
    private int hp;
    private int speed;
    private int attack;
    private int defense;
    private int specialAttack;
    private int specialDefense;
    private String frontSprite;
    private String backSprite;
    private String frontShiny;
    private String backShiny;
    private boolean clickDisabled;
    private String abilityName;
    private String type = "";
    private String name;
    private int id;

        public int getID() {
            return id;
        }
        public void getDataPokemon(boolean search, Context c, Pokemon pokemon, List<PokemonData> pokemonDataList) {
            if (!(pokemon.getName().isEmpty())) {
                String url = "https://pokeapi.co/api/v2/pokemon/" + pokemon.getName().toLowerCase();
                RequestQueue queue = Volley.newRequestQueue(c);
                lookUp(url, queue, pokemonDataList, pokemon.getID(),search);
            }
            else {
                pokemonDataList.add(new PokemonData());
                pokemonDataList.get(0).clickDisabled = true;
                pokemonDataList.get(0).name = "No results";
                mAdapter.reload((ArrayList) pokemonDataList);
                getView().findViewById(R.id.loadingIcon).setVisibility(View.GONE);
                getView().findViewById(R.id.colorOverlay).setVisibility(View.GONE);
            }
        }

        public void lookUp(String url, RequestQueue queue, List<PokemonData> pokemonDataList, int idPokemon, boolean search) {

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            PokemonData pd = new PokemonData();
                            JsonParser parser = new JsonParser();
                            pd.id = idPokemon;
                            JsonObject obj = parser.parse(response).getAsJsonObject();
                            pd.clickDisabled = false;
                            if (obj == null) return;
                            JsonArray all_abilities = obj.getAsJsonArray("abilities");//.get("type").getAsJsonObject().get("name").getAsString();
                            String ability = "";
                            JsonObject object2;
                            ArrayList<JsonObject> abilities = new ArrayList<>();
                            for (int i = 0; i < all_abilities.size(); i++) {
                                object2 = all_abilities.get(i).getAsJsonObject();
                                System.out.println(object2);
                                boolean hidden = all_abilities.get(i).getAsJsonObject().get("is_hidden").getAsBoolean();
                                if (!hidden)
                                    abilities.add(object2); // To give double the chances to not hidden activities
                                abilities.add(object2);
                            }
                            Random rand = new Random();
                            int randomIndex = rand.nextInt(abilities.size());

                            JsonObject randomAbility = abilities.get(randomIndex);
                            pd.abilityName = randomAbility.get("ability").getAsJsonObject().get("name").getAsString();


                            // Pictures
                            pd.frontSprite = obj.get("sprites").getAsJsonObject().get("front_default").getAsString();

                            pd.backSprite = obj.get("sprites").getAsJsonObject().get("back_default").getAsString();
                            if (rand.nextDouble() < (1.0f / 500.0f)) {
                                try {
                                    pd.frontSprite = obj.get("sprites").getAsJsonObject().get("front_shiny").getAsString();
                                    pd.backSprite = obj.get("sprites").getAsJsonObject().get("back_shiny").getAsString();
                                }
                                catch (Exception e){
                                    pd.frontSprite = obj.get("sprites").getAsJsonObject().get("front_default").getAsString();
                                    pd.backSprite = obj.get("sprites").getAsJsonObject().get("back_default").getAsString();
                                }
                            }
                            try {
                                pd.frontShiny = obj.get("sprites").getAsJsonObject().get("front_shiny").getAsString();
                                pd.backShiny = obj.get("sprites").getAsJsonObject().get("back_shiny").getAsString();
                            }
                            catch (Exception e){
                                pd.frontShiny = "";
                                pd.backShiny = "";
                            }


                            // Sample to add pictures to imageview


                            // Types
                            JsonArray all_types = obj.getAsJsonArray("types");//.get("type").getAsJsonObject().get("name").getAsString();
                            JsonObject object;
                            for (int i = 0; i < all_types.size(); i++) {
                                String type1 = all_types.get(i).getAsJsonObject().get("type").getAsJsonObject().get("name").getAsString();
                                if (type1 != null) {
                                    // Find image?
                                    pd.type += type1;
                                }
                                if (i != all_types.size() - 1) pd.type += ", ";
                            }
                            System.out.println("");
                            pd.type = pd.type.substring(0, 1).toUpperCase() + pd.type.substring(1).toLowerCase();



                            // Name
                            pd.name = obj.get("name").getAsString();
                            pd.name = pd.name.substring(0, 1).toUpperCase() + pd.name.substring(1).toLowerCase();



                            // Stats
                            JsonArray allStats = obj.getAsJsonArray("stats");

                            pd.hp = allStats.get(0).getAsJsonObject().get("base_stat").getAsInt();
                            pd.attack = allStats.get(1).getAsJsonObject().get("base_stat").getAsInt();
                            pd.defense = allStats.get(2).getAsJsonObject().get("base_stat").getAsInt();
                            pd.specialAttack = allStats.get(3).getAsJsonObject().get("base_stat").getAsInt();
                            pd.specialDefense = allStats.get(4).getAsJsonObject().get("base_stat").getAsInt();
                            pd.speed = allStats.get(5).getAsJsonObject().get("base_stat").getAsInt();
                            pokemonDataList.add(pd);

                            if (search){
                                mAdapter.reload((ArrayList) pokemonDataList);
                                getView().findViewById(R.id.loadingIcon).setVisibility(View.GONE);
                                getView().findViewById(R.id.colorOverlay).setVisibility(View.GONE);
                            }
                            else {
                                if ((pokemonDataList.size()%15) ==  0 && !pokemonDataList.isEmpty() ) {
                                    pokemonDataList.sort(Comparator.comparing(PokemonData::getID));
                                    mAdapter.reload((ArrayList) pokemonDataList);
                                    getView().findViewById(R.id.loadingIcon).setVisibility(View.GONE);
                                    getView().findViewById(R.id.colorOverlay).setVisibility(View.GONE);
                                }
                            }

                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pokemonDataList.add(new PokemonData());
                    pokemonDataList.get(0).clickDisabled = true;
                    pokemonDataList.get(0).name = "No results";
                    mAdapter.reload((ArrayList) pokemonDataList);
                    getView().findViewById(R.id.loadingIcon).setVisibility(View.GONE);
                    getView().findViewById(R.id.colorOverlay).setVisibility(View.GONE);
                }
            });
            queue.add(stringRequest);
        }
}


}
