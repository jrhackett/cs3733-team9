package visuals;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class UserInterface extends Application {
	private static final Double WINDOW_WIDTH = 900.0;
	private static final Double WINDOW_HEIGHT = 600.0;
	
	private Display myDisplay;
	
	@Override
	public void start(Stage s) throws Exception {
		
		/* icon */
		s.getIcons().add(new Image(getClass().getResourceAsStream("globe.png")));
		
		/* basics */	
		s.setTitle("WPI MAPS");
		s.setResizable(false);
		
		/* setup */
		myDisplay = new Display(WINDOW_WIDTH, WINDOW_HEIGHT);
		Scene display = myDisplay.Init();
		s.setScene(display);
		
		/* css */
		display.getStylesheets().add("visuals/style.css");
		s.show();	
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}