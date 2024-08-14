package puzzles.lunarlanding.model;

import solver.Configuration;
import solver.Solver;
import util.Coordinates;
import util.Observer;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The LunarLandingModel represents the game board. It is part of the MVC
 * model for user interfaces
 * @author George Banacos
 * November 2021
 */
public class LunarLandingModel{

    /**
     * A list of viewers observing the model
     */
    private List<Observer< LunarLandingModel, String > > subjects = new ArrayList<>();
    /**
     * The configuration of the lunarlandingconfig
     */
    private LunarLandingConfig config;
    /**
     * isChosen keeps track of if a robot is chosen
     */
    private boolean isChosen = false;
    /**
     * curRow keeps track of the row selected
     */
    private Coordinates currentCoords;
    /**
     * The solver that can solve the puzzle using BFS
     */
    private static Solver s = new Solver();
    /**
     * The name of the last file opened
     */
    private String latestFile;

    /**
     * The name of the current file being opened
     */
    private String currentFile;

    /**
     * Constructs a model object by creating a LunarLandingConfig
     * @param fileName: The name of the file that represents the board
     */
    public LunarLandingModel(String fileName){
        load(fileName);
    }

    /**
     * The reload method reloads the latest file loaded.
     */
    public void reload(){
        LunarLandingConfig oldConfig = config;
        try {
            config = new LunarLandingConfig(currentFile);
            latestFile = currentFile;
            announce("show");
            announce("loaded");
        }catch (FileNotFoundException e){
            config = oldConfig;
            currentFile = latestFile;
        }
    }

    /**
     * Loads a file describing a LunarLanding board
     * @param fileName: The name of the file being loaded
     */
    public void load(String fileName){
        currentFile = fileName;
        this.reload();
    }

    /**
     * Adds an observer to this model
     * @param o: The viewer of the model
     */
    public void addObserver(Observer< LunarLandingModel, String > o){
        subjects.add(o);
    }

    /**
     * Tries to select a robot at a certain row and column
     * @param row: A specific row
     * @param col: A specific column
     */
    public void selection(int row, int col){
        if(config.robotAtLocation(row, col)) { //If there is a robot at the location
            this.currentCoords = new Coordinates(row,col);
            this.isChosen = true;
            announce("selection");
            announce("space");
        }
        else{
            announce("No figure at that position");
        }
    }

    /**
     * Moves the robot if a robot is selected
     * @param direction: The direction that the robot tries to move
     */
    public void move(String direction){
        if (!isChosen){ //if a robot isn't chosen
            announce("Choose a character to move first");
        }
        else {
            int directionValue = switch (direction) { //Turns the direction into a number for the config.movePiece method
                case "north" -> 0;
                case "east" -> 2;
                case "south" -> 4;
                case "west" -> 6;
                default -> -1;
            };
            if (directionValue != -1) {
                Coordinates newCoords = config.movePiece(currentCoords, directionValue);
                isChosen = false;
                if (newCoords != null) {
                    announce("show");
                }
                else{
                    announce("Illegal move");
                }
            }
            else{
                announce("Illegal move");
            }
            if(config.isSolution()){
                announce("YOU WON!");
            }
        }
    }

    /**
     * Gets the next hint toward the goal. A solver is called on the config which
     * does a BFS search to find the path to the goal. Even if the user makes a
     * change to the state of the board the getHint will use the current board
     * state as a start of the path.
     */
    public void getHint(){
        List<Configuration> steps;
        steps = s.BFS(config);
        if(steps.size() == 0){
            announce("Unsolvable board");
        }
        else if(steps.size() == 1){//If there is only one step to the solution (the solution has been found)
            announce("Current board is already solved");
        }
        else{
            config = (LunarLandingConfig) steps.get(1); //Sets the next step config to the current config
            announce("show");
            if (config.isSolution()){
                announce("I WON!");
            }
        }
    }

    /**
     * Gets the height of the board
     * @return board height
     */
    public int getBoardHeight(){
        return config.getHeight();
    }

    /**
     * Gets the length of the board
     * @return board length
     */
    public int getBoardLength(){
        return config.getLength();
    }

    /**
     * Gets the coordinates of the goal spot
     * @return goal spot coordinates
     */
    public Coordinates getGoal(){ return config.getGoal(); }

    /**
     * Gets the locations of all the robots
     * @return a dictionary of robot locations
     */
    public Map<Character, Coordinates> getRobotLocations(){ return config.getRobotLocations(); }

    /**
     * Calls the toString of the config has the model's toString
     * @return config's toString
     */
    @Override
    public String toString(){
        return config.toString();
    }

    /**
     * Updates all of the subjects of this model that a change has happened
     * @param message: The message that is being sent.
     */
    private void announce(String message){
        for ( var obs : this.subjects){
            obs.update(this, message);
        }
    }
}
