package cs363.assignment2;

import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Statistics {
	
	// FOR REFERENCE
	// 0 = MALE, HEAVY, TALL
	// 1 = FEMALE, SKINNY, SHORT
	
	double likelihood;
	ConcurrentLinkedQueue<Integer> missings;
	
	public Statistics(){
		
		missings = new ConcurrentLinkedQueue<Integer>();
	}
	
	public ConcurrentLinkedQueue<Integer> find(Double target, Vector<Double> input){
		
		missings = new ConcurrentLinkedQueue<Integer>();
		
		for(int i=0; i<input.size(); i++){
			
			if(input.get(i).equals(target)) missings.add(i);
		}
		
		return missings;
	}
	
	/**
	 * Calculate the expected value for true in the given column.
	 * @param prior
	 * @param input
	 * @return an expectation
	 */
	public double expectation(double prior, Vector<Double> input){
		
		double total = 0;
		
		for(int i=0; i<input.size(); i++){
			
			if(input.get(i) == -1) total += prior;
			else total += input.get(i);
		}
		
		return total/input.size();
	}	
	
	public void maximization(double expectation, Vector<Double> input){
		
		for(int i=0; i<input.size(); i++){
			
			if(input.get(i) != 0 || input.get(i) != 1)
		}
	}
}
