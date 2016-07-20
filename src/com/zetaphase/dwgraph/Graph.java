package com.zetaphase.dwgraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class Graph<T>{

	private Map<T, Node<T>> adjacencyList;
	
	private List<T> vertexList = new ArrayList<T>();
	
	public Graph(){
		adjacencyList = new HashMap<>();
	}
	
	public boolean isLoop(){
		List<T> visited = new ArrayList<T>();
		for (T vertex1 : vertexList){
			for (T vertex2 : vertexList){
				if (!visited.contains(vertex2)&&(vertex1!=vertex2)){
					if ((shortestPath(vertex1, vertex2)!=null)&&(shortestPath(vertex2, vertex1)!=null)){
						return true;
					}
				}
			}
			visited.add(vertex1);
		}
		return false;
	}
	
	// add vertex to graph
	public boolean addVertex(T vertex){
		if (adjacencyList.containsKey(vertex)){
			return false;
		}
		vertexList.add(vertex);
		adjacencyList.put(vertex, new Node<>(vertex));
		return true;
	}
	//adds direct edge between two vertices in a graph
	public boolean addEdge(T vertex1, T vertex2){
		return addEdge(vertex1, vertex2, 0);
	}
	//adds weighted edge between two vertices in a graph
	public boolean addEdge(T vertex1, T vertex2, int weight){
		if (!containsVertex(vertex1) || !containsVertex(vertex2)){
			throw new RuntimeException("Vertex does not exist");
		}
		Node<T> node1 = getNode(vertex1);
		Node<T> node2 = getNode(vertex2);
		//node2.setParent(node1);
		node2.addParent(node1);
		return node1.addEdge(node2, weight);
	}
	//remove vertex from graph
	public boolean removeVertex(T vertex){
		if (!adjacencyList.containsKey(vertex)){
			return false;
		}
		
		final Node<T> toRemove = getNode(vertex);
		adjacencyList.values().forEach(node -> node.removeEdge(toRemove));	
		adjacencyList.remove(vertex);
		return true;
	}
	
	public boolean removeEdge(T vertex1, T vertex2){
		if(!containsVertex(vertex1)||!containsVertex(vertex2)){
			return false;
		}
		return getNode(vertex1).removeEdge(getNode(vertex2));
	}
	
	public int vertexCount(){
		return adjacencyList.keySet().size();
	}
	
	public int edgeCount(){
		return adjacencyList.values()
				.stream()
				.mapToInt(Node::getEdgeCount)
				.sum();
	}
	
	public boolean containsVertex(T vertex){
		return adjacencyList.containsKey(vertex);
	}
	
	public boolean containsEdge(T vertex1, T vertex2){
		if(!containsVertex(vertex1) || !containsVertex(vertex2)){
			return false;
		}
		return getNode(vertex1).hasEdge(getNode(vertex2));
	}
	
	public List<T> shortestPath(T startVertex, T endVertex){
		if(!containsVertex(startVertex) || ! containsVertex(endVertex)){
			return null;
		}
		
		runBFS(startVertex);
		
		List<T> path = new ArrayList<>();
		
		Node<T> end = getNode(endVertex);
		while(end != null && end != getNode(startVertex)){
			path.add(end.vertex());
			end = end.parent();
		}
		if (end==null){
			return null;
		}else{
			Collections.reverse(path);
		}
		return path;
	}
	
	private void runBFS(T startVertex){
		if (!containsVertex(startVertex)){
			throw new RuntimeException("Vertex does not exist.");
		}
		
		resetGraph();
		
		Queue<Node<T>> queue = new LinkedList<>();
		Node<T> start = getNode(startVertex);
		queue.add(start);
		
		while (!queue.isEmpty()){
			Node<T> first = queue.remove();
			first.setVisited(true);
			first.edges().forEach(edge -> {
				Node<T> neighbor = edge.toNode();
				if(!neighbor.isVisited()){
					neighbor.setParent(first);
					queue.add(neighbor);
				}
			});
		}
	}
	
	public boolean hasIsland(T startVertex){
		if (!containsVertex(startVertex)){
			throw new RuntimeException("Vertex does not exist.");
		}
		
		resetGraph();
		
		Queue<Node<T>> queue = new LinkedList<>();
		Node<T> start = getNode(startVertex);
		queue.add(start);
		vertexList.remove(start.vertex());

		while (!queue.isEmpty()){
			Node<T> first = queue.remove();
			//System.out.println(first.vertex());
			first.setVisited(true);
			vertexList.remove(first.vertex());
			if (!first.noParents()){
				for(Node<T> parent: first.parents()){
					if (!parent.isVisited()){
						queue.add(parent);
					}
				}
			}
			for(Edge<T> edge : first.edges()){
				Node<T> neighbor = edge.toNode();
				if(!neighbor.isVisited()){
					//neighbor.setParent(first);
					queue.add(neighbor);
					vertexList.remove(neighbor.vertex());
				}
			}
		}
		System.out.println(vertexList);
		if(vertexList.isEmpty()){
			return false;
		}else{
			return true;
		}
	}
	
	public boolean hasLoop(T startVertex){
		if (!containsVertex(startVertex)){
			throw new RuntimeException("Vertex does not exist.");
		}
		
		resetGraph();
		
		Queue<Node<T>> queue = new LinkedList<>();
		Node<T> start = getNode(startVertex);
		queue.add(start);

		while (!queue.isEmpty()){
			Node<T> first = queue.remove();
			first.setVisited(true);
			for(Edge<T> edge : first.edges()){
				Node<T> neighbor = edge.toNode();
				if(!neighbor.isVisited()){
					neighbor.setParent(first);
					queue.add(neighbor);
				}else{
					return true;
				}
			}
		}
		return false;
	}
	
	private Node<T> getNode(T value){
		return adjacencyList.get(value);
	}
	
	private void resetGraph(){
		adjacencyList.keySet().forEach(key -> {
			Node<T> node = getNode(key);
			node.setParent(null);
			node.setVisited(false);
		});
	}
	
	public static void main(String [] args){
		Graph<String> graph = new Graph<String>();
		graph.addVertex("A");
		graph.addVertex("B");
		graph.addVertex("C");
		graph.addVertex("D");
		graph.addVertex("E");
		graph.addVertex("F");
		graph.addEdge("A", "B");
		graph.addEdge("B", "C");
		graph.addEdge("D", "C");
		graph.addEdge("D", "B");
		graph.addEdge("D", "E");
		graph.addEdge("C", "F");
		graph.addEdge("E", "D");
		graph.addEdge("D", "B");
		//System.out.println(graph.shortestPath("A", "E"));
		//System.out.println(graph.hasLoop("A"));
		System.out.println(graph.hasIsland("A"));
	}
	
}
