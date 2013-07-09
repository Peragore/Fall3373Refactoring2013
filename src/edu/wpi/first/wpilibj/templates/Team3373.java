/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.DriverStationLCD.Line;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.lang.Math;
import edu.wpi.first.wpilibj.IterativeRobot;
//import edu.wpi.first.wpilibj.RobotDrive;
//import edu.wpi.first.wpilibj.SimpleRobot;
//import edu.wpi.first.wpilibj.templates.Shooter;
/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SimpleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory. 
 */
public class Team3373 extends SimpleRobot{
    /**
     * This function is called once each time the robot enters autonomous mode.
     */

   DriverStationLCD LCD = DriverStationLCD.getInstance();
   //SmartDashboard smartDashboard;
   SuperJoystick driveStick = new SuperJoystick(1); 
   SuperJoystick shooterController = new SuperJoystick(2);
   
   //Deadband objDeadband = new Deadband();
   Timer robotTimer = new Timer();
   PickupArm arm = new PickupArm();
   Elevator elevator = Elevator.getInstance();
   Shooter objShooter = Shooter.getInstance();
   Camera camera = new Camera();
   DigitalInput frontBackSwitch = new DigitalInput(13);
   DigitalInput leftRightSwitch = new DigitalInput(12);
   CameraControl cameraControl = new CameraControl(); //TODO: Fix camera PWM 
   double rotateLimitMaximum = 4.8;//are these used?
   double rotateLimitMinimum = 0.2;//are these used?
   Drive drive = Drive.getInstance();
   Deadband deadband = new Deadband();
   NewMath newMath = new NewMath();
   TableLookUp lookUp = new TableLookUp();
   boolean test;
   boolean solenidFlag=false;
   
  /*********************************
   * Math/Shooter Action Variables *
   *********************************/
   
   TableLookUp objTableLookUp = new TableLookUp();
   
   double ShooterSpeedStage2 = 0;//was StageTwoTalon.get()
   double percentageScaler = 0.75;
   double ShooterSpeedStage1 = ShooterSpeedStage2 * percentageScaler;//was StageOneTalon.get()
   
   double ShooterSpeedMax = 5300.0;
   double ShooterSpeedAccel = 250;
   double stageOneScaler = .5; //What stage one is multiplied by in order to make it a pecentage of stage 2
   double PWMMax = 1; //maximum voltage sent to motor
   double MaxScaler = PWMMax/5300;
   double ShooterSpeedScale = MaxScaler * ShooterSpeedMax; //Scaler for voltage to RPM. Highly experimental!!
   double target;
   double RPMModifier = 250;
   double idle = 1 * ShooterSpeedScale;
   double off = 0;
   double change;
   
   double startTime = 9000000;
   double backTime = 90000000;
   double aTime = 900000000;
   double bTime = 900000000;
   double targetRotatePosition;
   boolean manualToggle;
   double manualStatus;
   boolean armTestFlag;
   boolean canShoot;
   int LX = 1;
   int LY = 2;
   int Triggers = 3;
   int RX = 4;
   int RY = 5;
   int DP = 6;
   double rotateTest = 2.7;
   double autonomousSpeedTarget = 1;
   boolean autonomousElevateFlag = true;
   double feedAngle = 2.8;
   double climbAngle = 3.105;
   double autoTarget;
   double[] targetSlot;
   double[] targetAngle;
   
   //double climbingPosition = 2.75;
   boolean controlFlag = true;
           
   public Team3373(){
      //camera.robotInit();
    }
   
   /********************************
    * Beginning of Autonomous Code *
    ********************************/
   
   public void autonomous() {
        cameraControl.moveTest(0); //moves camera to an upward position in prep for teleop
        if (isAutonomous() && isEnabled()){
            elevator.canRun = true;
            camera.canRun = true;
            objShooter.canRun = true;                
            
            autoPositionChecker();
            autoElevateToShoot();
            autoSmoothShooterAccel();
            autoShoot();
        }

    }
   
