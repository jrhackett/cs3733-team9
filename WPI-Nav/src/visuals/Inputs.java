package visuals;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

import java.util.HashMap;

/**
 * Currently using a 'simple' ComboBox.
 * Looking into alternate/advanced/accurate options
 */
public class Inputs extends ComboBox {
	private double WIDTH;
	private String initial;
	ObservableList<logic.Map> dataMap;
	ObservableList<logic.Node> dataNode;
	ObservableList<InputItem> data;

	@SuppressWarnings("unchecked") /* probably should remove this */
	public Inputs(String s,double WIDTH ){
		super();
		this.WIDTH = WIDTH;
		initial = s;
		this.setMaxWidth(WIDTH); //TODO sizing here
		this.setMinWidth(WIDTH);
		this.setEditable(true);

		//		this.setItems(getDummyLocations());
		/*
		this.valueProperty().addListener(new ChangeListener<Node>(){
			
			@Override
			public void changed(ObservableValue<? extends Node> arg0, Node oldValue, Node newValue) {
				if (oldValue != null){
					System.out.print(oldValue.getName() + "->");
				}
				System.out.println(newValue.getName());
			
			}
		});
	*/

		/*
		this.setCellFactory(new Callback<ListView<Node>, ListCell<Node>>() {

			@Override
			public ListCell<Node> call(ListView<Node> param) {
				ListCell<Node> cell = new ListCell<>(){
					@Override
					public void updateItem(Node node, boolean empty){
						if (node != null){
							setText(node.toString());
							 
							
						}
						setTextFill(Color.GREEN);
					}
				}
				return null;
			}
			
		});
		*/
	}





	/****************************************************************************************************************
			ConvertMaps and ConvertNodes will no longer be used - we need to convert them together.
	 ****************************************************************************************************************/

	public ObservableList<InputItem> createInputItems(HashMap<Integer, logic.Node> nodes, HashMap<Integer, logic.Map> maps){
		this.data = FXCollections.observableArrayList();
		nodes.forEach((k,v) -> {
			//for each of its names


			//for each of the nodes's maps
					//create an item

			//TODO consider what we want for floor level etc - probably for rooms we just want the building name
			//TODO 	and if its a bathroom we want to grab the floor from the map



		});

		return data;
	}


	/**
	 * Converts a HashMap of maps to an ObserableList
	 * @param maps
	 * @return
     */
	public ObservableList<logic.Map> convertMaps(HashMap<Integer, logic.Map> maps) {
		this.dataMap = FXCollections.observableArrayList();
		maps.forEach((k,v) -> {dataMap.add(v);});
		return dataMap.sorted(); //NOTE: this wont work in java 8.40
	}

	/**
	 * Converts a HashMap of Nodes to an ObservableList
	 * @param nodes
	 * @return
	 */
	public ObservableList<logic.Node> convertNodes(HashMap<Integer, logic.Node> nodes) {
		this.dataNode = FXCollections.observableArrayList();
		nodes.forEach((k,v) -> {dataNode.add(v);});
		return dataNode.sorted();
	}




	public void addNode(logic.Node n){
		this.dataNode.add(n);
	}

	public void addMapToMaps(logic.Map map){
		dataMap.add(map);
	}

	
	
}