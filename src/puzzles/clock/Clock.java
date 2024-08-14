package puzzles.clock;

import solver.*;
import java.util.*;

/**
 * Main class for the "clock" puzzle.
 *
 * @author YOUR NAME HERE
 */
public class Clock {
    /** The amount of hours in the clock */
    protected final int hours;
    /** The starting hours hand of the clock */
    protected final int start;
    /** The ending hours hand of the clock, i.e. the solution */
    protected final int end;

    /**
     * Public constructor that creates a Clock object to solve a clock puzzle
     * @param hours The hours
     * @param start The start
     * @param end The end
     */
    public Clock(int hours, int start, int end){
        this.hours = hours;
        this.start = start;
        this.end = end;
    }

    /** Getter for the solution */
    protected int getSolution() { return end; }

    /** Getter for Clock puzzle specifications */
    protected int getHours() { return hours; }

    /**
     * Returns nothing. Calls the BFS algorithm to retrieve the shortest path and
     * prints it out when done.
     */
    private void solve(){
        System.out.println("Hours: " + hours + ", Start: " + start + ", End: " + end);
        Configuration startConfig = new ClockConfig(start, this);
        Solver solver = new Solver();
        List<Configuration> path = solver.BFS(startConfig);
        System.out.println("Total Configs: " + solver.getTotalConfigs());
        System.out.println("Unique Configs: " + solver.getUniqueConfigs());
        if(path.size() == 0) System.out.println("No solution");
        for(int i = 0; i < path.size(); i++)
            System.out.println("Step " + i + ": " + path.get(i));
    }

    /**
     * Run an instance of the clock puzzle.
     * @param args [0]: number of hours on the clock;
     *             [1]: starting time on the clock;
     *             [2]: goal time to which the clock should be set.
     */
    public static void main( String[] args ) {
        if ( args.length != 3 ) {
            System.out.println("Usage: java Clock hours start end");
        }
        else {
            int hours = Integer.parseInt(args[0]);
            int start = Integer.parseInt(args[1]);
            int end = Integer.parseInt(args[2]);
            Clock puzzle = new Clock(hours, start, end);
            puzzle.solve();
        }
    }
}
