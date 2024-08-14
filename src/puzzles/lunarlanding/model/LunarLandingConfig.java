package puzzles.lunarlanding.model;

import puzzles.clock.ClockConfig;
import puzzles.lunarlanding.LunarLanding;
import solver.Configuration;
import util.Coordinates;
import util.Grid;
import util.Coordinates.Direction;

import java.io.*;
import java.security.spec.RSAOtherPrimeInfo;
import java.util.*;

/**
 * The lunarLandingConfig represents and manipulates a board
 * for the game of LunarLanding
 * @author George Banacos
 * November 2021
 */
public class LunarLandingConfig implements Configuration {

    /**
     * A blank character
     */
    private final Character blank = '_';

    /**
     * The spot that the explorer has to reach
     */
    private Coordinates goalSpot;

    /**
     * The locations of the robots on the board. The Character that represents the robot is a key
     * and the coordinates are the value
     */
    private Map<Character, Coordinates> robotLocations = new HashMap<>();

    /**
     * The board is represented by a Grid object
     */
    private Grid<Character> board;

    /**
     * LunarLandingConfig is the constructor which takes a file name and
     * creates a boards and initializes everything else associated with the board
     * @param fileName: The name of the file which the information to initialize
     *                the board is located
     */
    public LunarLandingConfig(String fileName) throws FileNotFoundException{
        try(BufferedReader reader = new BufferedReader(new FileReader(fileName))){ //try with resources
            String line = reader.readLine(); //First line set up
            String[] boardArgs = line.split(" ");
            board = new Grid<Character>(blank, Integer.parseInt(boardArgs[0]), Integer.parseInt(boardArgs[1]) );
            goalSpot = new Coordinates( Integer.parseInt(boardArgs[2]), Integer.parseInt( boardArgs[3]));
            while (!( line = reader.readLine()).equals("")){ //For each robot on the board
                String[] robotArgs = line.split("\\s++");
                Coordinates c = new Coordinates(Integer.parseInt(robotArgs[1]), Integer.parseInt(robotArgs[2]));
                board.set(robotArgs[0].charAt(0), c); //Put the robots on the board
                robotLocations.put(robotArgs[0].charAt(0), c); //Put the robots in the hashmap
            }
        }catch(IOException e){
            System.out.println("File could not be opened");
            throw new FileNotFoundException("sadf");
        }
    }

    /**
     * A copy constructor for the LunarLandingConfig
     * @param l: the original lunar landing config
     * @param robotLocations: The locations of the robots on the board (this was
     *                        just created in the method calling the copy consturctor)
     */
    public LunarLandingConfig(LunarLandingConfig l, HashMap<Character, Coordinates> robotLocations){
        Grid<Character> board = new Grid<Character>(blank, l.board.getNRows(), l.board.getNCols());
        for(Character c : robotLocations.keySet()){ //Put the robots on the board
            board.set(c, robotLocations.get(c));
        }
        this.board = board;
        this.goalSpot = l.goalSpot;
        this.robotLocations = robotLocations;
    }

    /**
     * This is a public method that allows a LunarLandingConfig to use the private
     * method movePiece without having to use getNeighbors.
     * @param coords: the coordinates that the robot is at
     * @param direction: The direction that the robot is trying to be used in
     * @return: False if it isn't a valid move, true otherwise.
     */
    public Coordinates movePiece(Coordinates coords, int direction){
        Character robot = board.get(coords);
        Coordinates directionDelta;
        directionDelta = Direction.values()[direction].coords;
        Coordinates newCoords = movePiece(coords, directionDelta);
        if (newCoords == null){
            return null;
        }
        else{
            robotLocations.put(robot, newCoords);
            board.set(blank, coords);
            board.set(robot, newCoords);
            return newCoords;
        }
    }

    /**
     * This method checks to see if there is a robot at the location specified by the row and column
     * @param row: The row selected
     * @param col: the column selected
     * @return: True if there is a robot at the location, false otherwise
     */
    public boolean robotAtLocation(int row, int col){
        return !board.get(row, col).equals(blank);
    }

    /**
     * Checks if this configuration is the solution
     * @return: True if it is the solution, false otherwise
     */
    @Override
    public boolean isSolution() {
        if (board.get(goalSpot.row(),goalSpot.col()).equals('E')){
            return true;
        }
        return false;
    }

