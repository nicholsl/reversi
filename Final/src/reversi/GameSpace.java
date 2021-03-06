/* By Josh Gerstein and Liz Nichols, 6/3/2018.
 * The BoardView consists of nodes that are GameSpaces. GameSpaces change color based on their state
 * State information comes from the model
 */
package edu.carleton.gersteinj.reversi;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

class GameSpace extends Circle {

    private Content content;
    private final Coordinates location;

    /*
    constructs game space using content, and a coordinates location object
     */
    GameSpace(Content content, Coordinates location){
        super(50,5,38, Color.valueOf("#4C934C"));
        this.location = location;
        this.content = content;
        changeCircleBasedOnState();

    }

    Coordinates getLocation() {
        return new Coordinates(location);
    }

    void setState(Content content){
        this.content = content;
    }

    private void changeCircleBasedOnState(){
        switch(content) {
            case UNPLAYABLE:
                setFill(Color.valueOf("#4C934C"));
                setRadius(38);
                break;
            case PLAYABLE:
                setFill(Color.valueOf("#EDC9FF"));
                setRadius(20);
                break;
            case WHITE:
                setFill(Color.valueOf("#FBFAF8"));
                setRadius(38);
                break;
            case BLACK:
                setFill(Color.valueOf("#0A122A"));
                setRadius(38);
                break;
        }
    }
    /*
    updates the content of a circle node
     */
    void update(Content content) {
        setState(content);
        changeCircleBasedOnState();
    }
}

