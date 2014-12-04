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
	 * Each statistic represents probabilities over one directed arc of a 
	 * Bayesian network
	 * @param input : the input data
	 * @param column1 : index of the column for the parent
	 * @param column2 : index of the column for the child
	 * @param probabilities : an array of expectations
	 */
	public Statistics(Data input, int column1, int column2, double[] probabilities){
		
		/***************** SETTING MEMBER VARIABLES ***************/
		
		parentData = input.column.get(column1);
		childData = input.column.get(column2);
		
		pParent = new BinaryProbability(probabilities[0]);
		pChildgTrueParent = new BinaryProbability(probabilities[1]);
		pChildgFalseParent = new BinaryProbability(probabilities[2]);
				
		/***************** COUNTING AND TRACKING VARIABLES ************/
		
		totalRows = parentData.size();
		
		missingChildren = find(-1.0, childData);	// INDICES OF TYPES OF DATA
		missingParents = find(-1.0, parentData);	// TO ENABLE MORE EFFICIENT
		trueChildren = find(1.0, childData);		// COUNTING AND UPDATING
		falseChildren = find(0.0, childData);
		trueParents = find(1.0, parentData);
		falseParents = find(0.0, parentData);	
		
		/********************** CALCULATIONS **********************/		
		
		// At this point all expectations are calculated.
		
		pParent.set(maximizeParent(parentData, missingParents, trueParents));		
		updateExpectations(parentData, missingParents, pParent.pTrue);		
	}
	
	public void recalculate(){
		
		pChild = new BinaryProbability(
				totalProbability(pChildgTrueParent, pChildgFalseParent, pParent));
		
		pParentgTrueChild = new BinaryProbability(
				bayes(pChildgTrueParent.pTrue, pParent.pTrue, pChild.pTrue));
		pParentgFalseChild = new BinaryProbability(
				bayes(pChildgTrueParent.pFalse, pParent.pTrue, pChild.pFalse)); 
	}
	
	public Vector<Integer> find(double target, Vector<Double> input){
		
		Vector<Integer> output = new Vector<Integer>();
		
		for(int i=0; i< input.size(); i++) if(input.get(i) == target) output.add(i);
		
		return output;
	}
	
	public void updateExpectations(Vector<Double> column, Vector<Integer> targets, double value){
		
		for(int i=0; i<targets.size(); i++) column.set(targets.get(i), value);		
	}
	
	public double maximizeParent(	Vector<Double> parent, 
									Vector<Integer> missings, 
									Vector<Integer> trues)
	{
													// COUNT THE OBSERVED TRUE
		double total = 0;							// VALUES, THEN FOR EACH	
		for(int i=0; i<missings.size(); i++) 		// MISSING VALUE ADD THE 
			total += parent.get(missings.get(i));	// PROBABILITY THAT IT'S TRUE
			
		for(int i=0; i<trues.size(); i++) 			
			total += parent.get(missings.get(i)) * pParent.pTrue;			
																										
		return total/parent.size();
	}
	
	/**
	 * Pass vectors of data, where the missing and true children are, then decide
	 * if you're trying to calculate p(child = true) given either true or false 
	 * parents by setting tfParents equal either to trueParents or falseParents.
	 * @param parentData : parent column
	 * @param childData	: child column
	 * @param missingsChildren : indices of missing Children
	 * @param trueChildren : indices of true Children
	 * @param missingParents : indices of missing Parents
	 * @param tfParents : indices either of true or false parents, depending on
	 * 					  the calculation
	 * @param parentTrue : what choice you made for tfParents
	 * @return probability of a true child given either a true or false parent
	 */
	public double maximizeChild(	Vector<Double> parentData, 
									Vector<Double> childData, 
									Vector<Integer> missingsChildren, 
									Vector<Integer> trueChildren,
									Vector<Integer> missingParents,
									Vector<Integer> tfParents,
									boolean parentTrue)
	{
		double tempAnswer = 0, totalA = 0, totalB = 0, totalC = 0, totalD = 0;

		if(parentTrue) 	tempAnswer = pChildgTrueParent.pTrue;
		if(!parentTrue) tempAnswer = pChildgFalseParent.pTrue;
					
		if(parentTrue){
			
			// COUNT WHERE PARENT = 1 AND CHILD = 1
			for(int i=0; i<tfParents.size(); i++) 
				if(childData.get(tfParents.get(i)) == 1) totalA += 1.0;
			
			// COUNT (WHERE PARENT = 1 AND CHILD = ?) * P(CHILD = 1 | PARENT = 1)
			for(int i=0; i<missingChildren.size(); i++)
				if(parentData.get(missingChildren.get(i)) == 1.0)
					totalB += pChildgTrueParent.pTrue;
						
			// COUNT (WHERE PARENT = ? AND CHILD = 1) * P(PARENT = 1 | CHILD = 1);
			for(int i=0; i<missingParents.size(); i++)
				if(childData.get(missingParents.get(i)) == 1.0)
					totalC += bayes(tempAnswer, 
									pParent.pTrue, 
									totalProbability(pChildgTrueParent,
													 pChildgFalseParent,
													 pParent));			
			// COUNT WHERE PARENT = 1 + 
			// COUNT WHERE PARENT = ? * P(PARENT = 1)
			for(int i=0; i<tfParents.size(); i++)
				if(parentData.get(tfParents.get(i)) == 1) totalD += 1.0;
			for(int i=0; i<missingParents.size(); i++)
				totalD += pParent.pTrue;			
		}
		
		if(!parentTrue){
			
			// COUNT WHERE PARENT = 0 AND CHILD = 1
			for(int i=0; i<tfParents.size(); i++) 
				if(childData.get(tfParents.get(i)) == 1) totalA += 1.0;
			
			// COUNT (WHERE PARENT = 0 AND CHILD = ?) * P(CHILD = 1 | PARENT = 0)
			for(int i=0; i<missingChildren.size(); i++)
				if(parentData.get(missingChildren.get(i)) == 1.0)
					totalB += pChildgFalseParent.pTrue;
			
			// COUNT (WHERE PARENT = ? AND CHILD = 1) * P(PARENT = 0 | CHILD = 1);
			for(int i=0; i<missingParents.size(); i++)
				if(childData.get(missingParents.get(i)) == 1.0)
					totalC += bayes(tempAnswer, 
									pParent.pTrue, 
									totalProbability(pChildgTrueParent,
													 pChildgFalseParent,
													 pParent));		
			// COUNT WHERE PARENT = 1 + 
			// COUNT WHERE PARENT = ? * P(PARENT = 0)
			for(int i=0; i<tfParents.size(); i++)
				if(parentData.get(tfParents.get(i)) == 0) totalD += 1.0;
			for(int i=0; i<missingParents.size(); i++)
				totalD += pParent.pFalse;				
		}
		
		return (totalA + totalB + totalC) / totalD;	
	}
	
	public double totalProbability(	BinaryProbability pChildgTrueParent, 
									BinaryProbability pChildgFalseParent, 
									BinaryProbability pParent){
		
		return (pChildgTrueParent.pTrue * pParent.pTrue + pChildgFalseParent.pTrue * pParent.pFalse);
	}
	
	public double bayes(double pChildgParent, double pParent, double pChild){
		
		return (pChildgParent * pParent) / pChild;
	}
}
