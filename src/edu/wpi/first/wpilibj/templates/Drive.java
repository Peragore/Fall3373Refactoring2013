/*
 * This class controls robot motion based upon joystick input. Class is created
 * as a singleton.
 * @Author Team3373
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.*;

public class Drive {
    // Since drive is tied directly to PWM hardware ports, allow only one
    // instance of Drive to ever be created.  Provide a getInstance() method
    // to return the one and only Drive object to other classes that
    // need to use it. This is known as a Singleton pattern.
    
    static int frontLeftMotor = 1;
    static int rearLeftMotor = 2;   
    static int frontRightMotor = 4;
    static int rearRightMotor = 3;
    
    static double defaultSpeed = .50;
    static double turboSpeed = 1.00;
    static double sniperSpeed = .25;
    double speed = defaultSpeed; 
    
    private static final Drive instance = new Drive();
    private Drive() {}  //Prevents other classes from declaring new Drive()
    
    public static Drive getInstance() {
        return instance;
    }  
    
    RobotDrive mechanum = new RobotDrive(frontLeftMotor,rearLeftMotor, frontRightMotor, rearRightMotor);


    /**************************
     * Controls robot movement
     **************************/
    
    public void drive(double driveLX, double driveRX, double driveLY){ //these are the correct variable names.....
        mechanum.setSafetyEnabled(false);
        
        //Sets the motor speeds; Negative variables on the second two inputs must be negative in order ensure non-inverted movement
        mechanum.mecanumDrive_Cartesian(driveLX * speed, -driveRX * speed, -driveLY * speed, 0); 
    }
    
    //allows the robot to adjust speed based on button presses
    public void setSpeed(boolean sniper, boolean turbo){
        
        if(turbo && !sniper){
            speed = turboSpeed; //In turbo mode
        } else if(!turbo && sniper){
            speed = sniperSpeed; //In sniper mode
        } else {
            speed = defaultSpeed; //In default mode
        }
        
    }
}
