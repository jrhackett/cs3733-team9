package controller;

import SVGConverter.SvgImageLoaderFactory;
import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import logic.*;
import visuals.Display;
import visuals.Instructions;

import java.util.ArrayList;
import java.util.HashMap;




/**
 * IMPORTANT: This Class will 'launch' the application.
 *            And control the application
 */


public class Controller extends Application {

    /* visual component */
    private Display myDisplay;

    /* information variables */


    /* nodes and graphs */
    private HashMap<Integer, INode> nodes;   /* all the nodes */
    private HashMap<Integer, IMap> maps;

    /* information of the maps */
    private int currentBuilding = 0;
    public int prevBuilding = 0;
    private int currentFloor;
    private Campus campus;
    private logic.IMap currentMap;                  /* current map being used */
    private HashMap<Integer, Building> buildings;   /* information on organization of floors */

    /* switches */
    private boolean FIRST = false; //if the last thing to be set was first
    public boolean FLAG = false;

    private logic.INode tempStart;
    private logic.INode tempEnd;

    public logic.INode startNode;
    public logic.INode endNode;

    /* path data */
    public ArrayList<INode> pathNodes;       /* this is set then used to get instructions from logic.Directions */
    ArrayList<ArrayList<Instructions>> fullPath;
    private int currentIndex;
    private int lastMapID;



    private Stage stage;

    private double flipFlop = 1;
    public boolean rightButtonFlag = false;
    boolean firstTime = true;

    public int lastSoft = -1;

    private Scene display;


    /** Frosty **/
    private static final double W = 330;
    private static final double H = 590;

    private static final double BLUR_AMOUNT = 60;

    private static final Effect frostEffect =
            new BoxBlur(BLUR_AMOUNT, BLUR_AMOUNT, 3);

    private boolean PATH_FLAG = true;


    /**
     * init occurs in the preloader
     */
    @Override
    public void init(){
         /* load up svg converter */

        /* load up svg converter */
        SvgImageLoaderFactory.install();

		/* get information */
        nodesFromFile();
        mapsFromFile();
        buildingsFromFile();

        /* node images */
        //TODO FIX
        nodes.forEach((k,v) -> {
            /*if(buildings.get(maps.get(v.getMap_id()).getBuildingID()).getName().equals("Stratton Hall")){
                v.setPicturePath("../images/pictures/Stratton Hall.png");
            }else*/
            if((v instanceof Transition) && v.getMap_id() == 0 && ((logic.Transition)v).getBuildingID() != 0) {
                //v.setPicturePath("../images/pictures/Stratton Hall.png");//TODO change this from always Stratton to the right picture
                String path;
                try {
                    Building building = this.getBuilding(((logic.Transition)v).getBuildingID());
                    path = building.getName();
                    path += ".png";
                    v.setPicturePath(path);
                    //FileFetch.getImageForPictures(path); //TODO make this image scale with resizing
                } catch(NullPointerException e) {          //TODO bad bad bad -- need rest of pictures
                    v.setPicturePath("../images/Riley.png");
                }
            }/*else
            v.setPicturePath("../images/Riley.png");*/
        });


        this.buildings.forEach((k,v) -> {
            v.translateBuilding(getNodesOfBuilding(k), nodes).forEach((key,value) ->{
                nodes.put(key, value);
            });
        });


        /* basic layout */
        //s.initStyle(StageStyle.TRANSPARENT);  // <-- removes the top part of the app close/open [switch to UNDECORATED]

        this.myDisplay = new Display(this);    //creates scene
        display = myDisplay.Init(); //initializes scene

    }

    /** Starts the visual UI when ready **/
    @Override
    public void start(Stage s) throws Exception {

        stage = s;

		/* basic layout */
        s.initStyle(StageStyle.DECORATED);  // <-- removes the top part of the app close/open
        s.setResizable(true);

        /* setup */
        s.setScene(display);                   //sets scene to display
        display.getStylesheets().add(myDisplay.getClass().getResource("style.css").toExternalForm());


        s.show();   //shows scene
        defaultMap();
        showTutorial();
    }


    /**
     * return the HashMap of Nodes [to display][of the current map]
     *
     * @param id
     * @return
     */
    public HashMap<Integer, INode> getNodesOfBuilding(int id) {

        HashMap<Integer, INode> value = new HashMap<>();

        nodes.forEach((k, v) -> {
            if (maps.get(v.getMap_id()).getBuildingID() == id) {
                value.put(k, v);
            }
        });
        return value;
    }

    /****************************************************************************************************************
                                    FUNCTIONS THAT ARE CALLED FROM UI AND CONTACT UI
     ****************************************************************************************************************/


    /**
     * Clears the path and data, and then zooms out
     */
    public void clear(){

        Directions.cleartotalDistance();

        this.startNode = null;
        this.endNode = null;
        this.pathNodes = null;
        this.fullPath = null;

        flipFlop *= -1;
        boolean full = stage.isFullScreen();

        if (full) {
            stage.setFullScreen(!full);
            stage.setFullScreen(full);
        } else {
            stage.setWidth(stage.getWidth() + flipFlop);
        }

            this.myDisplay.start.getEditor().clear();
            this.myDisplay.end.getEditor().clear();
        }


    /**
     * Creates the freeze image of the background
     * @param background
     * @return
     */
    public StackPane freeze(Node background) {
        Image frostImage = background.snapshot(
                new SnapshotParameters(),
                null
        );

        ImageView frost = new ImageView(frostImage);

        Rectangle filler = new Rectangle(0, 0, this.stage.getWidth(), this.stage.getHeight());
        filler.widthProperty().bind(this.stage.widthProperty());
        filler.heightProperty().bind(this.stage.heightProperty());
        filler.setFill(Color.AZURE);

        Pane frostPane = new Pane(frost);
        frostPane.setEffect(frostEffect);

        StackPane frostView = new StackPane(
                filler,
                frostPane
        );

        Rectangle clipShape = new Rectangle(0, 0, this.stage.getWidth(), this.stage.getHeight());
        clipShape.heightProperty().bind(this.stage.heightProperty());
        clipShape.widthProperty().bind(this.stage.widthProperty());
        frostView.setClip(clipShape);



        //clipShape.yProperty().bind(y);

        return frostView;
    }

