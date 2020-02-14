package com.navin;

import com.navin.Floor;

import java.io.Serializable;
import java.util.ArrayList;

public class Mall implements Serializable {
    public String Name;
    public ArrayList<Floor> floors = new ArrayList<Floor>();

    public int get_Floor_Count(){
        return floors.size();
    }

    public ArrayList<Floor> getFloors(){
        return floors;
    }
}
