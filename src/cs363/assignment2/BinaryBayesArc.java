package cs363.assignment2;

import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.text.html.HTMLDocument.Iterator;

public class BinaryBayesArc {
	
	// FOR REFERENCE
	// false = MALE, HEAVY, TALL
	// true = FEMALE, SKINNY, SHORT
	
	// ALWAYS PASS THE TRUE VALUE TO THE BINARY PROBABILITY CONSTRUCTOR
	
	String parentName, childName;
	
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
	public BinaryBayesArc(	String parentName, String childName, 
							Data input, int column1, int column2, 
							BinaryProbability prParent, 
							BinaryProbability pChildTrueParent,
							BinaryProbability pChildFalseParent)
	{
		
		/***************** SETTING MEMBER VARIABLES ***************/
		
		this.parentName = parentName;
		this.childName = childName;
		
		parentData = input.column.get(column1);
		childData = input.column.get(column2);
		
		missingChildren = 	new Vector<Integer>();
		missingParents 	= 	new Vector<Integer>(); 
		trueChildren 	= 	new Vector<Integer>(); 
		falseChildren 	= 	new Vector<Integer>();
		trueParents 	= 	new Vector<Integer>(); 
		falseParents 	= 	new Vector<Integer>();
		
		pParent 			= prParent;
		pChildgTrueParent 	= pChildTrueParent;
		pChildgFalseParent 	= pChildFalseParent;
		
		pChild = new BinaryProbability(
				totalProbability(pChildgTrueParent, pChildgFalseParent, pParent));
		
		pParentgTrueChild = new BinaryProbability(
				bayes(pChildgTrueParent.pTrue, pParent.pTrue, pChild.pTrue));
		
		pParentgFalseChild = new BinaryProbability(
				bayes(pChildgTrueParent.pFalse, pParent.pTrue, pChild.pFalse)); 
				
		/***************** COUNTING AND TRACKING VARIABLES ************/
		
		totalRows = parentData.size();	
		getIndices();
		
		//System.out.println(totalRows);
		
		/********************** CALCULATIONS **********************/		
		
		// At this point all expectations are calculated.
		
		double threshold = 0.001, difference = 0.0, temp = 0.0;
		
		recalculate();
		input.printData();
			
		temp = pParent.pTrue;
		//System.out.println(temp);
		
		//for(int i=0; i<10; i++){
			
			maximizeParent();
			recalculate();
			maximizeChild();
			recalculate();
			update();
			printValues();
		//}			
			
		difference = Math.abs(temp - pParent.pTrue);
				
	}
	
	public void loop(){
		
		maximizeParent();
		recalculate();
		maximizeChild();
		recalculate();
		update();
		printValues();
	}
	
	private void recalculate(){
		
		pChild.set(totalProbability(pChildgTrueParent, pChildgFalseParent, pParent));	
		pParentgTrueChild.set(bayes(pChildgTrueParent.pTrue, pParent.pTrue, pChild.pTrue));
		pParentgFalseChild.set(bayes(pChildgTrueParent.pFalse, pParent.pTrue, pChild.pFalse)); 
	}
	
	private void getIndices(){
		
		for(int i=0; i<totalRows; i++){
			
			if(parentData.get(i) == -1) missingParents.add(i);
			if(childData.get(i) == -1) missingChildren.add(i);
			if(parentData.get(i) == 1) trueParents.add(i);
			if(childData.get(i) == 1) trueChildren.add(i);
			if(parentData.get(i) == 0) falseParents.add(i);
			if(childData.get(i) == 0) falseChildren.add(i);
		 }
	}
	
	private void update(){
		
//		System.out.println(totalRows);
//		System.out.println(pParent.pTrue);
//		System.out.println(missingParents.size());
		for(int i=0; i<missingParents.size(); i++){
			
			parentData.set(missingParents.get(i), pParent.pTrue);
		}

		for(int i=0; i<missingChildren.size(); i++){
			
			if(parentData.get(missingChildren.get(i)) == 1.0) 
				childData.set(missingChildren.get(i), pChildgTrueParent.pTrue);
			
			if(parentData.get(missingChildren.get(i)) == 0.0) 
				childData.set(missingChildren.get(i), pChildgFalseParent.pTrue);
		}
	}
	
