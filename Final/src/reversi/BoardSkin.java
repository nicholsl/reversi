package edu.carleton.gersteinj.reversi;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.*;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;



/**
 * Board state will be kept track of with an 8x8x3 matrix - columns, rows, content
 */
public class BoardSkin extends GridPane {

    public static final int SQUARE_SIZE = 100;
    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;


    public BoardSkin(){
        super();
        getStyleClass().add("game-grid");

        for (int i = 0; i < WIDTH; i++){
            ColumnConstraints column = new ColumnConstraints(100);
            getColumnConstraints().add(column);

        }

        for(int i = 0; i < HEIGHT; i++) {
            RowConstraints row = new RowConstraints(100);
            getRowConstraints().add(row);
        }

        setStyle("-fx-background-color: blue; -fx-grid-lines-visible: true");


    }






//
//    Group root = new Group();
//    Scene s = new Scene(root, 300, 300, Color.BLACK);
//
//    Rectangle rect = new Rectangle(25,25,250,250);


    //root.getChildren().add(r);



//    Boardskin(Board board);
//
//    private final Square[][] squares = new Square[8][8];
//
//    getStyleClass().add("board");
//
//    for(int i = 0; i < 8; i++) {
//        for (int j = 0; j < 8; j++) {
//            add(board.getSquare(i, j).getSkin(), i, j);
//        }
//    }
}

