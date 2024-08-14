package puzzles.tipover.ptui;

import puzzles.tipover.model.*;

import util.Observer;
import util.ptui.ConsoleApplication;

import java.io.PrintWriter;
import java.util.List;

/**
 * Text Based User Interface for the TipOverPuzzle. The user types in commands from the command line and
 * the puzzle is displayed as a multi-line String.
 * @author Damon Gonzalez
 * November 2021
 */
public class TipOverPTUI extends ConsoleApplication implements Observer<TipOverModel, String> {
    /** The model being used by this Application */
    private TipOverModel model;
    /** The PrintWriter object that updates the game to the console */
    private PrintWriter out;
    /** Constants for all the commands a user can perform */
    private final static String LOAD = "load";
    private final static String RELOAD = "reload";
    private final static String MOVE = "move";
    private final static String HINT = "hint";
    private final static String SHOW = "show";

    /**
     * Initializes the text-based interface to set up the game
     */
    @Override
    public void init(){
        this.model = new TipOverModel();
        this.model.addObserver(this);
        List<String> args = getArguments();
        if(args.size() != 0)
            model.safeLoad(args.get(0));
    }

    /**
     * Creates the controller for the UI
     * @param console Where the UI should print output. It is recommended to save
     *                this object in a field in the subclass.
     */
    @Override
    public void start(PrintWriter console) {
        this.out = console;
        super.setOnCommand(
                LOAD, 1, ": load a new puzzle",
                e -> model.load(e[0])
        );
        super.setOnCommand(
                RELOAD, 0, ": reload the same puzzle",
                e -> model.reload()
        );
        super.setOnCommand(
                MOVE, 1, ": move the tipper(NORTH, SOUTH, EAST, WEST)",
                e -> model.move(e[0])
        );
        super.setOnCommand(
                HINT, 0, ": show the next step",
                e -> model.cheat()
        );
        super.setOnCommand(
                SHOW, 0, ": display the current state of the board",
                e -> update(model, "")
        );
    }

    /**
     * Updates the state of the view by printing out various things according to commands given by the user
     * @param model The model used by this UI
     * @param data If not an empty string, than a move was requested that cannot be legally performed and it equals
     *             the direction in which the move was requested. Otherwise, an empty string
     */
    @Override
    public void update(TipOverModel model, String data) {
        TipOverConfig currentConfig = model.getCurrentConfig();
        if(currentConfig == null){
            System.out.println("No File Loaded");
        }
        else {
            System.out.println(currentConfig);
            if (!data.equals("")) {
                System.out.println("Unable to move in " + data + " direction");
            } else if (currentConfig.isSolution()) {
                int steps = model.getNumSteps();
                this.out.println("YOU WIN!!!(in " + steps + " steps)");
                this.out.println("You can keep moving your tipper around(although, why would you want to do that),");
                this.out.println("You can reload the same puzzle to see if there is a faster way, or");
                this.out.println("You can load a totally new puzzle");
            }
        }
    }

    public static void main(String[] args) {
        if(args.length < 2) ConsoleApplication.launch(TipOverPTUI.class, args);
        else System.out.println("USAGES: \njava TipOverPTUI\njava TipOverPTUI {filename}");
    }
}
