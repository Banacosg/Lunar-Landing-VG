package solver;

import java.util.List;

/**
 * Configuration abstraction for the solver algorithm
 * This interface expects classes to return objects that are of its same type.
 * @author Damon Gonzalez
 */
public interface Configuration {
    /** Uses 'this' to determine if it is a solution configuration or not */
    boolean isSolution();
    /** Uses 'this' and returns an array of Configurations that are its neighbors */
    List<Configuration> getNeighbors();
    /** To make all configurations uniquely hashable */
    boolean equals(Object other);
    /** To make all configurations uniquely hashable */
    int hashCode();
    /** So that a Configuration is easily representable in the step printout */
    String toString();
}
