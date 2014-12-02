package cs363.assignment2;

import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.text.html.HTMLDocument.Iterator;

public class Statistics {
	
	// FOR REFERENCE
	// false = MALE, HEAVY, TALL
	// true = FEMALE, SKINNY, SHORT
	
	// ALWAYS PASS THE TRUE VALUE TO THE BINARY PROBABILITY CONSTRUCTOR
	
	Vector<Double> parentData, childData;
	
	int totalRows;
	BinaryProbability 	pParent, pChild, 
						pChildgTrueParent, pChildgFalseParent, 
						pParentgTrueChild, pParentgFalseChild;
	
	// TO TRACK THE POSITIONS OF THE MISSING VALUES;
	Vector<Integer> missingChildren, 
					missingParents, 
					trueChildren, 
					falseChildren,
					trueParents, 
					falseParents;
	/**
	 * Each Statistics object represents the information over one arc of the graph.
	 */
	public Statistics(Data input, int column1, int column2, double[] probabilities){
		
		/***************** SETTING MEMBER VARIABLES ***************/
		
		parentData = input.column.get(column1);
		childData = input.column.get(column2);
		
		pParent = new BinaryProbability(probabilities[0]);
		pChildgTrueParent = new BinaryProbability(probabilities[1]);
		pChildgFalseParent = new BinaryProbability(probabilities[2]);
				
		/********************** CALCULATIONS **********************/
		
		totalRows = parentData.size();
		
		missingChildren = find(-1.0, childData);
		missingParents = find(-1.0, parentData);
		trueChildren = find(1.0, childData);
		falseChildren = find(0.0, childData);
		trueParents = find(1.0, parentData);
		falseParents = find(0.0, parentData);	
		
		pChild = new BinaryProbability(totalProbability(pChildgTrueParent, pChildgFalseParent, pParent));
		pParentgTrueChild = new BinaryProbability(bayes(pChildgTrueParent.pTrue, pParent.pTrue, pChild.pTrue));
		pParentgFalseChild = new BinaryProbability(bayes(pChildgTrueParent.pFalse, pParent.pTrue, pChild.pFalse)); 
	
		// At this point all expectations are calculated.
	}
	
	public Vector<Integer> find(double target, Vector<Double> input){
		
		Vector<Integer> output = new Vector<Integer>();
		
		for(int i=0; i< input.size(); i++)
			if(input.get(i) == target) output.add(i);
		
		return output;
	}
	
	public void updateExpectations(Vector<Double> column, Vector<Integer> targets, double value){
		
		for(int i=0; i<targets.size(); i++){
			
			column.set(targets.get(i), value);
		}
	}
	
	public void maximize(){
		
		pParent = count()
	}
	
	public double count(Vector<Double> parent, Vector<Integer> locations, double p){
		
		double total = 0;
		
		int j=0;
		for(int i=0; i<parent.size(); i++){
			
			total += 
		}
	}
	
	public double count(Vector<Double> parent, Vector<Double> child, Vector<Integer> locations, double p){
		
		
	}
	
	public double totalProbability(	BinaryProbability pChildgTrueParent, 
									BinaryProbability pChildgFalseParent, 
									BinaryProbability pParent){
		
		return (pChildgTrueParent.pTrue * pParent.pTrue + pChildgFalseParent.pTrue * pParent.pFalse);
	}
	
	public double bayes(double pChildgParent, double pParent, double pChild){
		
		return (pChildgParent * pParent) / pChild;
	}
	
	/**
	 * Calculate the expected value for true in the given column.
	 * @param prior
	 * @param input
	 * @return an expectation
	 */
	public double expectation(double prior, Vector<Double> input){
		

	}	
	
	public void maximization(double expectation, Vector<Double> input){
		
	
	}
}
