package puzzles.lunarlanding;

import puzzles.lunarlanding.model.LunarLandingConfig;
import solver.Configuration;
import solver.Solver;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * DESCRIPTION
 * @author George Banacos
 * November 2021
 */
public class LunarLanding {

    /**
     * The configuration for the LunarLanding solver
     */
    private Configuration lunarLandingConfiguration;

    /**
     * Constructs a LunarLanding.
     * @param fileName: The file name for the LunarLandingConfig to use
     */
    public LunarLanding(String fileName){
        try {
            lunarLandingConfiguration = new LunarLandingConfig(fileName);
        } catch (FileNotFoundException e){

        }
    }

    /*
     * code to read the file name from the command line and
     * run the solver on the puzzle
     */
    public static void main( String[] args ) {
        if (args.length == 1){
            String fileName = args[0];
            LunarLanding l = new LunarLanding(fileName);
            Solver s = new Solver();
            List<Configuration> path = s.BFS(l.lunarLandingConfiguration);
            for(int i = 0; i < path.size(); i++){
                System.out.println("Step " + i + ":");
                LunarLandingConfig LLC = (LunarLandingConfig) path.get(i);
                System.out.println(LLC.toString());
            }
        }
        else{
            System.out.println("Input file name");
        }
    }
}
