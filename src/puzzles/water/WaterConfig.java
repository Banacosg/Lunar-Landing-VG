package puzzles.water;

import solver.*;

import java.util.*;

public class WaterConfig implements Configuration {
    /** The amounts in each of the buckets in this configuration */
    private final int[] theseAmounts;
    /** A reference to the puzzle being solved */
    private final Water puzzle;

    /**
     * Public constructor for WaterConfig to initialize a configuration with the
     * current amounts in the buckets and a Water object as variables
     * @param theseAmounts The current amounts in the buckets
     * @param puzzle A reference to the puzzle object, type Water
     */
    public WaterConfig(int[] theseAmounts, Water puzzle){
        this.theseAmounts = theseAmounts;
        this.puzzle = puzzle;
    }

    /**
     * A WaterConfig is a solution if one of its buckets has a value equal to
     *  amount in the puzzle
     * @return True if this is a solution, False otherwise
     */
    @Override
    public boolean isSolution() {
        for(int amount : theseAmounts)
            if(amount == puzzle.getSolution())
                return true;
        return false;
    }

    @Override
    public List<Configuration> getNeighbors() {
        List<Configuration> neighbors = new ArrayList<>();
        int[] capacities = puzzle.getBucketCapacities();
        for(int i = 0; i < theseAmounts.length; i++) {
            if (theseAmounts[i] != 0) {
                int[] neighbor = theseAmounts.clone();
                neighbor[i] = 0;
                neighbors.add(new WaterConfig(neighbor, puzzle));
            }
            if(theseAmounts[i] != capacities[i]){
                int[] neighbor = theseAmounts.clone();
                neighbor[i] = capacities[i];
                neighbors.add(new WaterConfig(neighbor, puzzle));
            }
            if(theseAmounts[i] != 0) {
                List<Integer> unfilled = unfilledIndices(i, capacities);
                for (Integer index : unfilled) {
                    if(theseAmounts[i] < capacities[index] - theseAmounts[index]){
                        int[] neighbor = theseAmounts.clone();
                        neighbor[i] = 0;
                        neighbor[index] += theseAmounts[i];
                        neighbors.add(new WaterConfig(neighbor, puzzle));
                    }
                    else if(theseAmounts[i] > capacities[index] - theseAmounts[index]){
                        int[] neighbor = theseAmounts.clone();
                        neighbor[i] -= (capacities[index] - theseAmounts[index]);
                        neighbor[index] = capacities[index];
                        neighbors.add(new WaterConfig(neighbor, puzzle));
                    }
                }
            }
        }
        return neighbors;
    }

    /**
     * A private helper function to getNeighbors that returns all the values of an int array
     *  that are below their corresponding capacity and are not at the index provided as a parameter
     * @param index The excluded index
     * @param capacities The corresponding capacities
     * @return A list of ints that represent the unfilled Indices, if list has size 0 there are no indices that are unfilled
     */
    private List<Integer> unfilledIndices(int index, int[] capacities){
        List<Integer> indices = new ArrayList<>();
        for(int i = 0; i < capacities.length; i++)
            if(i != index && theseAmounts[i] != capacities[i]) indices.add(i);
        return indices;
    }

    /**
     * Implemented so that WaterConfigs can be uniquely hashable in a HashMap
     * @param other The object being compared
     * @return True if the array theseAmounts are equal, False otherwise
     */
    @Override
    public boolean equals(Object other){
        if(other instanceof WaterConfig){
            WaterConfig otherConfig = (WaterConfig) other;
            return Arrays.equals(theseAmounts, otherConfig.theseAmounts);
        }
        return false;
    }

    /**
     * Implemented so that WaterConfigs can be uniquely hashable in a HashMap
     * @return The hash code of the instance variable hours
     */
    @Override
    public int hashCode(){
        return Arrays.hashCode(theseAmounts);
    }

    /**
     * Implemented so that a ClockConfig can be displayed when a solution is found
     * @return The data for this config
     */
    @Override
    public String toString() {
        return Arrays.toString(theseAmounts);
    }
}
