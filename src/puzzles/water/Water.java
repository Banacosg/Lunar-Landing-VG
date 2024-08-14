package puzzles.water;

import solver.*;
import java.util.*;

/**
 * Main class for the water buckets puzzle.
 *
 * @author Damon Gonzalez
 */
public class Water {
    /** The desired amount of water */
    private final int amount;
    /** The capacities of each bucket given to you */
    private final int[] bucketCapacities;

    /**
     * Public constructor to create an instance of Water class to solve
     * a water puzzle given the desired amount and bucket capacities
     * @param amount The amount
     * @param bucketCapacities The capacities
     */
    public Water(int amount, int[] bucketCapacities){
        this.amount = amount;
        this.bucketCapacities = bucketCapacities;
    }

    /** Getter for the solution */
    protected int getSolution() { return amount; }

    /** Getter for details specific to this Water puzzle */
    protected int[] getBucketCapacities() { return bucketCapacities; }

    /**
     * Returns nothing. Calls the BFS algorithm to retrieve the shortest path and
     * prints it out when done.
     */
    public void solve(){
        System.out.println("Amount: " + amount + ", Buckets: " + Arrays.toString(bucketCapacities));
        Configuration startConfig = new WaterConfig(new int[bucketCapacities.length], this);
        Solver solver = new Solver();
        List<Configuration> path = solver.BFS(startConfig);
        System.out.println("Total Configs: " + solver.getTotalConfigs());
        System.out.println("Unique Configs: " + solver.getUniqueConfigs());
        if(path.size() == 0) System.out.println("No solution");
        for(int i = 0; i < path.size(); i++)
            System.out.println("Step " + i + ": " + path.get(i));
    }


    /**
     * Run an instance of the water buckets puzzle.
     * @param args [0]: desired amount of water to be collected;
     *             [1..N]: the capacities of the N available buckets.
     */
    public static void main( String[] args ) {
        if ( args.length < 2 ) {
            System.out.println("Usage: java Water amount bucket1 bucket2 ...");
        }
        else {
            int amount = Integer.parseInt(args[0]);
            int[] bucketCapacities = new int[args.length - 1];
            for(int i = 1; i < args.length; i++)
                bucketCapacities[i - 1] = Integer.parseInt(args[i]);
            Water puzzle = new Water(amount, bucketCapacities);
            puzzle.solve();
        }
    }
}
