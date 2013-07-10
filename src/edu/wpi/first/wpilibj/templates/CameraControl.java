/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.*;

/**
 * Controls servo for camera
 * @author Philip
 */
public class CameraControl {

    Servo cameraServo = new Servo(9);
    double servoTarget = .79;
    static double servoMin = .6;
    static double servoMax = 1;
    static double deadbandValue = .2;

    //moves camera controlled by a servo
    public void moveCamera(double joystickValue){
        if (joystickValue > deadbandValue || joystickValue < -deadbandValue){    
            servoTarget += (joystickValue)/500;
        }
        
        if (servoTarget > 1) {
            servoTarget = 1;
        }
        else if (servoTarget < 0) {
            servoTarget = 0;
        }   
        else if (servoTarget > servoMin && servoTarget < servoMax){
            cameraServo.set(servoTarget);
        }
        
    }
}
