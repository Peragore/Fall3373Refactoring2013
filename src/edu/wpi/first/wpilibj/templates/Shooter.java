/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.DriverStationLCD.Line;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Talon;
/**
 * Controls the shooting mechanism for the robot
 * @author RoboHawks
 */
public class Shooter {
    // Since shooter is tied directly to PWM hardware ports, allow only one
    // instance of shooter to ever be created.  Provide a getInstance() method
    // to return the one and only shooter object to other classes that
    // need to use it. This is known as a Singleton pattern.
    private static final Shooter instance = new Shooter();
    private Shooter() {}  //Prevents other classes from declaring new Shooter()
    public static Shooter getInstance() {
        return instance;
    }
    //TODO: Remove hardcoded ports, add variables to Team3373 with naming convention
    Relay shootSpike = new Relay(4);
    Relay feedSpike = new Relay(5);
    Talon firstMotorTalon = new Talon(6); //Shooter Talons, spin the wheels
    Talon secondMotorTalon = new Talon(5); //Shooter Talons, spin the wheels
    DigitalInput shootLimit = new DigitalInput(6);
    Elevator elevator = Elevator.getInstance();

    double percentageScaler = 0.75;
    double secondMotorVoltage =  0.0;
    double firstMotorVoltage =  0.0;

    int elevatorStatus = 0;
    boolean limit = shootLimit.get();   
    boolean isNotRunning = true;
    boolean canRun = true;
    
    double defaultScaler = .5;
    double startVoltage = .1;
    double stopVoltage = 0;
    
    


    
    public void increaseSpeed(){ //increases stage 2 by 1/10 of possible speed
        secondMotorVoltage += 0.1;
        if (secondMotorVoltage >= 1) {
            secondMotorVoltage = 1;
        }
        setMotorVoltagesAndPercentiles();
    }
    
    public void decreaseSpeed(){//decreases stage 2 by 1/10 of possible speed
        secondMotorVoltage -=  0.1;
        if (secondMotorVoltage <= 0) {
            secondMotorVoltage = 0;
        }
        setMotorVoltagesAndPercentiles();
    }

    

    
    public void setMotorVoltagesAndPercentiles() {
        firstMotorVoltage =  secondMotorVoltage * percentageScaler;
        firstMotorTalon.set(firstMotorVoltage);
        secondMotorTalon.set(secondMotorVoltage);       
    }
    
    public void goToSpeed(double targetSpeed){
         firstMotorTalon.set(targetSpeed * percentageScaler);
         secondMotorTalon.set(targetSpeed);
    }
    
    public void smoothShooterAccel() {
        goToSpeed(.25);
        try {
            Thread.sleep(300);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        goToSpeed(.5);
        try {
            Thread.sleep(300);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }                
        goToSpeed(.75);
        try {
            Thread.sleep(300);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }                
        goToSpeed(1);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }        
    }
    
    public void printLCD(DriverStationLCD LCD){
        double Scaler = 5936; //converts voltage to RPM for display purposes only
        double speedOne = firstMotorTalon.get();
        String speed1 = Double.toString(speedOne);
        double speedTwo = secondMotorTalon.get();
        String speed2 = Double.toString(speedTwo);
        LCD.println(Line.kUser3, 1, ((firstMotorTalon.get()/secondMotorTalon.get()) *100) + "                       %");
        LCD.println(Line.kUser4, 1,"S1:" + speed1);
        LCD.println(Line.kUser2, 1,"S2:" + speed2);
        LCD.println(Line.kUser1, 1, "RPM1: " + (speedOne * Scaler));
        LCD.println(Line.kUser2, 1, "RPM2: " + (speedTwo * Scaler));
        LCD.updateLCD();
    }
    
    //moves frisbee into contact with shooting wheels to shoot
    public void shoot(){
        final Thread thread = new Thread(new Runnable() {
            public void run(){
                limit = shootLimit.get();
                System.out.println(limit);
                //starts shoot arm motion when the arm is at rest
                while(limit && canRun){
                    shootSpike.set(Relay.Value.kForward);
                    limit = shootLimit.get();
                }

                //continues shoot arm motion until arm is back at home position
                while(!limit && canRun){
                    shootSpike.set(Relay.Value.kForward);
                    limit = shootLimit.get();
                }

                //turns arm motion off
                shootSpike.set(Relay.Value.kOff);
            } 
        });
        
        thread.start();
    }
    
    public void loadFrisbee(Elevator elev){
        final Elevator elv = elev;
        final Thread thread = new Thread(new Runnable() {
        
            public void run(){
                feedSpike.set(Relay.Value.kReverse);
                try{
                    Thread.sleep(350);
                }
                catch(InterruptedException e){}
                feedSpike.set(Relay.Value.kOff);   
            } 
        });
        thread.start();
    }
    
    //creates a thread to handle acceleration and firing
    public void shooterThread(){
        final Thread thread = new Thread(new Runnable() {
            public void run(){
                isNotRunning = false;
                if (secondMotorTalon.get() < 1){
                    smoothShooterAccel();
                }
                
                shoot();
                
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                
                loadFrisbee(elevator);
                
                //goToSpeed(.25);
                isNotRunning = true; //no longer busy (/.^.\)
            }
        });
        thread.start();
    }

    
    /*****************
     * Debug Methods *
     *****************/
       
    public void increasePercentage(){//increases percentage of what stage 2 is multiplyied by to get 1
        percentageScaler += 0.05;
        if (percentageScaler >= 1){percentageScaler = 1;}
        firstMotorVoltage =  secondMotorVoltage * percentageScaler;
        firstMotorTalon.set(firstMotorVoltage);
        secondMotorTalon.set(secondMotorVoltage);
    }
    
    public void decreasePercentage(){//decreases percentage of what stage 2 is multiplyied by to get 1
        percentageScaler -= 0.05;
        if (percentageScaler <= 0){percentageScaler = 0;}
        firstMotorVoltage = secondMotorVoltage * percentageScaler;
        firstMotorTalon.set(firstMotorVoltage);
        secondMotorTalon.set(secondMotorVoltage);
    }
    
    public void start(){
        percentageScaler = defaultScaler;
        secondMotorVoltage =  startVoltage;  
        setMotorVoltagesAndPercentiles();
    }
    
    public void stop(){
        firstMotorVoltage = stopVoltage;
        percentageScaler = defaultScaler;
        setMotorVoltagesAndPercentiles();
    }

}
