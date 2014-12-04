package cs363.assignment2;

public class BinaryProbability {

	double pTrue, pFalse;
	//TODO choose whether to use doubles or bigDecimals
	
	/**
	 * Constructs an instance of a binary literal based on the probability of TRUE;
	 * @param p
	 */
	public BinaryProbability(double p){
		
		pTrue = p;
		pFalse = 1-p;
	}
	
	public void set(double p){
		
		pTrue = p;
		pFalse = 1-p;
	}
}
