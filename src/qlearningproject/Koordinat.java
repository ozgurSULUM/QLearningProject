/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qlearningproject;

/**
 *
 * @author ozgur
 */
public class Koordinat {
    private int x;
    private int y;
    private int state;
    public static int MATRIX_LENGTH;

    public Koordinat(int x, int y) {
        this.x = x;
        this.y = y;
        this.state = MATRIX_LENGTH*y+x;
    }
    
    public boolean isEqual(Koordinat koordinat){
        if((this.x == koordinat.getX())&&(this.y == koordinat.getY())){
            return true;
        }
        return false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }


    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
    
    @Override
    public String toString() {
        return "("+x+","+y+","+"K"+")\n";
    }
    
    
    
}
