/* By Josh Gerstein and Liz Nichols, 6/3/2018.
 * Model is the part of the model that stores the current state of a game, along with the full sequence of moves that
 * have been played in that game.
 */

package edu.carleton.gersteinj.reversi;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main class of REVERSI
 */
public class Main extends Application {

    private Parent createContent() {
        Pane root = new Pane();
        root.setPrefSize(600, 600);

        return root;
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("reversi.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Reversi");


        Controller controller = loader.getController();
        //BoardView theboard = new BoardView();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        controller.boardView.setOnMousePressed(controller);
        controller.undo.setOnMousePressed(controller);

    }


    public static void main(String[] args) {
        launch(args);
    }
}
