/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import android.telecom.RemoteConference;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;



import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import java.util.List;

/**
 * Demonstrates empty OpMode
 */
@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name="Autonomous", group="Auto")

public class Autonomous extends LinearOpMode {
  RobotHardware robot = new RobotHardware(this);
  private ElapsedTime runtime = new ElapsedTime();
  private ElapsedTime autonomousTime = new ElapsedTime();
  private boolean failsafeTrigger = true;

  private static final String TFOD_MODEL_ASSET = "PowerPlay.tflite";
  // private static final String TFOD_MODEL_FILE  = "/sdcard/FIRST/tflitemodels/CustomTeamModel.tflite";


  private static final String[] LABELS = {
          "1 Bolt",
          "2 Bulb",
          "3 Panel"
  };

  /*
   * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
   * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
   * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
   * web site at https://developer.vuforia.com/license-manager.
   *
   * Vuforia license keys are always 380 characters long, and look as if they contain mostly
   * random data. As an example, here is a example of a fragment of a valid key:
   *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
   * Once you've obtained a license key, copy the string from the Vuforia web site
   * and paste it in to your code on the next line, between the double quotes.
   */
  private static final String VUFORIA_KEY =
          "AYEGaiX/////AAABmUea0mDnR0RNvu01w0v1potz5t0OsEI5/FtWgJ7IijUSEu7bsbjQjG+IhQln+fPGGX4bbGvlIu1Rl5EySR5yrvgQOyRp9oHIAISVSzPcIsl20XmxKl9lBRZgsUhN30HIk62jtUMfzCtyyiva5HeKYIWGEsESWOQarWDurOda/X+71dPGDZxcFe4DKz9mGYoH6C7HuzpGy3uRrhA70kBpwhlljBAvtmYP/TJ44RqPBN42h8/AQn3f3KndjOpyiUJhtcL5mpayUdu3/W+V+CQI6Rj905YL79em/XGV+lg7fO126rBwGm2DkypnX8qB5WYm+Jgmcqeh+yFfQYESeaGyrFKRSWeoO5mlQLUXKHiv/VIq";

  /**
   * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
   * localization engine.
   */
  private VuforiaLocalizer vuforia;

  /**
   * {@link #tfod} is the variable we will use to store our instance of the TensorFlow Object
   * Detection engine.
   */
  private TFObjectDetector tfod;
  String labelExists;


  @Override
  public void runOpMode() {

    robot.init();
    waitForStart();
    initVuforia();
    initTfod();
    if (tfod != null) {
      tfod.activate();

      // The TensorFlow software will scale the input images from the camera to a lower resolution.
      // This can result in lower detection accuracy at longer distances (> 55cm or 22").
      // If your target is at distance greater than 50 cm (20") you can increase the magnification value
      // to artificially zoom in to the center of image.  For best results, the "aspectRatio" argument
      // should be set to the value of the images used to create the TensorFlow Object Detection model
      // (typically 16/9).
      tfod.setZoom(1.0, 16.0/9.0);
    }


   if(opModeIsActive() && autonomousTime.seconds() < 10.0) {
     while(opModeIsActive() && autonomousTime.seconds() < 10.0) { //program will cut out after 10 seconds
       if (tfod != null) {
         List <Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
         if(updatedRecognitions != null) {
           for(Recognition recognitions : updatedRecognitions) {
              labelExists = recognitions.getLabel(); //grabs a label from the updatedRecognitions
              if(labelExists != null){ //detects if the string has a label in it
                telemetry.addData("The label exists", "moving onto movement");
                telemetry.update();
                break; //breaks out of for loop
              }
           }
           if (labelExists != null)
           {

             if (labelExists.equals("1 Bolt"))  //moves the robot up and to the left if tflow reads "1 Bolt"
             {
               //use robotHardware to move up and to the left
               robot.driveMecanum(0.4);
               runtime.reset();
               while(opModeIsActive() && runtime.seconds() < 1.2) //makes robot drive forward for 2.5 seconds
               {
                 telemetry.addData("Step 1", "Current runtime: " + runtime.seconds() + "/1");
                 telemetry.update();
               }
               //robot.resetWheels();
               robot.stop();
               robot.strafeMecanum(0.4);//positive values moves left
               runtime.reset();
               while(opModeIsActive() && runtime.seconds() < 1.5)
               {
                 telemetry.addData("Step 2", "Current runtime: " + runtime.seconds() + "/1.5");
                 telemetry.update();
               }
               robot.stop();
               failsafeTrigger = false;
               break;
               //robot.resetWheels();
             }
             // Moves the robot straight forward if tflow reads "2 Bulb"
             else if (labelExists.equals("2 Bulb")) { //needs else if
               //use robotHardware to move up
               robot.driveMecanum(.4);
               runtime.reset();
               while(opModeIsActive() && runtime.seconds()<1){
                 telemetry.addData("Step 1", "Current runtime: "+ runtime.seconds()+"/1");
                 telemetry.update();
               }
               //robot.resetWheels();
               robot.stop();

               telemetry.addData("Robot Status:", "Complete");
               telemetry.update();
               failsafeTrigger = false;
               break;
             }
             //moves the robot up and to the right if tflow reads "3 Panel"
             else if (labelExists.equals("3 Panel")) {
               //use robotHardware to move up and to the right
               robot.driveMecanum(.4);
               runtime.reset();
               while(opModeIsActive() && runtime.seconds() < 1.2){
                 telemetry.addData("Step 1", "Current runtime: " + runtime.seconds() + "/1.2");
                 telemetry.update();
               }
               robot.stop();
               //robot.resetWheels();
               robot.strafeMecanum(-0.4); //negative moves right
               runtime.reset();
               while(opModeIsActive() && runtime.seconds() < 1.2)
               {
                 telemetry.addData("Step 2", "Current runtime: " + runtime.seconds() + "/1.5");
                 telemetry.update();
               }
               robot.stop();
               failsafeTrigger = false;
               break;
             }

           }

         }
       }
     }
   }

   if (failsafeTrigger) {
     robot.driveMecanum(.4);
     runtime.reset();
     while(runtime.seconds()<1){
       telemetry.addData("Step 1", "Current runtime: "+ runtime.seconds()+"/1");
       telemetry.update();
     }
     //robot.resetWheels();
     robot.stop();

     telemetry.addData("Robot Status:", "Complete");
     telemetry.update();
     failsafeTrigger = false;
   }

    sleep(1000);

  }
  private void initVuforia() {
    /*
     * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
     */
    VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

    parameters.vuforiaLicenseKey = VUFORIA_KEY;
    parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

    //  Instantiate the Vuforia engine
    vuforia = ClassFactory.getInstance().createVuforia(parameters);
  }

  /**
   * Initialize the TensorFlow Object Detection engine.
   */
  private void initTfod() {
    int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
            "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
    TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
    tfodParameters.minResultConfidence = 0.75f;
    tfodParameters.isModelTensorFlow2 = true;
    tfodParameters.inputSize = 300;
    tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);

    // Use loadModelFromAsset() if the TF Model is built in as an asset by Android Studio
    // Use loadModelFromFile() if you have downloaded a custom team model to the Robot Controller's FLASH.
    tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
    // tfod.loadModelFromFile(TFOD_MODEL_FILE, LABELS);
  }


}
