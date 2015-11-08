package logic;

import java.util.ArrayList;

public class Node {

	public String name;
	public int    id;
	public double x_coord;
	public double y_coord;
	public double z_coord;
	public double g_scores;
	public double h_scores;
	public double f_scores = 0;
	public ArrayList<Edge> adjacencies;
	public Node parent;

	public Node(String val, int id, double x, double y, double z) {
		name = val;
		this.id = id;
		x_coord = x;
		y_coord = y;
		z_coord = z;
		adjacencies = new ArrayList<Edge>();
	}

	public String getName() {
		return name;
	}
	
	public double getX() {
		return x_coord;
	}
	
	public double getY() {
		return x_coord;
	}
	
	public double getZ() {
		return x_coord;
	}

	public void addEdge(Edge edge) {
		adjacencies.add(edge);
	}

	public String toString()
	{
		return String.format("%s: %f, %f, %f\n", this.name, this.x_coord, this.y_coord, this.z_coord);
	}

}