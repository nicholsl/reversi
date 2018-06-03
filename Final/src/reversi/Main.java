package edu.carleton.gersteinj.reversi;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

    private Parent createContent() {
        Pane root = new Pane();
        root.setPrefSize(600,600);

        return root;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("reversi.fxml"));
        BoardSkin theboard = new BoardSkin();
        Scene scene = new Scene(theboard);
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
