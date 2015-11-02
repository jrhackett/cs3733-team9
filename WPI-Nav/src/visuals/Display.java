package visuals;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Display {
	/* logger */
	private final Logger logger = LoggerFactory.getLogger(Display.class);
	
	/* constants */
	private static final double WIDTH_BUFFER = 25;
	private static final double HEIGHT_BUFFER = 25;
	private static final double GAP = 5.5;
	private static final double BUTTON_SIZE = 25;
	
	/* values */
    private static final Color BACKGROUND_COLOR = Color.web("a5adb0");

	private static final double TABLE_WIDTH = 600;

	private static final double TABLE_HEIGHT = 200;

    /* visuals */
	private Scene scene;
	private AnchorPane root;
	private Double width;
	private Double height;
	
	/* simply for testing!!!! */
	private static ObservableList<Instructions> getInstructionsTest(){
		ObservableList<Instructions> data = FXCollections.observableArrayList();
		
		for (int i = 0; i < 10; i++){
			data.add(new Instructions("Go North!!", 50));
		}
		
		return data;
		
	}
	
	public Display(Double width, Double height){
        root = new AnchorPane();
        this.width = width;
        this.height = height;
	}
	
	public Scene Init(){
		/* start */
		TextField start = new Inputs("Starting Location?");
		start.setTranslateX(WIDTH_BUFFER);
		start.setTranslateY(HEIGHT_BUFFER);
		
		/* end */
		TextField end = new Inputs("Destination!");
		end.setTranslateX(WIDTH_BUFFER);
		end.setTranslateY(HEIGHT_BUFFER * 2 + GAP);

		/* + button [later] */
		Label addButton = button("+", start.getMaxWidth(), 0);
				
		/* map */
					
		/* instructions */
		TableView<Instructions> instructions = createInstructionsTable();

		
		/* image */
		double dimension = width - (TABLE_WIDTH + 2 * WIDTH_BUFFER + GAP);
		ImageDisplay imageDisplay = new ImageDisplay(dimension);
		
		StackPane sp = new StackPane();
		sp.getChildren().add(imageDisplay);
		sp.setTranslateY(height - (HEIGHT_BUFFER + dimension));
		sp.setTranslateX(WIDTH_BUFFER);

		/* icon */

		
        root.getChildren().addAll(start, end, addButton, instructions, sp);
        scene = new Scene(root, width, height, BACKGROUND_COLOR);
        return scene;
	}

	private TableView<Instructions> createInstructionsTable() {
		TableView<Instructions> instructions = new TableView<Instructions>();
		instructions.setMinWidth(TABLE_WIDTH);
		instructions.setMaxWidth(TABLE_WIDTH);
		instructions.setMinHeight(TABLE_HEIGHT);
		instructions.setMaxHeight(TABLE_HEIGHT);
		instructions.getColumns().addAll(Instructions.getColumn(instructions));
		instructions.setItems(getInstructionsTest());
		instructions.setTranslateX(width - TABLE_WIDTH - WIDTH_BUFFER);
		instructions.setTranslateY(height - TABLE_HEIGHT - HEIGHT_BUFFER);
		instructions.setColumnResizePolicy(
	            TableView.CONSTRAINED_RESIZE_POLICY
		        );
		return instructions;
	}

	private StackPane createAddButton() {
		Text plus = new Text("+");
		plus.setFont(Font.font("Comic Sans", FontWeight.BOLD, 25));
	    return createButton(plus);
	}

	private StackPane createButton(Node content) {
		StackPane button = new StackPane();
		Rectangle background = new Rectangle(BUTTON_SIZE, BUTTON_SIZE, Color.web("#ABB8B8"));
		background.setArcHeight(7);
		background.setArcWidth(7);
		button.getChildren().addAll(background, content);
		return button;
	}
	
	private Label button(String content, double width, double height){
		Label button = new Label(content);
		button.getStyleClass().add("my_button");		
        button.setTranslateX(width + WIDTH_BUFFER + GAP);
		button.setTranslateY(height + HEIGHT_BUFFER);
		button.setMinSize(25, 25); button.setMaxSize(25, 25);
		
		DropShadow ds = new DropShadow();
		ds.setOffsetX(.5);
		ds.setOffsetY(.5);
		ds.setColor(Color.GRAY);
		button.setEffect(ds);
		
		return button;
	}
}
