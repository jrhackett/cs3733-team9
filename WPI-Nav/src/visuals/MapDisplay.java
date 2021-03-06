package visuals;

import controller.Controller;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import logic.Building;
import logic.FileFetch;
import logic.IMap;
import logic.INode;
import org.controlsfx.control.PopOver;

import java.util.ArrayList;
import java.util.HashMap;




public class MapDisplay extends Pane {
    /* constants */
    private double IMAGE_WIDTH = 700;//660
    private double IMAGE_HEIGHT = 525;//495


    /* Data Structures */
    private HashMap<Integer, Circle> id_circle;
    private Controller controller;
    private HashMap<Integer, ArrayList<Line>> lines; /* mapId to line */
    ArrayList<INode> idPath;

    /* Visuals */
    private ScaleTransition st;
    private Transition ste;


    private Image mapImage;
    public ImageView mapView;

    public PopOver previousPopOver;

    private Color last = Color.TRANSPARENT;
    private Color lastStroke = Color.TRANSPARENT;
    private Paint lastSoft = Color.TRANSPARENT;


    /**--------------------------------------------------------*/
    private Color lineColor = Color.web("#00CCFF", 0.7);
    private Color transitionColor = Color.YELLOW;
    private Color pathBodyColor = Color.BLUE;
    private Color pathBorderColor = Color.LIGHTBLUE;
    private Color endBodyColor = Color.FIREBRICK;
    private Color endBorderColor = Color.RED;
    private Color startBodyColor = Color.GREEN;
    private Color startBorderColor = Color.LIGHTGREEN;
    /**--------------------------------------------------------------*/

    private INode startNode;
    private INode endNode;
    private HashMap<Integer, INode> nodeMap;
    private ArrayList<ArrayList<Instructions>> pathList;

    private int mapIdOldInt;


    private int mapIdNewInt;

    private boolean HIGLIGHTED = false;

    private ArrayList<INode> path; //last set path

    //////////////////////// ICON CODE ///////////////////////////
    private HashMap<Integer, ImageView> id_ICON;

    public Timeline timeline;

    /**
     * Constructor
     *
     * @param controller
     */
    public MapDisplay(Controller controller) {
        super();
        this.controller = controller;
        this.setStyle("-fx-background-color: #f4f4f4");
        this.mapView = new ImageView();
        this.setMaxWidth(IMAGE_WIDTH);
        this.setMaxHeight(IMAGE_HEIGHT);
        this.id_circle = new HashMap<>();
        this.path = new ArrayList<>();
        this.lines = new HashMap<>();
        this.idPath = new ArrayList<>();
        this.ste = new ScaleTransition();
        this.st = new ScaleTransition();


        this.getStylesheets().add(getClass().getResource("style.css").toExternalForm());


        controller.getNodes().forEach((k,v) -> {
            Circle c = createCircle(v);
            normal(c, v);
            id_circle.put(k,c);
        });

        previousPopOver = new PopOver();
    }


    /****************************************************************************************************************
                                         CIRCLE CODE
     ****************************************************************************************************************/


    /**
     * Draws the nodes given on the map
     *
     * @param nodes
     */
    public void drawNodes(HashMap<Integer, INode> nodes) {
        nodeMap = nodes;
        //to make it easy we will create all the nodes

        nodes.forEach((k, v) -> {
            this.getChildren().add(id_circle.get(k));
        });
    }

