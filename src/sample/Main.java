package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader =  new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("RPG Talker");
        primaryStage.setScene(new Scene(root, 800, 500));
        Controller mycontroller = loader.getController();
        mycontroller.stage=primaryStage;
        primaryStage.show();
    }

    public static void main(String[] args) {
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
        launch(args);
    }
}