    private void showTutorial(){
        if (User.isUserNew()){//User.isUserNew()
            StackPane imageStack = new StackPane();

            javafx.scene.Node frost      = freeze(this.myDisplay.root);

            imageStack.setOnMouseClicked(e -> {
                myDisplay.root.getChildren().removeAll(imageStack, frost);
            });

            Label hello     = new Label ("Hello!");
            Label tutorial = new Label("It looks like you are new to CapraNav!");
            Label subTutorial = new Label("Would you like a tutorial?");
            Button no       = new Button("No, thank you");
            Button yes      = new Button("Yes, please");

            no.setId("tutorial-button");
            no.setTextFill(Color.web("#eee"));
            yes.setId("tutorial-button");
            yes.setTextFill(Color.web("#eee"));

            VBox greetings  = new VBox();
            greetings.setSpacing(25);

            HBox buttons    = new HBox();
            buttons.getChildren().addAll(no, yes);

            no.setOnAction(e -> {
                myDisplay.root.getChildren().removeAll(imageStack, frost);
            });

            yes.setOnAction(e -> {
                myDisplay.root.getChildren().removeAll(imageStack, frost);
                this.playTutorial();
            });

            hello.setId("introLabel");
            tutorial.setId("introSubLabel");
            subTutorial.setId("introSubLabel");
            tutorial.setTranslateY(-25);
            subTutorial.setTranslateY(-50);
            buttons.setTranslateY(-50);
            //no.setId("popoverButtons");
            //yes.setId("popoverButtons");

            greetings.getChildren().addAll(hello, tutorial, subTutorial, buttons);
            imageStack.getChildren().addAll(greetings);
            buttons.setAlignment(Pos.CENTER);
            //buttons.setTranslateX(-47);
            buttons.setSpacing(5);
            greetings.setAlignment(Pos.CENTER);
            this.myDisplay.root.getChildren().addAll(frost, imageStack);
        }

    }

    private void playTutorial(){
        this.myDisplay.showToolTips();
    }

    public void showAboutPanel() {
        StackPane imageStack = new StackPane();
        javafx.scene.Node frost      = freeze(this.myDisplay.root);

        imageStack.setOnMouseClicked(e -> {
            myDisplay.root.getChildren().removeAll(imageStack, frost);
        });


        //customize the stackpane here
        VBox vbox = new VBox();
        vbox.setId("about-panel");
        vbox.setSpacing(8);
        vbox.setAlignment(Pos.TOP_CENTER);

        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.setId("about-title");
        Label aboutLabel = new Label("About CapraNav");
        aboutLabel.setId("about-label");
        aboutLabel.setTextFill(Color.web("#eeeeee"));

        hbox.getChildren().add(aboutLabel);

        Image goatLogo = FileFetch.getImageFromFile("goat-logo.png");
        ImageView goatLogoView = new ImageView(goatLogo);

        FlowPane flowPane = new FlowPane();
        Text text = new Text();
        text.setId("about-text");
        text.setWrappingWidth(500);
        //text.setTextAlignment(TextAlignment.JUSTIFY);
        text.setText(
                "CapraNav was created for a software engineering class at Worcester Polytechnic Institute during B term of 2015. " +
                "The team consisted of nine members ranging from sophomores to seniors with various backrounds.\n\nMembers included " +
                "Kurt Bugbee, Josh Friscia, Mike Giancola, Jacob Hackett, Charlie Lovering, Tucker Martin, Anthony Ratte, Greg Tighe and Henry Wheeler-Mackta. " +
                "The professor for the course was Wilson Wong and the coach for this team was Nilesh Patel."
        );

        flowPane.setPrefWrapLength(500);
        flowPane.setAlignment(Pos.CENTER);
        flowPane.getChildren().add(text);

        Button attributions = new Button();
        attributions.setId("about-button");
        attributions.setText("Credits");
        attributions.setTextFill(Color.BLUE);
        attributions.setTextAlignment(TextAlignment.CENTER);

        goatLogoView.setTranslateY(20);
        flowPane.setTranslateY(25);
        attributions.setTranslateY(40);

        attributions.setOnMouseClicked(e -> {
            this.myDisplay.root.getChildren().removeAll(imageStack, frost);
            this.showCredits();
        });

        vbox.getChildren().addAll(hbox, goatLogoView, flowPane, attributions);
        imageStack.getChildren().add(vbox);
        this.myDisplay.root.getChildren().addAll(frost, imageStack);

    }