    /**
     * Given a MAP_NAME -> ask Controller for map name and nodes for the map [NEW VERSION]
     * Add image to map
     * Then add the Nodes
     *
     * @param map
     */
    public void setMap(IMap map) {

        //Removes all old nodes, old lines, and old maps
        this.getChildren().remove(0, this.getChildren().size());

        this.mapImage = FileFetch.getImageFromFile("floorPlans/" + map.getFilePath());


        this.mapView = new ImageView(mapImage);
        this.mapView.setFitHeight(IMAGE_HEIGHT);
        this.mapView.setFitWidth(IMAGE_WIDTH);

        this.getChildren().add(mapView);
        if(map.getID() == 0){
            makePolygons();
        }
        drawNodes(controller.getNodesOfMap(map.getID()));

        mapView.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() >= 2) selectNearestNode(e);
        });
    }

    /**
     * Selects the nearest node, allowing the controller to handle it
     * @param e
     */
    private void selectNearestNode(MouseEvent e){
        controller.handleMapClick(controller.nearestNode(e.getX(), e.getY()));
   }

    /**
     * An animation that plays a soft scaling and setting to the appropriate color
     * @param idOld
     * @param idNew
     */
    public void softSelectAnimation(int idOld, int idNew) {
        Circle c;

        if(idOld > -1) {
            if ((controller.endNode.getID() == idOld)||(controller.startNode.getID() == idOld)) {
                c = id_circle.get(controller.endNode.getID());
                c.setFill(Color.FIREBRICK);
                c = id_circle.get(controller.startNode.getID());
                c.setFill(Color.GREEN);
            } else {
                c = id_circle.get(idOld);
                c.setFill(Color.BLUE);
            }
        }

        c = id_circle.get(idNew);
        c.setFill(Color.HOTPINK);

        ScaleTransition st = new ScaleTransition(Duration.millis(75), c);

        st.setToX(2);
        st.setToY(2);
        st.setCycleCount(1);
        st.play();

        ScaleTransition st2 = new ScaleTransition(Duration.millis(65), c);
        st2.setToX(1);
        st2.setToY(1);
        st2.setCycleCount(1);


        st.setOnFinished(event -> {
            st2.play();
        });
    }

    /**
     * default circle
     *
     * @param c
     */
    public void normal(Circle c, INode v) {

        c.setFill(Color.TRANSPARENT);
        c.setStrokeWidth(0);
        c.setRadius(5);
        c.setOpacity(1);
        c.setEffect(null);
    }


    /**
     * Creates a circle associated with a node
     * @param v
     * @return
     */
    private Circle createCircle(INode v) {



        double x = v.getX();  /* the nodes currently have way too small X / Y s - later we'll need to somehow scale */
        double y = v.getY();
        Circle circle = new Circle(x, y, 5);

        circle.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
                controller.handleMapClick(v);
            }
        });


        return circle;
    }

    /**
     * Creates the popover for a node
     * @param v
     * @return
     */
    public PopOver createPopOverForNode(INode v) {
        PopOver popOver = new PopOver();
        popOver.setId("popover-id");
        VBox vbox = new VBox();

        Building building = controller.getBuilding(((logic.Transition)v).getBuildingID());
        HashMap<Integer, Integer> floorPlan = building.getFloorMap();

        Label buildingName = new Label(building.getName());
        buildingName.setStyle("-fx-font-size:9;");
        buildingName.setTextFill(Color.web("#333333"));
        Button pictureButton = new Button();

        Image picture = FileFetch.getImageFromFile("picture.png", 12, 12, true, true);
        ImageView pictureView = new ImageView(picture);
        pictureButton.setGraphic(pictureView);
        pictureButton.setId("picture-button");

        pictureButton.setOnMouseClicked(e-> {
            controller.showNodeImage(v);
            popOver.hide();
        });

        Label selectFloor = new Label("Select Floor:");
        selectFloor.setTextFill(Color.web("#333333"));
        selectFloor.setStyle("-fx-font-size:8;");

        FlowPane flowPane = new FlowPane();
        flowPane.setId("popover-id");

        /* displays only the buttons that the building has in rows of up to 3 */
        for(int i = floorPlan.size() - 1; i >= 0; i--) {
            Button button = new Button();
            if(((logic.Transition) v).getToFloor() == i)
                button.setId("popover-buttons-highlighted");
            else
                button.setId("popover-buttons");
            String value;
            Object array[] = floorPlan.keySet().toArray();
            if(floorPlan.keySet().toArray()[i].equals(-1)) {
                value = "SB";
                button.setStyle("-fx-padding:4 3 4 3;");
            }
            else if(floorPlan.keySet().toArray()[i].equals(0)) {
                value = "B";
            }
            else
            {
                value = floorPlan.keySet().toArray()[i].toString();
            }
            button.setText(value);
            final int x = (Integer)floorPlan.keySet().toArray()[i];
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    controller.switchToBuildingView(building.getID(), x);
                    popOver.hide();
                }
            });

            button.setOnMouseEntered(e -> requestFocus());
            flowPane.getChildren().add(button);
        }

        flowPane.setHgap(2);
        flowPane.setVgap(2);
        flowPane.setTranslateY(-2);

        vbox.setAlignment(Pos.CENTER);
        vbox.setId("popover-id");
        vbox.setSpacing(2);
        vbox.getChildren().addAll(pictureButton, flowPane);

        popOver.setContentNode(vbox);
        popOver.setArrowLocation(PopOver.ArrowLocation.RIGHT_TOP);
        popOver.setDetachable(false);
        popOver.setArrowSize(5.0);

        if(v.getPicturePath() == null) {
            pictureButton.setOpacity(0.0);
        }

        return popOver;
    }

    /**
     * highlights a circle
     *
     * @param c
     * @param color
     * @param colorStroke
     */
    private void highlight(Circle c, Color color, Color colorStroke) {
        c.setFill(color);
        c.setStroke(colorStroke);
        c.setStrokeWidth(1);
    }

    public void highlightPath(int id){
        if (controller.startNode.getID() != id && controller.endNode.getID() != id)
        highlightPath(id_circle.get(id));
    }

    /**
     * Highlights the path of nodes.
     *
     * @param c
     */
    public void highlightPath(Circle c) {
        //c.setFill(Color.web("#00CCFF"));
        // c.setStroke(Color.web("#0018A8"));
        c.setRadius(5);
        highlight(c, pathBodyColor, pathBorderColor);

        DropShadow ds = new DropShadow();
        ds.setColor(Color.WHITE);
        ds.setOffsetX(2.0);
        ds.setOffsetY(2.0);
        c.setEffect(ds);
    }


    /**
     * removes all lines from the display
     */
    public void revertPathNodes(){
        //This removes all lines from the display [overkill
        lines.forEach((k,v) -> {
           this.getChildren().removeAll(v);
        });

        //this seems to be not fully working - TODO FIX
        id_circle.forEach((k,v) -> {
            normal(id_circle.get(k), controller.getNode(k));
        });
    }

    /**
     * Clears the path
     */
    public void clearPath(){
        this.lines = new HashMap<>();
        this.path  = new ArrayList<>();
    }

    /**
     * Show path of nodes
     */

    public void createPath(ArrayList<ArrayList<Instructions>> path) {

        pathList = path;
        revertPathNodes();

        idPath = new ArrayList<>();
        lines = new HashMap<>();


        for (ArrayList<Instructions> list : path) {
            ArrayList<Line> lineArrayList = new ArrayList<>();
            double coordX = list.get(0).getNode().getX();
            double coordY = list.get(0).getNode().getY();

            for (Instructions i : list) {
                /** path nodes highlight blue **/
                highlightPath(id_circle.get(i.getNode().getID()));
                idPath.add(i.getNode());



                /** create all the lines and add them to a list **/
                Line line = new Line();
                line.setStartX(coordX);
                line.setStartY(coordY);
                coordX = i.getNode().getX();
                coordY = i.getNode().getY();
                line.setEndX(coordX);
                line.setEndY(coordY);

                /** below is the line [color] [dashed] [size] **/
                line.setStroke(lineColor);
                line.setStrokeWidth(3);
                line.setStrokeDashOffset(5);
                line.getStrokeDashArray().addAll(2d, 7d);

                /** shadow effect on the line **/
                DropShadow dropShadow = new DropShadow();
                dropShadow.setRadius(5.0);
                dropShadow.setOffsetX(2.0);
                dropShadow.setOffsetY(2.0);
                dropShadow.setColor(Color.BLACK);

                line.setEffect(dropShadow);
                lineArrayList.add(line);
            }
            //System.out.println("lines put into hashmap");
            if (lines.containsKey(list.get(0).getNode().getMap_id())){
                lineArrayList.addAll(lines.get(list.get(0).getNode().getMap_id()));
                lines.put(list.get(0).getNode().getMap_id(), lineArrayList);
            } else {
                lines.put(list.get(0).getNode().getMap_id(), lineArrayList);
            }
        }

        setStartNode(idPath.get(0));
        setEndNode(idPath.get(idPath.size()-1), false);
    }

    /**
     * Removes the old lines from the last map
     * Draw the new lines for this map
     *
     * @param mapIdOld
     * @param mapIdNew
     */
    public void showLines(int mapIdOld, int mapIdNew) {

        mapIdNewInt = mapIdNew;
        mapIdOldInt = mapIdOld;

        if (mapIdOld != -1) {
            try {
                this.getChildren().removeAll(lines.get(mapIdOld));
            } catch (NullPointerException e){
               // System.out.println("MAP HAS NO LINES YET");
            }
        }

        //if map has lines to show, show them
        if(this.lines.containsKey(mapIdNew)){
            this.getChildren().addAll(lines.get(mapIdNew));
        }
    }

    /**
     * Gets the lines
     * @return
     */
    public HashMap<Integer, ArrayList<Line>> getLines() {
        return lines;
    }

    /**
     * The color and effect for when a node is set as a destination
     *
     * @param iNode
     */
    public void setStartNode(INode iNode) {
        startNode = iNode;

        Circle c;

        if (!id_circle.containsKey(iNode.getID())) {
            c = createCircle(controller.getNode(iNode.getID()));
        } else {
            c = id_circle.get(iNode.getID());

        }

        c.setRadius(5);
        highlight(c, startBodyColor, startBorderColor);
        id_circle.put(iNode.getID(), c);


        ScaleTransition st = new ScaleTransition(Duration.millis(75), c);

        st.setToX(2);
        st.setToY(2);
        st.setCycleCount(1);
        st.play();

        ScaleTransition st2 = new ScaleTransition(Duration.millis(65), c);
        st2.setToX(1);
        st2.setToY(1);
        st2.setCycleCount(1);
        st2.play();


        st.setOnFinished(event -> {
            st2.play();
        });
    }


    /**
     * Sets the end node
     * @param v
     * @param animation
     */
    public void setEndNode(INode v, boolean animation) {
        endNode = v;
        Circle c;

        if (id_circle.containsKey(v.getID())) {
            c = id_circle.get(v.getID());

        } else {
            c = createCircle(v);
        }

        c.setRadius(5);
        highlight(c, endBodyColor, endBorderColor);

        if (animation) {
            ScaleTransition st = new ScaleTransition(Duration.millis(75), c);
            //st.setByX(1.1f);
            //st.setByY(1.1f);
            st.setToX(2);
            st.setToY(2);
            st.setCycleCount(1);
            st.play();

            ScaleTransition st2 = new ScaleTransition(Duration.millis(65), c);
            st2.setToX(1);
            st2.setToY(1);
            st2.setCycleCount(1);
            st2.play();


            st.setOnFinished(event -> {
                st2.play();
            });
        }

        id_circle.put(v.getID(), c);
    }


    /**
     * remove node completely from map pane
     * (this is for temporary nodes)
     *
     * @param id
     */
    public void removeNode(int id) {
        if (id_circle.containsKey(id)) {
            //normal(id_circle.get(id), );//hideLast(id)
            this.getChildren().remove(id_circle.remove(id));
            id_circle.remove(id);
        }
    }

    public void hideLast(INode node) {
        normal(id_circle.get(node.getID()), node);
    }


    /****************************************************************************************************************
                                                 ICON CODE
     ****************************************************************************************************************/

    /****************************************************************************************************************
     Color Change CODE
     ****************************************************************************************************************/

    public void updateAll(){

        this.revertPathNodes();
        //this.drawNodes(nodeMap);
        try {
            this.createPath(pathList);
            this.showLines(mapIdNewInt, mapIdOldInt);
            this.setStartNode(startNode);
            this.setEndNode(endNode, true);
        }catch(Exception e){

        }
    }

    public void setTransitionColor(Color body){
        transitionColor = body;
    }
    public void setPathColor(Color body, Color border, Color path){
        lineColor = path;
        pathBodyColor = body;
        pathBorderColor = border;
    }
    public void setStartColor(Color body, Color border){
        startBodyColor = body;
        startBorderColor = border;
    }
    public void setEndColor(Color body, Color border){
        endBodyColor = body;
        endBorderColor = border;
    }


    /****************************************************************************************************************
     *  POLYGONS
     ****************************************************************************************************************/


    private void makePolygons() {
        //Create Atwater Kent ID 1883
        Polygon atwaterKent = new Polygon();
        atwaterKent.getPoints().addAll(new Double[]{
                435.369,118.974,
                416.409,151.917,
                427.074,158.079,
                423.756,164.004,
                459.069,184.386,
                462.861,178.224,
                473.526,184.386,
                492.723,151.321,
                476.133,142.2,
                467.601,156.183,
                443.427,142.437,
                452.433,127.98



        });
        atwaterKent.setFill(Color.TRANSPARENT);
        addPolygonEvents(atwaterKent, 1883);

        //Create campus center
        Polygon campusCenter = new Polygon();
        campusCenter.getPoints().addAll(new Double[]{
                299.331,202.872,
                295.6575,225.624,
                324.69,230.0085,
                326.349,220.5285,
                328.77825,221.121,
                327.534,222.8985,
                328.3635,228.05325,
                332.6295,231.1935,
                337.962,230.30475,
                341.043,226.098,
                340.2135,220.82475,
                335.355,217.6845,
                335.7105,216.2625,
                346.07925,217.74375,
                353.9595,168.3885,
                332.09625,164.89275,
                331.62225,167.796,
                330.378,167.49975,
                323.8605,176.9205,
                329.48925,180.65325,
                324.927,186.69675,
                322.49775,185.03775,
                317.99475,185.86725,
                315.21,189.6,
                315.98025,194.2215,
                318.1725,195.762,
                313.49175,202.10175,
                319.29825,205.953,
                315.98025,205.479,
                313.077,209.745,
                304.95975,203.7015,

        });
        campusCenter.setFill(Color.TRANSPARENT);
        addPolygonEvents(campusCenter, 1876);

        //Stratton Hall
        Polygon stratton = new Polygon();
        stratton.getPoints().addAll(new Double[]{
                391.05,295.6575,
                384.414,333.696,
                402.189,336.777,
                408.588,298.146,

        });
        stratton.setFill(Color.TRANSPARENT);
        addPolygonEvents(stratton, 1899);

        Polygon project = new Polygon();
        project.getPoints().addAll(new Double[]{
                397.5675,257.5005,
                392.709,287.1255,
                410.484,290.088,
                415.3425,260.463,

        });
        project.setFill(Color.TRANSPARENT);
        addPolygonEvents(project, 2029);

        Polygon fuller = new Polygon();
        fuller.getPoints().addAll(new Double[]{
                499.359,153.9315,
                505.758,166.611,
                485.0205,177.1575,
                489.0495,185.4525,
                478.74,190.9035,
                490.827,215.196,
                526.2585,197.421,
                517.9635,180.594,
                518.556,180.2385,
                518.556,179.646,
                528.3915,174.7875,
                515.1195,148.2435,
                512.394,147.2955,
        });
        fuller.setFill(Color.TRANSPARENT);
        addPolygonEvents(fuller, 1886);

        Polygon boynton = new Polygon();
        boynton.getPoints().addAll(new Double[]{
                404.796,351.8265,
                401.3595,371.43825,
                414.513,373.512,
                415.1055,370.3125,
                440.109,374.46,
                439.81275,377.00775,
                444.2565,377.71875,
                444.849,375.2895,
                446.68575,375.5265,
                448.93725,361.899,
                417.41625,356.62575,
                417.77175,353.90025,
        });
        boynton.setFill(Color.TRANSPARENT);
        addPolygonEvents(boynton, 1895);

        Polygon higgins = new Polygon();
        higgins.getPoints().addAll(new Double[]{
                273.6165,116.2485,
                269.05425,122.82525,
                283.03725,132.6015,
                277.58625,140.6595,
                289.0215,148.42125,
                299.568,133.431,
                301.701,134.79375,
                304.308,131.12025,
                302.2935,129.69825,
                306.915,122.94375,
                312.30675,126.73575,
                317.9355,118.67775,
                311.2995,114.1155,
                305.55225,122.23275,
                295.24275,115.182,
                293.58375,117.4335,
                292.75425,116.13,
                290.14725,116.07075,
                288.84375,118.3815,
                290.088,120.57375,
                291.3915,120.5145,
                290.26575,122.1735,
                287.5995,120.45525,
                284.9925,123.951,

        });
        higgins.setFill(Color.TRANSPARENT);
        addPolygonEvents(higgins, 1881);

        Polygon library = new Polygon();
        library.getPoints().addAll(new Double[]{
                500.1885,248.0205,
                497.5815,253.9455,
                494.9745,261.5295,
                492.96,267.573,
                491.6565,272.9055,
                490.827,277.29,
                489.9975,282.267,
                489.405,288.192,
                488.931,294.591,
                488.5755,302.649,
                516.66,308.4555,
                518.319,300.99,
                517.4895,300.753,
                518.9115,297.0795,
                520.689,292.221,
                521.9925,287.718,
                522.822,284.4,
                523.6515,278.5935,
                524.3625,273.8535,
                524.8365,268.521,
                525.192,263.781,
                525.3105,260.937,
                526.0215,260.937,
                527.6805,253.59,
        });
        library.setFill(Color.TRANSPARENT);
        addPolygonEvents(library, 2148);

        Polygon salisbury = new Polygon();
        salisbury.getPoints().addAll(new Double[]{
                441.6495,201.3315,
                439.5165,214.011,
                425.889,211.7595,
                421.0305,241.503,
                445.323,245.769,
                443.7825,255.96,
                463.4535,259.278,
                472.104,206.427,
        });
        salisbury.setFill(Color.TRANSPARENT);
        addPolygonEvents(salisbury, 1909);//TODO double check this value

        Polygon harrington = new Polygon();
        harrington.getPoints().addAll(new Double[]{
                192.207,216.7365,
                186.4005,237.8295,
                185.9265,257.5005,
                192.918,260.3445,
                192.0885,266.7435,
                196.71,267.4545,
                197.5395,262.2405,
                207.4935,266.388,
                207.138,269.1135,
                226.335,272.076,
                226.6905,269.469,
                230.2455,269.1135,
                229.7715,272.6685,
                234.5115,273.261,
                235.2225,268.758,
                249.561,267.4545,
                254.775,248.376,
                255.6045,226.2165,

        });
        harrington.setFill(Color.TRANSPARENT);
        addPolygonEvents(harrington,3579);

    }

    /**
     * Creates the events for the polygons
     * @param p
     * @param key
     */
    private void addPolygonEvents(Polygon p, Integer key){
        this.getChildren().add(p);
        PopOver popOver;
        p.addEventFilter(MouseEvent.MOUSE_ENTERED_TARGET,
                new EventHandler<MouseEvent>() {
                    public void handle(MouseEvent event) {
                        p.setFill(Color.web("red", 0.5));
                        p.setStroke(Color.RED);
                    }
                });
        p.addEventFilter(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    public void handle(MouseEvent event) {
                        controller.getMyDisplay().zoomAndPan.zoomToNode(controller.getNode(key));
                        PopOver popOver = createPopOverForNode(controller.getNode(key));
                        if (!(previousPopOver.equals(popOver))) {
                            double zX = controller.getMyDisplay().zoomAndPan.zoomOffsetX;
                            double zY = controller.getMyDisplay().zoomAndPan.zoomOffsetY;

                            if(zX == 0 && zY == 0){
                                popOver.show(p, 50);
                                popOver.setArrowSize(0);
                            }

                            else {
                                popOver.show(p, controller.getStageOffset().getX() + 40, controller.getStageOffset().getY());
                                popOver.setArrowSize(0);
                            }
                            previousPopOver.hide();
                            previousPopOver = popOver;
                        }
                    }
                });
        p.addEventFilter(MouseEvent.MOUSE_EXITED_TARGET,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        //previousPopOver.hide();//test
                        p.setFill(Color.TRANSPARENT);
                        p.setStroke(Color.TRANSPARENT);
                    }
                });

    }

    /**
     * Specifics for different rotations
     * @param building
     */
    public void rotationAnimation(int building){
        double pivotX = 0, pivotY = 0,angle = 0;
        switch (building) {
            case 0: //Campus Map
                pivotX = 0;
                pivotY = 0;
                angle = 0;
                break;
            case 1: //Stratton Hall
                pivotX = 396.5;
                pivotY = 315.92;
                angle = -104;
                break;
            case 2: //Atwater Kent
                pivotX = 449.7;
                pivotY = 160.3;
                angle = 152;
                break;
            case 3: //Boynton
                pivotX = 424.7;
                pivotY = 365.2;
                angle = -12;
                break;
            case 4: //Campus Center
                pivotX = 329;
                pivotY = 203.82;
                angle = 83;
                break;
            case 5: //Library
                pivotX = 506.2;
                pivotY = 278.47;
                angle = -105;
                break;
            case 6: //Higgins House
                pivotX = 291.54;
                pivotY = 131.53;
                angle = -124.74;
                break;
            case 7: //HH apartment
                pivotX = 0;
                pivotY = 0;
                angle = 0;
                break;
            case 8: //HH garage
                pivotX = 0;
                pivotY = 0;
                angle = 0;
                break;
            case 9: //Project Center
                pivotX = 403.;
                pivotY = 247;
                angle = -102;
                break;
            case 10: //Fuller Labs
                pivotX = 505;
                pivotY = 188;
                angle = -66;
                break;
            case 11: //Salisbury
                pivotX = 448.6;
                pivotY = 228;
                angle = 83;
                break;
            case 12: //Harrington
                pivotX = 219;
                pivotY = 244;
                angle = -10;

        }
        Node map = controller.getMyDisplay().zoomAndPan.panAndZoomPane;
        Rotate rotation = new Rotate();
        rotation.setPivotX(pivotX);
        rotation.setPivotY(pivotY);
        map.getTransforms().add(rotation);

        double dur = Math.abs((angle/360)*800);
        if (dur < 250){
            dur += 150;
        }

        timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(rotation.angleProperty(), 0)),
                new KeyFrame(Duration.millis(dur), new KeyValue(rotation.angleProperty(), angle)),
                new KeyFrame(Duration.millis(dur + 1), new KeyValue(rotation.angleProperty(), 0)));

        timeline.play();
    }

}