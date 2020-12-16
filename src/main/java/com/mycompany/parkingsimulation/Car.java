/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.parkingsimulation;

import java.util.Random;
import java.util.concurrent.Semaphore;
import javafx.application.Platform;
import javafx.scene.image.ImageView;

public class Car extends Thread {

   int id;
   int contador =0;
  Engine engine;
  ImageView carImage;
  int state = 0;
  boolean waiting = false;
  Semaphore door;
  Semaphore parking;
  String name;
  double y, x;
  int degrees =0;
  boolean updatedegrees = false;
  int parkingSpace;
  double parkingPoint;
  
  public int getCurrenState() {
      return this.state;
  }
  public boolean isWaiting() {
      return this.waiting;
  }
  
  public double getX() {
      return this.x;
  }
   public double getY() {
      return this.y;
  }
    Runnable updater = new Runnable() {
        @Override
        public void run() {
            if (updatedegrees) {
                updatedegrees = false;
                carImage.setRotate(degrees);
            }
            switch(state) {
                case 0:
                case 8:
                case 12:
                case 19:
                case 23:
                case 25:
                case 29:
                case 31:
                    carImage.setTranslateX(x);
                    break;
                case 5:
                case 3:
                case 10:
                case 14:
                case 17:
                case 21:
                case 27:
                    carImage.setTranslateY(y);
                    break;
            }
        }
    };
  
  public Car(String name, Semaphore door,  Semaphore parking,ImageView carImage, Engine engine, int id) {
      super(name);
     
      this.engine = engine;
      this.name = name;
      this.door = door;
      this.parking = parking;
      this.carImage = carImage;
      this.id = id;
  }   
  