    /**
     * Shows credits
     */
    public void showCredits() {
        StackPane imageStack = new StackPane();
        javafx.scene.Node frost      = freeze(this.myDisplay.root);

        imageStack.setOnMouseClicked(e -> {
            myDisplay.root.getChildren().removeAll(imageStack, frost);
        });

        VBox vbox = new VBox();
        vbox.setId("about-panel");
        vbox.setSpacing(8);
        vbox.setAlignment(Pos.TOP_CENTER);

        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.setId("about-title");
        Label aboutLabel = new Label("Credits");
        aboutLabel.setId("about-label");
        aboutLabel.setTextFill(Color.web("#eeeeee"));

        hbox.getChildren().add(aboutLabel);

        FlowPane teamFlowPane = new FlowPane();
        teamFlowPane.setPrefWrapLength(400);
        teamFlowPane.setAlignment(Pos.CENTER);
        teamFlowPane.setHgap(20);
        teamFlowPane.setVgap(8);

        Label teamLabel = new Label("Team Members");
        teamLabel.setId("about-text");
        teamLabel.setStyle("-fx-font-weight: bold");
        ArrayList<String> teamMembers = new ArrayList<>();
        ArrayList<String> positions = new ArrayList<>();

        teamMembers.add("Kurt Bugbee");teamMembers.add("Josh Friscia");teamMembers.add("Mike Giancola");teamMembers.add("Jacob Hackett");
        teamMembers.add("Charlie Lovering");teamMembers.add("Tucker Martin");teamMembers.add("Anthony Ratte");teamMembers.add("Greg Tighe");teamMembers.add("Henry 'Whackta' Wheeler-Mackta");
        positions.add("Product Owner 2");positions.add("Project Manager 2");positions.add("Product Owner 1");positions.add("Lead Software Engineer 1");
        positions.add("\t  Lead UI/UX 1\nLead Software Engineer 2");positions.add("Lead UI/UX 2");positions.add("Test Engineer 2");positions.add("Project Manager 1");positions.add("Test Engineer 1");

        int i = 0;
        for(String name : teamMembers) {
            VBox vbox1 = new VBox();
            vbox1.setAlignment(Pos.CENTER);
            vbox1.setSpacing(8);
            Circle circle = new Circle();
            circle.setRadius(60);
            //Image person = FileFetch.getImageFromFile("goat-logo.png");
            Image person = FileFetch.getImageFromFile(name + ".png");
            circle.setFill(new ImagePattern(person));
            Label label = new Label(name);
            label.setStyle("-fx-font-size:12;");
            Label positionLabel = new Label(positions.get(i));
            positionLabel.setStyle("-fx-font-size:10;");
            positionLabel.setTranslateY(-7);
            positionLabel.setAlignment(Pos.CENTER);
            vbox1.getChildren().addAll(circle, label, positionLabel);
            teamFlowPane.getChildren().add(vbox1);
            i++;
        }

        Label iconLabel = new Label("Icon Authors");
        iconLabel.setId("about-text");
        iconLabel.setStyle("-fx-font-weight: bold");

        //TODO add hyperlinks here for authors and flaticon
        HBox iconBox = new HBox();
        iconBox.setAlignment(Pos.TOP_CENTER);
        iconBox.setSpacing(16);
        VBox iconLeftBox =new VBox();
        VBox iconRightBox = new VBox();

        Text iconLeftText = new Text();
        iconLeftText.setStyle("-fx-font-size:12;");
        iconLeftText.setText("Question mark icon made by Daniel Bruce from FlatIcon\n" +
                "Picture icon made by FreePik from FlatIcon\n" +
                "Settings icon made by FreePik from FlatIcon\n" +
                "Email icon made by icon-works.com from FlatIcon\n" +
                "Location pin icon made by FreePik from FlatIcon");

        Text iconRightText = new Text();
        iconRightText.setStyle("-fx-font-size:12;");
        iconRightText.setText("Paper airplane icon made by FreePik from FlatIcon\n" +
                "Stair icon made by FreePik from FlatIcon\n" +
                "Elevator icon made by FreePik from FlatIcon\n" +
                "Parking icon made by Google from FlatIcon\n" +
                "Bathroom icon made by FreePik from FlatIcon");

        iconLeftBox.getChildren().add(iconLeftText);
        iconRightBox.getChildren().add(iconRightText);

        iconBox.getChildren().addAll(iconLeftBox, iconRightBox);

        vbox.getChildren().addAll(hbox, teamLabel,teamFlowPane, iconLabel, iconBox);
        imageStack.getChildren().add(vbox);
        this.myDisplay.root.getChildren().addAll(frost, imageStack);
    }

    public void showNodeImage(INode node) {
        StackPane imageStack = new StackPane();
        javafx.scene.Node frost      = freeze(this.myDisplay.root);

        imageStack.setOnMouseClicked(e -> {
            myDisplay.root.getChildren().removeAll(imageStack, frost);
        });

        imageStack.setOnMouseClicked(e -> {
            myDisplay.root.getChildren().removeAll(imageStack, frost);
        });

        //add image to stack pane -> if no image return void
        //TODO make this image scale with resizing
        Image image = FileFetch.getImageForPictures(node.getPicturePath(), 800, 800, true, true);
        ImageView iv = new ImageView(image);

        imageStack.getChildren().add(iv);

        this.myDisplay.root.getChildren().addAll(frost, imageStack);
    }

    /**
     * Sends email
     * @param email
     * @return
     */
    public boolean sendEmail(String email){
        INode end = null;
        String startString = null;
        String endString = null;
        ArrayList<String> simplifiedInstruction = new ArrayList<>();
        if (fullPath == null) return false;
        for (ArrayList<Instructions> il : fullPath){
            for (Instructions i : il){
                simplifiedInstruction.add(i.getInstruction_string());
                end = i.getNode();
            }
        }

        if (fullPath != null) {
            INode start = fullPath.get(0).get(0).getNode();
            if (start.isInteresting()) {
                startString = start.getNames().get(0);
            } else if (start.isTransition()) {
                startString = start.toString();
            }
            if (end != null && end.isInteresting()) {
                endString = end.getNames().get(0);
            } else if (start.isTransition()) {
                endString = end.toString();
            }
        }

        logic.Email e = new logic.Email(email);
        if(simplifiedInstruction.size() != 0 && startString != null && endString != null) {
            return e.sendDirections(simplifiedInstruction, startString, endString);
        }
        else return false;
    }

    /**
     * returns the nodes
     * @return
     */
    public HashMap<Integer, INode> getNodes(){
        return nodes;
    }


    /**
     * This begins replaces getNamedNodes, it returns all 'named' nodes
     * @return
     */
    public HashMap<Integer, INode> getInterestingNodes(){
        HashMap<Integer, INode> value = new HashMap<>();

        nodes.forEach((k,v) -> {
             if(v.isInteresting()){
                 value.put(k, v);}});

        return value;
    }


    /**
     * This handles the values from the SEARCH BARS
     * @param id
     * @param START
     */
    public void handleSearchInput(int id, boolean START) {
        rightButtonFlag = true;
        FIRST = START; //Set START so when / if map clicked properly sets start/end node

        if (START) {

            if (endNode != null && id == endNode.getID() ){ //&& startNode != null
                endNode = startNode;
                this.myDisplay.start.setValue(this.myDisplay.end.getValue());
            }

            if (startNode != null) {
                //this visually hides the last start node -> sets it to normal
                this.myDisplay.mapDisplay.hideLast(startNode);
                //this.myDisplay.mapDisplay.revertPathNodes();
            }

            this.startNode = nodes.get(id);
        } else {

            if (startNode != null && id == startNode.getID() ){ //&& startNode != null
                startNode = endNode;
                this.myDisplay.end.setValue(null);//this.myDisplay.start.getValue()
                // this.myDisplay.mapDisplay.setStartNode(endNode);
            }

            if (startNode != null) {
                //this visually hides the last start node -> sets it to normal
                this.myDisplay.mapDisplay.hideLast(startNode);
                //this.myDisplay.mapDisplay.revertPathNodes();
            }

            this.endNode = nodes.get(id);

        }


        if (startNode != null && endNode != null) {
            findPaths();
        } else if (startNode != null) switchMapSetting(startNode.getMap_id());

        if (startNode != null && endNode == null) {
            //Hopefully in find paths this is taken care of - will confirm later
            if (startNode.getMap_id() == currentMap.getID()) {
                //gotta switch maps
                //switchMapSetting(startNode.getMap_id());
                myDisplay.mapDisplay.setStartNode(startNode);
            }
        }

        if (endNode != null && startNode == null) {
            if (endNode.getMap_id() == currentMap.getID()) {
                myDisplay.mapDisplay.setEndNode(endNode, true);
            } else {
                myDisplay.mapDisplay.setEndNode(endNode, false);
            }
        }

        flipFlop *= -1;
        stage.setWidth(stage.getWidth() + flipFlop);
        rightButtonFlag = false;

    }

