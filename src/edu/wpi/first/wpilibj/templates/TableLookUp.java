/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;
import java.util.Hashtable;

/**
 * Helper class to decide what elevation should be fed to the elevator in order to shoot
 * @author Jamie Dyer
 */
public class TableLookUp {
    double[] distanceMiddle = {10,15,20,25,30,35};// this is for the middle target
    double[] angleMiddle = {3.152,3.093,2.921,2.90,2.807,2.7965};//these values are in voltage not Angles! for the middle target
    double[] distanceHigh = {10,15,20,25,30,35};// this is for the middle target
    double[] angleHigh = {3.19233,3.093,2.957,2.989,2.896,2.796};//these values are in voltage not Angles! for the middle target
    int match;
    double anglePercentage;
    double difference;
    double rpmPercentage;

    
    //given a distance and a set of numbers to use, determines necessary angle to shoot at
    public double lookUpAngle(double currentDistance, double[] distanceArray, double[] angleArray){//pass in our distance, and the arrays for the target we are aiming array
        double result = 0;
        if(currentDistance < distanceArray[distanceArray.length - 1]){
            for (int i = 0; i < distanceArray.length; i++){//searches for a match of distance in angle array
                if (currentDistance == distanceArray[i]){
                    result = angleArray[i];
                    break;
                } 
                else if(currentDistance>distanceArray[i] && currentDistance < distanceArray[i+1]){//used to find two values to interperlate 
                    difference = distanceArray[i+1] - distanceArray[i];
                    anglePercentage = (currentDistance-distanceArray[i])/difference;
                    result = angleArray[i]+((angleArray[i+1]-angleArray[i]) * anglePercentage);
                    break;
                }   
            }
        } 
        return result;
    }
}
