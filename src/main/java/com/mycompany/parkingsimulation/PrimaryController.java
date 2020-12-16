package com.mycompany.parkingsimulation;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class PrimaryController {

    @FXML
    ImageView carTest;
    @FXML
    AnchorPane canvas;

    
    Image carImg;
    
    @FXML
    private void startSimulation() {
       
        canvas.getChildren().clear();
       Engine engine = new Engine("motor",carTest, canvas);
       engine.start();
    }
}



