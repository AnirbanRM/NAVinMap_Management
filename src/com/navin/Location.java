package com.navin;

import java.io.Serializable;
import java.util.ArrayList;

public class Location implements Serializable {
    public double X,Y;
    public ArrayList<Store> stores;

    public Location(double X,double Y){
        stores = new ArrayList<Store>();
        this.X=X;
        this.Y=Y;
    }

    public void add_store(Store e){
        stores.add(e);
    }

    public void remove_store(Store e){
        stores.remove(e);
    }

    public ArrayList<Store> getStores(){
        return stores;
    }

    public boolean match(double x,double y){
        if(Math.abs(this.X-x)<10&&Math.abs(this.Y-y)<10)
            return true;
        else
            return false;
    }
}
