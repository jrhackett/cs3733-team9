package visuals;

import controller.Controller;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import logic.Node;

import java.util.ArrayList;
import java.util.HashMap;


public class MapDisplay extends Pane {
    private static final double BORDER = 7;
    private double height;
    private double width;

    private double IMAGE_WIDTH = 660;
    private double IMAGE_HEIGHT = 495;


    /* Data Structures */
    private HashMap<Integer, Circle> id_circle;
    private Controller controller;

    /* Visuals */
    private Image mapImage;
    private ImageView mapView;

    private Color last = Color.TRANSPARENT;
    private Color lastStroke = Color.TRANSPARENT;



    public MapDisplay(Controller controller){
        super();
        this.controller = controller;
        this.setStyle("-fx-background-color: #F5F5DC");
        this.mapView = new ImageView();
        this.setMaxWidth(IMAGE_WIDTH);
        this.setMaxHeight(IMAGE_HEIGHT);

    }

    /**
     * Given a MAP_NAME -> ask Controller for map name and nodes for the map
     * Add image to map
     * Then add the Nodes
     * @param map
     */
    public void setMap(logic.Map map){
        this.getChildren().remove(mapView);
        this.mapImage = new Image(getClass().getResourceAsStream("../images/" + map.getPath() + ".png"), IMAGE_WIDTH, IMAGE_HEIGHT, true, true);
        this.mapView = new ImageView(mapImage);
        this.getChildren().add(mapView);
        drawNodes(controller.getNodesOfMap(map.getID()));

        /*
        mapView.setOnMouseClicked(e -> {
        });
        */
    }

    /**
     * Show path of nodes
     *
     */
    public void showPath(ArrayList<Node> path){
        id_circle.forEach((k,v) -> {
            normal(v);
        });

        for (Node n : path){
            highlightPath(id_circle.get(n.getID()));
        }


    }



    /**
     * Draws the nodes given on the map
     * @param nodes
     */
    public void drawNodes(HashMap<Integer, Node> nodes){
        this.id_circle = new HashMap<>();

        nodes.forEach((k,v) -> {
            Circle circle = createCircle(v);
            id_circle.put(k, circle);
            this.getChildren().add(circle);
        });
    }


    private Circle createCircle(Node v){

        double x = v.getX();  /* the nodes currently have way too small X / Y s - later we'll need to somehow scale */
        double y = v.getY();
        Circle circle = new Circle(x, y, 5);
        normal(circle);

        circle.setOnMouseEntered(e -> {
            last = (Color)circle.getFill();
            lastStroke = (Color)circle.getStroke();
            highlight(circle, Color.BLUE, Color.BLACK);
        });

        circle.setOnMouseExited(e -> {
            highlight(circle, last, lastStroke);
        });

        circle.setOnMouseClicked(e -> {

        });

        circle.setOnMousePressed(e -> {

        });

        circle.setOnMouseReleased(e -> {

        });

        return circle;
    }



    private void highlightPath(Circle c){
        c.setFill(Color.web("#00CCFF"));
        c.setStroke(Color.web("#0018A8"));
        c.setRadius(1);

        DropShadow ds = new DropShadow();
        ds.setColor(Color.WHITE);
        ds.setOffsetX(2.0);
        ds.setOffsetY(2.0);
        c.setEffect(ds);
    }

    private void highlight(Circle c, Color color, Color colorStroke ) {
        c.setFill(color);
        c.setStroke(colorStroke);
        c.setStrokeWidth(1);
    }

    private void normal(Circle c) {
        c.setFill(Color.TRANSPARENT);
        c.setStrokeWidth(0);
        c.setRadius(5);
        c.setOpacity(1);
        c.setEffect(null);
    }

    /*
    private void highlightAll() {
        this.getChildren().forEach(e -> {
            if (e instanceof Circle) {
                highlight((Circle) e, Color.GOLD, Color.RED);
            }
        });
    }
    private void hideAll() {
        this.getChildren().forEach(e -> {
            if (e instanceof Circle) {
                normal((Circle) e);
            }
        });
    }
    */
}


