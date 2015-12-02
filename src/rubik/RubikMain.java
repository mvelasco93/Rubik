/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rubik;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.jpl.games.model.Moves;
import com.jpl.games.model.Rubik;
import java.io.IOException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author Administrator
 */
public class RubikMain extends Application {
    
    private Stage primaryStage;
    private BorderPane rootLayout;
    private BorderPane RubikInterface;
    private Scene scene;
    
    public  Rubik rubik=new Rubik();
    public Moves moves=new Moves();
    
    public LocalTime time=LocalTime.now();
    public Timeline timer;
    
    public final StringProperty clock = new SimpleStringProperty("00:00:00");
    public final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Cubo Rubik");     
    
        initRootLayout();
        showRubikInterface();
        
        
    }
  
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(RubikMain.class.getResource("RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            scene = new Scene(rootLayout);           
            
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void showRubikInterface() {
        try {
            // Load  overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(RubikMain.class.getResource("RubikInterface.fxml"));
            RubikInterface = (BorderPane) loader.load();
        
            rootLayout.setCenter(RubikInterface);
            RubikInterface.setCenter(rubik.getSubScene());
            
            RubikInterface.getChildren().stream().filter(withMoveButtons())
                    .forEach(n->{
                        Button b=(Button)n;
                        b.setOnAction(e->rotateFace(b.getText()));
                        b.hoverProperty().addListener((ov,b0,b1)->updateArrow(b.getText(),b1));
                    });

            rubik.isOnRotation().addListener((b0,b1,b2)->{
                if(b2){
                // store the button hovered 
                RubikInterface.getChildren().stream().filter(withToolbars())
                    .forEach(tb->{
                        ((ToolBar)tb).getItems().stream().filter(withMoveButtons().and(isButtonHovered()))
                            .findFirst();
                    });
            } 
            });
            
            scene.addEventHandler(MouseEvent.ANY, rubik.eventHandler);
            scene.cursorProperty().bind(rubik.getCursor());
  
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void rotateFace(final String btRot){
        RubikInterface.getChildren().stream()
            .filter(withToolbars())
            .forEach(tb->{
                ((ToolBar)tb).getItems().stream()
                    .filter(withMoveButtons().and(withButtonTextName(btRot)))
                    .findFirst().ifPresent(n->this.rubik.isHoveredOnClick().set(((Button)n).isHover()));
            });
        this.rubik.rotateFace(btRot);
    }
    
    public void ScrambleCube(){
        this.rubik.doScramble();
        this.rubik.isOnScrambling().addListener((ov,v,v1)->{
            if(v && !v1){
                System.out.println("Revuelto!");
                moves=new Moves();
            }
        });
    }
    
    public void doReplay(){
        RubikInterface.getChildren().stream().filter(withToolbars()).forEach(setDisable(true));
        this.rubik.doReplay(moves.getMoves());
        this.rubik.isOnReplaying().addListener((ov,v,v1)->{
            if(v && !v1){
                System.out.println("replayed!");
                RubikInterface.getChildren().stream().filter(withToolbars()).forEach(setDisable(false));
            }
        });
    }
    
    private void updateArrow(String face, boolean hover){
        rubik.updateArrow(face,hover);
    }
  
    private static Predicate<Node> withToolbars(){
        return n -> (n instanceof ToolBar);
    }
    private static Predicate<Node> withMoveButtons(){
        return n -> (n instanceof Button) && ((Button)n).getText().length()<=2;
    }
    private static Predicate<Node> withButtonTextName(String text){
        return n -> ((Button)n).getText().equals(text);
    }
    private static Predicate<Node> isButtonHovered(){
        return n -> ((Button)n).isHover();
    }
    private static Consumer<Node> setDisable(boolean disable){
        return n -> n.setDisable(disable);
    }
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    public Rubik getRubik(){
        return rubik;
    }
    public static void main(String[] args) {
        launch(args);
    }
    
}
