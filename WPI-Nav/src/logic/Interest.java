package logic;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;

/**
 * Created by jacobhackett on 11/24/15.
 */
public abstract class Interest extends Node {

    private ArrayList<String> names = new ArrayList<>();

    public Interest(int id, double x, double y, double z, double x1, double y1, double z1, String name){
        super(id, x, y, z, x1, y1, z1);
        this.names = new ArrayList<>();
        this.names.add(name);
    }

    public Interest(int id, double x, double y, double z, double x1, double y1, double z1, int map_id, String name){
        super(id, x, y, z, x1, y1, z1, map_id);
        this.names = new ArrayList<>();
        this.names.add(name);
    }

    public Interest(INode iNode){
        super(iNode);
    }

    public boolean isInteresting() {
        return true;
    }

    public String getName() {
        return names.get(0);
    }

    /**
     * Returns the names of this node
     * @return
     */
    public ArrayList<String> getNames() {
        return names;
    }

    /**
     * Adds an ArrayList of String to the names
     * @param toAdd
     */
    public void addNames(ArrayList<String> toAdd) {
        names.addAll(toAdd);
    }

    public ImageView getIcon(){
        Image image = FileFetch.getImageFromFile("pin56_small.svg", 22, 22, true, true);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(22);
        imageView.setFitWidth(22);
        return imageView;
    }

    public String toString(){
        return getName();
    }



}
