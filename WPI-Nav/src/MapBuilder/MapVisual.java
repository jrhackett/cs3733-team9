package MapBuilder;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import logic.Node;

import java.util.HashMap;

public class MapVisual extends StackPane{
	/* Constants */
	private static final double BORDER = 7;
	private double height;
	private double width;

	/* Data Structures */
	private HashMap<Integer, Circle> id_circle;

	private MapBuilderController controller;

	/* Visuals */
	private Image mapImage;
	private ImageView mapView;
	private Rectangle default_background;
	private Group nodeCircles;

	/* COLORS */
	private boolean HIGHLIGHTED = false;
	private boolean CLICKED = false;
	private Color last = Color.TRANSPARENT;
	private Color lastStroke = Color.TRANSPARENT;
	private Color lastC = Color.TRANSPARENT;
	private Color lastStrokeC = Color.TRANSPARENT;


	/* this will be used to put together the map */
	/* overlaying the nodes and fixed map image should work */
	public MapVisual(MapBuilderController controller){
		super();
		this.controller = controller;
		this.mapView = new ImageView();
		this.nodeCircles = new Group();

		set_up_background(); //gray border
		this.getChildren().addAll(nodeCircles); //
	}

	/**
	 * Given a MAP_NAME -> ask Controller for map name and nodes for the map
	 * Add image to map
	 * Then add the Nodes
	 * @param map
	 */
	public void setMap(logic.Map map){
		System.out.println("MAP PATH:  " + map.getPath());
		this.getChildren().remove(mapView);
		this.mapImage = new Image(getClass().getResourceAsStream("../images/" + map.getPath() + ".png"));
		this.mapView = new ImageView(mapImage);
		this.getChildren().add(mapView);
		drawNodes(controller.getNodesOfMap(map.getID()));
		mapView.setOnMouseClicked(e -> {
			//create this node
			int id = controller.newNodeAtLocation(e); //x & y are already relative to map

			//create a circle for this node!!
			Node n = controller.getNode(id);
			Circle c = createCircle(n);
			id_circle.put(id, c);
			nodeCircles.getChildren().removeAll();
			nodeCircles.getChildren().add(c);

		});
	}

	/**
	 * Draws the nodes given on the map
	 * @param nodes
     */
	public void drawNodes(HashMap<Integer, Node> nodes){
		this.getChildren().remove(nodeCircles);
		this.nodeCircles = new Group();
		this.id_circle = new HashMap<>();


		nodes.forEach((k,v) -> {
			Circle circle = createCircle(v);
			id_circle.put(k, circle);
			nodeCircles.getChildren().add(circle); /* adding it to group */
		});

		this.getChildren().add(nodeCircles);
	}

	private Circle createCircle(Node v){
		double x = v.getX();  /* the nodes currently have way too small X / Y s - later we'll need to somehow scale */
		double y = v.getY();
		Circle circle = new Circle(x, y, 5);
		normal(circle);

		//when mouse moves over the node highlight it
		circle.setOnMouseEntered(e -> {
			last = (Color)circle.getFill();
			lastStroke = (Color)circle.getStroke();
			highlight(circle, Color.GOLD, Color.BLACK);
		});
		circle.setOnMouseExited(e -> highlight(circle, last, lastStroke));

		//when the mouse clicks a node change color!
		//depending on the PHASE OF THE MBT, DO SOMETHING!
		circle.setOnMousePressed(e -> {
			System.out.println(v.toString());
			if (!CLICKED) {
				lastC = (Color)circle.getFill();
				lastStrokeC = (Color)circle.getStroke();
				highlight(circle, Color.GREEN, Color.GREEN);
				CLICKED = true;
			}
		});

		circle.setOnMouseReleased(e -> {
			if (CLICKED) {
				highlight(circle, lastC, lastStrokeC);
				CLICKED = false;
			}
		});
		return circle;
	}

	private void highlight(Circle c, Color color, Color colorStroke ) {
		c.setFill(color);
		c.setOpacity(.6);
		c.setStroke(colorStroke);
		c.setStrokeWidth(1);
	}

	private void normal(Circle c) {
		c.setFill(Color.BLUE);
		c.setOpacity(1);
		c.setStrokeWidth(0);
	}

	private void highlightAll() {
		nodeCircles.getChildren().forEach(e -> {
			if (e instanceof Circle) {
				System.out.println("HIGHLIGHTING");
				highlight((Circle) e, Color.GOLD, Color.RED);
			}
		});
	}
	private void hideAll() {
		nodeCircles.getChildren().forEach(e -> {
			if (e instanceof Circle) {
				normal((Circle) e);
			}
		});
	}
	private void set_up_background(){
		default_background = new Rectangle(width, height);
		default_background.setFill(Color.DARKBLUE);
		default_background.setOpacity(.2);
		default_background.setArcHeight(7);
		default_background.setArcWidth(7);
	}
}
