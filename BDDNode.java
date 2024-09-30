package BDD.BDD;

public class BDDNode {
	String function;
	String currentOrder;
	BDDNode left;
	BDDNode right;
	
	public BDDNode(String function) {
		this.function = function;
		this.left = null;
		this.right = null;
	}
}
