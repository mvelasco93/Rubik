/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rubik;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author asus
 */
public class AcercaController implements Initializable {

    /**
     * Initializes the controller class.
     */

        
    @FXML
    private Label label;
    
    @FXML
    private void regresar(ActionEvent event) throws IOException {   
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("RubikInterface.fxml"));    
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    
    
}
