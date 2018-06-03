package edu.carleton.gersteinj.reversi;

// Stores the history of a completed game.

import java.io.*;
import java.util.ArrayList;
import java.util.List;

class History implements Serializable {
    private List<MoveSequence> matches;

    History(){
        matches = new ArrayList<>();
    }

    void addMatch(MoveSequence match){
        matches.add(match);
    }

    /**
     * Used https://www.tutorialspoint.com/java/java_serialization.htm for reference. If any errors occur in attempting
     * to deserialize the file, returns a new, empty History instance.
     * @param filename: location of file to deserialize
     * @return deserialized History object
     */
    static History load(String filename) {
        FileInputStream fileIn;
        try {
            fileIn = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new History();
        }
        try {
            ObjectInputStream in = new ObjectInputStream(fileIn);
            return (History) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new History();
        }
    }

    void save(String filename) {
        FileOutputStream fileOut;
        ObjectOutputStream out;
        try {
            fileOut = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            // Print entire contents to console to help prevent loss
            for (MoveSequence match : matches){
                System.out.println(match.toString());
            }
            return;
        }
        try {
            out = new ObjectOutputStream(fileOut);
            out.writeObject(this);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
