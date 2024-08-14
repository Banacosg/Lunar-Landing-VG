package puzzles.tipover.model;

import solver.Configuration;
import solver.Solver;
import util.Observer;
import util.Coordinates;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * The Model used to keep track of the data for this puzzle, and all behaviors necessary for interacting
 * with any type of user-interface.
 * @author Damon Gonzalez
 * November 2021
 */
public class TipOverModel {
    /** The current Configuration in this model */
    private TipOverConfig currentConfig = null;
    /** A counter variable to keep track of how many steps the user has performed on this attempt */
    private int steps;
    /** The name of the file from where the last successful puzzle load was from */
    private String lastFilename = "";
    /** A list of observers to this model */
    private final List<Observer<TipOverModel, Object>> observers = new LinkedList<>();

    /**
     * Loads a new puzzle into this model given a filename and using safeLoad(), then notifies observers
     * @param filename The file of the initial configuration of the puzzle
     */
    public void load(String filename){
        safeLoad(filename);
        steps = 0;
        notifyObservers("");
    }

    /**
     * Loads the same puzzle as loaded last, effectively resets the currentConfig back to its original value, then notifies observers
     */
    public void reload(){
        load(lastFilename);
    }

    /**
     * Loads a new puzzle by setting the currentConfig the TipOverConfig initializer's constructor. Does
     *  not call notifyObservers to avoid an exception at the start of the program
     * @param filename The file of the initial configuration of the puzzle
     */
    public void safeLoad(String filename){
        if(!filename.equals("")) {
            try {
                currentConfig = new TipOverConfig(filename);
                lastFilename = filename;
            } catch (IOException exception) {
                System.out.println("Unable to find:" + filename);
            }
        }
    }

    /**
     * Used to cheat and show the user the next step in the shortest path from the current Configuration to the solution. If
     * there is no solution, then the user is not told and a valid move is chosen at random.
     */
    public void cheat(){
        if(currentConfig != null) {
            Solver solver = new Solver();
            List<Configuration> path = solver.BFS(currentConfig);
            if (path.size() > 1) {
                currentConfig = (TipOverConfig) path.get(1);
            } else {
                List<Configuration> neighbors = currentConfig.getNeighbors();
                if (neighbors.size() != 0) {
                    currentConfig = (TipOverConfig) neighbors.get((int) (currentConfig.getNeighbors().size() * Math.random()));
                }
            }
            steps++;
        }
        notifyObservers("");
    }

    /**
     * Replaces the current Configuration with one moved by the tipper in a specified direction
     * @param direction The direction given by the user
     */
    public void move(String direction){
        if(currentConfig != null) {
            List<Configuration> neighbors = currentConfig.getNeighbors();
            Coordinates requestedMove = getCoordinates(direction);
            for (Configuration neighbor : neighbors) {
                Coordinates neighborTipperLocation = ((TipOverConfig) neighbor).getTipperLocation();
                if (neighborTipperLocation.equals(requestedMove)) {
                    currentConfig = (TipOverConfig) neighbor;
                    steps++;
                    notifyObservers("");
                    return;
                }
            }
            notifyObservers(direction);//IF THE DIRECTION WAS INVALID
        }
        else {
            notifyObservers("");
        }
    }

    /**
     * Private helper method used to help determine if a move requested by the user can legally be performed
     * @param direction The direction which the user wants the tipper to go, either NORTH, SOUTH, EAST, WEST
     * @return A Coordinates object representing the location of the tipper if it were to move in the specified direction,
     *  if no valid direction is given it returns a Coordinates object with values row: -1 and col: -1.
     */
    private Coordinates getCoordinates(String direction){
        Coordinates tipperLocation = currentConfig.getTipperLocation();
        switch (direction.toUpperCase()){
            case "NORTH" -> {
                return new Coordinates(tipperLocation.row() - 1, tipperLocation.col());
            }
            case "SOUTH" -> {
                return new Coordinates(tipperLocation.row() + 1, tipperLocation.col());
            }
            case "WEST" -> {
                return new Coordinates(tipperLocation.row(), tipperLocation.col() - 1);
            }
            case "EAST" -> {
                return new Coordinates(tipperLocation.row(), tipperLocation.col() + 1);
            }
            default -> {
                return new Coordinates(-1, -1);//BY DEFINITION INVALID COORDINATES IF NO PROPER DIRECTION IS GIVEN
            }
        }
    }

    /**
     * Public getter for the current Configuration
     * @return returns the TipOverConfig that represents the current
     */
    public TipOverConfig getCurrentConfig() { return currentConfig; }

    /**
     * Public getter for steps
     * @return An int value of the amount of steps the user has taken on this puzzle
     */
    public int getNumSteps() { return steps; }

    /**
     * Adds an object of type Observer to the list of observers held in this model
     * @param o The observer to be added
     */
    public void addObserver(Observer o){
        observers.add(o);
    }

    /**
     * Notifies all observers if there is a change within the model
     * @param data Extra data given to the observers if necessary
     */
    private void notifyObservers(String data){
        for(Observer observer : observers)
            observer.update(this, data);
    }

    /**
     * Small scope method that is used by the UI to call update on itself
     */
    public void go(){
        notifyObservers("");
    }
}