    /**
     * This function is the one you want when you
     * @param t
     */
    public void handleEnterBuilding(Transition t){

        this.currentMap = maps.get(t.getMap_id());
        this.currentBuilding = t.getBuildingID();
        this.currentFloor = t.getToFloor();
        switchToBuildingView(t.getBuildingID(), t.getToFloor());
        handleMapLines();
    }

    /**
     * Gets the map id of a given node
     */
    public int getMapOfNode(INode iNode){
        return this.maps.get(iNode.getMap_id()).getID();
    }


    /**
     * Switches to the map of the given id
     * @param mapId
     */
    public void switchMapSetting(int mapId){
        if (maps.get(mapId).getID() == 0){
            hideBuildingPane();
            if (currentMap.getID() == 0) {
                firstTime = true;
            }
            defaultMap();
            prevBuilding = 0;

        } else {
            this.currentMap = maps.get(mapId);
            this.currentBuilding = currentMap.getBuildingID();
            this.currentFloor = currentMap.getFloor();

            showBuildingPane();//changes the pane to visible
            switchToBuildingView(currentBuilding, currentFloor);
        }
    }

    /**
     * Switches to the view for buildings
     * @param buildingID
     * @param startingFLOOR
     */
    public void switchToBuildingView(int buildingID, int startingFLOOR){
        this.currentBuilding = buildingID;
        this.currentMap      = maps.get(buildings.get(buildingID).getFloorID(startingFLOOR));
        this.currentFloor    = startingFLOOR;

        myDisplay.setBuildingName(buildings.get(buildingID).getName());
        showBuildingPane();
        setFloor(startingFLOOR);
    }

    /**
     * Increases the floor we're on
     */
    public void handleIncreaseFloorButton(){
        if (currentBuilding != 0 && buildings.get(currentBuilding).getFloorMap().containsKey(currentFloor + 1)){
            //setCurrentMap(buildings.get(currentBuilding).getFloorMap().get(++currentFloor));
            setFloor(++currentFloor);
        }
    }

    /**
     * goes down in floors
     */
    public void handleDecreaseFloorButton(){
        if (currentBuilding != 0 && buildings.get(currentBuilding).getFloorMap().containsKey(currentFloor - 1)){
            //setCurrentMap(buildings.get(currentBuilding).getFloorMap().get(--currentFloor));
            setFloor(--currentFloor);
        }
    }


    public void setFloor(int i){
        if (buildings.get(currentBuilding).containsFloor(i)) {

            this.currentFloor = i;
            this.myDisplay.setBuildingNumber(i); //TODO Change this to something better / more informatative
            //handleMapLines(); //removes old lines
//            if (currentMap.getID() == buildings.get(currentBuilding).getFloorID(i)) {
//                System.out.println("HERE 4");
//                firstTime = true;
//            }

            /** switches to the map **/
            //Play rotation animation if previous building is the campus map
            if (prevBuilding == 0 && !rightButtonFlag) {
                int buildNum = buildings.get(currentBuilding).getID();
                this.getMyDisplay().mapDisplay.rotationAnimation(buildNum);
                this.getMyDisplay().mapDisplay.timeline.setOnFinished(e ->
                        setCurrentMap(buildings.get(currentBuilding).getFloorID(i)));
            }
            //otherwise just set the current map
            else setCurrentMap(buildings.get(currentBuilding).getFloorID(i));

            /** CSS SWITCH LOGIC **/
            if (currentBuilding != 0 && buildings.get(currentBuilding).getFloorMap().containsKey(currentFloor + 1)) {
                //set id for normal
                this.myDisplay.setRightButtonID("arrow-buttons");
            } else {
                //set id for grey
                this.myDisplay.setRightButtonID("arrow-buttons-grayed");
                this.myDisplay.setRightButtonStyle("-fx-background-color:#eee;");
            }


            if (currentBuilding != 0 && buildings.get(currentBuilding).getFloorMap().containsKey(currentFloor - 1)) {
                //set id for normal
                this.myDisplay.setLeftButtonID("arrow-buttons");
            } else {
                //set id for grey
                this.myDisplay.setLeftButtonID("arrow-buttons-grayed");
                this.myDisplay.setLeftButtonStyle("-fx-background-color:#eee;");

            }

            /** update arrows **/

            if (fullPath != null && PATH_FLAG) {

                if(currentMap.getID() != 0) {

                    for (int z = 0; z < fullPath.size(); z++) {
                        if (fullPath.get(z).get(0).getNode().getMap_id() == currentMap.getID()) {
                            currentIndex = z;
                            break;
                        }
                    }

                } else {


                    ArrayList<Integer> outsides = new ArrayList<>();

                    for (int z = 0; z < fullPath.size(); z++) {
                        if (fullPath.get(z).get(0).getNode().getMap_id() == 0) {
                            outsides.add(z);
                        }
                    }

                    if (outsides.size() == 1){
                        currentIndex = outsides.get(0);
                    } else {
                        int smallestDifference = Integer.MAX_VALUE;
                        int tempIndex          = -1;

                        for(int j = 0; j < outsides.size(); j++){
                            if (smallestDifference < (outsides.get(j) - currentIndex) && (outsides.get(j) - currentIndex) > 0){
                                smallestDifference = (outsides.get(j) - currentIndex);
                                tempIndex          =  outsides.get(j);
                            }
                        }

                        currentIndex = tempIndex;
                    }
                }

                if (currentIndex != -1) {
                    myDisplay.setInstructions(fullPath.get(currentIndex)); //TODO UPDATE setInstructions
                } else {
                    this.myDisplay.clearInstructions();
                }



                if (fullPath != null && fullPath.size() > 0 &&  this.currentIndex + 1 < fullPath.size()){
                    this.myDisplay.setIDRightArrowButton("arrow-buttons");
                } else {
                    this.myDisplay.setIDRightArrowButton("arrow-buttons-grayed");
                }

                if (fullPath != null && fullPath.size() > 0 && this.currentIndex - 1 > -1) {
                    this.myDisplay.setIDLeftArrowButton("arrow-buttons");
                } else {
                    this.myDisplay.setIDLeftArrowButton("arrow-buttons-grayed");
                }


            }

        }
        //rightButtonFlag = false;
        prevBuilding = currentBuilding;
    }

