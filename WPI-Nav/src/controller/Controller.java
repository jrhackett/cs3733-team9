package controller;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import logic.Node;
import visuals.Display;

import java.util.ArrayList;


/**
 * IMPORTANT: This Class will 'launch' the application.
 *            And control the application
 */
public class Controller extends Application {
    /* visual constants */
    private static final Double WINDOW_WIDTH = 900.0;
    private static final Double WINDOW_HEIGHT = 600.0;

    /* visual component */
    private Display myDisplay;

    /* information variables */
    public String start;
    public String destination;
    public String[] mid_way_points;
    public int current_mid_way_points = 0;
    public int max_mid_way_points = 3;



    @Override
    public void start(Stage s) throws Exception {

		/* icon */
        s.getIcons().add(new Image(getClass().getResourceAsStream("../visuals/images/globe.png")));

		/* basic layoutgit */
        s.setTitle("WPI MAPS");
        s.setResizable(false);

		/* setup */
        this.myDisplay = new Display(WINDOW_WIDTH, WINDOW_HEIGHT, this);

        //Struct Nodes = Controller.getNodes();
        Scene display = myDisplay.Init(); //Nodes
        s.setScene(display);

        s.show();
    }


    /* logic methods */
    /* function that gets the map name */
    public static String getMapName() {
        return "wpi-campus-map";
    }

    /* function to get all nodes for a map */
    public static ArrayList<Node> getNodes(Object p0) {
        return null;
    }

    /* function that sets a new destination */
    public static void setNewDestination(Node n) {

    }

    public static Node[] getMapNodes(String MAP_NAME) {
        Node n1 = new Node("Institute",0, 0, 0, 0);
        Node n2 = new Node("RecCenter",1, 0, 10, 0);
        Node n3 = new Node("Field",2, 0, 20, 0);
        Node n4 = new Node("Harrington",3, 3, 16, 0);
        Node n5 = new Node("Quad",4, 5, 5, 0);
        Node n6 = new Node("Morgan",5, 6, 1, 0);
        Node n7 = new Node("Riley",6, 11, 2, 0);
        Node n8 = new Node("Higgins Labs",7, 10, 13, 0);
        Node n9 = new Node("Campus Center",8, 10, 20, 0);
        Node n10 = new Node("Fountain",9, 16, 17, 0);
        Node n11 = new Node("Alden",10, 16, 3, 0);
        Node n12 = new Node("West Street",11, 18, 8, 0);
        Node n13 = new Node("Library",12, 20, 20, 0);

        Node[] nodes = {n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13};
        return nodes;
    }

    public String getDefaultMapName() {
        return "wpi-campus-map";
    }

    public Node[] getPathNodes(){
        Node n8 = new Node("Higgins Labs",7, 10, 13, 0);
        Node n9 = new Node("Campus Center",8, 10, 20, 0);
        Node n10 = new Node("Fountain",9, 16, 17, 0);
        Node n11 = new Node("Alden",10, 16, 3, 0);
        Node n12 = new Node("West Street",11, 18, 8, 0);
        Node n13 = new Node("Library",12, 20, 20, 0);
        Node[] nodes = {n8, n9, n10, n11, n12, n13};
        return nodes;
    }


    /* function that gets a path, given two+ nodes */

    /* function for handicap path, given two+ nodes */

    /* function looking for nearest three bathrooms */

    /* function that shows three paths to food */





    public static void main(String[] args) {
        launch(args);
    }
}
