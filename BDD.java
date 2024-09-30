package BDD.BDD;

import java.util.ArrayList;
import java.util.HashMap;
import java.lang.Math;

public class BDD {
	
	BDDNode root;
	String function;
	ArrayList<String> order;
	HashMap<String, BDDNode> Nodes = new HashMap<String, BDDNode>();
	
	//createBDD function which assigns the root, function and order
	public void createBDD(String function, ArrayList<String> order) {
		this.root = newNode(function);
		this.function = function;
		this.order = order;
		this.createBDDTree(this.function, this.root,  this.order);
	}
	
	//createBDDTree function which creates the tree recursively, first left children, then right children
	public void createBDDTree(String function, BDDNode currentNode, ArrayList<String> order) {
		
		if (order.isEmpty())
			return;
		
		currentNode.currentOrder = order.get(0);
		
		String leftChildFunc = createLeftFunc(function, order);
		String rightChildFunc = createRightFunc(function, order);
		
		ArrayList<String> newOrder = new ArrayList<String>(order.subList(1, order.size()));
		
		//in case the function of current node is 0, we do not want to progress any further
		if (currentNode.function != "0" && currentNode.function != "1") {
			
			//left child creation
			BDDNode leftChild = newNode(leftChildFunc);
			currentNode.left = leftChild;
			createBDDTree(currentNode.left.function, currentNode.left, newOrder);
		
			//right child creation
			BDDNode rightChild = newNode(rightChildFunc);
			currentNode.right = rightChild;
			createBDDTree(currentNode.right.function, currentNode.right, newOrder);
			
			//in case left child and right share the same function, reduce them in function reduceBDD
			if (leftChild.equals(rightChild) && currentNode != root)
				reduceBDD(leftChild, currentNode);
		}
	}
	
	//createLeftFunc function which returns a function for the left child node that has to be created
	public String createLeftFunc(String function, ArrayList<String> order) {
		ArrayList<String> leftChildFunc = new ArrayList<String>();
		
		//special cases
		if (function.equals(order.get(0))) {
			return "0";	
		}
		if (function.equals("!" + order.get(0))) {
			return "1";	
		}
		if (function.equals("!" + order.get(0) + "+" + order.get(0)) || function.equals(order.get(0) + "+!" + order.get(0))) {
			return "1";	
		}
		if (function.contains("+") == false && function.contains(order.get(0)) && function.contains("!" + order.get(0)) == false) {
			return "0";
		}
		
		//split the function into parts by the + symbols
		String tempCurrFunc = function.replace("+", " ");
		String[] currentFunction = tempCurrFunc.split(" ");
		
		for (int j = 0; j < currentFunction.length; j++) {
			
			//decomposing the function using the rules of Shannon decomposition for the left child node
			if (currentFunction[j].equals("!" + order.get(0))) {
				return "1";	
			}
			if (currentFunction[j].contains("!" + order.get(0))) {
				if (currentFunction[j].replace("!" + order.get(0), "").equals("") == false)
					leftChildFunc.add(currentFunction[j].replace("!" + order.get(0), ""));
			}
			else if (currentFunction[j].contains(order.get(0)) == false && currentFunction[j].contains("!" + order.get(0)) == false)
				leftChildFunc.add(currentFunction[j]);
		}
		
		//joining the function by + symbols to create the final function of the left child node
		String finalLeftChildFunc = String.join("+", leftChildFunc);
		if (finalLeftChildFunc.isEmpty())
			finalLeftChildFunc = "0";
		return finalLeftChildFunc;
	}
	
