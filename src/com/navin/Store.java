package com.navin;

import com.navin.Location;

import java.io.Serializable;

public class Store extends Location implements Serializable {
    public String ID="";
    public String color="#ff12b1";
    public Store(double X, double Y) {
        super(X, Y);
    }

    public boolean match(double x, double y){
        if(Math.abs(this.X-x)<15&&Math.abs(this.Y-y)<15)
            return true;
        else
            return false;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public String getID() {
        return ID;
    }

}