	private void maximizeParent(){	
		
		double total = 0;							
		for(int i=0; i<trueParents.size(); i++) 	// COUNT THE OBSERVED TRUE
			total += 1.0;							// VALUES, THEN FOR EACH
													// MISSING VALUE ADD THE 
		for(int i=0; i<missingParents.size(); i++) 	// PROBABILITY THAT IT'S TRUE	
			total += pParent.pTrue;			
																										
		pParent.set(total/totalRows);
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
	public void maximizeChild()
	{
		double totalA = 0, totalB = 0, totalC = 0, totalD = 0;
							
			// COUNT WHERE PARENT = 1 AND CHILD = 1
			for(int i=0; i<trueParents.size(); i++) 
				if(childData.get(trueParents.get(i)) == 1) totalA += 1.0;
			
			// COUNT (WHERE PARENT = 1 AND CHILD = ?) * P(CHILD = 1 | PARENT = 1)
			for(int i=0; i<missingChildren.size(); i++)
				if(parentData.get(missingChildren.get(i)) == 1.0)
					totalB += pChildgTrueParent.pTrue;
						
			// COUNT (WHERE PARENT = ? AND CHILD = 1) * P(PARENT = 1 | CHILD = 1);
			for(int i=0; i<missingParents.size(); i++)
				if(childData.get(missingParents.get(i)) == 1.0)
					totalC += pParentgTrueChild.pTrue;
			
			// COUNT WHERE PARENT = 1 + 
			// COUNT WHERE PARENT = ? * P(PARENT = 1)
			for(int i=0; i<trueParents.size(); i++) totalD += 1.0;
			for(int i=0; i<missingParents.size(); i++) totalD += pParent.pTrue;			

			pChildgTrueParent.set((totalA + totalB + totalC) / totalD);
			
			// COUNT WHERE PARENT = 0 AND CHILD = 1
			for(int i=0; i<falseParents.size(); i++) 
				if(childData.get(falseParents.get(i)) == 1) totalA += 1.0;
			
			// COUNT (WHERE PARENT = 0 AND CHILD = ?) * P(CHILD = 1 | PARENT = 0)
			for(int i=0; i<missingChildren.size(); i++)
				if(parentData.get(missingChildren.get(i)) == 0.0)
					totalB += pChildgFalseParent.pTrue;
			
			// COUNT (WHERE PARENT = ? AND CHILD = 1) * P(PARENT = 0 | CHILD = 1);
			for(int i=0; i<missingParents.size(); i++)
				if(childData.get(missingParents.get(i)) == 1.0)
					totalC += pParentgTrueChild.pFalse;
			
			// COUNT WHERE PARENT = 0 + 
			// COUNT WHERE PARENT = ? * P(PARENT = 0)
			for(int i=0; i<falseParents.size(); i++) totalD += 1.0;
			for(int i=0; i<missingParents.size(); i++) totalD += pParent.pFalse;				

			pChildgFalseParent.set((totalA + totalB + totalC) / totalD);	
	}
	
	public double totalProbability(	BinaryProbability pChildgTrueParent, 
									BinaryProbability pChildgFalseParent, 
									BinaryProbability pParent){
		
		return (pChildgTrueParent.pTrue * pParent.pTrue + pChildgFalseParent.pTrue * pParent.pFalse);
	}
	
	public double bayes(double pChildgParent, double pParent, double pChild){
		
		return (pChildgParent * pParent) / pChild;
	}
	
	public void printValues(){
		
//		System.out.println("VARIABLE\tP(TRUE)\tP(FALSE)");
		System.out.println("P(" + parentName + "):\t" + pParent.pTrue);
//
//		System.out.println("P(" + childName + "): " + pChild.pTrue);
//
//		System.out.println("P(WEIGHT = LOW | FEMALE): " + pChildgTrueParent.pTrue);
//
//		System.out.println("P(WEIGHT = LOW | MALE): " + pChildgFalseParent.pTrue);
//
//		System.out.println("P(FEMALE | WEIGHT = LOW): " + pParentgTrueChild.pTrue);
//
//		System.out.println("P(FEMALE | WEIGHT = HIGH): " + pParentgFalseChild.pTrue);
	}
}
