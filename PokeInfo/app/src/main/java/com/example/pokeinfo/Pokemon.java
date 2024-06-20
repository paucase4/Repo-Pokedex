package com.example.pokeinfo;

import java.util.ArrayList;

public class Pokemon {
    private String name;
    private int apiID;

    public int getID() {
        return apiID;
    }

    public void setID(int apiID) {
        this.apiID = apiID;
    }

    private ArrayList<String> types;

    public Pokemon(int id) {
        this.apiID = id;
        this.name = String.valueOf(id);
    }
    public Pokemon(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<String> types) {
        this.types = types;
    }
}
