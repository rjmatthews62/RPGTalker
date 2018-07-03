package au.com.mithril.rpgtalker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.util.Properties;

public class Main extends Application {
    public static final String configFile = "rpgtalker.cfg";
    public static RobProperties config = new RobProperties();

    @Override
    public void start(Stage primaryStage) throws Exception{
        try {
            config.load(new InputStreamReader(new FileInputStream(configFile),"UTF-8"));
        } catch (Exception e) {
            // Ignore error.
        }
        FXMLLoader loader =  new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("RPG Talker");
        primaryStage.setScene(new Scene(root, 800, 500));
        Controller mycontroller = loader.getController();
        mycontroller.stage=primaryStage;
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        try {
            config.store(new OutputStreamWriter(new FileOutputStream(configFile), "UTF-8"), "RPG Talker Configuration.");
        } catch (Exception e) {
            // Ignore error.
        }
        super.stop();
    }

    public static void main(String[] args) {
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
        launch(args);
    }
}