    /**
     * Hides the pane displaying building pane: name and arrows
     */
    public void hideBuildingPane(){
        this.myDisplay.BUILDING_VISIBLE.setValue(false);
    }

    /**
     * Show the pane displaying building pane: names and arrows
     */
    public void showBuildingPane(){
        this.myDisplay.BUILDING_VISIBLE.setValue(true);
    }

    /**
     * Sets the current map, both the variable and the display
     * @param id
     */
    public void setCurrentMap(int id){
        flipFlop *= -1;
        this.currentMap = maps.get(id);
        this.myDisplay.mapDisplay.setMap(maps.get(id));
        handleMapLines();
        //stage.setWidth(stage.getWidth()-flipFlop);
        boolean full = stage.isFullScreen();

        if(!firstTime){
            if(full) {
                stage.setFullScreen(!full);
                stage.setFullScreen(full);
            }
            else {
                stage.setWidth(stage.getWidth() + flipFlop);
            }

        }
        firstTime = false;


        if (PATH_FLAG) {
            if (fullPath != null) {

                if (currentMap.getID() != 0) {

                    for (int z = 0; z < fullPath.size(); z++) {
                        if (fullPath.get(z).get(0).getNode().getMap_id() == currentMap.getID()) {
                            currentIndex = z;
                            break;
                        }
                    }

                } else {
                    //campus map


                    ArrayList<Integer> outsides = new ArrayList<>();

                    for (int z = 0; z < fullPath.size(); z++) {
                        if (fullPath.get(z).get(0).getNode().getMap_id() == 0) {
                            outsides.add(z);
                        }
                    }

                    if (outsides.size() == 1) {
                        currentIndex = outsides.get(0);
                    } else {
                        int smallestDifference = Integer.MAX_VALUE;
                        int tempIndex = -1;

                        for (int j = 0; j < outsides.size(); j++) {
                            if (smallestDifference < (outsides.get(j) - currentIndex) && (outsides.get(j) - currentIndex) > 0) {
                                smallestDifference = (outsides.get(j) - currentIndex);
                                tempIndex = outsides.get(j);
                            }
                        }

                        currentIndex = tempIndex;
                    }
                }


                if (currentIndex != -1) {
                    myDisplay.setInstructions(fullPath.get(currentIndex)); //TODO UPDATE setInstructions
                } else {
                    this.myDisplay.clearInstructions();
                }


                if (fullPath != null && fullPath.size() > 0 && this.currentIndex + 1 < fullPath.size()) {
                    this.myDisplay.setIDRightArrowButton("arrow-buttons");
                } else {
                    this.myDisplay.setIDRightArrowButton("arrow-buttons-grayed");
                }

                if (fullPath != null && fullPath.size() > 0 && this.currentIndex - 1 > -1) {
                    this.myDisplay.setIDLeftArrowButton("arrow-buttons");
                } else {
                    this.myDisplay.setIDLeftArrowButton("arrow-buttons-grayed");
                }


            }
        }
    }

    /**
     * Handles the showing of lines and switching of lastmapid
     */
    public void handleMapLines(){
        myDisplay.mapDisplay.showLines(lastMapID, currentMap.getID());
        lastMapID = currentMap.getID();
    }


    /**
     * THIS IS POST TEMP NODE CREATION
     * We are going to either
     *      A: Set it as the new Start Value
     *      B: Set it as the new End Value
     * Then change the value in the boxes
     *
     * @param n
     */
    public void handleMapClick(INode n){
        /**
         * This function SHOULD make it so there are up to two temporary nodes at a time
         * both start/end. But, it won't remove them til more are requested even if maps are
         * switched.
         */

        String item;

        if (!FIRST){

            item = myDisplay.start.nodeToString(n, currentMap);
            System.out.println(item);
            myDisplay.start.setValue(item);


        } else {

            item = myDisplay.end.nodeToString(n, currentMap);
            System.out.println(item);
            myDisplay.end.setValue(item);
        }
    }

    /**
     * Finds the nearest node on the same map
     * @param x
     * @param y
     * @return
     */
    public INode nearestNode(double x, double y){
        double distance = Double.MAX_VALUE;
        INode n = null;

        for(HashMap.Entry<Integer, INode> cursor : nodes.entrySet()){
            INode v = cursor.getValue();

            if(v.getMap_id() == currentMap.getID() && Math.sqrt((v.getX() - x)*(v.getX() - x) + (v.getY() - y)*(v.getY() - y)) < distance){
                    n = v;
                    distance = Math.sqrt((v.getX() - x)*(v.getX() - x) + (v.getY() - y)*(v.getY() - y));
                }
        }

        this.myDisplay.start.addNode(n, maps.get(n.getMap_id()), true); //maps(n.getMap_id())
        this.myDisplay.end.addNode(n, maps.get(n.getMap_id()), true); //maps(n.getMap_id())

        return n;
    }


    /**
     * Returns the id of the nearest node
     * @param x
     * @param y
     * @param z
     * @return
     */
    public ArrayList<INode> nearestNodeID(double x, double y, double z){
        double distance =   Double.MAX_VALUE;
        double distancea =  Double.MAX_VALUE;
        double distanceb =  Double.MAX_VALUE;

        INode a = null;
        INode b = null;
        INode c = null;

        ArrayList<INode> value = new ArrayList<>();

        for(HashMap.Entry<Integer, INode> cursor : nodes.entrySet()){
            INode v = cursor.getValue();
            double temp = Math.sqrt((v.getX() - x)*(v.getX() - x) + (v.getY() - y)*(v.getY() - y));

            if(a != null && b != null && v.getMap_id() == currentMap.getID() && temp < distanceb){
                c = v;
                distanceb = temp;
            } else if (a != null && v.getMap_id() == currentMap.getID() && temp < distancea){
                c = b;
                b = v;
                distanceb = distancea;
                distancea = temp;
            } else if(v.getMap_id() == currentMap.getID() && temp < distance){
                c = b;
                b = a;
                a = v;
                distanceb = distancea;
                distancea = distance;
                distance = temp;
            }
        }


        if (c != null){
            value.add(c);
        }
        if (b != null){
            value.add(b);
        }
        if (a != null){
            value.add(a);
        }

        return value;
    }