    //checks whether the robot is at the front or back of the pyramid
    public void autoPositionChecker() {
        if (frontBackSwitch.get()){ //further away, right side, value returned is also feed/climb position
            autoTarget = lookUp.lookUpAngle(18, lookUp.distanceHigh, lookUp.angleHigh);
        } 
        else { //close, right
            autoTarget = lookUp.lookUpAngle(10, lookUp.distanceHigh, lookUp.angleHigh);
        }
    }
    //brings elevator to shooting position in Autonomous
    public void autoElevateToShoot(){ 
        while (autonomousElevateFlag) {
            try {
                Thread.sleep(10L);
            } 
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            
            if (autoTarget > 0) {
                elevator.goTo(autoTarget);
            }
            
            if (!elevator.elevateFlag) {
                autonomousElevateFlag = false;
            }
      }
    }
    
    //smoothely accelerates the shooter wheel to max speed over a period of time
    public void autoSmoothShooterAccel() {
        objShooter.goToSpeed(autonomousSpeedTarget*.33);

        try {
            Thread.sleep(300L);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        objShooter.goToSpeed(autonomousSpeedTarget * .66);
        try {
            Thread.sleep(300L);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        objShooter.goToSpeed(autonomousSpeedTarget);
        
        //waits for the speed to stabilize
        try {
            Thread.sleep(1500L);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    //Shoots the frisbee in autonomous three times
    public void autoShoot() {
        for (int i = 0; i <= 2; i++){
            objShooter.shoot();
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            objShooter.loadFrisbee(elevator);
            try {
                Thread.sleep(1500);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
    

   

    

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void disabled(){
        if (isDisabled()){
            elevator.createDataSet();
        }
        while (isDisabled()){ //resetting variables and flags
            camera.distanceTimeOut = 0;
            manualToggle = false;
            armTestFlag = false;
            arm.demoOnFlag = false;
            targetRotatePosition = arm.pot1.getVoltage(); 
            arm.demoStatus = 0;
            elevator.elevationTarget = elevator.angleMeter.getVoltage();
            cameraControl.servoTarget = .79;
            objShooter.busyStatus = true;
            camera.distanceFlag = false;
            controlFlag = true;
            autonomousElevateFlag = true;
            objShooter.stageOneTalon.set(0);
            objShooter.stageTwoTalon.set(0);
            camera.distFlag = false;
            elevator.elevatorTalon1.set(0);
            elevator.elevatorTalon2.set(0);
            elevator.canRun = false;
            camera.canRun = false;
            objShooter.canRun = false;
            elevator.elevateFlag = true;
        }
    }
    
   /**********************************
    * Beginning of Teleoperated Code *
    **********************************/
    public void operatorControl() {
            
            teleopInit();
            
            while (isOperatorControl() & isEnabled()){
                //TODO: Test if actually does anything whatsoever 
                try {
                     Thread.sleep(10L);
                 } catch (InterruptedException ex) {
                     ex.printStackTrace();
                 }

                 //Resets the internal toggle flags when a previously pushed button has been released
                 driveStick.clearButtons();
                 shooterController.clearButtons();

                 outputToSmartDashboard();
                 
                 //shooter code
                 teleopDistanceFinding();
                 teleopShootAndElevateControl();

                 teleopDrive();

                 teleopFeedFrisbees();

                 teleopClimb();

                 //Testing for string Potentiometer
                 double stringPotVoltage;
                 stringPotVoltage = elevator.stringPot.getVoltage();
                 System.out.println(stringPotVoltage);

        }

    }  
    
    public void teleopInit() {
        camera.distFlag = true;
        robotTimer.start();

        if (isEnabled()){
            elevator.canRun = true;
            camera.canRun = true;
            objShooter.canRun = true;
        }
    }
    
    //prints to smart dashboard
    public void outputToSmartDashboard() {
        SmartDashboard.putNumber("Target Voltage: ", elevator.shootTarget);
        SmartDashboard.putNumber("Loop Count", elevator.whileCount);
        SmartDashboard.putBoolean("leftRightSwitch: ", leftRightSwitch.get());
        SmartDashboard.putBoolean("frontBackSwitch: ", frontBackSwitch.get());
        SmartDashboard.putNumber("Distance: ", camera.middle_distance);
        SmartDashboard.putBoolean("LowerLimt", elevator.lowerLimit.get());
        SmartDashboard.putString("Looking at: ", "high distance");
        SmartDashboard.putString("Looking at: ", "middle distance");
        SmartDashboard.putString("Looking at: ", "middle distance");
        SmartDashboard.putNumber("Voltage", elevator.angleMeter.getVoltage());
        SmartDashboard.putNumber("Target Voltage: ", lookUp.lookUpAngle(camera.middle_distance, targetSlot, targetAngle));
        SmartDashboard.putNumber("Current Voltage: ", elevator.currentAngle);
        SmartDashboard.putBoolean("Shooting: ", objShooter.busyStatus);
        SmartDashboard.putNumber("Current Voltage: ", elevator.currentAngle);
        SmartDashboard.putBoolean("Shooting: ", objShooter.busyStatus);
        SmartDashboard.putNumber("Servo: ", cameraControl.cameraServo.get());
        LCD.updateLCD();
    }
    
    //automatically finds distance from camera
    public void teleopDistanceFinding() {
        cameraControl.moveTest(-shooterController.getRawAxis(LY));

        if (shooterController.isStartPushed()){
            camera.findDist();
        }

        if (shooterController.isYPushed()){ //says the shooter is aiming at high
            targetSlot = lookUp.distanceHigh;
            targetAngle = lookUp.angleHigh;
        } 
        else if (shooterController.isXPushed()){ //says the shooter is aiming at middle
            targetSlot = lookUp.distanceMiddle;
            targetAngle = lookUp.angleMiddle;  
        }
    }
    
    //aims and fires frisbee on button presses
    public void teleopShootAndElevateControl(){
        if (shooterController.isBackPushed()){ //elevates the shooter to correct angle on button press
            controlFlag = true;
            elevator.canRun = true;
            elevator.elevateFlag = true;
            elevator.goTo(lookUp.lookUpAngle(camera.middle_distance, targetSlot, targetAngle));    
        }

        if (shooterController.isAPushed() && objShooter.busyStatus){ //shoots the frisbee  
            objShooter.shooterThread();
        }

        if (shooterController.isBPushed()){ //decelerates the shooting 
            objShooter.goToSpeed(.25);
        }
        
        if (driveStick.isAPushed()){//added option to got to elevation of autonomuos 
            controlFlag = true;
            elevator.canRun = true;
            elevator.elevateFlag = true;
            elevator.goTo(lookUp.lookUpAngle(18, lookUp.distanceHigh, lookUp.angleHigh));
        }
    }
    
    //controls everything related to making the robot move
    public void teleopDrive() {
        drive.setSpeed(driveStick.isLBHeld(), driveStick.isRBHeld()); //sniper/turbo
        drive.drive( //controls driving
                    newMath.toTheThird(deadband.zero(driveStick.getRawAxis(LX), 0.1)), 
                    newMath.toTheThird(deadband.zero(driveStick.getRawAxis(RX), 0.1)), 
                    newMath.toTheThird(deadband.zero(driveStick.getRawAxis(LY), 0.1))
                   );
    }
    
    //takes shooter to feed height and feeds
    public void teleopFeedFrisbees() {
        if (driveStick.isBPushed()) {
            elevator.canRun = true;
            elevator.elevateFlag = true;
            elevator.goTo(feedAngle);
        }

        if (driveStick.isBackPushed()){
           controlFlag = true;
           objShooter.loadFrisbee(elevator);
        }        
    }
    
    //takes shooter deck to climb height and climbs
    public void teleopClimb() {
        if(driveStick.isXPushed()){
            controlFlag = true;
            elevator.canRun = true;
            elevator.elevateFlag = true;
            elevator.goTo(climbAngle);

        }

        if(driveStick.isYPushed()){
            elevator.canRun = true;
            controlFlag = false;
        }

       if (!controlFlag){
            elevator.lower();
            if (elevator.lowerLimit.get()){
                elevator.off();
                controlFlag = true;
            }
        }        
    }
    
    
    public void test() {
        
        while (isEnabled() && isTest()) {
            if (shooterController.isRBHeld()){
                elevator.raise();
            } else if (shooterController.isLBHeld()){
               elevator.lower(); 
            } else {
                elevator.off();
            }
            teleopDrive();
        
        }   
    }

}




