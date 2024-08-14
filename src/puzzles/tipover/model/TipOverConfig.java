package puzzles.tipover.model;

import solver.Configuration;
import util.Coordinates;

import java.io.*;
import java.util.*;

/**
 * Public Configuration class for the TipOver puzzle that holds all the state necessary for representing
 *  a configuration, and holds all the methods necessary for a Breadth First Search Algorithm.
 * @author Damon Gonzalez
 * November 2021
 */
public class TipOverConfig implements Configuration{
    /** The grid of squares that holds the value of the configuration */
    private char[] grid;
    /** The amount of rows, does not change */
    private static int rows;
    /** The amount of columns in each row, does not change */
    private static int cols;
    /** The location of the goal crate, does not change */
    private static Coordinates goal;
    /** The location of the tipper of this config */
    private Coordinates tipperLocation;

    /**
     * Public constructor for a TipOverConfig that is meant to be used to generate
     *  an initial configuration of a puzzle, given a filename
     * @param filename The name of the file that holds the puzzle
     */
    public TipOverConfig(String filename) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(filename));
        String line = in.readLine();
        String[] values = line.split("\\s++");
        rows = Integer.parseInt(values[0]);
        cols = Integer.parseInt(values[1]);
        grid = new char[rows * cols];
        tipperLocation = new Coordinates(Integer.parseInt(values[2]), Integer.parseInt(values[3]));
        goal = new Coordinates(Integer.parseInt(values[4]), Integer.parseInt(values[5]));
        int index = 0;
        while(!(line = in.readLine()).equals("")){
            String[] row = line.split("\\s++");
            for(String square : row) {
                grid[index] = square.charAt(0);
                index++;
            }
        }
    }

    /**
     * Copy constructor for this class
     * @param other The other TipOverConfig to be copied from
     */
    private TipOverConfig(TipOverConfig other){
        this.grid = Arrays.copyOf(other.grid, other.grid.length);
        this.tipperLocation = new Coordinates(other.tipperLocation.row(), other.tipperLocation.col());
    }

    /**
     * A configuration is a goal if its tipper location is equal to its goal crate location
     * @return True if the configuration is a solution to the puzzle, False otherwise
     */
    @Override
    public boolean isSolution() {
        return goal.equals(tipperLocation);
    }

    @Override
    public List<Configuration> getNeighbors() {//UPDATE THIS SO THAT THE TIPPER CAN MOVE FROM A TOWER(GREATER THAN '1') TO ANOTHER TOWER WITHOUT TIPPING OVER
        List<Configuration> neighbors = new ArrayList<>();
        char tipperSquare = get(tipperLocation);
        Coordinates NORTH = new Coordinates(tipperLocation.row() - 1, tipperLocation.col());
        if(tipperLocation.row() > 0 && get(NORTH) != '0'){
            TipOverConfig neighbor = new TipOverConfig(this);
            neighbor.tipperLocation = NORTH;
            neighbors.add(neighbor);
        }
        Coordinates SOUTH = new Coordinates(tipperLocation.row() + 1, tipperLocation.col());
        if(tipperLocation.row() + 1 < rows && get(SOUTH) != '0'){
            TipOverConfig neighbor = new TipOverConfig(this);
            neighbor.tipperLocation = SOUTH;
            neighbors.add(neighbor);
        }
        Coordinates WEST = new Coordinates(tipperLocation.row(), tipperLocation.col() - 1);
        if(tipperLocation.col() > 0 && get(WEST) != '0'){
            TipOverConfig neighbor = new TipOverConfig(this);
            neighbor.tipperLocation = WEST;
            neighbors.add(neighbor);
        }
        Coordinates EAST = new Coordinates(tipperLocation.row(), tipperLocation.col() + 1);
        if(tipperLocation.col() + 1 < cols && get(EAST) != '0'){
            TipOverConfig neighbor = new TipOverConfig(this);
            neighbor.tipperLocation = EAST;
            neighbors.add(neighbor);
        }
        if(tipperSquare != '1')    {
            int towerLength = Integer.parseInt(String.valueOf(tipperSquare));
            Coordinates toNORTH = new Coordinates(tipperLocation.row() - towerLength, tipperLocation.col());
            if(tipperLocation.row() + 1 > towerLength && isClear(NORTH, toNORTH)){
                TipOverConfig neighbor = new TipOverConfig(this);
                neighbor.towerFall(NORTH, toNORTH);
                neighbor.set(neighbor.tipperLocation, '0');
                neighbor.tipperLocation = NORTH;
                neighbors.add(neighbor);
            }
            Coordinates toSOUTH = new Coordinates(tipperLocation.row() + towerLength, tipperLocation.col());
            if(tipperLocation.row() + towerLength < rows && isClear(SOUTH, toSOUTH)){
                TipOverConfig neighbor = new TipOverConfig(this);
                neighbor.towerFall(SOUTH, toSOUTH);
                neighbor.set(neighbor.tipperLocation, '0');
                neighbor.tipperLocation = SOUTH;
                neighbors.add(neighbor);
            }
            Coordinates toWEST = new Coordinates(tipperLocation.row(), tipperLocation.col() - towerLength);
            if(tipperLocation.col() + 1 > towerLength && isClear(WEST, toWEST)){
                TipOverConfig neighbor = new TipOverConfig(this);
                neighbor.towerFall(WEST, toWEST);
                neighbor.set(neighbor.tipperLocation, '0');
                neighbor.tipperLocation = WEST;
                neighbors.add(neighbor);
            }
            Coordinates toEAST = new Coordinates(tipperLocation.row(), tipperLocation.col() + towerLength);
            if(tipperLocation.col() + towerLength < cols && isClear(EAST, toEAST)){
                TipOverConfig neighbor = new TipOverConfig(this);
                neighbor.towerFall(EAST, toEAST);
                neighbor.set(neighbor.tipperLocation, '0');
                neighbor.tipperLocation = EAST;
                neighbors.add(neighbor);
            }
        }
        return neighbors;
    }

    /**
     * A TipOverConfig is equal to another if its grids are equal and its tipperLocation is equal
     * @param other The object being compared
     * @return True if the objects are completely equal, false otherwise
     */
    @Override
    public boolean equals(Object other){
        if(other instanceof TipOverConfig otherConfig){
            return this.tipperLocation.equals(otherConfig.tipperLocation)
                    && Arrays.equals(this.grid, otherConfig.grid);
        }
        return false;
    }

    /**
     * Gives a hash code that is necessary for the HashMap used in the solver
     * @return An integer hash value of the state of this grid
     */
    @Override
    public int hashCode(){
        return Arrays.hashCode(grid) + tipperLocation.hashCode();
    }

    /**
     * Represents this configuration as text symbols
     * @return String representation
     */
    @Override
    public String toString(){
        StringBuilder message = new StringBuilder("   ");
        for(int i = 0; i < cols; i++)
            message.append("  ").append(i);
        message.append("\n   ");
        message.append("___".repeat(Math.max(0, cols)));
        message.append("\n");
        int start = 0;
        for(int i = 0; i < rows; i++){
            message.append(i).append(" |");
            for(int j = start; j < start + cols; j++){
                Coordinates c = new Coordinates(i, j);
                message.append(" ");
                if(goal.equals(c))
                    message.append("!");
                else if(tipperLocation.equals(c))
                    message.append("*");
                else
                    message.append(" ");
                char square = get(c);
                if(square == '0') message.append("_");
                else message.append(square);
            }
            message.append("\n");
        }
        return message.toString();
    }

    /**
     * Private helper function to abstract configuration's relationship to the array of characters representing
     *  a two-dimensional grid. Sets a given location in grid to a specified new character; Guaranteed
     *  to be valid coordinates of grid
     * @param a The Coordinate object that represents its row, col location
     * @param c The char being set
     */
    private void set(Coordinates a, char c){
        int index = a.row() * cols + a.col();
        grid[index] = c;
    }

    /**
     * Public helper function to abstract configuration's relationship to the array of characters representing
     *  a two-dimensional grid. Returns a char at a given location in grid; Guaranteed
     *  to be valid coordinates of grid. It is public because the GUI must use is to get elements of the grid
     * @param a The Coordinate object that represents its row, col location
     * @return The char being gotten
     */
    public char get(Coordinates a){
        int index = a.row() * cols + a.col();
        return grid[index];
    }

    /**
     * Private helper function to determine if there is space between two coordinates(on the grid) for a tower to fall on. Guaranteed
     *  to be valid coordinates of grid and to be in line horizontally or vertically
     * @param from The coordinate of one end of the fallen tower
     * @param to The coordinate of the other end of the fallen tower
     * @return True if there is space, False otherwise
     */
    private boolean isClear(Coordinates from, Coordinates to){
        if(from.row() == to.row()){
            if(from.col() > to.col()){
                for(int i = to.col(); i <= from.col(); i++){
                    Coordinates current = new Coordinates(from.row(), i);
                    if(get(current) != '0') return false;
                }
            } else {
                for(int i = from.col(); i <= to.col(); i++){
                    Coordinates current = new Coordinates(from.row(), i);
                    if(get(current) != '0') return false;
                }
            }
        } else {
            if(from.row() > to.row()){
                for(int i = to.row(); i <= from.row(); i++){
                    Coordinates current = new Coordinates(i, from.col());
                    if(get(current) != '0') return false;
                }
            } else {
                for(int i = from.row(); i <= to.row(); i++){
                    Coordinates current = new Coordinates(i, from.col());
                    if(get(current) != '0') return false;
                }
            }
        }
        return true;
    }

    /**
     * Private helper function that is meant to simulate the tipping over of a tower, coordinates are provided
     *  for each end of the tower. In order to simulate this, the method sets all coordinates in grid from one parameter
     *  to the other, inclusive, to the char '1'.
     * @param from One end of the fallen tower
     * @param to The other end of the fallen tower
     */
    private void towerFall(Coordinates from, Coordinates to){
        if(from.row() == to.row()){
            if(from.col() > to.col()){
                for(int i = to.col(); i <= from.col(); i++){
                    Coordinates current = new Coordinates(from.row(), i);
                    set(current, '1');
                }
            } else {
                for(int i = from.col(); i <= to.col(); i++){
                    Coordinates current = new Coordinates(from.row(), i);
                    set(current, '1');
                }
            }
        } else {
            if(from.row() > to.row()){
                for(int i = to.row(); i <= from.row(); i++){
                    Coordinates current = new Coordinates(i, from.col());
                    set(current, '1');
                }
            } else {
                for(int i = from.row(); i <= to.row(); i++){
                    Coordinates current = new Coordinates(i, from.col());
                    set(current, '1');
                }
            }
        }
    }

    /**
     * Public getter for the location of the tipper
     * @return The location of the tipper, as a Coordinates object
     */
    public Coordinates getTipperLocation() { return tipperLocation; }

    /**
     * Public getter for the location of the goal
     * @return The location of the goal crate, as a Coordinates object
     */
    public static Coordinates getGoal() { return goal; }

    /**
     * Public getter for the amount of rows in this puzzle
     * @return The amount of rows of the grid in the puzzle
     */
    public static int getRows() { return rows; }

    /**
     * Public getter for the amount of columns in this puzzle
     * @return The amount of columns of the grid in the puzzle
     */
    public static int getCols() { return cols; }
}
