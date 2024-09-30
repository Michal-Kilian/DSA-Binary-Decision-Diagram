package BDD.BDD;

import java.io.BufferedReader;  
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class BDDTest {
	
	final static int numberOfVariables = 18;
	final static int numberOfFunctions = 100;
	
	static ArrayList<String> order = new ArrayList<String>(Arrays.asList("A","B","C","D"));
	static String function = "!A!C+ABD";
	static String values = "1000";
	
	public static void main(String[] args) throws IOException {
		BDD bdd = new BDD();
		
		bdd.createBDD(function, order);
		//bdd.printUniqueNodes(bdd.Nodes);
		//bdd.printReductionEffectiveness(bdd.Nodes, order);
		System.out.println(bdd.useBDD(values));
		System.out.println(evaluate(function, values));
		
		//testBDD();
	}
	
	//evaluate function to check the correctness of the functions createBDD and useBDD
	private static String evaluate(String function, String values) {
		HashMap<String, Integer> OrderValues = new HashMap<String, Integer>();
		
		for (int i = 0; i < order.size(); i++) {
			OrderValues.put(order.get(i), Character.getNumericValue(values.charAt(i)));
		}
		
		int result = 0;
		boolean nextIsNeg = false;
		
		String tempCurrFunc = function.replace("+", " ");
		String[] currentFunction = tempCurrFunc.split(" ");
		
		for (int j = 0; j < currentFunction.length; j++) {
			int tempResult = 1;
			for (int k = 0; k < currentFunction[j].length(); k++) {
				String currentChar = String.valueOf(currentFunction[j].charAt(k));
				int currentValue;
				if (currentChar.equals("!")) {
					nextIsNeg = true;
					continue;
				}
				
				if (nextIsNeg == false)
					currentValue = OrderValues.get(currentChar);
				else {
					currentValue = 1 - OrderValues.get(currentChar);
					nextIsNeg = false;
				}
				tempResult *= currentValue;
			}
			result += tempResult;
		}
		return ((result >= 1) ? "1" : "0");
	}
	
	private static String generateRandomFunc(ArrayList<String> order) {
		ArrayList<String> variables = order;
		String function;
		ArrayList<String> usedVariables = new ArrayList<String>();
		final int maxAmountOfTerms = 10;
		ArrayList<String> terms = new ArrayList<String>();
		Random random = new Random();
		
		for (int j = 0; j < random.nextInt(1, maxAmountOfTerms); j++) {
			String term = "";
			
			for (int i = 0; i < variables.size(); i++) {
				
				boolean use = random.nextBoolean();
				if (use == false)
					continue;
				
				boolean neg = random.nextBoolean();
				if (neg == false) {
					term += variables.get(i);
					usedVariables.add(variables.get(i));
				}
				else {
					term += "!" + variables.get(i);
					usedVariables.add(variables.get(i));
				}
			}
			terms.add(term);
		}
		
		for (int i = 0; i < numberOfVariables; i++) {
			if (usedVariables.contains(variables.get(i)) == false) {
				String term = ""; 
				term += variables.get(i);
				terms.add(term);
			}
		}
		
		function = String.join("+", terms);
		if (function.contains("++"))
			function.replace("++", "+");
		
		return function;
	}
	
	private static void fillUse() throws IOException {
		File file = new File("C:\\Users\\kilia\\eclipse-workspace\\BDD\\src\\BDD\\BDD\\values(18).txt");
		FileWriter myWriter = new FileWriter(file);
		for (int i = 0; i < Math.pow(2, 18); i++) {
		    myWriter.write(String.format("%18s\n", Integer.toBinaryString(i)).replace(" ", "0"));
		}
		myWriter.close();
	}
	
	private static void fillRandomFunctions() throws IOException {
		File file = new File("C:\\Users\\kilia\\eclipse-workspace\\BDD\\src\\BDD\\BDD\\randomFunctions(18).txt");
		FileWriter myWriter = new FileWriter(file);
		for (int i = 0; i < numberOfFunctions; i++) {
			myWriter.write(generateRandomFunc(order) + "\n");
		}
		myWriter.close();
	}
	
	private static void testBDD() throws IOException {
		File funcFile = new File("C:\\Users\\kilia\\eclipse-workspace\\BDD\\src\\BDD\\BDD\\randomFunctions(" + numberOfVariables + ").txt");
		Scanner funcScanner = new Scanner(funcFile);
		BufferedReader useScanner = new BufferedReader(new FileReader("C:\\Users\\kilia\\eclipse-workspace\\BDD\\src\\BDD\\BDD\\values(" + numberOfVariables + ").txt"));
		
		int errors = 0;
		double sumEffectiveness = 0;
		long sumTimeComplexity = 0;
		
		for (int i = 0; i < numberOfFunctions; i++) {
			BDD bdd = new BDD();
			String currentFunc = (funcScanner).next();
			useScanner.mark(1000000);
			
			for (int j = 0; j < Math.pow(2, 10); j++) {
				String currentUse = useScanner.readLine();
				
				long start = System.currentTimeMillis();
				bdd.createBDD(currentFunc, order);
				long finish = System.currentTimeMillis();
				sumTimeComplexity += finish - start;
				
				if (bdd.useBDD(currentUse).equals(evaluate(currentFunc, currentUse)) == false) {
					errors++;
				}
			}
			sumEffectiveness += bdd.printReductionEffectiveness(bdd.Nodes, order);
			System.out.println("Test (" + (i + 1) + ") successfull (" + currentFunc + ")");
			useScanner.reset();
			bdd.Nodes.clear();
		}
		funcScanner.close();
		useScanner.close();
		System.out.println("Errors during the test: " + errors);
		System.out.println("Average reduction effectiveness: " + (sumEffectiveness / numberOfFunctions) + " %");
		System.out.println("Average time complexity of creating the tree: " + (sumTimeComplexity / numberOfFunctions) + " ms/it");
	}
}