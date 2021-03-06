/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rubik;

import com.jpl.games.model.Move;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.jpl.games.model.Moves;
import com.jpl.games.model.Rubik;
import com.mvm.games.records.Record;
import com.mvm.sql.Java2MySql;
import javafx.scene.image.Image;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.BorderPane;
import javafx.scene.input.MouseEvent;
import javax.naming.NamingException;

/**
 *
 * @author Administrator
 */
public class RubikMain extends Application {
    
    private Stage primaryStage;
    private BorderPane rootLayout;
    private BorderPane RubikInterface;
    private Scene scene;
    
    public static Rubik rubik=new Rubik();
    public static Moves moves=new Moves();
    
    public LocalTime time=LocalTime.now();
    public static Timeline timer = new Timeline();
    
    public final StringProperty clock = new SimpleStringProperty("00:00:00");
    public final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());
    
    public static ObservableList<Record> recordData = FXCollections.observableArrayList();
    public static Connection actualDB;
    public static Java2MySql instanciaDB = new Java2MySql();

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Cubo Rubik");     
        
        Image applicationIcon = new Image(getClass().getResourceAsStream("rubik_s_cube.png"));
        this.primaryStage.getIcons().add(applicationIcon);
        initRootLayout();
        showRubikInterface();  
        ChangeListener<Number> clockLis=(ov,l,l1)->clock.set(LocalTime.ofNanoOfDay(l1.longValue()).format(fmt));

    }
  
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            String css = RubikMain.class.getResource("RubikInterface.css").toExternalForm();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(RubikMain.class.getResource("RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();    

            // Show the scene containing the root layout.
            scene = new Scene(rootLayout);           
            scene.getStylesheets().clear();
            scene.getStylesheets().add(css);
            scene.cursorProperty().bind(rubik.getCursor());
            scene.addEventHandler(MouseEvent.ANY, rubik.eventHandler);
            primaryStage.setScene(scene);         
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void showRubikInterface() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(RubikMain.class.getResource("RubikInterface.fxml"));
            RubikInterface = (BorderPane) loader.load();
            
            rootLayout.setCenter(RubikInterface);
            RubikInterface.setCenter(rubik.getSubScene());
                      
            ChangeListener<Number> clockLis=(ov,l,l1)->clock.set(LocalTime.ofNanoOfDay(l1.longValue()).format(fmt));

            rubik.isOnReplaying().addListener((ov,b,b1)->{
                if(b&&!b1){
                    rubik.getTimestamp().removeListener(clockLis);
                    if(!rubik.isSolved().get()){
                        timer.play();
                    }
                }
            });
                       
            rubik.getLastRotation().addListener((ov,v,v1)->{
                if(!rubik.isOnReplaying().get() && !v1.isEmpty()){
                moves.addMove(new Move(v1, LocalTime.now().minusNanos(time.toNanoOfDay()).toNanoOfDay()));
                }
            });
            
            rubik.getCount().addListener((ov,v,v1)->{
                int moves = v1.intValue()+1;
            });
            
            rubik.getLastRotation().addListener((ov,v,v1)->{
                if(!rubik.isOnReplaying().get() && !v1.isEmpty()){
                    moves.addMove(new Move(v1, LocalTime.now().minusNanos(time.toNanoOfDay()).toNanoOfDay()));
                }
            });
        
            scene.addEventHandler(MouseEvent.ANY, rubik.eventHandler);
            scene.cursorProperty().bind(rubik.getCursor());
  
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void rotateFace(final String btRot){
        /*RubikInterface.getChildren().stream()
            .filter(withToolbars())
            .forEach(tb->{
                ((ToolBar)tb).getItems().stream()
                    .filter(withMoveButtons().and(withButtonTextName(btRot)))
                    .findFirst().ifPresent(n->rubik.isHoveredOnClick().set(((Button)n).isHover()));
            });*/
        rubik.rotateFace(btRot);
    }
    
    public void ScrambleCube(){
        rubik.doScramble();
        rubik.isOnScrambling().addListener((ov,v,v1)->{
            if(v && !v1){
                System.out.println("Revuelto!");
                moves=new Moves();
            }
        });
    }
    
    public void SolveCube(){
        if(moves.getNumMoves()!=0){
            rubik.doSolve(moves);
            rubik.isOnReplaying().addListener((ov,v,v1)->{
            if(v && !v1){
                System.out.println("Resuelto!");               
            }
        });
        
        }
        
    }
    
    public void ReplayCube(){
        rubik.doReplay(moves.getMoves());
        rubik.isOnReplaying().addListener((ov,v,v1)->{
            if(v && !v1){
                System.out.println("replayed!");               
            }
        });
    }
    
    public static ObservableList<Record> getRecordData(){
        return recordData;
    }
    
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    public Rubik getRubik(){
        return rubik;
    }
    public static void main(String[] args) throws NamingException, SQLException {
        
        actualDB = instanciaDB.openConnection();
        
        
        if(actualDB != null){
            System.out.println("Successful connection");
            recordData = Java2MySql.loadData(actualDB);
         
        }
        
        launch(args);
    }
    
}