    /**
     * Returns the name of the nearest node of the location, that is named.
     * @param x
     * @param y
     * @param z
     * @return
     */
    public String nearestNamedNodeName(double x, double y, double z){
        double distance = Double.MAX_VALUE;
        INode n = null;


        for(HashMap.Entry<Integer, INode> cursor : nodes.entrySet()){
            INode v = cursor.getValue();
            if((v.isInteresting() ||  (v instanceof Transition && !(v instanceof TStairs || v instanceof Elevator))) && cursor.getKey() != -1 && cursor.getKey() != -2){ //we dont want the name Near Near Stratton Hall
                if(v.getMap_id() == currentMap.getID() && Math.sqrt((v.getX() - x)*(v.getX() - x) + (v.getY() - y)*(v.getY() - y)) < distance ){
                    n = v;
                    distance = Math.sqrt((v.getX() - x)*(v.getX() - x) + (v.getY() - y)*(v.getY() - y));
                }
            }
        }

        if (n == null){
            //System.out.println("NO DISTANCE FOUND OR SOMETHING");
            return "Entrance";
        }
        return n.toString();
    }

    /**
     * Sets the current map to the campus map
     */
    public void defaultMap(){
        setCurrentMap(campus.getID());
    }

    /**
     * For the creation of search bar, get all the names of the building
     * @param building_id
     * @return
     */
    public ArrayList<String> getBuildingNames(int building_id){
        return this.buildings.get(building_id).getNames();
    }

