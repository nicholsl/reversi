package edu.carleton.gersteinj.reversi;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class GameSpace extends Circle {

    enum State { UNPLAYABLE, BLACK, WHITE, PLAYABLE}
//    String state
    GameSpace.State state;


    GameSpace(Content content){
        super(50,5,38, Color.valueOf("#4C934C"));

        // Circle circle = new Circle(10,Color.DARKGREEN);
        if (content == Content.PLAYABLE){
            this.state = State.PLAYABLE;
            this.setRadius(38);
        }
        else if (content == Content.WHITE){
            this.state = State.WHITE;
            this.setRadius(38);
        }
        else if (content == Content.BLACK){
            this.state = State.BLACK;
            this.setRadius(38);
        }
        else if (content == Content.UNPLAYABLE){
            this.state = State.UNPLAYABLE;
            this.setRadius(38);
        }

        if (this.state == State.UNPLAYABLE) {
            this.setFill(Color.valueOf("#4C934C"));
            this.setRadius(38);
        }
        if (this.state == State.PLAYABLE){
            this.setFill(Color.valueOf("#EDC9FF"));
            this.setRadius(5);
            Circle circle = new Circle(10,Color.valueOf("#EDC9FF"));
//            this.union(circle,this);
            //node
        }
        else if (this.state == State.WHITE){
            this.setFill(Color.valueOf("#FBFAF8"));
            this.setRadius(38);
        }
        else if (this.state == State.BLACK){
            this.setFill(Color.valueOf("#0A122A"));
            this.setRadius(38);
        }
//        ReadOnlyObjectWrapper<State> state = new javafx.beans.property.ReadOnlyObjectWrapper<>(State.EMPTY);



    }

    public void setState(Content content){
        if (content == Content.PLAYABLE){
            this.state = State.PLAYABLE;
        }
        else if (content == Content.WHITE){
            this.state = State.WHITE;
        }
        else if (content == Content.BLACK){
            this.state = State.BLACK;
        }
        else if (content == Content.UNPLAYABLE){
            this.state = State.UNPLAYABLE;
        }

    }

    public void changeCircleBasedOnState(){
        if (this.state == State.UNPLAYABLE) {
            this.setFill(Color.valueOf("#4C934C"));
            this.setRadius(38);
        }
        if (this.state == State.PLAYABLE){
            this.setFill(Color.valueOf("#EDC9FF"));
            this.setRadius(20);
        }
        else if (this.state == State.WHITE){
            this.setFill(Color.valueOf("#FBFAF8"));
            this.setRadius(38);
        }
        else if (this.state == State.BLACK){
            this.setFill(Color.valueOf("#0A122A"));
            this.setRadius(38);
        }
    }

    public void update(){
    }


//    public javafx.beans.property.ReadOnlyObjectProperty<State> stateProperty() {
//        return state.getReadOnlyProperty();
//    }
//    public State getState() {
//        return this.state()
//    }
//
//    private final Board board;
//
//    public GameSpace(Board board){
//        this.board = board;
//
//        skin = new SquareSkin(this);
//    }
//
    public void pressed(){

    }



}

