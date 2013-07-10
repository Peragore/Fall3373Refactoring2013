/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * 
 * Stores math functions that were not including in WPILib
 */
package edu.wpi.first.wpilibj.templates;

/**
 *
 * @author Philip
 */
public class NewMath {
    //takes the number "a" and brings it to the power of number "b"
    public double pow(double a, double b){
        double base = a;
        for (int i = 0; i < b-1; i++){
            a *= base;
        }
        return a;
    }
    
}

