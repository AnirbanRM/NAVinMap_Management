package com.navin;

import java.io.Serializable;

public class Pair implements Serializable {

    public Location A,B;

    public void setA(Location a) {
        A = a;
    }

    public void setB(Location b) {
        B = b;
    }

    public double getSlope(){
        return (B.Y-A.Y)/(B.X-A.X);
    }

    public double distance(double X1, double Y1,double X2, double Y2){
        return Math.pow(Math.pow(X1-X2,2)+Math.pow(Y1-Y2,2),0.5);
    }

    public Pair match(double x, double y){
        if(get_difference(x,y)<3)
            return this;
        else return null;
    }

    public double get_difference(double x, double y){
        return distance(x,y,A.X,A.Y)+distance(x,y,B.X,B.Y)-distance(A.X,A.Y,B.X,B.Y);
    }
}