    @Override
   public void run() {
       boolean alive = true;
      try {
          
         //estados
        while(alive) {
            boolean execute = true;
            if (this.state < 3 && engine.isNearestToNextCarInX(id) ) {
                execute = false;
             
            } else  if (this.state < 7 && engine.isNearestToNextCarInY(id) ) {
                execute = false;
              
            }
            
            
            if (execute)
            switch(this.state) {
                case 0:
                    {
                        x = this.carImage.getTranslateX() + Utils.carSpeed;
                        Platform.runLater(updater);
                        if (x > Utils.midleX) {
                            state = 1;

                        }

                    }
                break;
                case 1:
                   degrees = 90;
                   updatedegrees = true;
                   Platform.runLater(updater);
                   state = 2;
                break;
                case 2: 
                  
                    this.waiting = true;
                    this.parking.acquire();
                    this.waiting = false;
                    state = 3;
                
                break;
                case 3: 
                    {
                        y = this.carImage.getTranslateY() + Utils.carSpeed;
                        Platform.runLater(updater);
                        if (y > Utils.outsideY) {
                            state = 4;
                        }
                    }
                break;
                case 4:



                  //esperando para entrar
                    this.waiting = true;
                    this.door.acquire();

                    System.out.println("Entra auto ");
                    this.waiting = false;
                    state = 5;






                break;
                 case 5: 
                   {
                        y = this.carImage.getTranslateY() + Utils.carSpeed;
                        Platform.runLater(updater);
                         if (y > Utils.insideY) {
                            state = 6;
                        }
                    }
                break;
                 case 6: 
                  //bloquear salida
                    this.door.release();
                    state = 7;
                break;
                 case 7:
                     
                     
                    degrees = 0;
                    updatedegrees = true;
                    Platform.runLater(updater);
                    state = 8;
                     break;
                 case 8:
                      x = this.carImage.getTranslateX() + Utils.carSpeed;
                        Platform.runLater(updater);
                        if (x > Utils.finalParkingPointX) {
                            state = 9;
                        }
                     break;
                 case 9:
                     degrees = 90;
                    updatedegrees = true;
                    Platform.runLater(updater);
                    state = 10;
                  break;
                case 10:
                    y = this.carImage.getTranslateY() + Utils.carSpeed;
                    Platform.runLater(updater);
                    if (y > Utils.finalParkingPointY) {
                        state = 11;
                     }
                 break;
                 case 11:
                     this.parkingSpace = this.engine.getParkingAvailable(id);
                    this.parkingPoint = this.engine.getParkingX(this.parkingSpace);
                     degrees = 180;
                    updatedegrees = true;
                    Platform.runLater(updater);
                    state = 12;
                  break;
                 case 12:
                      x = this.carImage.getTranslateX() - Utils.carSpeed;
                        Platform.runLater(updater);
                        if (x < this.parkingPoint) {
                            state = 13;
                        }
                     break;
                 case 13:
                      degrees = 90;
                    updatedegrees = true;
                    Platform.runLater(updater);
                    state = 14;
                     break;
                 case 14:
                    y = this.carImage.getTranslateY() + Utils.carSpeed;
                    Platform.runLater(updater);
                    if (y > Utils.parkingPointY) {
                        state = 15;
                     }
                 break;
                 case 15: 
                     Random ran = new Random();
                     int time = (ran.nextInt(50) + 1) * 1000;
                     //int time = (ran.nextInt(5) + 1) * 1000;
                     //System.out.println("Dormido");
                     Thread.sleep(time);
                     state = 16;
                     break;
                 case 16:
                      degrees = 270;
                    updatedegrees = true;
                    Platform.runLater(updater);
                    state = 17;
                     break;
                 case 17:
                    y = this.carImage.getTranslateY() - Utils.carSpeed;
                    Platform.runLater(updater);
                    if (y < Utils.finalParkingPointY) {
                        state = 18;
                     }
                    break;
                case 18:
                    this.engine.releaseParking(id);
                    degrees = 180;
                    updatedegrees = true;
                    Platform.runLater(updater);
                    state = 19;
                  break;
                case 19:
                      x = this.carImage.getTranslateX() - Utils.carSpeed;
                        Platform.runLater(updater);
                        if (x < Utils.startParkingPointX) {
                            state = 20;
                        }
                     break;
                case 20:
                     degrees = 270;
                    updatedegrees = true;
                    Platform.runLater(updater);
                    state = 21;
                  break;
               
                 case 21:
                    y = this.carImage.getTranslateY() - Utils.carSpeed;
                    Platform.runLater(updater);
                    if (y < Utils.insideY) {
                        state = 22;
                     }
                 break;
                 case 22:
                    degrees = 0;
                    updatedegrees = true;
                    Platform.runLater(updater);
                    state = 23;
                  break;
                  case 23: 
                    {
                        x = this.carImage.getTranslateX() + Utils.carSpeed;
                        Platform.runLater(updater);
                        if (x > (Utils.midleX / 2)) {
                            state = 24;
                        }
                    }
                break;
                case 24:







                  
                    this.waiting = true;
                    this.door.acquire();
                    contador ++;
                    System.out.println("Saliendo auto "+ contador);
                    this.waiting = false;
                    state = 25;







                break;
                case 25: 
                    {//salida de coche velocidad
                        x = this.carImage.getTranslateX() + Utils.carSpeed;
                        Platform.runLater(updater);
                        if (x > (Utils.midleX)) {
                            state = 26;
                        }
                    }
                break;
                case 26:
                    degrees = 270;
                    updatedegrees = true;
                    Platform.runLater(updater);
                    state = 27;
                 break;
                case 27:
                    y = this.carImage.getTranslateY() - Utils.carSpeed;
                    Platform.runLater(updater);
                    if (y < Utils.midleY) {
                        state = 28;
                     }
                 break;
                case 28:
                    degrees = 180;
                    updatedegrees = true;
                    Platform.runLater(updater);
                    state = 29;
                 break;
                case 29: 
                    {
                        x = this.carImage.getTranslateX() - Utils.carSpeed;
                        Platform.runLater(updater);
                        if (x < ((Utils.midleX / 2) + 50)) {
                            state = 30;
                        }
                    }
                break;
                case 30: 
                   
                    this.door.release();
                    System.out.println("boot");
                    this.parking.release();
                    state = 31;
                break;
                case 31: 
                    {
                        x = this.carImage.getTranslateX() - Utils.carSpeed;
                        Platform.runLater(updater);
                        if (x < -80) {
                            alive = false;
                        }
                    }
                break;
                
               
            }
            Thread.sleep(100);  
        }
   
      }catch (InterruptedException exc) {
          System.out.println("Fallo en hilo");
      }
  
   
   }
    

 
 
 
}
