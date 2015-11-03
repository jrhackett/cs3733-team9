import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/*
 * An implementation of a Graph of Nodes
 */
public class Graph 
{
	/*
	 * nodes is a HashMap of all nodes in the graph
	 */
	HashMap<Integer, Node> nodes;
	
	/*
	 * Constructor initializes the graph with a LinkedList of nodes
	 */
	public Graph(HashMap<Integer, Node> nodes)
	{
		this.nodes = nodes;
	}
	
	/*
	 * Prints all nodes and node values in the hashmap
	 */
	public String toString()
	{
		for(Node node : this.nodes.values())
		{
			System.out.println(node.toString());
		}
		return "done";
	}
	
	/*
	 * Methods to add:
	 * Search for node with certain name
	 * Search for node with certain x and y coordinates
	 */
	
}