    /**
     * Gets the neighbors of the current configuration
     * @return an arraylist of neighbors
     */
    @Override
    public List<Configuration> getNeighbors() {
        ArrayList<Configuration> neighbors = new ArrayList<>();
        for (Character robot : robotLocations.keySet()) { //For each robot on the board
            Coordinates currentRobot = robotLocations.get(robot);
            for (Direction d : Coordinates.CARDINAL_NEIGHBORS) { //For N, E, S, W
                Coordinates movedPiece = movePiece(currentRobot, d.coords); //Move the piece
                if (movedPiece == null){

                }
                else if ( movedPiece.row() == currentRobot.row() && movedPiece.col() == currentRobot.col() ){

                }
                else{ //If the piece moved and it is valid
                    HashMap<Character, Coordinates> newLocations = new HashMap<>(); //Creates a new hashmap with the new location
                    for (Character c : robotLocations.keySet()){
                        newLocations.put(c, robotLocations.get(c));
                        newLocations.put(robot, movedPiece);
                    }
                    Configuration neighbor = new LunarLandingConfig(this, newLocations );//Get the neighbor
                    neighbors.add(neighbor); //Add the neighbor
                }
            }
        }
        return neighbors;
    }

    /**
     * Converts the board to toString
     * @return a board
     */
    @Override
    public String toString() {
        String result = "    ";

        String[] boardResult = new String[board.getNRows()]; //Creates an array of each row of the board
        for (int r = 0; r < board.getNRows(); r++){
            String rowResult = "";
            for (int c = 0; c < board.getNCols(); c++){
                if (goalSpot.col() == c && goalSpot.row() == r && !board.get(r,c).equals(blank)){
                    rowResult += " !" + board.get(r,c);
                }
                else{
                    rowResult += "  " + board.get(r, c);
                }
            }
            boardResult[r] = rowResult;
        }

        for (int c = 0; c < board.getNCols(); c++){ //Top numbers
            result += "  " + c;
        }
        result += "\n    ";
        for (int c = 0; c < board.getNCols(); c++){ //Top dash
            result += "___";
        }
        result += "\n";
        for(int r = 0; r < board.getNRows(); r++){ //side numbers and the row of the board
            result += r + "  |" + boardResult[r] + "\n";
        }
        return result;
    }

    /**
     * Check to see if two configurations are equal based on the location of the robots
     * @param other: the other configuration
     * @return true if they are equal, false otherwise
     */
    @Override
    public boolean equals(Object other){
        if(other instanceof LunarLandingConfig otherConfig) {
            for (Character c : robotLocations.keySet()) {
                if(!otherConfig.robotLocations.get(c).equals(this.robotLocations.get(c))){
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Hash code based on the board
     * @return an int
     */
    @Override
    public int hashCode(){
        return this.board.hashCode();
    }

    /**
     * Moves each piece
     * @param startingCoords: the coordinates that the robot starts at
     * @param d: the direction the robot it trying to be moved
     * @return: the new coords of the robot or null because it was invalid
     */
    private Coordinates movePiece(Coordinates startingCoords ,Coordinates d){
        Coordinates currentCoords = startingCoords;
        while (board.legalCoords(currentCoords.sum(d)) && board.get(currentCoords.sum(d)).equals(blank)){
            currentCoords = currentCoords.sum(d);
        }
        if(!board.legalCoords(currentCoords.sum(d))){
            return null;
        }
        else if (!board.get(currentCoords.sum(d)).equals(blank)){
            return currentCoords;
        }
        else{
            System.out.println("Something went wrong!");
            return null;
        }
    }

    /**
     * Gets the height of the board
     * @return board height
     */
    public int getHeight(){
        return board.getNRows();
    }
    /**
     * Gets the length of the board
     * @return board length
     */
    public int getLength(){
        return board.getNCols();
    }
    /**
     * Gets the locations of all the robots
     * @return a dictionary of robot locations
     */
    public Coordinates getGoal(){
        return goalSpot;
    }
    /**
     * Gets the locations of all the robots
     * @return a dictionary of robot locations
     */
    public Map<Character, Coordinates> getRobotLocations(){
        return robotLocations;
    }
}