	//createRightFunc function which returns a function for the right child node that has to be created
	public String createRightFunc(String function, ArrayList<String> order) {
		ArrayList<String> rightChildFunc = new ArrayList<String>();
		
		//special cases
		if (function.equals(order.get(0))) {
			return "1";	
		}
		if (function.equals("!" + order.get(0))) {
			return "0";
		}
		if (function.equals("!" + order.get(0) + "+" + order.get(0)) || function.equals(order.get(0) + "+!" + order.get(0))) {
			return "1";	
		}
		if (function.contains("+") == false && function.contains("!" + order.get(0)) && function.contains(order.get(0)) == false) {
			return "1";
		}
		
		//split the function into parts by the + symbols
		String tempCurrFunc = function.replace("+", " ");
		String[] currentFunction = tempCurrFunc.split(" ");
		
		for (int j = 0; j < currentFunction.length; j++) {
			
			//decomposing the function using the rules of Shannon decomposition for the left child node
			if (currentFunction[j].equals(order.get(0))) {
				return "1";
			}
				
			int index = currentFunction[j].indexOf(order.get(0));
			
			if (index == 0) {
				if (currentFunction[j].replace(order.get(0), "").equals("") == false) {
					rightChildFunc.add(currentFunction[j].replace(order.get(0), ""));
					continue;	
				}
			}
			else if (currentFunction[j].contains(order.get(0)) && currentFunction[j].charAt(index - 1) != ('!')) {
				if (currentFunction[j].replace(order.get(0), "").equals("") == false)
					rightChildFunc.add(currentFunction[j].replace(order.get(0), ""));
			}
			else if (currentFunction[j].contains(order.get(0)) == false && currentFunction[j].contains("!" + order.get(0)) == false)
				rightChildFunc.add(currentFunction[j]);
		}
		
		//joining the function by + symbols to create the final function of the left child node
		String finalRightChildFunc = String.join("+", rightChildFunc);
		if (finalRightChildFunc.isEmpty())
			finalRightChildFunc = "0";
		return finalRightChildFunc;
	}
	
	//newNode function that is called whenever a new node has to be created and checks if it already exists in the HashMap
	//of unique nodes, if yes, get the node from the HashMap, if not, create a new node (first type of reduction)
	public BDDNode newNode(String function) {
		if (Nodes.containsKey(function))
			return Nodes.get(function);
		else {
			BDDNode newNode = new BDDNode(function);
			Nodes.put(newNode.function, newNode);
			return newNode;
		}
	}
	
	//reduceBDD function that is called whenever the right child and the left child of the respective node share the same 
	//function
	//instead of creating two identical nodes, rewrite the parent node of these children by one of them (the parent node of
	//these children is a redundant node -> second type of reduction)
	public void reduceBDD(BDDNode child, BDDNode currentNode) {
		Nodes.remove(currentNode.function);
		
		currentNode.function = child.function;
		currentNode.currentOrder = child.currentOrder;
		currentNode.left = child.left;
		currentNode.right = child.right;
		
		int index = order.lastIndexOf(currentNode.currentOrder);
		if (index < 0)
			index = 0;
		ArrayList<String> newOrder = new ArrayList<String>(order.subList(index, order.size()));
		
		createBDDTree(currentNode.function, currentNode, newOrder);
	}
	
	//useBDD function to get a final value of the tree based on the values in the argument
	public String useBDD(String values) {
		BDDNode currentNode = this.root;
		while (currentNode.function.equals("0") == false && currentNode.function.equals("1") == false) {
			int index = order.indexOf(currentNode.currentOrder);
			if (values.charAt(index) == '0')
				currentNode = currentNode.left;
			else
				currentNode = currentNode.right;
		}
		return currentNode.function;
	}
	
	//helper function to print a node
	public void printNode(BDDNode node) {
		System.out.println("Node values: ");
		System.out.println(node.function);
		System.out.println(node.currentOrder);
		System.out.println(node.left);
		System.out.println(node.right);
	}
	
	//function to print all unique nodes created that the tree contains
	public void printUniqueNodes(HashMap<String, BDDNode> Nodes) {
		System.out.println("Unique Nodes:");
		Nodes.entrySet().forEach(entry -> {
		    System.out.println(entry.getKey());
		});
	}
	
	//function to print the amount of nodes if the tree was not reduced, the amount of nodes after reduction and
	//the reduction effectiveness percentage
	public double printReductionEffectiveness(HashMap<String, BDDNode> Nodes, ArrayList<String> order) {
		//System.out.println("Nodes before reduction: " + ((-1) +(int) Math.pow(2, order.size() + 1)));
		//System.out.println("Nodes after reduction: " + Nodes.size());
		//System.out.println("Reduction effectiveness: " + ((1 - (float) Nodes.size()/((-1) +(int) Math.pow(2, order.size() + 1))) * 100) + " %");
		return ((1 - (float) Nodes.size()/((-1) +(int) Math.pow(2, order.size() + 1))) * 100);
	}
}
