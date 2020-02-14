package com.navin;

import java.io.Serializable;
import java.util.ArrayList;

public class Floor implements Serializable {

    public byte[] background_map=null;

    public String FloorID;
    public double CanvasWidth,CanvasHeight;
    public ArrayList<Location> path_nodes = new ArrayList<Location>();
    public ArrayList<Store> stores = new ArrayList<Store>();
    public ArrayList<Pair> roads = new ArrayList<Pair>();

    public Floor(String ID, double width, double height){
        this.FloorID=ID;
        this.CanvasWidth=width;
        this.CanvasHeight=height;
    }

    public ArrayList<Location> getPath_nodes() {
        return path_nodes;
    }

    public ArrayList<Store> getStores() {
        return stores;
    }

    public ArrayList<Pair> getRoads() {
        return roads;
    }
}
