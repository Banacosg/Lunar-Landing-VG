package puzzles.lunarlanding.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import puzzles.lunarlanding.model.LunarLandingModel;
import util.Coordinates;
import util.Observer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The GUI for the board game Lunar Landing
 * @author George Banacos
 * November 2021
 */
public class LunarLandingGUI extends Application
        implements Observer< LunarLandingModel, String > {

    /**
     * The model part of the MVC GUI setup.
     */
    private LunarLandingModel model;
    /**
     * The height of the current board
     */
    private int boardHeight;
    /**
     * The length of the current board
     */
    private int boardLength;

    /**
     * All the robot/explorer images named as their respective color.
     * They are all imageview and it should be the case that not all robots are
     * on the board at the same time
     */
    private ImageView blue = new ImageView(new Image(getClass().getResourceAsStream("resources/robot-blue.png")));
    private ImageView green = new ImageView(new Image(getClass().getResourceAsStream("resources/robot-green.png")));
    private ImageView lightBlue = new ImageView(new Image(getClass().getResourceAsStream("resources/robot-lightblue.png")));
    private ImageView orange = new ImageView(new Image(getClass().getResourceAsStream("resources/robot-orange.png")));
    private ImageView pink = new ImageView(new Image(getClass().getResourceAsStream("resources/robot-pink.png")));
    private ImageView purple = new ImageView(new Image(getClass().getResourceAsStream("resources/robot-purple.png")));
    private ImageView white = new ImageView(new Image(getClass().getResourceAsStream("resources/robot-white.png")));
    private ImageView yellow = new ImageView(new Image(getClass().getResourceAsStream("resources/robot-yellow.png")));
    private ImageView lander = new ImageView(new Image(getClass().getResourceAsStream("resources/lander.png")));
    private ImageView explorer = new ImageView(new Image(getClass().getResourceAsStream("resources/explorer.png")));

    /**
     * An enum of the different robots/explorer
     */
    private enum Robots{
        BLUE,
        GREEN,
        LIGHTBLUE,
        ORANGE,
        PINK,
        PURPLE,
        WHITE,
        YELLOW,
        LANDER,
        EXPLOER
    }

    /**
     * The board which the game is displayed on
     */
    private GridPane board;
    /**
     * The text above the board which gives the user feedback
     */
    private Label output = new Label("File loaded");

    /**
     * init initializes the model and the length and height of the board
     */
    @Override
    public void init(){
        List<String> args = getParameters().getRaw();
        if(args.size() != 0) {
            model = new LunarLandingModel(args.get(0));
            model.addObserver(this);
            boardHeight = model.getBoardHeight();
            boardLength = model.getBoardLength();
        }
    }

    /**
     * Start builds the starting configuration of the board as well as
     * creates the side buttons and top text box
     * @param stage
     */
    @Override
    public void start( Stage stage ) {
        stage.setTitle( "Lunar Landing" );


        board = new GridPane();
        addCellsToGrid(board, createBoard());

        GridPane arrows = new GridPane();
        createArrowMenu(arrows);

        Button load = new Button();
        load.setText("LOAD");
        load.setOnAction( event ->
            {
                FileChooser fileChooser = new FileChooser();
                File file = new File("data/lunarlanding");
                fileChooser.setInitialDirectory(file);
                fileChooser.setTitle("Open Resource File");
                File loadFile = fileChooser.showOpenDialog(stage);
                if (loadFile != null){
                    model.load("data/lunarlanding/" + loadFile.getName());
                }
            });

        Button reload = new Button();
        reload.setText("RELOAD");
        reload.setOnAction(event -> model.reload());

        Button hint = new Button();
        hint.setText("HINT");
        hint.setOnAction(event -> model.getHint());

        GridPane leftButtons = new GridPane();
        leftButtons.add( arrows, 0, 0);
        leftButtons.add(load, 0, 1);
        leftButtons.add(reload,0, 2);
        leftButtons.add(hint, 0, 3);

        BorderPane pane = new BorderPane();
        pane.setCenter(board);
        pane.setTop(output);
        pane.setRight(leftButtons);

        Scene scene = new Scene( pane );

        stage.setScene( scene );
        stage.show();
    }

    /**
     * Updates the GUI
     * @param lunarLandingModel: The model
     * @param message: The message that the update uses to know what needs to be updated
     */
    @Override
    public void update( LunarLandingModel lunarLandingModel, String message ) {
        switch (message) {
            case "show" -> addCellsToGrid(board, createBoard());
            case "Illegal move" -> output.setText("Illegal move");
            case "loaded" -> output.setText("File loaded");
            case "selection" -> output.setText("");
            case "space" -> output.setText("");
            default -> output.setText(message);
        }
    }

    /**
     * Creates the board by first creating a board with all black images
     * then adding the robots in afterward and then if possible putting
     * a red goal spot on the board
     * @return: A list of nodes which constitutes the board
     */

    private ArrayList<Node> createBoard() {
        boardLength = model.getBoardLength();
        boardHeight = model.getBoardHeight();
        ArrayList<Node> cells = new ArrayList<>();
        int imageCounter = 0;
        for (int i = 0; i < boardLength * boardHeight; i++) { //Fills the whole board with black
            cells.add(new ImageView(new Image(getClass().getResourceAsStream("resources/blank.png"))));
        }
        Coordinates goal = model.getGoal();
        cells.remove(goal.row() * boardLength + goal.col());
        cells.add(goal.row() * boardLength + goal.col(), new ImageView(new Image(getClass().getResourceAsStream("resources/goal.png"))));
        Map<Character, Coordinates> robotLocations = model.getRobotLocations();
        for (Character c : robotLocations.keySet()) { //For each robot in the Map
            Coordinates curCoords = robotLocations.get(c);
            int curRow = curCoords.row();
            int curCol = curCoords.col();
            if (c.equals('E')) {
                cells.remove(curRow * boardLength + curCol);
                cells.add(curRow * boardLength + curCol, new LanderButton(curRow, curCol, Robots.EXPLOER));
            } else {
                cells.remove(curRow * boardLength + curCol);
                cells.add(curRow * boardLength + curCol, new LanderButton(curRow, curCol, Robots.values()[imageCounter]));
                imageCounter++;
            }
        }
        return cells;
    }

    /**
     * Adds cells to the lunar landing board
     * @param board: The lunar landing board
     * @param cells: A list of nodes that are to be put into the lunar landing board
     */
    private void addCellsToGrid(GridPane board, ArrayList<Node> cells){
        board.getChildren().clear(); //First clear the board so it can hold the correct number of nodes
        for(int r = 0; r < boardHeight; r++){
            for(int c = 0; c < boardLength; c++){
                board.add(cells.get(r * boardLength + c), c, r); //Adds each cell
            }
        }
    }

    /**
     * Creates buttons for the north, east, south and west direction and puts them in a grid pane
     * @param arrows: The gridpane that each of the directional buttons goes into
     */
    private void createArrowMenu(GridPane arrows){
        Button up = new Button();
        up.setText("N");
        up.setOnAction( event -> model.move("north"));
        up.setPrefSize(30,30);
        arrows.add(up, 1, 0);
        Button left = new Button();
        left.setText("W");
        left.setOnAction( event -> model.move("west"));
        left.setPrefSize(30,30);
        arrows.add(left, 0, 1);
        Button right = new Button();
        right.setText("E");
        right.setOnAction( event -> model.move("east"));
        right.setPrefSize(30,30);
        arrows.add(right, 2, 1);
        Button down = new Button();
        down.setText("S");
        down.setOnAction( event -> model.move("south"));
        down.setPrefSize(30,30);
        arrows.add(down, 1, 2);
    }

    /**
     * LanderButton is a type of button that has a graphic of
     * a robot or an explorer. It extends buttons.
     */
    private class LanderButton extends Button{
        /**
         * Creates a button, sets an action, padding and a graphic
         * @param row: The row that the button is located at
         * @param col: The column the button is located at
         * @param robot: The image that will be on the button
         */
        public LanderButton(int row, int col, Robots robot){
            this.setOnAction( event -> model.selection(row, col) );
            this.setPadding(new Insets(0,0,0,0));
            switch (robot) {
                case BLUE -> this.setGraphic(blue);
                case GREEN -> this.setGraphic(green);
                case LIGHTBLUE -> this.setGraphic(lightBlue);
                case ORANGE -> this.setGraphic(orange);
                case PINK -> this.setGraphic(pink);
                case WHITE -> this.setGraphic(white);
                case YELLOW -> this.setGraphic(yellow);
                case LANDER -> this.setGraphic(lander);
                case EXPLOER -> this.setGraphic(explorer);
                default -> this.setGraphic(white);
            }
        }
    }

    /**
     * Main launches the GUI
     * @param args:The file to open
     */
    public static void main( String[] args ) {
        Application.launch( args );
    }
}