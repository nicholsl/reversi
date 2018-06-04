/* By Josh Gerstein and Liz Nichols, 6/3/2018.
 * The BoardView consists of nodes that are GameSpaces. GameSpaces change color based on their state
 * State information comes from the model
 */
package edu.carleton.gersteinj.reversi;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class GameSpace extends Circle {

    enum State { UNPLAYABLE, BLACK, WHITE, PLAYABLE}
    GameSpace.State state;


    GameSpace(Content content){
        super(50,5,38, Color.valueOf("#4C934C"));

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


    // set state of circle
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

    //change visual attributes of circle based on state
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

}

