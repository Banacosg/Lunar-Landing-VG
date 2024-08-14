package puzzles.lunarlanding.ptui;

import puzzles.lunarlanding.model.LunarLandingConfig;
import puzzles.lunarlanding.model.LunarLandingModel;
import solver.Solver;
import util.Observer;

import java.util.Scanner;

/**
 * A PTUI for the board game Lunar Landing
 * @author George Banacos
 * November 2021
 */
public class LunarLandingPTUI implements Observer<LunarLandingModel, String> {

    /**
     * The model associated with the PTUI
     */
    private LunarLandingModel model;

    /**
     * The constructor for the PTUI. It creates the model and adds
     * itself as an observer
     * @param fileName: The name of the file that is to be used as
     *                the board
     */
    public LunarLandingPTUI(String fileName){
        model = new LunarLandingModel(fileName);
        model.addObserver(this);
    }

    /**
     * Prints out the help menu
     */
    private void help(){
        System.out.println("Legal commands are...\n" +
                "\t> help : Show all commands.\n" +
                "\t> reload filename: Load the most recent file again.\n" +
                "\t> load filename: Load a new game board file. (1 argument)\n" +
                "\t> hint : Make the next move for me.\n" +
                "\t> show : Display the board.\n" +
                "\t> go {north|south|east|west}: Tell chosen character where to go. (1 argument)\n" +
                "\t> choose row column: Choose which character moves next. (2 arguments)\n" +
                "\t> quit");
    }

    /**
     * Updates the command line view of the board game
     * @param l: The current lunarLandingModel
     * @param arg: The current argument for this method
     */
    @Override
    public void update(LunarLandingModel l, String arg){
        switch (arg.split(" ")[0]){
            case "carrot":
                System.out.print("> ");
                break;
            case "illegal":
                System.out.println("Illegal command"); //Purposefully excluded break
            case "help":
                this.help();
                break;
            case "loaded":
                System.out.println("File loaded");
                break;
            case "space":
                System.out.println("");
                break;
            case "show":
                System.out.println();
                System.out.println(l.toString());
                break;
            case "selection":
                break;
            default:
                System.out.println(arg);
                break;
        }
    }

    /**
     *This run method controls the input from the user and
     * calls the appropriate methods.
     */
    private void run(){
        Scanner scanner = new Scanner(System.in);
        String input;
        update(model, "show");
        update(model, "File loaded");
        update(model, "carrot");
        while(!(input = scanner.nextLine()).equals("quit")){ //While the next line isn't quit
            String[] inputArgs = input.split("\\s++");
            switch (inputArgs[0]){ //Switch case based on the first token of inputArgs
                case "help":
                    update(model, "help");
                    break;
                case "reload":
                    model.reload();
                    break;
                case "load":
                    if (inputArgs.length == 2) { //Checks for correct number of arguments
                        model.load(inputArgs[1]);
                    }
                    else{
                        System.out.println("Number of arguments error");
                    }
                    break;
                case "hint":
                    model.getHint();
                    break;
                case "show":
                    update(model, "show");
                    break;
                case "go":
                    if (inputArgs.length == 2) { //Checking for the correct number of arguments
                        model.move(inputArgs[1]);
                        //update(model," ");
                    }
                    else{
                        System.out.println("Number of arguments error");
                    }
                    break;
                case "choose":
                    try { //Using try in case the input is non-numerical or input args < 3
                        model.selection(Integer.parseInt(inputArgs[1]), Integer.parseInt(inputArgs[2]));
                    } catch (IndexOutOfBoundsException e){
                        System.out.println("Error selecting position");
                    } catch (NumberFormatException n){
                        System.out.println("Error parsing positional arguments");
                    }
                    break;
                default: //No recognized command
                    update(model, "illegal command");
            }
            update(model, "carrot");
        }

    }

    /**
     * The main which checks if the file is valid and then starts
     * the PTUI
     * @param args: The arguments
     */
    public static void main( String[] args ) {
        if (args.length != 1){
            System.out.println("Input error");
        }
        else {
            LunarLandingPTUI PTUI = new LunarLandingPTUI(args[0]);
            PTUI.run();
        }
    }
}