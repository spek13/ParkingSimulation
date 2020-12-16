/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.parkingsimulation;
import java.util.ArrayList;
import java.util.concurrent.*; 
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class Engine extends Thread {
   
    ImageView carTemplate;
    AnchorPane canvas;
    Semaphore door;
    Semaphore parking;
    int contadorautos =0;
  
    ArrayList<Car> cars;
    int[] parkingSpaces;
    double parkingJumps;
    
    public Engine(String name,  ImageView carTemplate, AnchorPane canvas) {
       super(name);
       this.carTemplate = carTemplate;
       this.canvas = canvas;
       //Semaforos
       this.door = new Semaphore(1);
       this.parking = new Semaphore(Utils.parkingSpaces);//10

       this.parkingSpaces = new int[Utils.parkingSpaces];

       for (int i = 0; i < Utils.parkingSpaces; i++) {
           this.parkingSpaces[i] = -1;
       }
       cars = new ArrayList<Car>();
       this.parkingJumps = (Utils.finalParkingPointX - Utils.startParkingPointX) / Utils.parkingSpaces;
    }
   


    //lugares disponibles
    public int getParkingAvailable(int id) {
        int index = -1;
         for (int i = 0; i < Utils.parkingSpaces; i++) {
           if (this.parkingSpaces[i] == -1) {
               this.parkingSpaces[i] = id;
               index = i;
               i = Utils.parkingSpaces;
           }   
        }
       return index;
    }
    //liberar lugares
    public void releaseParking(int id){
     for (int i = 0; i < Utils.parkingSpaces; i++) {
           if (this.parkingSpaces[i] == id) {
               this.parkingSpaces[i] = -1;
               i = Utils.parkingSpaces;

           }   
        }
    }
    //encolar autos
   void createCar() {
        contadorautos++;
       Image img = carTemplate.getImage();
       ImageView view = new ImageView(img);
       view.setFitWidth(62);
       view.setFitHeight(35);
        this.canvas.getChildren().add(view);
       Car car  = new Car("CAR",this.door, this.parking, view, this, this.cars.size());

       System.out.println("Auto encolado  " + contadorautos);
       cars.add(car);
       car.start();
   } 
   
   public boolean isNearestToNextCarInX(int carId) {
       boolean nearest = false;
       if (carId - 1 >= 0) {
        Car car1 = this.cars.get(carId);
        Car car2 = this.cars.get(carId - 1);
        double resta = car2.getX() - car1.getX();
        double resta2 = Math.abs( car2.getY() - car1.getY());

        nearest = ((Double.compare(0, resta) < 0) && (Double.compare(resta, Utils.stopDistanceX) < 0) && (Double.compare(resta2, 10) < 0));
       } 
       return nearest;
   }
   
      public boolean isNearestToNextCarInY(int carId) {
       boolean nearest = false;
       if (carId - 1 >= 0) {
        Car car1 = this.cars.get(carId);
        Car car2 = this.cars.get(carId - 1);
        double resta = car2.getY() - car1.getY();
        double resta2 = Math.abs( car2.getX() - car1.getX());

        nearest = ((Double.compare(0, resta) < 0) && (Double.compare(resta, Utils.stopDistanceY) < 0) && (Double.compare(resta2, 10) < 0));
       } 
       return nearest;
   }
    public double getParkingX(int parkingId) {
        double jumps = parkingId  * this.parkingJumps;
        return Utils.finalParkingPointX - jumps;
    }
   
           
           
    Runnable updater = new Runnable() {
        @Override
        public void run() {
         createCar();
        }
    };
   @Override
   public void run() {
       boolean alive = true;
       int i = 0;
    try {
        while (alive) {
            boolean spawn = canSpawn();
            
            if  (spawn && i < Utils.totalCars) {
                Platform.runLater(updater);
                Thread.sleep(Utils.spawnTime); 
                i++;
            } if ( i > Utils.totalCars) {
                alive = false;
            }  else {
                Thread.sleep(1000);
            }
          
        }
    }catch (InterruptedException exc) {
          
    }
   }
   
   boolean canSpawn(){
       int size = this.cars.size();
       boolean spawn = true;
       
        if (size > 0) {
           Car car = this.cars.get(size-1);
           double x = car.getX();
           return !(Double.compare(x, 50) < 0);
       }
       
       for (int i = 0; i < size; i++) {
           Car car = this.cars.get(i);
           if (car.isWaiting() && car.getCurrenState() == 2) {
               spawn = false;
               i = size;
           }
       
       }
       return spawn;
   }
}
