package puzzles.clock;

import solver.Configuration;

import java.util.*;

/**
 * Implementation of configuration abstraction for the BFS solver algorithm
 * This class contains the logic necessary to compute neighbors of itself, other ClockConfigs
 * @author: Damon Gonzalez
 */
public class ClockConfig implements Configuration {
    /** The value of this config */
    private final int hour;
    /** A reference to the puzzle being solved */
    private final Clock puzzle;

    /**
     * Public constructor for ClockConfig to initialize a configuration with the
     * hour hand and Clock object as variables
     * @param hour The current hour of the clock
     * @param puzzle A reference to the puzzle object, type Clock
     */
    public ClockConfig(int hour, Clock puzzle){
        this.hour = hour;
        this.puzzle = puzzle;
    }

    /**
     * A ClockConfig is a solution if its hour is equal to the puzzle's end
     * @return True if this is a solution, False otherwise
     */
    @Override
    public boolean isSolution() {
        return hour == puzzle.getSolution();
    }

    /**
     * A ClockConfig always has two neighbors, one is one step counter clockwise from hour, the other
     *  is one step clockwise from hour
     * @return An array of length 2 that contains this's neighbors
     */
    @Override
    public List<Configuration> getNeighbors() {
        int leftHour, rightHour;
        if(hour == 1) leftHour = puzzle.getHours();
        else leftHour = hour - 1;
        if(hour == puzzle.getHours()) rightHour = 1;
        else rightHour = hour + 1;
        return new ArrayList<>(Arrays.asList(new ClockConfig(leftHour, puzzle), new ClockConfig(rightHour, puzzle)));
    }

    /**
     * Implemented so that ClockConfigs can be uniquely hashable in a HashMap
     * @param other The object being compared
     * @return True if the hours are the same, False otherwise
     */
    @Override
    public boolean equals(Object other){
        if(other instanceof ClockConfig)
            return this.hour == ((ClockConfig) other).hour;
        return false;
    }

    /**
     * Implemented so that ClockConfigs can be uniquely hashable in a HashMap
     * @return The hash code of the instance variable hours
     */
    @Override
    public int hashCode(){
        return Integer.hashCode(hour);
    }

    /**
     * Implemented so that a ClockConfig can be displayed when a solution is found
     * @return The data for this config
     */
    @Override
    public String toString() { return "" + hour; }
}