    public IMap getCurrentMap() {
        return this.currentMap;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Get's the path nodes from the start node to the end node
     * @param s
     * @param f
     * @return
     */
    public ArrayList<logic.INode> getPathNodes(logic.INode s, logic.INode f){
        this.pathNodes = AStarShortestPath.AStarSearch(s, f, nodes);
        return pathNodes;
    }

    /**
     * gets the instructions by via pathNodes set by getPathNodes
     * @return an ArrayList<String?
     */
    public void getInstructions(){
        this.fullPath =  Directions.stepByStep(this.pathNodes, this.maps, this.buildings);
    }


    /**
     * return the HashMap of Nodes that have NAMES!!
     */
    public HashMap<Integer, INode> getNamedNodesOfMap(){
        HashMap<Integer, INode> value = getNodesOfMap(this.currentMap.getID());
        nodes.forEach((k,v) -> {
            if (k !=0) {
                //if (v.getName().equals("ENTER TEXT") || v.getName().equals("")) {
                if(!v.isInteresting()){
                    value.remove(k, v);}}});
        return  value;
    }



    /**
     * return the HashMap of Nodes [to display][of the current map]
     * @param id
     * @return
     */
    public HashMap<Integer, INode> getNodesOfMap(int id){

        HashMap<Integer, INode> value = new HashMap<>();

        nodes.forEach((k,v) -> {
            if(v.getMap_id() == id){
                value.put(k,v);
            }
        });

        return value;
    }


    /**
     * Handles searches for specific nodes - nearest/close/near by
     * @param s
     */
    public void handleSpecificSearch(String s) {

        String prefixes[] 	= {"Nearest ", "Close ", "Near by ", ""};
        String locations[] 	= {"Bathroom", "Men's Room", "Woman's Bathroom" , "Food", "Restaurant",  };


        if (s.equals(prefixes[0]+locations[0]) || s.equals(prefixes[1]+locations[0]) || s.equals(prefixes[2]+locations[0]) || s.equals(prefixes[3]+locations[0])) {
            setDestination(findNearestRoomType(Bathroom.bathRoomTypeToString(BathroomType.GENERAL)), false);
        } else if (s.equals(prefixes[0]+locations[1]) || s.equals(prefixes[1]+locations[1]) || s.equals(prefixes[2]+locations[1]) || s.equals(prefixes[3]+locations[1])) {
            setDestination(findNearestRoomType(Bathroom.bathRoomTypeToString(BathroomType.MENS)), false);
        } else if (s.equals(prefixes[0]+locations[2]) || s.equals(prefixes[1]+locations[2]) || s.equals(prefixes[2]+locations[2]) || s.equals(prefixes[3]+locations[2])) {
            setDestination(findNearestRoomType(Bathroom.bathRoomTypeToString(BathroomType.WOMAN)), false);
        } else if (s.equals(prefixes[0]+locations[3]) || s.equals(prefixes[1]+locations[3]) || s.equals(prefixes[2]+locations[3]) || s.equals(prefixes[3]+locations[3])) {
            setDestination(findNearestFood(), false);
        } else if (s.equals(prefixes[0]+locations[4]) || s.equals(prefixes[1]+locations[4]) || s.equals(prefixes[2]+locations[4]) || s.equals(prefixes[3]+locations[4])) {
            setDestination(findNearestFood(), false);
        }
    }

    /**
     * Sets the desintation as the give node
     * @param destination
     * @param START
     */
    private void setDestination(INode destination, boolean START){
        handleSearchInput(destination.getID(), START);
    }

    /**
     * Finds the nearest Food node
     * @return
     */
    private INode findNearestFood(){
        if (startNode == null) {
            return  null;
        };

        double distance = Double.MAX_VALUE;
        INode n = null;

        ArrayList<INode> toSearch = new ArrayList<>();


        for (HashMap.Entry<Integer, INode> cursor : nodes.entrySet()) {
            INode v = cursor.getValue();
            if (v instanceof Food) { //we dont want the name Near Near Stratton Hall
                toSearch.add(v);
            }
        }

        for (INode node : toSearch){
            Directions.stepByStep(AStarShortestPath.AStarSearch(startNode, node, nodes), maps, buildings);
            if (Directions.getTotalDistance() < distance){
                distance = Directions.getTotalDistance();
                n = node;
            }
            Directions.cleartotalDistance();
        }

        return  n;
    }

    /**
     * Finds the nearest room of a given type - set up for bathrooms only
     * @param type
     * @return
     */
    private INode findNearestRoomType(String type){

        if (startNode == null) {
            return  null;
        };

        double distance = Double.MAX_VALUE;
        INode n = null;

        ArrayList<INode> inBuilding = new ArrayList<>();

        if (startNode.getMap_id() == 0) {

            for (HashMap.Entry<Integer, INode> cursor : nodes.entrySet()) {
                INode v = cursor.getValue();
                if (v.toString().equals(type) && v.getMap_id() == startNode.getMap_id()) { //we dont want the name Near Near Stratton Hall
                    inBuilding.add(v);
                }
            }

        } else {

            for (HashMap.Entry<Integer, INode> cursor : nodes.entrySet()) {
                INode v = cursor.getValue();
                if (v.toString().equals(type)) { //we dont want the name Near Near Stratton Hall
                    inBuilding.add(v);
                }
            }
        }

        for (INode node : inBuilding){
            Directions.stepByStep(AStarShortestPath.AStarSearch(startNode, node, nodes), maps, buildings);
            if (Directions.getTotalDistance() < distance){
                distance = Directions.getTotalDistance();
                n = node;
            }
            Directions.cleartotalDistance();
        }

        return  n;
    }


    /**
     * Returns the node given the id
     * @param id
     * @return
     */
    public INode getNode(Integer id){
        return nodes.get(id);
    }

    /**
     * This will kick everything off!
     * We can later change it so other things trigger this.
     * We also have to think about clearing things
     */
    public void findPaths(){
        lastSoft = -1;
        //Creates instructions - setting fullPath
        //this.myDisplay.clearInstructions();
        getPathNodes(startNode, endNode);
        getInstructions();



        //set ids of buttons
        if (fullPath != null && fullPath.size() > 0 &&  this.currentIndex + 1 < fullPath.size()){
            this.myDisplay.setIDRightArrowButton("arrow-buttons");
        }

        if (fullPath != null && fullPath.size() > 0 && this.currentIndex - 1 > -1) {
            this.myDisplay.setIDLeftArrowButton("arrow-buttons");
        }

        //current index of which arraylist of instructions we are displaying
        currentIndex = 0;//start off with the first list



        //the last map we wee showing
        lastMapID = fullPath.get(currentIndex).get(0).getNode().getMap_id();

        //sets the instructions to the corresponding list of instructions to the first path
        myDisplay.setInstructions(fullPath.get(currentIndex)); //TODO UPDATE setInstructions

        //create the path
        myDisplay.mapDisplay.createPath(fullPath);

        //switch the map to the building
        switchMapSetting(startNode.getMap_id());

        //shows the lines

        //myDisplay.mapDisplay.showLines(-1, lastMapID); //TODO UPDATE showPath
        myDisplay.updateTimeEstimation();
        myDisplay.TIME_VISIBLE.setValue(true);

        myDisplay.mapDisplay.previousPopOver.hide();
    }

    /**
     * Handles the organization of data and events when the user increments
     * the floor on the map
     */
    public void handleIncrementPathMap(){
        //if there is another list of instructions to go
        PATH_FLAG = false;
        rightButtonFlag = true;
        if (fullPath != null && fullPath.size() > 0 &&  this.currentIndex + 1 < fullPath.size()){
            myDisplay.setInstructions(fullPath.get(++currentIndex)); //TODO UPDATE setInstructions
            switchMapSetting(fullPath.get(currentIndex).get(0).getNode().getMap_id());
            this.myDisplay.mapDisplay.softSelectAnimation(lastSoft, fullPath.get(currentIndex).get(0).getNode().getID());
            lastSoft = fullPath.get(currentIndex).get(0).getNode().getID();
            this.myDisplay.mapDisplay.showLines(lastMapID, fullPath.get(currentIndex).get(0).getNode().getMap_id()); //TODO UPDATE showPath
        }

        if (fullPath != null && fullPath.size() > 0 &&  this.currentIndex + 1 < fullPath.size()){
            this.myDisplay.setIDRightArrowButton("arrow-buttons");
        } else {
            this.myDisplay.setIDRightArrowButton("arrow-buttons-grayed");
        }
        rightButtonFlag = false;
        PATH_FLAG = true;

    }

    /**
     * Handles the organization of data and events when the user decrements
     * the floor on the map
     */
    public void handleDecrementPathMap(){
        PATH_FLAG = false;
        rightButtonFlag = true;
        //if there is another list of instructions to go
        if (fullPath != null && fullPath.size() > 0 && this.currentIndex - 1 > -1){
            myDisplay.setInstructions(fullPath.get(--currentIndex)); //TODO UPDATE setInstructions
            switchMapSetting(fullPath.get(currentIndex).get(0).getNode().getMap_id());
            this.myDisplay.mapDisplay.softSelectAnimation(lastSoft, fullPath.get(currentIndex).get(0).getNode().getID());
            lastSoft = fullPath.get(currentIndex).get(0).getNode().getID();
        }

        if (fullPath != null && fullPath.size() > 0 && this.currentIndex - 1 > -1) {
            this.myDisplay.setIDLeftArrowButton("arrow-buttons");
        } else {
            this.myDisplay.setIDLeftArrowButton("arrow-buttons-grayed");
        }
        rightButtonFlag = false;
        PATH_FLAG = true;
    }

    /**
     * Changes the weights corresponding to weather / handicap
     */
    public void handleWeightOptions(boolean weather, boolean handicap){
        rightButtonFlag = true;
        Weighted.resetEdges(nodes);
        if (weather)  Weighted.makeEdgesWeather(nodes);
        if (handicap) Weighted.makeEdgesHandicapped(nodes);
        if (startNode != null && endNode != null) {
            findPaths();
        }
        rightButtonFlag = false;
    }

    /****************************************************************************************************************
                                    Zooming Functions and Control
     ****************************************************************************************************************/



    public Building getBuilding(int buildingID) {
        return buildings.get(buildingID);
    }

    public Display getMyDisplay() {
        return this.myDisplay;
    }

    public Point2D getStageOffset(){
        double xOff = this.stage.getX();
        double yOff = this.stage.getY();

        double appWidth = this.stage.getWidth();
        double appHeight = this.stage.getHeight();
        double expandedWidth = this.getMyDisplay().expandedWidth;
        double gapWidth = this.getMyDisplay().GAP;
        boolean dashboardEx = this.getMyDisplay().DASHBOARD_VISIBLE.getValue();
        boolean directionsEx = this.getMyDisplay().DIRECTIONS_VISIBLE.getValue();

        double offsetX = 0;
        if (dashboardEx){
            offsetX += expandedWidth;
        }
        else {
            offsetX += gapWidth;
        }
        if(directionsEx){
            offsetX += expandedWidth;
        }
        else {
            offsetX += gapWidth;
        }

        offsetX /=2;

        Point2D offset = new Point2D(offsetX+xOff+appWidth/2,yOff+appHeight/2.5);

        return offset;
    }


    /****************************************************************************************************************
                                                  PARSING FUNCTIONS
     ****************************************************************************************************************/


    public HashMap<Integer, IMap> getMaps(){
        return this.maps;
    }

    /**
     * Load nodes to file
     * @param
     */
    private void nodesFromFile(){
        nodes = new Parser<INode>().fromFileGraph();
    }

    /**
     * Loads buildings from file
     */
    private void buildingsFromFile(){
        buildings = new Parser<Building>().fromFileBuilding();
    }

    /**
     * Loads maps from file
     */
    private void mapsFromFile() {
        maps = new Parser<IMap>().fromFileMap();
        campus = (Campus)maps.get(0);
    }


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        public static void main(String[] args) {
            LauncherImpl.launchApplication(Controller.class, myPreloader.class, args);
        }
}




//    /** RELIC
//     * Switch this to just the INode
//     * @param n
//     */
//    public void updateNodeInformation(INode n){
//        this.myDisplay.updateNodeIcon(n.getIcon(), n);
//        this.myDisplay.updateNodeTitle(n.toString());
//        this.myDisplay.ICON_VISIBLE.setValue(true);
//
//        if (n.getPicturePath() != null){
//            this.myDisplay.updatePictureIcon(true);
//            this.selectedInformationNode = n;
//        } else {
//            this.myDisplay.updatePictureIcon(false);
//            this.selectedInformationNode = null;
//        }
//    }




//    public void handleMapClick(INode n){
//        /**
//         * This function SHOULD make it so there are up to two temporary nodes at a time
//         * both start/end. But, it won't remove them til more are requested even if maps are
//         * switched.
//         */
//
//        if (!FIRST){ //If the last node we added was the first
//
//            if (!nodes.containsKey(n.getID())) {
//                //System.out.println("Adding to hashmap 1");
//                tempStart = n;
//                nodes.put(n.getID(), n);//puts the node in the map!
//            } else {
//                if (startNode != null){
//                    this.myDisplay.mapDisplay.hideLast(startNode);
//                }
//                this.FLAG = true;//this is set in attempts to avoid triggering stuff twice
//            }
////            this.FLAG = true;//this is set in attempts to avoid triggering stuff twice
//
//            myDisplay.start.setValue(myDisplay.start.addNode(n, currentMap));
//
//
//        } else {//Else if the last node we added was the last [note, a lot of this gets wonky when we add midway points, jesus
//            //this.endNode = n;
//
//            if (!nodes.containsKey(n.getID())) {
//                //System.out.println("Adding to hashmap 2");
//                tempEnd = n;
//                nodes.put(n.getID(), n);
//                this.FLAG = true;
//
//            } else {
//                if (endNode != null) this.myDisplay.mapDisplay.hideLast(endNode);
//            }
////            this.FLAG = true;
//
//            myDisplay.end.setValue(myDisplay.end.addNode(n, currentMap));
//
//        }
//
//        this.FLAG = false;//this should prevent some double triggering of events
//    }



//    /**
//     * This handles the values from the SEARCH BARS
//     * @param id
//     * @param START
//     */
//    public void handleSearchInput(int id, boolean START) {
//        this.myDisplay.mapDisplay.revertPathNodes();
//
//        FIRST = START; //Set START so when / if map clicked properly sets start/end node
//
//        //clears old path lines from display
//        //myDisplay.mapDisplay.revertPathNodes();
//
//
//        if (nodes.containsKey(id)) {
//
//            if (START) {//setting start
//                //System.out.println("START TRUE!");
//
//                //if start is A and end is B and user tries to make start B, then end will be A
//                if (endNode != null && id == endNode.getID() ){ //&& startNode != null
//                    endNode = startNode;
//                    this.myDisplay.start.setValue(this.myDisplay.end.getValue());
//                }
//
//                if (!FLAG) {//if not from a click on the map hide the last
//                    //System.out.println("FLAG FALSE!");
//                    if (startNode != null) {
//                        //this visually hides the last start node -> sets it to normal
//                        this.myDisplay.mapDisplay.hideLast(startNode);
//                        //this.myDisplay.mapDisplay.revertPathNodes();
//                    }
//                }
//
//                //set the startNode id
//                this.startNode = nodes.get(id);
//                //Else we are changing the EndNode
//            } else {
//
//                //flip startNode and endNode if need be
//                if (startNode != null && id == startNode.getID() ){ //&& startNode != null
//                    startNode = endNode;
//                    this.myDisplay.end.setValue(null);//this.myDisplay.start.getValue()
//                    // this.myDisplay.mapDisplay.setStartNode(endNode);
//                }
//
//
//                if (!FLAG) {//if not from a click on the map hide the last
//                    // System.out.println("FLAG FALSE!");
//                    if (endNode != null) {
//                        //  System.out.println("HIDING END!");
//                        this.myDisplay.mapDisplay.hideLast(endNode);
//                        // this.myDisplay.mapDisplay.revertPathNodes();
//                    }
//                }
//
//                this.endNode = nodes.get(id);
//            }
//
//            if (startNode != null && endNode != null) {
//                findPaths();
//            } else if (startNode != null) switchMapSetting(startNode.getMap_id());
//
//            if (startNode != null && endNode == null) {
//                //TODO if current map contains it, play, if it doesn't - switch and play
//                //Hopefully in find paths this is taken care of - will confirm later
//                if (startNode.getMap_id() == currentMap.getID()) {
//                    //gotta switch maps
//                    //switchMapSetting(startNode.getMap_id());
//                    myDisplay.mapDisplay.setStartNode(startNode);
//                }
//            }
//
//            if (endNode != null && startNode == null) {
//                // System.out.println("END NODE HIGHLIGHTED!");
//                System.out.println("THE END NODE: " + endNode.toString());
//                //TODO if current map contains it, play, if it doesn't - dont play, just set and color
//                if (endNode.getMap_id() == currentMap.getID()) {
//                    myDisplay.mapDisplay.setEndNode(endNode, true);
//                } else {
//                    myDisplay.mapDisplay.setEndNode(endNode, false);
//                }
//            }
//
//        }
//    }